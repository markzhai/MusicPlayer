package application;

public class Ball {
	private int r, g, b, alpha;
	private float posX, posY;
	private float radius;
	private int startTime;
	private int duration;
	private int type;
	
	private int key;  // 0 ~ 25 stands A, B, C...

	public Ball(float posX, float posY, float radius, int startTime) {
		this.posX = posX;
		this.posY = posY;
		this.setRadius(radius);
		this.setStartTime(startTime);
		this.setDuration(2000);
	}
	
	public Ball(float posX, float posY, float radius, int startTime, int duration) {
		this.posX = posX;
		this.posY = posY;
		this.setRadius(radius);
		this.setStartTime(startTime);
		this.setDuration(duration);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}
	
	public void setColor(int r, int g, int b, int alpha) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.alpha = alpha;
	}

	public int getAlpha() {
		return alpha;
	}

	public int getB() {
		return b;
	}

	public int getG() {
		return g;
	}

	public int getR() {
		return r;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
}
