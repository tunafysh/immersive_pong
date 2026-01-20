import 'package:flutter/material.dart';
// import 'package:tflite/tflite.dart';
import 'package:immersive_pong/app/app.ui.dart';
import 'package:immersive_pong/config.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(ChangeNotifierProvider(create: (_) => RendererConfig(), child: App()));
}
