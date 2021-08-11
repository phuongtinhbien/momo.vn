package com.minh.momo_vn

import android.app.Activity
import android.content.Intent
import android.util.Log
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener
import vn.momo.momo_partner.AppMoMoLib


@Suppress("DEPRECATION")
class MomoVnPluginDelegate(private var activity: Activity) : ActivityResultListener{

    private var pendingResult: Result? = null
    private var pendingReply: Map<String, Any>? = null
    private var activityBinding: ActivityPluginBinding? = null

    fun openCheckout(momoRequestPaymentData: Any, result: Result) {
        this.pendingResult = result;
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT)
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN)

        val paymentInfo: HashMap<String, Any> = momoRequestPaymentData as HashMap<String, Any>
        val isTestMode: Boolean? = paymentInfo["isTestMode"] as Boolean?

        if (isTestMode == null || !isTestMode) {
            AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.PRODUCTION)
        } else {
            AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT)
        }

        AppMoMoLib.getInstance().requestMoMoCallBack(activity, paymentInfo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO) {
                _handleResult(data)
            }
        } else {

            Log.d("OAR-resultCode", resultCode.toString())
            Log.d("OAR-requestCode", requestCode.toString())
            Log.d("OAR-data", data.toString())
            Log.d("OAR-activity", activity.toString())
            val data: MutableMap<String, Any> = java.util.HashMap()
            data["error"] = "User canceled"
            sendReply(data)
        }

        return true
    }

    private fun sendReply(data: Map<String, Any>) {
        if (this.pendingResult != null) {
            this.pendingResult?.success(data)
            pendingReply = null
        } else {
            pendingReply = data
        }
    }

    private fun _handleResult(data: Intent?) {
        data?.let {
            val status = data.getIntExtra("status", -1)
            var isSuccess: Boolean = false
            if (status == MomoVnConfig.CODE_PAYMENT_SUCCESS) isSuccess = true
            val token = data.getStringExtra("data")
            val phonenumber = data.getStringExtra("phonenumber")
            val message = data.getStringExtra("message")
            var extra = data.getStringExtra("extra")
            if (extra == null) {
                extra = "";
            }
            val data: MutableMap<String, Any> = java.util.HashMap()
            data["isSuccess"] = isSuccess
            data["status"] = status
            data["phoneNumber"] = phonenumber.toString()
            data["token"] = token.toString()
            data["message"] = message.toString()
            data["extra"] = extra.toString()
            sendReply(data)
        } ?: run {
            val data: MutableMap<String, Any> = java.util.HashMap()
            data["isSuccess"] = false
            data["status"] = 7;
            data["phoneNumber"] = ""
            data["token"] = ""
            data["message"] = ""
            data["extra"] = ""
            sendReply(data)
        }
    }



}