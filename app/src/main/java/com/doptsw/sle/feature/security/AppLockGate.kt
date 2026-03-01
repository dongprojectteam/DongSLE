package com.doptsw.sle.feature.security

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.security.MessageDigest

private const val PREF_NAME = "sle_lock_pref"
private const val KEY_PIN_HASH = "pin_hash"

@Composable
fun AppLockGate(
    onUnlocked: () -> Unit
) {
    val context = LocalContext.current
    var savedHash by remember { mutableStateOf(loadSavedPinHash(context)) }
    var pin by rememberSaveable { mutableStateOf("") }
    var confirmPin by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val isSetupMode = savedHash == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFF4F7FF), Color(0xFFFDFEFF))))
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isSetupMode) "앱 비밀번호 설정" else "비밀번호 입력",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isSetupMode) {
                        "처음 실행이므로 4자리 숫자 비밀번호를 만들어주세요."
                    } else {
                        "앱을 열려면 4자리 숫자 비밀번호가 필요합니다."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5F6C80)
                )
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        pin = it.filter(Char::isDigit).take(4)
                        errorMessage = null
                    },
                    label = { Text(if (isSetupMode) "새 비밀번호 (4자리)" else "비밀번호 (4자리)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isSetupMode) {
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = {
                            confirmPin = it.filter(Char::isDigit).take(4)
                            errorMessage = null
                        },
                        label = { Text("비밀번호 확인") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Text(
                    text = "주의: 비밀번호를 잊어버리면 절대 복구할 수 없습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB42318)
                )
                errorMessage?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB42318)
                    )
                }
                Button(
                    onClick = {
                        if (isSetupMode) {
                            when {
                                pin.length != 4 || confirmPin.length != 4 -> {
                                    errorMessage = "비밀번호는 4자리 숫자로 입력해주세요."
                                }

                                pin != confirmPin -> {
                                    errorMessage = "비밀번호가 일치하지 않습니다."
                                }

                                else -> {
                                    val hash = hashPin(pin)
                                    savePinHash(context, hash)
                                    savedHash = hash
                                    pin = ""
                                    confirmPin = ""
                                    errorMessage = null
                                    onUnlocked()
                                }
                            }
                        } else {
                            if (pin.length != 4) {
                                errorMessage = "비밀번호는 4자리 숫자여야 합니다."
                                return@Button
                            }
                            if (hashPin(pin) == savedHash) {
                                pin = ""
                                errorMessage = null
                                onUnlocked()
                            } else {
                                errorMessage = "비밀번호가 일치하지 않습니다."
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isSetupMode) "비밀번호 저장" else "잠금 해제")
                }
            }
        }
        Text(
            text = "SLE는 보안을 위해 앱 실행 시 비밀번호 확인을 요구합니다.",
            modifier = Modifier.padding(top = 14.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF7A879A),
            textAlign = TextAlign.Center
        )
    }
}

private fun loadSavedPinHash(context: Context): String? {
    return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_PIN_HASH, null)
}

private fun savePinHash(context: Context, hash: String) {
    context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(KEY_PIN_HASH, hash).apply()
}

private fun hashPin(pin: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
    return digest.joinToString(separator = "") { "%02x".format(it) }
}
