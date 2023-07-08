import 'package:flutter/material.dart';

final baseRectangleBorder = RoundedRectangleBorder(borderRadius: BorderRadius.circular(10));
final baseInputBorder = OutlineInputBorder(borderRadius: BorderRadius.circular(10));

Widget baseButton(BuildContext context, String text, Function()? onPressed, {double width = 140}) {
  return SizedBox(
    width: width,
    height: 40,
    child: ElevatedButton(
      style: ButtonStyle(
        shape: MaterialStateProperty.all(baseRectangleBorder),
        backgroundColor: MaterialStateProperty.resolveWith<Color>(
          (Set<MaterialState> states) {
            if (states.contains(MaterialState.disabled)) {
              return Theme.of(context).colorScheme.inversePrimary.withOpacity(0.4);
            } else {
              return Theme.of(context).colorScheme.inversePrimary;
            }
          }
        ),
      ),
      onPressed: onPressed,
      child: Text(
        text,
        style: Theme.of(context).textTheme.titleLarge
          ?.copyWith(color: (onPressed == null)
            ? Theme.of(context).colorScheme.onSurface.withOpacity(0.2)
            : Theme.of(context).colorScheme.onSurface),
      ),
    ),
  );
}