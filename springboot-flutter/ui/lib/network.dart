import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:ui/entity/room.dart';
import 'package:ui/entity/session.dart';


const _baseUrl = "http://localhost:8080";

Future<Room> createRoom() async {
  final response = await http.post(
    Uri.parse('$_baseUrl/create')
  );

  if (response.statusCode == 201) {
    return Room.fromJson(jsonDecode(response.body));
  } else {
    throw Exception('Failed to create room');
  }
}

Future<Session> addParticipant(String roomId, String name) async {
  final response = await http.put(
    Uri.parse('$_baseUrl/$roomId/addParticipant').replace(queryParameters: { 'name': name })
  );

  if (response.statusCode == 201) {
    return Session.fromJson(jsonDecode(response.body));
  } else if (response.statusCode == 400) {
    throw Exception('Failed to add participant: missing name');
  } else if (response.statusCode == 404) {
    throw Exception('Failed to add participant: invalid room ID');
  } else if (response.statusCode == 409) {
    throw Exception('Failed to add participant: There\'s already a participant with the same name in the room');
  } else {
    throw Exception('Failed to add participant');
  }
}