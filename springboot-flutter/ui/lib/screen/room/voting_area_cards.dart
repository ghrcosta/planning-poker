import 'package:flutter/material.dart';

import 'package:ui/screen/base.dart';

class VoteCardsWidget extends StatefulWidget {
  final Function(String) onClickVote;
  final Function() onClickReveal;
  final Function() onClickClear;
  final String? voteSelected;
  final bool votesRevealed;

  const VoteCardsWidget({
    required this.onClickVote,
    required this.onClickReveal,
    required this.onClickClear,
    required this.voteSelected,
    required this.votesRevealed,
    super.key
  });

  @override
  State<VoteCardsWidget> createState() => _VoteCardsWidgetState();
}

class _VoteCardsWidgetState extends State<VoteCardsWidget> {
  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 20),
      alignment: Alignment.topCenter,
      child: _voteWidget(context),
    );
  }

  Widget _voteWidget(BuildContext context) {
    return SizedBox(
      width: 500,
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              _voteOptionCard("0"),
              _voteOptionCard("½"),
              _voteOptionCard("1"),
              _voteOptionCard("2"),
              _voteOptionCard("3"),
              _voteOptionCard("5"),
              _voteOptionCard("8"),
              _voteOptionCard("13"),
              _voteOptionCard("?"),
            ],
          ),
          const SizedBox(height: 20),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              baseButton(context, "Reveal", widget.votesRevealed ? null : widget.onClickReveal),
              baseButton(context, "Clear", widget.onClickClear),
            ],
          ),
        ],
      ),
    );
  }

  Widget _voteOptionCard(String text) {
    final backgrounColor =
      (text == widget.voteSelected)
        ? Theme.of(context).colorScheme.surfaceVariant
        : Theme.of(context).colorScheme.surface;
    
    final textColor =
      (text == widget.voteSelected)
        ? Theme.of(context).colorScheme.onSurfaceVariant
        : Theme.of(context).colorScheme.onSurface;

    return Card(
      color: backgrounColor,
      shape: baseRectangleBorder,
      child: InkWell(
        onTap: () {
          widget.onClickVote(text);
        },
        customBorder: baseRectangleBorder,
        child: SizedBox(
          height: 70,
          width: 40,
          child: Center(
            child: Text(
              text,
              style: Theme.of(context).textTheme.headlineMedium?.copyWith(color: textColor),
            ),
          ),
        ),
      ),
    );
  }
}