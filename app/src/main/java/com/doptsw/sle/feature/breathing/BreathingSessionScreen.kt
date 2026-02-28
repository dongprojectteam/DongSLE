@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.doptsw.sle.feature.breathing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doptsw.sle.R

@Composable
fun BreathingSessionScreen(
    onBack: () -> Unit,
    onGoSetup: () -> Unit,
    onTutorial: () -> Unit
) {
    val vm: BreathingSessionViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()
    var showStopDialog by remember { mutableStateOf(false) }
    val soundPlayer = remember { BreathingSoundPlayer() }
    var lastSignal by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            soundPlayer.release()
            vm.stopSession()
        }
    }

    LaunchedEffect(uiState.edgeSignal, uiState.soundEnabled, uiState.phase) {
        if (uiState.soundEnabled && uiState.edgeSignal > 0 && uiState.edgeSignal != lastSignal) {
            soundPlayer.play(BreathingEngine.soundTypeForPhase(uiState.phase))
            lastSignal = uiState.edgeSignal
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.breathing_session_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text("<") } },
                actions = { TextButton(onClick = onTutorial) { Text("?") } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFF))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = phaseLabel(uiState.phase),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.breathing_remaining, uiState.remainingSecondsInPhase),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = stringResource(R.string.breathing_round_progress, uiState.currentRound, uiState.rounds),
                        color = Color(0xFF5B6574)
                    )
                    LinearProgressIndicator(
                        progress = { uiState.totalProgress },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            BreathingSquare(tickInCycle = uiState.tickInCycle)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = vm::togglePause) {
                    Text(if (uiState.isPaused) stringResource(R.string.breathing_resume) else stringResource(R.string.breathing_pause))
                }
                TextButton(onClick = vm::toggleSound) {
                    Text(
                        if (uiState.soundEnabled) stringResource(R.string.breathing_sound_on)
                        else stringResource(R.string.breathing_sound_off)
                    )
                }
                TextButton(onClick = { showStopDialog = true }) {
                    Text(stringResource(R.string.breathing_stop), color = Color(0xFFE55353))
                }
            }
        }
    }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            text = { Text(stringResource(R.string.breathing_stop_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showStopDialog = false
                    vm.stopSession()
                    onGoSetup()
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) { Text("취소") }
            }
        )
    }

    if (uiState.isCompleted) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.breathing_completed_title)) },
            text = { Text(stringResource(R.string.breathing_completed_message)) },
            confirmButton = {
                TextButton(onClick = { vm.restartSession() }) {
                    Text(stringResource(R.string.breathing_restart))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    vm.stopSession()
                    onGoSetup()
                }) {
                    Text(stringResource(R.string.breathing_go_setup))
                }
            }
        )
    }
}

@Composable
private fun BreathingSquare(tickInCycle: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "멈춤",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(bottom = 4.dp)
                )
                VerticalText(
                    text = "들이쉬기",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(start = 6.dp)
                )
                Text(
                    text = "멈춤",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(top = 4.dp)
                )
                VerticalText(
                    text = "내쉬기",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(end = 6.dp)
                )

                Canvas(modifier = Modifier.size(190.dp)) {
                    val stroke = 6.dp.toPx()
                    drawRect(
                        color = Color(0xFFB8C4E6),
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    val progress = BreathingEngine.cycleProgressTick(tickInCycle)
                    val point = BreathingEngine.perimeterPoint(progress)
                    val x = point.first * size.width
                    val y = point.second * size.height
                    drawCircle(
                        color = Color(0xFF2A67F8),
                        radius = 8.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalText(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        text.forEach { ch ->
            Text(
                text = ch.toString(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun phaseLabel(phase: BreathingPhase): String {
    return when (phase) {
        BreathingPhase.EXHALE -> stringResource(R.string.breathing_phase_exhale)
        BreathingPhase.HOLD_AFTER_EXHALE -> stringResource(R.string.breathing_phase_hold)
        BreathingPhase.INHALE -> stringResource(R.string.breathing_phase_inhale)
        BreathingPhase.HOLD_AFTER_INHALE -> stringResource(R.string.breathing_phase_hold)
    }
}
