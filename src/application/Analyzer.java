package application;

import java.util.ArrayList;

import processing.core.PApplet;
import ddf.minim.AudioSample;
import ddf.minim.Minim;
import audioFeatureExtraction.SpectralContrast;

@SuppressWarnings("serial")
public class Analyzer extends PApplet{
	private Minim minim = null;
	int result = Config.STYLE_UNKOWN;
	SpectralContrast featureSC = new SpectralContrast();
	
	public Analyzer(Minim minim) {
		if (minim == null)
			System.out.println("minim null");
		this.minim = minim;
	}
	
	public int getStyle(String filename) {
		
		AudioSample source = minim.loadSample(filename);	
			
		if (source == null) {
			try {
				throw new MyException("file not exist!");
			} catch (MyException e) {
				e.printStackTrace();
			}
			return Config.ERROR_CODE;
		}else{			
			result = featureSC.SpectralContrastClassification(source);
		}
			
		source.close();
		return result;
	}
	
	public ArrayList<Integer> getStyle(ArrayList<String> filenames) {
		ArrayList<Integer> styleList = new ArrayList<Integer>(filenames.size());
		for (int i = 0; i < filenames.size(); ++i) {
			styleList.add(getStyle(filenames.get(i)));
		}
		return styleList;
	}
	
	
}

