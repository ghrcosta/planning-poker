import 'package:flutter/material.dart';

import 'package:ui/entity/participant.dart';
import 'package:ui/screen/base.dart';

class VoteResultsWidget extends StatefulWidget {
  final List<Participant> participantResults;
  final String userName;

  const VoteResultsWidget({
    required this.participantResults,
    required this.userName,
    super.key
  });

  @override
  State<VoteResultsWidget> createState() => _VoteResultsWidgetState();
}

class _VoteResultsWidgetState extends State<VoteResultsWidget> {
  @override
  Widget build(BuildContext context) {
    final voteMap = <String, List<String>>{};
    for (var i = 0; i < widget.participantResults.length; i++) {
      final participant = widget.participantResults[i];
      final id = participant.vote;
      if (id == null) {
        continue;
      }
      if (voteMap.containsKey(id)) {
        voteMap[id]!.add(participant.name);
      } else {
        voteMap[id] = [participant.name];
      }
    }

    var sortedMapKeys = voteMap.keys.toList();
    sortedMapKeys.sort((a, b) =>
      Participant.convertToVoteSortingValue(a).compareTo(Participant.convertToVoteSortingValue(b)));
    sortedMapKeys = sortedMapKeys.reversed.toList();
    
    final List<Widget> entryGroups = [];
    for (var i = 0; i < sortedMapKeys.length; i++) {
      final key = sortedMapKeys[i];
      entryGroups.add(
        _voteResultEntry(key, voteMap[key]!, widget.userName)
      );
      entryGroups.add(
        const SizedBox(height: 20)
      );
    }

    final List<Widget> allWidgets = [const SizedBox(height: 80)];
    allWidgets.addAll(entryGroups);

    return Column(
      children: allWidgets,
    );
  }

  Widget _voteResultEntry(String vote, List<String> participantNames, String userName) {
    final List<Widget> participantRows = [];

    participantNames.sort((a, b) => a.compareTo(b));
    for (var i = 0; i < participantNames.length; i++) {
      final name = participantNames[i];

      final isThisUser = (name == userName);
      
      var nameColor =
        isThisUser
          ? Theme.of(context).colorScheme.tertiary
          : Theme.of(context).colorScheme.secondary;

      participantRows.add(
        Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            _voteOptionResultCard(vote, isThisUser),
            const SizedBox(width: 5),
            Text(
              name,
              style: Theme.of(context).textTheme.titleLarge?.copyWith(color: nameColor)
            )
          ],
        )
      );
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: participantRows,
    );
  }

  Widget _voteOptionResultCard(String text, bool highlight) {
    final backgrounColor =
      highlight
        ? Theme.of(context).colorScheme.tertiaryContainer
        : Theme.of(context).colorScheme.surface;
    
    final textColor =
      highlight
        ? Theme.of(context).colorScheme.tertiary
        : Theme.of(context).colorScheme.onSurfaceVariant;

    return Card(
      color: backgrounColor,
      shape: baseRectangleBorder,
      child: SizedBox(
        height: 40,
        width: 30,
        child: Center(
          child: Text(
            text,
            style: Theme.of(context).textTheme.titleMedium?.copyWith(color: textColor),
          ),
        ),
      ),
    );
  }
}