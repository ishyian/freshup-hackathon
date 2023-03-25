package com.melitopolcherry.hackathon.data.restApi.errorsHandler

import com.melitopolcherry.hackathon.data.restApi.base.ErrorResponse

class ApiException(message: String?, cause: Throwable?, val details: ErrorResponse) : RuntimeException(message, cause)