package com.melitopolcherry.hackathon.data.restApi

import com.melitopolcherry.hackathon.data.restApi.ahal.RestApiRxHelper
import com.melitopolcherry.hackathon.data.restApi.base.ErrorResponse

open class BaseEvenzRepository : RestApiRxHelper<ErrorResponse>(ErrorResponse::class.java)