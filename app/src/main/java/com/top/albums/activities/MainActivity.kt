package com.top.albums.activities

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.top.albums.adapters.MainAdapter
import com.top.albums.R
import com.top.albums.data.Album
import org.json.JSONObject
import java.net.URL
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.top.albums.data.AlbumMainDetails
import android.net.ConnectivityManager
import android.content.Context
import android.content.Intent
import android.provider.Settings

interface AlbumLoadListener {
    fun onFinished(result: ArrayList<Album>)
}

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var error: TextView
    private lateinit var recyclerView: RecyclerView
    private var mainAdapter: MainAdapter = MainAdapter(this, ArrayList())
    private lateinit var linLayoutManager: LinearLayoutManager
    private val albumLoadListener = object : AlbumLoadListener {
        override fun onFinished(result: ArrayList<Album>) {
            setAdapter(result)
        }
    }
    private var loadTask: LoadAlbumsAsync? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        linLayoutManager = LinearLayoutManager(this)
        setupViews()
    }

    override fun onResume() {
        super.onResume()
        loadAlbums()
    }

    private fun setupViews() {
        progressBar = findViewById(R.id.progress_bar)
        error = findViewById(R.id.error)
        error.setOnClickListener {
            if (isNetworkAvailable()) {
                loadAlbums()
            } else {
                startActivityForResult(Intent(Settings.ACTION_SETTINGS), 0);
            }
        }

        recyclerView = findViewById<RecyclerView>(R.id.recycler).apply {
            setHasFixedSize(true)
            layoutManager = linLayoutManager as RecyclerView.LayoutManager
            adapter = mainAdapter
        }
        val mDividerItemDecoration = DividerItemDecoration(recyclerView.context, linLayoutManager.orientation)
        recyclerView.addItemDecoration(mDividerItemDecoration)
    }

    private fun loadAlbums() {
        if (!isNetworkAvailable()) {
            error.text = getString(R.string.no_connection)
            error.visibility = VISIBLE
            progressBar.visibility = GONE
            mainAdapter.clear()
        } else {
            error.text = getString(R.string.try_again)
            error.visibility = GONE
            if (mainAdapter.itemCount == 0) progressBar.visibility = VISIBLE
            loadTask = LoadAlbumsAsync(albumLoadListener)
            loadTask?.execute()
        }
    }

    private fun setAdapter(albums: ArrayList<Album>) {
        progressBar.visibility = GONE
        mainAdapter = MainAdapter(this, albums)
        recyclerView.adapter = mainAdapter
        mainAdapter.notifyDataSetChanged()
        error.visibility = if (albums.isEmpty()) VISIBLE else GONE
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun onStop() {
        super.onStop()
        loadTask?.cancel(true)
    }

    private class LoadAlbumsAsync(private val callback: AlbumLoadListener) : AsyncTask<Void, Void, ArrayList<Album>>() {
        override fun doInBackground(vararg params: Void?): ArrayList<Album> {
            val albums = ArrayList<Album>()
            val forecastJsonStr =
                URL("https://rss.itunes.apple.com/api/v1/us/apple-music/top-albums/all/25/explicit.json").readText()
            val forecastJson = JSONObject(forecastJsonStr)
            val feed = forecastJson.optJSONObject("feed")

            feed.run {
                optJSONArray("results")?.run {

                    for (i in 0 until length()) {
                        val album = getJSONObject(i)

                        val url = album.optString("url")
                        val albumName = album.optString("name")
                        val artistName = album.optString("artistName")
                        val albumArt = album.optString("artworkUrl100")
                        val amd = AlbumMainDetails(url, albumName, artistName, albumArt)


                        val releaseDate = album.optString("releaseDate")
                        val copyright = album.optString("copyright")
                        val genres = album.optJSONArray("genres")
                        var genre: String? = null
                        if (genres != null && genres.length() > 0) {
                            genre = genres.optJSONObject(0)?.optString("name")
                        }
                        albums.add(Album(amd, (genre ?: "N/A"), releaseDate, copyright))
                    }
                }
            }
            return albums
        }

        override fun onPostExecute(result: ArrayList<Album>) {
            callback.onFinished(result)
        }
    }
}
