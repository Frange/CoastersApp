package com.frange.coasters.domain.usecase.base

import com.frange.coasters.domain.usecase.base.NamedMainThread
import com.frange.coasters.domain.usecase.base.NamedWorkerThread
import java.util.concurrent.Executor
import javax.inject.Inject

class Threading @Inject constructor(
    @NamedWorkerThread workerThreadExecutor: Executor,
    @NamedMainThread mainExecutor: Executor
) {
    private val mWorkerThreadExecutor: Executor
    private val mMainExecutor: Executor

    init {
        mWorkerThreadExecutor = workerThreadExecutor
        mMainExecutor = mainExecutor
    }

    fun executeOnWorkingThread(r: Runnable?) {
        mWorkerThreadExecutor.execute(r)
    }

    fun executeOnMainThread(r: Runnable?) {
        mMainExecutor.execute(r)
    }

    fun execute(e: Executor, r: Runnable?) {
        e.execute(r)
    }
}