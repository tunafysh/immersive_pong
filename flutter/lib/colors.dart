import 'package:flutter/material.dart';

class Zinc {
  static const zinc50  = Color(0xFFFAFAFA);
  static const zinc100 = Color(0xFFF4F4F5);
  static const zinc200 = Color(0xFFE4E4E7);
  static const zinc300 = Color(0xFFD4D4D8);
  static const zinc400 = Color(0xFFA1A1AA);
  static const zinc500 = Color(0xFF71717A);
  static const zinc600 = Color(0xFF52525B);
  static const zinc700 = Color(0xFF3F3F46);
  static const zinc800 = Color(0xFF27272A);
  static const zinc900 = Color(0xFF18181B);
}

final ThemeData zincLightTheme = ThemeData(
  brightness: Brightness.light,
  scaffoldBackgroundColor: Zinc.zinc50,
  colorScheme: ColorScheme.light(
    primary: Zinc.zinc500,
    onPrimary: Zinc.zinc50,
    background: Zinc.zinc100,
    surface: Zinc.zinc100,
    onSurface: Zinc.zinc800,
    secondary: Zinc.zinc400,
    onSecondary: Zinc.zinc50,
  ),
  appBarTheme: AppBarTheme(
    backgroundColor: Zinc.zinc100,
    foregroundColor: Zinc.zinc800,
  ),
  textTheme: TextTheme(
    bodyLarge: TextStyle(color: Zinc.zinc800),
    bodyMedium: TextStyle(color: Zinc.zinc700),
    bodySmall: TextStyle(color: Zinc.zinc600),
  ),
);

final ThemeData zincDarkTheme = ThemeData(
  brightness: Brightness.dark,
  scaffoldBackgroundColor: Zinc.zinc900,
  colorScheme: ColorScheme.dark(
    primary: Zinc.zinc400,
    onPrimary: Zinc.zinc900,
    background: Zinc.zinc800,
    surface: Zinc.zinc700,
    onSurface: Zinc.zinc200,
    secondary: Zinc.zinc500,
    onSecondary: Zinc.zinc900,
  ),
  appBarTheme: AppBarTheme(
    backgroundColor: Zinc.zinc800,
    foregroundColor: Zinc.zinc200,
  ),
  textTheme: TextTheme(
    bodyLarge: TextStyle(color: Zinc.zinc200),
    bodyMedium: TextStyle(color: Zinc.zinc300),
    bodySmall: TextStyle(color: Zinc.zinc400),
  ),
);