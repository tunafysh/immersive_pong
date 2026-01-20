enum Mode {
  singleplayer,
  multiplayer,
}

enum Difficulty {
  easy,
  hard,
}

enum PaddleType {
  human,
  ai,
}

class Config {
  Mode mode;
  Difficulty? difficulty;
  PaddleType topPaddle;

  Config({
    required this.mode,
    required this.difficulty,
    required this.topPaddle,
  });
}