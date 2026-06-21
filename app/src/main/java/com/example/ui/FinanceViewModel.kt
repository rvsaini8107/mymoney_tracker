package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = FinanceRepository(database)

    // User Profile State (Mock-Google Auth)
    var isLoggedIn by mutableStateOf(false)
        private set
    var userName by mutableStateOf("")
        private set
    var userEmail by mutableStateOf("")
        private set

    // Active Selection State
    var selectedMonthYear by mutableStateOf("")
        private set

    // Database Flows
    val transactions: StateFlow<List<TransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<BudgetEntity>> = repository.budgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debts: StateFlow<List<DebtEntity>> = repository.debts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Default active month configuration (e.g., "2026-06" to match current time)
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        selectedMonthYear = sdf.format(Date())
    }

    // Google Sign-In Simulation (Safe and Fully Offline Functional)
    fun ssoLogin(email: String, name: String) {
        viewModelScope.launch {
            userName = name
            userEmail = email
            isLoggedIn = true
            
            // Seed base categories in budget database if first launch
            insertSeedBudgetsIfNeeded()
        }
    }

    fun logout() {
        isLoggedIn = false
        userName = ""
        userEmail = ""
    }

    fun updateSelectedMonth(year: Int, month: Int) {
        // month is 0-indexed
        selectedMonthYear = String.format(Locale.getDefault(), "%04d-%02d", year, month + 1)
    }

    private suspend fun insertSeedBudgetsIfNeeded() {
        val currentBudgets = budgets.value
        if (currentBudgets.isEmpty()) {
            val defaultCategories = listOf("Food", "Shopping", "Rent", "Travel / Fuel", "Entertainment", "Others")
            defaultCategories.forEach { cat ->
                val baseLimit = when (cat) {
                    "Food" -> 5000.0
                    "Shopping" -> 3000.0
                    "Rent" -> 10000.0
                    "Travel / Fuel" -> 2000.0
                    "Entertainment" -> 1500.0
                    else -> 2000.0
                }
                repository.insertBudget(
                    BudgetEntity(
                        category = cat,
                        limitAmount = baseLimit,
                        monthYear = selectedMonthYear
                    )
                )
            }
        }
    }

    // --- TRANSACTIONS ---
    fun addTransaction(type: String, amount: Double, category: String, date: Long, note: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    type = type,
                    amount = amount,
                    category = category,
                    date = date,
                    note = note
                )
            )
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // --- BUDGETS ---
    fun addOrUpdateBudget(category: String, limitAmount: Double) {
        viewModelScope.launch {
            val existing = budgets.value.find { it.category == category && it.monthYear == selectedMonthYear }
            if (existing != null) {
                repository.updateBudget(existing.copy(limitAmount = limitAmount))
            } else {
                repository.insertBudget(
                    BudgetEntity(
                        category = category,
                        limitAmount = limitAmount,
                        monthYear = selectedMonthYear
                    )
                )
            }
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    // --- DEBTS (Udaar Tracker) ---
    fun addDebt(personName: String, amount: Double, type: String, note: String, date: Long) {
        viewModelScope.launch {
            repository.insertDebt(
                DebtEntity(
                    personName = personName,
                    amount = amount,
                    type = type,
                    date = date,
                    note = note,
                    isSettled = false
                )
            )
        }
    }

    fun toggleSettleDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.updateDebt(debt.copy(isSettled = !debt.isSettled))
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    // --- CALCULATIONS FOR ACTIVE MONTH ---
    // Returns transactions falling into the selectedMonthYear
    fun getActiveMonthTransactions(list: List<TransactionEntity>): List<TransactionEntity> {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return list.filter {
            val dateStr = sdf.format(Date(it.date))
            dateStr == selectedMonthYear
        }
    }

    // Total Income inside Selected Month
    fun getTotalIncome(list: List<TransactionEntity>): Double {
        return getActiveMonthTransactions(list)
            .filter { it.type == "INCOME" }
            .sumOf { it.amount }
    }

    // Total Expense inside Selected Month
    fun getTotalExpense(list: List<TransactionEntity>): Double {
        return getActiveMonthTransactions(list)
            .filter { it.type == "EXPENSE" }
            .sumOf { it.amount }
    }
}
