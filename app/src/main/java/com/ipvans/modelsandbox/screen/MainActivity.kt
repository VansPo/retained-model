package com.ipvans.modelsandbox.screen

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import com.ipvans.modelsandbox.App
import com.ipvans.modelsandbox.R
import com.ipvans.modelsandbox.core.getModel
import com.trello.rxlifecycle.components.support.RxAppCompatActivity

class MainActivity : RxAppCompatActivity() {

    lateinit var model: MainModel
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


        model = getModel("main_activity") { MainModel() }
        presenter = MainPresenter(this, model)

    }

    override fun onStart() {
        super.onStart()
        model.attachPresenter(presenter)

        Log.i("MODEL", "App models count after creation: ${(application as App).models.size}")
    }

    override fun onStop() {
        model.detachPresenter()
        super.onStop()
    }
}