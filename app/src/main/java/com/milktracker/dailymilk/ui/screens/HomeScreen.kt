package com.milktracker.dailymilk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.milktracker.dailymilk.data.MilkRepository
import com.milktracker.dailymilk.ui.theme.MilkBlue
import com.milktracker.dailymilk.ui.theme.MilkBlueLight
import com.milktracker.dailymilk.ui.theme.Success
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    milkRepository: MilkRepository,
    onNavigateToSummary: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val today = LocalDate.now()
    val dateString = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
    
    val todayEntry by milkRepository.getEntryByDateFlow(dateString).collectAsState(initial = null)
    
    var selectedAmount by remember { mutableStateOf<Double?>(null) }
    var customAmount by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // Update selected amount when entry changes
    LaunchedEffect(todayEntry) {
        todayEntry?.let { entry ->
            selectedAmount = entry.amountLiters
            if (entry.amountLiters != 0.0 && entry.amountLiters != 0.5 && entry.amountLiters != 1.0) {
                customAmount = entry.amountLiters.toString()
                showCustomInput = true
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Milk Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MilkBlue,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onNavigateToSummary) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Monthly Summary",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MilkBlueLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        style = MaterialTheme.typography.titleMedium,
                        color = MilkBlue
                    )
                    Text(
                        text = today.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MilkBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Question
            Text(
                text = "How much milk did you receive today?",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick selection buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MilkAmountButton(
                    amount = 0.0,
                    label = "0L",
                    isSelected = selectedAmount == 0.0 && !showCustomInput,
                    onClick = {
                        selectedAmount = 0.0
                        showCustomInput = false
                        customAmount = ""
                    }
                )
                
                MilkAmountButton(
                    amount = 0.5,
                    label = "0.5L",
                    isSelected = selectedAmount == 0.5 && !showCustomInput,
                    onClick = {
                        selectedAmount = 0.5
                        showCustomInput = false
                        customAmount = ""
                    }
                )
                
                MilkAmountButton(
                    amount = 1.0,
                    label = "1L",
                    isSelected = selectedAmount == 1.0 && !showCustomInput,
                    onClick = {
                        selectedAmount = 1.0
                        showCustomInput = false
                        customAmount = ""
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Custom amount button
            TextButton(
                onClick = { showCustomInput = !showCustomInput }
            ) {
                Text(
                    text = if (showCustomInput) "Hide Custom Amount" else "Enter Custom Amount",
                    color = MilkBlue
                )
            }
            
            // Custom input field
            if (showCustomInput) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
                            customAmount = value
                            selectedAmount = value.toDoubleOrNull()
                        }
                    },
                    label = { Text("Amount in Liters") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.6f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MilkBlue,
                        cursorColor = MilkBlue
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save button
            Button(
                onClick = {
                    selectedAmount?.let { amount ->
                        scope.launch {
                            milkRepository.saveEntry(dateString, amount)
                            showSavedMessage = true
                        }
                    }
                },
                enabled = selectedAmount != null,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MilkBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (showSavedMessage) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Saved",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saved!", style = MaterialTheme.typography.titleMedium)
                } else {
                    Text("Save Entry", style = MaterialTheme.typography.titleMedium)
                }
            }
            
            // Reset saved message after a delay
            LaunchedEffect(showSavedMessage) {
                if (showSavedMessage) {
                    kotlinx.coroutines.delay(2000)
                    showSavedMessage = false
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Today's entry status
            todayEntry?.let { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Recorded",
                            tint = Success,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Today's entry: ${entry.amountLiters}L recorded",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Success
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MilkAmountButton(
    amount: Double,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(if (isSelected) MilkBlue else Color.Transparent)
            .border(
                width = 2.dp,
                color = MilkBlue,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) Color.White else MilkBlue
        )
    }
}
