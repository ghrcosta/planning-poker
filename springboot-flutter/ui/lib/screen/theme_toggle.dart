import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ThemeToggleFab extends StatelessWidget {
  const ThemeToggleFab({super.key});

  @override
  Widget build(BuildContext context) {
    return FloatingActionButton(
      onPressed: () => { _toggleTheme(context) },
      backgroundColor: Theme.of(context).colorScheme.surfaceVariant,
      child: Icon(
        Icons.lightbulb,
        color: Theme.of(context).colorScheme.inverseSurface,
      ),
    );
  }

  void _toggleTheme(BuildContext context) {
    context.isDarkMode
      ? Get.changeThemeMode(ThemeMode.light)
      : Get.changeThemeMode(ThemeMode.dark);
  }
}