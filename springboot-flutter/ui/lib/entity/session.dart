import 'package:shared_preferences/shared_preferences.dart';

class Session {
  final String roomId;
  final String participantName;

  const Session({required this.roomId, required this.participantName});

  factory Session.fromJson(Map<String, dynamic> json) {
    return Session(
      roomId: json['roomId'],
      participantName: json['participantName'],
    );
  }

  static const _prefsIdRoom = 'roomId';
  static const _prefsIdParticipant = 'participantName';

  static Future<Session?> getCurrentSessionIfItExists() async {
    final prefs = await SharedPreferences.getInstance();

    final savedRoomId = prefs.getString(_prefsIdRoom);
    final savedParticipant = prefs.getString(_prefsIdParticipant);

    if (savedRoomId != null && savedParticipant != null) {
      return Session(roomId: savedRoomId, participantName: savedParticipant);
    }
    return null;
  }

  Future<void> save() async {
    final prefs = await SharedPreferences.getInstance();
    prefs.setString(_prefsIdRoom, roomId);
    prefs.setString(_prefsIdParticipant, participantName);
  }
}