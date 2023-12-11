package com.phone.tracker.locate.number.app.getlocations

sealed class ChangeCurrentSpeed {

    class UpdateLiveSpeed(val speed: Float) : ChangeCurrentSpeed()

    object CurrentSpeedAvailability : ChangeCurrentSpeed()

}