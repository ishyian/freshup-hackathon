package com.melitopolcherry.hackathon.data.restApi.errorsHandler

import com.google.gson.Gson
import com.melitopolcherry.hackathon.data.restApi.base.ErrorResponse
import retrofit2.HttpException

class ErrorHandler : IErrorHandler {
    private val gson: Gson = Gson()

    override fun processError(throwable: Throwable): Throwable {
        if (throwable !is HttpException) {
            return throwable
        }

        val response = throwable.response()
        val errorBody = response?.errorBody() ?: return throwable

        val errorResponse = gson.fromJson(errorBody.charStream(), ErrorResponse::class.java)
                ?: return throwable

        return ApiException(errorResponse.message, throwable, errorResponse)
    }
}