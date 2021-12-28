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
        let b64Bytes = args["bytes"] as? String ?? ""
        let pdfFile = Data(base64Encoded: b64Bytes, options: .ignoreUnknownCharacters)!
        let printInfo = UIPrintInfo(dictionary: nil)
        printInfo.jobName = "Print Form"
        printInfo.outputType = .general

        let printController = UIPrintInteractionController.shared
        printController.printInfo = printInfo
        printController.showsNumberOfCopies = false

        printController.printingItem = pdfFile

        printController.present(animated: true, completionHandler: nil)
    }
  }
}
