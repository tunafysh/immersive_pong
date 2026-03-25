// server.js
import { Hono } from 'hono'
import { upgradeWebSocket } from 'hono/cloudflare-workers'

// --- IN-MEMORY STORES ---
const parties = new Map()       // partyId -> { id, name, createdAt, hostId, members: [{playerId, joinedAt}] }
const partyClients = new Map()  // partyId -> Set of {ws, playerId}
const partyBalls = new Map()    // partyId -> { ballX, ballY, velX, velY }

// --- APP ---
const app = new Hono()

// --- LIST PARTIES ---
app.get('/parties', (c) => {
  return c.json(Array.from(parties.values()))
})

// --- CREATE PARTY ---
app.post('/parties', async (c) => {
  const body = await c.req.json()
  const partyId = crypto.randomUUID()
  const hostId = crypto.randomUUID()
  
  const party = { 
    id: partyId, 
    name: body.name, 
    createdAt: Date.now(), 
    hostId,
    members: [{ playerId: hostId, joinedAt: Date.now() }]
  }
  parties.set(partyId, party)

  return c.json(party, 201)
})

// --- JOIN PARTY ---
app.post('/parties/:id/join', async (c) => {
  const partyId = c.req.param('id')
  const party = parties.get(partyId)
  if (!party) return c.text('Party not found', 404)

  const playerId = crypto.randomUUID()
  const playerExists = party.members.find(m => m.playerId === playerId)
  
  if (!playerExists) {
    party.members.push({ playerId, joinedAt: Date.now() })
  }
  
  return c.json({ ...party, yourPlayerId: playerId }, 200)
})

// --- WEBSOCKET ---
app.get('/ws/:partyId', upgradeWebSocket((c) => {
  const partyId = c.req.param('partyId')
  const playerId = c.req.query('playerId')
  
  // Validate party exists
  const party = parties.get(partyId)
  if (!party) {
    return { 
      onOpen(evt, ws) {
        ws.send(JSON.stringify({ 
          type: 'error', 
          data: { message: 'Party not found' } 
        }))
        ws.close()
      },
      onMessage() {}, 
      onClose() {}, 
      onError() {} 
    }
  }

  // Validate playerId provided
  if (!playerId) {
    return {
      onOpen(evt, ws) {
        ws.send(JSON.stringify({ 
          type: 'error', 
          data: { message: 'playerId query parameter required' } 
        }))
        ws.close()
      },
      onMessage() {}, 
      onClose() {}, 
      onError() {} 
    }
  }

  // Validate player is member of party
  const isMember = party.members.some(m => m.playerId === playerId)
  if (!isMember) {
    return {
      onOpen(evt, ws) {
        ws.send(JSON.stringify({ 
          type: 'error', 
          data: { message: 'Player not member of this party' } 
        }))
        ws.close()
      },
      onMessage() {}, 
      onClose() {}, 
      onError() {} 
    }
  }

  if (!partyClients.has(partyId)) partyClients.set(partyId, new Set())
  const clients = partyClients.get(partyId)

  return {
    onOpen(evt, ws) {
      // Store connection with playerId
      const clientInfo = { ws, playerId }
      clients.add(clientInfo)
      
      // Send initial party state
      ws.send(JSON.stringify({
        type: 'party_state',
        data: {
          partyId,
          hostId: party.hostId,
          members: party.members.map(m => m.playerId),
          yourPlayerId: playerId
        }
      }))

      // Notify others that player joined
      const joinMessage = JSON.stringify({
        type: 'player_joined',
        data: {
          playerId,
          playerCount: clients.size
        }
      })
      
      for (const client of clients) {
        if (client.playerId !== playerId) {
          client.ws.send(joinMessage)
        }
      }
    },
    
    onMessage(evt, ws) {
      try {
        const message = JSON.parse(evt.data)
        
        // Handle ball updates
        if (message.type === 'ball_update') {
          // Validate ball data structure
          if (!message.data || !message.data.pos || !message.data.vel) {
            ws.send(JSON.stringify({
              type: 'error',
              data: { message: 'Invalid ball_update format' }
            }))
            return
          }

          // Broadcast to all other clients
          const syncMessage = JSON.stringify({
            type: 'ball_sync',
            data: {
              ...message.data,
              fromPlayerId: playerId
            }
          })

          for (const client of clients) {
            if (client.playerId !== playerId) {
              client.ws.send(syncMessage)
            }
          }
        }
      } catch (err) {
        ws.send(JSON.stringify({
          type: 'error',
          data: { message: 'Invalid message format' }
        }))
      }
    },
    
    onClose(evt, ws) {
      // Find and remove this client
      let disconnectedClient = null
      for (const client of clients) {
        if (client.ws === ws) {
          disconnectedClient = client
          clients.delete(client)
          break
        }
      }

      if (!disconnectedClient) return

      // Check if host disconnected
      const isHost = disconnectedClient.playerId === party.hostId
      
      if (isHost) {
        // Host left - close party
        const closeMessage = JSON.stringify({
          type: 'party_closed',
          data: { reason: 'host_disconnected' }
        })
        
        for (const client of clients) {
          client.ws.send(closeMessage)
          client.ws.close()
        }
        
        // Clean up party
        parties.delete(partyId)
        partyBalls.delete(partyId)
        partyClients.delete(partyId)
      } else {
        // Regular player left
        const leftMessage = JSON.stringify({
          type: 'player_left',
          data: {
            playerId: disconnectedClient.playerId,
            playerCount: clients.size
          }
        })
        
        for (const client of clients) {
          client.ws.send(leftMessage)
        }

        // Remove from party members
        party.members = party.members.filter(m => m.playerId !== disconnectedClient.playerId)

        // If no clients left, clean up party
        if (clients.size === 0) {
          parties.delete(partyId)
          partyBalls.delete(partyId)
          partyClients.delete(partyId)
        }
      }
    },
    
    onError(evt) {
      console.error('WebSocket error', evt)
    }
  }
}))

export default app