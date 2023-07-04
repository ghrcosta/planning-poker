import 'package:flutter/material.dart';

import 'package:ui/entity/session.dart';
import 'package:ui/screen/room/name_selection.dart';
import 'package:ui/network.dart';

class RoomScreen extends StatefulWidget {
  final String roomId;

  const RoomScreen({
    required this.roomId,
    super.key
  });

  @override
  State<RoomScreen> createState() => _RoomScreenState();
}

class _RoomScreenState extends State<RoomScreen> {
  Session _session = const Session(roomId: '', participantName: '');

  @override
  void initState() {
    super.initState();
    _loadCurrentSessionIfItExists();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _buildRoomBasedOnState(),
    );
  }

  Widget _buildRoomBasedOnState() {
    if (_session.roomId == '') {
      return _buildNameSelection();
    } else {
      return _buildRoom();
    }
  }

  Widget _buildNameSelection() {
    return RoomNameSelectionWidget(onSubmit: _addParticipant);
  }

  Widget _buildRoom() {
    return Center(
      child: Column(
        children: [
          FilledButton(
            onPressed: (() => {}),
            child: Text(
              'Room',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _loadCurrentSessionIfItExists() async {
    final savedSession = await Session.getCurrentSessionIfItExists();
    setState(() {
      if (savedSession != null) {
        _session = savedSession;
      } else {
        _session = const Session(roomId: '', participantName: '');
      }
    });
  }

  Future<void> _addParticipant(String name) async {
    addParticipant(widget.roomId, name)
      .then((session) => _setSession(session))
      .catchError((e) => print(e));
  }

  Future<void> _setSession(Session session) async {
    session.save();
    setState(() {
      _session = session;
    });
  }
}