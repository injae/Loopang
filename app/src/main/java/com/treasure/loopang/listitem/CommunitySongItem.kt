package com.treasure.loopang.listitem

import android.widget.ImageView

data class CommunitySongItem(var userNickName: String = "", var songName: String = "",
                             var likedNum: Int = 0, var downloadNum: Int = 0,
                             var TrackInfo: String = "", var productionDate: String = "",
                             var songId: String = "")