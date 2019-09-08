import 'dart:async';

import 'package:flutter/services.dart';

class FlutterPdfPrinter {
  static const MethodChannel _channel =
      const MethodChannel('flutter_pdf_printer');

  /// prints the file located at [filePath]
  /// throws FormatException if the path is null or empty
  static Future<void> printFile(String filePath) async {
    if (filePath == null || filePath.isEmpty) {
      throw FormatException("filePath given is null or empty");
    }
    await _channel.invokeMethod("printFile", {"file": filePath});
  }
}
