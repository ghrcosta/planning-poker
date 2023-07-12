class Participant {
  final String name;
  final String? vote;

  const Participant({required this.name, required this.vote});

  factory Participant.fromJson(Map<String, dynamic> json) {
    return Participant(name: json['name'], vote: json['vote']);
  }

  double convertToVoteSortingValue() {
    final vote = _getValidVote();
    switch(vote) {
      case "null":
        return -3;
      case "½":
        return 0.5;
      case "?":
        return -1;
      default:
        try {
          return double.parse(vote);
        } on FormatException {
          return -2;
        }
    }
  }

  String _getValidVote() {
    final vote = this.vote;
    if (vote == null) {
      return "null";
    }
    return vote;
  }
}