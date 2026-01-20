import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immersive_pong/config.dart';

class ConfigNotifier extends Notifier<Config> {
  @override
  Config build() {
    return Config(
      mode: Mode.singleplayer,
      difficulty: Difficulty.easy,
      topPaddle: PaddleType.ai,
    );
  }

  void setMode(Mode mode) {
    state = Config(
      mode: mode,
      difficulty: state.difficulty,
      topPaddle: state.topPaddle,
    );
  }

  void setDifficulty(Difficulty difficulty) {
    state = Config(
      mode: state.mode,
      difficulty: difficulty,
      topPaddle: state.topPaddle,
    );
  }

  void setTopPaddle(PaddleType paddleType) {
    state = Config(
      mode: state.mode,
      difficulty: state.difficulty,
      topPaddle: paddleType,
    );
  }
}

final configProvider = NotifierProvider<ConfigNotifier, Config>(() {
  return ConfigNotifier();
});
