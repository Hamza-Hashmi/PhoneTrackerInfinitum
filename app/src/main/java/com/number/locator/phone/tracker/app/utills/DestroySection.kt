package com.phone.tracker.locate.number.app.utills



class DestroySection : DestroyInterFace {

    private val destroyables = mutableListOf<DestroyInterFace>()

    fun <T : DestroyInterFace> own1(destroyable: T): T {
        destroyables.add(destroyable)
        return destroyable
    }



    fun own(destroyable: () -> Unit) {
        destroyables.add(object : DestroyInterFace {
            override fun destroy() {
                destroyable.invoke()
            }
        })
    }

    override fun destroy() {
        for (destroyable in destroyables) {
            destroyable.destroy()
        }
        destroyables.clear()
    }

}
