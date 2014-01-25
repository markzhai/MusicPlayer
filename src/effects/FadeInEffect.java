package effects;

import ddf.minim.AudioEffect;

public class FadeInEffect implements AudioEffect {
	private float fadeMultiplier;
	private float fadeValue = 1; // The fade-in duration, seconds
	private int i = 0;
	private int times = 1;
	private float sampleRate;
	private float totalSamples;
	private int bufferSize;

	public FadeInEffect(float sampleRate, int bufferSize, int length, float fadeValue) {
		this.fadeValue = fadeValue;
		this.sampleRate = sampleRate;
		this.totalSamples = length * sampleRate;
		this.bufferSize = bufferSize;
	}

	// each time process an array of bufferSize floats, not full AudioStream
	public void process(float[] sample) {
		float totalSamplesToFade = fadeValue * sampleRate;

		if (totalSamplesToFade > totalSamples)
			totalSamplesToFade = totalSamples;

		// System.out.println(totalSamplesToFade);

		for (; i < Math.min(totalSamplesToFade, times * bufferSize); i++) {
			fadeMultiplier = i / totalSamplesToFade;
			sample[i % 1024] = sample[i % 1024] * fadeMultiplier;
		}
		times++;
	}

	public void process(float[] left, float[] right) {
		process(left);
		process(right);
	}

	void process_full_buffer(float[] sample) {
		float totalSamplesToFade = fadeValue * sampleRate;

		if (totalSamplesToFade > totalSamples)
			totalSamplesToFade = totalSamples;

		for (int i = 0; i < Math.min(totalSamplesToFade, bufferSize); i++) {
			fadeMultiplier = i / totalSamplesToFade;
			sample[i] = sample[i] * fadeMultiplier;
		}
	}
}