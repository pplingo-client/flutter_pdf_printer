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
import java.io.IOException
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.File
import java.io.OutputStream
import java.io.InputStream

@TargetApi(Build.VERSION_CODES.KITKAT)
class FlutterPdfPrinterPlugin(private val mgr: PrintManager): MethodCallHandler, PrintDocumentAdapter() {
  private var filePath: String? = null

  override fun onWrite(pageRanges: Array<PageRange>, parcelFileDescriptor: ParcelFileDescriptor, cancellationSignal: CancellationSignal, writeResultCallback: PrintDocumentAdapter.WriteResultCallback) {
    var `in`: InputStream? = null
    var out: OutputStream? = null
    try {
      val file = File(filePath)
      `in` = FileInputStream(file)
      out = FileOutputStream(parcelFileDescriptor.fileDescriptor)

      val buf = ByteArray(16384)
      var size: Int

      do {
        size = `in`.read(buf)
        if ( size <= 0 || cancellationSignal.isCanceled) {
          break
        }
        out.write(buf, 0, size)
      } while(true)

      if (cancellationSignal.isCanceled) {
        writeResultCallback.onWriteCancelled()
      } else {
        writeResultCallback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
      }
    } catch (e: Exception) {
      writeResultCallback.onWriteFailed(e.message)
    } finally {
      try {
        `in`!!.close()
        out!!.close()
      } catch (e: IOException) {
      }

    }
  }

  override fun onLayout(printAttributes: PrintAttributes, printAttributes1: PrintAttributes, cancellationSignal: CancellationSignal, layoutResultCallback: PrintDocumentAdapter.LayoutResultCallback, bundle: Bundle) {
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
      filePath = call.argument<String>("file")
      mgr.print("PrintFile", this, PrintAttributes.Builder().build())
      result.success(true)
    } else {
      result.notImplemented()
    }
  }
}
