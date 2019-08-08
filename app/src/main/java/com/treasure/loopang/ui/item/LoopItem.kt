package com.treasure.loopang.ui.item

import com.treasure.loopang.audio.LoopMusic

class LoopItem (var loopTitle: String = "loop_title",
                val dateString: String = "2019/05/29",
                val filePath: String? = null,
                var playState: Boolean = false,
                var childItems: MutableList<LoopMusic>? = null)