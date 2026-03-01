package com.doptsw.sle.feature.disc

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doptsw.sle.SleApplication
import com.doptsw.sle.data.repository.DiscAnswerSelection
import com.doptsw.sle.data.repository.DiscRepository
import com.doptsw.sle.data.repository.DiscResultDraft
import com.doptsw.sle.data.repository.DiscResultRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DiscQuestionUiState(
    val questions: List<DiscQuestion> = DiscQuestionBank.questions,
    val currentIndex: Int = 0,
    val answers: List<DiscAnswer> = DiscQuestionBank.questions.map { DiscAnswer(questionId = it.id) },
    val saving: Boolean = false,
    val errorMessage: String? = null
) {
    val currentQuestion: DiscQuestion
        get() = questions[currentIndex]

    val currentAnswer: DiscAnswer
        get() = answers[currentIndex]

    val progress: Float
        get() = (currentIndex + 1).toFloat() / questions.size.toFloat()

    val canGoPrevious: Boolean
        get() = currentIndex > 0

    val canGoNext: Boolean
        get() = DiscEngine.isQuestionComplete(currentAnswer) && !saving

    val isLastQuestion: Boolean
        get() = currentIndex == questions.lastIndex
}

class DiscHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DiscRepository = (application as SleApplication).discRepository
    val recentResults = repository.observeRecent(limit = 10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class DiscQuestionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DiscRepository = (application as SleApplication).discRepository

    var uiState by mutableStateOf(DiscQuestionUiState())
        private set

    fun selectMost(index: Int) {
        updateCurrentAnswer { DiscEngine.selectMost(it, index) }
    }

    fun selectLeast(index: Int) {
        updateCurrentAnswer { DiscEngine.selectLeast(it, index) }
    }

    fun goPrevious() {
        if (!uiState.canGoPrevious || uiState.saving) return
        uiState = uiState.copy(currentIndex = uiState.currentIndex - 1, errorMessage = null)
    }

    fun nextOrSubmit(onCompleted: (Long) -> Unit) {
        if (!uiState.canGoNext) return
        if (!uiState.isLastQuestion) {
            uiState = uiState.copy(currentIndex = uiState.currentIndex + 1, errorMessage = null)
            return
        }

        val currentAnswers = uiState.answers
        val score = DiscEngine.calculateScore(uiState.questions, currentAnswers)
        val interpretation = DiscEngine.interpretation(score)
        val selectedAnswers = currentAnswers.mapIndexedNotNull { index, answer ->
            val question = uiState.questions[index]
            val mostType = answer.mostIndex?.let { question.options.getOrNull(it)?.type } ?: return@mapIndexedNotNull null
            val leastType = answer.leastIndex?.let { question.options.getOrNull(it)?.type } ?: return@mapIndexedNotNull null
            DiscAnswerSelection(
                questionId = answer.questionId,
                mostType = mostType.name,
                leastType = leastType.name
            )
        }
        if (selectedAnswers.size != uiState.questions.size) {
            uiState = uiState.copy(errorMessage = "모든 문항에서 Most/Least를 선택해주세요.")
            return
        }

        uiState = uiState.copy(saving = true, errorMessage = null)
        viewModelScope.launch {
            repository.save(
                DiscResultDraft(
                    answers = selectedAnswers,
                    d = score.d,
                    i = score.i,
                    s = score.s,
                    c = score.c,
                    topTypes = interpretation.relatedTypes.map { it.name },
                    interpretationKey = interpretation.typeCode
                )
            ).onSuccess { id ->
                uiState = uiState.copy(saving = false)
                onCompleted(id)
            }.onFailure { e ->
                uiState = uiState.copy(saving = false, errorMessage = e.message ?: "결과 저장에 실패했습니다.")
            }
        }
    }

    private fun updateCurrentAnswer(transform: (DiscAnswer) -> DiscAnswer) {
        if (uiState.saving) return
        val answers = uiState.answers.toMutableList()
        answers[uiState.currentIndex] = transform(answers[uiState.currentIndex])
        uiState = uiState.copy(answers = answers, errorMessage = null)
    }
}

data class DiscResultUiState(
    val record: DiscResultRecord? = null,
    val interpretation: DiscInterpretation? = null
)

class DiscResultViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repository: DiscRepository = (application as SleApplication).discRepository
    private val resultId = savedStateHandle.get<String>("resultId")?.toLongOrNull() ?: -1L

    val uiState = repository.observeOne(resultId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun buildUiState(record: DiscResultRecord?): DiscResultUiState {
        if (record == null) return DiscResultUiState()
        val score = DiscScore(d = record.d, i = record.i, s = record.s, c = record.c)
        return DiscResultUiState(record = record, interpretation = DiscEngine.interpretation(score))
    }
}
