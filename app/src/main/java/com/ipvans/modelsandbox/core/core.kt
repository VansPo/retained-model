package com.ipvans.modelsandbox.core

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log

@Suppress("UNCHECKED_CAST")
inline fun <P, M : Model<P>> Context.getModel(key: String, f: () -> M): M {
    val app = when (applicationContext) {
        is ModelContainer -> applicationContext as ModelContainer
        else -> throw IllegalArgumentException("Your Application must implement ModelContainer interface!")
    }

    val model = app.models[key] as M? ?: let {
        val component = f.invoke()
        app.models[key] = component as Model<P>
        component.setModelDestroyListener {
            app.models.remove(key)

            //todo remove debug log
            Log.i("MODEL", "App models count after destroy: ${app.models.size}")
        }
        component
    }

    if (this is AppCompatActivity)
        supportFragmentManager.getOrCreateRetainedFragment(key, model)

    return model
}

fun <P, M : Model<P>> FragmentManager.getOrCreateRetainedFragment(tag: String, model: M): RetainFragment<P, M> {
    @Suppress("UNCHECKED_CAST")
    val fragment = findFragmentByTag(tag) as RetainFragment<P, M>?
    return when {
        fragment == null -> {
            // create fragment with new instance
            val newFragment = RetainFragment<P, M>()
            newFragment.apply {
                newFragment.model = model
                beginTransaction()
                        .add(newFragment, tag)
                        .commit()
            }
        }
        fragment.model == null -> {
            // container-fragment found, but value is null => recreate fragment and create new instance
            val newFragment = RetainFragment<P, M>()
            newFragment.apply {
                newFragment.model = model
                beginTransaction()
                        .remove(fragment)
                        .add(newFragment, tag)
                        .commit()
            }
        }
        else -> fragment
    }
}

open class RetainFragment<P, M : Model<P>>() : Fragment() {

    var model: M? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        model?.onCreate()
    }

    override fun onDestroy() {
        model?.onDestroy()
        super.onDestroy()
    }
}

interface ModelContainer {

    val models: MutableMap<String, Model<*>>

}