@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.doptsw.sle.feature.breathing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.doptsw.sle.R

@Composable
fun BreathingTutorialScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.breathing_tutorial_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text("<") } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF1F2A44))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        stringResource(R.string.breathing_tutorial_heading),
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TutorialTag("Mindful Breathing")
                        TutorialTag("HRV")
                        TutorialTag("ACT")
                    }
                }
            }

            QuoteHighlight("감정이 올라온 순간, 호흡에 주의를 고정해 거리두기를 만듭니다.")

            TutorialParagraph(stringResource(R.string.breathing_tutorial_intro_1))
            TutorialParagraph(stringResource(R.string.breathing_tutorial_intro_2))
            TutorialParagraph(stringResource(R.string.breathing_tutorial_intro_3))
            TutorialParagraph(stringResource(R.string.breathing_tutorial_intro_4))
            TutorialParagraph(stringResource(R.string.breathing_tutorial_intro_5))

            Text(
                stringResource(R.string.breathing_tutorial_howto_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TutorialStep("STEP 1", stringResource(R.string.breathing_tutorial_step_1))
            TutorialStep("STEP 2", stringResource(R.string.breathing_tutorial_step_2))
            TutorialStep("STEP 3", stringResource(R.string.breathing_tutorial_step_3))
            TutorialStep("STEP 4", stringResource(R.string.breathing_tutorial_step_4))
            TutorialParagraph(stringResource(R.string.breathing_tutorial_outro))
        }
    }
}

@Composable
private fun TutorialTag(text: String) {
    Text(
        text = text,
        color = Color(0xFFDDE8FF),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0x263F7CFF))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

@Composable
private fun QuoteHighlight(text: String) {
    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFEEF5FF))
    ) {
        Text(
            text = "\"$text\"",
            modifier = Modifier.padding(14.dp),
            color = Color(0xFF20476A),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TutorialParagraph(text: String) {
    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF6F8FC))
    ) {
        Text(text = text, modifier = Modifier.padding(14.dp), color = Color(0xFF273142))
    }
}

@Composable
private fun TutorialStep(step: String, text: String) {
    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = step,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF2A67F8),
                fontWeight = FontWeight.SemiBold
            )
            Text(text = text, color = Color(0xFF273142))
        }
    }
}
