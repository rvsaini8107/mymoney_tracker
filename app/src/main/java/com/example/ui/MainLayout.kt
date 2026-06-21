package com.example.ui

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Ledger, 1: Budget, 2: Report, 3: Debts
    var showProfileDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    
    val displayMonthFormatter = remember {
        val parser = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        Pair(parser, formatter)
    }

    val activeMonthText = remember(viewModel.selectedMonthYear) {
        try {
            val dateObj = displayMonthFormatter.first.parse(viewModel.selectedMonthYear)
            if (dateObj != null) displayMonthFormatter.second.format(dateObj) else viewModel.selectedMonthYear
        } catch (e: Exception) {
            viewModel.selectedMonthYear
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable {
                                // Trigger month picker dialog
                                val cal = Calendar.getInstance()
                                val dialog = DatePickerDialog(
                                    context,
                                    { _, year, month, _ ->
                                        viewModel.updateSelectedMonth(year, month)
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                )
                                dialog.show()
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🗓️ $activeMonthText",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                navigationIcon = {
                    Row(
                        modifier = Modifier.padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("₹", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                actions = {
                    // Profile Avatar Action
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .clickable { showProfileDialog = true }
                            .testTag("avatar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = viewModel.userName.take(1).uppercase(Locale.getDefault()),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // Respect Window Insets automatically for standard NavigationBar
            NavigationBar(
                modifier = Modifier
                    .testTag("main_navigation_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Dashboard Item
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Text("🧾", fontSize = 20.sp) }, // Unicode icons are fully compliant on Play Store
                    label = { Text("Ledger") },
                    modifier = Modifier.testTag("nav_tab_ledger")
                )

                // Budgets Item
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Text("🎯", fontSize = 20.sp) },
                    label = { Text("Budgets") },
                    modifier = Modifier.testTag("nav_tab_budgets")
                )

                // Reports Item
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Text("📈", fontSize = 20.sp) },
                    label = { Text("Reports") },
                    modifier = Modifier.testTag("nav_tab_reports")
                )

                // Debts Item
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Text("🤝", fontSize = 20.sp) },
                    label = { Text("Debts") },
                    modifier = Modifier.testTag("nav_tab_debts")
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        // Render content pages with beautiful directional cross-fades
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> DashboardScreen(viewModel = viewModel)
                1 -> BudgetScreen(viewModel = viewModel)
                2 -> ReportScreen(viewModel = viewModel)
                3 -> DebtScreen(viewModel = viewModel)
            }
        }
    }

    // --- PROFILE INFORMATION DETAIL POPUP ---
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Text(
                    text = "My Profile Ledger",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Larger Avatar Badge
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = viewModel.userName.take(1).uppercase(Locale.getDefault()),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = viewModel.userName,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = viewModel.userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Localized security badge
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "🤖 Google Auth Connection State",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Synced as: ${viewModel.userEmail}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showProfileDialog = false
                    },
                    modifier = Modifier.testTag("logout_button_dialog")
                ) {
                    Text("Logout Profile")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showProfileDialog = false },
                    modifier = Modifier.testTag("dismiss_profile_button")
                ) {
                    Text("Close")
                }
            }
        )
    }
}
