# flutter_tencent_captcha

flutter 插件
腾讯验证码服务 的flutter 插件

### how to use

##💻 添加依赖 在 pubspec.yaml中添加:

    flutter_tencent_captcha: ^0.0.1

## 然后使用即可.

    '''
    void _handleClickVerify() async {
        TencentCaptchaConfig config = TencentCaptchaConfig(
          appId: "your appid",
          bizState: 'roobo-tencent-captcha',
          enableDarkMode: true,
          needFeedBack: false,
        );
        await TencentCaptcha.verify(
          config: config,
          onLoaded: (dynamic data) {
            your code
          },
          onSuccess: (dynamic data) {
            your code
          },
          onFail: (dynamic data) {
            your code
          },
        );
      }

    '''

## 效果图