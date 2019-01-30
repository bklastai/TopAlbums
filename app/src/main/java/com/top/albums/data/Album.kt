package com.top.albums.data

data class AlbumMainDetails(val url: String, val albumName: String, val artistName: String, val albumArt: String)

data class Album(val mainDetails: AlbumMainDetails, val genre: String, val releaseDate: String, val copyright: String)