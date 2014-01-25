package application;

public final class Config {
	public static final int ERROR_CODE = -1;

	public static final int appWidth = 464;
	public static final int appHeight = 77;
	public static final int visualizerHeight = 500;
	public static final int progressBarX = 184, progressBarY = 55, progressBarWidth = 116, progressBarHeight = 8;
	public static final int volumeBarX = 47, volumeBarY = 65, volumeBarWidth = 50;
	public static final int songTextCenterX = 474, songTextY = 48;
	public static final int timeTextCenterX = 474 + progressBarX, timeTextY = 63;
	
	public static final float MIN_GAIN = -40;
	public static final float MAX_GAIN = 6.0206f;

	// minim provided effects:
	// http://code.compartmental.net/minim/javadoc/ddf/minim/effects/package-frame.html
	// http://code.compartmental.net/tools/minim/manual-ugens/
	public static final int EFFECT_NUM = 5;
	public static final int ECHO_EFFECT = 1;
	public static final int FADEIN_EFFECT = 2;
	public static final int FADEOUT_EFFECT = 3;
	public static final int CHANGEPITCH_EFFECT = 4;
	public static final int REVERSE_EFFECT = 5;
	
	public static final int STYLE_CLASSIC = 1;
	public static final int STYLE_POPULAR = 2;
	public static final int STYLE_SPEECH = 3;
	public static final int STYLE_UNKOWN = 5;
	public static final int STYLE_CLASSIC_PIANO = 1;
	public static final int STYLE_CLASSIC_VIOLIN = 2;
	public static final int STYLE_POPULAR_ROCK = 1;
	public static final int STYLE_POPULAR_JAZZ = 2;
	public static final int STYLE_POPULAR_POP = 2;
	
	public static final float ECHO_LIGHT_DELAY = 0.15f;
	public static final float ECHO_LIGHT_MULTIPLIER = 0.5f;
	public static final float ECHO_INTENSE_DELAY = 0.2f;
	public static final float ECHO_INTENSE_MULTIPLIER = 0.8f;
	
	public static final String SYTLE_STRING[] = new String[] {
		"classic", "popular", "speech", "unknown"
	};

	public static final String SKIN_DIRECTORY = "data//";
	public static final String ICON = SKIN_DIRECTORY + "MyPlayerIco.png";
	public static final String PLAYER_FONT = "MicrosoftYaHei-Bold-16.vlw";
	public static final String PLAYER_SKIN = "player_skin.png";
	public static final String[] PLAYER_PLAY = {"play.png", "play_hot.png", "pause.png", "pause_hot.png"};
	public static final String[] PLAYER_PROGRESS = {"progress_bar.png", "progress_thumb.png"};
	public static final String[] PLAYER_EQ = {"equalizer.png", "equalizer_hot.png"};
	public static final String[] PLAYER_NEXT = {"next.png", "next_over.png"};
	public static final String[] PLAYER_PREV = {"prev.png", "prev_over.png"};
	public static final String[] PLAYER_VOLUME = {"volume_bar.png", "volume_thumb.png", "volume_mute.png", "volume_full.png"};
}