package com.frange.coasters.domain.usecase.base

import android.util.Log
import java.io.IOException

abstract class BaseUseCase<Param, Callback : BaseUseCaseCallback?>(threading: Threading) {

    companion object {
        private const val EXCEPTION = "Exception"
    }

    private var threading: Threading

    init {
        this.threading = threading
    }

    @Throws(IOException::class)
    protected abstract fun runUseCase(param: Param, callback: Callback)
    fun execute(params: Param, callback: Callback) {
        threading.executeOnWorkingThread {
            try {
                runUseCase(params, callback)
            } catch (e: Exception) {
                Log.v("Exception", "$EXCEPTION: $params")
//                sendToMainThread(callback.onConnectionError())
//                sendToMainThread(callback::onConnectionError)
            }
        }
    }

    protected fun sendToMainThread(r: Runnable) {
        threading.executeOnMainThread(r)
    }

}