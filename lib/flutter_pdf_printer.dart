import 'dart:async';
import 'dart:io';
import 'dart:convert';

import 'package:flutter/services.dart';

class FlutterPdfPrinter {
  static const MethodChannel _channel =
      const MethodChannel('flutter_pdf_printer');

  /// prints the file located at [filePath]
  /// throws FormatException if the path is null or empty
  static Future<void> printFile(String filePath) async {
    if (filePath.isEmpty) {
      throw FormatException("filePath given is null or empty");
    }
    var file = File(filePath);
    var bytes = await file.readAsBytes();
    var b64Bytes = base64Encode(bytes);
    await printFileFromBytes(b64Bytes);
  }

  static Future<void> printFileFromBytes(String b64Bytes) async {
    if (Platform.isAndroid || Platform.isIOS) {
      await _channel.invokeMethod("printFile", {"bytes": b64Bytes});
    }
  }
}
