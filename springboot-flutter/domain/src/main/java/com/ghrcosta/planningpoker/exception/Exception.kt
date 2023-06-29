package com.ghrcosta.planningpoker.exception

class RoomNotFoundException(roomId: String):
    RuntimeException("Room '${roomId}' not found")

class DuplicatedParticipantNameException(roomId: String, participantName: String):
    RuntimeException("Room '${roomId}' already has a participant named '${participantName}'")

class ParticipantNotFoundException(roomId: String, participantName: String):
    RuntimeException("Room '${roomId}' doesn't have a participant named '${participantName}'")