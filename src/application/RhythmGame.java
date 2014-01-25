package application;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;
import ddf.minim.AudioSample;
import ddf.minim.analysis.BeatDetect;

/*
 * To do list:
 * 1. background picture
 * 2. difficulty change (scroll bar)
 * 3. optimize add ball strategy
 */

// to do 加球的坐标
public class RhythmGame {
	private MusicPlayer applet;
	
	public BeatListener beatListener;
	private BeatDetect beatDetect;
	private Random random = new Random();
	private PImage hatEclipse;
	private PImage kickEclipse;
	private PImage snareEclipse;
	private LinkedList<Ball> ballList = new LinkedList<Ball>();
	private int eclipseWidth;
	private float prevBallPosX = 0, prevBallPosY = 0;
	
	private int difficulty = 8;  // maximum ball on the screen
	private int count = 1;
	private int score = 0;

	private AudioSample hitSound;
	private AudioSample kickSound;
	private AudioSample snareSound;
	
	private LinkedList<Integer> vocabulary = new LinkedList<Integer>();
	
	private boolean dragging = false;
	private Ball draggingFromBall;
	private Ball draggingToBall;

	public RhythmGame(MusicPlayer applet, int difficulty) {
		this.applet = applet;
		this.difficulty = difficulty;
		for (int i = 0; i < 26; ++i)
			vocabulary.push(i);
		hitSound = applet.minim.loadSample("soft-hitclap_1.wav");
		kickSound = applet.minim.loadSample("soft-hitclap_2.wav");
		snareSound = applet.minim.loadSample("soft-hitclap_3.wav");
		hatEclipse = applet.loadImage("sliderb1.png");
		kickEclipse = applet.loadImage("sliderb2.png");
		snareEclipse = applet.loadImage("sliderb3.png");
		eclipseWidth = hatEclipse.width;
	}
	
	public void pushVocabulary(int key) {
		boolean canPush = true;
		ListIterator<Integer> iter = vocabulary.listIterator();
		while(iter.hasNext()) {
			Integer k = iter.next();
			if (k == key)
				canPush = false;
		}
		if (canPush)
			vocabulary.push(key);
	}

	public void mousePressed() {
		ListIterator<Ball> listIterator = ballList.listIterator();
		while (listIterator.hasNext()) {
			Ball currentBall = listIterator.next();
			if (over(applet.mX, applet.mY, currentBall)) {
				if (currentBall.getType() == 3) {
					dragging = true;
					draggingFromBall = currentBall;

					if (!listIterator.hasNext())
						break;
					draggingToBall = listIterator.next();
					while ((draggingToBall.getKey() != draggingFromBall.getKey()) && listIterator.hasNext()) {
						draggingToBall = listIterator.next();
					}

				} else {
					listIterator.remove();
					triggerSound(currentBall.getType());
					vocabulary.push(currentBall.getKey());
					float progress = (float) (applet.millis() - currentBall.getStartTime()) / currentBall.getDuration();
					score += PApplet.max(0, 1000 * (1 - progress));
				}
				return;
			}
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if (dragging == true) {
			boolean condition1 = false;
			
			if (draggingToBall.getPosX() >= draggingFromBall.getPosX())
				if (applet.mouseX <= draggingToBall.getPosX() + eclipseWidth / 2 && applet.mouseX >= draggingFromBall.getPosX() - eclipseWidth / 2)
					condition1 = true;
			if (draggingToBall.getPosX() <= draggingFromBall.getPosX())
				if (applet.mouseX <= draggingFromBall.getPosX() + eclipseWidth / 2 && applet.mouseX >= draggingToBall.getPosX() - eclipseWidth / 2)
					condition1 = true;

			if (condition1) {
				float k = (draggingToBall.getPosY() - draggingFromBall.getPosY()) / 
						(draggingToBall.getPosX() - draggingFromBall.getPosX());
				float b = draggingToBall.getPosY() - k * draggingToBall.getPosX();
				
				float tempY = k * applet.mouseX + b;
						
				if (applet.mouseY > tempY + eclipseWidth / 2 * PApplet.sqrt(k * k + 1) ||
						applet.mouseY < tempY - eclipseWidth / 2 * PApplet.sqrt(k * k + 1))
					dragging = false;
				if (over(applet.mouseX, applet.mouseY, draggingToBall)) {
					triggerSound(draggingFromBall.getType());
					ballList.remove(draggingFromBall);
					ballList.remove(draggingToBall);
					pushVocabulary(draggingFromBall.getKey());
					float progress = (float) (applet.millis() - draggingFromBall.getStartTime()) / draggingFromBall.getDuration();
					score += PApplet.max(0, 10000 * (1 - progress));
				}
			} else {
				dragging = false;
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		dragging = false;
	}
	
	private void triggerSound(int type) {
		if (type == 1)
			hitSound.trigger();
		else if (type == 2)
			kickSound.trigger();
		else
			snareSound.trigger();
	}

	private boolean over(int mouseX, int mouseY, Ball ball) {
		float value1 = (mouseX - ball.getPosX()) * (mouseX - ball.getPosX());
		float value2 = (mouseY - ball.getPosY()) * (mouseY - ball.getPosY());
		if (value1 + value2 <= eclipseWidth * eclipseWidth / 4)
			return true;
		return false;
	}
	
	public void keyPressed() {
		int pressedKey = applet.key;
		if (pressedKey >= 'a' && pressedKey <= 'z') {
			pressedKey -= ('a' - 'A');
		}
		if (pressedKey <= 'Z' && pressedKey >= 'A') {
			pressedKey -= 'A';
			ListIterator<Ball> listIterator = ballList.listIterator();
			while (listIterator.hasNext()) {
				Ball currentBall = listIterator.next();
				if (currentBall.getKey() == pressedKey) {
					listIterator.remove();
					triggerSound(currentBall.getType());
					vocabulary.push(currentBall.getKey());
					float progress = (float) (applet.millis() - currentBall.getStartTime()) / currentBall.getDuration();
					score += PApplet.max(0, 1000 * (1 - progress));
					break;
				}
			}
		}
	}
	
	public void init() {
		int position = 0;
		if (applet.player != null) {
			position = applet.player.position();
			applet.player.close();
		}
		if (applet.songs.isEmpty() || applet.songs.get(applet.songIndex) == null)
			return;
		applet.player = applet.minim.loadFile(applet.songs.get(applet.songIndex), 512);
		//applet.player.cue(position);
		applet.player.play();
		
		beatDetect = new BeatDetect(applet.player.bufferSize(), applet.player.sampleRate());
		beatDetect.detectMode(BeatDetect.FREQ_ENERGY);
		beatDetect.setSensitivity(1000);
		beatListener = new BeatListener(beatDetect, applet.player);
	}
	
	public void reset() {
		if (applet.player != null && applet.player.isPlaying())
			applet.player.close();
		applet.player = applet.minim.loadFile(applet.songs.get(applet.songIndex), 512);
		applet.player.play();
		beatDetect = new BeatDetect(applet.player.bufferSize(), applet.player.sampleRate());
		beatDetect.detectMode(BeatDetect.FREQ_ENERGY);
		beatDetect.setSensitivity(1000);
		beatListener = new BeatListener(beatDetect, applet.player);
		
		vocabulary.clear();
		for (int i = 0; i < 26; ++i)
			vocabulary.push(i);
	}
	
	public void nextSong() {
		
	}
	
	public void close() {
		applet.fullscreenMode = false;
		applet.player.pause();
		applet.player.close();
		applet.player = null;
	}
	
	public void draw() {
		applet.imageMode(PApplet.CENTER);
		applet.noStroke();
		applet.background(0);

		applet.fill(255, 255, 255);
		applet.textSize(64);
		applet.text(score, applet.width - 200, 50);
		
		//beatDetect.detect(player.mix);
		if (ballList.size() < difficulty && (beatDetect.isHat() || beatDetect.isKick() || beatDetect.isSnare())) {
			addNextBall();
		}
        ListIterator<Ball> listIterator = ballList.listIterator();
        
        while (listIterator.hasNext()) {
        	Ball currentBall = listIterator.next();
        	if (applet.millis() - currentBall.getStartTime() >= currentBall.getDuration()) {
        		vocabulary.push(currentBall.getKey());
        		listIterator.remove();
        	}
        	float progress = (float)(applet.millis() - currentBall.getStartTime()) / currentBall.getDuration();
        	//float radius = currentBall.getRadius()*(1.333f - progress / 3);
        	float radius = currentBall.getRadius()*(2f - 1f * progress);
        	applet.strokeWeight(3);
        	applet.stroke(currentBall.getR(), currentBall.getG(), currentBall.getB(), currentBall.getAlpha());
        	applet.noFill();
        	//fill(currentBall.getR(), currentBall.getG(), currentBall.getB(), currentBall.getAlpha());
        	applet.ellipse(currentBall.getPosX(), currentBall.getPosY(), radius, radius);
        	
        	applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        	applet.textSize(80);
        	if (currentBall.getType() == 1) {
        		applet.image(hatEclipse, currentBall.getPosX(), currentBall.getPosY());
        	} else if (currentBall.getType() == 2) {
        		applet.image(kickEclipse, currentBall.getPosX(), currentBall.getPosY());
        	} else {
        		applet.image(snareEclipse, currentBall.getPosX(), currentBall.getPosY());
        	}
        	char keyToChar = (char) ('A' + currentBall.getKey());
        	applet.text(keyToChar, currentBall.getPosX(), currentBall.getPosY());
        }
        
        if (dragging == true) {
        	applet.image(snareEclipse, applet.mouseX, applet.mouseY);
        }
	}
	
	private void addNextBall() {
		// decide whether the next ball's position would related to previous ball's position
		if (count > random.nextInt(difficulty / 2) || prevBallPosX == 0 || prevBallPosY == 0 || ballList.size() == 0) {
			//prevBallPosX = eclipseWidth + random.nextInt(width - 2 * eclipseWidth);
			prevBallPosX = 2 * eclipseWidth + random.nextInt(applet.width - 4 * eclipseWidth);
			prevBallPosY = 2 * eclipseWidth + random.nextInt(applet.height - 4 * eclipseWidth);
			count = 1;
		}
		
		// Add a new ball according to last ball's position
		do {
			float angle = random.nextFloat() * PApplet.TWO_PI;
			float multiplier = (random.nextInt(100) / 50f + 2) * PApplet.pow(-1, random.nextInt(4));
			prevBallPosX = prevBallPosX + multiplier * PApplet.cos(angle) * eclipseWidth / 2;
			prevBallPosY = prevBallPosY + multiplier * PApplet.sin(angle) * eclipseWidth / 2;
		} while(prevBallPosX > applet.width - eclipseWidth / 2 || prevBallPosX < eclipseWidth / 2 ||
				prevBallPosY > applet.height - eclipseWidth / 2 || prevBallPosY < eclipseWidth / 2);
		
		Ball nextBall = new Ball(prevBallPosX, prevBallPosY, eclipseWidth, applet.millis());
		int index = random.nextInt(vocabulary.size());
		nextBall.setKey(vocabulary.get(index));
		vocabulary.remove(index);
		
		nextBall.setColor(random.nextInt(255), random.nextInt(255), random.nextInt(255), random.nextInt(100) + 155);
		
		if (beatDetect.isSnare()) {
			nextBall.setType(3);
			float pairBallX, pairBallY;
			if (nextBall.getPosX() > applet.width / 2)
				pairBallX = nextBall.getPosX() - eclipseWidth;
			else
				pairBallX = nextBall.getPosX() + eclipseWidth;
			if (nextBall.getPosX() > applet.height / 2)
				pairBallY = nextBall.getPosY() - eclipseWidth;
			else
				pairBallY = nextBall.getPosY() + eclipseWidth;
			
			Ball pairBall = new Ball(pairBallX, pairBallY, eclipseWidth, nextBall.getStartTime());
			pairBall.setType(3);
			pairBall.setKey(nextBall.getKey());
			pairBall.setColor(nextBall.getR(), nextBall.getG(), nextBall.getB(), nextBall.getAlpha());
			ballList.add(pairBall);
			count += 3;
		} else if (beatDetect.isKick()) {
			nextBall.setType(2);
		} else if (beatDetect.isHat()){
			nextBall.setType(1);
		}
		++count;
		ballList.add(nextBall);
	}
}