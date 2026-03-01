package com.doptsw.sle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.doptsw.sle.feature.breathing.BreathingSessionScreen
import com.doptsw.sle.feature.breathing.BreathingSetupScreen
import com.doptsw.sle.feature.breathing.BreathingTutorialScreen
import com.doptsw.sle.feature.decision.DecisionEditScreen
import com.doptsw.sle.feature.decision.DecisionEntryScreen
import com.doptsw.sle.feature.decision.DecisionListScreen
import com.doptsw.sle.feature.decision.DecisionTutorialScreen
import com.doptsw.sle.feature.decision.DecisionViewScreen
import com.doptsw.sle.feature.diary.DiaryCalendarScreen
import com.doptsw.sle.feature.diary.DiaryDayListScreen
import com.doptsw.sle.feature.diary.DiaryEditScreen
import com.doptsw.sle.feature.diary.DiarySearchScreen
import com.doptsw.sle.feature.diary.DiaryTutorialScreen
import com.doptsw.sle.feature.diary.DiaryViewScreen
import com.doptsw.sle.feature.mainmenu.MainMenuScreen
import com.doptsw.sle.feature.security.AppLockGate
import com.doptsw.sle.feature.video.VideoListScreen
import com.doptsw.sle.navigation.AppRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier) {
                    SleApp()
                }
            }
        }
    }
}

@Composable
private fun SleApp() {
    var unlocked by rememberSaveable { mutableStateOf(false) }

    if (!unlocked) {
        AppLockGate(onUnlocked = { unlocked = true })
        return
    }

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppRoute.MainMenu) {
        composable(AppRoute.MainMenu) {
            MainMenuScreen(
                onDiaryClick = { navController.navigate(AppRoute.DiaryCalendar) },
                onBreathingClick = { navController.navigate(AppRoute.BreathingSetup) },
                onDecisionClick = { navController.navigate(AppRoute.DecisionEntry) },
                onVideoClick = { navController.navigate(AppRoute.VideoList) }
            )
        }
        composable(AppRoute.BreathingSetup) {
            BreathingSetupScreen(
                onBack = { navController.popBackStack() },
                onStart = { rounds -> navController.navigate(AppRoute.breathingSession(rounds)) },
                onTutorial = { navController.navigate(AppRoute.BreathingTutorial) }
            )
        }
        composable(AppRoute.BreathingSession) {
            BreathingSessionScreen(
                onBack = { navController.popBackStack() },
                onGoSetup = {
                    navController.popBackStack(AppRoute.BreathingSetup, false)
                },
                onTutorial = { navController.navigate(AppRoute.BreathingTutorial) }
            )
        }
        composable(AppRoute.BreathingTutorial) {
            BreathingTutorialScreen(onBack = { navController.popBackStack() })
        }
        composable(AppRoute.DecisionEntry) {
            DecisionEntryScreen(
                onBack = { navController.popBackStack() },
                onNavigateNew = {
                    navController.navigate(AppRoute.DecisionEditNew) {
                        popUpTo(AppRoute.DecisionEntry) { inclusive = true }
                    }
                },
                onNavigateList = {
                    navController.navigate(AppRoute.DecisionList) {
                        popUpTo(AppRoute.DecisionEntry) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.DecisionList) {
            DecisionListScreen(
                onBack = { navController.popBackStack() },
                onCreate = { navController.navigate(AppRoute.DecisionEditNew) },
                onOpen = { id -> navController.navigate(AppRoute.decisionView(id)) },
                onTutorial = { navController.navigate(AppRoute.DecisionTutorial) }
            )
        }
        composable(AppRoute.DecisionView) {
            DecisionViewScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(AppRoute.decisionEdit(id)) },
                onTutorial = { navController.navigate(AppRoute.DecisionTutorial) }
            )
        }
        composable(AppRoute.DecisionEditNew) {
            DecisionEditScreen(
                onBack = { navController.popBackStack() },
                onSaved = { id, _ ->
                    navController.navigate(AppRoute.decisionView(id)) {
                        popUpTo(AppRoute.DecisionEditNew) { inclusive = true }
                    }
                },
                onTutorial = { navController.navigate(AppRoute.DecisionTutorial) }
            )
        }
        composable(AppRoute.DecisionEditExisting) {
            DecisionEditScreen(
                onBack = { navController.popBackStack() },
                onSaved = { id, _ ->
                    navController.navigate(AppRoute.decisionView(id)) {
                        popUpTo(AppRoute.DecisionEditExisting) { inclusive = true }
                    }
                },
                onTutorial = { navController.navigate(AppRoute.DecisionTutorial) }
            )
        }
        composable(AppRoute.DecisionTutorial) {
            DecisionTutorialScreen(onBack = { navController.popBackStack() })
        }
        composable(AppRoute.VideoList) {
            VideoListScreen(onBack = { navController.popBackStack() })
        }
        composable(AppRoute.DiaryCalendar) {
            DiaryCalendarScreen(
                onBack = { navController.popBackStack() },
                onOpenDayList = { date -> navController.navigate(AppRoute.diaryDayList(date)) },
                onCreateEntry = { date -> navController.navigate(AppRoute.diaryEditNew(date)) },
                onSearch = { navController.navigate(AppRoute.DiarySearch) },
                onTutorial = { navController.navigate(AppRoute.DiaryTutorial) }
            )
        }
        composable(AppRoute.DiaryDayList) {
            DiaryDayListScreen(
                onBack = { navController.popBackStack() },
                onOpenEntry = { id -> navController.navigate(AppRoute.diaryView(id)) },
                onCreateEntry = { date -> navController.navigate(AppRoute.diaryEditNew(date)) },
                onTutorial = { navController.navigate(AppRoute.DiaryTutorial) }
            )
        }
        composable(AppRoute.DiaryView) {
            DiaryViewScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(AppRoute.diaryEdit(id)) },
                onTutorial = { navController.navigate(AppRoute.DiaryTutorial) }
            )
        }
        composable(AppRoute.DiaryEditNew) {
            DiaryEditScreen(
                onBack = { navController.popBackStack() },
                onSaved = { id, isNew ->
                    if (isNew) {
                        navController.navigate(AppRoute.diaryView(id)) {
                            popUpTo(AppRoute.DiaryEditNew) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                },
                onTutorial = { navController.navigate(AppRoute.DiaryTutorial) }
            )
        }
        composable(AppRoute.DiaryEditExisting) {
            DiaryEditScreen(
                onBack = { navController.popBackStack() },
                onSaved = { id, isNew ->
                    if (isNew) {
                        navController.navigate(AppRoute.diaryView(id))
                    } else {
                        navController.popBackStack()
                    }
                },
                onTutorial = { navController.navigate(AppRoute.DiaryTutorial) }
            )
        }
        composable(AppRoute.DiarySearch) {
            DiarySearchScreen(
                onBack = { navController.popBackStack() },
                onOpenEntry = { id -> navController.navigate(AppRoute.diaryView(id)) },
                onTutorial = { navController.navigate(AppRoute.DiaryTutorial) }
            )
        }
        composable(AppRoute.DiaryTutorial) {
            DiaryTutorialScreen(onBack = { navController.popBackStack() })
        }
    }
}
