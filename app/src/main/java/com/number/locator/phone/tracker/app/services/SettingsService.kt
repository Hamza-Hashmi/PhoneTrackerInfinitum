package com.phone.tracker.locate.number.app.services

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.number.locator.phone.tracker.app.services.CustomSpeedometerService
import com.number.locator.phone.tracker.app.utills.context


@RequiresApi(Build.VERSION_CODES.N)
class SettingsService : TileService() {

    override fun onStartListening() {
        updateState(CustomSpeedometerService.isRunning(context))
    }

    private fun updateState(running: Boolean) {
        qsTile.apply {
            state = if (running) {
                Tile.STATE_ACTIVE
            } else {
                Tile.STATE_INACTIVE
            }
            updateTile()
        }
    }

    override fun onClick() {
        updateState(!CustomSpeedometerService.isRunning(context))
        CustomSpeedometerService.popupStateOfRunning(context)
    }

}