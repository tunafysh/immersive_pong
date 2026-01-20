import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immersive_pong/components/elements/config_drawer.sp.dart';
import 'package:immersive_pong/providers/config_provider.dart';
import 'package:immersive_pong/config.dart';

class Singleplayer extends ConsumerWidget {
  const Singleplayer({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final config = ref.watch(configProvider);

    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.person,
              size: 80,
              color: Theme.of(context).colorScheme.primary,
            ),
            const SizedBox(height: 24),
            Text(
              'Singleplayer Mode',
              style: Theme.of(
                context,
              ).textTheme.headlineMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),
            Text(
              'Difficulty: ${config.difficulty?.name.toUpperCase() ?? "EASY"}',
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: config.difficulty == Difficulty.hard
                    ? Colors.red
                    : Colors.green,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 48),
            SizedBox(
              width: 200,
              child: OutlinedButton.icon(
                onPressed: () {
                  showModalBottomSheet(
                    context: context,
                    builder: (context) => const SingleplayerSettingsSheet(),
                  );
                },
                icon: const Icon(Icons.settings),
                label: const Text('Options'),
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: 200,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  foregroundColor: Theme.of(context).colorScheme.onPrimary,
                  backgroundColor: Theme.of(context).colorScheme.primary,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                onPressed: () {
                  // TODO: Navigate to game screen
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Starting game...'),
                      duration: Duration(seconds: 1),
                    ),
                  );
                },
                icon: const Icon(Icons.play_arrow),
                label: const Text('Start Game'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
