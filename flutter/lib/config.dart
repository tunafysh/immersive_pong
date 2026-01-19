import 'package:flutter/material.dart';

enum Difficulty { easy, medium, hard }
enum Role { client, server }
enum SingleplayerMode { local, ai }

class SingleplayerConfig {
  SingleplayerMode mode = SingleplayerMode.local;
  Difficulty difficulty = Difficulty.easy;

  bool get isAI => mode == SingleplayerMode.ai;
}

class MultiplayerConfig {
  Role role = Role.client;
  String link = "";
}

class RendererConfig extends ChangeNotifier {
  // Current mode
  bool isMultiplayer = false;

  // Configs
  final singleplayer = SingleplayerConfig();
  final multiplayer = MultiplayerConfig();

  // Switch mode
  void setMultiplayer(bool value) {
    isMultiplayer = value;
    notifyListeners();
  }

  // Singleplayer updates
  void setSingleplayerMode(SingleplayerMode mode) {
    singleplayer.mode = mode;
    notifyListeners();
  }

  void setSingleplayerDifficulty(Difficulty d) {
    singleplayer.difficulty = d;
    notifyListeners();
  }

  // Multiplayer updates
  void setMultiplayerRole(Role r) {
    multiplayer.role = r;
    notifyListeners();
  }

  void setMultiplayerLink(String link) {
    multiplayer.link = link;
    notifyListeners();
  }
}
