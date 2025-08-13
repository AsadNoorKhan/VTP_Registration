package com.valsgroup.registration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.valsgroup.registration.api.NetworkModule
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.valsgroup.registration.ui.theme.ErrorRed
import com.valsgroup.registration.ui.theme.TableHeaderBg
import com.valsgroup.registration.ui.theme.TableRowBg
import com.valsgroup.registration.ui.theme.TableRowAltBg
import com.valsgroup.registration.ui.theme.SuccessGreen
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationForm() {
    var username by remember { mutableStateOf("") }
    var showAdditionalFields by remember { mutableStateOf(false) }
    var showTable by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    
    // Additional fields state
    var imeiId by remember { mutableStateOf("") }
    var userStatus by remember { mutableStateOf("Y") }
    var tagging by remember { mutableStateOf("") }
    
    // Edit mode state (for storing original values)
    var editUsername by remember { mutableStateOf("") }
    var editImeiId by remember { mutableStateOf("") }
    var editUserStatus by remember { mutableStateOf("Y") }
    var editTagging by remember { mutableStateOf("") }
    
    // API data state
    var tableData by remember { mutableStateOf<List<TableRow>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var filteredTableData by remember { mutableStateOf<List<TableRow>>(emptyList()) }
    
    val coroutineScope = rememberCoroutineScope()
    
    // Update filtered data when tableData or searchQuery changes
    LaunchedEffect(tableData, searchQuery) {
        filteredTableData = if (searchQuery.isBlank()) {
            tableData
        } else {
            tableData.filter { row ->
                row.imei.contains(searchQuery, ignoreCase = true) ||
                row.userStatus.contains(searchQuery, ignoreCase = true) ||
                row.tagging.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(16.dp), // Removed .verticalScroll(rememberScrollState())
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Registration Form",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Manage IMEI registrations and user data",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Username and Action Buttons
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { 
                            if (showAdditionalFields) {
                                showAdditionalFields = false
                            }
                            showTable = true
                            
                            // Fetch data from API when showing table
                            if (username.isNotBlank()) {
                                // Reset search when fetching new data
                                searchQuery = ""
                                coroutineScope.launch {
                                    isLoading = true
                                    errorMessage = null
                                    
                                    try {
                                        val result = NetworkModule.apiRepository.getUserStatus(username)
                                        result.onSuccess { (statusCode, response) ->
                                            if (statusCode == 200) {
                                                if (response.records.isNotEmpty()) {
                                                    // Show table
                                                    tableData = response.records.map { record ->
                                                        TableRow(
                                                            imei = record.imei_id.toString(),
                                                            userStatus = record.user_status,
                                                            tagging = record.tagging ?: "N/A"
                                                        )
                                                    }
                                                    errorMessage = null
                                                } else {
                                                    // Show server's status field (since message does not exist)
                                                    errorMessage = response.status ?: "No data found"
                                                    tableData = emptyList()
                                                }
                                            } else {
                                                // Show server's status field (since message does not exist)
                                                errorMessage = response.status ?: "No registered data found (HTTP $statusCode)"
                                                tableData = emptyList()
                                            }
                                        }.onFailure { exception ->
                                            errorMessage = "Failed to load data: ${exception.message}"
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Error: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SHOW")
                    }
                    
                    Button(
                        onClick = { 
                            if (showTable) {
                                showTable = false
                            }
                            isEditMode = false
                            showAdditionalFields = !showAdditionalFields
                            
                            // Reset form data when opening additional fields
                            if (showAdditionalFields) {
                                imeiId = ""
                                userStatus = "Y"
                                tagging = ""
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ADD")
                    }
                }
            }
        }
        
        // Additional fields (shown when ADD button is clicked)
        if (showAdditionalFields) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Filled.Edit else Icons.Filled.Add,
                            contentDescription = null,
                            tint = if (isEditMode) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isEditMode) "Edit User Information" else "Additional Information",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    
                    // IMEI ID field (16 or 12 digits) - read-only in edit mode
                    OutlinedTextField(
                        value = imeiId,
                        onValueChange = { 
                            if (!isEditMode && it.length <= 16 && it.all { char -> char.isDigit() }) {
                                imeiId = it
                            }
                        },
                        label = { Text("IMEI ID (max 16 digits)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        supportingText = { 
                            Text("${imeiId.length}/16") 
                        },
                        enabled = !isEditMode,
                        colors = if (isEditMode) OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) else OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    // Username length validation display
                    if (username.length > 50) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorRed.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Username must be 50 characters or less",
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    
                    // User ID display (taken from username) - always read-only
                    OutlinedTextField(
                        value = username,
                        onValueChange = { },
                        label = { Text("User ID (from Username)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        supportingText = { Text("${username.length}/50") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    // User Status field (Y/N)
                    Column {
                        Text(
                            text = "User Status",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = userStatus == "Y",
                                    onClick = { userStatus = "Y" },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    "Yes (Y)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = userStatus == "N",
                                    onClick = { userStatus = "N" },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    "No (N)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    // Tagging field (25 characters)
                    OutlinedTextField(
                        value = tagging,
                        onValueChange = { 
                            if (it.length <= 25) {
                                tagging = it
                            }
                        },
                        label = { Text("Tagging (25 characters)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = { Text("${tagging.length}/25") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    // Submit button
                    Button(
                        onClick = {
                            println("Submit pressed")
                            coroutineScope.launch {
                                isLoading = true
                                errorMessage = null
                                val payload = RegistrationPayload(
                                    imei_id = imeiId.toLongOrNull() ?: 0,
                                    user_id = username,
                                    user_status = userStatus,
                                    tagging = tagging
                                )
                                println("Payload: $payload")
                                val result = NetworkModule.apiRepository.registerImei(payload)
                                println("API result: $result")
                                result.onSuccess { (statusCode, response) ->
                                    if (statusCode == 200 || statusCode == 201) {
                                        tableData = listOf(
                                            TableRow(
                                                imei = payload.imei_id.toString(),
                                                userStatus = payload.user_status,
                                                tagging = payload.tagging
                                            )
                                        )
                                        showAdditionalFields = false
                                        showTable = true
                                        
                                        // Reset form data after successful submission
                                        imeiId = ""
                                        userStatus = "Y"
                                        tagging = ""
                                    } else {
                                        errorMessage = "Registration failed: HTTP $statusCode - ${response.message ?: response.operation ?: "Unknown error"}"
                                    }
                                }.onFailure { exception ->
                                    errorMessage = "Failed to register: ${exception.message}"
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = username.isNotBlank() && 
                                 username.length <= 50 &&
                                 imeiId.length <= 16 && 
                                 imeiId.isNotBlank() &&
                                 tagging.isNotBlank() &&
                                 tagging.length <= 25,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Submit",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
        
        // Table (shown when SHOW button is clicked)
        if (showTable) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Table header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "User Data Table",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    
                    // Show loading state
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Loading...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    // Show error message
                    errorMessage?.let { error ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorRed.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = error,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    
                    // Show table only if not loading and no error
                    if (!isLoading && errorMessage == null) {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search records...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Clear search",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Show search results count
                        if (searchQuery.isNotBlank()) {
                            Text(
                                text = "Showing ${filteredTableData.size} of ${tableData.size} records",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        // Table header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = TableHeaderBg,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "IMEI",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(2.2f),
                                maxLines = 1
                            )
                            Text(
                                text = "Status",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Tagging",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(1.3f),
                                maxLines = 1
                            )
                            Text(
                                text = "Action",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(0.9f),
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Make only the table scrollable
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 320.dp), // Adjust height as needed
                            userScrollEnabled = true
                        ) {
                            items(filteredTableData) { row ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (filteredTableData.indexOf(row) % 2 == 0) TableRowBg else TableRowAltBg
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp, horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = row.imei,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.95,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.weight(2.2f),
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = row.userStatus,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.95,
                                                color = if (row.userStatus == "Y") SuccessGreen else MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = row.tagging,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.95,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.weight(1.3f),
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Button(
                                            onClick = {
                                                // Enter edit mode with current row data
                                                editUsername = username
                                                editImeiId = row.imei
                                                editUserStatus = row.userStatus
                                                editTagging = row.tagging
                                                imeiId = row.imei
                                                userStatus = row.userStatus
                                                tagging = row.tagging
                                                isEditMode = true
                                                showTable = false
                                                showAdditionalFields = true
                                            },
                                            modifier = Modifier
                                                .weight(0.9f)
                                                .height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.tertiary
                                            ),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit",
                                                modifier = Modifier.size(14.dp)
                                            )
                                            // Removed Spacer and Text("Edit")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Data class for table rows
data class TableRow(
    val imei: String,
    val userStatus: String,
    val tagging: String
)

// Data class for registration payload
// Use Long for imei_id
 data class RegistrationPayload(
    val imei_id: Long,
    val user_id: String,
    val user_status: String,
    val tagging: String
) 