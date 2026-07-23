package com.pnow.weatheractivityplanner.domain.model

sealed interface ActivityDailyReason {

    sealed interface Skiing : ActivityDailyReason {
        data object SnowAndFreezing : Skiing
        data object FreezingOnly : Skiing
        data object SnowOnly : Skiing
        data object Rain : Skiing
        data object TooWarm : Skiing
        data object GettingColder : Skiing
        data object None : Skiing
    }

    sealed interface Surfing : ActivityDailyReason {
        data object Thunderstorm : Surfing
        data object Rain : Surfing
        data object WarmAndWindy : Surfing
        data object WindyOnly : Surfing
        data object WarmOnly : Surfing
        data object BuildingConditions : Surfing
        data object None : Surfing
    }

    sealed interface OutdoorSightseeing : ActivityDailyReason {
        data object ClearAndComfortable : OutdoorSightseeing
        data object Rain : OutdoorSightseeing
        data object Fog : OutdoorSightseeing
        data object ClearOnly : OutdoorSightseeing
        data object WarmingUp : OutdoorSightseeing
        data object None : OutdoorSightseeing
    }

    sealed interface IndoorSightseeing : ActivityDailyReason {
        data object PoorOutdoor : IndoorSightseeing
        data object ExtremeTemp : IndoorSightseeing
        data object GreatOutdoor : IndoorSightseeing
        data object None : IndoorSightseeing
    }

    sealed interface Cycling : ActivityDailyReason {
        data object Rain : Cycling
        data object Gusty : Cycling
        data object Comfortable : Cycling
        data object Cold : Cycling
        data object WindPickingUp : Cycling
        data object None : Cycling
    }

    sealed interface Sunbathing : ActivityDailyReason {
        data object Rain : Sunbathing
        data object Windy : Sunbathing
        data object WarmAndSunny : Sunbathing
        data object SunnyOnly : Sunbathing
        data object WarmOnly : Sunbathing
        data object WarmingUp : Sunbathing
        data object None : Sunbathing
    }

    sealed interface Stargazing : ActivityDailyReason {
        data object Rain : Stargazing
        data object Fog : Stargazing
        data object ClearAndLongNight : Stargazing
        data object ClearOnly : Stargazing
        data object LongNightOnly : Stargazing
        data object None : Stargazing
    }

    sealed interface Fishing : ActivityDailyReason {
        data object Thunderstorm : Fishing
        data object Windy : Fishing
        data object CalmAndDry : Fishing
        data object CalmOnly : Fishing
        data object DryOnly : Fishing
        data object WatersCalming : Fishing
        data object None : Fishing
    }
}
