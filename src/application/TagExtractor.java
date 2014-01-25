package application;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class TagExtractor {
	private String filepath;
	private AudioFile file;
	private AudioHeader header;
	private Tag tag;
	
	public TagExtractor() {
		
	}
	
	public TagExtractor(String filepath) {
		this.setFilepath(filepath);
	}

	public void setStyle(String style) {
		try {
			System.out.println(style);
			tag.setField(FieldKey.GENRE, style);
			file.setTag(tag);
			file.commit();
		} catch (KeyNotFoundException e) {
			e.printStackTrace();
		} catch (FieldDataInvalidException e) {
			e.printStackTrace();
		} catch (CannotWriteException e) {
			e.printStackTrace();
		}
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
		String title = tag.getFirst(FieldKey.TITLE);
		if (title.isEmpty()) {
			int beginIndex = filepath.lastIndexOf("- ") + 2;
			if (beginIndex < 2)
				beginIndex = filepath.lastIndexOf('-') + 1;
			if (beginIndex < 1)
				beginIndex = filepath.lastIndexOf('\\') + 1;
			int endIndex = filepath.lastIndexOf('.');
			
			title = filepath.substring(beginIndex, endIndex);
		}
		return title;
	}
	
	public String getAlbum() {
		return tag.getFirst(FieldKey.ALBUM);
	}

	public String getArtist() {
		String artist = tag.getFirst(FieldKey.ARTIST);
		if (artist.isEmpty()) {
			int beginIndex = filepath.lastIndexOf('\\') + 1;
			int endIndex = filepath.lastIndexOf(" - ");
			if (beginIndex > 0 && endIndex > 0 && endIndex > beginIndex)
				artist = filepath.substring(beginIndex, endIndex);
		}
		return artist;
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
