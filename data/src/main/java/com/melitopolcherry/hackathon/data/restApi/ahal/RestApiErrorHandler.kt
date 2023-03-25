package com.melitopolcherry.hackathon.data.restApi.ahal

import com.melitopolcherry.hackathon.data.restApi.base.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException

open class RestApiErrorHandler<T> : IRestApiErrorHandler<T> {
    private val gson: Gson = Gson()

//    { "error_code": "UNAUTHORIZED",
    //    "error_message": "Access token expired: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiZXZlbnpfcmVzdF9hcGkiXSwidXNlcl9uYW1lIjoidG9ueWRhcmtvd29ya0BnbWFpbC5jb20iLCJzY29wZSI6WyJSRUFEIiwiV1JJVEUiXSwiZXhwIjoxNjQ3NDMxMjk1LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiRVAtMHBvVUFmZjZDTmFWTGxvUVU1TTBCNklnIiwiY2xpZW50X2lkIjoiZXZlbnpfbW9iaWxlX2FwcCJ9.wjVr_xH6WR96f8EHQA7QZs3a_68pDIgLOSnkS2U8M9Y" }

    override fun processError(throwable: Throwable, entity: Class<T>): Throwable {
        if (throwable !is HttpException) {
            return throwable
        }
        val response = throwable.response()
        val errorBody = response?.errorBody() ?: return throwable

        val errorResponse: T = gson.fromJson(errorBody.charStream(), entity)
            ?: return throwable
        return if (errorResponse is ErrorResponse) {
            var desc = errorResponse.code?.description
            if (desc == null) {
                desc = errorResponse.error_description
            }
            if (desc == null) {
                desc = errorResponse.error_message
            }
            if (desc == null) {
                desc = errorResponse.message
            }
            RestApiException(desc, throwable, errorResponse).error
        } else {
            RestApiException(throwable.message, throwable, errorResponse).error
        }
    }
}