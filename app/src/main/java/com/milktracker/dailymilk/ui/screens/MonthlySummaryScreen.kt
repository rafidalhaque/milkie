package com.milktracker.dailymilk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.milktracker.dailymilk.data.MilkEntry
import com.milktracker.dailymilk.data.MilkRepository
import com.milktracker.dailymilk.data.PreferencesRepository
import com.milktracker.dailymilk.ui.theme.MilkBlue
import com.milktracker.dailymilk.ui.theme.MilkBlueLight
import com.milktracker.dailymilk.ui.theme.Success
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySummaryScreen(
    milkRepository: MilkRepository,
    preferencesRepository: PreferencesRepository,
    onNavigateBack: () -> Unit
) {
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    val yearMonth = selectedMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
    
    val entries by milkRepository.getEntriesForMonth(yearMonth).collectAsState(initial = emptyList())
    val pricePerLiter by preferencesRepository.pricePerLiter.collectAsState(initial = 50.0)
    
    val totalLiters = entries.sumOf { it.amountLiters }
    val totalCost = totalLiters * pricePerLiter
    val daysWithMilk = entries.count { it.amountLiters > 0 }
    
    var entryToDelete by remember { mutableStateOf<MilkEntry?>(null) }
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Summary") },
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
        ) {
            // Month selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Month",
                        tint = MilkBlue
                    )
                }
                
                Text(
                    text = "${selectedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedMonth.year}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MilkBlue
                )
                
                IconButton(
                    onClick = { selectedMonth = selectedMonth.plusMonths(1) },
                    enabled = selectedMonth.isBefore(YearMonth.now())
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Month",
                        tint = if (selectedMonth.isBefore(YearMonth.now())) MilkBlue else Color.Gray
                    )
                }
            }
            
            // Summary cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Milk",
                    value = String.format("%.1f L", totalLiters),
                    backgroundColor = MilkBlueLight
                )
                
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Cost",
                    value = String.format("৳%.0f", totalCost),
                    backgroundColor = Success.copy(alpha = 0.15f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Additional stats
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$daysWithMilk",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MilkBlue
                        )
                        Text(
                            text = "Days with milk",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${entries.size}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MilkBlue
                        )
                        Text(
                            text = "Total entries",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("৳%.0f/L", pricePerLiter),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MilkBlue
                        )
                        Text(
                            text = "Price per liter",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Daily entries list
            Text(
                text = "Daily Entries",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No entries for this month",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entries.sortedByDescending { it.date }) { entry ->
                        DailyEntryItem(
                            entry = entry,
                            pricePerLiter = pricePerLiter,
                            onDeleteClick = { entryToDelete = entry }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog
    entryToDelete?.let { entry ->
        val date = LocalDate.parse(entry.date)
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Delete Entry") },
            text = {
                Text(
                    "Are you sure you want to delete the entry for ${date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))} (${entry.amountLiters}L)?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            milkRepository.deleteEntry(entry.date)
                        }
                        entryToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    backgroundColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MilkBlue
            )
        }
    }
}

@Composable
fun DailyEntryItem(
    entry: MilkEntry,
    pricePerLiter: Double,
    onDeleteClick: () -> Unit
) {
    val date = LocalDate.parse(entry.date)
    val cost = entry.amountLiters * pricePerLiter
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${entry.amountLiters}L",
                    style = MaterialTheme.typography.titleMedium,
                    color = MilkBlue,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = String.format("৳%.0f", cost),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
