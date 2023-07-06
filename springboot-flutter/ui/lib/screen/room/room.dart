import 'package:flutter/material.dart';
import 'package:ui/entity/participant.dart';
import 'package:ui/entity/room.dart';

import 'package:ui/entity/session.dart';
import 'package:ui/screen/room/name_selection.dart';
import 'package:ui/screen/room/voting_area.dart';
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
    return RoomVotingAreaWidget(
      onClickVote: ((text) => {}),
      onClickReveal: ((text) => {}),
      onClickClear: ((text) => {}),
      room: Room(
        id: _session.roomId,
        participants: [
          const Participant(name: "Caio", vote: "2"),
          const Participant(name: "George", vote: "2"),
          const Participant(name: "Malco", vote: "2"),
          const Participant(name: "Marco", vote: "3"),
          const Participant(name: "Leonardo", vote: "1"),
          const Participant(name: "Rafael", vote: "1"),
          const Participant(name: "Rebeca", vote: "3"),
          const Participant(name: "Salomão", vote: "13"),
          const Participant(name: "Thalis", vote: "?"),
          const Participant(name: "invalid", vote: null),
        ],
        votesRevealed: true
      ),
      voteSelected: "2",
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
      .catchError((e) =>
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString()))
        )
      );
  }

  Future<void> _setSession(Session session) async {
    session.save();
    setState(() {
      _session = session;
    });
  }
}