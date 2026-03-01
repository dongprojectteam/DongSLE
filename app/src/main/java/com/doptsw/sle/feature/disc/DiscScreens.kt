@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.doptsw.sle.feature.disc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doptsw.sle.R
import com.doptsw.sle.data.repository.DiscResultRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiscHomeScreen(
    onBack: () -> Unit,
    onStartDiagnosis: () -> Unit,
    onTutorial: () -> Unit,
    onOpenResult: (Long) -> Unit
) {
    val vm: DiscHomeViewModel = viewModel()
    val recent by vm.recentResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.disc_home_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text("<") } },
                actions = { TextButton(onClick = onTutorial) { Text("?") } }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ActionCard(
                    title = stringResource(R.string.disc_start_diagnosis),
                    subtitle = stringResource(R.string.disc_start_diagnosis_subtitle),
                    icon = Icons.Rounded.RocketLaunch,
                    tint = Color(0xFF2668E8),
                    onClick = onStartDiagnosis
                )
            }
            item {
                Text(
                    text = stringResource(R.string.disc_recent_results),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (recent.isEmpty()) {
                item {
                    ElevatedCard {
                        Text(
                            text = stringResource(R.string.disc_recent_empty),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            } else {
                items(recent) { result ->
                    RecentResultCard(result = result, onClick = { onOpenResult(result.id) })
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(tint.copy(alpha = 0.14f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color(0xFF647186), modifier = Modifier.padding(top = 3.dp))
            }
        }
    }
}

@Composable
private fun RecentResultCard(result: DiscResultRecord, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "유형: ${result.topTypes.joinToString("")}형",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text("점수 D:${result.d} I:${result.i} S:${result.s} C:${result.c}", color = Color(0xFF5E6A7C))
            Text(formatDiscDate(result.createdAt), color = Color(0xFF7C8798), style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun DiscQuestionScreen(
    onBack: () -> Unit,
    onMainMenu: () -> Unit,
    onCompleted: (Long) -> Unit
) {
    val vm: DiscQuestionViewModel = viewModel()
    val state = vm.uiState
    val question = state.currentQuestion
    val answer = state.currentAnswer

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.disc_question_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text("<") } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFF5F8FF), Color(0xFFFDFEFF))))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${state.currentIndex + 1}/${state.questions.size}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
            ElevatedCard(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = stringResource(R.string.disc_question_prompt, question.id), fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.disc_most_label), color = Color(0xFF1D62E2), fontWeight = FontWeight.Medium)
                    question.options.forEachIndexed { index, option ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = answer.mostIndex == index, onClick = { vm.selectMost(index) })
                            Text(text = option.word)
                        }
                    }
                    Text(stringResource(R.string.disc_least_label), color = Color(0xFFCC4E24), fontWeight = FontWeight.Medium)
                    question.options.forEachIndexed { index, option ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = answer.leastIndex == index, onClick = { vm.selectLeast(index) })
                            Text(text = option.word)
                        }
                    }
                    state.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = vm::goPrevious, enabled = state.canGoPrevious) {
                    Text(stringResource(R.string.disc_prev))
                }
                TextButton(onClick = onMainMenu) {
                    Icon(Icons.Rounded.Home, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text(stringResource(R.string.disc_main_menu))
                }
                TextButton(onClick = { vm.nextOrSubmit(onCompleted) }, enabled = state.canGoNext) {
                    Text(if (state.isLastQuestion) stringResource(R.string.disc_finish) else stringResource(R.string.disc_next))
                }
            }
        }
    }
}

@Composable
fun DiscResultScreen(
    onBack: () -> Unit,
    onRestart: () -> Unit,
    onMainMenu: () -> Unit
) {
    val vm: DiscResultViewModel = viewModel()
    val record by vm.uiState.collectAsState()
    val state = vm.buildUiState(record)
    val item = state.record
    val interpretation = state.interpretation

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.disc_result_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text("<") } }
            )
        }
    ) { innerPadding ->
        if (item == null || interpretation == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.disc_loading))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFEEF5FF))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "결과 유형: ${interpretation.typeCode}형",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = interpretation.title, color = Color(0xFF304B72))
                    Text(text = interpretation.summary, color = Color(0xFF4A5C77))
                }
            }

            ScoreBarRow("D", item.d, Color(0xFFE05D4C))
            ScoreBarRow("I", item.i, Color(0xFFF39C39))
            ScoreBarRow("S", item.s, Color(0xFF3B9D7D))
            ScoreBarRow("C", item.c, Color(0xFF4B74E0))

            TypeIconRow(interpretation.relatedTypes)

            ElevatedCard {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("강점", fontWeight = FontWeight.SemiBold)
                    Text(interpretation.strengths)
                    Text("주의할 점", fontWeight = FontWeight.SemiBold)
                    Text(interpretation.cautions)
                    Text("해석", fontWeight = FontWeight.SemiBold)
                    Text(interpretation.detail)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onRestart) { Text(stringResource(R.string.disc_restart)) }
                TextButton(onClick = onMainMenu) { Text(stringResource(R.string.disc_main_menu)) }
            }
        }
    }
}

@Composable
private fun ScoreBarRow(label: String, score: Int, color: Color) {
    val maxAbs = DiscQuestionBank.questions.size
    val totalRange = maxAbs * 2
    val progress = ((score + maxAbs).coerceIn(0, totalRange)) / totalRange.toFloat()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("$label : $score", fontWeight = FontWeight.SemiBold)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = color,
            trackColor = color.copy(alpha = 0.18f)
        )
    }
}

@Composable
private fun TypeIconRow(types: List<DiscType>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        types.forEach { type ->
            val pair = when (type) {
                DiscType.D -> Icons.Rounded.Bolt to Color(0xFFE06D52)
                DiscType.I -> Icons.Rounded.Groups to Color(0xFFF39C39)
                DiscType.S -> Icons.Rounded.Handshake to Color(0xFF3B9D7D)
                DiscType.C -> Icons.Rounded.Analytics to Color(0xFF4A73DD)
            }
            Card(colors = CardDefaults.cardColors(containerColor = pair.second.copy(alpha = 0.12f))) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(pair.first, contentDescription = null, tint = pair.second)
                    Text(type.name, modifier = Modifier.padding(start = 6.dp), color = pair.second, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

private fun formatDiscDate(ts: Long): String {
    return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(Date(ts))
}
