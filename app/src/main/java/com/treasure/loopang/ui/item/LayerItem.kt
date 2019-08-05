package com.treasure.loopang.ui.item

import com.treasure.loopang.audio.Sound

class LayerItem (
    val sound: Sound,
    var layerLabel: String = "Layer #",
    var muteState: Boolean = false,
    var effectState: Boolean = false
)