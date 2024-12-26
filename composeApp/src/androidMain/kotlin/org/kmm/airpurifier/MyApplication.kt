package org.kmm.airpurifier

import android.app.Application
import android.content.Context
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.kmm.airpurifier.di.initKoin
import org.koin.android.ext.koin.androidContext

internal lateinit var appContext: Context

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this@MyApplication
        initKoin {
            androidContext(this@MyApplication)
        }
        Napier.base(DebugAntilog())
    }
}