package com.example.arthas.util

// import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

class Argon2Hasher {

    /*
    private val encoder = Argon2PasswordEncoder(
        16, // длина соли - 16 байт
        32, // длина хеша - 32 байта
        1, // параллелизм - 1 поток
        60000, // память - 60_000 КБ
        3 // итерации - 3
    )
    */

    fun hash(password: String): String {
        // return encoder.encode(password)
        return password.hashCode().toString()
    }

    fun verify(password: String, hashedPassword: String): Boolean {
        // return encoder.matches(password, hashedPassword)
        return password.hashCode().toString() == hashedPassword
    }

}