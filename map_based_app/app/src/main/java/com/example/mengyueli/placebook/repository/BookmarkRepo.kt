package com.example.mengyueli.placebook.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import com.example.mengyueli.placebook.db.BookmarkDao
import com.example.mengyueli.placebook.db.PlaceBookDatabase
import com.example.mengyueli.placebook.model.Bookmark

// 1
class BookmarkRepo(private val context: Context) {
    // 2
    private var db: PlaceBookDatabase =
            PlaceBookDatabase.getInstance(context)
    private var bookmarkDao: BookmarkDao = db.bookmarkDao()
// 3
fun addBookmark(bookmark: Bookmark): Long? {
    val newId = bookmarkDao.insertBookmark(bookmark)
    bookmark.id = newId
    return newId
}
    // 4
    fun createBookmark(): Bookmark {
        return Bookmark()
    }
    // 5
    val allBookmarks: LiveData<List<Bookmark>>
        get() {
            return bookmarkDao.loadAll()
        }

    fun getLiveBookmark(bookmarkId: Long): LiveData<Bookmark> {
        val bookmark = bookmarkDao.loadLiveBookmark(bookmarkId)
        return bookmark
    }

    fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.updateBookmark(bookmark)
    }
    fun getBookmark(bookmarkId: Long): Bookmark {
        return bookmarkDao.loadBookmark(bookmarkId)
    }
}