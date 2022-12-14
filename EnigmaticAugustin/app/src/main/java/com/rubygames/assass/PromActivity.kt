package com.rubygames.assass

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.orhanobut.hawk.Hawk
import com.rubygames.assass.MainClass.Companion.MAIN_ID
import com.rubygames.assass.MainClass.Companion.appsCheck
import com.rubygames.assass.MainClass.Companion.countryCode
import com.rubygames.assass.MainClass.Companion.geo
import com.rubygames.assass.logic.view.MenuActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PromActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adact)

        checkCountry()
    }

    private fun getShData(): String? {
        val restCheck: String? = Hawk.get(appsCheck)
        return restCheck
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun checkCountry() {
        val check = getShData()
        if (check == "0") {
            aLone()
        } else {
            GlobalScope.launch(Dispatchers.Default) {
                getAdId()
            }
                intAfb()
        }
    }

    private fun getAdId(){
        val adInfo = AdvertisingIdClient(applicationContext)
        adInfo.start()

        val adIdInfo = adInfo.info.id
        Hawk.put(MAIN_ID, adIdInfo)
    }

    private fun aLone() {
        val intent = Intent(this@PromActivity, MenuActivity::class.java)
        Hawk.put(geo, null)
        Hawk.put(countryCode, null)
        Hawk.put(appsCheck, null)

        startActivity(intent)
        finish()
    }

    private fun intAfb() {
        val intent = Intent(this@PromActivity, ASFActivity::class.java)
        startActivity(intent)
        finish()
    }
}