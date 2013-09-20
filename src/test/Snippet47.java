package test;

/*
 * ToolBar example snippet: create tool bar (normal, hot and disabled images)
 *
 * For a list of all SWT example snippets see
 * http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/platform-swt-home/dev.html#snippets
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class Snippet47 {

public static void main (String [] args) {
  Display display = new Display ();
  Shell shell = new Shell (display);

  Image image = new Image (display, 20, 20);
  Color color = display.getSystemColor (SWT.COLOR_BLUE);
  GC gc = new GC (image);
  gc.setBackground (color);
  gc.fillRectangle (image.getBounds ());
  gc.dispose ();
  
  Image disabledImage = new Image (display, 20, 20);
  color = display.getSystemColor (SWT.COLOR_GREEN);
  gc = new GC (disabledImage);
  gc.setBackground (color);
  gc.fillRectangle (disabledImage.getBounds ());
  gc.dispose ();
  
  Image hotImage = new Image (display, 20, 20);
  color = display.getSystemColor (SWT.COLOR_RED);
  gc = new GC (hotImage);
  gc.setBackground (color);
  gc.fillRectangle (hotImage.getBounds ());
  gc.dispose ();
  
  ToolBar bar = new ToolBar (shell, SWT.BORDER | SWT.FLAT);
  bar.setSize (200, 32);
  for (int i=0; i<12; i++) {
    ToolItem item = new ToolItem (bar, 0);
    item.setImage (image);
    item.setDisabledImage (disabledImage);
    item.setHotImage (hotImage);
    if (i % 3 == 0) item.setEnabled (false);
  }
  
  shell.open ();
  bar.getItems () [1].setImage (disabledImage);
  while (!shell.isDisposed ()) {
    if (!display.readAndDispatch ()) display.sleep ();
  }
  image.dispose ();
  disabledImage.dispose ();
  hotImage.dispose ();
  display.dispose ();
}
} 

