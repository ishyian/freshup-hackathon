package com.melitopolcherry.hackathon.data.restApi.ahal

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

open class RestApiRxHelper<T>(private val entity: Class<T>) : RestApiHelper<T>() {

    fun <TResponse> processResponse(upstream: Single<TResponse>): Single<TResponse> {
        return upstream
            .onErrorResumeNext { throwable ->
                Single.error(
                    errorHandler.processError(
                        throwable,
                        entity
                    )
                )
            }
    }

    fun <TResponse> processListResponse(upstream: Single<List<TResponse>>): Single<List<TResponse>> {
        return upstream
            .onErrorResumeNext { throwable ->
                Single.error(
                    errorHandler.processError(
                        throwable,
                        entity
                    )
                )
            }
    }

    fun processResponse(upstream: Completable): Completable {
        return upstream
            .onErrorResumeNext { throwable ->
                Completable.error(
                    errorHandler.processError(
                        throwable,
                        entity
                    )
                )
            }
    }
}