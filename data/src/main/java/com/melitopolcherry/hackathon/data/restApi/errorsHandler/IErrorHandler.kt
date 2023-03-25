package com.melitopolcherry.hackathon.data.restApi.errorsHandler

interface IErrorHandler {
    fun processError(throwable: Throwable): Throwable
}
