# Immersive Pong - WebSocket Party Server

A real-time multiplayer relay server for Immersive Pong built with [Hono](https://hono.dev/) for Cloudflare Workers.

## Overview

This server enables multiplayer gameplay by relaying ball position and velocity updates between connected players. The server acts as a pure relay - each client runs its own physics simulation, and the server simply broadcasts updates to all other players in the party.

### Key Features

- **Party-based Multiplayer**: Create or join parties to play with others
- **Host-based Management**: Party creator becomes the host; party ends when host disconnects
- **Real-time Ball Sync**: WebSocket-based relay for ball position/velocity updates
- **Player Identification**: Automatic player ID assignment when joining parties
- **Connection Validation**: Ensures only party members can connect
- **Lifecycle Management**: Handles player join/leave and party closure events

## Architecture

```
Client A                    Server (Relay)                Client B
   |                             |                            |
   |--- Create Party ----------->|                            |
   |<-- Party Info (hostId) -----|                            |
   |                             |<--- Join Party ------------|
   |                             |---- Party Info (playerId)->|
   |                             |                            |
   |--- WebSocket Connect ------>|                            |
   |<-- party_state -------------|                            |
   |                             |<--- WebSocket Connect -----|
   |<-- player_joined ------------|--- party_state ---------->|
   |                             |                            |
   |--- ball_update ------------->|                            |
   |                             |---- ball_sync ------------>|
   |                             |<--- ball_update -----------|
   |<-- ball_sync ----------------|                            |
```

## Quick Start

### Prerequisites

- Node.js (v18+)
- pnpm (or npm)
- Wrangler CLI (for Cloudflare Workers)

### Installation

```bash
# Install dependencies
pnpm install

# Install Wrangler globally (if not already installed)
pnpm add -g wrangler
```

### Development

Start the development server:

```bash
wrangler dev
```

The server will be available at `http://localhost:8787`

### Deployment

Deploy to Cloudflare Workers:

```bash
# Production deployment
wrangler deploy

# Or deploy to a specific environment
wrangler deploy --env production
```

## API Documentation

### REST Endpoints

#### GET `/parties`
List all active parties.

**Response:** Array of party objects

#### POST `/parties`
Create a new party.

**Request Body:**
```json
{
  "name": "My Party"
}
```

**Response:** Party object with `hostId` (save this for WebSocket connection!)

#### POST `/parties/:id/join`
Join an existing party.

**Response:** Party object with `yourPlayerId` (save this for WebSocket connection!)

### WebSocket Endpoint

#### GET `/ws/:partyId?playerId={playerId}`
Connect to a party's WebSocket for real-time updates.

**Query Parameters:**
- `playerId`: Your unique player ID (obtained from create/join party)

**Messages:** See [CLIENT_INTEGRATION.md](./CLIENT_INTEGRATION.md) for full protocol documentation.

## Project Structure

```
Server/
├── server.js                 # Main server implementation
├── package.json              # Dependencies and scripts
├── wrangler.toml             # Cloudflare Workers configuration
├── CLIENT_INTEGRATION.md     # Client integration guide
├── TESTING.md                # Testing instructions
└── README.md                 # This file
```

## Message Protocol

### Client → Server

- `ball_update`: Send ball position and velocity

### Server → Client

- `party_state`: Initial state on connection
- `ball_sync`: Ball update from another player
- `player_joined`: New player connected
- `player_left`: Player disconnected (non-host)
- `party_closed`: Host disconnected (party ended)
- `error`: Error message

See [CLIENT_INTEGRATION.md](./CLIENT_INTEGRATION.md) for detailed message formats and examples.

## Implementation Details

### Data Storage

The server uses in-memory Maps for state management:
- `parties`: Party metadata (id, name, host, members)
- `partyClients`: WebSocket connections per party
- `partyBalls`: Ball state per party (currently unused, reserved for future use)

**Note:** All data is lost on server restart. For production, consider using Cloudflare Durable Objects or an external database.

### Host Management

- The first player to create a party becomes the host
- Host ID is included in party data and WebSocket messages
- When host disconnects, the party is automatically closed
- All connected clients are notified with `party_closed` message

### WebSocket Validation

Before accepting a WebSocket connection, the server validates:
1. Party exists
2. `playerId` query parameter is provided
3. Player is a member of the party

Invalid connections receive an error message and are immediately closed.

### Broadcasting

When a client sends a `ball_update`:
1. Server validates the message format
2. Server adds `fromPlayerId` to the data
3. Server broadcasts `ball_sync` to **all other clients** (excluding sender)

This prevents echo and ensures clients only receive updates from other players.

## Testing

See [TESTING.md](./TESTING.md) for comprehensive testing instructions, including:
- Manual testing with cURL
- WebSocket testing with websocat
- Browser-based testing with HTML/JS client
- Complete validation checklist

## Client Integration

See [CLIENT_INTEGRATION.md](./CLIENT_INTEGRATION.md) for:
- Complete API documentation
- WebSocket protocol details
- Kotlin/Compose example implementation
- Best practices and troubleshooting

## Configuration

Edit `wrangler.toml` to configure:
- Worker name
- Environment settings
- Compatibility date
- Custom variables

## Limitations & Future Enhancements

### Current Limitations
- In-memory storage (state lost on restart)
- No authentication/authorization
- No rate limiting
- No reconnection support

### Future Enhancements
- [ ] Persistent storage (Durable Objects or DB)
- [ ] Game state synchronization (score, game events)
- [ ] Host migration (new host when original leaves)
- [ ] Spectator mode
- [ ] Connection health monitoring (ping/pong)
- [ ] Rate limiting per client
- [ ] Authentication tokens
- [ ] Party expiration/cleanup
- [ ] Maximum party size limits

## Tech Stack

- **Framework:** [Hono](https://hono.dev/) - Ultra-fast web framework
- **Runtime:** Cloudflare Workers
- **WebSocket:** Native WebSocket support via `hono/cloudflare-workers`
- **Protocol:** JSON over WebSocket

## Contributing

When making changes:
1. Test locally with `wrangler dev`
2. Follow the existing code style
3. Update documentation if needed
4. Test with multiple clients

## License

ISC

## Support

For issues or questions:
- Check [CLIENT_INTEGRATION.md](./CLIENT_INTEGRATION.md) for integration help
- Check [TESTING.md](./TESTING.md) for testing guidance
- Review server logs for error details
