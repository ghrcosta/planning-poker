import 'package:flutter/material.dart';

import 'package:ui/network.dart';

class HomeScreen extends StatelessWidget {
  final Function(String?) navigateToRoom;

  const HomeScreen({
    required this.navigateToRoom,
    super.key
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.background,
      body: Center(
        child: FilledButton(
          onPressed: _createRoomAndNavigateToIt,
          child: Text(
            'Create room',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
        ),
      ),
    );
  }

  void _createRoomAndNavigateToIt() async {
    createRoom()
      .then((room) => navigateToRoom(room.id))
      .catchError((e) => print(e));
  }
}