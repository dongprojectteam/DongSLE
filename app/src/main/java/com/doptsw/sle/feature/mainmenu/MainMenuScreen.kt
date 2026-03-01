@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.doptsw.sle.feature.mainmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.doptsw.sle.R

@Composable
fun MainMenuScreen(
    onDiaryClick: () -> Unit,
    onBreathingClick: () -> Unit,
    onDecisionClick: () -> Unit,
    onVideoClick: () -> Unit
) {
    val items = listOf(
        MenuItem(
            label = stringResource(R.string.menu_diary),
            subtitle = "Emotion journaling",
            icon = Icons.Rounded.FavoriteBorder,
            accent = Color(0xFFEC5F77),
            onClick = onDiaryClick
        ),
        MenuItem(
            label = stringResource(R.string.menu_breathing),
            subtitle = "Breath rhythm guide",
            icon = Icons.Rounded.Spa,
            accent = Color(0xFF3A9C93),
            onClick = onBreathingClick
        ),
        MenuItem(
            label = stringResource(R.string.menu_decision),
            subtitle = "Decision framework",
            icon = Icons.Rounded.Lightbulb,
            accent = Color(0xFFF2A63A),
            onClick = onDecisionClick
        ),
        MenuItem(
            label = stringResource(R.string.menu_video),
            subtitle = "Helpful videos",
            icon = Icons.Rounded.PlayCircle,
            accent = Color(0xFF5B6EF5),
            onClick = onVideoClick
        )
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(R.string.main_menu_title)) }) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFFF4F7FF), Color(0xFFFDFEFF))))
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2A44))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "오늘의 상태를 정리해보세요",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "필요한 메뉴를 선택하면 바로 시작할 수 있습니다.",
                                color = Color(0xFFD8E0FF),
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
                items(items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = item.onClick),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(item.accent.copy(alpha = 0.14f))
                                    .padding(10.dp)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = item.accent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text(text = item.label, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = item.subtitle,
                                    color = Color(0xFF5F6C80),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Provided by DOPT",
                            color = Color(0xFF647186),
                            style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "AI Solution Lab, VD",
                            color = Color(0xFF8A95A8),
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "v1.0.1.0",
                            color = Color(0xFF9AA4B4),
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class MenuItem(
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val onClick: () -> Unit
)
