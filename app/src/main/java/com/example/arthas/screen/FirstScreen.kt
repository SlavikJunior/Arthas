package com.example.arthas.screen

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arthas.R
import com.example.arthas.navigation.NavRoute
import com.example.arthas.utils.isEmpty
import com.example.arthas.utils.isValidEmail
import com.example.arthas.utils.isValidPassword

@Composable
fun FirstScreen(navController: NavController) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        var email by rememberSaveable { mutableStateOf("") }
        var emailHasError by rememberSaveable { mutableStateOf(false) }
        var password by rememberSaveable { mutableStateOf("") }
        var passwordHasError by rememberSaveable { mutableStateOf(false) }
        var passwordIsVisible by rememberSaveable { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = email,
                onValueChange = {
                    emailHasError = false
                    email = it
                },
                label = { Text(text = stringResource(R.string.email_text_field_hint)) },
                singleLine = true,
                modifier = Modifier
                    .contentType(ContentType.EmailAddress)
                    .border(2.dp, if (emailHasError) Color.Red else Color.DarkGray),
            )

            TextField(
                value = password,
                onValueChange = {
                    passwordHasError = false
                    password = it
                },
                label = { Text(text = stringResource(R.string.password_text_field_hint)) },
                singleLine = true,
                modifier = Modifier
                    .contentType(ContentType.Password)
                    .border(2.dp, if (passwordHasError) Color.Red else Color.DarkGray),
                visualTransformation = if (passwordIsVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordIsVisible)
                        Icons.Sharp.Close
                    else
                        Icons.Sharp.Check
                    IconButton(onClick = { passwordIsVisible = !passwordIsVisible }) {
                        Icon(image, null)
                    }
                }
            )
        }

        Button(
            onClick = {
                val emailEmpty = isEmpty(email)
                val passwordEmpty = isEmpty(password)
                val emailValid = isValidEmail(email)
                val passwordValid = isValidPassword(password)
                emailHasError = false
                passwordHasError = false

                fun checkAndNavigate() {
                    when {
                        emailEmpty -> {
                            emailHasError = true
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_email_empty),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        passwordEmpty -> {
                            passwordHasError = true
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_password_empty),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        !emailValid -> {
                            emailHasError = true
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_email_invalid),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        !passwordValid -> {
                            passwordHasError = true
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_password_invalid),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            navController?.navigate(NavRoute.SecondScreen.destination + "/$email")
                        }
                    }
                }
                checkAndNavigate()
            },
            content = {
                Text(text = stringResource(R.string.btn_navigate_to_the_second_screen))
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(64.dp)
        )
    }
}