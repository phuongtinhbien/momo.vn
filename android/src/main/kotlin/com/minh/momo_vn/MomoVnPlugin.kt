package com.minh.momo_vn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.NewIntentListener
import vn.momo.momo_partner.AppMoMoLib


class MomoVnPlugin : FlutterPlugin, ActivityAware, MethodCallHandler,
        NewIntentListener, PluginRegistry.ActivityResultListener {
    private lateinit var momoVnPluginDelegate: MomoVnPluginDelegate

    private var activity: Activity? = null
    private var context: Context? = null
    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        // Register a method channel that the Flutter app may invoke
        val channel = MethodChannel(binding.binaryMessenger, MomoVnConfig.CHANNEL_NAME)
        // Handle method calls (onMethodCall())
        channel.setMethodCallHandler(this)
        context = binding.applicationContext
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        // Listen for new intents (notification clicked)
        momoVnPluginDelegate = MomoVnPluginDelegate(activity!!)
        binding.addOnNewIntentListener(this)
    }


    override fun onNewIntent(intent: Intent): Boolean {
        return momoVnPluginDelegate.onActivityResult(AppMoMoLib.getInstance().REQUEST_CODE_MOMO, Activity.RESULT_OK, intent)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            MomoVnConfig.METHOD_REQUEST_PAYMENT -> {
                this.momoVnPluginDelegate.openCheckout(call.arguments, result)
            }
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {}
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}
    override fun onDetachedFromActivity() {}
    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return momoVnPluginDelegate.onActivityResult(requestCode, resultCode, data)
    }
}