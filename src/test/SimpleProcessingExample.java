package test;

import processing.core.PApplet;

public class SimpleProcessingExample extends PApplet{

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "SimpleProcessingExample" });
	}
	
    public void setup() {
        size(300, 300);
        background(255);
        fill(0);        
        
        // Instantiate the UI - Using "this" to make it possible for the UI to reference this
        SwtUI mySwtUI = new SwtUI(this);
        Thread mySwtUIThread = new Thread(mySwtUI);

        //Start the UI-threads
        mySwtUIThread.start();
        
    }
    // position is public, to make it accessible from SwtUI
    public int position = 30;
    public void draw() {
          background(255);

          ellipse(position, 150, 25, 25);
        }
}