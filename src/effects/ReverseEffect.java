package effects;

import ddf.minim.AudioEffect;
import processing.core.*;

public class ReverseEffect implements AudioEffect {
	
	public void process(float[] samp) {
		float[] reversed = new float[samp.length];
		int i = samp.length - 1;
		for (int j = 0; j < reversed.length; i--, j++) {
			reversed[j] = samp[i];
		}
		// we have to copy the values back into sample for this to work
		PApplet.arrayCopy(reversed, samp);
	}

	public void process(float[] left, float[] right) {
		process(left);
		process(right);
	}
}