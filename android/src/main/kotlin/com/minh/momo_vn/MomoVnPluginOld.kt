@file:Suppress("DEPRECATION")

package com.minh.momo_vn

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class MomoVnPluginOld(private var registrar: Registrar) : MethodCallHandler {
    private val momoVnPluginDelegate = MomoVnPluginDelegateOld(registrar)
    init {
        registrar.addActivityResultListener(momoVnPluginDelegate)
    }
    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val momoPaymentPlugin = MomoVnPluginOld(registrar)
            val channel = MethodChannel(registrar.messenger(), MomoVnConfig.CHANNEL_NAME)
            channel.setMethodCallHandler(momoPaymentPlugin)
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            MomoVnConfig.METHOD_REQUEST_PAYMENT -> {
                this.momoVnPluginDelegate.openCheckout(call.arguments, result)
            }
            else -> result.notImplemented()
        }
    }


}