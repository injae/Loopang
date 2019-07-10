package com.treasure.loopang.ui.item

import com.treasure.loopang.audiov2.Sound

class LayerItem (
    val sound: Sound,
    var layerLabel: String = "Layer #",
    var muteState: Boolean = false,
    var effectState: Boolean = false
)