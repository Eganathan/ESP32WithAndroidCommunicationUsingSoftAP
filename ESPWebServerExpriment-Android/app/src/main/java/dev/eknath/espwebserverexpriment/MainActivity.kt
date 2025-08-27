package dev.eknath.espwebserverexpriment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.eknath.espwebserverexpriment.data.models.InputData
import dev.eknath.espwebserverexpriment.ui.theme.ESPWebServerExprimentTheme
import dev.eknath.espwebserverexpriment.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESPWebServerExprimentTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var newMessage by remember { mutableStateOf("") }
    var editingInput by remember { mutableStateOf<InputData?>(null) }
    var editMessage by remember { mutableStateOf("") }
    
    // Show snackbar messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        if (uiState.errorMessage != null || uiState.successMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ESP8266 CRUD API Test") },
                actions = {
                    IconButton(onClick = { viewModel.loadAllInputs() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Connection Info
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ESP8266 Connection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("WiFi: Eknath_SoftAPExperiment")
                    Text("Password: 12345678")
                    Text("URL: http://192.168.4.1")
                }
            }
            
            // Create Input Section
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Create New Input",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = newMessage,
                        onValueChange = { newMessage = it },
                        label = { Text("Message") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.createInput(newMessage)
                            newMessage = ""
                        },
                        enabled = newMessage.isNotBlank() && !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Input")
                    }
                }
            }
            
            // Status Messages
            uiState.errorMessage?.let { error ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        text = "Error: $error",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            uiState.successMessage?.let { success ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(
                        text = success,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            // Loading indicator
            if (uiState.isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Inputs List
            Text(
                text = "Inputs (${uiState.inputs.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.inputs) { input ->
                    InputCard(
                        input = input,
                        isEditing = editingInput?.id == input.id,
                        editMessage = editMessage,
                        onEditMessageChange = { editMessage = it },
                        onEdit = {
                            editingInput = input
                            editMessage = input.message
                        },
                        onSave = {
                            viewModel.updateInput(input.id, editMessage)
                            editingInput = null
                            editMessage = ""
                        },
                        onCancel = {
                            editingInput = null
                            editMessage = ""
                        },
                        onDelete = { viewModel.deleteInput(input.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun InputCard(
    input: InputData,
    isEditing: Boolean,
    editMessage: String,
    onEditMessageChange: (String) -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${input.id}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Time: ${input.timestamp}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isEditing) {
                OutlinedTextField(
                    value = editMessage,
                    onValueChange = onEditMessageChange,
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onSave) {
                        Text("Save")
                    }
                    OutlinedButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                }
            } else {
                Text(
                    text = input.message,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}