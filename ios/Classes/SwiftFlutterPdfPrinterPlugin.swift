import Flutter
import UIKit


public class SwiftFlutterPdfPrinterPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_pdf_printer", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterPdfPrinterPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    let args = call.arguments! as! [String: Any]
    if (call.method == "printFile"){
        let filePath = args["file"] as? String ?? ""
        let guide_url = URL(fileURLWithPath: filePath)
        if UIPrintInteractionController.canPrint(guide_url) {
            let printInfo = UIPrintInfo(dictionary: nil)
            printInfo.jobName = guide_url.lastPathComponent
            printInfo.outputType = .photo
            
            let printController = UIPrintInteractionController.shared
            printController.printInfo = printInfo
            printController.showsNumberOfCopies = false
            
            printController.printingItem = guide_url
            
            printController.present(animated: true, completionHandler: nil)
        }
    }
  }
}
