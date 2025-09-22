package scrumdapp.services

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

interface EnvironmentService {
    fun getVariable(value: String): String
}

class DotenvService() : EnvironmentService {
    val dotenv: Dotenv?
    init {
        dotenv = try {
            Dotenv.load()
        } catch (e: Exception) {
            println("Failed to load dotenv")
            null
        }
    }

    override fun getVariable(value: String): String {
        return dotenv?.get(value) ?: System.getenv(value) ?: throw Exception("Env variable '$value' not found")
    }
}