package com.rubygames.assass

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerLib
import com.onesignal.OneSignal
import com.orhanobut.hawk.Hawk
import com.rubygames.assass.MainClass.Companion.C1
import com.rubygames.assass.MainClass.Companion.DEEPL
import com.rubygames.assass.MainClass.Companion.MAIN_ID
import com.rubygames.assass.MainClass.Companion.link
import com.rubygames.assass.databinding.ActivityWisBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

class WIS : AppCompatActivity() {

    private val first = 1

    var vcAU: ValueCallback<Array<Uri>>? = null
    var str: String? = null

    lateinit var inter: WebView
    lateinit var bind: ActivityWisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityWisBinding.inflate(layoutInflater)
        setContentView(bind.root)
        inter = bind.vwvw

        val cmg = CookieManager.getInstance()
        cmg.setAcceptCookie(true)
        cmg.setAcceptThirdPartyCookies(inter, true)
        webSettings()

        val activity: Activity = this
        inter.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                try {
                    if (URLUtil.isNetworkUrl(url)) {
                        return false
                    }
                    if (appInstalledOrNot(url)) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Application is not installed", Toast.LENGTH_LONG).show()
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.telegram.messenger")))
                    }
                    return true
                } catch (e: Exception) {
                    return false
                }
                view.loadUrl(url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                saveUrl(url)
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()
            }
        }

        inter.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
                vcAU?.onReceiveValue(null)
                vcAU = filePathCallback
                var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent!!.resolveActivity(packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", str)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        str = "file:" + photoFile.absolutePath
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                    } else {
                        takePictureIntent = null
                    }
                }
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"

                val intentArray: Array<Intent?> = takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser))
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                startActivityForResult(chooserIntent, first)

                return true
            }

            @Throws(IOException::class)
            private fun createImageFile(): File {
                var imageStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere")
                if (!imageStorageDir.exists()) {
                    imageStorageDir.mkdirs()
                }
                imageStorageDir = File(imageStorageDir.toString() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg")
                return imageStorageDir
            }
        }
        inter.loadUrl(urr())
    }

    private fun pushToOneSignal(string: String) {
        OneSignal.setExternalUserId(string, object : OneSignal.OSExternalUserIdUpdateCompletionHandler {
                override fun onSuccess(results: JSONObject) {
                    try {
                        if (results.has("push") && results.getJSONObject("push").has("success")) {
                            val isPushSuccess = results.getJSONObject("push").getBoolean("success")
                            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id for push status: $isPushSuccess")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    try {
                        if (results.has("email") && results.getJSONObject("email").has("success")) {
                            val isEmailSuccess = results.getJSONObject("email").getBoolean("success")
                            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id for email status: $isEmailSuccess")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    try {
                        if (results.has("sms") && results.getJSONObject("sms").has("success")) {
                            val isSmsSuccess = results.getJSONObject("sms").getBoolean("success")
                            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id for sms status: $isSmsSuccess")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(error: OneSignal.ExternalIdError) {
                    OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id done with error: $error")
                }
            })
    }

    private fun webSettings() {
        val ws = inter.settings
        ws.javaScriptEnabled = true

        ws.useWideViewPort = true
        ws.loadWithOverviewMode = true
        ws.allowFileAccess = true
        ws.domStorageEnabled = true

        ws.userAgentString = ws.userAgentString.replace("; wv", "")
        ws.javaScriptCanOpenWindowsAutomatically = true
        ws.setSupportMultipleWindows(false)

        ws.displayZoomControls = false
        ws.builtInZoomControls = true
        ws.setSupportZoom(true)

        ws.pluginState = WebSettings.PluginState.ON
        ws.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        ws.setAppCacheEnabled(true)

        ws.allowContentAccess = true
    }

    private fun urr(): String {
        val spoon = getSharedPreferences("SP_WEBVIEW_PREFS", MODE_PRIVATE)
        val pack = "com.rubygames.assass"

        val cpOne:String? = Hawk.get(C1, "null")
        val mainId: String? = Hawk.get(MAIN_ID, "null")
        val dpOne: String? = Hawk.get(DEEPL, "null")

        val afId = AppsFlyerLib.getInstance().getAppsFlyerUID(this)
        AppsFlyerLib.getInstance().setCollectAndroidID(true)

        val af_id = "deviceID="
        val subOne = "sub_id_1="
        val adid = "ad_id="
        val sub4 = "sub_id_4="
        val sub5 = "sub_id_5="
        val sub6 = "sub_id_6="

        val naming = "naming"
        val depp = "deeporg"

        val bvr = Build.VERSION.RELEASE
        val linkAB = Hawk.get(link, "null")
        var aft = ""

        if (cpOne != "null"){
            aft = "$linkAB$subOne$cpOne&$af_id$afId&$adid$mainId&$sub4$pack&$sub5$bvr&$sub6$naming"
            pushToOneSignal(afId.toString())
        } else {
            aft = "$linkAB$subOne$dpOne&$af_id$afId&$adid$mainId&$sub4$pack&$sub5$bvr&$sub6$depp"
            pushToOneSignal(afId.toString())
        }
        return spoon.getString("SAVED_URL", aft).toString()
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        try {
            packageManager.getPackageInfo("org.telegram.messenger", PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) { }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != first || vcAU == null) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        var results: Array<Uri>? = null
        if (resultCode == RESULT_OK) {
            if (data == null || data.data == null) {
                results = arrayOf(Uri.parse(str))
            } else {
                val dataString = data.dataString
                if (dataString != null) {
                    results = arrayOf(Uri.parse(dataString))
                }
            }
        }
        vcAU?.onReceiveValue(results)
        vcAU = null
    }

    private var exitexitexitexit = false
    override fun onBackPressed() {
        if (inter.canGoBack()) {
            if (exitexitexitexit) {
                inter.stopLoading()
                inter.loadUrl(url)
            }
            this.exitexitexitexit = true
            inter.goBack()
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                exitexitexitexit = false
            }, 2000)
        } else {
            super.onBackPressed()
        }
    }

    var url = ""
    fun saveUrl(strurl: String?) {
        if (!strurl!!.contains("t.me")) {
            if (url == "") {
                url = getSharedPreferences("SP_WEBVIEW_PREFS", MODE_PRIVATE).getString("SAVED_URL", strurl).toString()

                val sp = getSharedPreferences("SP_WEBVIEW_PREFS", MODE_PRIVATE)
                val ed = sp.edit()

                ed.putString("SAVED_URL", strurl)
                ed.apply()
            }
        }
    }
}