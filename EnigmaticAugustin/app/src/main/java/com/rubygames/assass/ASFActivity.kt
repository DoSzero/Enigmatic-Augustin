package com.rubygames.assass

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.rubygames.assass.MainClass.Companion.C1
import com.rubygames.assass.MainClass.Companion.DEEPL
import com.rubygames.assass.MainClass.Companion.appsCheck
import com.rubygames.assass.MainClass.Companion.countryCode
import com.rubygames.assass.MainClass.Companion.geo
import com.orhanobut.hawk.Hawk
import com.rubygames.assass.databinding.ActivityAsfBinding
import com.rubygames.assass.logic.view.MenuActivity
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ASFActivity : AppCompatActivity() {

    lateinit var bindAsf: ActivityAsfBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindAsf = ActivityAsfBinding.inflate(layoutInflater)
        setContentView(bindAsf.root)

        val appsCh: String? = Hawk.get(appsCheck, "null")
        if (appsCh == "1") {
            AppsFlyerLib.getInstance().init("QJPxhk6Ca46jNNK7W3krrY", conversionDataListener, applicationContext)
            AppsFlyerLib.getInstance().start(this)
        }
        folk()
    }

    private val conversionDataListener = object : AppsFlyerConversionListener {
        override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
            val dataGotten = data?.get("campaign").toString()
            Hawk.put(C1, dataGotten)
        }
        override fun onConversionDataFail(p0: String?) {}
        override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
        override fun onAttributionFailure(p0: String?) {}
    }

   private fun folk() {
       val countriesPool: String = Hawk.get(geo)
       val countyC: String = Hawk.get(countryCode, "null")
       val deeplink: String? = Hawk.get(DEEPL, "null")
       val appsCh: String? = Hawk.get(appsCheck, "null")
       var naming: String? = Hawk.get(C1)

       if (appsCh == "1") {
           val executorService = Executors.newSingleThreadScheduledExecutor()
           executorService.scheduleAtFixedRate({
               if (naming != null) {
                   if (naming!!.contains("tdb2") || countriesPool.contains(countyC) || deeplink!!.contains("tdb2")) {
                       executorService.shutdown()
                       we()
                   } else {
                       executorService.shutdown()
                       lone()
                   }
               } else {
                   naming = Hawk.get(C1)
               }
           }, 0, 1, TimeUnit.SECONDS)
       }  else if (countriesPool.contains(countyC)) {
           we()
       } else {
           lone()
       }
   }

    private fun lone() {
        val intent = Intent(this@ASFActivity, MenuActivity::class.java)
        Hawk.put(geo, null)
        Hawk.put(countryCode, null)
        Hawk.put(appsCheck, null)

        startActivity(intent)
        finish()
    }

    private fun we() {
        val intent = Intent(this@ASFActivity, WIS::class.java)
        Hawk.put(geo, null)
        Hawk.put(countryCode, null)
        Hawk.put(appsCheck, null)

        startActivity(intent)
        finish()
    }
}


