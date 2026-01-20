import 'package:flutter/material.dart';
import 'package:logging/logging.dart';

final _logger = Logger('Multiplayer');

class Multiplayer extends StatelessWidget {
  const Multiplayer({super.key});

  @override
  Widget build(BuildContext context) {
    return const Text("Multiplayer Mode", style: TextStyle(fontSize: 24));
  }
}
