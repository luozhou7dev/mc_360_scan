import 'dart:async';

import 'package:flutter/services.dart';

class Mc360Scan {
  factory Mc360Scan() {
    if (_instance == null) {
      final MethodChannel methodChannel =
          const MethodChannel('mc_360_scan/method_channel');
      final EventChannel eventChannel =
          const EventChannel('mc_360_scan/event_channel');
      _instance = Mc360Scan.private(methodChannel, eventChannel);
    }
    return _instance;
  }

  Mc360Scan.private(this._methodChannel, this._eventChannel);

  static Mc360Scan _instance;

  final MethodChannel _methodChannel;
  final EventChannel _eventChannel;
  Stream<String> _onScanCompleted;

  Future<String> get platformVersion async {
    final String version =
        await _methodChannel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<void> startScanning() async {
    await _methodChannel.invokeMethod('startScanning');
  }

  Future<void> stopScanning() async {
    await _methodChannel.invokeMethod('stopScanning');
  }

  Stream<String> get onScanCompleted {
    if (_onScanCompleted == null) {
      _onScanCompleted =
          _eventChannel.receiveBroadcastStream().map((dynamic event) => event);
    }
    return _onScanCompleted;
  }
}
