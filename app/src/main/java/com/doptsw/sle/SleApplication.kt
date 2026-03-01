package com.doptsw.sle

import android.app.Application
import com.doptsw.sle.data.local.AppDatabase
import com.doptsw.sle.data.repository.DecisionRepository
import com.doptsw.sle.data.repository.DiaryRepository
import com.doptsw.sle.data.repository.DiscRepository
import com.doptsw.sle.data.repository.RoomDecisionRepository
import com.doptsw.sle.data.repository.RoomDiaryRepository
import com.doptsw.sle.data.repository.RoomDiscRepository

class SleApplication : Application() {
    private val database by lazy { AppDatabase.getInstance(this) }

    val diaryRepository: DiaryRepository by lazy {
        RoomDiaryRepository(database.diaryDao())
    }

    val decisionRepository: DecisionRepository by lazy {
        RoomDecisionRepository(database.decisionDao())
    }

    val discRepository: DiscRepository by lazy {
        RoomDiscRepository(database.discDao())
    }
}
