import processing.core.PApplet;

public class Launcher {
	public static void main(String args[]) {
		String[] appletArgs = new String[] {"MusicPlayer"};
		if (args != null) {
			PApplet.main(PApplet.concat(appletArgs, args));
		} else {
			PApplet.main(appletArgs);
		}
	}
}