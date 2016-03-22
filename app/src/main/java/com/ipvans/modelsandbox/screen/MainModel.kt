package com.ipvans.modelsandbox.screen

import android.util.Log
import com.ipvans.modelsandbox.core.Model
import rx.Observable
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.plusAssign
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit

class MainModel() : Model<MainPresenter>() {

    //todo consider using Observable
    private var containerDestroyed = false
    private var countdownFinished = false
    //
    private val modelLifecycleSubscriptions = CompositeSubscription()
    private val viewLifecycleSubscriptions = CompositeSubscription()

    private val clicks = PublishSubject<Any>()

    val countdown = clicks
            .switchMap {
                countdownFinished = false
                Observable.from(20 downTo 0)
                        .zipWith(Observable.interval(1, TimeUnit.SECONDS)) {
                            count, i ->
                            count
                        }
                        .takeUntil { it == 0 }
            }
            .doOnNext {
                if (it == 0) {
                    Log.i("MODEL", "countdown finished, container destroyed = $containerDestroyed")
                    countdownFinished = true
                    // if activity is destroyed and our long task is finished
                    // destroy model
                    if (containerDestroyed) finish()
                }
            }
            .publish()

    override fun onCreate() {
        Log.i("MODEL", "create model")
        containerDestroyed = false
        modelLifecycleSubscriptions += countdown.connect()
    }

    override fun attachPresenter(presenter: MainPresenter) {
        Log.i("MODEL", "attach presenter")
        viewLifecycleSubscriptions += presenter.clicks.subscribe(clicks)
    }

    override fun detachPresenter() {
        Log.i("MODEL", "detach presenter")
        viewLifecycleSubscriptions.clear()
    }

    override fun onDestroy() {
        Log.i("MODEL", "destroy model")
        containerDestroyed = true
        if (countdownFinished) finish()
    }

    fun finish() {
        modelLifecycleSubscriptions.clear()
        destroy()
    }

}