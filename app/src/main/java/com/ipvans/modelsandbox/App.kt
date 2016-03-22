package com.ipvans.modelsandbox

import android.app.Application
import com.ipvans.modelsandbox.core.Model
import com.ipvans.modelsandbox.core.ModelContainer
import com.squareup.leakcanary.LeakCanary

class App : Application(), ModelContainer {

    override val models: MutableMap<String, Model<*>> = mutableMapOf()

    override fun onCreate() {
        super.onCreate()

        LeakCanary.install(this)
    }

}