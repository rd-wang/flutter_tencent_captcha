package com.roobo.flutter.flutter_tencent_captcha;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterTencentCaptchaPlugin
 */
public class FlutterTencentCaptchaPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, EventChannel.StreamHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private String captchaHtmlPath;
    private Activity activity;
    private String appId;
    private EventChannel eventChannel;
    private EventChannel.EventSink eventSink;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.captchaHtmlPath = flutterPluginBinding.getFlutterAssets().getAssetFilePathBySubpath("assets/captcha.html", "flutter_tencent_captcha");
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_tencent_captcha");
        channel.setMethodCallHandler(this);

        this.eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_tencent_captcha/event_channel");
        this.eventChannel.setStreamHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getSDKVersion")) {
            handleMethodGetSDKVersion(call, result);
        } else if (call.method.equals("init")) {
            handleMethodInit(call, result);
        } else if (call.method.equals("verify")) {
            handleMethodVerify(call, result);
        } else {
            result.notImplemented();
        }
    }


    private void handleMethodGetSDKVersion(@NonNull MethodCall call, @NonNull Result result) {
        result.success("0.0.1");
    }

    private void handleMethodInit(@NonNull MethodCall call, @NonNull Result result) {
        this.appId = call.argument("appId");
        result.success(true);
    }

    private void handleMethodVerify(@NonNull MethodCall call, @NonNull final Result result) {
        String configJsonString = "{}";

        if (call.hasArgument("config"))
            configJsonString = call.argument("config");

        TencentCaptchaSender.getInstance().listener(new TencentCaptchaListener() {
            @Override
            public void onLoaded(String data) {
                final Map<String, Object> result = new HashMap<>();
                result.put("method", "onLoaded");
                result.put("data", convertMsgToMap(data));

                eventSink.success(result);
            }

            @Override
            public void onSuccess(String data) {
                final Map<String, Object> result = new HashMap<>();
                result.put("method", "onSuccess");
                result.put("data", convertMsgToMap(data));

                eventSink.success(result);
            }

            @Override
            public void onFail(String msg) {
                final Map<String, Object> result = new HashMap<>();
                result.put("method", "onFail");
                result.put("data", convertMsgToMap(msg));

                eventSink.success(result);
            }
        });

        Intent intent = new Intent(activity, TencentCaptchaActivity.class);
        intent.putExtra("captchaHtmlPath", this.captchaHtmlPath);
        intent.putExtra("configJsonString", configJsonString);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        result.success(true);
    }


    private static Map<String, Object> convertMsgToMap(String jsonString) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            map = toMap(jsonObject);
        } catch (JSONException ex) {
            // skip;
        }
        return map;
    }

    private static Map<String, Object> toMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        this.channel.setMethodCallHandler(null);
        this.channel = null;

        this.eventChannel.setStreamHandler(null);
        this.eventChannel = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {

    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        this.eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        this.eventSink = null;
    }
}
