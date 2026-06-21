package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "INCOME" or "EXPENSE"
    val amount: Double,
    val category: String,
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val limitAmount: Double,
    val monthYear: String // For example: "2026-06"
)

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personName: String,
    val amount: Double,
    val type: String, // "BORROWED" (Udaar Liya) or "LENT" (Udaar Diya)
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val isSettled: Boolean = false
)
