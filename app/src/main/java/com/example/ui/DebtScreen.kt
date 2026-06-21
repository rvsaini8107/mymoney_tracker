package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DebtEntity
import com.example.ui.theme.ColorBorrow
import com.example.ui.theme.ColorExpense
import com.example.ui.theme.ColorIncome
import com.example.ui.theme.ColorLend
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val debts by viewModel.debts.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val currencyFormatter = remember {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format
    }

    // Calculations
    val activeDebts = debts.filter { !it.isSettled }
    val totalBorrowed = activeDebts.filter { it.type == "BORROWED" }.sumOf { it.amount }
    val totalLent = activeDebts.filter { it.type == "LENT" }.sumOf { it.amount }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- TITLE & EXPLANATION ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "Lend & Borrow (उधार - Udaar Ledger)",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Track funds borrowed from or lent to acquaintances. Tap checkmark to mark as repaid/reclaimed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // --- DOUBLE DEBT HIGHLIGHT CARD ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("debt_indicators_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Borrowed Card Column (Liability)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Borrowed (उधार लिया)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorBorrow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormatter.format(totalBorrowed),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("total_borrowed_text")
                        )
                        Text(
                            text = "Repay status pending",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    // Separation Line
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp)
                    )

                    // Lent Card Column (Asset)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = "Lent (उधार दिया)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorLend
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormatter.format(totalLent),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("total_lent_text")
                        )
                        Text(
                            text = "Collection outstanding",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // --- DEBT LIST REACTION ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Ledgers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "${debts.size} records total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            if (debts.isEmpty()) {
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
                        Text("🤝", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No debts or borrowings yet!",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Tap the + button to record who you borrowed from or lent money to.",
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
                    items(debts, key = { it.id }) { debt ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .testTag("debt_item_${debt.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (debt.isSettled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                                 else MaterialTheme.colorScheme.surface
                            ),
                            border = if (debt.isSettled) null else BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                            elevation = if (debt.isSettled) CardDefaults.cardElevation(defaultElevation = 0.dp)
                                        else CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Handshape motif colorized based on status
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (debt.isSettled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                            else if (debt.type == "BORROWED") ColorBorrow.copy(alpha = 0.15f)
                                            else ColorLend.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val emoji = if (debt.isSettled) "✅"
                                                else if (debt.type == "BORROWED") "📥" // took money
                                                else "📤" // gave money
                                    Text(emoji, fontSize = 20.sp)
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = debt.personName,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = if (debt.isSettled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                                     else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        // Borrowed vs Lent pill tag
                                        val tagText = if (debt.type == "BORROWED") "Borrowed" else "Lent"
                                        val tagColor = if (debt.type == "BORROWED") ColorBorrow else ColorLend
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(tagColor.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = tagText,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = tagColor
                                            )
                                        }
                                    }

                                    if (debt.note.isNotEmpty()) {
                                        Text(
                                            text = debt.note,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            maxLines = 1
                                        )
                                    }

                                    Text(
                                        text = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date(debt.date)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                                    )
                                }

                                // Actions (Settle trigger / Delete trigger)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = currencyFormatter.format(debt.amount),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = if (debt.isSettled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                               else if (debt.type == "BORROWED") ColorBorrow
                                               else ColorLend,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    // Mark Settle Check Toggle
                                    IconButton(
                                        onClick = { viewModel.toggleSettleDebt(debt) },
                                        modifier = Modifier.size(36.dp).testTag("settle_debt_${debt.id}")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (debt.isSettled) ColorIncome 
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Toggle settle state",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    // Delete record 
                                    IconButton(
                                        onClick = { viewModel.deleteDebt(debt) },
                                        modifier = Modifier.size(36.dp).testTag("delete_debt_${debt.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete record",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(84.dp))
                    }
                }
            }
        }

        // --- ADD DEBT FAB ACTION ---
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_debt_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Debt log")
        }
    }

    // --- FORM DIALOG TO ADD NEW UDAAR ---
    if (showAddDialog) {
        var inputName by remember { mutableStateOf("") }
        var inputAmount by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf("BORROWED") } // "BORROWED" or "LENT"
        var inputNote by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add Borrow/Lend Log",
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
                    // Type Selector Tabs (Borrowed / Lent)
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
                                .background(if (selectedType == "BORROWED") ColorBorrow else Color.Transparent)
                                .clickable { selectedType = "BORROWED" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Udaar Liya (Borrowed)",
                                color = if (selectedType == "BORROWED") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedType == "LENT") ColorLend else Color.Transparent)
                                .clickable { selectedType = "LENT" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Udaar Diya (Lent)",
                                color = if (selectedType == "LENT") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Person Text Input
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = { Text("Person Name (किसका?)") },
                        placeholder = { Text("e.g. Rahul, Suresh") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("debt_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Amount Text Input
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        label = { Text("Amount (राशि)") },
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("debt_amount_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Optional Notes Details
                    OutlinedTextField(
                        value = inputNote,
                        onValueChange = { inputNote = it },
                        label = { Text("Remarks (उद्देश्य - e.g. Lunch loan, Rent)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("debt_note_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = inputAmount.toDoubleOrNull() ?: 0.0
                        if (inputName.trim().isNotEmpty() && amt > 0) {
                            viewModel.addDebt(
                                personName = inputName.trim(),
                                amount = amt,
                                type = selectedType,
                                note = inputNote,
                                date = System.currentTimeMillis()
                            )
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_debt_button")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddDialog = false },
                    modifier = Modifier.testTag("cancel_debt_button")
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
