package com.doptsw.sle.feature.disc

enum class DiscType {
    D, I, S, C
}

data class DiscOption(
    val word: String,
    val type: DiscType
)

data class DiscQuestion(
    val id: Int,
    val options: List<DiscOption>
)

data class DiscAnswer(
    val questionId: Int,
    val mostIndex: Int? = null,
    val leastIndex: Int? = null
)

data class DiscScore(
    val d: Int,
    val i: Int,
    val s: Int,
    val c: Int
) {
    fun valueOf(type: DiscType): Int = when (type) {
        DiscType.D -> d
        DiscType.I -> i
        DiscType.S -> s
        DiscType.C -> c
    }
}

data class DiscInterpretation(
    val typeCode: String,
    val title: String,
    val summary: String,
    val strengths: String,
    val cautions: String,
    val detail: String,
    val relatedTypes: List<DiscType>
)

object DiscQuestionBank {
    val questions: List<DiscQuestion> = listOf(
        DiscQuestion(1, listOf(DiscOption("단호한", DiscType.D), DiscOption("사교적인", DiscType.I), DiscOption("온화한", DiscType.S), DiscOption("정확한", DiscType.C))),
        DiscQuestion(2, listOf(DiscOption("대담한", DiscType.D), DiscOption("표현력있는", DiscType.I), DiscOption("참을성있는", DiscType.S), DiscOption("신중한", DiscType.C))),
        DiscQuestion(3, listOf(DiscOption("결단력있는", DiscType.D), DiscOption("열정적인", DiscType.I), DiscOption("차분한", DiscType.S), DiscOption("체계적인", DiscType.C))),
        DiscQuestion(4, listOf(DiscOption("주도적인", DiscType.D), DiscOption("낙천적인", DiscType.I), DiscOption("안정적인", DiscType.S), DiscOption("논리적인", DiscType.C))),
        DiscQuestion(5, listOf(DiscOption("추진력있는", DiscType.D), DiscOption("활기찬", DiscType.I), DiscOption("협력적인", DiscType.S), DiscOption("분석적인", DiscType.C))),
        DiscQuestion(6, listOf(DiscOption("직선적인", DiscType.D), DiscOption("외향적인", DiscType.I), DiscOption("배려하는", DiscType.S), DiscOption("꼼꼼한", DiscType.C))),
        DiscQuestion(7, listOf(DiscOption("과감한", DiscType.D), DiscOption("친화적인", DiscType.I), DiscOption("일관된", DiscType.S), DiscOption("질서정연한", DiscType.C))),
        DiscQuestion(8, listOf(DiscOption("통솔하는", DiscType.D), DiscOption("설득력있는", DiscType.I), DiscOption("성실한", DiscType.S), DiscOption("규범적인", DiscType.C))),
        DiscQuestion(9, listOf(DiscOption("도전적인", DiscType.D), DiscOption("영향력있는", DiscType.I), DiscOption("신뢰감주는", DiscType.S), DiscOption("세밀한", DiscType.C))),
        DiscQuestion(10, listOf(DiscOption("자립적인", DiscType.D), DiscOption("매력적인", DiscType.I), DiscOption("헌신적인", DiscType.S), DiscOption("계획적인", DiscType.C))),
        DiscQuestion(11, listOf(DiscOption("경쟁적인", DiscType.D), DiscOption("유쾌한", DiscType.I), DiscOption("부드러운", DiscType.S), DiscOption("조심성있는", DiscType.C))),
        DiscQuestion(12, listOf(DiscOption("신속한", DiscType.D), DiscOption("재미있는", DiscType.I), DiscOption("수용적인", DiscType.S), DiscOption("객관적인", DiscType.C))),
        DiscQuestion(13, listOf(DiscOption("명확한", DiscType.D), DiscOption("즉흥적인", DiscType.I), DiscOption("평화로운", DiscType.S), DiscOption("이성적인", DiscType.C))),
        DiscQuestion(14, listOf(DiscOption("확고한", DiscType.D), DiscOption("개방적인", DiscType.I), DiscOption("인내하는", DiscType.S), DiscOption("정돈된", DiscType.C))),
        DiscQuestion(15, listOf(DiscOption("강단있는", DiscType.D), DiscOption("감성적인", DiscType.I), DiscOption("도움주는", DiscType.S), DiscOption("철저한", DiscType.C))),
        DiscQuestion(16, listOf(DiscOption("결의있는", DiscType.D), DiscOption("친근한", DiscType.I), DiscOption("지지하는", DiscType.S), DiscOption("정밀한", DiscType.C))),
        DiscQuestion(17, listOf(DiscOption("직접적인", DiscType.D), DiscOption("대화좋아하는", DiscType.I), DiscOption("조화로운", DiscType.S), DiscOption("합리적인", DiscType.C))),
        DiscQuestion(18, listOf(DiscOption("이끄는", DiscType.D), DiscOption("유연한", DiscType.I), DiscOption("겸손한", DiscType.S), DiscOption("절제된", DiscType.C))),
        DiscQuestion(19, listOf(DiscOption("개척하는", DiscType.D), DiscOption("관계지향적인", DiscType.I), DiscOption("순한", DiscType.S), DiscOption("신뢰성있는", DiscType.C))),
        DiscQuestion(20, listOf(DiscOption("주관뚜렷한", DiscType.D), DiscOption("호감주는", DiscType.I), DiscOption("느긋한", DiscType.S), DiscOption("타당한", DiscType.C))),
        DiscQuestion(21, listOf(DiscOption("결행하는", DiscType.D), DiscOption("쾌활한", DiscType.I), DiscOption("안심주는", DiscType.S), DiscOption("검증하는", DiscType.C))),
        DiscQuestion(22, listOf(DiscOption("과업지향적인", DiscType.D), DiscOption("자유로운", DiscType.I), DiscOption("관용적인", DiscType.S), DiscOption("규칙적인", DiscType.C))),
        DiscQuestion(23, listOf(DiscOption("목표지향적인", DiscType.D), DiscOption("상호적인", DiscType.I), DiscOption("양보하는", DiscType.S), DiscOption("일관성있는", DiscType.C))),
        DiscQuestion(24, listOf(DiscOption("단정적인", DiscType.D), DiscOption("밝은", DiscType.I), DiscOption("친절한", DiscType.S), DiscOption("엄밀한", DiscType.C))),
        DiscQuestion(25, listOf(DiscOption("주도권잡는", DiscType.D), DiscOption("흥겨운", DiscType.I), DiscOption("꾸준한", DiscType.S), DiscOption("구조적인", DiscType.C))),
        DiscQuestion(26, listOf(DiscOption("행동빠른", DiscType.D), DiscOption("공감하는", DiscType.I), DiscOption("포근한", DiscType.S), DiscOption("체계화된", DiscType.C))),
        DiscQuestion(27, listOf(DiscOption("결정빠른", DiscType.D), DiscOption("친밀한", DiscType.I), DiscOption("온순한", DiscType.S), DiscOption("원칙적인", DiscType.C))),
        DiscQuestion(28, listOf(DiscOption("앞장서는", DiscType.D), DiscOption("정서표현적인", DiscType.I), DiscOption("성숙한", DiscType.S), DiscOption("기준중시하는", DiscType.C)))
    )
}
