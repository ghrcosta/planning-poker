import 'package:flutter/material.dart';
import 'package:ui/screen/base.dart';

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
        return Center(
          child: Column(
            children: [
              const SizedBox(height: 100),
              SizedBox(
                width: 220,
                child: TextField(
                  controller: _textController,
                  decoration: InputDecoration(
                    border: baseInputBorder,
                    enabledBorder: baseInputBorder.copyWith(borderSide: BorderSide(
                      color: Theme.of(context).colorScheme.onSurface,
                    )),
                    focusedBorder: baseInputBorder.copyWith(borderSide: BorderSide(
                      color: Theme.of(context).colorScheme.surfaceTint,
                    )),
                    labelText: 'Your name',
                    errorText: _submitted ? _errorText : null,
                  ),
                ),
              ),
              const SizedBox(height: 10),
              baseButton(context, 'Submit', _errorText == null ? _submit : null),
            ],
          ),
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