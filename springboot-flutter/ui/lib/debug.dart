import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/foundation.dart';

// Concentrating all code that checks for debug mode in a single file

String getBaseUrl() {
  return kDebugMode ? "http://localhost:8080" : "";
}

void doFirestoreExtraInitialization() {
  if (kDebugMode) {
    FirebaseFirestore.instance.useFirestoreEmulator('localhost', 8081);
  }
}