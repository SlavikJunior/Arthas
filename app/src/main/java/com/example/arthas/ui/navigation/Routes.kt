package com.example.arthas.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object CarsList : Routes("carslist")
    object Upload : Routes("upload")
    object Profile : Routes("profile")
    object Recovery : Routes("recovery/{${Args.EMAIL}}") {
        fun createRoute(email: String) = "recovery/$email"

        object Args {
            const val EMAIL = "email"
        }
    }
}
