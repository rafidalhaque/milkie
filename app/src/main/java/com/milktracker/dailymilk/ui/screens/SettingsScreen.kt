package com.milktracker.dailymilk.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.milktracker.dailymilk.data.PreferencesRepository
import com.milktracker.dailymilk.notification.NotificationHelper
import com.milktracker.dailymilk.ui.UiConstants
import com.milktracker.dailymilk.ui.theme.MilkBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesRepository: PreferencesRepository,
    notificationHelper: NotificationHelper,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val notificationHour by preferencesRepository.notificationHour.collectAsState(initial = 8)
    val notificationMinute by preferencesRepository.notificationMinute.collectAsState(initial = 0)
    val pricePerLiter by preferencesRepository.pricePerLiter.collectAsState(initial = 50.0)
    val notificationsEnabled by preferencesRepository.notificationsEnabled.collectAsState(initial = true)
    
    var showTimePicker by remember { mutableStateOf(false) }
    var priceInput by remember { mutableStateOf("") }
    var hasNotificationPermission by remember { mutableStateOf(false) }
    
    // Initialize price input
    LaunchedEffect(pricePerLiter) {
        priceInput = pricePerLiter.toString()
    }
    
    // Check notification permission
    LaunchedEffect(Unit) {
        hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted && notificationsEnabled) {
            notificationHelper.scheduleNotification(notificationHour, notificationMinute)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MilkBlue,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notification Settings Section
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                color = MilkBlue
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Enable/Disable notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Reminder",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Get reminded to log your milk delivery",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { enabled ->
                                scope.launch {
                                    preferencesRepository.setNotificationsEnabled(enabled)
                                    if (enabled) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            notificationHelper.scheduleNotification(notificationHour, notificationMinute)
                                        }
                                    } else {
                                        notificationHelper.cancelNotification()
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = MilkBlue)
                        )
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    // Notification time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Reminder Time",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "When to send the daily reminder",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        
                        TextButton(
                            onClick = { showTimePicker = true },
                            enabled = notificationsEnabled
                        ) {
                            Text(
                                text = String.format("%02d:%02d", notificationHour, notificationMinute),
                                color = if (notificationsEnabled) MilkBlue else Color.Gray,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    
                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠️ Notification permission required",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        ) {
                            Text("Grant Permission", color = MilkBlue)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price Settings Section
            Text(
                text = "Pricing",
                style = MaterialTheme.typography.titleMedium,
                color = MilkBlue
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Price per Liter",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Used to calculate monthly cost",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "৳", style = MaterialTheme.typography.titleLarge)
                        
                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { value ->
                                if (value.isEmpty() || value.matches(UiConstants.DECIMAL_INPUT_REGEX)) {
                                    priceInput = value
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MilkBlue,
                                cursorColor = MilkBlue
                            )
                        )
                        
                        Button(
                            onClick = {
                                priceInput.toDoubleOrNull()?.let { price ->
                                    scope.launch {
                                        preferencesRepository.setPricePerLiter(price)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MilkBlue)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // About Section
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = MilkBlue
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Daily Milk Tracker",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Version 1.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Track your daily milk deliveries and view monthly summaries with ease.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
    
    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = notificationHour,
            initialMinute = notificationMinute
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Set Reminder Time") },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            preferencesRepository.setNotificationTime(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            if (notificationsEnabled) {
                                notificationHelper.scheduleNotification(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )
                            }
                        }
                        showTimePicker = false
                    }
                ) {
                    Text("OK", color = MilkBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
