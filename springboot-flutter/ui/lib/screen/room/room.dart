import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';

import 'package:ui/entity/participant.dart';
import 'package:ui/entity/room.dart';
import 'package:ui/entity/session.dart';
import 'package:ui/screen/room/name_selection.dart';
import 'package:ui/screen/room/voting_area.dart';
import 'package:ui/network.dart';
import 'package:ui/screen/theme_toggle.dart';

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
  late Stream<DocumentSnapshot> _roomStream;  // Cannot be final because it's set again on theme toggle

  @override
  void initState() {
    _loadCurrentSessionIfItExists();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.background,
      floatingActionButton: const ThemeToggleFab(),
      body: _buildRoomBasedOnState(),
    );
  }

  Widget _buildRoomBasedOnState() {
    if (_session.roomId == '' || _session.roomId != widget.roomId) {
      return _buildNameSelection();
    } else {
      return _startListeningForRoomUpdatesAndBuildVotingArea();
    }
  }

  Widget _buildNameSelection() {
    return RoomNameSelectionWidget(submitName: _addParticipant);
  }

  Widget _startListeningForRoomUpdatesAndBuildVotingArea() {
    _roomStream = FirebaseFirestore.instance.collection('room').doc(widget.roomId).snapshots();

    return StreamBuilder<DocumentSnapshot>(
      stream: _roomStream,
      builder: (BuildContext context, AsyncSnapshot<DocumentSnapshot> snapshot) {
        if (snapshot.hasError) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Something went wrong'))
          );
          return const Text('Something went wrong');
        }
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const LinearProgressIndicator(semanticsLabel: 'Loading indicator');
        }
        if (snapshot.hasError) {
          return Text("Connection to Firestore database failed: ${snapshot.error}");
        }
        if (snapshot.data!.data() == null) {
          return const Text("Failed to load data: Firestore returned null");
        }

        final Room room;
        try {
          final firestoreId = snapshot.data!.id;  // Same as "widget.roomId"
          final firestoreData = snapshot.data!.data()! as Map<String, dynamic>;
          room = Room.fromFirestore(firestoreId, firestoreData);
        } catch(e) {
          return Text("Failed to load data -- Invalid room object:\n$e");
        }
        return _buildVotingArea(room);
      }
    );
  }

  Widget _buildVotingArea(Room room) {
    final participant = room.participants
      .firstWhere((i) => i.name == _session.participantName,
      orElse: () => const Participant(name: '', vote: '')
    );
    return RoomVotingAreaWidget(
      submitVote: _submitVote,
      revealVotes: _revealVotes,
      clearVotes: _clearVotes,
      room: room,
      participant: participant,
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
    await addParticipant(widget.roomId, name)
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

  Future<void> _submitVote(String vote) async {
    await submitVote(vote)
      .catchError((e) =>
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString()))
        )
      );
  }

  Future<void> _revealVotes() async {
    await revealVotes()
      .catchError((e) =>
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString()))
        )
      );
  }

  Future<void> _clearVotes() async {
    await clearVotes()
      .catchError((e) =>
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString()))
        )
      );
  }
}