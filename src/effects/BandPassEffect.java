package effects;

import ddf.minim.AudioEffect;
import ddf.minim.effects.BandPass;

public class BandPassEffect implements AudioEffect{
	
	private BandPass bpf;
	private float centerFreq;
	private float bandwidth;
	private float bpfQ;
	
	public BandPassEffect(float centerFreq, float bandwidth, float sampleRate){		
		this.centerFreq = centerFreq;
		this.bandwidth = bandwidth;		
		this.bpfQ = this.centerFreq/this.bandwidth;
		bpf = new BandPass(centerFreq, bandwidth, sampleRate);
	}

	@Override
	public void process(float[] samples) {
		// TODO Auto-generated method stub
		bpf.process(samples);
		
	}

	@Override
	public void process(float[] lsamples, float[] rsamples) {
		// TODO Auto-generated method stub
		bpf.process(lsamples, rsamples);
	}

}
