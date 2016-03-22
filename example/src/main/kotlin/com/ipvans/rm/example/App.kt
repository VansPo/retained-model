package com.ipvans.rm.example

import android.app.Application
import com.ipvans.rm.retainedmodel.Model
import com.ipvans.rm.retainedmodel.ModelContainer

class App : Application(), ModelContainer {

    override val models: MutableMap<String, Model<*>> = mutableMapOf()

    override fun onCreate() {
        super.onCreate()
    }

}