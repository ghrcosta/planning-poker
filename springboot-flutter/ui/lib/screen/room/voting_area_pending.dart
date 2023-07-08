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
    widget.participantsPendingResults.retainWhere((i) => i.vote != null);
    widget.participantsPendingResults.sort((a, b) => a.name.compareTo(b.name));

    final List<Widget> entryGroups = [];
    for (var i = 0; i < widget.participantsPendingResults.length; i++) {
      final participantName = widget.participantsPendingResults[i].name;
      final isThisUser = (participantName == widget.userName);
      entryGroups.add(
        _votePendingEntry(participantName, isThisUser)
      );
    }

    final List<Widget> allWidgets = [const SizedBox(height: 80)];
    allWidgets.addAll(entryGroups);

    return Column(
      children: allWidgets,
    );
  }

  Widget _votePendingEntry(String name, bool highlight) {
    var nameColor =
      highlight
        ? Theme.of(context).colorScheme.tertiary
        : Theme.of(context).colorScheme.secondary;

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
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
    final backgrounColor =
      highlight
        ? Theme.of(context).colorScheme.tertiaryContainer
        : Theme.of(context).colorScheme.surface;

    return Card(
      color: backgrounColor,
      shape: baseRectangleBorder,
      child: const SizedBox(
        height: 40,
        width: 30,
      ),
    );
  }
}