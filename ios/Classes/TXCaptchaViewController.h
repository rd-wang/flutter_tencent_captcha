#import <UIKit/UIKit.h>
#import "TXCaptchaWebView.h"

NS_ASSUME_NONNULL_BEGIN

typedef void (^TXCaptchaCallback)(NSDictionary *data);

@interface TXCaptchaViewController : UIViewController
- (instancetype)initWithConfig:(NSString*)config captchaHtmlPath:(NSString*) path;

@property (nonatomic, copy) TXCaptchaCallback onLoaded;
@property (nonatomic, copy) TXCaptchaCallback onSuccess;
@property (nonatomic, copy) TXCaptchaCallback onFail;

@end

NS_ASSUME_NONNULL_END
