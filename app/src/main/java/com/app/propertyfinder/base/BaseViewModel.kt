package com.app.propertyfinder.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.app.propertyfinder.PropertyFinderApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val job = SupervisorJob()
    protected val ioScope = CoroutineScope(Dispatchers.IO + job)

    protected val appContext = application.applicationContext

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
