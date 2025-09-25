package com.jeroenvdg.scrumdapp

import com.jeroenvdg.scrumdapp.models.UserTable.*
import com.jeroenvdg.scrumdapp.models.GroupsTable.*
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import scrumdapp.services.EnvironmentService

object Database {
    suspend fun Application.initializeDatabase(): Database {
        val env = dependencies.resolve<EnvironmentService>()
        val database = Database.connect(
            url = env.getVariable("DATABASE_URL"),
            driver = env.getVariable("DATABASE_DRIVER"),
            user = env.getVariable("DATABASE_USER"),
            password = env.getVariable("DATABASE_PASSWORD")
        )
        transaction (database) {
            SchemaUtils.create(Groups, UserGroups, GroupCheckins, GroupInvite, Users, UserSessions)
        }
        println("Database initialization complete!")
        return database
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T {
        return newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}