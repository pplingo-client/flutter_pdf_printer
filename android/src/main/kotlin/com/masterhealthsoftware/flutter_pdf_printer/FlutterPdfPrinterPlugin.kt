package com.masterhealthsoftware.flutter_pdf_printer

import android.annotation.TargetApi
import android.content.Context.PRINT_SERVICE
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.print.PrintDocumentInfo
import android.util.Base64
import java.io.IOException
import java.io.FileOutputStream
import java.io.OutputStream

@TargetApi(Build.VERSION_CODES.KITKAT)
class FlutterPdfPrinterPlugin(private val mgr: PrintManager): MethodCallHandler, PrintDocumentAdapter() {
  private var b64Bytes: String? = null

  override fun onWrite(pageRanges: Array<PageRange>, parcelFileDescriptor: ParcelFileDescriptor, cancellationSignal: CancellationSignal, writeResultCallback: WriteResultCallback) {
    var out: OutputStream? = null
    try {
      val bytes = Base64.decode(b64Bytes, Base64.DEFAULT)
      out = FileOutputStream(parcelFileDescriptor.fileDescriptor)

      out.write(bytes)
      if (cancellationSignal.isCanceled) {
        writeResultCallback.onWriteCancelled()
      } else {
        writeResultCallback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
      }
    } catch (e: Exception) {
      writeResultCallback.onWriteFailed(e.message)
    } finally {
      try {
        out!!.close()
      } catch (e: IOException) {
      }

    }
  }

  override fun onLayout(printAttributes: PrintAttributes, printAttributes1: PrintAttributes, cancellationSignal: CancellationSignal, layoutResultCallback: LayoutResultCallback, bundle: Bundle) {
    if (cancellationSignal.isCanceled) {
      layoutResultCallback.onLayoutCancelled()
    } else {
      val builder = PrintDocumentInfo.Builder("temp.pdf")
      builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
              .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
              .build()
      layoutResultCallback.onLayoutFinished(builder.build(),
              printAttributes1 != printAttributes)
    }
  }

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "flutter_pdf_printer")
      channel.setMethodCallHandler(FlutterPdfPrinterPlugin(registrar.activeContext().getSystemService(PRINT_SERVICE) as PrintManager))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "printFile") {
      b64Bytes = call.argument<String>("bytes")
      mgr.print("PrintFile", this, PrintAttributes.Builder().build())
      result.success(true)
    } else {
      result.notImplemented()
    }
  }
}
