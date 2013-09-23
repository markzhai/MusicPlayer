package ui;

import processing.core.PImage;

public class RoundButton extends Button {
	public RoundButton(MusicPlayer parent, PImage image, PImage imageHot, PImage imagePressed, PImage imagePressedHot) {
		super(parent, image, imageHot, imagePressed, imagePressedHot);
	}
	
	public RoundButton(MusicPlayer parent, PImage image, PImage imageHot, PImage imagePressed, PImage imagePressedHot, float x, float y, float width, float height){
		super(parent, image, imageHot, imagePressed, imagePressedHot, x, y, width, height);
	}
	
	public boolean over() {
		int mouseX = this.getParent().mouseX;
		int mouseY = this.getParent().mouseY;
		if (mouseX > getLocation().x && mouseX < getLocation().x + getDimension().x) {
			double radius = this.getDimension().x / 2;
			double temp = (mouseX - this.getLocation().x - radius);
			double upperbound = Math.sqrt(radius * radius - temp * temp) + radius + this.getLocation().y;
			double lowerbound = - Math.sqrt(radius * radius - temp * temp) + radius + this.getLocation().y;
			if (mouseY < upperbound && mouseY > lowerbound) {
				//System.out.println(upperbound + " - " + lowerbound + " - " + mouseY);
				return true;
			}
		}
		return false;
	}
	
	public void action() {
		if (getParent().isMusicPlaying()) {
			setCondition(1);
			getParent().setMusicPlaying(false);
		} else {
			setCondition(0);
			getParent().setMusicPlaying(true);
		}
	}
}
