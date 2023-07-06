import 'package:flutter/material.dart';

// Based on https://codewithandrea.com/articles/flutter-text-field-form-validation/

class RoomNameSelectionWidget extends StatefulWidget {
  final ValueChanged<String> onSubmit;

  const RoomNameSelectionWidget({
    required this.onSubmit,
    super.key
  });

  @override
  State<RoomNameSelectionWidget> createState() => _RoomNameSelectionWidgetState();
}

class _RoomNameSelectionWidgetState extends State<RoomNameSelectionWidget> {
  final _textController = TextEditingController();
  bool _submitted = false;

  @override
  void dispose() {
    // To prevent issues, it's necessary to manually call dispose() on the controller
    // See https://stackoverflow.com/a/74878578
    _textController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder(
      valueListenable: _textController,
      builder: (context, TextEditingValue value, __) {
        return Column(
          children: [
            SizedBox(
              height: 80,
              child: TextField(
                controller: _textController,
                decoration: InputDecoration(
                  labelText: 'Your name',
                  border: const OutlineInputBorder(),
                  errorText: _submitted ? _errorText : null,
                ),
              ),
            ),
            SizedBox(
              height: 40,
              child: ElevatedButton(
                onPressed: _textController.value.text.trim().isNotEmpty ? _submit : null,
                child: Text(
                  'Submit',
                  style: Theme.of(context).textTheme.headlineSmall,
                ),
              ),
            )
          ],
        );
      },
    );
  }

  String? get _errorText {
    final text = _textController.value.text.trim();
    if (text.isEmpty) {
      return 'Can\'t be empty';
    }
    if (text.length <= 2) {
      return 'Too short';
    }
    return null;
  }

  void _submit() {
    setState(() => _submitted = true);
    if (_errorText == null) {
      widget.onSubmit(_textController.value.text.trim());
    }
  }
}