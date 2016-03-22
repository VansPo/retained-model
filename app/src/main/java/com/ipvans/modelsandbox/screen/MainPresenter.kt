package com.ipvans.modelsandbox.screen

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.PublishSubject

class MainPresenter(context: MainActivity, val model: MainModel) {

    private val clicksSubject = PublishSubject<Any>()
    val clicks = clicksSubject.asObservable()

    init {
        context.init()

        // restore state
        model.countdown
                .bindToLifecycle(context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { context.updateCounter(it) }
    }

    fun clickAction() = clicksSubject.onNext(0)

}