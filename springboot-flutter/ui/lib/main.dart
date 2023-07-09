import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import 'debug.dart';
import 'firebase_options.dart';
import 'router.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();  // Required to initialize Firebase
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  doFirestoreExtraInitialization();
  runApp(const PlanningPokerApp());
}

class PlanningPokerApp extends StatefulWidget {
  const PlanningPokerApp({super.key});

  @override
  State<StatefulWidget> createState() => _PlanningPokerAppState();
}

class _PlanningPokerAppState extends State<PlanningPokerApp> {
  final _routerDelegate = AppRouterDelegate();
  final _routeInformationParser = AppRouteInformationParser();

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp.router(
      title: 'Planning Poker',
      theme: ThemeData(useMaterial3: true, colorScheme: lightColorScheme, textTheme: textTheme),
      darkTheme: ThemeData(useMaterial3: true, colorScheme: darkColorScheme, textTheme: textTheme),
      themeMode: ThemeMode.system,
      routerDelegate: _routerDelegate,
      routeInformationParser: _routeInformationParser,
    );
  }
}

// Everything else uses the default: https://api.flutter.dev/flutter/material/TextTheme-class.html
const textTheme = TextTheme(
  bodyLarge: TextStyle(fontSize: 14),
  bodyMedium: TextStyle(fontSize: 12),
  bodySmall: TextStyle(fontSize: 10)
);

// Colors created using https://m3.material.io/theme-builder#/custom
// Base colors:
// Primary: #526991
// Secondary: #8891a5
// Tertiary: #61925b
// Neutral: #909094

const lightColorScheme = ColorScheme(
  brightness: Brightness.light,
  primary: Color(0xFF285EA7),
  onPrimary: Color(0xFFFFFFFF),
  primaryContainer: Color(0xFFD6E3FF),
  onPrimaryContainer: Color(0xFF001B3D),
  secondary: Color(0xFF555F71),
  onSecondary: Color(0xFFFFFFFF),
  secondaryContainer: Color(0xFFD9E3F9),
  onSecondaryContainer: Color(0xFF121C2B),
  tertiary: Color(0xFF296B2A),
  onTertiary: Color(0xFFFFFFFF),
  tertiaryContainer: Color(0xFFACF4A2),
  onTertiaryContainer: Color(0xFF002203),
  error: Color(0xFFBA1A1A),
  errorContainer: Color(0xFFFFDAD6),
  onError: Color(0xFFFFFFFF),
  onErrorContainer: Color(0xFF410002),
  background: Color(0xFFFDFBFF),
  onBackground: Color(0xFF1A1B1E),
  surface: Color(0xFFFDFBFF),
  onSurface: Color(0xFF1A1B1E),
  surfaceVariant: Color(0xFFC4C6CF),  // Default was Neutral Variance Luminance 90, this is 80
  onSurfaceVariant: Color(0xFF44474E),
  outline: Color(0xFF74777F),
  onInverseSurface: Color(0xFFF1F0F4),
  inverseSurface: Color(0xFF2F3033),
  inversePrimary: Color(0xFFA9C7FF),
  shadow: Color(0xFF000000),
  surfaceTint: Color(0xFF285EA7),
  outlineVariant: Color(0xFFC4C6CF),
  scrim: Color(0xFF000000),
);

const darkColorScheme = ColorScheme(
  brightness: Brightness.dark,
  primary: Color(0xFFA9C7FF),
  onPrimary: Color(0xFF003063),
  primaryContainer: Color(0xFF00468C),
  onPrimaryContainer: Color(0xFFD6E3FF),
  secondary: Color(0xFFBDC7DC),
  onSecondary: Color(0xFF283141),
  secondaryContainer: Color(0xFF3E4758),
  onSecondaryContainer: Color(0xFFD9E3F9),
  tertiary: Color(0xFF91D888),
  onTertiary: Color(0xFF003908),
  tertiaryContainer: Color(0xFF0B5314),
  onTertiaryContainer: Color(0xFFACF4A2),
  error: Color(0xFFFFB4AB),
  errorContainer: Color(0xFF93000A),
  onError: Color(0xFF690005),
  onErrorContainer: Color(0xFFFFDAD6),
  background: Color(0xFF1A1B1E),
  onBackground: Color(0xFFE3E2E6),
  surface: Color(0xFF2F3033),  // Default was Neutral Luminance 10, this is 20
  onSurface: Color(0xFFE3E2E6),
  surfaceVariant: Color(0xFF74777F),  // Default was Neutral Variance Luminance 30, this is 50
  onSurfaceVariant: Color(0xFFEEF0FA),  // Default was Neutral Variance Luminance 80, this is 95
  outline: Color(0xFF8E9099),
  onInverseSurface: Color(0xFF1A1B1E),
  inverseSurface: Color(0xFFE3E2E6),
  inversePrimary: Color(0xFF285EA7),
  shadow: Color(0xFF000000),
  surfaceTint: Color(0xFFA9C7FF),
  outlineVariant: Color(0xFF44474E),
  scrim: Color(0xFF000000),
);