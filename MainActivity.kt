package com.example.totp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TOTPApp()
        }
    }

    private fun copy(text: String) {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("TOTP", text))
    }

    @Composable
    fun TOTPApp() {

        var secret by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var secondsLeft by remember { mutableStateOf(30) }

        fun generate() {
            if (secret.isBlank()) return
            code = TOTP.generate(secret)
            secondsLeft = 30
        }

        LaunchedEffect(Unit) {
            while (true) {
                if (secret.isNotBlank()) {
                    code = TOTP.generate(secret)
                    secondsLeft = 30
                }

                repeat(30) {
                    delay(1000)
                    if (secondsLeft > 0) secondsLeft--
                }
            }
        }

        val progress = secondsLeft / 30f

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("TOTP Generator", style = MaterialTheme.typography.headlineMedium)

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = secret,
                    onValueChange = { secret = it },
                    label = { Text("Base64 Secret") },
                    singleLine = true
                )

                Spacer(Modifier.height(20.dp))

                TotpCircle(secondsLeft)

                Spacer(Modifier.height(10.dp))

                Text(
                    text = code,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(Modifier.height(20.dp))

                Row {

                    Button(onClick = { generate() }) {
                        Text("Generate")
                    }

                    Spacer(Modifier.width(10.dp))

                    Button(onClick = { copy(code) }) {
                        Text("Copy")
                    }
                }
            }
        }
    }
}
