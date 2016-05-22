package com.example.music.model;

import android.graphics.Bitmap;

public class Track {
	// 歌曲名
	private String title;
	// 专辑封面
	private Bitmap art;
	// 歌手
	private String artist;
	private long id;
	// 专辑Id
	private long albumId;
	// 文件位置
	private String url;
	// 时长
	private long duration;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Bitmap getArt() {
		return art;
	}

	public void setArt(Bitmap art) {
		this.art = art;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

}
