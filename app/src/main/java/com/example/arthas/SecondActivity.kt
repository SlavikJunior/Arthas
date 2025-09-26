package com.example.arthas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

class SecondActivity : ComponentActivity() {

    lateinit var value: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        value = if (intent.getStringExtra("value").isNullOrBlank())
            "Default value"
        else
            intent.getStringExtra("value") as String

        setContent {
            SetUpActivity()
        }
    }

    @Composable
    @Preview(showBackground = true, showSystemUi = true)
    fun SetUpActivity() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.66F)
            ) {
                Text(
                    text = value,
                    fontSize = 28.sp
                )

                Button(
                    onClick = {
                        val intent = Intent(this@SecondActivity, ThirdActivity::class.java)
                        intent.putExtra("value", value)
                        startActivity(intent)

                    },
                    content = {
                        Text(
                            text = getString(R.string.go_to_third_activity),
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                )

                Button(
                    onClick = {
                        val intent = Intent(this@SecondActivity, MainActivity::class.java)
                        intent.putExtra("value", value)
                        startActivity(intent)

                    },
                    content = {
                        Text(
                            text = getString(R.string.go_to_main_activity),
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                )
            }
        }
    }
}