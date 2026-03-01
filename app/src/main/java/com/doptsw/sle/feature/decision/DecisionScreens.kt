@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.doptsw.sle.feature.decision

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doptsw.sle.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DecisionEntryScreen(
    onBack: () -> Unit,
    onNavigateNew: () -> Unit,
    onNavigateList: () -> Unit
) {
    val vm: DecisionEntryViewModel = viewModel()
    val count by vm.count.collectAsState()
    var routed by remember { mutableStateOf(false) }

    LaunchedEffect(count, routed) {
        if (!routed && count >= 0) {
            routed = true
            if (count == 0) onNavigateNew() else onNavigateList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.decision_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text("<") } }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.decision_loading))
        }
    }
}

@Composable
fun DecisionListScreen(
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onOpen: (Long) -> Unit,
    onTutorial: () -> Unit
) {
    val vm: DecisionListViewModel = viewModel()
    val records by vm.records.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.decision_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text("<") } },
                actions = { TextButton(onClick = onTutorial) { Text("?") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate) { Text("+") }
        }
    ) { innerPadding ->
        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.decision_empty))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(records) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpen(record.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(record.titleText(), fontWeight = FontWeight.SemiBold)
                            Text(formatDate(record.updatedAt), color = Color(0xFF647186))
                            Text(record.conclusion.take(80), modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DecisionViewScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onTutorial: () -> Unit
) {
    val vm: DecisionViewViewModel = viewModel()
    val record by vm.record.collectAsState()
    var showDelete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.decision_view_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text("<") } },
                actions = { TextButton(onClick = onTutorial) { Text("?") } }
            )
        }
    ) { innerPadding ->
        val item = record
        if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { Text(stringResource(R.string.decision_loading)) }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(item.titleText(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            AlternatingReasonBlock(
                reasonsA = item.reasonsA,
                reasonsB = item.reasonsB
            )
            Text("그래서 결론은", fontWeight = FontWeight.SemiBold, color = Color(0xFF2A67F8))
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFEEF5FF))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(item.conclusion, modifier = Modifier.padding(top = 6.dp))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { onEdit(item.id) }) { Text(stringResource(R.string.action_edit)) }
                TextButton(onClick = { showDelete = true }) { Text(stringResource(R.string.action_delete)) }
            }
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            text = { Text(stringResource(R.string.decision_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showDelete = false
                    vm.delete(onDone = onBack)
                }) { Text(stringResource(R.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
fun DecisionEditScreen(
    onBack: () -> Unit,
    onSaved: (Long, Boolean) -> Unit,
    onTutorial: () -> Unit
) {
    val vm: DecisionEditViewModel = viewModel()
    val state = vm.uiState
    val reasonRounds = maxOf(state.reasonsA.size, state.reasonsB.size)
    val filledA = state.reasonsA.count { it.trim().isNotEmpty() }
    val filledB = state.reasonsB.count { it.trim().isNotEmpty() }
    val nextTargetLabel = if (state.nextTarget == ReasonTarget.A) {
        stringResource(R.string.decision_target_a)
    } else {
        stringResource(R.string.decision_target_b)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.id == null) stringResource(R.string.decision_edit_new_title)
                        else stringResource(R.string.decision_edit_edit_title)
                    )
                },
                navigationIcon = { TextButton(onClick = onBack) { Text("<") } },
                actions = { TextButton(onClick = onTutorial) { Text("?") } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = state.optionA,
                onValueChange = vm::updateOptionA,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.decision_option_a_label)) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
            OutlinedTextField(
                value = state.optionB,
                onValueChange = vm::updateOptionB,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.decision_option_b_label)) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Text(stringResource(R.string.decision_reasons_section), fontWeight = FontWeight.SemiBold)
            Text(
                text = stringResource(R.string.decision_reasons_hint),
                color = Color(0xFF5E6A7C)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFF))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.decision_progress_label), fontWeight = FontWeight.SemiBold)
                    Text(
                        stringResource(
                            R.string.decision_progress_text,
                            filledA,
                            filledB,
                            DecisionValidation.MIN_REQUIRED_REASONS
                        )
                    )
                    Text(
                        stringResource(R.string.decision_next_target, nextTargetLabel),
                        color = Color(0xFF2A67F8),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            for (i in 0 until reasonRounds) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.decision_round_label, i + 1),
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2A67F8)
                        )
                        if (i < state.reasonsA.size) {
                            OutlinedTextField(
                                value = state.reasonsA[i],
                                onValueChange = {
                                    vm.updateReason(
                                        DecisionReasonItem(ReasonTarget.A, i, (i * 2) + 1),
                                        it
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("A 를 선택하는 이유 #${i + 1}") }
                            )
                        }
                        if (i < state.reasonsB.size) {
                            OutlinedTextField(
                                value = state.reasonsB[i],
                                onValueChange = {
                                    vm.updateReason(
                                        DecisionReasonItem(ReasonTarget.B, i, (i * 2) + 2),
                                        it
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("B 를 선택하는 이유 #${i + 1}") }
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .padding(6.dp)
                            ) {
                                Text(
                                    text = "B의 추가가 더 필요하면 추가 버튼을 또 누르세요.",
                                    color = Color(0xFF6A7486)
                                )
                            }
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = vm::addNextReason) {
                    Text(stringResource(R.string.action_add))
                }
                TextButton(onClick = vm::removeLastReason) {
                    Text(stringResource(R.string.decision_remove_last))
                }
            }

            OutlinedTextField(
                value = state.conclusion,
                onValueChange = vm::updateConclusion,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.decision_conclusion_label)) }
            )

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            TextButton(
                onClick = { vm.save(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.action_save)) }
        }
    }
}

@Composable
fun DecisionTutorialScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.decision_tutorial_title)) },
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
                        stringResource(R.string.decision_tutorial_heading),
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    TagRow()
                }
            }
            Paragraph(stringResource(R.string.decision_tutorial_intro_1))
            Paragraph(stringResource(R.string.decision_tutorial_intro_2))
            Paragraph(stringResource(R.string.decision_tutorial_intro_3))
            Text(stringResource(R.string.decision_tutorial_howto_title), fontWeight = FontWeight.SemiBold)
            StepCard("STEP 1", stringResource(R.string.decision_tutorial_step_1))
            StepCard("STEP 2", stringResource(R.string.decision_tutorial_step_2))
            StepCard("STEP 3", stringResource(R.string.decision_tutorial_step_3))
            StepCard("STEP 4", stringResource(R.string.decision_tutorial_step_4))
            StepCard("STEP 5", stringResource(R.string.decision_tutorial_step_5))
            StepCard("STEP 6", stringResource(R.string.decision_tutorial_step_6))
        }
    }
}

@Composable
private fun TagRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf("Decisional Balance", "Defusion", "Cognitive Flexibility").forEach {
            Text(
                text = it,
                color = Color(0xFFDDE8FF),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun Paragraph(text: String) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF6F8FC))
    ) {
        Text(text = text, modifier = Modifier.padding(14.dp))
    }
}

@Composable
private fun StepCard(step: String, text: String) {
    ElevatedCard {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(step, color = Color(0xFF2A67F8), fontWeight = FontWeight.SemiBold)
            Text(text)
        }
    }
}

@Composable
private fun AlternatingReasonBlock(
    reasonsA: List<String>,
    reasonsB: List<String>
) {
    val rounds = maxOf(reasonsA.size, reasonsB.size)
    val orderedReasons = buildList {
        for (i in 0 until rounds) {
            if (i < reasonsA.size) add(reasonsA[i])
            if (i < reasonsB.size) add(reasonsB[i])
        }
    }
    Card {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            orderedReasons.forEachIndexed { index, reason ->
                Text(reason, color = Color(0xFF36465D))
                if (index != orderedReasons.lastIndex) {
                    Text("그럼에도 불구하고", color = Color(0xFF2A67F8), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
private fun formatDate(ts: Long): String {
    return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(Date(ts))
}

