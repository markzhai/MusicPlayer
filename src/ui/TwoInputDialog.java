package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TwoInputDialog extends Dialog {

	private String message1;
	private String message2;
	private String input1;
	private String input2;

	/**
	 * @wbp.parser.constructor
	 */
	public TwoInputDialog(Shell parent, String text) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, text);
	}

	public TwoInputDialog(Shell parent, int style, String text) {
		// Let users override the default styles
		super(parent, style);
		setText(text);
	}

	public String getMessage1() {
		return message1;
	}

	public String getMessage2() {
		return message2;
	}

	public void setMessage1(String message) {
		this.message1 = message;
	}
	
	public void setMessage2(String message) {
		this.message2 = message;
	}

	public String getInput1() {
		return input1;
	}
	
	public String getInput2() {
		return input2;
	}
	
	public void setInput1(String input) {
		this.input1 = input;
	}

	public void setInput2(String input) {
		this.input2 = input;
	}
	
	public String[] open() {
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		Point location = getParent().getLocation();
		location.x += getParent().getSize().x / 2;
		location.y += getParent().getSize().y / 2;
		shell.setLocation(location);
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return (new String[]{input1, input2});
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(2, false));

		GridData data = new GridData();
		
		Label label1 = new Label(shell, SWT.NONE);
		label1.setText(message1);

		// Display the input box
		final Text text1 = new Text(shell, SWT.BORDER);

		Label label2 = new Label(shell, SWT.NONE);
		label2.setText(message2);
		
		final Text text2 = new Text(shell, SWT.BORDER);
		
		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.CENTER);
		GridData gd_ok = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		ok.setLayoutData(gd_ok);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input1 = text1.getText();
				input2 = text2.getText();
				shell.close();
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input1 = null;
				input2 = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}
}