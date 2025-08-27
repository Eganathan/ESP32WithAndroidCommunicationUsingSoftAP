package dev.eknath.espwebserverexpriment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eknath.espwebserverexpriment.data.models.InputData
import dev.eknath.espwebserverexpriment.data.repository.InputRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val inputs: List<InputData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class MainViewModel : ViewModel() {
    
    private val repository = InputRepository()
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        loadAllInputs()
    }
    
    fun createInput(message: String) {
        if (message.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            repository.createInput(message)
                .onSuccess { newInput ->
                    _uiState.value = _uiState.value.copy(
                        inputs = _uiState.value.inputs + newInput,
                        isLoading = false,
                        successMessage = "Input created successfully"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }
    
    fun loadAllInputs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            repository.getAllInputs()
                .onSuccess { inputs ->
                    _uiState.value = _uiState.value.copy(
                        inputs = inputs,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }
    
    fun updateInput(id: Int, message: String) {
        if (message.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            repository.updateInput(id, message)
                .onSuccess { updatedInput ->
                    val updatedList = _uiState.value.inputs.map { input ->
                        if (input.id == id) updatedInput else input
                    }
                    _uiState.value = _uiState.value.copy(
                        inputs = updatedList,
                        isLoading = false,
                        successMessage = "Input updated successfully"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }
    
    fun deleteInput(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            repository.deleteInput(id)
                .onSuccess {
                    val updatedList = _uiState.value.inputs.filter { it.id != id }
                    _uiState.value = _uiState.value.copy(
                        inputs = updatedList,
                        isLoading = false,
                        successMessage = "Input deleted successfully"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}