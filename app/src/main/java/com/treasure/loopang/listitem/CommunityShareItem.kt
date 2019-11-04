package com.treasure.loopang.listitem

import com.treasure.loopang.audio.LoopMusic

data class CommunityShareItem (
    var loopTitle: String = "loop_title",
    var dateString: String = "2019/05/29",
  //  val filePath: String? = null,
    var childItems: MutableList<LoopMusic>? = null)