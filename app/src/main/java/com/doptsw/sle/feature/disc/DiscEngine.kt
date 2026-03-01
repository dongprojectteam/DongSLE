package com.doptsw.sle.feature.disc

import kotlin.math.sqrt

object DiscEngine {
    data class DiscCoordinate(
        val x: Int,
        val y: Int,
        val r: Double
    )

    fun isQuestionComplete(answer: DiscAnswer): Boolean {
        return answer.mostIndex != null && answer.leastIndex != null
    }

    fun selectMost(answer: DiscAnswer, optionIndex: Int): DiscAnswer {
        return if (answer.leastIndex == optionIndex) {
            answer.copy(mostIndex = optionIndex, leastIndex = null)
        } else {
            answer.copy(mostIndex = optionIndex)
        }
    }

    fun selectLeast(answer: DiscAnswer, optionIndex: Int): DiscAnswer {
        return if (answer.mostIndex == optionIndex) {
            answer.copy(mostIndex = null, leastIndex = optionIndex)
        } else {
            answer.copy(leastIndex = optionIndex)
        }
    }

    fun calculateScore(questions: List<DiscQuestion>, answers: List<DiscAnswer>): DiscScore {
        var d = 0
        var i = 0
        var s = 0
        var c = 0

        answers.forEach { answer ->
            val question = questions.firstOrNull { it.id == answer.questionId } ?: return@forEach
            val mostType = answer.mostIndex?.let { question.options.getOrNull(it)?.type }
            val leastType = answer.leastIndex?.let { question.options.getOrNull(it)?.type }

            when (mostType) {
                DiscType.D -> d += 1
                DiscType.I -> i += 1
                DiscType.S -> s += 1
                DiscType.C -> c += 1
                null -> Unit
            }
            when (leastType) {
                DiscType.D -> d -= 1
                DiscType.I -> i -= 1
                DiscType.S -> s -= 1
                DiscType.C -> c -= 1
                null -> Unit
            }
        }
        return DiscScore(d = d, i = i, s = s, c = c)
    }

    fun toCoordinate(score: DiscScore): DiscCoordinate {
        val x = (score.d + score.i) - (score.s + score.c)
        val y = (score.d + score.c) - (score.i + score.s)
        val r = sqrt((x * x + y * y).toDouble())
        return DiscCoordinate(x = x, y = y, r = r)
    }

    fun interpretation(score: DiscScore): DiscInterpretation {
        val coordinate = toCoordinate(score)
        val types = typesByCoordinate(coordinate.x, coordinate.y)
        val typeCode = when {
            types.size == 4 -> "DISC"
            else -> types.joinToString("") { it.name }
        }
        val strength = strengthLabel(coordinate.r)

        val title = when (typeCode) {
            "D" -> "D 중심 - 주도·결단"
            "I" -> "I 중심 - 사교·표현"
            "S" -> "S 중심 - 협력·안정"
            "C" -> "C 중심 - 분석·정확"
            "DI" -> "DI 혼합 - 우측 중앙"
            "CS" -> "CS 혼합 - 좌측 중앙"
            "DC" -> "DC 혼합 - 상단 중앙"
            "IS" -> "IS 혼합 - 하단 중앙"
            "DISC" -> "균형형 - 원점 중심"
            else -> "$typeCode 유형"
        }

        val summary = "X=${coordinate.x}, Y=${coordinate.y}, R=${"%.1f".format(coordinate.r)} · 프로파일 강도: $strength"

        val strengths = when (typeCode) {
            "D" -> "빠른 판단과 목표 추진이 강합니다."
            "I" -> "관계 형성과 설득, 표현 에너지가 강합니다."
            "S" -> "협력과 안정적인 실행, 지원 역량이 강합니다."
            "C" -> "분석과 정확성, 체계적 판단이 강합니다."
            "DI", "CS", "DC", "IS" -> "축 경계의 두 성향을 상황에 맞게 함께 사용할 수 있습니다."
            "DISC" -> "특정 방향 치우침이 적어 유연한 대응이 가능합니다."
            else -> "좌표 기반 혼합 성향이 나타납니다."
        }

        val cautions = when (typeCode) {
            "D" -> "속도가 빠른 만큼 설명과 공감이 부족해질 수 있습니다."
            "I" -> "에너지 중심으로 흐르며 세부 마감이 약해질 수 있습니다."
            "S" -> "안정 지향으로 변화 대응이 늦어질 수 있습니다."
            "C" -> "정확성 추구로 의사결정이 지연될 수 있습니다."
            "DI", "CS", "DC", "IS" -> "두 성향 사이에서 메시지 일관성이 흔들릴 수 있습니다."
            "DISC" -> "상황별 스타일 선택 기준이 없으면 방향성이 약해질 수 있습니다."
            else -> "강점이 분산되어 우선순위가 흔들릴 수 있습니다."
        }

        val detail = buildString {
            append("가로축 X=(D+I)-(S+C), 세로축 Y=(D+C)-(I+S)로 판정했습니다. ")
            append(
                when {
                    coordinate.x > 0 -> "외향·영향형 경향"
                    coordinate.x < 0 -> "내향·안정형 경향"
                    else -> "외향/내향 균형"
                }
            )
            append(", ")
            append(
                when {
                    coordinate.y > 0 -> "빠름·과업형 경향"
                    coordinate.y < 0 -> "느림·관계형 경향"
                    else -> "속도/안정 균형"
                }
            )
            append("입니다.")
        }

        return DiscInterpretation(
            typeCode = typeCode,
            title = title,
            summary = summary,
            strengths = strengths,
            cautions = cautions,
            detail = detail,
            relatedTypes = types
        )
    }

    private fun typesByCoordinate(x: Int, y: Int): List<DiscType> {
        return when {
            x > 0 && y > 0 -> listOf(DiscType.D)
            x > 0 && y < 0 -> listOf(DiscType.I)
            x < 0 && y < 0 -> listOf(DiscType.S)
            x < 0 && y > 0 -> listOf(DiscType.C)
            x > 0 && y == 0 -> listOf(DiscType.D, DiscType.I)
            x < 0 && y == 0 -> listOf(DiscType.C, DiscType.S)
            x == 0 && y > 0 -> listOf(DiscType.D, DiscType.C)
            x == 0 && y < 0 -> listOf(DiscType.I, DiscType.S)
            else -> DiscType.entries
        }
    }

    private fun strengthLabel(r: Double): String {
        return when {
            r <= 5.0 -> "약한 혼합"
            r <= 12.0 -> "보통"
            else -> "강한 유형"
        }
    }
}
