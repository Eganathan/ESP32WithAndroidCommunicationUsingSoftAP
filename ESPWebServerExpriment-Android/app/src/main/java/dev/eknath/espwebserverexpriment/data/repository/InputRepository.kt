package dev.eknath.espwebserverexpriment.data.repository

import dev.eknath.espwebserverexpriment.data.api.ApiClient
import dev.eknath.espwebserverexpriment.data.models.InputData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InputRepository {
    
    private val apiService = ApiClient.apiService
    
    suspend fun createInput(message: String): Result<InputData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createInput(message)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to create input: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllInputs(): Result<List<InputData>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllInputs()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data.inputs)
            } else {
                Result.failure(Exception("Failed to get inputs: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getInput(id: Int): Result<InputData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getInput(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to get input: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateInput(id: Int, message: String): Result<InputData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateInput(id, message)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to update input: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteInput(id: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteInput(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data.message)
            } else {
                Result.failure(Exception("Failed to delete input: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}