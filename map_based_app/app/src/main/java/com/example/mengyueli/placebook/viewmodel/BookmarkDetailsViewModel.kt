package com.example.mengyueli.placebook.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import android.graphics.Bitmap
import com.example.mengyueli.placebook.model.Bookmark
import com.example.mengyueli.placebook.repository.BookmarkRepo
import com.example.mengyueli.placebook.util.ImageUtils

class BookmarkDetailsViewModel(application: Application) :
        AndroidViewModel(application) {

    private var bookmarkDetailsView: LiveData<BookmarkDetailsView>? = null

    private var bookmarkRepo: BookmarkRepo =
            BookmarkRepo(getApplication())


    data class BookmarkDetailsView(
            var id: Long? = null,
            var name: String = "",
            var phone: String = "",
            var address: String = "",
            var notes: String = ""
    )
    {
        fun getImage(context: Context): Bitmap? {
            id?.let {
                return ImageUtils.loadBitmapFromFile(context,
                        Bookmark.generateImageFilename(it))
            }
            return null
        }
    }

    private fun mapBookmarkToBookmarkView(bookmarkId: Long) {
        val bookmark = bookmarkRepo.getLiveBookmark(bookmarkId)
        bookmarkDetailsView = Transformations.map(bookmark) { bookmark ->
            val bookmarkView = bookmarkToBookmarkView(bookmark)
            bookmarkView
        }
    }

    fun getBookmark(bookmarkId: Long): LiveData<BookmarkDetailsView>? {
        if (bookmarkDetailsView == null) {
            mapBookmarkToBookmarkView(bookmarkId)
        }
        return bookmarkDetailsView
    }

    private fun bookmarkToBookmarkView(bookmark: Bookmark):
            BookmarkDetailsView {
        return BookmarkDetailsView(
                bookmark.id,
                bookmark.name,
                bookmark.phone,
                bookmark.address,
                bookmark.notes
        )
    }
}