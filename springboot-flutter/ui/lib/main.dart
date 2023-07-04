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
      theme: ThemeData(
        useMaterial3: true,
      ),
      routerDelegate: _routerDelegate,
      routeInformationParser: _routeInformationParser,
    );
  }
}