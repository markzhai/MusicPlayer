package application;

import java.util.Iterator;
import java.util.LinkedList;

import processing.core.PImage;

public class FFTBuffer {
	private int bufferSize = 50;
	LinkedList<PImage> buffer = new LinkedList<PImage>();

	public FFTBuffer() {}
	
	public void pushBuffer(PImage image) {
		if (buffer.size() >= bufferSize) {
			buffer.removeFirst();
		}
		buffer.add(image);	//O(1)	
	}
	
	public PImage getBuffer(int index) {
		return buffer.get(index);	// O(n), Iterator.remove() is O(1) <--- main benefit of LinkedList<E>
	}
	
	public int getSize() {
		return buffer.size();
	}
	
	public Iterator<PImage> getIter() {
		return buffer.iterator();
	}
}
