import 'package:flutter/material.dart';

import 'package:ui/entity/participant.dart';
import 'package:ui/entity/room.dart';
import 'package:ui/screen/room/voting_area_cards.dart';
import 'package:ui/screen/room/voting_area_pending.dart';
import 'package:ui/screen/room/voting_area_results.dart';

class RoomVotingAreaWidget extends StatefulWidget {
  final Function(String) submitVote;
  final Function() revealVotes;
  final Function() clearVotes;
  final Room room;
  final Participant participant;

  const RoomVotingAreaWidget({
    required this.submitVote,
    required this.revealVotes,
    required this.clearVotes,
    required this.room,
    required this.participant,
    super.key
  });

  @override
  State<RoomVotingAreaWidget> createState() => _RoomVotingAreaWidgetState();
}

class _RoomVotingAreaWidgetState extends State<RoomVotingAreaWidget> {
  bool _isLoading = false;

  Future<void> onSubmitVote(String vote) async {
    setState(() { _isLoading = true; });
    await widget.submitVote(vote);
    setState(() { _isLoading = false; });
  }

  Future<void> onRevealVotes() async {
    setState(() { _isLoading = true; });
    await widget.revealVotes();
    setState(() { _isLoading = false; });
  }

  Future<void> onClearVotes() async {
    setState(() { _isLoading = true; });
    await widget.clearVotes();
    setState(() { _isLoading = false; });
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
        children: [
          ListView(
            children: [
              VoteCardsWidget(
                onClickVote: onSubmitVote,
                onClickReveal: onRevealVotes,
                onClickClear: onClearVotes,
                voteSelected: widget.participant.vote,
                votesRevealed: widget.room.votesRevealed,
              ),
              if (widget.room.votesRevealed)
                VoteResultsWidget(
                  participantResults: widget.room.participants,
                  userName: widget.participant.name
                )
              else
                VotePendingWidget(
                  participantsPendingResults: widget.room.participants,
                  userName: widget.participant.name
                )
            ],
          ),
          if (_isLoading)
            const LinearProgressIndicator(semanticsLabel: 'Loading indicator'),
        ],
    );
  }
}
