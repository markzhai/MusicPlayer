package effects;

import ddf.minim.AudioEffect;

public class EchoEffect implements AudioEffect {
	float sampleRate;
	int bufferSize;
	int clipping_count = 0;
	float delay_line_duration = 0.15f; // Length of delay line, in seconds
	int delay_line1_length;
	float[] delay_line1_sample;
	float delay_line1_multiplier = 0.8f;
	float delay_line1_output;	
	int position;

	public EchoEffect(float sampleRate , int bufferSize){
		this.sampleRate = sampleRate;
		this.bufferSize = bufferSize;
		delay_line1_length = (int)Math.floor(delay_line_duration * sampleRate);
		delay_line1_sample = new float[delay_line1_length];
		for(int i=0; i < delay_line1_length; i++){
			delay_line1_sample[i] = 0;
		}
		position = 0;
	}

	public EchoEffect(float sampleRate , int bufferSize, float delayDuration, float multiplier){
		this(sampleRate, bufferSize);
		this.delay_line_duration = delayDuration;
		this.delay_line1_multiplier = multiplier;
		System.out.println("Echo effect with delay " + delayDuration + ", multiplier " + multiplier);
	}

	@Override
	public void process(float[] sample) {
		for(int i=0; i < sample.length; i++){
			if((i + position*bufferSize) >= delay_line1_length){
				delay_line1_output = delay_line1_sample[(i + position*bufferSize) % delay_line1_length];
			} else {
				delay_line1_output = 0;
			}
			sample[i] = sample[i] + (float)(delay_line1_output * delay_line1_multiplier);
			if(sample[i] > 1.0 || sample[i] < -1.0){
				clipping_count++;
			}  
			delay_line1_sample[(i + position*bufferSize) % delay_line1_length] = sample[i];		     
		}
		position ++ ;

	}

	@Override
	public void process(float[] lsample, float[] rsample) {
		process(lsample);
		position--;
		process(rsample);
	}

	public void changeDelayTime(float delaytime){
		if(delaytime > 0) delay_line_duration = delaytime;
	}

}
