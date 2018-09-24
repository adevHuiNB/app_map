package com.example.mengyueli.placebook.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.mengyueli.placebook.R
import com.example.mengyueli.placebook.ui.MapsActivity
import com.example.mengyueli.placebook.viewmodel.MapsViewModel

// 1
class BookmarkListAdapter(
        private var bookmarkData: List<MapsViewModel.BookmarkView>?,
        private val mapsActivity: MapsActivity) :
        RecyclerView.Adapter<BookmarkListAdapter.ViewHolder>() {
    // 2
    class ViewHolder(v: View,
                     private val mapsActivity: MapsActivity) :
            RecyclerView.ViewHolder(v) {

        init {
            v.setOnClickListener {
                val bookmarkView = itemView.tag as MapsViewModel.BookmarkView
                mapsActivity.moveToBookmark(bookmarkView)
            }
        }
        val nameTextView: TextView =
                v.findViewById(R.id.bookmarkNameTextView) as TextView
        val categoryImageView: ImageView =
                v.findViewById(R.id.bookmarkIcon) as ImageView
    }
    // 3
    fun setBookmarkData(bookmarks: List<MapsViewModel.BookmarkView>) {
        this.bookmarkData = bookmarks
        notifyDataSetChanged()
    }
    // 4
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int): BookmarkListAdapter.ViewHolder {
        val vh = ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.bookmark_item, parent, false), mapsActivity)
        return vh }

    override fun onBindViewHolder(holder: ViewHolder,
                                  position: Int) {
// 5
        val bookmarkData = bookmarkData ?: return
        // 6
        val bookmarkViewData = bookmarkData[position]
        // 7
        holder.itemView.tag = bookmarkViewData
        holder.nameTextView.text = bookmarkViewData.name
        holder.categoryImageView.setImageResource(
                R.drawable.ic_other)
    }
    // 8
    override fun getItemCount(): Int {
        return bookmarkData?.size ?: 0
    }
}