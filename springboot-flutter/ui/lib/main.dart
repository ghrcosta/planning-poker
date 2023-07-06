import 'package:flutter/material.dart';

import 'router.dart';

void main() {
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
    return MaterialApp.router(
      title: 'Planning Poker',
      theme: ThemeData(useMaterial3: true, colorScheme: lightColorScheme),
      darkTheme: ThemeData(useMaterial3: true, colorScheme: darkColorScheme),
      routerDelegate: _routerDelegate,
      routeInformationParser: _routeInformationParser,
    );
  }
}

// Colors created using https://m3.material.io/theme-builder#/custom

const lightColorScheme = ColorScheme(
  brightness: Brightness.light,
  primary: Color(0xFF1B1B1F),
  onPrimary: Color(0xFFE3E1EC),
  primaryContainer: Color(0xFFDDE1FF),
  onPrimaryContainer: Color(0xFF1B1B1F),
  secondary: Color(0xFF5A5D72),
  onSecondary: Color(0xFFFBF8FF),
  secondaryContainer: Color(0xFFDFE1F9),
  onSecondaryContainer: Color(0xFF171B2C),
  tertiary: Color(0xFF75546E),
  onTertiary: Color(0xFFFFFFFF),
  tertiaryContainer: Color(0xFFFFD7F4),
  onTertiaryContainer: Color(0xFF2C1229),
  error: Color(0xFFBA1A1A),
  errorContainer: Color(0xFFFFDAD6),
  onError: Color(0xFFFFFFFF),
  onErrorContainer: Color(0xFF410002),
  background: Color(0xFFFFFBFF),
  onBackground: Color(0xFF24005A),
  surface: Color(0xFFFFFBFF),
  onSurface: Color(0xFF1B1B1F),
  surfaceVariant: Color(0xFFE2E1EC),
  onSurfaceVariant: Color(0xFF45464F),
  outline: Color(0xFF767680),
  onInverseSurface: Color(0xFFF6EDFF),
  inverseSurface: Color(0xFF3A1D71),
  inversePrimary: Color(0xFFB8C4FF),
  shadow: Color(0xFF000000),
  surfaceTint: Color(0xFFFFFBFF),
  outlineVariant: Color(0xFFC6C5D0),
  scrim: Color(0xFF000000),
);

const darkColorScheme = ColorScheme(
  brightness: Brightness.dark,
  primary: Color(0xFFB8C4FF),
  onPrimary: Color(0xFF0F2878),
  primaryContainer: Color(0xFF2C4090),
  onPrimaryContainer: Color(0xFFDDE1FF),
  secondary: Color(0xFFC2C5DD),
  onSecondary: Color(0xFF2C2F42),
  secondaryContainer: Color(0xFF424659),
  onSecondaryContainer: Color(0xFFDFE1F9),
  tertiary: Color(0xFFE4BAD9),
  onTertiary: Color(0xFF44273F),
  tertiaryContainer: Color(0xFF5C3D56),
  onTertiaryContainer: Color(0xFFFFD7F4),
  error: Color(0xFFFFB4AB),
  errorContainer: Color(0xFF93000A),
  onError: Color(0xFF690005),
  onErrorContainer: Color(0xFFFFDAD6),
  background: Color(0xFF24005A),
  onBackground: Color(0xFFEADDFF),
  surface: Color(0xFF24005A),
  onSurface: Color(0xFFEADDFF),
  surfaceVariant: Color(0xFF45464F),
  onSurfaceVariant: Color(0xFFC6C5D0),
  outline: Color(0xFF90909A),
  onInverseSurface: Color(0xFF24005A),
  inverseSurface: Color(0xFFEADDFF),
  inversePrimary: Color(0xFF4559A9),
  shadow: Color(0xFF000000),
  surfaceTint: Color(0xFFB8C4FF),
  outlineVariant: Color(0xFF45464F),
  scrim: Color(0xFF000000),
);