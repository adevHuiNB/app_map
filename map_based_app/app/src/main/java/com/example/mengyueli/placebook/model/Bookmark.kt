package com.example.mengyueli.placebook.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

// 1
@Entity
// 2
data class Bookmark(
        // 3
        @PrimaryKey(autoGenerate = true) var id: Long? = null,
        // 4
        var placeId: String? = null,
        var name: String = "",
        var address: String = "",
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var phone: String = ""
)