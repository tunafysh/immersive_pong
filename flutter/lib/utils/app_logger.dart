import 'package:logger/logger.dart';

/// Global logger instance configured for the entire application.
///
/// Usage:
/// - Use `appLogger.d()` for debug messages (removed in production)
/// - Use `appLogger.i()` for informational messages
/// - Use `appLogger.w()` for warnings
/// - Use `appLogger.e()` for errors
///
/// Example:
/// ```dart
/// appLogger.d('Debug message');
/// appLogger.i('User tapped button', 'User ID: 123');
/// appLogger.w('Warning message');
/// appLogger.e('Error occurred', error: e, stackTrace: stackTrace);
/// ```
final appLogger = Logger(
  printer: PrettyPrinter(
    methodCount: 2, // Number of method calls to be displayed
    errorMethodCount: 8, // Number of method calls for errors
    lineLength: 120, // Width of the output
    colors: true, // Colorful log messages
    printEmojis: false, // Print an emoji for each log message
    dateTimeFormat: DateTimeFormat.onlyTimeAndSinceStart,
  ),
  level: Level.debug, // Change to Level.info in production to hide debug logs
);

/// Simplified logger for production use (less verbose).
/// Use this in release builds by replacing `appLogger` with `productionLogger`.
final productionLogger = Logger(
  printer: SimplePrinter(colors: true, printTime: true),
  level: Level.info,
);
