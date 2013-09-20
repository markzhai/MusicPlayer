package model;

public class Song extends AbstractModelObject {
	private String title;
	private String artist;
	private String filename;
	private String style;
	
	public Song() { }
	
	public Song(String title, String artist) {
		this.title = title;
		this.artist = artist;
	}
	
	public Song(String title, String artist, String style) {
		this.title = title;
		this.artist = artist;
		this.style = style;
	}
	
	public Song(String title, String artist, String style, String filename) {
		this.title = title;
		this.artist = artist;
		this.style = style;
		this.filename = filename;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		String oldValue = this.title;
		this.title = title;
		firePropertyChange("title", oldValue, this.title);
	}
	
	public String getArtist() {
		return artist;
	}
	
	public void setArtist(String artist) {
		firePropertyChange("artist", this.artist, this.artist = artist);
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		firePropertyChange("filename", this.filename, this.filename = filename);
	}
	
	public String getStyle() {
		return style;
	}
	
	public void setStyle(String style) {
		firePropertyChange("style", this.style, this.style = style);
	}
}
