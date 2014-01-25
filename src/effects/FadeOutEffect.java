package effects;

import ddf.minim.AudioEffect;

public class FadeOutEffect implements AudioEffect {
	private float fadeMultiplier;
	private float sampleRate;
	private float totalSamples;
	private float fadeValue; // The fade-out duration, seconds
	
	public FadeOutEffect(float sampleRate, int bufferSize, int length, float fadeValue) {
		this.fadeValue = fadeValue;
		this.sampleRate = sampleRate;
		this.totalSamples = length * sampleRate;
	}
	
	public void process(float[] sample) {
		process_full_buffer(sample);
	}

	void process_full_buffer(float[] sample) {
	    float tmp;
	    float totalSamplesToFade = fadeValue * sampleRate;
	    if (totalSamplesToFade > totalSamples)
	      totalSamplesToFade = totalSamples;
	    // Calculate the starting point of the samples
	    int start = (int) (totalSamples - totalSamplesToFade);
	    for (int i = start; i < totalSamples; i++) {
	      // Apply the fade out multiplier to each sample
	      tmp = (float) (i - start);
	      fadeMultiplier = 1 - tmp / totalSamplesToFade;
	      sample[i] = sample[i] * fadeMultiplier;
	    }
	  }

	public void process(float[] left, float[] right) {
		process(left);
		process(right);
	}
}
