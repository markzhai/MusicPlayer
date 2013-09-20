package tools;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class TagExtractor {
	private String filepath;
	private AudioFile file;
	private AudioHeader header;
	private Tag tag;
	
	public static void main(String args[]) {
		TagExtractor extractor = new TagExtractor("e:\\川田まみ - PSI - missing.mp3");
		System.out.println(extractor.getAlbum());
		System.out.println(extractor.getFormat());
		System.out.println(extractor.getEncodingType());
	}
	
	public TagExtractor() {}
	
	public TagExtractor(String filepath) {
		this.setFilepath(filepath);
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
		try {
			File f = new File(filepath);
			file = AudioFileIO.read(f);
			tag = file.getTag();
			header = file.getAudioHeader();
		} catch (CannotReadException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TagException e) {
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			e.printStackTrace();
		}
	}
	
	public String getTitle() {
		return tag.getFirst(FieldKey.TITLE);
	}
	
	public String getAlbum() {
		return tag.getFirst(FieldKey.ALBUM);
	}

	public String getArtist() {
		return tag.getFirst(FieldKey.ARTIST);
	}
	
	public String getComment() {
		return tag.getFirst(FieldKey.COMMENT);
	}

	public String getDiscNo() {
		return tag.getFirst(FieldKey.DISC_NO);
	}

	public String getTrack() {
		return tag.getFirst(FieldKey.TRACK);
	}
	
	public String getComposer() {
		return tag.getFirst(FieldKey.COMPOSER);
	}

	public String getYear() {
		return tag.getFirst(FieldKey.YEAR);
	}

	public String getArtistSort() {
		return tag.getFirst(FieldKey.ARTIST_SORT);
	}

	public String getGenre() {
		return tag.getFirst(FieldKey.GENRE);
	}
	
	public String getEncodingType() {
		return header.getEncodingType();
	}
	
	public int getTrackLength() {
		return header.getTrackLength();
	}
	
	public String getSampleRate() {
		return header.getSampleRate();
	}
	
	public String getBitRate() {
		return header.getBitRate();
	}
	
	public String getFormat() {
		return header.getFormat();
	}
}
