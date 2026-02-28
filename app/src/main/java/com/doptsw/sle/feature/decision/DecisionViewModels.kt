package com.doptsw.sle.feature.decision

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doptsw.sle.SleApplication
import com.doptsw.sle.data.repository.DecisionDraft
import com.doptsw.sle.data.repository.DecisionRecord
import com.doptsw.sle.data.repository.DecisionRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DecisionEntryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DecisionRepository = (application as SleApplication).decisionRepository
    val count = repository.observeCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), -1)
}

class DecisionListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DecisionRepository = (application as SleApplication).decisionRepository
    val records = repository.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class DecisionViewViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repository: DecisionRepository = (application as SleApplication).decisionRepository
    private val id = savedStateHandle.get<String>("id")?.toLongOrNull() ?: -1L
    val record = repository.observeOne(id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.delete(id)
            onDone()
        }
    }
}

class DecisionEditViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repository: DecisionRepository = (application as SleApplication).decisionRepository
    private val id: Long? = savedStateHandle.get<String>("id")?.toLongOrNull()
    var uiState by mutableStateOf(DecisionEditUiState())
        private set

    init {
        if (id != null) {
            viewModelScope.launch {
                repository.observeOne(id).collect { record ->
                    if (record != null) {
                        uiState = uiState.copy(
                            id = record.id,
                            optionA = record.optionA,
                            optionB = record.optionB,
                            reasonsA = if (record.reasonsA.isEmpty()) List(DecisionValidation.INITIAL_REQUIRED_PAIRS) { "" } else record.reasonsA,
                            reasonsB = if (record.reasonsB.isEmpty()) List(DecisionValidation.INITIAL_REQUIRED_PAIRS) { "" } else record.reasonsB,
                            conclusion = record.conclusion
                        )
                    }
                }
            }
        }
    }

    fun updateOptionA(value: String) {
        uiState = uiState.copy(optionA = value, error = null)
    }

    fun updateOptionB(value: String) {
        uiState = uiState.copy(optionB = value, error = null)
    }

    fun updateConclusion(value: String) {
        uiState = uiState.copy(conclusion = value, error = null)
    }

    fun updateReason(item: DecisionReasonItem, value: String) {
        uiState = when (item.target) {
            ReasonTarget.A -> uiState.copy(
                reasonsA = uiState.reasonsA.toMutableList().also { it[item.indexInTarget] = value },
                error = null
            )
            ReasonTarget.B -> uiState.copy(
                reasonsB = uiState.reasonsB.toMutableList().also { it[item.indexInTarget] = value },
                error = null
            )
        }
    }

    fun addNextReason() {
        uiState = if (uiState.nextTarget == ReasonTarget.A) {
            uiState.copy(reasonsA = uiState.reasonsA + "")
        } else {
            uiState.copy(reasonsB = uiState.reasonsB + "")
        }
    }

    fun removeLastReason() {
        if (uiState.reasonsA.size <= DecisionValidation.INITIAL_REQUIRED_PAIRS &&
            uiState.reasonsB.size <= DecisionValidation.INITIAL_REQUIRED_PAIRS
        ) return
        uiState = if (uiState.nextTarget == ReasonTarget.A) {
            uiState.copy(reasonsB = uiState.reasonsB.dropLast(1))
        } else {
            uiState.copy(reasonsA = uiState.reasonsA.dropLast(1))
        }
    }

    fun save(onSaved: (Long, Boolean) -> Unit) {
        val validation = DecisionValidation.validate(uiState)
        if (!validation.valid) {
            uiState = uiState.copy(error = validation.message)
            return
        }
        viewModelScope.launch {
            val isNew = uiState.id == null
            val result = repository.save(
                DecisionDraft(
                    id = uiState.id,
                    optionA = uiState.optionA.trim(),
                    optionB = uiState.optionB.trim(),
                    reasonsA = uiState.reasonsA.map { it.trim() }.filter { it.isNotEmpty() },
                    reasonsB = uiState.reasonsB.map { it.trim() }.filter { it.isNotEmpty() },
                    conclusion = uiState.conclusion.trim()
                )
            )
            result.onSuccess { onSaved(it, isNew) }
                .onFailure { uiState = uiState.copy(error = it.message ?: "저장 실패") }
        }
    }
}

fun DecisionRecord.titleText(): String = "$optionA  vs  $optionB"
