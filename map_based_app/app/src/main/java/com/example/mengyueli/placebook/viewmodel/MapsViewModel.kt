package com.example.mengyueli.placebook.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.graphics.Bitmap
import android.util.Log
import com.example.mengyueli.placebook.repository.BookmarkRepo
import com.google.android.gms.location.places.Place

// 1
class MapsViewModel(application: Application) :
        AndroidViewModel(application) {
    private val TAG = "MapsViewModel"
    // 2
    private var bookmarkRepo: BookmarkRepo = BookmarkRepo(
            getApplication())
    // 3
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
        Log.i(TAG, "New bookmark $newId added to the database.")
    }
}