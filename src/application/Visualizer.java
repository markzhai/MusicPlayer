package application;

import java.util.Iterator;

import processing.core.*;

public class Visualizer {
	private MusicPlayer applet;
	private int active = 1;
	private int max = 6;
	private float _x1, _y1, _x2, _y2;
	
	/* visualizer3 */
	private int hVal = 0;
	private FFTBuffer fftBuffer = new FFTBuffer();
	
	/* visualizer4 */
	private Frequence[] frequenzArray = new Frequence[Frequence.spectrumMax + 1];

	/* visualizer5 */
	float[] myBuffer = null;
	
	/* visualizer6 */
	float[] peaks;

	int peak_hold_time = 10;  // how long before peak decays
	int[] peak_age;  // tracks how long peak has been stable, before decaying
	
	// how wide each 'peak' band is, in fft bins
	int binsperband = 1;
	int peaksize = 0; // how many individual peak bands we have (dep. binsperband)
	float gain = 40; // in dB (zero point starts from -40dB
	float dB_scale = 2.0f;  // pixels per dB
	
	int legend_height, legend_width;
	/* visualizer6 end */
	
	public Visualizer(MusicPlayer applet) {
		this.applet = applet;
		for (int i = Frequence.spectrumMin; i < Frequence.spectrumMax; i++) {
			frequenzArray[i] = new Frequence(150, Config.appHeight + Config.visualizerHeight / 3);
		}
	}
	
	/**
	 * @param relative Whether or not the values are relative
	 */
	public void setArea(float x1, float y1, float x2, float y2, boolean relative) {
		_x1 = x1;
		_y1 = y1;
		_x2 = (relative ? (x1 + x2) : x2);
		_y2 = (relative ? (y1 + y2) : y2);
	}
	
	public void next() {
		applet.background(0);
		active = (active < max ? (active + 1) : 1);
	}
	
	public void draw() {
		applet.stroke(255, 0, 0, 128);
		switch (active) {
		case 1:
			visualize1();
			break;
		case 2:
			visualize2();
			break;
		case 3:
			visualize3();
			break;
		case 4:
			visualize4();
			break;
		case 5:
			visualize5();
			break;
		case 6:
			visualize6();
			break;
		default:
			break;
		}
	}
	
	// Spectrum - frequency and db
	// spectrum可以再分为幅度频谱(amplitude spectrum),相位频谱(phase spectrum),以及能量频谱(energy frequency spectrum)。
	public void visualize1() {
		applet.background(0);
		applet.strokeWeight(PApplet.max(2, applet.width / applet.fft.specSize()));    // origin 0.5
		applet.fft.forward(applet.player.mix);
		applet.stroke(255, 0, 0, 128);
		for (int i = 0; i < applet.fft.specSize(); i++) {
			float y = applet.fft.getBand(i) * 4;
			float x = (_x2 - _x1) / applet.fft.specSize() * i;
			applet.line(x, _y2, x, (_y2 - y) > _y1 ? (_y2 - y) : _y1);
		}
		applet.text("Spectrum", _x2 - 100, _y1 + 20);
	}

	private int colmax = Config.appWidth;
	private int rowmax = Config.visualizerHeight;
	private int[][] sgram = new int[colmax][rowmax];
	private int col;
	private int leftedge = 0;

	// x - time, y - frequency, color - value
	public void visualize2() {
	    // colors
	    applet.background(0);
	    applet.colorMode(PApplet.HSB, 255);
	    int sval = 0;

	    // perform a forward FFT on the samples in the input buffer
	    applet.fft.forward(applet.player.mix);
	    for (int i = 0; i < rowmax /* fft.specSize() */; ++i) {
	        // fill in the new column of spectral values (and scale)
	        sgram[col][i] = (int)Math.round(Math.max(0, 52 * Math.log10(1000 * applet.fft.getBand(i))));
	    }

	    // next time will be the next column
	    col = col + 1;
	    // wrap back to the first column when we get to the end
	    if (col == colmax) { col = 0; }

	    // Draw points.
	    // leftedge is the column in the ring-filled array that is drawn at the extreme left
	    // start from there, and draw to the end of the array
	    for (int i = 0; i < colmax - leftedge; ++i) {
	        for (int j = 0; j < rowmax; ++j) {
	            sval = Math.min(255, sgram[i + leftedge][j]);
	            applet.stroke(255 - sval, sval, sval);
	            applet.point(i, applet.height - j);
	        }
	    }

	    // Draw the rest of the image as the beginning of the array (up to leftedge)
	    for (int i = 0; i < leftedge; ++i) {
	        for (int j = 0; j < rowmax; ++j) {
	            sval = Math.min(255, sgram[i][j]);
	            applet.stroke(255 - sval, sval, sval);
	            applet.point(i + colmax - leftedge, applet.height - j);
	        }
	    }

	    // Next time around, move the left edge over by one, to have the whole thing scroll left
	    leftedge = leftedge + 1;
	    // Make sure it wraps around
	    if (leftedge == colmax) {leftedge = 0; }
	    applet.colorMode(PApplet.RGB);
	    applet.text("Spectrogram", _x2 - 100, _y1 + 20);
	}

	/* An average spectrum is simply a spectrum with fewer bands than the full spectrum 
	 * where each average band is the average of the amplitudes of some number of contiguous 
	 * frequency bands in the full spectrum.
	 */
	public void visualize3() {
		applet.background(0);
		applet.strokeCap(PApplet.SQUARE);
		float w;
		float margin = 0.02f;
		
		applet.tint(255, 255, 255, 254);
		int bufferSize  = fftBuffer.getSize();
		if (bufferSize != 0) {
			float startPos = 1f - bufferSize * margin;
			Iterator<PImage> iter = fftBuffer.getIter();
			int i = 0;
			while(iter.hasNext()) {
				float pos = startPos + i * margin;
				applet.image(iter.next(), (applet.width - applet.width * pos) / 1.5f, 
						(applet.height - applet.height * pos) / 2, applet.width * pos, applet.height * pos);
				i++;
			}
		}
		applet.noTint();
		
		applet.strokeWeight(PApplet.max(1, applet.width / applet.fft.avgSize()));
		applet.fft.forward(applet.player.mix);
		w = PApplet.max(1, applet.width / applet.fft.avgSize());
		
		applet.colorMode(PApplet.HSB);
		applet.stroke(hVal, 255, 255);
		applet.colorMode(PApplet.RGB);
		
		// draw the linear averages
		for (int i = 0; i < applet.fft.avgSize(); i++) {
			//// draw a rectangle for each average, multiply the value by 5 so we can see it better
			applet.line(i * w, applet.height, i * w, applet.height - applet.fft.getAvg(i) * 4);
		}
		
		fftBuffer.pushBuffer(applet.get());
		hVal = (hVal > 255? 0 : (hVal + 2));
	}

	public void visualize4() {
		applet.fft.forward(applet.player.mix);
	    for (int i = Frequence.spectrumMin; i < Frequence.spectrumMax; i++) { // for the defined channel spectrum
	    	float temp = PApplet.min(50, PApplet.max(0, (applet.fft.getBand(i) / 10f))); //get the value of the current channel
	    	frequenzArray[i].draw(temp, i); //draw the channel
	    }
	}
	
	private void visualize5() {
		if (myBuffer == null)
			myBuffer = new float[applet.player.bufferSize()];
		int tbase = 1024;
		float gain = 200;
		
		applet.background(0);
		applet.stroke(255);
		// draw the output waveforms, so there's something to look at
		// first grab a stationary copy
		for (int i = 0; i < applet.player.bufferSize(); ++i) {
			myBuffer[i] = applet.player.mix.get(i);	// get the ith sample value
		}
		// find trigger point as largest slope in first 1/4 of buffer
		int offset = 0;
		float maxdx = 0;
		for (int i = 0; i < myBuffer.length / 4; ++i) {
			float dx = myBuffer[i + 1] - myBuffer[i];
			if (dx > maxdx) {
				offset = i;
				maxdx = dx;
			}
		}
		
		toggleColor();
		applet.strokeWeight(2);
		
		// plot out that waveform
		int mylen = PApplet.min(tbase, myBuffer.length - offset);
		for (int i = 0; i < mylen - 1; i++) {
			float x1 = PApplet.map(i, 0, tbase, 0, applet.width);
			float x2 = PApplet.map(i + 1, 0, tbase, 0, applet.width);
			applet.line(x1, applet.height / 2 - myBuffer[i + offset] * gain * applet.height / 450, 
						x2, applet.height / 2 - myBuffer[i + 1 + offset] * gain * applet.height / 450);
		}	
	}

	int spectrum_height; // determines range of dB shown
	int spectrum_width; // determines how much of spectrum we see
	
	private void visualize6() {
		if (applet.fullscreenMode) {
			spectrum_height = applet.height - 100;
			spectrum_width = 1020;
			legend_height = 0;
			legend_width = 200;
			dB_scale = 6.0f;
		} else {
			spectrum_height = applet.height - 40;
			spectrum_width = applet.width;
			legend_height = 0;
			legend_width = 50;
			dB_scale = 4.0f;
		}
		if (peaksize == 0) {
			peaksize = 1 + Math.round(applet.fft.specSize()/binsperband);
			peaks = new float[peaksize];
			peak_age = new int[peaksize];
		}

		applet.background(0);
		applet.fft.forward(applet.player.mix);

		// draw peak bars
		applet.noStroke();
		applet.fill(0, 128, 144); // dim cyan
		for (int i = 0; i < peaksize; ++i) {
			int thisy = spectrum_height - PApplet.round(peaks[i]);
			applet.rect(legend_width + binsperband * i, thisy, binsperband, spectrum_height - thisy);
			// update decays
			if (peak_age[i] < peak_hold_time) {
				++ peak_age[i];
			} else {
				peaks[i] -= 1.0;
				if (peaks[i] < 0) {
					peaks[i] = 0;
				}
			}
		}

		// now draw current spectrum in brighter blue
		applet.stroke(64, 192, 255);
		applet.noFill();
		for (int i = 0; i < spectrum_width; i++) {
			// draw the line for frequency band i using dB scale
			float val = dB_scale * (20 * ((float)Math.log10(applet.fft.getBand(i))) + gain);
			if (applet.fft.getBand(i) == 0) {
				val = -200;
			} // avoid log(0)
			int y = spectrum_height - Math.round(val);
			if (y > spectrum_height) {
				y = spectrum_height;
			}
			applet.line(legend_width + i, spectrum_height, legend_width + i, y);
			// update the peak record
			// which peak bin are we in?
			int peaksi = i / binsperband;
			if (peaksize > peaks[peaksi]) {
				peaks[peaksi] = val;
				// reset peak age counter
				peak_age[peaksi] = 0;
			}
		}

		// add legend
		// frequency axis
		applet.fill(255);
		applet.stroke(255);
		int y = spectrum_height;
		applet.line(legend_width, y, legend_width + spectrum_width, y); // horizontal line
		// x,y address of text is immediately to the left of the middle of the letters
		applet.textAlign(PApplet.CENTER, PApplet.TOP);
		for (float freq = 0f; freq < applet.player.sampleRate() / 2; freq += 2000.0) {
			int x = legend_width + applet.fft.freqToIndex(freq); // which bin holds this frequency
			applet.line(x, y, x, y + 4); // tick mark
			applet.text(Math.round(freq / 1000) + "kHz", x, y + 5); // add text label
		}

		// level axis
		int x = legend_width;
		applet.line(x, 100, x, spectrum_height); // vertictal line
		applet.textAlign(PApplet.RIGHT, PApplet.CENTER);
		for (float level = -40f; level <= 100.0; level += 20.0) {
			y = spectrum_height - (int) (dB_scale * (level + gain));
			applet.line(x, y, x - 3, y);
			applet.text((int) level + " dB", x - 5, y);
		}
		applet.text("Spectrum in dB + decaying peak-hold", _x2 - 50, _y1 + 20);
	}
	
	public class Frequence {
		private float x, x2;
		private float y, y2;
		
		public final static float length = 0.3f; // length of the strokes per frame
		public final static float threshhold = 1.5f; // threshold for highlight color
		public final static int spectrumMin = 25; // minimum value of the FFT spectrum
		public final static int spectrumMax = 180; // maximum value of the FFT spectrum
		public final static float rotation = PApplet.PI - 0.5f; // rotation of the visualization

		public Frequence(int initialX, int initialY) {
			x = initialX;
			y = initialY;
		}

		public void draw(float value, int factor) {
			// calculating the next x and y positions
			y2 = length * PApplet.sin(PApplet.radians((value) * 100) + (PApplet.PI / spectrumMax * factor) + rotation);
			x2 = length * PApplet.cos(PApplet.radians((value) * 100) + (PApplet.PI / spectrumMax * factor) + rotation);
			//System.out.println(x + " - " + x2);
			//System.out.println(y + " -- " + y2);
			if (value > threshhold) {
				// if value higher then the threshold draw orange line
				applet.strokeWeight(1f);  // origin 1
				applet.stroke(255, 183, 0, 50);
				applet.line(x, y, x + x2, y + y2);
			} else {
				// if value lower then the threshold draw white line
				applet.strokeWeight(0.5f);    // origin 0.5
				applet.stroke(255, 255, 255, 60);
				applet.line(x, y, x + x2, y + y2);
			}
			// set start x and y
			x = x + x2;
			y = y + y2;
			if (x > applet.width)
				x = 0;
			else if (x < 0)
				x = applet.width;
			if (applet.fullscreenMode && y > applet.height) {
				y = 0;
			} else if (!applet.fullscreenMode && y > applet.height) {
				y = Config.appHeight;
			} else if (!applet.fullscreenMode && y < Config.appHeight) {
				y = applet.height;
			}
		}
	}
	
	public void toggleColor() {
		applet.colorMode(PApplet.HSB);
		applet.stroke(hVal, 255, 255);
		applet.colorMode(PApplet.RGB);
		hVal = (hVal > 255? 0 : (hVal + 1));
	}
	
	// Calculates the base-10 logarithm of a number
	public float log10 (float x) {
	  return (PApplet.log(x) / PApplet.log(10));
	}
}
