import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;
import g4p_controls.*;
import ddf.minim.*;
import ddf.minim.signals.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;

import java.awt.Frame;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;

// Launch this, the Processing thingy. This will open the UI thingy.

public class MusicPlayer extends PApplet {
	public boolean controlPanelOpened = false;
	public boolean musicChanged = false;
	public String filename = "songs/Disaster - Etertica Graffiti.mp3";
	
	public static void main(String args[]) {
		String[] appletArgs = new String[] {"MusicPlayer"};
		if (args != null) {
			PApplet.main(concat(appletArgs, args));
		} else {
			PApplet.main(appletArgs);
		}
	}
	
	public int position = 30;
	GWindow[] window;
	GButton btnStart;
	GLabel lblInstr;

	GImageToggleButton btnEqualizer;

	PImage player_skin;

	PImage seeker;
	PImage seeker2;
	PImage closeButton;
	PImage minimizeButton;
	PImage prev;
	PImage play;
	PImage stop;
	PImage next;
	PImage openfile;
	PImage shuffle;
	PImage repeat;
	PImage repeatOn;
	PImage pause;

	PFont font;
	final java.awt.Color fullyTransparent = new java.awt.Color(0, 0, 0, 0);

	/* audio file data */
	Minim minim;
	AudioPlayer player;
	FFT fft;
	AudioMetaData meta;
	double duration;
	boolean isPlaying;
	boolean isRepeat;
	/* end */

	int mX, mY;
	int visualize_height = 293;

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
		// 464 * 293
		size(464, 293 + visualize_height, P3D);
		minim = new Minim(this);

		player = minim.loadFile(filename);
		player.play();
		duration = player.length();
		isPlaying = true;
		isRepeat = false;

		player_skin = loadImage("player_skin.bmp");
		seeker = loadImage("progress_thumb.bmp");

		//font = createFont("MicrosoftYaHei", 10);
		font = loadFont("MS-PGothic-12.vlw");

		btnEqualizer = new GImageToggleButton(this, 403, 23, "equalizer.bmp", "equalizer_2.bmp", 2, 1);

		fft = new FFT(player.bufferSize(), player.sampleRate());
		fft.linAverages(128);
		// lblInstr = new GLabel(this, 132, 34, 120, 60,
		// "Use the mouse to draw a rectangle in any of the 3 windows");
		// lblInstr.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
		// lblInstr.setVisible(false);
	}

	public void draw() {
		background(0);
		
		if (musicChanged) {
			player.close();
			player = minim.loadFile(filename);
			player.play();
			musicChanged = false;
		}
		
		image(player_skin, 0, 0);
		// background(240);
		// 184, 55 ~ 300

        ellipse(position, 150, 25, 25);
		image(seeker, 184 + (int) (player.position() / duration * (116 - seeker.width)), 56);

		textMode(MODEL);
		textFont(font);
		textSize(14);
		fill(0, 102, 153);
		meta = player.getMetaData();
		//String content = meta.author() + " - " + meta.title();
		String content = meta.title();
		text(content,  (player_skin.width - textWidth(content)) / 2 + 10, 48);

		int timeLeft = player.position() - player.length();
		String timeLeftStr = String.format("%02d:%02d", timeLeft / 1000 / 60, -timeLeft / 1000 % 60);
		text(timeLeftStr, (player_skin.width - textWidth(timeLeftStr)) / 2 + 92, 63);

		if (isRepeat) {
			if (player.position() >= player.length()) {
				player.rewind();
				player.play();
			}
		}

		fft.forward(player.mix);
		stroke(255, 0, 0, 128);
		for (int i = 0; i < fft.specSize(); i++) {
			line(i, height, i, height - fft.getBand(i) * 4);
		}
	}

	/**
	 * Create the three windows so that they share mouse handling and drawing
	 * code.
	 */
	public void createWindows() {
		int col;
		window = new GWindow[1];
		for (int i = 0; i < 1; i++) {
			col = (128 << (i * 8)) | 0xff000000;
			window[i] = new GWindow(this, "Window " + i, 70 + i * 220,
					160 + i * 50, 200, 200, false, JAVA2D);
			// window[i].setBackground(col);
			window[i].addData(new MyWinData());
			window[i].addDrawHandler(this, "windowDraw");
			window[i].addMouseHandler(this, "windowMouse");
			window[i].setActionOnClose(GWindow.CLOSE_WINDOW);
			window[i].setOnTop(false);
		}
	}

	void closeWindows() {
		for (int i = 0; i < 1; i++) {
			window[i].forceClose();
		}
		window = null;
	}

	public void mousePressed() {
		mX = mouseX;
		mY = mouseY;
	}

	public void mouseDragged() {
		// java.awt.Point p = frame.getLocation();
		// frame.setLocation(p.x + mouseX - mX, p.y + mouseY - mY);
		java.awt.Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
		frame.setLocation(p.x - mX, p.y - mY);
	}

	public void handleToggleButtonEvents(GImageToggleButton button, GEvent event) {
		if (window == null && button == btnEqualizer) {
			createWindows();
		} else {
			closeWindows();
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