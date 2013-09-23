package ui;
import java.util.ArrayList;

import processing.core.*;
import processing.event.*;
import g4p_controls.*;
import ddf.minim.*;
import ddf.minim.analysis.*;

public class MusicPlayer extends PApplet {
	private static final long serialVersionUID = 8077161629600021292L;
	
	public ArrayList<String> songs = new ArrayList<String>();
	private int songIndex = 0;
	private String songListName = null;
	private ControlPanelUI controlPanel;

	public static void main(String args[]) {
		String[] appletArgs = new String[] {"ui.MusicPlayer"};
		if (args != null) {
			PApplet.main(concat(appletArgs, args));
		} else {
			PApplet.main(appletArgs);
		}
	}

	/* GUI data */
	private int mX, mY;
	private int appWidth = 464, appHeight = 293;
	private int progressBarX = 184, progressBarY = 55, progressBarWidth = 116, progressBarHeight = 8;
	private int volumeBarX = 47, volumeBarY = 65, volumeBarWidth = 50;
	private int visualizerHeight = 293;
	private int songTextCenterX = 474, songTextY = 48;
	private int timeTextCenterX = 474 + progressBarX, timeTextY = 63;
	public int position = 30;
	private GWindow[] window;
	
	private Button btnPlay;
	private GImageToggleButton btnEqualizer, btnNext, btnPrev;
	private GImageButton btnProgressBar, btnProgressSeeker;
	private GImageButton btnVolumeBar, btnVolumeSeeker, btnVolumeMute, btnVolumeFull;
	
	private PImage player_skin;

	private PImage volumnSeeker;
	private PImage closeButton;
	private PImage minimizeButton;
	private PImage shuffle;
	private PImage repeat;
	private PImage repeatOn;

	private PFont font;
	
	/* audio file data */
	Minim minim;
	AudioPlayer player;
	FFT fft;
	AudioMetaData meta;
	boolean isPlaying;
	boolean isRepeat;
	private float minGain = -80, maxGain = (float) 6.0206;

	/* music control flag */
	private boolean controlPanelOpened = false;
	private boolean musicChanged = false;
	private boolean musicPlaying = false;
	
	public void init() {
		frame.removeNotify();
		frame.setUndecorated(true);
		frame.addNotify();

		super.init();
	}

	public void setup() {
		if (controlPanelOpened == false) {
	        SwtUI mySwtUI = new SwtUI(this);
	        Thread mySwtUIThread = new Thread(mySwtUI);
	        mySwtUIThread.start();
	        controlPanelOpened = true;
		}
		size(appWidth, appHeight + visualizerHeight, P3D);
		minim = new Minim(this);

		isPlaying = true;
		isRepeat = false;
		player_skin = loadImage("player_skin.png");

		font = loadFont("MS-PGothic-12.vlw");

		btnEqualizer = new GImageToggleButton(this, 403, 23, "equalizer.png", "equalizer_hot.png", 2, 1);
		btnPlay = new RoundButton(this, loadImage("play.png"), loadImage("play_hot.png"), 
				loadImage("pause.png"), loadImage("pause_hot.png"), 53, 22, 38, 38);
		btnNext = new GImageToggleButton(this, 96, 24, "next.png", "next_over.png", 1, 1);
		btnPrev = new GImageToggleButton(this, 15, 25, "prev.png", "prev_over.png", 1, 1);
				
		btnProgressBar = new GImageButton(this, progressBarX, progressBarY, new String[]{"progress_bar.png"});
		btnProgressSeeker = new GImageButton(this, progressBarX, progressBarY, new String[]{"progress_thumb.png"});
		btnVolumeBar = new GImageButton(this, volumeBarX, volumeBarY, new String[]{"volume_bar.png"});
		btnVolumeSeeker = new GImageButton(this, (float) (volumeBarX + 35.34037), volumeBarY - 4, new String[]{"volume_thumb.png"});
		btnVolumeMute = new GImageButton(this, 34, 61, new String[]{"volume_mute.png"});
		btnVolumeFull = new GImageButton(this, 101, 61, new String[]{"volume_full.png"});
	}
	public void draw() {
		background(0);
		loadCurrentSong();
		
		image(player_skin, 0, 0);
		btnPlay.display();
		ellipse(position, 150, 25, 25);

		if (player != null) {
			btnProgressSeeker.moveTo(progressBarX + ((float)player.position() / 
					player.length() * (progressBarWidth - btnProgressSeeker.getWidth())), progressBarY);
			btnVolumeSeeker.moveTo((float)(volumeBarX + ((player.getGain() + (float)80) / 
					(maxGain - minGain) * (volumeBarWidth - btnVolumeSeeker.getWidth()))), volumeBarY - 4);

			textMode(MODEL);
			textFont(font);
			//		textSize(14);
			fill(0, 102, 153);
			meta = player.getMetaData();
			//		String content = meta.author() + " - " + meta.title();
			String content = meta.title();
			text(content,  (songTextCenterX - textWidth(content)) / 2, songTextY);

			int timeLeft = player.position() - player.length();
			String timeLeftStr = String.format("%02d:%02d", timeLeft / 1000 / 60, -timeLeft / 1000 % 60);
			text(timeLeftStr, (timeTextCenterX - textWidth(timeLeftStr)) / 2, timeTextY);

			if (player.position() >= player.length()) {
				System.out.println("11");
				if (isRepeat) {
					player.rewind();
					player.play();
				} else if (songIndex < songs.size()) {
					++ songIndex;
					player = minim.loadFile(songs.get(songIndex));
					player.play();
				}
			}
			visualize();
		}
	}

	/**
	 * Create the three windows so that they share mouse handling and drawing
	 * code.
	 */
	public void createWindows() {
		window = new GWindow[1];
		for (int i = 0; i < 1; i++) {
			// int col = (128 << (i * 8)) | 0xff000000;
			window[i] = new GWindow(this, "Window " + i, 70 + i * 220, 160 + i * 50, 200, 200, false, JAVA2D);
			// window[i].setBackground(col);
			window[i].addData(new MyWinData());
			window[i].addDrawHandler(this, "windowDraw");
			window[i].addMouseHandler(this, "windowMouse");
			window[i].setActionOnClose(GWindow.CLOSE_WINDOW);
			window[i].setOnTop(false);
		}
	}

	private void loadCurrentSong() {
		if (musicChanged) {
			if (player != null)
				player.close();
			player = minim.loadFile(songs.get(songIndex));
			player.play();
			fft = new FFT(player.bufferSize(), player.sampleRate());
			fft.linAverages(128);
			musicChanged = false;
			musicPlaying = true;
		} else if (player!= null && !musicPlaying) {
			player.pause();
		} else if (player!= null && musicPlaying && !player.isPlaying()) {
			player.play();
		}
	}
	
	private void closeWindows() {
		for (int i = 0; i < 1; i++) {
			window[i].forceClose();
		}
		window = null;
	}

	public void mousePressed() {
		mX = mouseX;
		mY = mouseY;
		btnPlay.update();
	}

	public void mouseDragged() {
		// java.awt.Point p = frame.getLocation();
		// frame.setLocation(p.x + mouseX - mX, p.y + mouseY - mY);
		java.awt.Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
		frame.setLocation(p.x - mX, p.y - mY);
	}

	public void handleButtonEvents(GImageButton button, GEvent event) {
		if (player != null && button == btnProgressBar) {
		    float newSongPosition = map(mouseX, btnProgressBar.getX(), 
		    		btnProgressBar.getX()+ btnProgressBar.getWidth(),  0, player.length()) ; 
		    player.cue((int)newSongPosition);
		} else if (player != null && button == btnVolumeBar) {
			float newVolumePosition = map(mouseX, btnVolumeBar.getX(), 
					btnVolumeBar.getX()+ btnVolumeBar.getWidth(), minGain, maxGain);
			System.out.println(newVolumePosition);
			player.setGain(newVolumePosition);
		} else if (player != null && button == btnVolumeMute) {
			player.setGain(minGain);
		} else if (player != null && button == btnVolumeFull) {
			player.setGain(maxGain);
		}
	}
	
	public void handleToggleButtonEvents(GImageToggleButton button, GEvent event) {
		if (window == null && button == btnEqualizer) {
			createWindows();
		} else if (button == btnPrev) {
			if (songIndex > 0)
				-- songIndex;
			musicChanged = true;
		} else if (button == btnNext) {
			if (songIndex < songs.size() - 1)
				++ songIndex;
			musicChanged = true;
		} else {
			//closeWindows();
		}
	}

	public void visualize() {
		fft.forward(player.mix);
		stroke(255, 0, 0, 128);
		for (int i = 0; i < fft.specSize(); i++) {
			line(i, height, i, height - fft.getBand(i) * 4);
		}
	}
	/**
	 * Handles mouse events for ALL GWindow objects
	 * 
	 * @param appc
	 *            the PApplet object embeded into the frame
	 * @param data
	 *            the data for the GWindow being used
	 * @param event
	 *            the mouse event
	 */
	public void windowMouse(GWinApplet appc, GWinData data, MouseEvent event) {
		MyWinData data2 = (MyWinData) data;
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			data2.sx = data2.ex = appc.mouseX;
			data2.sy = data2.ey = appc.mouseY;
			data2.done = false;
			break;
		case MouseEvent.RELEASE:
			data2.ex = appc.mouseX;
			data2.ey = appc.mouseY;
			data2.done = true;
			break;
		case MouseEvent.DRAG:
			data2.ex = appc.mouseX;
			data2.ey = appc.mouseY;
			break;
		}
	}

	/**
	 * Handles drawing to the windows PApplet area
	 * 
	 * @param appc
	 *            the PApplet object embeded into the frame
	 * @param data
	 *            the data for the GWindow being used
	 */
	void windowDraw(GWinApplet appc, GWinData data) {
		MyWinData data2 = (MyWinData) data;
		if (!(data2.sx == data2.ex && data2.ey == data2.ey)) {
			appc.stroke(255);
			appc.strokeWeight(2);
			appc.noFill();
			if (data2.done) {
				appc.fill(128);
			}
			appc.rectMode(CORNERS);
			appc.rect(data2.sx, data2.sy, data2.ex, data2.ey);
		}
	}

	public boolean isControlPanelOpened() {
		return controlPanelOpened;
	}

	public void setControlPanelOpened(boolean controlPanelOpened) {
		this.controlPanelOpened = controlPanelOpened;
	}
	
	public boolean isMusicPlaying() {
		return musicPlaying;
	}

	public void setMusicPlaying(boolean musicPlaying) {
		this.musicPlaying = musicPlaying;
	}

	public boolean isMusicChanged() {
		return musicChanged;
	}

	public void setMusicChanged(boolean musicChanged) {
		this.musicChanged = musicChanged;
	}

	public ArrayList<String> getSongs() {
		return songs;
	}

	public void setSongs(ArrayList<String> songs) {
		this.songs = songs;
	}
	
	public int getSongIndex() {
		return songIndex;
	}

	public void setSongIndex(int songIndex) {
		this.songIndex = songIndex;
	}
	
	public String getSongListName() {
		return songListName;
	}

	public void setSongListName(String listName) {
		this.songListName = listName;
	}

	public ControlPanelUI getControlPanel() {
		return controlPanel;
	}

	public void setControlPanel(ControlPanelUI controlPanel) {
		this.controlPanel = controlPanel;
	}
	
	/**
	 * Simple class that extends GWinData and holds the data that is specific to
	 * a particular window.
	 * 
	 * @author Peter Lager
	 */
	class MyWinData extends GWinData {
		int sx, sy, ex, ey;
		boolean done;
	}
}