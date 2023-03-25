package com.melitopolcherry.hackathon.data.restApi.ahal

data class RestApiException<T>(val message: String?, val cause: Throwable?, val apiException: T) {

    val error = ApiError(message, cause)

    class ApiError(message: String?, cause: Throwable?) : RuntimeException(message, cause)

}
