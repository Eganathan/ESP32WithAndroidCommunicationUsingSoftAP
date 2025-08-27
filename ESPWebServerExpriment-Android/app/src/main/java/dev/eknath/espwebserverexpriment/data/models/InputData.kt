package dev.eknath.espwebserverexpriment.data.models

import com.google.gson.annotations.SerializedName

data class InputData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("timestamp")
    val timestamp: String
)

data class ApiResponse<T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: T
)

data class InputListResponse(
    @SerializedName("inputs")
    val inputs: List<InputData>,
    @SerializedName("count")
    val count: Int
)

data class MessageResponse(
    @SerializedName("message")
    val message: String
)

data class ErrorResponse(
    @SerializedName("error")
    val error: String
)