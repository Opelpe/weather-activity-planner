package com.pnow.weatheractivityplanner.domain.ranking

private const val MIN_SCORE = 0f
private const val MAX_SCORE = 100f

internal fun coerceActivityScore(score: Float): Float = score.coerceIn(MIN_SCORE, MAX_SCORE)

internal fun Float.activityBonus(
    condition: Boolean,
    amount: Float,
): Float =
    if (condition) this + amount else this

internal fun Float.activityPenalty(
    condition: Boolean,
    amount: Float,
): Float =
    if (condition) this - amount else this

internal fun Float.activityBonus(
    fraction: Float,
    amount: Float,
): Float = this + (amount * fraction.coerceIn(0f, 1f))

internal fun Float.activityPenalty(
    fraction: Float,
    amount: Float,
): Float = this - (amount * fraction.coerceIn(0f, 1f))


internal fun fractionBetween(
    value: Double,
    from: Double,
    to: Double,
): Float =
    ((value - from) / (to - from)).toFloat().coerceIn(0f, 1f)
