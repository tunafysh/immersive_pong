import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:uuid/uuid.dart';
import 'package:web_socket_channel/web_socket_channel.dart';
import 'dart:convert';

class Room {
  final String id;
  final String hostName;
  final DateTime createdAt;
  final bool isActive;
  final String? wsUrl; // WebSocket URL: wss://ip:port
  final WebSocketChannel? channel;

  Room({
    required this.id,
    required this.hostName,
    required this.createdAt,
    this.isActive = true,
    this.wsUrl,
    this.channel,
  });

  Room copyWith({
    String? id,
    String? hostName,
    DateTime? createdAt,
    bool? isActive,
    String? wsUrl,
    WebSocketChannel? channel,
  }) {
    return Room(
      id: id ?? this.id,
      hostName: hostName ?? this.hostName,
      createdAt: createdAt ?? this.createdAt,
      isActive: isActive ?? this.isActive,
      wsUrl: wsUrl ?? this.wsUrl,
      channel: channel ?? this.channel,
    );
  }

  // QR code data as JSON for in-app scanning
  String get qrData =>
      jsonEncode({'roomId': id, 'wsUrl': wsUrl, 'hostName': hostName});

  // Parse QR code data from JSON
  static Room? fromQrData(String qrData) {
    try {
      final data = jsonDecode(qrData) as Map<String, dynamic>;
      return Room(
        id: data['roomId'] as String,
        hostName: data['hostName'] as String? ?? 'Host',
        createdAt: DateTime.now(),
        wsUrl: data['wsUrl'] as String?,
      );
    } catch (e) {
      print('Failed to parse QR data: $e');
      return null;
    }
  }
}

class RoomNotifier extends Notifier<Room?> {
  static const _uuid = Uuid();
  WebSocketChannel? _channel;

  @override
  Room? build() {
    // Clean up WebSocket connection when provider is disposed
    ref.onDispose(() {
      _channel?.sink.close();
    });
    return null;
  }

  Room createRoom({String hostName = 'Host', String? wsUrl}) {
    // Close existing connection if any
    _channel?.sink.close();

    // Create new WebSocket connection if URL is provided
    if (wsUrl != null && wsUrl.isNotEmpty) {
      try {
        _channel = WebSocketChannel.connect(Uri.parse(wsUrl));

        // Listen to incoming messages
        _channel!.stream.listen(
          (message) {
            // TODO: Handle incoming WebSocket messages
            print('WebSocket message received: $message');
          },
          onError: (error) {
            print('WebSocket error: $error');
          },
          onDone: () {
            print('WebSocket connection closed');
          },
        );
      } catch (e) {
        print('Failed to connect to WebSocket: $e');
      }
    }

    final room = Room(
      id: _uuid.v4(),
      hostName: hostName,
      createdAt: DateTime.now(),
      wsUrl: wsUrl,
      channel: _channel,
    );
    state = room;
    return room;
  }

  void closeRoom() {
    _channel?.sink.close();
    _channel = null;
    state = null;
  }

  void joinRoom(String roomId, {String? wsUrl}) {
    // Close existing connection if any
    _channel?.sink.close();

    // Connect to WebSocket if URL is provided
    if (wsUrl != null && wsUrl.isNotEmpty) {
      try {
        _channel = WebSocketChannel.connect(Uri.parse(wsUrl));

        // Send join message
        _channel!.sink.add('{"action":"join","roomId":"$roomId"}');

        // Listen to incoming messages
        _channel!.stream.listen(
          (message) {
            // TODO: Handle incoming WebSocket messages
            print('WebSocket message received: $message');
          },
          onError: (error) {
            print('WebSocket error: $error');
          },
          onDone: () {
            print('WebSocket connection closed');
          },
        );
      } catch (e) {
        print('Failed to connect to WebSocket: $e');
      }
    }

    final room = Room(
      id: roomId,
      hostName: 'Guest',
      createdAt: DateTime.now(),
      wsUrl: wsUrl,
      channel: _channel,
    );
    state = room;
  }

  void sendMessage(String message) {
    _channel?.sink.add(message);
  }
}

final roomProvider = NotifierProvider<RoomNotifier, Room?>(() {
  return RoomNotifier();
});
