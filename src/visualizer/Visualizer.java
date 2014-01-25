package visualizer;

import java.io.Serializable;

import processing.core.PApplet;

public interface Visualizer extends Serializable {	
	/**
	 * Sets the PApplet to draw to. Must be called before process().
	 * 
	 * @param applet Where to draw to. Must not be null
	 */
	void drawTo(PApplet applet);
	
	/**
	 * Sets the area that can be used by this visualizer. If relative is set to false
	 * the visualizer will use the given values as its boundary, independent of the
	 * size of the PApplet. If relative is true, the values must be in the range [0,1]
	 * and represent an area relative to the size of the PApplet. Processings coordinate
	 * system is used, so (0,0) is the top left pixel.
	 * 
	 * @param x1 Must be >= 0 and <= 1 if relative is true
	 * @param y1 Must be >= 0 and <= 1 if relative is true
	 * @param x2 Must be > x1 and <= 1 if relative is true
	 * @param y2 Must be > y1 and <= 1 if relative is true
	 * @param relative Whether or not the values are relative
	 */
	void setArea(float x1, float y1, float x2, float y2, boolean relative);
		
	/**
	 * Draws to the PApplet specified by drawTo.
	 */
	void process();
	
	/**
	 * Sets the color to use when drawing this visualizer. How a visualizer uses the color
	 * specified is not defined. It might not be used at all.
	 * 
	 * @param color The color to use
	 */
	void setColor(int color);
	
	/**
	 * Sets two colors to interpolate between when drawing this visualizer. How a visualizer uses the colors
	 * specified is not defined. They might not be used at all.
	 * 
	 * @param a The color to start from
	 * @param b The color to interpolate to
	 * @param mode The color mode to use. Must bei either PApplet.RGB or PApplet.HSB
	 */
	void setColor(int a, int b, int mode);
	
	/**
	 * Sets the colors to interpolate between when drawing this visualizer. How a visualizer uses the colors
	 * specified is not defined. They might not be used at all.
	 * 
	 * @param thresholds The relative thresholds to use. Values must be between 0 and 1 and sorted. The first element must be 0, the last 1. Must be of same length as colors
	 * @param colors The The colors to use. Must be of same length as thresholds
	 * @param mode The color mode to use. Must be either PApplet.RGB or PApplet.HSB
	 */
	void setColor(float[] thresholds, int[] colors, int mode);
	
	/**
	 * Returns a short string representation of this visualizer.
	 * 
	 * @return visualizer info
	 */
	String toString();
	
	/**
	 * Disposes this visualizer, releasing all resources that were used exclusively
	 * by this visualizer. Subsequent calls to any methods of the visualizer might cause exceptions.
	 */
	void dispose();
}