package com.melitopolcherry.hackathon.data.restApi.ahal

interface IRestApiErrorHandler<T> {
    fun processError(throwable: Throwable, entity: Class<T>): Throwable
}
