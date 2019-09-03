#import "FlutterPdfPrinterPlugin.h"
#import <flutter_pdf_printer/flutter_pdf_printer-Swift.h>

@implementation FlutterPdfPrinterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterPdfPrinterPlugin registerWithRegistrar:registrar];
}
@end
