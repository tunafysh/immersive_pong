import 'package:flutter/material.dart';
import 'package:immersive_pong/config.dart';
import 'package:logging/logging.dart';
import 'package:provider/provider.dart';

final _logger = Logger('Singleplayer');

class Singleplayer extends StatefulWidget {
  const Singleplayer({super.key});

  @override
  State<Singleplayer> createState() => _SingleplayerState();
}

class _SingleplayerState extends State<Singleplayer> {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // Title
          const Text("Singleplayer Mode", style: TextStyle(fontSize: 24)),
          const SizedBox(height: 20),

          // Simple OutlinedButton
          OutlinedButton(
            onPressed: () {
              showModalBottomSheet(
                context: context,
                shape: const RoundedRectangleBorder(
                  borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
                ),
                builder: (context) {
                  return Consumer<RendererConfig>(
                    builder: (context, config, _) {
                      return SizedBox(
                        height: 300,
                        child: Column(
                          children: [
                            const Padding(
                              padding: EdgeInsets.all(16.0),
                              child: Text(
                                "Configure Settings",
                                style: TextStyle(
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                            const Divider(),
                            ListTile(
                              leading: const Icon(Icons.speed),
                              title: const Text("Difficulty"),
                              trailing: Row(
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  Text(
                                    config.singleplayer.difficulty.name
                                        .toUpperCase(),
                                  ),
                                  Switch(
                                    value:
                                        config.singleplayer.difficulty ==
                                        Difficulty.hard,
                                    onChanged: (value) {
                                      config.setSingleplayerDifficulty(
                                        value
                                            ? Difficulty.hard
                                            : Difficulty.easy,
                                      );
                                    },
                                  ),
                                ],
                              ),
                            ),
                          ],
                        ),
                      );
                    },
                  );
                },
              );
            },
            child: const Text("Configure Settings"),
          ),

          const SizedBox(height: 10),

          // Simple ElevatedButton
          ElevatedButton(
            onPressed: () {
              print("Start Game pressed");
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: Theme.of(context).colorScheme.primary,
              foregroundColor: Theme.of(context).colorScheme.onPrimary,
            ),
            child: const Text("Start Game"),
          ),
        ],
      ),
    );
  }
}
