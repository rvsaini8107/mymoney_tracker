package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ColorExpense
import com.example.ui.theme.ColorIncome
import java.text.NumberFormat
import java.util.*

@Composable
fun ReportScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val rawTransactions by viewModel.transactions.collectAsState()
    val activeTransactions = viewModel.getActiveMonthTransactions(rawTransactions)

    val totalIncome = viewModel.getTotalIncome(rawTransactions)
    val totalExpense = viewModel.getTotalExpense(rawTransactions)
    val savings = (totalIncome - totalExpense).coerceAtLeast(0.0)
    val savingsRate = if (totalIncome > 0) (savings / totalIncome) * 100.0 else 0.0

    val currencyFormatter = remember {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format
    }

    // Group expenses by category
    val expensesByCategory = remember(activeTransactions) {
        activeTransactions
            .filter { it.type == "EXPENSE" }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
    }

    val totalExpensesSum = expensesByCategory.sumOf { it.second }

    // Assign beautiful, harmonious visual color arrays to each category
    val categoryColors = remember {
        mapOf(
            "Food" to Color(0xFFF87171),         // Warm Red-Pink
            "Shopping" to Color(0xFFFB923C),     // Soft Orange
            "Rent" to Color(0xFF60A5FA),         // Cool Sky Blue
            "Utilities" to Color(0xFF34D399),    // Jade Green
            "Travel / Fuel" to Color(0xFFA78BFA),// Pale Purple
            "Entertainment" to Color(0xFFF472B6),// Rose Pink
            "Others" to Color(0xFF94A3B8)        // Neutral Slate
        )
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
            // Heading titles
            Text(
                text = "Savings & Analytics (मासिक बचत रिपोर्ट)",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Understand your money streams, calculated for the active billing cycle.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- SAVINGS SCORECARD ROW ---
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("savings_scorecard"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Monthly Savings Status",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Text(
                                text = currencyFormatter.format(savings),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Savings Rate: " + String.format(Locale.getDefault(), "%.1f%%", savingsRate),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (savingsRate >= 20) ColorIncome else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action recommendation message based on savings percentages
                            val advice = when {
                                totalIncome == 0.0 -> "Record your salary or incomes to start analyzing your savings rates limits."
                                savingsRate >= 30.0 -> "Amazing speed! You are building local security at record levels. Keep it up! 🚀"
                                savingsRate >= 15.0 -> "Healthy savings rate! You are pacing towards safe financial cushions. 👍"
                                else -> "Savings are a bit low. Set stricter budgets on food/shopping lists to improve. ⚠️"
                            }

                            Text(
                                text = advice,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                }

                if (totalExpensesSum == 0.0) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(
                                modifier = Modifier.padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("📊", fontSize = 36.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        "No analytics available",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        "Add expense transactions to generate proportional breakdown reports.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // --- CANVAS ARC CHART ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Expense Distribution (खर्च विभाजन)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start
                                )
                                
                                Spacer(modifier = Modifier.height(20.dp))

                                // Render native canvas Segment Chart
                                Box(
                                    modifier = Modifier.size(160.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        var currentAngle = -90f
                                        expensesByCategory.forEach { (cat, amount) ->
                                            val sweepAngle = ((amount / totalExpensesSum) * 360f).toFloat()
                                            val color = categoryColors[cat] ?: Color(0xFF94A3B8)
                                            drawArc(
                                                color = color,
                                                startAngle = currentAngle,
                                                sweepAngle = sweepAngle,
                                                useCenter = false,
                                                style = Stroke(width = 24.dp.toPx()),
                                                size = Size(size.width - 24.dp.toPx(), size.height - 24.dp.toPx()),
                                                topLeft = Offset(12.dp.toPx(), 12.dp.toPx())
                                            )
                                            currentAngle += sweepAngle
                                        }
                                    }
                                    // Center indicators inside donut hole
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Total Spend", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = currencyFormatter.format(totalExpense),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }

                    // --- BREAKDOWN LIST ---
                    items(expensesByCategory) { (category, amount) ->
                        val percentage = (amount / totalExpensesSum) * 100
                        val color = categoryColors[category] ?: Color(0xFF94A3B8)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini Colored Dot
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = category,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = currencyFormatter.format(amount),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f%% of total", percentage),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
}
