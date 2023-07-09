class Participant {
  final String name;
  final String? vote;

  const Participant({required this.name, required this.vote});

  factory Participant.fromJson(Map<String, dynamic> json) {
    return Participant(name: json['name'], vote: json['vote']);
  }

  String getValidVote() {
    final vote = this.vote;
    if (vote == null) {
      return "null";
    }
    return vote;
  }

  static double convertToVoteSortingValue(String validVoteStr) {
    switch(validVoteStr) {
      case "null":
        return -2;
      case "½":
        return 0.5;
      case "?":
        return -1;
      default:
        try {
          return double.parse(validVoteStr);
        } on FormatException {
          return -3;
        }
    }
  }
}