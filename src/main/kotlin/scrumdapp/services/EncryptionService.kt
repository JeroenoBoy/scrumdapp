package com.jeroenvdg.scrumdapp.services

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.GCMParameterSpec

private const val saltLength = 10

interface EncryptionService {
    fun hashValue(value: String): String
    fun compareHash(value: String, hash: String): Boolean
    fun encryptString(value: String): String
    fun decryptString(value: String): String
}

class EncryptionServiceImpl(envService: EnvironmentService) : EncryptionService {
    val env = envService

    override fun hashValue(value: String): String {
        val salt = ByteArray(saltLength)
        SecureRandom().nextBytes(salt)
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)

        val hash = digest.digest(value.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(salt + hash)
    }

    override fun compareHash(value: String, hash: String): Boolean {
        val decoded = Base64.getDecoder().decode(hash)
        if (decoded.size != saltLength + 32) { return false }

        val oldHash = decoded.copyOfRange(saltLength, decoded.size)
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(decoded.copyOfRange(0, saltLength))

        return digest.digest(value.toByteArray(Charsets.UTF_8)).contentEquals(oldHash)
    }

    override fun encryptString(value: String): String {
        val iv = ByteArray(env.getVariable("GCM_IV_SIZE").toInt())
        SecureRandom().nextBytes(iv)
        val cypher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(env.getVariable("GCM_SPEC_SIZE").toInt(), iv)
        val key = SecretKeySpec(env.getVariable("AES_SECRET_KEY").toByteArray(Charsets.UTF_8).copyOf(16), "AES")
        cypher.init(Cipher.ENCRYPT_MODE, key, spec)
        return Base64.getEncoder().encodeToString(iv + cypher.doFinal(value.toByteArray(Charsets.UTF_8)))
    }

    override fun decryptString(value: String): String {
        val decoded = Base64.getDecoder().decode(value)
        val iv = decoded.copyOfRange(0, env.getVariable("GCM_IV_SIZE").toInt())
        val cypher = Cipher.getInstance("AES/GCM/NoPadding")
        val ciphertext = decoded.copyOfRange(env.getVariable("GCM_IV_SIZE").toInt(), decoded.size)
        val spec = GCMParameterSpec(env.getVariable("GCM_SPEC_SIZE").toInt(), iv)
        val key = SecretKeySpec(env.getVariable("AES_SECRET_KEY").toByteArray(Charsets.UTF_8).copyOf(16), "AES")
        cypher.init(Cipher.DECRYPT_MODE, key, spec)
        return cypher.doFinal(ciphertext).toString(Charsets.UTF_8)
    }
}


