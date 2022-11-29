package com.rubygames.assass

import android.app.Application
import com.onesignal.OneSignal
import com.orhanobut.hawk.Hawk

class MainClass : Application() {

    companion object {
        const val oneS = "2a8b2683-39eb-4460-8174-8b5093652260"
        var appsCheck = "appsChecker"
        var geo = "geo"
        var C1: String? = "c11"
        var link = "link"
        var MAIN_ID: String? = "mainid"
        var DEEPL: String? = "d11"
        var countryCode: String? = "countryCode"
    }

    override fun onCreate() {
        super.onCreate()

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(oneS)
        Hawk.init(this).build()
    }
}
