import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_pdf_printer/flutter_pdf_printer.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  test('checkNullability', () {
    expect(() => FlutterPdfPrinter.printFile(null), throwsException);
  });

  test('checkNullability', () {
    expect(() => FlutterPdfPrinter.printFile(""), throwsException);
  });
}
