package com.ipvans.rm.example.screen

import android.util.Log
import com.ipvans.rm.retainedmodel.Model
import rx.Observable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.plusAssign
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit

class MainModel() : Model<MainPresenter>() {

    private val countDownFinishSubject = BehaviorSubject<Boolean>()
    private val containerDestroySubject = BehaviorSubject<Boolean>()
    private val forceDestroySubject = BehaviorSubject<Boolean>()

    private val modelLifecycleSubscriptions = CompositeSubscription()
    private val viewLifecycleSubscriptions = CompositeSubscription()

    private val clicks = PublishSubject<Any>()

    val countdown = clicks
            .switchMap {
                countDownFinishSubject.onNext(false)
                Observable.from(20 downTo 0)
                        .zipWith(Observable.interval(0, 1, TimeUnit.SECONDS)) {
                            count, i ->
                            count
                        }
                        .takeUntil { it == 0 }
            }
            .doOnNext {
                if (it == 0) {
                    Log.i("MODEL", "countdown finished, container destroyed = ${containerDestroySubject.value}")
                    countDownFinishSubject.onNext(true)
                }
            }
            .publish()

    private val destroyEventObservable = Observable.combineLatest(
            countDownFinishSubject, containerDestroySubject, forceDestroySubject.startWith(false)) {
        countdown, container, forceDestroy ->
        (countdown && container) || forceDestroy
    }
            .filter { it }
            .doOnNext { finish() }
            .publish()

    override fun onCreate() {
        Log.i("MODEL", "create model")
        containerDestroySubject.onNext(false)
        modelLifecycleSubscriptions += countdown.connect()
        modelLifecycleSubscriptions += destroyEventObservable.connect()
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
        containerDestroySubject.onNext(true)
    }

    fun finish() {
        modelLifecycleSubscriptions.clear()
        destroy()
    }

}