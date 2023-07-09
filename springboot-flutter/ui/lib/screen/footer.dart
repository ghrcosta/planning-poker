import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class FooterWidget extends StatelessWidget {
  const FooterWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(4),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          InkWell(
            onTap: _launchGitHubLink,
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Image.asset('assets/images/icons8-github-16.png'),
                const SizedBox(width: 2),
                Text(
                  'Source code',
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
              ],
            ),
          ),
          const SizedBox(height: 2),
          RichText(
            text: TextSpan(
              children: [
                TextSpan(
                  text: 'GitHub',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(decoration: TextDecoration.underline),
                  recognizer: TapGestureRecognizer()..onTap = _launchGitHubIcon8Link
                ),
                TextSpan(
                  text: ', ',
                  style: Theme.of(context).textTheme.bodySmall
                ),
                TextSpan(
                  text: 'Red Card',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(decoration: TextDecoration.underline),
                  recognizer: TapGestureRecognizer()..onTap = _launchFlashcardsIcon8Link
                ),
                TextSpan(
                  text: ' icons by ',
                  style: Theme.of(context).textTheme.bodySmall
                ),
                TextSpan(
                  text: 'Icons8',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(decoration: TextDecoration.underline),
                  recognizer: TapGestureRecognizer()..onTap = _launchIcons8Link
                ),
              ]
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _launchGitHubLink() async {
    final url = Uri.parse('https://github.com/ghrcosta/planning-poker');
    await launchUrl(url);
  }

  Future<void> _launchGitHubIcon8Link() async {
    final url = Uri.parse('https://icons8.com/icon/AZOZNnY73haj/github');
    await launchUrl(url);
  }

  Future<void> _launchFlashcardsIcon8Link() async {
    final url = Uri.parse('https://icons8.com/icon/53676/red-card');
    await launchUrl(url);
  }

  Future<void> _launchIcons8Link() async {
    final url = Uri.parse('https://icons8.com/');
    await launchUrl(url);
  }
}