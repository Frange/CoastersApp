package com.frange.coasters.domain.usecase.base

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Qualifier

@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
annotation class NamedWorkerThread(
    /**
     * The name.
     */
    val value: String = "workerThread"
)
