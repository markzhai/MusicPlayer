package model;

import java.util.ArrayList;
import java.util.List;

public class SongList extends AbstractModelObject {
	private final List<Song> songs = new ArrayList();
	private String name;
	
	public SongList() { }
	
	public SongList(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		firePropertyChange("name", this.name, this.name = name);
	}
	
	public List getSongs() {
		return songs;
	}
	
	public void addSong(Song song) {
		songs.add(song);
		firePropertyChange("songs", null, this.songs);
	}
	public void removeSong(Song song) {
		songs.remove(song);
		firePropertyChange("songs", null, this.songs);
	}
	
	public void setTitle() {
	}
	
	public String getTitle() {
		return "";
	}
}