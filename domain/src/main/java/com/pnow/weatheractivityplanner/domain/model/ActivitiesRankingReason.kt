package com.pnow.weatheractivityplanner.domain.model

sealed interface ActivitiesRankingReason {

    sealed interface Skiing : ActivitiesRankingReason {
        data object SnowAndFreezing : Skiing
        data object FreezingOnly : Skiing
        data object SnowOnly : Skiing
        data object Rain : Skiing
        data object None : Skiing
    }

    sealed interface Surfing : ActivitiesRankingReason {
        data object Thunderstorm : Surfing
        data object Rain : Surfing
        data object WarmAndWindy : Surfing
        data object WindyOnly : Surfing
        data object WarmOnly : Surfing
        data object None : Surfing
    }

    sealed interface OutdoorSightseeing : ActivitiesRankingReason {
        data object ClearAndComfortable : OutdoorSightseeing
        data object Rain : OutdoorSightseeing
        data object Fog : OutdoorSightseeing
        data object ClearOnly : OutdoorSightseeing
        data object None : OutdoorSightseeing
    }

    sealed interface IndoorSightseeing : ActivitiesRankingReason {
        data object PoorOutdoor : IndoorSightseeing
        data object ExtremeTemp : IndoorSightseeing
        data object GreatOutdoor : IndoorSightseeing
        data object None : IndoorSightseeing
    }

    sealed interface Cycling : ActivitiesRankingReason {
        data object Rain : Cycling
        data object Gusty : Cycling
        data object Comfortable : Cycling
        data object Cold : Cycling
        data object None : Cycling
    }

    sealed interface BeachDay : ActivitiesRankingReason {
        data object Rain : BeachDay
        data object Windy : BeachDay
        data object WarmAndSunny : BeachDay
        data object SunnyOnly : BeachDay
        data object WarmOnly : BeachDay
        data object None : BeachDay
    }

    sealed interface Stargazing : ActivitiesRankingReason {
        data object Rain : Stargazing
        data object Fog : Stargazing
        data object ClearAndLongNight : Stargazing
        data object ClearOnly : Stargazing
        data object LongNightOnly : Stargazing
        data object None : Stargazing
    }

    sealed interface Fishing : ActivitiesRankingReason {
        data object Thunderstorm : Fishing
        data object Windy : Fishing
        data object CalmAndDry : Fishing
        data object CalmOnly : Fishing
        data object DryOnly : Fishing
        data object None : Fishing
    }
}
