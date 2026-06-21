package com.example.data

import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val database: AppDatabase) {
    val transactions: Flow<List<TransactionEntity>> = database.transactionDao().getAllTransactions()
    val budgets: Flow<List<BudgetEntity>> = database.budgetDao().getAllBudgets()
    val debts: Flow<List<DebtEntity>> = database.debtDao().getAllDebts()

    fun getBudgetsByMonth(monthYear: String): Flow<List<BudgetEntity>> {
        return database.budgetDao().getBudgetsByMonth(monthYear)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        database.transactionDao().insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        database.transactionDao().updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        database.transactionDao().deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Int) {
        database.transactionDao().deleteTransactionById(id)
    }

    suspend fun insertBudget(budget: BudgetEntity) {
        database.budgetDao().insertBudget(budget)
    }

    suspend fun updateBudget(budget: BudgetEntity) {
        database.budgetDao().updateBudget(budget)
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        database.budgetDao().deleteBudget(budget)
    }

    suspend fun deleteBudgetById(id: Int) {
        database.budgetDao().deleteBudgetById(id)
    }

    suspend fun insertDebt(debt: DebtEntity) {
        database.debtDao().insertDebt(debt)
    }

    suspend fun updateDebt(debt: DebtEntity) {
        database.debtDao().updateDebt(debt)
    }

    suspend fun deleteDebt(debt: DebtEntity) {
        database.debtDao().deleteDebt(debt)
    }

    suspend fun deleteDebtById(id: Int) {
        database.debtDao().deleteDebtById(id)
    }
}
