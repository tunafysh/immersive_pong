import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immersive_pong/config.dart';
import 'package:immersive_pong/providers/config_provider.dart';

class SingleplayerSettingsSheet extends ConsumerWidget {
  const SingleplayerSettingsSheet({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final config = ref.watch(configProvider);
    final configNotifier = ref.read(configProvider.notifier);

    final isAiPaddle = config.topPaddle == PaddleType.ai;
    final isHardMode = config.difficulty == Difficulty.hard;

    return SizedBox(
      height: 400,
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
            leading: Icon(Icons.speed, color: isAiPaddle ? null : Colors.grey),
            title: Text(
              "Difficulty",
              style: TextStyle(color: isAiPaddle ? null : Colors.grey),
            ),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  config.difficulty?.name.toUpperCase() ?? 'EASY',
                  style: TextStyle(
                    color: isAiPaddle
                        ? (isHardMode ? Colors.red : Colors.green)
                        : Colors.grey,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(width: 8),
                Switch(
                  value: isHardMode,
                  onChanged: isAiPaddle
                      ? (value) {
                          configNotifier.setDifficulty(
                            value ? Difficulty.hard : Difficulty.easy,
                          );
                        }
                      : null,
                ),
              ],
            ),
          ),
          if (isAiPaddle && isHardMode)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0),
              child: Text(
                '* Hard mode might not be difficult at first because the AI doesn\'t know your play patterns yet.',
                style: TextStyle(
                  fontSize: 12,
                  fontStyle: FontStyle.italic,
                  color: Theme.of(
                    context,
                  ).colorScheme.onSurface.withValues(alpha: 0.6),
                ),
              ),
            ),
          ListTile(
            leading: const Icon(Icons.swap_vert),
            title: const Text("Top Paddle"),
            trailing: DropdownButton<PaddleType>(
              value: config.topPaddle,
              onChanged: (PaddleType? value) {
                if (value != null) {
                  configNotifier.setTopPaddle(value);
                }
              },
              items: PaddleType.values.map((PaddleType type) {
                return DropdownMenuItem<PaddleType>(
                  value: type,
                  child: Text(type.name.toUpperCase()),
                );
              }).toList(),
            ),
          ),
        ],
      ),
    );
  }
}
