class Participant {
  final String name;
  final String? vote;

  const Participant({required this.name, required this.vote});

  factory Participant.fromJson(Map<String, dynamic> json) {
    return Participant(name: json['name'], vote: json['vote']);
  }
}