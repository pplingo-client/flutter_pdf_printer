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
import io.flutter.embedding.engine.plugins.FlutterPlugin
import java.io.OutputStream
import androidx.annotation.NonNull
import android.util.Log
import android.app.Activity
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware

@TargetApi(Build.VERSION_CODES.KITKAT)
class FlutterPdfPrinterPlugin(): FlutterPlugin,ActivityAware, MethodCallHandler, PrintDocumentAdapter() {
  private var b64Bytes: String? = null
  private lateinit var mgr: PrintManager
  private  var activity:Activity? = null

  override fun onDetachedFromActivity() {
    this.activity = null
  }
  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }
  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    mgr = binding.activity.getSystemService(PRINT_SERVICE) as PrintManager;
    this.activity = binding.activity

  }
  override fun onDetachedFromActivityForConfigChanges() {}

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    var channel = MethodChannel(flutterPluginBinding.flutterEngine.dartExecutor, "flutter_pdf_printer")
    channel.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {}

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
