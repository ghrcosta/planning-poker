import 'package:flutter/material.dart';

import 'package:ui/network.dart';
import 'package:ui/screen/base.dart';
import 'package:ui/screen/theme_toggle.dart';

class HomeScreen extends StatefulWidget {
  final Function(String) navigateToRoom;

  const HomeScreen({
    required this.navigateToRoom,
    super.key
  });

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  bool _isLoading = false;

  Future<void> _createRoomAndNavigateToIt(BuildContext context) async {
    setState(() { _isLoading = true; });
    await createRoom()
      .then((room) {
        setState(() { _isLoading = false; });
        widget.navigateToRoom(room.id);
      })
      .catchError((e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString()))
        );
      });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      floatingActionButton: const ThemeToggleFab(),
      backgroundColor: Theme.of(context).colorScheme.background,
      body: Stack(
        children: [
          Center(
            child: Column(
              children: [
                const SizedBox(height: 100),
                baseButton(
                  context,
                  'Create room',
                  () => { _createRoomAndNavigateToIt(context) },
                  width: 200
                )
              ],
            )
          ),
          if (_isLoading)
              const LinearProgressIndicator(semanticsLabel: 'Loading indicator'),
        ],
      ),
    );
  }
}