package com.ipvans.modelsandbox.screen

import android.support.design.widget.FloatingActionButton
import android.widget.TextView
import com.ipvans.modelsandbox.R
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.PublishSubject

class MainPresenter(context: MainActivity, val model: MainModel) {

    val text by lazy { context.findViewById(R.id.textview) as TextView }
    val fab by lazy { context.findViewById(R.id.fab) as FloatingActionButton }

    private val clicksSubject = PublishSubject<Any>()
    val clicks = clicksSubject.asObservable()

    init {
        fab.setOnClickListener { clicksSubject.onNext(0) }

        // restore state
        model.countdown
                .bindToLifecycle(context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { text.text = "Countdown: $it" }
    }

}