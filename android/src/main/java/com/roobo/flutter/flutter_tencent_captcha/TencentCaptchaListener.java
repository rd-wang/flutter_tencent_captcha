package com.roobo.flutter.flutter_tencent_captcha;

public interface TencentCaptchaListener {
    void onLoaded(String data);

    void onSuccess(String data);

    void onFail(String data);
}
