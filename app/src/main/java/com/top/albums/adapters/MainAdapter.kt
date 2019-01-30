package com.top.albums.adapters

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.top.albums.data.Album
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.squareup.picasso.Picasso
import com.top.albums.R
import com.top.albums.activities.AlbumDetailsActivity

class MainAdapter(private val activity: Activity, private val albums: ArrayList<Album>) : RecyclerView.Adapter<MainAdapter.AlbumViewHolder>() {

    class AlbumViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var albumName: TextView = itemView.findViewById(R.id.album_name)
        var artistName: TextView = itemView.findViewById(R.id.artist_name)
        var albumArt: ImageView = itemView.findViewById(R.id.album_art) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val listItem = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_album, parent, false) as ViewGroup
        Log.d("TESTIN", "onCreateViewHolder =  ")
        return AlbumViewHolder(listItem)

    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = getItem(position)

        holder.albumName.text = album?.mainDetails?.albumName.toString()
        holder.artistName.text = album?.mainDetails?.artistName.toString()
        Picasso.get().load(album?.mainDetails?.albumArt).into(holder.albumArt)

        holder.itemView.setOnClickListener {
            startActivity(activity, getIntent(album), null)
        }
        Log.d("TESTIN", "name =  " + album?.mainDetails?.albumName.toString())
    }

    private fun getIntent(album: Album?): Intent {
        return Intent(activity, AlbumDetailsActivity::class.java).apply {
            putExtra(AlbumDetailsActivity.iekUrl, album?.mainDetails?.url)
            putExtra(AlbumDetailsActivity.iekAlbumArt, album?.mainDetails?.albumArt)
            putExtra(AlbumDetailsActivity.iekAlbumName, album?.mainDetails?.albumName)
            putExtra(AlbumDetailsActivity.iekArtistName, album?.mainDetails?.artistName)
            putExtra(AlbumDetailsActivity.iekGenre, album?.genre)
            putExtra(AlbumDetailsActivity.iekReleaseDate, album?.releaseDate)
            putExtra(AlbumDetailsActivity.iekCopyright, album?.copyright)
        }
    }

    override fun getItemCount() = albums.size

    private fun getItem(position: Int): Album? {
        if (position >= itemCount) return null
        return albums[position]
    }

    fun clear() {
        albums.clear()
        notifyDataSetChanged()
    }
}