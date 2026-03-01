@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.doptsw.sle.feature.disc

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
fun DiscTutorialScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.disc_tutorial_title)) },
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
                        text = "DISC 행동유형 모델이란?",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TutorialTag("DISC")
                        TutorialTag("Behavior Pattern")
                        TutorialTag("Communication")
                    }
                }
            }

            Paragraph("DISC는 인간의 행동 패턴을 네 가지 핵심 요소로 분류한 심리 도구입니다. 성격 자체보다는 특정 상황에서 어떤 방식으로 행동하고 소통하는가에 초점을 맞춥니다.")
            Paragraph("본인의 유형을 파악하려면 두 가지 질문이 중요합니다. 나의 속도는 빠르고 주도적인가, 아니면 신중하고 수용적인가? 나의 관심사는 일과 과업인가, 아니면 사람과 관계인가?")

            TutorialSection(
                title = "D: 주도형 (Dominance / Driver)",
                subtitle = "결과가 최우선인 추진가",
                body = "속도가 빠르고 과업 중심입니다. 문제를 정면 돌파하며 결단력이 좋지만, 때로는 결과 중심으로 보일 수 있습니다. 다른 명칭: 주도형, 추진가, 지배형."
            )
            TutorialSection(
                title = "I: 사교형 (Influence / Inspiring)",
                subtitle = "분위기를 만드는 낙천가",
                body = "속도는 빠르고 사람 중심입니다. 설득과 분위기 형성이 강점이며 인정과 칭찬에 동기화됩니다. 다른 명칭: 사교형, 감화형, 사교가."
            )
            TutorialSection(
                title = "S: 안정형 (Steadiness / Supportive)",
                subtitle = "평화를 사랑하는 조력자",
                body = "속도는 완만하고 사람 중심입니다. 조화와 협력을 중요하게 여기며 변화가 급하면 스트레스를 받기 쉽습니다. 다른 명칭: 안정형, 지원형, 순응형."
            )
            TutorialSection(
                title = "C: 신중형 (Conscientiousness / Compliant)",
                subtitle = "완벽을 기하는 분석가",
                body = "속도는 완만하고 과업/데이터 중심입니다. 근거와 규칙을 중시해 정확도가 높지만 의사결정이 느려질 수 있습니다. 다른 명칭: 신중형, 준수형, 분석가."
            )

            Paragraph("본 진단은 William Moulton Marston의 Emotions of Normal People 을 바탕으로 만들어졌습니다.")
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
private fun Paragraph(text: String) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF6F8FC))) {
        Text(text = text, modifier = Modifier.padding(14.dp), color = Color(0xFF273142))
    }
}

@Composable
private fun TutorialSection(title: String, subtitle: String, body: String) {
    ElevatedCard(shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, color = Color(0xFF2A67F8), fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(body, color = Color(0xFF273142))
        }
    }
}
