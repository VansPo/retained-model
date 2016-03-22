package com.ipvans.rm.example

import android.app.Application

class App : Application(), ModelContainer {

    override val models: MutableMap<String, Model<*>> = mutableMapOf()

    override fun onCreate() {
        super.onCreate()
    }

}