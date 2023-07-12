import 'package:flutter/material.dart';
import 'package:ui/entity/participant.dart';

import 'package:ui/screen/base.dart';

class VotePendingWidget extends StatefulWidget {
  final List<Participant> participantsPendingResults;
  final String userName;

  const VotePendingWidget({
    required this.participantsPendingResults,
    required this.userName,
    super.key
  });

  @override
  State<VotePendingWidget> createState() => _VotePendingWidgetState();
}

class _VotePendingWidgetState extends State<VotePendingWidget> {
  @override
  Widget build(BuildContext context) {
    final participantsWithVotes = widget.participantsPendingResults.where((i) => i.vote != null).toList();
    participantsWithVotes.sort((a, b) => a.name.toLowerCase().compareTo(b.name.toLowerCase()));

    final participantsWithoutVotes = widget.participantsPendingResults.where((i) => i.vote == null).toList();
    participantsWithoutVotes.sort((a, b) => a.name.toLowerCase().compareTo(b.name.toLowerCase()));

    final sortedParticipants = participantsWithVotes.followedBy(participantsWithoutVotes);

    final List<Widget> entryGroups = [];
    for (final participant in sortedParticipants) {
      final isThisUser = (participant.name == widget.userName);
      entryGroups.add(
        _votePendingEntry(participant.name, hasVote: (participant.vote != null), highlight: isThisUser)
      );
    }

    final List<Widget> allWidgets = [const SizedBox(height: 80)];
    allWidgets.addAll(entryGroups);

    return Column(
      children: allWidgets,
    );
  }

  Widget _votePendingEntry(String name, { required bool hasVote, required bool highlight }) {
    Color nameColor;
    if (hasVote) {
      nameColor =
        highlight
          ? Theme.of(context).colorScheme.tertiary
          : Theme.of(context).colorScheme.secondary;
    } else {
      nameColor =
        highlight
          ? Theme.of(context).colorScheme.tertiaryContainer
          : Theme.of(context).colorScheme.secondaryContainer;
    }

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        if (hasVote)
          _votePendingCard(highlight),
        const SizedBox(width: 5),
        Text(
          name,
          style: Theme.of(context).textTheme.titleLarge?.copyWith(color: nameColor)
        )
      ],
    );
  }

  Widget _votePendingCard(bool highlight) {
    final backgroundColor =
      highlight
        ? Theme.of(context).colorScheme.tertiaryContainer
        : Theme.of(context).colorScheme.surface;

    return Card(
      color: backgroundColor,
      shape: baseRectangleBorder,
      child: const SizedBox(
        height: 40,
        width: 30,
      ),
    );
  }
}