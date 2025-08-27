package dev.eknath.espwebserverexpriment.data.api

import dev.eknath.espwebserverexpriment.data.models.ApiResponse
import dev.eknath.espwebserverexpriment.data.models.InputData
import dev.eknath.espwebserverexpriment.data.models.InputListResponse
import dev.eknath.espwebserverexpriment.data.models.MessageResponse
import retrofit2.Response
import retrofit2.http.*

interface EspApiService {
    
    @FormUrlEncoded
    @POST("input")
    suspend fun createInput(
        @Field("message") message: String
    ): Response<ApiResponse<InputData>>
    
    @GET("input")
    suspend fun getAllInputs(): Response<ApiResponse<InputListResponse>>
    
    @GET("input/{id}")
    suspend fun getInput(
        @Path("id") id: Int
    ): Response<ApiResponse<InputData>>
    
    @FormUrlEncoded
    @PUT("input/{id}")
    suspend fun updateInput(
        @Path("id") id: Int,
        @Field("message") message: String
    ): Response<ApiResponse<InputData>>
    
    @DELETE("input/{id}")
    suspend fun deleteInput(
        @Path("id") id: Int
    ): Response<ApiResponse<MessageResponse>>
}