package ui;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import application.Config;
import application.ControlPanelUI;
import application.MusicPlayer;

public class SwtUI implements Runnable {
	private MusicPlayer myParent;

	public SwtUI(MusicPlayer myPapa) {
		this.myParent = myPapa;
	}

	@Override
	public void run() {
		final Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				final Shell shell = new ControlPanelUI(display, myParent);
				shell.setImage(new Image(display, Config.ICON));
				shell.open();
				shell.layout();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
		}
		});
	}
}