# Immersive Pong - Client Integration Guide

This guide explains how to integrate your client application with the Immersive Pong WebSocket party server.

## Overview

The server acts as a **relay** for ball position and velocity updates between players in a party. The party creator is designated as the **host**, and the party ends when the host disconnects.

## REST API Endpoints

### 1. List All Parties

Get a list of all active parties.

**Endpoint:** `GET /parties`

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "My Pong Party",
    "createdAt": 1711234567890,
    "hostId": "660e8400-e29b-41d4-a716-446655440001",
    "members": [
      { "playerId": "660e8400-e29b-41d4-a716-446655440001", "joinedAt": 1711234567890 },
      { "playerId": "770e8400-e29b-41d4-a716-446655440002", "joinedAt": 1711234568000 }
    ]
  }
]
```

### 2. Create a Party

Create a new party. The creator automatically becomes the host.

**Endpoint:** `POST /parties`

**Request Body:**
```json
{
  "name": "My Pong Party"
}
```

**Response:** (Status: 201 Created)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "My Pong Party",
  "createdAt": 1711234567890,
  "hostId": "660e8400-e29b-41d4-a716-446655440001",
  "members": [
    { "playerId": "660e8400-e29b-41d4-a716-446655440001", "joinedAt": 1711234567890 }
  ]
}
```

**Important:** Save the `hostId` from the response - you'll need it to connect via WebSocket!

### 3. Join a Party

Join an existing party and receive a unique player ID.

**Endpoint:** `POST /parties/:partyId/join`

**Request Body:** (Empty object or omit body)
```json
{}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "My Pong Party",
  "createdAt": 1711234567890,
  "hostId": "660e8400-e29b-41d4-a716-446655440001",
  "members": [
    { "playerId": "660e8400-e29b-41d4-a716-446655440001", "joinedAt": 1711234567890 },
    { "playerId": "770e8400-e29b-41d4-a716-446655440002", "joinedAt": 1711234568000 }
  ],
  "yourPlayerId": "770e8400-e29b-41d4-a716-446655440002"
}
```

**Important:** Save `yourPlayerId` from the response - you'll need it to connect via WebSocket!

## WebSocket Connection

### Connection URL

Connect to the WebSocket using your party ID and player ID:

```
ws://your-server-address/ws/{partyId}?playerId={playerId}
```

**Example:**
```
ws://localhost:8787/ws/550e8400-e29b-41d4-a716-446655440000?playerId=660e8400-e29b-41d4-a716-446655440001
```

### Connection Validation

The server validates:
1. Party exists
2. `playerId` query parameter is provided
3. Player is a member of the party

If validation fails, you'll receive an error message and the connection will close.

## WebSocket Message Protocol

All messages are JSON-formatted strings.

### Messages Client SENDS to Server

#### Ball Update

Send this message whenever the ball position/velocity changes on your device.

```json
{
  "type": "ball_update",
  "data": {
    "pos": { "x": 540.5, "y": 960.2 },
    "vel": { "x": 450.0, "y": -450.0 },
    "timestamp": 1711234567890
  }
}
```

**Fields:**
- `pos.x`, `pos.y`: Current ball position coordinates
- `vel.x`, `vel.y`: Current ball velocity (pixels per second)
- `timestamp`: Client timestamp (milliseconds since epoch)

### Messages Client RECEIVES from Server

#### 1. Party State (Initial)

Sent immediately when you connect. Contains current party information.

```json
{
  "type": "party_state",
  "data": {
    "partyId": "550e8400-e29b-41d4-a716-446655440000",
    "hostId": "660e8400-e29b-41d4-a716-446655440001",
    "members": [
      "660e8400-e29b-41d4-a716-446655440001",
      "770e8400-e29b-41d4-a716-446655440002"
    ],
    "yourPlayerId": "770e8400-e29b-41d4-a716-446655440002"
  }
}
```

#### 2. Ball Sync

Received when another player sends a ball update. Use this to synchronize ball position.

```json
{
  "type": "ball_sync",
  "data": {
    "pos": { "x": 540.5, "y": 960.2 },
    "vel": { "x": 450.0, "y": -450.0 },
    "timestamp": 1711234567890,
    "fromPlayerId": "660e8400-e29b-41d4-a716-446655440001"
  }
}
```

**Note:** You will NOT receive your own ball updates - only updates from other players.

#### 3. Player Joined

Received when a new player joins the party.

```json
{
  "type": "player_joined",
  "data": {
    "playerId": "880e8400-e29b-41d4-a716-446655440003",
    "playerCount": 3
  }
}
```

#### 4. Player Left

Received when a player disconnects (but not the host).

```json
{
  "type": "player_left",
  "data": {
    "playerId": "880e8400-e29b-41d4-a716-446655440003",
    "playerCount": 2
  }
}
```

#### 5. Party Closed

Received when the host disconnects. The party has ended.

```json
{
  "type": "party_closed",
  "data": {
    "reason": "host_disconnected"
  }
}
```

**Action Required:** Disconnect your WebSocket and return to the party list/menu.

#### 6. Error

Received when something goes wrong.

```json
{
  "type": "error",
  "data": {
    "message": "Invalid ball_update format"
  }
}
```

**Common errors:**
- `"Party not found"` - Party ID is invalid
- `"playerId query parameter required"` - Missing playerId in URL
- `"Player not member of this party"` - Player hasn't joined via REST API
- `"Invalid ball_update format"` - Malformed ball update message
- `"Invalid message format"` - JSON parsing failed

## Example Implementation (Kotlin/Compose)

### Step 1: Create or Join Party

```kotlin
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Party(
    val id: String,
    val name: String,
    val createdAt: Long,
    val hostId: String,
    val members: List<Member>,
    val yourPlayerId: String? = null
)

@Serializable
data class Member(
    val playerId: String,
    val joinedAt: Long
)

val client = HttpClient()
val json = Json { ignoreUnknownKeys = true }

// Create party
suspend fun createParty(name: String): Party {
    val response: String = client.post("http://your-server/parties") {
        setBody("""{"name":"$name"}""")
        header("Content-Type", "application/json")
    }.bodyAsText()
    return json.decodeFromString<Party>(response)
}

// Join party
suspend fun joinParty(partyId: String): Party {
    val response: String = client.post("http://your-server/parties/$partyId/join") {
        setBody("{}")
        header("Content-Type", "application/json")
    }.bodyAsText()
    return json.decodeFromString<Party>(response)
}
```

### Step 2: Connect to WebSocket

```kotlin
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*

class PartyWebSocketClient(
    private val serverUrl: String,
    private val partyId: String,
    private val playerId: String
) {
    private var session: DefaultClientWebSocketSession? = null
    private val client = HttpClient {
        install(WebSockets)
    }
    
    suspend fun connect(
        onBallSync: (pos: Offset, vel: Offset, fromPlayerId: String) -> Unit,
        onPlayerJoined: (playerId: String, count: Int) -> Unit,
        onPlayerLeft: (playerId: String, count: Int) -> Unit,
        onPartyClosed: () -> Unit,
        onError: (message: String) -> Unit
    ) {
        try {
            client.webSocket(
                urlString = "$serverUrl/ws/$partyId?playerId=$playerId"
            ) {
                session = this
                
                // Listen for incoming messages
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        handleMessage(
                            text,
                            onBallSync,
                            onPlayerJoined,
                            onPlayerLeft,
                            onPartyClosed,
                            onError
                        )
                    }
                }
            }
        } catch (e: Exception) {
            onError("Connection error: ${e.message}")
        }
    }
    
    private fun handleMessage(
        text: String,
        onBallSync: (Offset, Offset, String) -> Unit,
        onPlayerJoined: (String, Int) -> Unit,
        onPlayerLeft: (String, Int) -> Unit,
        onPartyClosed: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val message = json.parseToJsonElement(text).jsonObject
            val type = message["type"]?.jsonPrimitive?.content
            val data = message["data"]?.jsonObject
            
            when (type) {
                "party_state" -> {
                    // Initial state received
                    println("Connected to party!")
                }
                "ball_sync" -> {
                    val posObj = data?.get("pos")?.jsonObject
                    val velObj = data?.get("vel")?.jsonObject
                    val fromId = data?.get("fromPlayerId")?.jsonPrimitive?.content
                    
                    if (posObj != null && velObj != null && fromId != null) {
                        val pos = Offset(
                            posObj["x"]?.jsonPrimitive?.float ?: 0f,
                            posObj["y"]?.jsonPrimitive?.float ?: 0f
                        )
                        val vel = Offset(
                            velObj["x"]?.jsonPrimitive?.float ?: 0f,
                            velObj["y"]?.jsonPrimitive?.float ?: 0f
                        )
                        onBallSync(pos, vel, fromId)
                    }
                }
                "player_joined" -> {
                    val playerId = data?.get("playerId")?.jsonPrimitive?.content
                    val count = data?.get("playerCount")?.jsonPrimitive?.int
                    if (playerId != null && count != null) {
                        onPlayerJoined(playerId, count)
                    }
                }
                "player_left" -> {
                    val playerId = data?.get("playerId")?.jsonPrimitive?.content
                    val count = data?.get("playerCount")?.jsonPrimitive?.int
                    if (playerId != null && count != null) {
                        onPlayerLeft(playerId, count)
                    }
                }
                "party_closed" -> {
                    onPartyClosed()
                }
                "error" -> {
                    val msg = data?.get("message")?.jsonPrimitive?.content ?: "Unknown error"
                    onError(msg)
                }
            }
        } catch (e: Exception) {
            onError("Failed to parse message: ${e.message}")
        }
    }
    
    suspend fun sendBallUpdate(ball: Ball) {
        val message = buildJsonObject {
            put("type", "ball_update")
            putJsonObject("data") {
                putJsonObject("pos") {
                    put("x", ball.pos.x)
                    put("y", ball.pos.y)
                }
                putJsonObject("vel") {
                    put("x", ball.vel.x)
                    put("y", ball.vel.y)
                }
                put("timestamp", System.currentTimeMillis())
            }
        }
        
        session?.send(Frame.Text(message.toString()))
    }
    
    suspend fun disconnect() {
        session?.close()
    }
}
```

### Step 3: Integrate with Game Loop

```kotlin
@Composable
fun MultiplayerGame(
    partyId: String,
    playerId: String
) {
    var ballState by remember { mutableStateOf(Ball.create()) }
    var score by remember { mutableStateOf(Scoreboard()) }
    val wsClient = remember { 
        PartyWebSocketClient("ws://your-server", partyId, playerId) 
    }
    
    LaunchedEffect(Unit) {
        // Connect to WebSocket
        launch {
            wsClient.connect(
                onBallSync = { pos, vel, fromId ->
                    // Update ball from other player
                    ballState = ballState.copy(pos = pos, vel = vel)
                },
                onPlayerJoined = { playerId, count ->
                    println("Player $playerId joined (total: $count)")
                },
                onPlayerLeft = { playerId, count ->
                    println("Player $playerId left (total: $count)")
                },
                onPartyClosed = {
                    // Host left - return to menu
                    println("Party closed!")
                    // navController.popBackStack()
                },
                onError = { message ->
                    println("Error: $message")
                }
            )
        }
        
        // Game loop
        launch {
            while (true) {
                delay(16) // ~60 FPS
                val deltaTime = 0.016
                
                // Update ball physics locally
                val (newBall, newScore) = ballState.update(
                    deltaTime, score, config, paddles
                )
                ballState = newBall
                score = newScore
                
                // Send update to other players
                wsClient.sendBallUpdate(ballState)
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            runBlocking { wsClient.disconnect() }
        }
    }
    
    // Your game rendering here
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawBall(ballState, colors)
        // ...
    }
}
```

## Best Practices

### 1. Rate Limiting

Avoid sending too many updates per second. Recommended: 30-60 updates/second max.

```kotlin
var lastUpdateTime = 0L
val updateInterval = 16L // ~60 FPS

if (System.currentTimeMillis() - lastUpdateTime > updateInterval) {
    wsClient.sendBallUpdate(ballState)
    lastUpdateTime = System.currentTimeMillis()
}
```

### 2. Handling Latency

Consider interpolating between ball positions to smooth out network lag:

```kotlin
// Store received position and velocity
var targetPos = ball.pos
var targetVel = ball.vel

// Interpolate towards target
val smoothingFactor = 0.3f
ball = ball.copy(
    pos = ball.pos + (targetPos - ball.pos) * smoothingFactor,
    vel = ball.vel + (targetVel - ball.vel) * smoothingFactor
)
```

### 3. Error Handling

Always handle connection errors and party closure gracefully:

```kotlin
onPartyClosed = {
    // Show message to user
    showDialog("Party Ended", "The host has left the party")
    // Navigate back to menu
    navController.popBackStack()
}

onError = { message ->
    // Log error
    Log.e("WebSocket", message)
    // Optionally show to user
    if (message.contains("Party not found")) {
        navController.popBackStack()
    }
}
```

### 4. Connection Lifecycle

Properly manage WebSocket lifecycle:
- Connect in `LaunchedEffect`
- Disconnect in `DisposableEffect.onDispose`
- Handle reconnection on network errors
- Clean up resources when leaving screen

## Testing Checklist

- [ ] Create party successfully
- [ ] List parties shows created party
- [ ] Join party successfully
- [ ] WebSocket connects with valid credentials
- [ ] WebSocket rejects invalid party ID
- [ ] WebSocket rejects invalid player ID
- [ ] Ball updates are sent and received correctly
- [ ] Player join notifications work
- [ ] Player leave notifications work
- [ ] Party closes when host disconnects
- [ ] Non-host can leave without closing party
- [ ] Error messages are displayed properly

## Troubleshooting

**Problem:** "Party not found" when connecting to WebSocket
- **Solution:** Ensure you created/joined the party via REST API first

**Problem:** Ball updates not received
- **Solution:** Check that you're sending valid JSON with `pos` and `vel` objects

**Problem:** Connection immediately closes
- **Solution:** Check server logs for validation errors (party/player validation)

**Problem:** Multiple ball updates causing jitter
- **Solution:** Implement rate limiting and interpolation

## Server Information

- **Framework:** Hono (Cloudflare Workers compatible)
- **WebSocket Protocol:** JSON over WebSocket
- **State Management:** In-memory (parties reset on server restart)
- **Authentication:** None (use player IDs for identification only)

## Future Enhancements

Potential features to add:
- Persistent state (database instead of in-memory)
- Game state synchronization (score, game start/end)
- Spectator mode
- Host migration (new host when original leaves)
- Ping/pong for connection health monitoring
- Rate limiting per client
- Reconnection support with state recovery
