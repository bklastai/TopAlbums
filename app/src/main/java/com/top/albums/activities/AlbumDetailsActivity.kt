package com.top.albums.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.top.albums.R
import android.content.pm.PackageManager

class AlbumDetailsActivity : AppCompatActivity() {
    companion object {
        const val iekUrl =  "url"
        const val iekAlbumArt =  "albumArt"
        const val iekAlbumName = "albumName"
        const val iekArtistName = "artistName"
        const val iekGenre = "genre"
        const val iekReleaseDate = "releaseDate"
        const val iekCopyright = "copyright"
        const val appleMusicPackage = "com.apple.android.music"
    }

    private lateinit var albumArt: ImageView
    private lateinit var albumName: TextView
    private lateinit var artistName: TextView
    private lateinit var genre: TextView
    private lateinit var releaseDate: TextView
    private lateinit var copyright: TextView
    private lateinit var listen: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        initViews()
        setDetails(intent)
        listen.setOnClickListener { openAppleMusic(appleMusicPackage, intent) }
    }

    private fun initViews() {
        albumArt = findViewById(R.id.album_art)
        albumName = findViewById(R.id.album_name)
        artistName = findViewById(R.id.artist_name)
        genre = findViewById(R.id.genre)
        releaseDate = findViewById(R.id.release_date)
        copyright = findViewById(R.id.copyright)
        listen = findViewById(R.id.listen)
    }

    private fun setDetails(intent: Intent) {
        Picasso.get().load(intent.getStringExtra(iekAlbumArt)).into(albumArt)
        albumName.text = intent.getStringExtra(iekAlbumName)
        artistName.text = intent.getStringExtra(iekArtistName)
        genre.text = intent.getStringExtra(iekGenre)
        releaseDate.text = intent.getStringExtra(iekReleaseDate)
        copyright.text = intent.getStringExtra(iekCopyright)
    }

    private fun openAppleMusic(packageName: String, i: Intent) {
        val intent = Intent(Intent.ACTION_VIEW)
        if (packageExists(packageName)) {
            intent.data = Uri.parse(i.getStringExtra(iekUrl))
        } else {
            intent.data = Uri.parse("market://details?id=$packageName")
        }
        startActivity(intent)
    }

    private fun packageExists(targetPackage: String): Boolean {
        return try {
            packageManager.getPackageInfo(targetPackage, PackageManager.GET_META_DATA)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}