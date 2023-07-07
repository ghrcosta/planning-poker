import 'participant.dart';

class Room {
  final String id;
  final List<Participant> participants;
  final bool votesRevealed;

  const Room({required this.id, required this.participants, required this.votesRevealed});

  factory Room.fromJson(Map<String, dynamic> json) {
    return Room(
      id: json['id'],
      participants: List<Participant>.from(
        json['participants'].map(
          (participantJson) => Participant.fromJson(participantJson)
        )
      ),
      votesRevealed: json['votesRevealed']
    );
  }

  factory Room.fromFirestore(String id, Map<String, dynamic> data) {
    data.putIfAbsent('id', () => id);
    return Room.fromJson(data);
  }
}