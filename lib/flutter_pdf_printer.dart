import 'dart:async';

import 'package:flutter/services.dart';

class FlutterPdfPrinter {
  static const MethodChannel _channel =
      const MethodChannel('flutter_pdf_printer');

  static Future<void> printFile(String file) async {
    await _channel.invokeMethod("printFile", {"file": file});
  }

}
