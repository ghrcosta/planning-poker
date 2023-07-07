import 'package:flutter/material.dart';

import 'package:ui/network.dart';
import 'package:ui/screen/base.dart';
import 'package:ui/screen/theme_toggle.dart';

class HomeScreen extends StatelessWidget {
  final Function(String?) navigateToRoom;

  const HomeScreen({
    required this.navigateToRoom,
    super.key
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      floatingActionButton: const ThemeToggleFab(),
      backgroundColor: Theme.of(context).colorScheme.background,
      body: Center(
        child: Column(
          children: [
            const SizedBox(height: 100),
            baseButton(context, 'Create room', () => { _createRoomAndNavigateToIt(context) }, width: 200)
          ],
        )
      ),
    );
  }

  Future<void> _createRoomAndNavigateToIt(BuildContext context) async {
    createRoom()
      .then((room) => navigateToRoom(room.id))
      .catchError((e) =>
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString()))
        )
      );
  }
}