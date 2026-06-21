package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun getBudgetsByMonth(monthYear: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudgetById(id: Int)
}

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts ORDER BY date DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity)

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteDebtById(id: Int)
}
