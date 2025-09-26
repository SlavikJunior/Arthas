package com.example.arthas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

class ThirdActivity : ComponentActivity() {

    lateinit var value: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
                        var intent = Intent(this@ThirdActivity, MainActivity::class.java)
                        intent.putExtra("value", value)
//                        intent = intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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