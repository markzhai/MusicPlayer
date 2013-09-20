

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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
				Shell shell = new ControlPanelUI(display, myParent);

				// The two buttons
				Button button1 = new Button(shell, SWT.PUSH);
				button1.setText("Left");
				button1.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						myParent.position = 30;
					}
				});
				Button button2 = new Button(shell, SWT.PUSH);
				button2.setText("Right");
				button2.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						myParent.position = 270;
					}
				});

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