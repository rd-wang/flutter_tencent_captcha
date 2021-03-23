import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

const _kMethodChannelName = 'flutter_tencent_captcha';
const _kEventChannelName = 'flutter_tencent_captcha/event_channel';

class TencentCaptcha {
  static const MethodChannel _methodChannel = const MethodChannel(_kMethodChannelName);
  static const EventChannel _eventChannel = const EventChannel(_kEventChannelName);

  static bool _eventChannelReadied = false;

  static Future<String?> get sdkVersion async {
    final String? sdkVersion = await _methodChannel.invokeMethod('getSDKVersion');
    return sdkVersion;
  }

  static Future<bool> init() async {
    if (_eventChannelReadied != true) {
      _eventChannel.receiveBroadcastStream().listen(_handleVerifyOnEvent);
      _eventChannelReadied = true;
    }
    return true;
  }

  static _handleVerifyOnEvent(dynamic event) {
    String method = '${event['method']}';
    dynamic data = event['data'];

    switch (method) {
      case 'onLoaded':
        if (_verifyOnLoaded != null) _verifyOnLoaded!(data);
        break;
      case 'onSuccess':
        if (_verifyOnSuccess != null) _verifyOnSuccess!(data);
        break;
      case 'onFail':
        if (_verifyOnFail != null) _verifyOnFail!(data);
        break;
    }
  }

  static Function(dynamic)? _verifyOnLoaded;
  static Function(dynamic)? _verifyOnSuccess;
  static Function(dynamic)? _verifyOnFail;

  static Future<bool?> verify({
    required TencentCaptchaConfig config,
    Function(dynamic data)? onLoaded,
    Function(dynamic data)? onSuccess,
    Function(dynamic data)? onFail,
  }) async {
    _verifyOnLoaded = onLoaded;
    _verifyOnSuccess = onSuccess;
    _verifyOnFail = onFail;

    return await _methodChannel.invokeMethod('verify', {
      'config': json.encode(config.toJson()),
    });
  }
}

class TencentCaptchaConfig {
  String appId;

  // 自定义透传参数，业务可用该字段传递少量数据，该字段的内容会被带入callback回调的对象中
  dynamic bizState;

  // 开启自适应深夜模式
  bool enableDarkMode;

  // 示例 {"width": 140, "height": 140}
  // 移动端原生webview调用时传入，为设置的验证码弹框大小。
  Map<String, dynamic>? sdkOpts;

  // 隐藏帮助按钮。
  // 示例 { needFeedBack: false }
  bool needFeedBack;

  TencentCaptchaConfig({
    required this.appId,
    this.bizState,
    this.enableDarkMode = false,
    this.sdkOpts,
    this.needFeedBack = false,
  });

  Map<String, dynamic> toJson() {
    Map<String, dynamic> jsonObject = Map<String, dynamic>();
    jsonObject.putIfAbsent("appId", () => appId);
    if (bizState != null) jsonObject.putIfAbsent("bizState", () => bizState);
    jsonObject.putIfAbsent("enableDarkMode", () => enableDarkMode);
    if (sdkOpts != null) jsonObject.putIfAbsent("sdkOpts", () => sdkOpts);
    jsonObject.putIfAbsent("needFeedBack", () => needFeedBack);

    return jsonObject;
  }
}
