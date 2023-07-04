import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:ui/entity/room.dart';
import 'package:ui/entity/session.dart';


const _baseUrl = kDebugMode ? "http://localhost:8080" : "";

Future<Room> createRoom() async {
  final response = await http.post(
    Uri.parse('$_baseUrl/create')
  );

  if (response.statusCode == 201) {
    return Room.fromJson(jsonDecode(response.body));
  } else {
    return Future.error('Failed to create room');
  }
}

Future<Session> addParticipant(String roomId, String name) async {
  final response = await http.put(
    Uri.parse('$_baseUrl/$roomId/addParticipant').replace(queryParameters: { 'name': name })
  );

  if (response.statusCode == 201) {
    return Session.fromJson(jsonDecode(response.body));
  } else if (response.body.isNotEmpty) {
    return Future.error('Failed to add participant: ${response.body} (${response.statusCode})');
  } else {
    return Future.error('Failed to add participant: ${response.statusCode}');
  }
}

Future<void> setVote(String vote) async {
  final session = await Session.getCurrentSessionIfItExists();
  if (session == null) {
    return Future.error("Session not found");
  }

  final response = await http.put(
    Uri.parse('$_baseUrl/${session.roomId}/vote').replace(queryParameters: { 'value': vote }),
    headers: <String, String>{
      'Content-Type': 'application/json; charset=UTF-8',
    },
    body: jsonEncode(session)
  );

  if (response.statusCode == 204) {
    return;
  } else if (response.body.isNotEmpty) {
    return Future.error('Failed to add participant: ${response.body} (${response.statusCode})');
  } else {
    return Future.error('Failed to add participant: ${response.statusCode}');
  }
}

Future<void> revealVotes() async {
  final session = await Session.getCurrentSessionIfItExists();
  if (session == null) {
    return Future.error("Session not found");
  }

  final response = await http.post(
    Uri.parse('$_baseUrl/${session.roomId}/revealVotes'),
    headers: <String, String>{
      'Content-Type': 'application/json; charset=UTF-8',
    },
    body: jsonEncode(session)
  );

  if (response.statusCode == 204) {
    return;
  } else if (response.body.isNotEmpty) {
    return Future.error('Failed to add participant: ${response.body} (${response.statusCode})');
  } else {
    return Future.error('Failed to add participant: ${response.statusCode}');
  }
}

Future<void> clearVotes() async {
  final session = await Session.getCurrentSessionIfItExists();
  if (session == null) {
    return Future.error("Session not found");
  }

  final response = await http.post(
    Uri.parse('$_baseUrl/${session.roomId}/clearVotes'),
    headers: <String, String>{
      'Content-Type': 'application/json; charset=UTF-8',
    },
    body: jsonEncode(session)
  );

  if (response.statusCode == 204) {
    return;
  } else if (response.body.isNotEmpty) {
    return Future.error('Failed to add participant: ${response.body} (${response.statusCode})');
  } else {
    return Future.error('Failed to add participant: ${response.statusCode}');
  }
}