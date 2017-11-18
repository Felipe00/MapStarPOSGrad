package io.felipe.mapstarposgrad

import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication

/**
 * Created by ALUNO on 18/11/2017.
 */

class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }
}
