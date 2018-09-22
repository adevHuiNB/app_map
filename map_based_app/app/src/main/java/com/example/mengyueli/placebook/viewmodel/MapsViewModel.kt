package com.example.mengyueli.placebook.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.mengyueli.placebook.model.Bookmark
import com.example.mengyueli.placebook.repository.BookmarkRepo
import com.example.mengyueli.placebook.util.ImageUtils
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng

// 1
class MapsViewModel(application: Application) :
        AndroidViewModel(application) {

    data class BookmarkMarkerView(
            var id: Long? = null,
            var location: LatLng = LatLng(0.0, 0.0),
            var name: String = "",
            var phone: String = ""
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


    private var bookmarks: LiveData<List<BookmarkMarkerView>>?
            = null

    private val TAG = "MapsViewModel"
    // 2
    private var bookmarkRepo: BookmarkRepo = BookmarkRepo(
            getApplication())
    // 3


    private fun bookmarkToMarkerView(bookmark: Bookmark):
            MapsViewModel.BookmarkMarkerView {
        return MapsViewModel.BookmarkMarkerView(
                bookmark.id,
                LatLng(bookmark.latitude, bookmark.longitude),
                bookmark.name,
                bookmark.phone)
    }

    private fun mapBookmarksToMarkerView() {
        // 1
        val allBookmarks = bookmarkRepo.allBookmarks
        // 2
        bookmarks = Transformations.map(allBookmarks) { bookmarks ->
            val bookmarkMarkerViews = bookmarks.map { bookmark ->
                bookmarkToMarkerView(bookmark)
            }
            bookmarkMarkerViews
        }
    }




    fun getBookmarkMarkerViews() :
            LiveData<List<BookmarkMarkerView>>? {
        if (bookmarks == null) {
            mapBookmarksToMarkerView()
        }
        return bookmarks
    }



    fun addBookmarkFromPlace(place: Place, image: Bitmap) {
        // 4
        val bookmark = bookmarkRepo.createBookmark()
        bookmark.placeId = place.id
        bookmark.name = place.name.toString()
        bookmark.longitude = place.latLng.longitude
        bookmark.latitude = place.latLng.latitude
        bookmark.phone = place.phoneNumber.toString()
        bookmark.address = place.address.toString()
        // 5
        val newId = bookmarkRepo.addBookmark(bookmark)
        bookmark.setImage(image, getApplication())
        Log.i(TAG, "New bookmark $newId added to the database.")
    }
}