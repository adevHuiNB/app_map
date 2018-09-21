package com.example.mengyueli.placebook.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.graphics.Bitmap
import com.example.mengyueli.placebook.util.ImageUtils

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
{
    // 1
    fun setImage(image: Bitmap, context: Context) {
        // 2
        id?.let {
            ImageUtils.saveBitmapToFile(context, image,
                    generateImageFilename(it))
        }
    }
    //3
    companion object {
        fun generateImageFilename(id: Long): String {
// 4
            return "bookmark$id.png"
        }
    } }