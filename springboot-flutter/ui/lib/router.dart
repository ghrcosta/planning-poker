import 'package:flutter/material.dart';

import 'screen/home/home.dart';
import 'screen/room/room.dart';

// Based on https://medium.com/flutter/learning-flutters-new-navigation-and-routing-system-7c9068155ade

class AppRoutePath {
  final String? roomId;
  final bool isUnknown;

  bool get isRoom => roomId != null && !isUnknown;

  AppRoutePath.home() : roomId = null, isUnknown = false;

  AppRoutePath.room(this.roomId) : isUnknown = false;
}

/// Converts between a URI string and an object that defines the route
class AppRouteInformationParser extends RouteInformationParser<AppRoutePath> {

  /// Convert the current URI into an object that defines the route path
  @override
  Future<AppRoutePath> parseRouteInformation(RouteInformation routeInformation) async {
    final rawLocation = routeInformation.location;
    final String location = (rawLocation != null) ? rawLocation : '/';
    final uri = Uri.parse(location);

    // Handle "/{roomId}"
    if (uri.pathSegments.length == 1) {
      var roomId = uri.pathSegments[0];
      return AppRoutePath.room(roomId);
    }

    // Anything else
    return AppRoutePath.home();
  }

  /// Converts a route path object into an URI
  @override
  RouteInformation restoreRouteInformation(AppRoutePath configuration) {
    if (configuration.isRoom) {
      return RouteInformation(location: '/${configuration.roomId}');
    }
    return const RouteInformation(location: '/');
  }
}

/// Navigate between screens based on the route path object
class AppRouterDelegate extends RouterDelegate<AppRoutePath>
    with ChangeNotifier, PopNavigatorRouterDelegateMixin<AppRoutePath> {
  
  String? _roomId;

  @override
  final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

  @override
  AppRoutePath get currentConfiguration =>
      (_roomId == null)
          ? AppRoutePath.home()
          : AppRoutePath.room(_roomId);

  @override
  Widget build(BuildContext context) {
    // In order for Dart to automatically handle a `String?` variable as `String` after an `if` already checked that
    // it's not null, it must be a local variable. The only other option is to add a '!' (e.g. _roomId!).
    // See: https://dart.dev/tools/non-promotion-reasons#property-or-this
    final roomId = _roomId;

    return Navigator(
      key: navigatorKey,
      pages: [
        // `pages` is a collection and `if`s inside a collection can only have a single expression, without brackets.
        // By adding brackets, Dart will interpret them as creating a Set. See "collection if".
        if (roomId == null)
          MaterialPage(
            key: const ValueKey('home'),
            child: HomeScreen(navigateToRoom: updateRoomId),
          )
        else
          MaterialPage(
            key: ValueKey('room/$roomId'),
            child: RoomScreen(roomId: roomId),
          )
      ],
      onPopPage: (route, result) {  // Handle return to previous page (by clicking the browser's back button)
        if (!route.didPop(result)) {
          return false;
        }
        _roomId = null;
        notifyListeners();
        return true;
      },
    );
  }

  @override
  Future<void> setNewRoutePath(AppRoutePath configuration) async {
    _roomId = configuration.roomId;
  }

  void updateRoomId(String? roomId) {
    _roomId = roomId;
    notifyListeners();
  }
}