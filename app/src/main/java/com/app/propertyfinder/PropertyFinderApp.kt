package com.app.propertyfinder

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.hilt.work.HiltWorkerFactory
import com.app.propertyfinder.utils.PrefUtils
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import androidx.work.Configuration
import org.acra.ACRA
import org.acra.annotation.ReportsCrashes
import javax.inject.Inject


@HiltAndroidApp
@ReportsCrashes(mailTo = "crashlog@hitasoft.com", formKey = "")
class PropertyFinderApp : Application(),Configuration.Provider,Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        FirebaseApp.initializeApp(this)
        Timber.plant(Timber.DebugTree())
        PrefUtils.init(this)
        ACRA.init(this);
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}