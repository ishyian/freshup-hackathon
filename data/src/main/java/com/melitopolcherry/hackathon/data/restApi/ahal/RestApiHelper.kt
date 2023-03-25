package com.melitopolcherry.hackathon.data.restApi.ahal

open class RestApiHelper<T> {

    protected val errorHandler: IRestApiErrorHandler<T> = RestApiErrorHandler()
}
