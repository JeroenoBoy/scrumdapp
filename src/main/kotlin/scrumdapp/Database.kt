package com.jeroenvdg.scrumdapp

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object Database {
    fun initializeDatabase() {
        // TODO("Add postgres connection and add initializers for tables")
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T {
        return newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}