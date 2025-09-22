package com.jeroenvdg.scrumdapp

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object Database {
    fun initializeDatabase(): Database {
        return Database.connect(
            url = "",
            user = "root",
            driver = "org.sqlite.JDBC",
            password = ""
        )
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T {
        return newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}