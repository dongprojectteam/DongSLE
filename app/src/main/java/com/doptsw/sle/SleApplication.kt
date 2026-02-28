package com.doptsw.sle

import android.app.Application
import com.doptsw.sle.data.local.AppDatabase
import com.doptsw.sle.data.repository.DecisionRepository
import com.doptsw.sle.data.repository.DiaryRepository
import com.doptsw.sle.data.repository.RoomDecisionRepository
import com.doptsw.sle.data.repository.RoomDiaryRepository

class SleApplication : Application() {
    private val database by lazy { AppDatabase.getInstance(this) }

    val diaryRepository: DiaryRepository by lazy {
        RoomDiaryRepository(database.diaryDao())
    }

    val decisionRepository: DecisionRepository by lazy {
        RoomDecisionRepository(database.decisionDao())
    }
}
