package ui;

import processing.core.*;

public abstract class Button {	
	private MusicPlayer parent;
	private PImage image;
	private PImage imageHot;
	private PImage imagePressed;
	private PImage imagePressedHot;
	private int condition = 0;
	
	private PVector location;
	private PVector dimension;

	public Button() {}
	
	public Button(MusicPlayer parent, PImage image, PImage imageHot, PImage imagePressed, PImage imagePressedHot) {
		this.setParent(parent);
		this.setImage(image);
		this.setImageHot(imageHot);
		this.setImagePressed(imagePressed);
		this.setImagePressedHot(imagePressedHot);
	}
	
	public Button(MusicPlayer parent, PImage image, PImage imageHot, PImage imagePressed, PImage imagePressedHot, float x, float y, float width, float height){
		this.setParent(parent);
		this.setImage(image);
		this.setImageHot(imageHot);
		this.setImagePressed(imagePressed);
		this.setImagePressedHot(imagePressedHot);
		this.setLocation(new PVector(x, y));
		this.setDimension(new PVector(width, height));
	}
	
	public boolean over() {
		return false;
	}
	
	public void action() {
	}
	
	public void update() {
	    if(this.getParent().mousePressed && over())
	        action();
	}

	public void display() {
		if (condition == 0) {
			if (over())
				getParent().image(imageHot, getLocation().x, getLocation().y);
			else
				getParent().image(image, getLocation().x, getLocation().y);
		} else if (condition == 1) {
			if (over())
				getParent().image(imagePressedHot, getLocation().x, getLocation().y);
			else
				getParent().image(imagePressed, getLocation().x, getLocation().y);
		}
	}
	
	public PImage getImage() {
		return image;
	}

	public void setImage(PImage image) {
		this.image = image;
	}
	
	public PVector getLocation() {
		return location;
	}

	public void setLocation(PVector location) {
		this.location = location;
	}

	public PVector getDimension() {
		return dimension;
	}

	public void setDimension(PVector dimension) {
		this.dimension = dimension;
	}

	public MusicPlayer getParent() {
		return parent;
	}

	public void setParent(MusicPlayer parent) {
		this.parent = parent;
	}

	public PImage getImageHot() {
		return imageHot;
	}

	public void setImageHot(PImage imageHot) {
		this.imageHot = imageHot;
	}

	public PImage getImagePressed() {
		return imagePressed;
	}

	public void setImagePressed(PImage imagePressed) {
		this.imagePressed = imagePressed;
	}

	public PImage getImagePressedHot() {
		return imagePressedHot;
	}

	public void setImagePressedHot(PImage imagePressedHot) {
		this.imagePressedHot = imagePressedHot;
	}
	
	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}
}
