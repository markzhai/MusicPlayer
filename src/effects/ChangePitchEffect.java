package effects;

import ddf.minim.AudioEffect;
import ddf.minim.analysis.FFT;

public class ChangePitchEffect implements AudioEffect {

	private float sampleRate;
	private int bufferSize;
	private int pitch = -1;
	private float rate = 1.0f;
	private int bandsNum = 513;
	private float[] spectrum;

	 /**
	   * Constructs an ChangePitchEffect that will accept sample buffers that are
	   * <code>bufferSize</code> long and have been recorded with a sample rate of
	   * <code>sampleRate</code>. <code>bufferSize</code> <em>must</em> be a
	   * power of two. This will throw an exception if it is not.
	   * 
	   * @param bufferSize
	   *          int: the length of the sample buffers you will be analyzing
	   * @param sampleRate
	   *          float: the sample rate of the audio you will be analyzing
	   */
	public ChangePitchEffect (float sampleRate, int bufferSize) {
		this.sampleRate = sampleRate;
		this.bufferSize = bufferSize;
		bandsNum = bufferSize/2+1;
		spectrum = new float[bandsNum];
	}

	public ChangePitchEffect (float sampleRate, int bufferSize, int pitch) {
		this(sampleRate, bufferSize);
		this.pitch = pitch;
	}
	
	// each time process an array of bufferSize floats, not full AudioStream
	public void process(float[] sample) {
		FFT fft;   
		fft = new FFT(sample.length, sampleRate);  
		fft.forward(sample);
		rate = (float) Math.pow(2,(float)pitch/12);	
		
		float[] newSpectrum = new float[(int) (fft.specSize()*rate)];
		for(int i=0; i<newSpectrum.length && rate!=1 ; i++){
			int lower = (int) Math.floor(i/rate);
			newSpectrum[i]=fft.getBand(lower)+(fft.getBand(lower+1)-fft.getBand(lower))*(i/rate-lower);
		}
		for(int i=0; i<fft.specSize(); i++){
			if(rate!=0){
				if(i<newSpectrum.length) fft.setBand(i, newSpectrum[i]);
				else fft.setBand(i, 0);
			}			
			spectrum[i]=fft.getBand(i);
		}
		fft.inverse(sample);
	}

	public void process(float[] left, float[] right) {
		process(left);
		process(right);
	}
	
	public int getBandsNum(){
		return bandsNum;
	}
	public float getBand(int i){
		return spectrum[i];
	}	
	public void changePitch(int pitch){
		this.pitch = pitch;
	}

}
