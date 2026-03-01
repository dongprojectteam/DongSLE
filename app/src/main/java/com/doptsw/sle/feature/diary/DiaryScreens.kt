@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    kotlinx.coroutines.ExperimentalCoroutinesApi::class
)

package com.doptsw.sle.feature.diary

import android.app.Application
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doptsw.sle.R
import com.doptsw.sle.SleApplication
import com.doptsw.sle.core.formatDisplayDate
import com.doptsw.sle.core.monthTitle
import com.doptsw.sle.data.repository.DiaryEntryDraft
import com.doptsw.sle.data.repository.DiaryRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun DiaryCalendarScreen(
    onBack: () -> Unit,
    onOpenDayList: (String) -> Unit,
    onCreateEntry: (String) -> Unit,
    onSearch: () -> Unit,
    onTutorial: () -> Unit
) {
    val vm: DiaryCalendarViewModel = viewModel()
    val month by vm.month.collectAsState()
    val counts by vm.dateCounts.collectAsState()
    val recentEntries by vm.recentEntries.collectAsState()
    val canLoadMore by vm.canLoadMore.collectAsState()
    val countMap = remember(counts) { counts.associate { it.entryDate to it.count } }
    val cells = remember(month) { buildCalendarCells(month) }
    val weekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val today = remember { LocalDate.now() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.diary_calendar_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "<") } },
                actions = {
                    TextButton(onClick = onSearch) { Text(text = stringResource(R.string.action_search)) }
                    TextButton(onClick = onTutorial) { Text(text = "?") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = vm::goPrevMonth) { Text(text = "<") }
                Text(text = monthTitle(month), style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = vm::goNextMonth) { Text(text = ">") }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        weekdays.forEachIndexed { index, day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelMedium,
                                color = weekendColor(index),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    cells.chunked(7).forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            week.forEach { cell ->
                                CalendarDayCell(
                                    cell = cell,
                                    count = cell.date?.let { countMap[it.toString()] } ?: 0,
                                    isToday = cell.date == today,
                                    onClick = {
                                        val date = cell.date?.toString() ?: return@CalendarDayCell
                                        val count = countMap[date] ?: 0
                                        if (count > 0) onOpenDayList(date) else onCreateEntry(date)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "최근 감정 일기",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (recentEntries.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "아직 작성된 감정 일기가 없습니다.",
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFF6E798A)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    recentEntries.forEach { entry ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenDayList(entry.entryDate) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = formatDisplayDate(entry.entryDate),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF647186)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = entry.situation.take(80))
                            }
                        }
                    }
                    if (canLoadMore) {
                        TextButton(
                            onClick = vm::loadMoreRecent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("더 보기")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryDayListScreen(
    onBack: () -> Unit,
    onOpenEntry: (Long) -> Unit,
    onCreateEntry: (String) -> Unit,
    onTutorial: () -> Unit
) {
    val vm: DiaryDayListViewModel = viewModel()
    val entries by vm.entries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "${stringResource(R.string.diary_day_list_title)} (${formatDisplayDate(vm.date)})") },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "<") } },
                actions = { TextButton(onClick = onTutorial) { Text(text = "?") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onCreateEntry(vm.date) }) {
                Text(text = "+")
            }
        }
    ) { innerPadding ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.empty_day_entries))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenEntry(entry.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = formatDisplayDate(entry.entryDate), style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = entry.situation, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryViewScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onTutorial: () -> Unit
) {
    val vm: DiaryViewViewModel = viewModel()
    val entry by vm.entry.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.diary_view_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "<") } },
                actions = { TextButton(onClick = onTutorial) { Text(text = "?") } }
            )
        }
    ) { innerPadding ->
        val current = entry
        if (current == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.empty_day_entries))
            }
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
            Text(text = formatDisplayDate(current.entryDate), style = MaterialTheme.typography.titleMedium)
            DiaryReadOnlyField(title = stringResource(R.string.field_situation_label), content = current.situation)
            DiaryReadOnlyField(title = stringResource(R.string.field_feeling_label), content = current.feeling)
            DiaryReadOnlyField(title = stringResource(R.string.field_thought_label), content = current.thought)
            DiaryReadOnlyField(title = stringResource(R.string.field_action_label), content = current.desiredAction)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onEdit(current.id) }) { Text(text = stringResource(R.string.action_edit)) }
                Button(onClick = { showDeleteDialog = true }) { Text(text = stringResource(R.string.action_delete)) }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        vm.delete(onDone = onBack)
                    }
                ) { Text(text = stringResource(R.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            },
            text = { Text(text = stringResource(R.string.delete_confirm_message)) }
        )
    }
}

@Composable
fun DiaryEditScreen(
    onBack: () -> Unit,
    onSaved: (Long, Boolean) -> Unit,
    onTutorial: () -> Unit
) {
    val vm: DiaryEditViewModel = viewModel()
    val uiState = vm.uiState
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.id == null) {
                            stringResource(R.string.diary_edit_title_new)
                        } else {
                            stringResource(R.string.diary_edit_title_edit)
                        }
                    )
                },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "<") } },
                actions = { TextButton(onClick = onTutorial) { Text(text = "?") } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = formatDisplayDate(uiState.entryDate), style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.situation,
                onValueChange = vm::updateSituation,
                label = { Text(text = stringResource(R.string.field_situation_label)) },
                placeholder = { Text(text = stringResource(R.string.hint_situation)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.feeling,
                onValueChange = vm::updateFeeling,
                label = { Text(text = stringResource(R.string.field_feeling_label)) },
                placeholder = { Text(text = stringResource(R.string.hint_feeling)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.thought,
                onValueChange = vm::updateThought,
                label = { Text(text = stringResource(R.string.field_thought_label)) },
                placeholder = { Text(text = stringResource(R.string.hint_thought)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.desiredAction,
                onValueChange = vm::updateDesiredAction,
                label = { Text(text = stringResource(R.string.field_action_label)) },
                placeholder = { Text(text = stringResource(R.string.hint_action)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )
            if (uiState.error != null) {
                Text(text = uiState.error, color = MaterialTheme.colorScheme.error)
            }
            Button(onClick = { vm.save(onSaved = onSaved) }, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.action_save))
            }
        }
    }
}

@Composable
fun DiarySearchScreen(
    onBack: () -> Unit,
    onOpenEntry: (Long) -> Unit,
    onTutorial: () -> Unit
) {
    val vm: DiarySearchViewModel = viewModel()
    val keyword by vm.keyword.collectAsState()
    val results by vm.results.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.diary_search_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "<") } },
                actions = { TextButton(onClick = onTutorial) { Text(text = "?") } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = keyword,
                onValueChange = vm::updateKeyword,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                label = { Text(text = stringResource(R.string.action_search)) },
                placeholder = { Text(text = stringResource(R.string.search_hint)) }
            )
            if (results.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.empty_search_result))
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(results) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenEntry(item.id) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = formatDisplayDate(item.entryDate), style = MaterialTheme.typography.labelMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = item.preview)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryTutorialScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.diary_tutorial_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "<") } }
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
                        text = stringResource(R.string.tutorial_heading),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    TutorialTags(tags = listOf("Affect Labeling", "CBT", "Expressive Writing"))
                }
            }

            QuoteHighlight(text = "핵심: 감정을 정확히 파악해 기록하고, 오늘의 감정은 오늘 정리합니다.")

            TutorialBlock(text = stringResource(R.string.tutorial_intro_1))
            TutorialBlock(text = stringResource(R.string.tutorial_intro_2))
            TutorialBlock(text = stringResource(R.string.tutorial_intro_3))
            TutorialBlock(text = stringResource(R.string.tutorial_intro_4))
            TutorialBlock(text = stringResource(R.string.tutorial_intro_5))

            Text(
                text = stringResource(R.string.tutorial_howto_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp, start = 2.dp)
            )

            TutorialStepCard(step = "STEP 1", content = stringResource(R.string.tutorial_step_1))
            TutorialStepCard(step = "STEP 2", content = stringResource(R.string.tutorial_step_2))
            TutorialStepCard(step = "STEP 3", content = stringResource(R.string.tutorial_step_3))
            TutorialStepCard(step = "STEP 4", content = stringResource(R.string.tutorial_step_4))
        }
    }
}

@Composable
private fun RowScope.CalendarDayCell(
    cell: CalendarCell,
    count: Int,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val date = cell.date
    val interaction = remember { MutableInteractionSource() }
    val pulse = rememberInfiniteTransition(label = "today-pulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse),
        label = "pulse-scale"
    )
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse),
        label = "pulse-alpha"
    )

    val dayNumberColor = when (date?.dayOfWeek) {
        DayOfWeek.SUNDAY -> Color(0xFFE75A5A)
        DayOfWeek.SATURDAY -> Color(0xFF4B7FFF)
        else -> MaterialTheme.colorScheme.onSurface
    }
    val containerColor = when {
        date == null -> Color.Transparent
        isToday -> Color(0xFFE9F1FF)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = date != null,
                interactionSource = interaction,
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .background(containerColor)
            .border(
                border = if (isToday) BorderStroke(1.5.dp, Color(0xFF2A67F8)) else BorderStroke(0.dp, Color.Transparent),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(6.dp)
    ) {
        if (date != null) {
            Column {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = dayNumberColor,
                    fontWeight = if (isToday) FontWeight.SemiBold else FontWeight.Normal
                )
                if (isToday) {
                    Text(
                        text = "TODAY",
                        color = Color(0xFF2A67F8),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .height(4.dp)
                            .fillMaxWidth(0.5f)
                            .graphicsLayer {
                                scaleX = pulseScale
                                alpha = pulseAlpha
                            }
                            .background(Color(0xFF2A67F8), RoundedCornerShape(999.dp))
                    )
                }
                if (count > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = count.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun TutorialTags(tags: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        tags.take(3).forEach { tag ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0x263F7CFF))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(text = tag, style = MaterialTheme.typography.labelSmall, color = Color(0xFFDDE8FF))
            }
        }
    }
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
private fun TutorialBlock(text: String) {
    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF6F8FC))
    ) {
        Text(text = text, modifier = Modifier.padding(14.dp), color = Color(0xFF273142))
    }
}

@Composable
private fun TutorialStepCard(step: String, content: String) {
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
            Text(text = content, color = Color(0xFF273142))
        }
    }
}

private fun weekendColor(index: Int): Color {
    return when (index) {
        0 -> Color(0xFFE75A5A)
        6 -> Color(0xFF4B7FFF)
        else -> Color(0xFF687385)
    }
}

@Composable
private fun DiaryReadOnlyField(title: String, content: String) {
    Text(text = title, style = MaterialTheme.typography.labelLarge)
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(text = content, modifier = Modifier.padding(10.dp))
    }
}

private data class CalendarCell(val date: LocalDate?)

private fun buildCalendarCells(month: YearMonth): List<CalendarCell> {
    val firstDate = month.atDay(1)
    val offset = toCalendarOffset(firstDate.dayOfWeek)
    val cells = mutableListOf<CalendarCell>()
    repeat(offset) { cells += CalendarCell(null) }
    for (day in 1..month.lengthOfMonth()) {
        cells += CalendarCell(month.atDay(day))
    }
    while (cells.size % 7 != 0) {
        cells += CalendarCell(null)
    }
    return cells
}

private fun toCalendarOffset(dayOfWeek: DayOfWeek): Int = dayOfWeek.value % 7

class DiaryCalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DiaryRepository = (application as SleApplication).diaryRepository
    private val _month = MutableStateFlow(YearMonth.now())
    private val _recentVisibleCount = MutableStateFlow(10)
    val month = _month.asStateFlow()
    val dateCounts = _month
        .flatMapLatest { yearMonth ->
            repository.observeDiaryDatesWithCount(
                monthStart = yearMonth.atDay(1).toString(),
                monthEnd = yearMonth.atEndOfMonth().toString()
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val allRecentEntries = repository.observeAllEntriesDesc()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentEntries = combine(allRecentEntries, _recentVisibleCount) { all, visible ->
        all.take(visible)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val canLoadMore = combine(allRecentEntries, _recentVisibleCount) { all, visible ->
        all.size > visible
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun goPrevMonth() {
        _month.value = _month.value.minusMonths(1)
    }

    fun goNextMonth() {
        _month.value = _month.value.plusMonths(1)
    }

    fun loadMoreRecent() {
        _recentVisibleCount.value = _recentVisibleCount.value + 10
    }
}

class DiaryDayListViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repository: DiaryRepository = (application as SleApplication).diaryRepository
    val date: String = savedStateHandle.get<String>("date") ?: LocalDate.now().toString()
    val entries = repository.observeEntriesByDate(date)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class DiaryViewViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repository: DiaryRepository = (application as SleApplication).diaryRepository
    private val entryId: Long = savedStateHandle.get<String>("entryId")?.toLongOrNull() ?: -1L
    val entry = repository.observeEntry(entryId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.delete(entryId)
            onDone()
        }
    }
}

data class DiaryEditUiState(
    val id: Long? = null,
    val entryDate: String = LocalDate.now().toString(),
    val situation: String = "",
    val feeling: String = "",
    val thought: String = "",
    val desiredAction: String = "",
    val error: String? = null
)

class DiaryEditViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repository: DiaryRepository = (application as SleApplication).diaryRepository
    private val entryId: Long? = savedStateHandle.get<String>("entryId")?.toLongOrNull()
    var uiState by mutableStateOf(
        DiaryEditUiState(entryDate = savedStateHandle.get<String>("date") ?: LocalDate.now().toString())
    )
        private set

    init {
        if (entryId != null) {
            viewModelScope.launch {
                val existing = repository.observeEntry(entryId).firstOrNull()
                if (existing != null) {
                    uiState = uiState.copy(
                        id = existing.id,
                        entryDate = existing.entryDate,
                        situation = existing.situation,
                        feeling = existing.feeling,
                        thought = existing.thought,
                        desiredAction = existing.desiredAction
                    )
                }
            }
        }
    }

    fun updateSituation(value: String) {
        uiState = uiState.copy(situation = value, error = null)
    }

    fun updateFeeling(value: String) {
        uiState = uiState.copy(feeling = value, error = null)
    }

    fun updateThought(value: String) {
        uiState = uiState.copy(thought = value, error = null)
    }

    fun updateDesiredAction(value: String) {
        uiState = uiState.copy(desiredAction = value, error = null)
    }

    fun save(onSaved: (Long, Boolean) -> Unit) {
        if (uiState.situation.trim().isEmpty() ||
            uiState.feeling.trim().isEmpty() ||
            uiState.thought.trim().isEmpty() ||
            uiState.desiredAction.trim().isEmpty()
        ) {
            uiState = uiState.copy(error = getApplication<Application>().getString(R.string.validation_all_fields_required))
            return
        }

        viewModelScope.launch {
            val isNew = uiState.id == null
            val result = repository.save(
                DiaryEntryDraft(
                    id = uiState.id,
                    entryDate = uiState.entryDate,
                    situation = uiState.situation.trim(),
                    feeling = uiState.feeling.trim(),
                    thought = uiState.thought.trim(),
                    desiredAction = uiState.desiredAction.trim()
                )
            )
            result.onSuccess { savedId ->
                onSaved(savedId, isNew)
            }.onFailure {
                uiState = uiState.copy(error = it.message ?: "save failed")
            }
        }
    }
}

class DiarySearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DiaryRepository = (application as SleApplication).diaryRepository
    private val _keyword = MutableStateFlow("")
    val keyword = _keyword.asStateFlow()
    val results = _keyword
        .map { it.trim() }
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(emptyList()) else repository.search(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateKeyword(value: String) {
        _keyword.value = value
    }
}
