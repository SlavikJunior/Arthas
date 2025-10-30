package com.example.arthas.utils

private val passwordMinLength = 8
val passwordMask = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$"
private val emailMask = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"

fun isValidEmail(email: String) =
    email.matches(Regex(emailMask))

fun isValidPassword(password: String): Boolean {
    if (password.length < passwordMinLength)
        return false

    return password.matches(Regex(passwordMask))
}

fun isEmpty(input: String) =
    !(input.isNotBlank() && input.isNotEmpty())