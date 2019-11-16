package com.treasure.loopang.listitem

import android.widget.ImageButton

data class CommunitySongItem(var userNickName: String = "", var songName: String = "",
                             var likedNum: Int = 0, var downloadNum: Int = 0,
                             var trackInfo: String = "", var productionDate: String = "",
                             var songId: String = "")