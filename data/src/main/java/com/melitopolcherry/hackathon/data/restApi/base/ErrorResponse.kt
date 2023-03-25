package com.melitopolcherry.hackathon.data.restApi.base

import com.melitopolcherry.hackathon.data.utils.Enums

data class ErrorResponse(
    val message: String?,
    val status: Int?,
    val description: String?,
    val error: String?,
    val error_code: Enums.ErrorCodes?,
    val error_message: String?,
    val code: Enums.ErrorCodes?,
    val error_description: String?
)
