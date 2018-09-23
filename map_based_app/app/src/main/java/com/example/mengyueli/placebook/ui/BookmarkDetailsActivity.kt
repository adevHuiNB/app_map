package com.example.mengyueli.placebook.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import com.example.mengyueli.placebook.R
import com.example.mengyueli.placebook.viewmodel.BookmarkDetailsViewModel
import kotlinx.android.synthetic.main.activity_bookmark_details.*

class BookmarkDetailsActivity : AppCompatActivity() {

    private lateinit var bookmarkDetailsViewModel:
            BookmarkDetailsViewModel
    private var bookmarkDetailsView:
            BookmarkDetailsViewModel.BookmarkDetailsView? = null

    override fun onCreate(savedInstanceState:
                          android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_details)
        setupToolbar()
        setupViewModel()
        getIntentData()
    }
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setupViewModel() {
        bookmarkDetailsViewModel =
                ViewModelProviders.of(this).get(
                        BookmarkDetailsViewModel::class.java)
    }

    private fun populateFields() {
        bookmarkDetailsView?.let { bookmarkView ->
            editTextName.setText(bookmarkView.name)
            editTextPhone.setText(bookmarkView.phone)
            editTextNotes.setText(bookmarkView.notes)
            editTextAddress.setText(bookmarkView.address)
        }
    }

    private fun populateImageView() {
        bookmarkDetailsView?.let { bookmarkView ->
            val placeImage = bookmarkView.getImage(this)
            placeImage?.let {
                imageViewPlace.setImageBitmap(placeImage)
            }
        }
    }

    private fun getIntentData() {
        // 1
        val bookmarkId = intent.getLongExtra(
                MapsActivity.Companion.EXTRA_BOOKMARK_ID, 0)
        // 2
        bookmarkDetailsViewModel.getBookmark(bookmarkId)?.observe(
                this, Observer<BookmarkDetailsViewModel.BookmarkDetailsView> {
            // 3
            it?.let {
                bookmarkDetailsView = it
                // Populate fields from bookmark
                populateFields()
                populateImageView()
            } })
    }
}