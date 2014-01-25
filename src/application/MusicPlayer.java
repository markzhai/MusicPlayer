package application;

import java.awt.Image;
import java.util.ArrayList;

import processing.core.*;
import processing.event.*;
import ui.MyButton;
import ui.RoundButton;
import ui.SwtUI;
import g4p_controls.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import effects.ChangePitchEffect;
import effects.EchoEffect;
import effects.FadeInEffect;
import effects.FadeOutEffect;
import effects.ReverseEffect;

// Resize the window may cause crash because of processing bug: https://github.com/processing/processing/issues/1880
public class MusicPlayer extends PApplet {
	private static final long serialVersionUID = 8077161629600021292L;
	private String songListName = null;
	private ControlPanelUI controlPanel;
	
	public ArrayList<String> songs = new ArrayList<String>();
	public int songIndex = 0;
	
	/* GUI data */
	public int mX, mY;

	private MyButton btnPlay;
	private GImageToggleButton btnEffect, btnNext, btnPrev;
	private GImageButton btnProgressBar, btnProgressSeeker;
	private GImageButton btnVolumeBar, btnVolumeSeeker, btnVolumeMute, btnVolumeFull;

	private PImage player_skin;
	private PFont font;
	
	public boolean fullscreenMode = false;	
	private boolean fullscreenToggle = false;

	/* audio file data */
	public Minim minim;
	public AudioPlayer player;
	public FFT fft;
	private TagExtractor tagExtractor = new TagExtractor();
	
	/* music control flag */
	private boolean controlPanelOpened = false;
	private boolean musicChanged = false;
	private boolean musicPause = false;
	private boolean isRepeat = false;

	private AudioEffect[] audioEffects = new AudioEffect[Config.EFFECT_NUM];
	public boolean effectOn = false;
	
	/* visualizer */
	private Visualizer visualizer;

	/* game */
	private RhythmGame game;
	private boolean gameModeOn = false;
	public int gameDifficulty = 6;

	public static void main(String args[]) {
		//String[] appletArgs = new String[] {"--present", "application.MusicPlayer"};
		String[] appletArgs = new String[] {"application.MusicPlayer"};
		if (args != null) {
			PApplet.main(concat(appletArgs, args));
		} else {
			PApplet.main(appletArgs);
		}
	}
	
	public void init() {
		PImage icon = loadImage(Config.ICON);
		frame.setIconImage((Image) icon.getNative());
		frame.removeNotify();
		frame.setUndecorated(true);
		frame.addNotify();
		frame.setResizable(true);
		super.init();
	}

	public void setup() {
		//frameRate(30);
		smooth();
		noFill();
		background(0);
		size(Config.appWidth, Config.appHeight + Config.visualizerHeight, P3D);
		if (frame != null) {
			frame.setResizable(true);
		}
		minim = new Minim(this);

		if (controlPanelOpened == false) {
			SwtUI mySwtUI = new SwtUI(this);
			Thread mySwtUIThread = new Thread(mySwtUI);
			mySwtUIThread.start();
			controlPanelOpened = true;
		}
		
		player_skin = loadImage(Config.PLAYER_SKIN);
		font = loadFont(Config.PLAYER_FONT);

		btnEffect = new GImageToggleButton(this, 403, 23, Config.PLAYER_EQ[0], Config.PLAYER_EQ[1], 2, 1);
		btnPlay = new RoundButton(this, loadImage(Config.PLAYER_PLAY[0]), loadImage(Config.PLAYER_PLAY[1]), 
										loadImage(Config.PLAYER_PLAY[2]), loadImage(Config.PLAYER_PLAY[3]), 53, 22, 38, 38);
		btnNext = new GImageToggleButton(this, 96, 24, Config.PLAYER_NEXT[0], Config.PLAYER_NEXT[1], 1, 1);
		btnPrev = new GImageToggleButton(this, 15, 25, Config.PLAYER_PREV[0], Config.PLAYER_PREV[1], 1, 1);

		btnProgressBar = new GImageButton(this, Config.progressBarX, Config.progressBarY, new String[] {Config.PLAYER_PROGRESS[0]});
		btnProgressSeeker = new GImageButton(this, Config.progressBarX, Config.progressBarY, new String[] {Config.PLAYER_PROGRESS[1]});
		btnVolumeBar = new GImageButton(this, Config.volumeBarX, Config.volumeBarY, new String[] {Config.PLAYER_VOLUME[0]});
		btnVolumeSeeker = new GImageButton(this, (float) (Config.volumeBarX + 35.34037), Config.volumeBarY - 4, new String[] {Config.PLAYER_VOLUME[1]});
		btnVolumeMute = new GImageButton(this, 34, 61, new String[] {Config.PLAYER_VOLUME[2]});
		btnVolumeFull = new GImageButton(this, 101, 61, new String[] {Config.PLAYER_VOLUME[3]});
		
		visualizer = new Visualizer(this);
		visualizer.setArea(0, Config.appHeight, Config.appWidth, Config.visualizerHeight, true);
		game = new RhythmGame(this, gameDifficulty);
	}

	public void draw() {
		loadCurrentSong();

		if (player != null && !player.isPlaying() && musicPause != true) {
			if (isRepeat) {
				player.rewind();
				player.play();
			} else if (songIndex < songs.size()) {
				++songIndex;
				musicChanged = true;
				loadCurrentSong();
			}
		}
		if (fullscreenMode) {
			if (!fullscreenToggle) {
				toggleButtonVisible();
				frame.setSize(displayWidth, displayHeight - 40);
				frame.setLocation(0, 0);
				fullscreenToggle = !fullscreenToggle;
			}
			if (player != null && player.isPlaying()) {
				if (gameModeOn)
					game.draw();
				else
					visualizer.draw();
			}
			
		} else {
			if (fullscreenToggle) {
				toggleButtonVisible();
				frame.setSize(Config.appWidth, Config.appHeight + Config.visualizerHeight);
				fullscreenToggle = !fullscreenToggle;
			}
			if (player != null && player.isPlaying()) {
				visualizer.draw();
				imageMode(CORNER);
				image(player_skin, 0, 0);
				btnProgressSeeker.moveTo(Config.progressBarX + ((float) player.position() / player.length() * (Config.progressBarWidth - btnProgressSeeker.getWidth())), Config.progressBarY);
				btnVolumeSeeker.moveTo((float) (Config.volumeBarX + ((player.getGain() - Config.MIN_GAIN) / (Config.MAX_GAIN - Config.MIN_GAIN) * (Config.volumeBarWidth - btnVolumeSeeker.getWidth()))), Config.volumeBarY - 4);

				textAlign(BASELINE);
				textMode(MODEL);
				textFont(font);
				//textSize(16);
				
				tagExtractor.setFilepath(songs.get(songIndex));
				
				String content = tagExtractor.getTitle();
				int timeLeft = player.position() - player.length();
				String timeLeftStr = String.format("%02d:%02d", timeLeft / 1000 / 60, -timeLeft / 1000 % 60);
				
				fill(255,255,255);
				text(content, (Config.songTextCenterX - textWidth(content)) / 2, Config.songTextY);
				text(timeLeftStr, (Config.timeTextCenterX - textWidth(timeLeftStr)) / 2, Config.timeTextY);
			} else {
				imageMode(CORNER);
				image(player_skin, 0, 0);
			}
			btnPlay.display();
		}
	}

	public void stop() {
		if (player != null)
			player.close();
		if (minim != null)
			minim.stop();
		super.stop();
	}
	
	private void loadCurrentSong() {
		if (gameModeOn && musicChanged) {
			game.reset();
			musicChanged = false;
			musicPause = false;
		}
		if (musicChanged) {
			if (songIndex >= songs.size()) {
				musicChanged = false;
				return;
			}
			if (player != null) {
				player.close();
			}
			player = minim.loadFile(songs.get(songIndex), 2048);	// by default: 2048
			player.play();
			fft = new FFT(player.bufferSize(), player.sampleRate());
			fft.linAverages(256);
			fft.logAverages(60, 7);
			musicChanged = false;
			musicPause = false;
		}
	}

	public void keyPressed() {
		if (gameModeOn) {
			if (key == ESC) {
				game.close();
				gameModeOn = false;
				key = 0;
				keyCode = 0;
			} else {
				game.keyPressed();
			}
		} else {
			if (key == 'b' || key == 'B') {
				fill(0);
			}
			if (key == 'f' || key == 'F') {
				fullscreenMode = !fullscreenMode;
				if (fullscreenMode) {
					visualizer.setArea(0, 0, displayWidth, displayHeight, true);
				} else
					visualizer.setArea(0, Config.appHeight, Config.appWidth, Config.visualizerHeight, true);
			}
			if (key == 'n' || key == 'N') {
				visualizer.next();
			}
			if (key == CODED) {
				
			}
			if (key == BACKSPACE) {
				
			}
		}
	}
	
	public void mousePressed(MouseEvent e) {
		mX = mouseX;
		mY = mouseY;
		btnPlay.update();
		if (gameModeOn) {
			game.mousePressed();
		} else {
			if (e.getCount() == 2 && e.getButton() == RIGHT) {
				background(0);
				visualizer.next();
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (!fullscreenMode) {
			java.awt.Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
			frame.setLocation(p.x - mX, p.y - mY);
		} else if (fullscreenMode && gameModeOn) {
			if (e.getButton() == LEFT) {
				game.mouseDragged(e);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (fullscreenMode && gameModeOn) {
			if (e.getButton() == LEFT) {
				game.mouseReleased(e);
			}
		}
	}
	
	public void handleButtonEvents(GImageButton button, GEvent event) {
		if (player != null && button == btnProgressBar) {
			float newSongPosition = map(mouseX, btnProgressBar.getX(), btnProgressBar.getX() + btnProgressBar.getWidth(), 0, player.length());
			player.cue((int) newSongPosition);
		} else if (player != null && button == btnVolumeBar) {
			float newVolumePosition = map(mouseX, btnVolumeBar.getX(), btnVolumeBar.getX() + btnVolumeBar.getWidth(), Config.MIN_GAIN, Config.MAX_GAIN);
			player.setGain(newVolumePosition);
		} else if (player != null && button == btnVolumeMute) {
			player.setGain(Config.MIN_GAIN);
		} else if (player != null && button == btnVolumeFull) {
			player.setGain(Config.MAX_GAIN);
		}
	}
	
	public void startGame() {
		gameModeOn = true;
		fullscreenMode = !fullscreenMode;
		game.init();
	}

	@SuppressWarnings("deprecation")
	public void addAudioEffect(int effect, float... parameters) {
		if (player == null)
			return;
		// remove existed same kind of AudioEffect
		if (audioEffects[effect - 1] != null)
			removeAudioEffect(effect);
		switch (effect) {
		case Config.ECHO_EFFECT:
			audioEffects[effect - 1] = new EchoEffect(player.sampleRate(), player.bufferSize(), parameters[0], parameters[1]);
			break;
		case Config.FADEIN_EFFECT:
			audioEffects[effect - 1] = new FadeInEffect(player.sampleRate(), player.bufferSize(), 10, 0.5f);
			break;
		case Config.FADEOUT_EFFECT:
			audioEffects[effect - 1] = new FadeOutEffect(player.sampleRate(), player.bufferSize(), 10, 0.5f);
			break;
		case Config.REVERSE_EFFECT:
			audioEffects[effect - 1] = new ReverseEffect();
			break;
		case Config.CHANGEPITCH_EFFECT:
			audioEffects[effect - 1] = new ChangePitchEffect(player.sampleRate(),player.bufferSize(), (int) parameters[0]);
			//??audioEffect.changePitch(pitch);
			break;
		default:
			return;
		}
		player.addEffect(audioEffects[effect - 1]);
	}
	
	@SuppressWarnings("deprecation")
	public void enableAudioEffect() {
		if (player == null || audioEffects == null)
			return;
		for (int i = 0; i < audioEffects.length; ++i) {
			if (audioEffects[i] != null)
				player.enableEffect(audioEffects[i]);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void disableAudioEffect() {
		if (player == null || audioEffects == null)
			return;
		for (int i = 0; i < audioEffects.length; ++i) {
			if (audioEffects[i] != null)
				player.disableEffect(audioEffects[i]);
		}
	}

	@SuppressWarnings("deprecation")
	public void removeAudioEffect(int effect) {
		if (player == null || audioEffects[effect - 1] == null) {
			return;
		}
		player.removeEffect(audioEffects[effect - 1]);
		audioEffects[effect - 1] = null;
	}
	
	public void handleToggleButtonEvents(GImageToggleButton button, GEvent event) {
		if (button == btnEffect) {
			effectOn = !effectOn;
			if (effectOn) {
				enableAudioEffect();
			} else {
				disableAudioEffect();
			}
		} else if (button == btnPrev) {
			if (songIndex > 0)
				--songIndex;
			musicChanged = true;
		} else if (button == btnNext) {
			if (songIndex < songs.size() - 1)
				++songIndex;
			musicChanged = true;
		} else {
		}
	}

	public boolean isControlPanelOpened() {
		return controlPanelOpened;
	}

	public void setControlPanelOpened(boolean controlPanelOpened) {
		this.controlPanelOpened = controlPanelOpened;
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
	
	private void toggleButtonVisible() {
		boolean currentVisible = btnNext.isVisible();
		btnNext.setVisible(!currentVisible);
		btnPrev.setVisible(!currentVisible);
		btnEffect.setVisible(!currentVisible);
		btnProgressBar.setVisible(!currentVisible);
		btnProgressSeeker.setVisible(!currentVisible);
		btnVolumeBar.setVisible(!currentVisible);
		btnVolumeSeeker.setVisible(!currentVisible);
		btnVolumeMute.setVisible(!currentVisible);
		btnVolumeFull.setVisible(!currentVisible);
	}
	
	public boolean isMusicPause() {
		return musicPause;
	}

	public void setMusicPause(boolean musicPause) {
		this.musicPause = musicPause;
	}

}