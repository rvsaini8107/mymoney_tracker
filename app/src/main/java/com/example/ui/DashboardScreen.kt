package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionEntity
import com.example.ui.theme.ColorExpense
import com.example.ui.theme.ColorIncome
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val rawTransactions by viewModel.transactions.collectAsState()
    val activeTransactions = viewModel.getActiveMonthTransactions(rawTransactions)

    val totalIncome = viewModel.getTotalIncome(rawTransactions)
    val totalExpense = viewModel.getTotalExpense(rawTransactions)
    val netBalance = totalIncome - totalExpense

    var showAddDialog by remember { mutableStateOf(false) }

    val currencyFormatter = remember {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- HEADER SUMMARY CARD ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 28.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Net Balance (इस महीने की बचत)",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = currencyFormatter.format(netBalance),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.testTag("net_balance_text")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Row of Income & Expense
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Income Summary Card
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("↑", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Income", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                Text(
                                    currencyFormatter.format(totalIncome),
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Expense Summary Card
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("↓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Expense", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                Text(
                                    currencyFormatter.format(totalExpense),
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // --- RECENT LEDGER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transactions (लेन-देन इतिहास)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "${activeTransactions.size} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            if (activeTransactions.isEmpty()) {
                // Beautiful Empty State Illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🧾",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No transactions this month!",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap the + button below to start tracking your daily spend.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeTransactions, key = { it.id }) { txn ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .testTag("transaction_item_${txn.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Category Icon Motif
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (txn.type == "INCOME") ColorIncome.copy(alpha = 0.15f)
                                            else ColorExpense.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val emoji = when (txn.category) {
                                        "Food" -> "🍔"
                                        "Shopping" -> "🛍️"
                                        "Rent" -> "🏠"
                                        "Utilities" -> "💡"
                                        "Travel / Fuel" -> "🚗"
                                        "Entertainment" -> "🍿"
                                        "Medical" -> "💊"
                                        "Salary" -> "💰"
                                        else -> "💸"
                                    }
                                    Text(emoji, fontSize = 22.sp)
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = txn.category,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (txn.note.isNotEmpty()) {
                                        Text(
                                            text = txn.note,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(txn.date)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                }

                                // Amount & Delete Action
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (txn.type == "INCOME") "+ ${currencyFormatter.format(txn.amount)}"
                                               else "- ${currencyFormatter.format(txn.amount)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (txn.type == "INCOME") ColorIncome else ColorExpense,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    IconButton(
                                        onClick = { viewModel.deleteTransaction(txn) },
                                        modifier = Modifier.size(36.dp).testTag("delete_txn_${txn.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete transaction",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(84.dp)) // Avoid floating FAB blocking
                    }
                }
            }
        }

        // --- FLOATING ACTION FAB ---
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_transaction_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Transaction")
        }
    }

    // --- ADD TRANSACTION DIALOG MODAL ---
    if (showAddDialog) {
        var inputAmount by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf("EXPENSE") } // "EXPENSE" or "INCOME"
        var selectedCategory by remember { mutableStateOf("Food") }
        var inputNote by remember { mutableStateOf("") }

        val categories = if (selectedType == "INCOME") listOf("Salary", "Others")
                         else listOf("Food", "Shopping", "Rent", "Utilities", "Travel / Fuel", "Entertainment", "Medical", "Others")

        // Sync category if selection changes
        LaunchedEffect(selectedType) {
            selectedCategory = if (selectedType == "INCOME") "Salary" else "Food"
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add Transaction",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Type Selector Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedType == "EXPENSE") ColorExpense else Color.Transparent)
                                .clickable { selectedType = "EXPENSE" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Expense",
                                color = if (selectedType == "EXPENSE") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedType == "INCOME") ColorIncome else Color.Transparent)
                                .clickable { selectedType = "INCOME" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Income",
                                color = if (selectedType == "INCOME") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Amount Text Input
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        label = { Text("Amount (राशि)") },
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("amount_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Category dropdown selection
                    Column {
                        Text(
                            "Category (श्रेणी)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Render category chips representing selections
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(categories) { cat ->
                                    val isSelected = selectedCategory == cat
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedCategory = cat },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val emoji = when (cat) {
                                                "Food" -> "🍔"
                                                "Shopping" -> "🛍️"
                                                "Rent" -> "🏠"
                                                "Utilities" -> "💡"
                                                "Travel / Fuel" -> "🚗"
                                                "Entertainment" -> "🍿"
                                                "Medical" -> "💊"
                                                "Salary" -> "💰"
                                                else -> "💸"
                                            }
                                            Text(emoji, modifier = Modifier.padding(end = 8.dp))
                                            Text(
                                                cat,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                                         else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Optional Notes Details
                    OutlinedTextField(
                        value = inputNote,
                        onValueChange = { inputNote = it },
                        label = { Text("Note / Remarks (कहाँ खर्च किया?)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = inputAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.addTransaction(
                                type = selectedType,
                                amount = amt,
                                category = selectedCategory,
                                date = System.currentTimeMillis(),
                                note = inputNote
                            )
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_transaction_button")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddDialog = false },
                    modifier = Modifier.testTag("cancel_transaction_button")
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
