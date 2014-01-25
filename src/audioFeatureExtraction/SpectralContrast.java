package audioFeatureExtraction;

import java.util.Arrays;

import application.Config;
import ddf.minim.AudioSample;
import ddf.minim.analysis.FFT;

public class SpectralContrast {
	private float[] allsamples;
	private float[] fftsamples;
	private float[][] spectra;
	private int buffersize; 
	private float samplerate;
	
	private FFT fft;
	private int totalchunks;
	private static float neighborfactor = 0.02f;
	private float[][] SC;
	
	private int result = -1; 
	 
	public int SpectralContrastClassification (AudioSample source) {
				
		float[] centerfreq = {2, 10, 25,  60, 125, 250, 500, 1000, 2000, 4000, 8000, 18000};
		float[] bandwidth =  {4,  6, 18,  40,  90, 180, 300,  700, 1200, 2400, 5400, 11000};
		int[] bandhistogram ={0,  0,  0,   0,   0,   0,   0,    0,    0,    0,    0,    0 };
		
		if (source == null) {
			try {
				throw new Exception("file not exist!");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}else{			
			buffersize = source.bufferSize();
			samplerate = source.sampleRate();
			allsamples = source.getChannel(source.LEFT);

			
			fftsamples = new float[buffersize];			
			fft = new FFT(buffersize,samplerate);
			fft.window(FFT.HAMMING);
			totalchunks = (allsamples.length/buffersize+1);
			spectra = new float[totalchunks][centerfreq.length];
			SC = new float[totalchunks][2];
			
			for(int chunkindex=0; chunkindex<totalchunks; chunkindex++){
				int chunkStartIndex = chunkindex * buffersize;
				int chunkSize = Math.min(allsamples.length-chunkStartIndex, buffersize);
				System.arraycopy(allsamples, chunkStartIndex, fftsamples,0,chunkSize);
				if(chunkSize<buffersize){
					Arrays.fill(fftsamples,chunkSize,buffersize-1,0);
				}				
				fft.forward(fftsamples);
							
			    for(int j=0; j<centerfreq.length; j++){		    		
					spectra[chunkindex][j] = fft.calcAvg(centerfreq[j]-bandwidth[j]/2,centerfreq[j]+bandwidth[j]/2 );		
		    	}
			    int[] pvindex = findMaxMinIndex(spectra[chunkindex]);			    
			    bandhistogram[pvindex[0]]++;			   
			}	
			
			int classic = (bandhistogram[5] + bandhistogram[6] - bandhistogram[4])*100/totalchunks/2;
			int popular = (int) ((bandhistogram[3] + bandhistogram[4] + bandhistogram[5] - bandhistogram[6])*100/totalchunks/3);
			int speech = (int) ((bandhistogram[4]+bandhistogram[5]-bandhistogram[3])*100/totalchunks/2);
			//int[] features = {classic,popular,speech};
			if(classic > popular && classic > speech){
				result = Config.STYLE_CLASSIC;
			}else{
				if(popular > speech) result = Config.STYLE_POPULAR;
				else if(speech > popular) result = Config.STYLE_SPEECH;
				else result = Config.STYLE_UNKOWN;
			}			
			System.out.println("Classification finish!\n");
		}//source!=null	

		return result;	
	}
	
	public int[] findMaxMinIndex(float[] array){
		float max=0,min=10000;
		int maxindex=-1;
		int minindex=-1;
		int[] result = new int[2];
		for(int i=0; i< array.length;  i++){
			if(array[i]>=max){
				max = array[i];
				maxindex = i;
			}
			if(array[i]<=min){
				min = array[i];
				minindex = i;						
			}
		}
		result[0]= maxindex;
		result[1]= minindex;
		return result;
	}
	
	
	public float calcPeak(float[] array, int maxindex, float neighborfactor){
		int neighbors = (int)((int) array.length * neighborfactor);
		int startindex = Math.max(0, maxindex-neighbors/2);
		int endindex = Math.min(maxindex + neighbors/2,(int)array.length-1);
		float sum = 0;
		for(int i=startindex; i<=endindex; i++){
			sum+=array[i];
		}
		return (float)sum/(endindex-startindex+1);
	}	
	public float calcValley(float[] array, int minindex, float neighborfactor){
		int neighbors = (int)((int) array.length * neighborfactor);
		int startindex = Math.max(0, minindex-neighbors/2);
		int endindex = Math.min(minindex + neighbors/2,(int)array.length-1);
		float sum = 0;
		for(int i=startindex; i<=endindex; i++){
			sum+=array[i];
		}
		return (float)sum/(endindex-startindex+1);
	}
	
	public float calcPeak(float[] array, float neighborfactor){
		int neighbors = (int)((int) array.length * neighborfactor);
		float[] newarray = Arrays.copyOf(array, array.length);
		float sum = 0;
		Arrays.sort(newarray);//ascending
		for(int i=0; i<neighbors; i++){
			sum+=newarray[array.length-i-1];
		}
		return sum/neighbors;
	}
	public float calcValley(float[] array, float neighborfactor){
		int neighbors = (int)((int) array.length * neighborfactor);
		float[] newarray = Arrays.copyOf(array, array.length);
		float sum = 0;
		Arrays.sort(newarray);//ascending
		for(int i=0; i<neighbors; i++){
			sum+=newarray[i];
		}
		return sum/neighbors;
	}
}
