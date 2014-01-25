package visualizer;

import processing.core.PApplet;
import util.ColorMapper;

public abstract class VisualizerAbstract implements Visualizer {
	private static final long serialVersionUID = 4237957722212363983L;
	
	/**
	 * The PApplet to draw to.
	 */
	protected transient PApplet p;
	
	/**
	 * The (primary) color mapper of this visualizer.
	 */
	protected final ColorMapper cm;
	
	private float _x1, _y1, _x2, _y2;
	private boolean _relative;
	
	/**
	 * Ctor.
	 */
	public VisualizerAbstract() {
		cm = new ColorMapper();
		
		setArea(0, 0, 1, 1, true);
		setColor(0xFFFFFFFF);
	}
			
	@Override
	public void setArea(float x1, float y1, float x2, float y2, boolean relative) {
		_x1 = x1;
		_y1 = y1;
		_x2 = x2;
		_y2 = y2;
		_relative = relative;
	}
	
	@Override
	public void setColor(int color) {
		cm.setColor(color);
	}
	
	@Override
	public void setColor(int a, int b, int mode) {
		cm.setColor(a, b, mode);
	}
	
	@Override
	public void setColor(float[] thresholds, int[] colors, int mode) {
		cm.setColor(thresholds, colors, mode);
	}
	
	@Override
	public void drawTo(PApplet applet) {
		p = applet;
	}
	
	/**
	 * Returns the area this visualizer may use. The area is specified by
	 * and array containing 4 values:
	 * 
	 * [0] - Low x-coordinate
	 * [1] - Low y-coordinate
	 * [2] - High x-coordinate
	 * [3] - High y-coordinate
	 * 
	 * If the area is specified relative (see setArea()) but no PApplet
	 * object is available, all values will be 0.
	 * 
	 * @return The erea
	 */
	protected float[] getArea() {
		float x1 = 0, y1 = 0, x2 = 0, y2 = 0;

		if (_relative) {
			if (p != null) {
				x1 = _x1 * p.width;
				y1 = _y1 * p.height;
				x2 = _x2 * p.width;
				y2 = _y2 * p.height;
			}
		} else {
			x1 = _x1;
			y1 = _y1;
			x2 = _x2;
			y2 = _y2;
		}

		return new float[] { x1, y1, x2, y2 };
	}
}