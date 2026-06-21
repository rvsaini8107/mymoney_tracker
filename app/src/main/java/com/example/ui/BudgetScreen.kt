package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import com.example.data.BudgetEntity
import com.example.ui.theme.ColorExpense
import com.example.ui.theme.ColorIncome
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val budgets by viewModel.budgets.collectAsState()
    val rawTransactions by viewModel.transactions.collectAsState()
    
    val activeTransactions = viewModel.getActiveMonthTransactions(rawTransactions)
    val activeBudgets = budgets.filter { it.monthYear == viewModel.selectedMonthYear }

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedCategoryForEdit by remember { mutableStateOf("") }
    var selectedBudgetLimitInput by remember { mutableStateOf("") }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Heading
            Text(
                text = "Budget Planner (बजट नियंत्रण)",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Set monthly expense caps to keep your pockets full. Color guides represent spending risk.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Let's create an onboarding visual summary when budgets are empty
            val categoriesToManage = listOf("Food", "Shopping", "Rent", "Travel / Fuel", "Entertainment", "Others")

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categoriesToManage) { cat ->
                    val budget = activeBudgets.find { it.category == cat }
                    val budgetLimit = budget?.limitAmount ?: 0.0
                    
                    // Sum actual transactions of the month on this Category
                    val actualExpense = activeTransactions
                        .filter { it.type == "EXPENSE" && it.category == cat }
                        .sumOf { it.amount }

                    val percentUsed = if (budgetLimit > 0.0) (actualExpense / budgetLimit) else 0.0

                    // Color indicators based on threshold levels
                    val statusColor = when {
                        percentUsed >= 1.0 -> ColorExpense
                        percentUsed >= 0.75 -> Color(0xFFF59E0B) // Amber
                        else -> ColorIncome
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCategoryForEdit = cat
                                selectedBudgetLimitInput = if (budgetLimit > 0.0) budgetLimit.toInt().toString() else "5000"
                                showEditDialog = true
                            }
                            .testTag("budget_item_$cat"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Row of title / limit / edit action
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val emoji = when (cat) {
                                        "Food" -> "🍔"
                                        "Shopping" -> "🛍️"
                                        "Rent" -> "🏠"
                                        "Travel / Fuel" -> "🚗"
                                        "Entertainment" -> "🍿"
                                        else -> "💸"
                                    }
                                    Text(emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = cat,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (budgetLimit > 0) currencyFormatter.format(budgetLimit) else "No Limit set",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = if (budgetLimit > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit budget",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Actual Spent Indicators
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Spent: ${currencyFormatter.format(actualExpense)}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (budgetLimit > 0) {
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f%% Used", percentUsed * 100),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Custom colored progress slider bar representation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                val progressFraction = percentUsed.coerceAtMost(1.0).toFloat()
                                if (progressFraction > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(progressFraction)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(statusColor)
                                    )
                                }
                            }

                            // Dynamic Warning Tips
                            if (budgetLimit > 0.0 && percentUsed >= 1.0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Budget critical",
                                        tint = ColorExpense,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Oops! Category overspent. Settle other lists down.",
                                        fontSize = 11.sp,
                                        color = ColorExpense,
                                        fontWeight = FontWeight.SemiBold
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

    // --- DIALOG MODAL FOR EDITING BUDGET ---
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Configure $selectedCategoryForEdit Limit",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Specify the monthly limits configuration you want for the category.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = selectedBudgetLimitInput,
                        onValueChange = { selectedBudgetLimitInput = it },
                        label = { Text("Limit Amount (बजट सीमा)") },
                        placeholder = { Text("e.g. 5000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("budget_limit_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val limitAmnt = selectedBudgetLimitInput.toDoubleOrNull() ?: 0.0
                        if (limitAmnt > 0) {
                            viewModel.addOrUpdateBudget(
                                category = selectedCategoryForEdit,
                                limitAmount = limitAmnt
                            )
                            showEditDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_budget_button")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false },
                    modifier = Modifier.testTag("cancel_budget_button")
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
