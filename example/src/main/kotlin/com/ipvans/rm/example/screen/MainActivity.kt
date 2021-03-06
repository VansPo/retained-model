package com.ipvans.rm.example.screen

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.TextView
import com.ipvans.rm.example.R
import com.ipvans.rm.example.App
import com.ipvans.rm.retainedmodel.getModel
import com.trello.rxlifecycle.components.support.RxAppCompatActivity

class MainActivity : RxAppCompatActivity() {

    lateinit var model: MainModel
    lateinit var presenter: MainPresenter

    val text by lazy { findViewById(R.id.textview) as TextView }
    val fab by lazy { findViewById(R.id.fab) as FloatingActionButton }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


        model = getModel("main_activity", selfDestroyable = false) { MainModel() }
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

    fun init() {
        fab.setOnClickListener { presenter.clickAction() }
    }

    fun updateCounter(count: Int) {
        text.text = "Countdown: $count"
    }
}