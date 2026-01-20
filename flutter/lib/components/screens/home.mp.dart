import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immersive_pong/providers/config_provider.dart';
import 'package:immersive_pong/providers/room_provider.dart';
import 'package:immersive_pong/config.dart';
import 'package:qr_flutter/qr_flutter.dart';

class Multiplayer extends ConsumerWidget {
  const Multiplayer({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final room = ref.watch(roomProvider);

    if (room != null) {
      return _RoomView(room: room);
    }

    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.group,
              size: 80,
              color: Theme.of(context).colorScheme.primary,
            ),
            const SizedBox(height: 24),
            Text(
              'Multiplayer Mode',
              style: Theme.of(
                context,
              ).textTheme.headlineMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),
            Text(
              'Play against a friend',
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: Theme.of(context).colorScheme.onSurface.withOpacity(0.7),
              ),
            ),
            const SizedBox(height: 48),
            SizedBox(
              width: 200,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  foregroundColor: Theme.of(context).colorScheme.onPrimary,
                  backgroundColor: Theme.of(context).colorScheme.primary,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                onPressed: () {
                  ref.read(roomProvider.notifier).createRoom();
                },
                icon: const Icon(Icons.add),
                label: const Text('Create Room'),
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: 200,
              child: OutlinedButton.icon(
                onPressed: () {
                  // TODO: Implement scan QR code to join
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Scan QR code feature coming soon...'),
                      duration: Duration(seconds: 2),
                    ),
                  );
                },
                icon: const Icon(Icons.qr_code_scanner),
                label: const Text('Join Room'),
              ),
            ),
            const SizedBox(height: 24),
            Text(
              '💡 Future: NFC support for quick joining',
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: Theme.of(context).colorScheme.onSurface.withOpacity(0.5),
                fontStyle: FontStyle.italic,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _RoomView extends ConsumerWidget {
  final Room room;

  const _RoomView({required this.room});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  IconButton(
                    onPressed: () {
                      ref.read(roomProvider.notifier).closeRoom();
                    },
                    icon: const Icon(Icons.arrow_back),
                    tooltip: 'Back',
                  ),
                  const Spacer(),
                ],
              ),
              Text(
                'Room Created!',
                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 16),
              Text(
                'Share this QR code with your friend',
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: Theme.of(
                    context,
                  ).colorScheme.onSurface.withOpacity(0.7),
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'They can scan it in-app to join',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: Theme.of(
                    context,
                  ).colorScheme.onSurface.withOpacity(0.5),
                  fontStyle: FontStyle.italic,
                ),
              ),
              const SizedBox(height: 24),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.1),
                      blurRadius: 10,
                      spreadRadius: 2,
                    ),
                  ],
                ),
                child: QrImageView(
                  data: room.qrData,
                  version: QrVersions.auto,
                  size: 250,
                  backgroundColor: Colors.white,
                ),
              ),
              const SizedBox(height: 24),
              Text(
                'Room ID: ${room.id.substring(0, 8)}...',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  fontFamily: 'monospace',
                  color: Theme.of(
                    context,
                  ).colorScheme.onSurface.withOpacity(0.6),
                ),
              ),
              const SizedBox(height: 24),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  OutlinedButton.icon(
                    onPressed: () {
                      showModalBottomSheet(
                        context: context,
                        builder: (context) => const MultiplayerSettingsSheet(),
                      );
                    },
                    icon: const Icon(Icons.settings),
                    label: const Text('Options'),
                  ),
                  const SizedBox(width: 16),
                  ElevatedButton.icon(
                    style: ElevatedButton.styleFrom(
                      foregroundColor: Theme.of(context).colorScheme.onPrimary,
                      backgroundColor: Theme.of(context).colorScheme.primary,
                    ),
                    onPressed: () {
                      // TODO: Start game when both players are ready
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                          content: Text('Waiting for opponent to join...'),
                          duration: Duration(seconds: 2),
                        ),
                      );
                    },
                    icon: const Icon(Icons.play_arrow),
                    label: const Text('Start Game'),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class MultiplayerSettingsSheet extends ConsumerWidget {
  const MultiplayerSettingsSheet({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // Force top paddle to human in multiplayer
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ref.read(configProvider.notifier).setTopPaddle(PaddleType.human);
    });

    return SizedBox(
      height: 200,
      child: Column(
        children: [
          const Padding(
            padding: EdgeInsets.all(16.0),
            child: Text(
              "Configure Settings",
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
          ),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.swap_vert),
            title: const Text("Top Paddle"),
            trailing: Chip(
              label: const Text('HUMAN'),
              backgroundColor: Colors.blue.withOpacity(0.2),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            child: Text(
              'In multiplayer, the top paddle is always controlled by a human player.',
              style: TextStyle(
                fontSize: 12,
                fontStyle: FontStyle.italic,
                color: Theme.of(context).colorScheme.onSurface.withOpacity(0.6),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
