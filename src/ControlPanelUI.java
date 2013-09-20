import java.util.Arrays;

import model.Song;
import model.SongList;
import model.SongLists;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.layout.*;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.win32.TCHAR;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;

import test.Snippet83;
import tools.TagExtractor;
import ui.ListDialog;

import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.ui.part.PluginTransfer;

public class ControlPanelUI extends Shell {
	private Table tableSongs;
	private SongLists songLists = new SongLists();
	private Table tableLists;
	private TableViewer listViewer;
	private TableViewer songViewer;

	private DataBindingContext m_bindingContext;
	private MusicPlayer player;
	
	private TagExtractor tagExtractor = new TagExtractor();

	private Shell shell;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					Display display = Display.getDefault();

					ControlPanelUI shell = new ControlPanelUI(display);
					shell.open();
					shell.layout();
					while (!shell.isDisposed()) {
						if (!display.readAndDispatch()) {
							display.sleep();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 * @wbp.parser.constructor
	 */
	public ControlPanelUI(Display display) {
		super(display, SWT.CLOSE | SWT.MIN | SWT.TITLE | SWT.APPLICATION_MODAL);
		
		DropTarget target = new DropTarget(this, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		target.setTransfer(new Transfer[] { FileTransfer.getInstance(), PluginTransfer.getInstance() });
		target.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				String fileList[] = null;
				FileTransfer ft = FileTransfer.getInstance();
                if (ft.isSupportedType(event.currentDataType)) {
                    fileList = (String[]) event.data;
                }
				for (int i = 0; i < fileList.length; ++i) {
					String filePath = fileList[i];
					int selectedList = 0;
					IStructuredSelection sel = (IStructuredSelection) listViewer.getSelection();
					if (sel.isEmpty()) {
						listViewer.setSelection(new StructuredSelection(listViewer.getElementAt(selectedList)),true);
					} else {
						selectedList = tableLists.getSelectionIndex();
					}
					tagExtractor.setFilepath(filePath);
					Song song = new Song(tagExtractor.getTitle(), tagExtractor.getArtist(), tagExtractor.getGenre(), filePath);
					songLists.getLists().get(selectedList).addSong(song);
				}
			}
		});
		setImage(SWTResourceManager.getImage("data\\playlist_skin.png"));
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		setLayout(rowLayout);
		shell = this;

		SashForm sashForm = new SashForm(this, SWT.SMOOTH);
		sashForm.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		sashForm.setSashWidth(2);
		sashForm.setBackground(SWTResourceManager.getColor(102, 153, 204));
		sashForm.setLayoutData(new RowData(499, 322));

		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));

		listViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableLists = listViewer.getTable();
		tableLists.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		ToolBar toolBarList = new ToolBar(composite_1, SWT.FLAT | SWT.RIGHT);

		ToolItem newListButton = new ToolItem(toolBarList, SWT.PUSH);
		newListButton.setImage(new Image(display, "data\\add.png"));
		newListButton.setHotImage(new Image(display, "data\\add_hot.png"));
		ToolItem deleteListButton = new ToolItem(toolBarList, SWT.NONE);
		deleteListButton.setImage(new Image(display, "data\\del.png"));
		deleteListButton.setHotImage(new Image(display, "data\\del_hot.png"));
		
		deleteListButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
				SongList list = (SongList) selection.getFirstElement();
				boolean confirm = MessageDialog.openConfirm(shell, "Confirm Delete",
						"Are you sure you want to delete list '" + list.getName() + "'?");
				if (confirm) {
					songLists.removeList(list);
					m_bindingContext.updateModels();
				}
			}
		});
		newListButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				SongList list = new SongList();
				ListDialog dialog = new ListDialog(shell, list, true);
				if (dialog.open() == Window.OK) {
					songLists.addList(list);
					listViewer.setSelection(new StructuredSelection(list), true);
					m_bindingContext.updateModels();
				}
			}
		});

		Composite composite_2 = new Composite(sashForm, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));

		songViewer = new TableViewer(composite_2, SWT.FULL_SELECTION | SWT.MULTI);
		songViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				Song selectedSong = (Song) sel.getFirstElement();
				player.musicChanged = true;
				player.filename = selectedSong.getFilename();
			}
		});

		tableSongs = songViewer.getTable();
		tableSongs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableSongs.setLinesVisible(true);
		tableSongs.setHeaderVisible(true);

		final TableColumn newColumnTableColumn = new TableColumn(tableSongs, SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("artist");

		final TableColumn newColumnTableColumn_1 = new TableColumn(tableSongs, SWT.NONE);
		newColumnTableColumn_1.setWidth(160);
		newColumnTableColumn_1.setText("title");

		final TableColumn newColumnTableColumn_2 = new TableColumn(tableSongs, SWT.NONE);
		newColumnTableColumn_2.setWidth(120);
		newColumnTableColumn_2.setText("style");

		ToolBar toolBarSong = new ToolBar(composite_2, SWT.FLAT | SWT.RIGHT);
		toolBarSong.setSize(141, 322);

		ToolItem addSongButton = new ToolItem(toolBarSong, SWT.NONE);
		addSongButton.setImage(new Image(display, "data\\add.png"));
		addSongButton.setHotImage(new Image(display, "data\\add_hot.png"));
		addSongButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
				fd.setText("Open");
				fd.setFilterPath("E:\\");
				String[] filterExt = { "*.mp3", "*.wav", "*.aiff", ".au", "*.snd", "*.*" };
				fd.setFilterExtensions(filterExt);
				fd.open();
				
				String[] selected = fd.getFileNames();
				for (int i = 0; i < selected.length; ++i) {
					String filePath = fd.getFilterPath() + "\\" + selected[i];
					int selectedList = 0;
					IStructuredSelection sel = (IStructuredSelection) listViewer.getSelection();
					if (sel.isEmpty()) {
						listViewer.setSelection(new StructuredSelection(listViewer.getElementAt(selectedList)),true);
					} else {
						selectedList = tableLists.getSelectionIndex();
					}
					tagExtractor.setFilepath(filePath);
					Song song = new Song(tagExtractor.getTitle(), tagExtractor.getArtist(), tagExtractor.getGenre(), filePath);
					songLists.getLists().get(selectedList).addSong(song);
				}
				m_bindingContext.updateModels();
			}
		});

		ToolItem deleteSongButton = new ToolItem(toolBarSong, SWT.NONE);
		deleteSongButton.setImage(new Image(display, "data\\del.png"));
		deleteSongButton.setHotImage(new Image(display, "data\\del_hot.png"));
		deleteSongButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection listSelection = (IStructuredSelection) listViewer.getSelection();
				IStructuredSelection songSelection = (IStructuredSelection) songViewer.getSelection();
				SongList list = (SongList) listSelection.getFirstElement();
				Song song = (Song) songSelection.getFirstElement();
				list.removeSong(song);
				m_bindingContext.updateModels();
			}
		});

		ToolItem effectButton = new ToolItem(toolBarSong, SWT.NONE);
		effectButton.setText("Effects");
		sashForm.setWeights(new int[] { 89, 334 });

		setDefaultValues();
		createContents();
		m_bindingContext = initDataBindings();
	}

	public void setDefaultValues() {
		SongList songs = new SongList("Default");
		SongList songs2 = new SongList("Favorite");
		
		songs2.addSong(new Song("Etertica Graffiti", "Disaster", "Douji", "songs\\Disaster - Etertica Graffiti.mp3"));
		songs2.addSong(new Song("PSI - missing", "川田まみ", "pop", "songs\\川田まみ - PSI - missing.mp3"));
		songs2.addSong(new Song("幽雅に咲かせ、墨染の桜 ～ Border of Life （Pyrotechnic mix）", "itm", "douji", "songs\\itm - 幽雅に咲かせ、墨染の桜 ～ Border of Life （Pyrotechnic mix）.mp3"));
		
		songLists.addList(songs);
		songLists.addList(songs2);
	}

	public ControlPanelUI(Display display, MusicPlayer player) {
		this(display);
		this.player = player;
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("PlayList");
		setSize(510, 354);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();

		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), SongList.class, "name");
		listViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		listViewer.setContentProvider(listContentProvider);

		IObservableList listsSongListsObserveList = BeanProperties.list("lists").observe(songLists);
		listViewer.setInput(listsSongListsObserveList);

		ObservableListContentProvider listContentProvider_1 = new ObservableListContentProvider();
		IObservableMap[] observeMaps = BeansObservables.observeMaps(
				listContentProvider_1.getKnownElements(), Song.class, new String[] { "artist", "title", "style" });
		songViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		songViewer.setContentProvider(listContentProvider_1);

		IObservableValue observeSingleSelectionListViewer = ViewerProperties.singleSelection().observe(listViewer);
		IObservableList listViewerSongsObserveDetailList = BeanProperties.list(
				SongList.class, "songs", Song.class).observeDetail(observeSingleSelectionListViewer);
		songViewer.setInput(listViewerSongsObserveDetailList);

		return bindingContext;
	}
	
	  static String getNameFromId(int id) {
		    String name = null;
		    int maxSize = 128;
		    TCHAR buffer = new TCHAR(0, maxSize);
		    int size = COM.GetClipboardFormatName(id, buffer, maxSize);
		    if (size != 0) {
		      name = buffer.toString(0, size);
		    } else {
		      switch (id) {
		      case COM.CF_HDROP:
		        name = "CF_HDROP";
		        break;
		      case COM.CF_TEXT:
		        name = "CF_TEXT";
		        break;
		      case COM.CF_BITMAP:
		        name = "CF_BITMAP";
		        break;
		      case COM.CF_METAFILEPICT:
		        name = "CF_METAFILEPICT";
		        break;
		      case COM.CF_SYLK:
		        name = "CF_SYLK";
		        break;
		      case COM.CF_DIF:
		        name = "CF_DIF";
		        break;
		      case COM.CF_TIFF:
		        name = "CF_TIFF";
		        break;
		      case COM.CF_OEMTEXT:
		        name = "CF_OEMTEXT";
		        break;
		      case COM.CF_DIB:
		        name = "CF_DIB";
		        break;
		      case COM.CF_PALETTE:
		        name = "CF_PALETTE";
		        break;
		      case COM.CF_PENDATA:
		        name = "CF_PENDATA";
		        break;
		      case COM.CF_RIFF:
		        name = "CF_RIFF";
		        break;
		      case COM.CF_WAVE:
		        name = "CF_WAVE";
		        break;
		      case COM.CF_UNICODETEXT:
		        name = "CF_UNICODETEXT";
		        break;
		      case COM.CF_ENHMETAFILE:
		        name = "CF_ENHMETAFILE";
		        break;
		      case COM.CF_LOCALE:
		        name = "CF_LOCALE";
		        break;
		      case COM.CF_MAX:
		        name = "CF_MAX";
		        break;
		      }

		    }
		    return name;
		  }
}
