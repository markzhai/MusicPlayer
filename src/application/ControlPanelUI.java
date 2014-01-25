package application;

import java.util.ArrayList;

import model.Song;
import model.SongList;
import model.SongLists;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.part.PluginTransfer;

import ui.OneInputDialog;
import ui.ListDialog;
import ui.TwoInputDialog;

public class ControlPanelUI extends Shell {
	private MusicPlayer player;
	private SongLists songLists = new SongLists();
	private TagExtractor tagExtractor = new TagExtractor();

	private Shell shell;
	private ToolItem newListButton, deleteListButton, addSongButton, deleteSongButton, effectButton;
	private DropTarget target;
	private Composite composite_1, composite_2;
	private Table tableSongs;
	private Table tableLists;
	private TableViewer listViewer;
	private TableViewer songViewer;
	private DataBindingContext m_bindingContext;
	private ToolItem gameButton;
	private ToolItem analyzeButton;
	
	private Analyzer analyzer;

	private final Menu effectMenu;
    private MenuItem echoEffectItem, fadeInEffectItem, fadeOutEffectItem, reverseEffectItem, changePitchEffectItem;
    private MenuItem echoEffectOff, lightEcho, intenseEcho, customEcho;
    private MenuItem changePitchEffectOff, slowPitch, fastPitch, customPitch;
    
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
		shell = this;
		
		this.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		target = new DropTarget(this, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		target.setTransfer(new Transfer[] { FileTransfer.getInstance(), PluginTransfer.getInstance() });

		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		setLayout(rowLayout);

		SashForm sashForm = new SashForm(this, SWT.SMOOTH);
		sashForm.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		sashForm.setSashWidth(2);
		sashForm.setBackground(SWTResourceManager.getColor(66,67,71));
		sashForm.setLayoutData(new RowData(499, 322));

		composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));

		listViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableLists = listViewer.getTable();
		tableLists.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		FontData fontData = new FontData("Broadway", 12, SWT.NONE);
		Font font = new Font(display, fontData);
		this.setFont(font);
		Color myColor = new Color(getDisplay(),255,255,255 );//204, 248, 6
		tableLists.setForeground(myColor);
		tableLists.setFont(font);
		
		ToolBar toolBarList = new ToolBar(composite_1, SWT.FLAT | SWT.RIGHT);

		newListButton = new ToolItem(toolBarList, SWT.PUSH);
		newListButton.setImage(new Image(display, Config.SKIN_DIRECTORY + "add.png"));
		newListButton.setHotImage(new Image(display, "data\\add_hot.png"));
		deleteListButton = new ToolItem(toolBarList, SWT.NONE);
		deleteListButton.setImage(new Image(display, "data\\del.png"));
		deleteListButton.setHotImage(new Image(display, "data\\del_hot.png"));

		composite_2 = new Composite(sashForm, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));

		
		songViewer = new TableViewer(composite_2, SWT.FULL_SELECTION | SWT.MULTI | SWT.TRANSPARENCY_MASK);
		tableSongs = songViewer.getTable();
		tableSongs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		fontData = new FontData("Arial", 12, SWT.NONE);
		font = new Font(display, fontData);
		this.setFont(font);
		myColor = new Color(getDisplay(), 255, 255, 255);
		tableSongs.setForeground(myColor);
		tableSongs.setFont(font);
		
		tableSongs.setLinesVisible(false);
		tableSongs.setHeaderVisible(true);
		final TableColumn newColumnTableColumn = new TableColumn(tableSongs, SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("Artist");

		final TableColumn newColumnTableColumn_1 = new TableColumn(tableSongs, SWT.NONE);
		newColumnTableColumn_1.setWidth(160);
		newColumnTableColumn_1.setText("Title");

		final TableColumn newColumnTableColumn_2 = new TableColumn(tableSongs, SWT.NONE);
		newColumnTableColumn_2.setWidth(120);
		newColumnTableColumn_2.setText("Style");

		ToolBar toolBarSong = new ToolBar(composite_2, SWT.FLAT | SWT.RIGHT);
		toolBarSong.setSize(141, 322);

		addSongButton = new ToolItem(toolBarSong, SWT.NONE);
		addSongButton.setImage(new Image(display, "data\\add.png"));
		addSongButton.setHotImage(new Image(display, "data\\add_hot.png"));
		deleteSongButton = new ToolItem(toolBarSong, SWT.NONE);
		deleteSongButton.setImage(new Image(display, "data\\del.png"));
		deleteSongButton.setHotImage(new Image(display, "data\\del_hot.png"));
		
		analyzeButton = new ToolItem(toolBarSong, SWT.NONE);
		analyzeButton.setImage(new Image(display, "data\\analyze.png"));
		analyzeButton.setHotImage(new Image(display, "data\\analyze_hot.png"));
		//analyzeButton.setText("Analyze");
		
		effectButton = new ToolItem(toolBarSong, SWT.NONE);
		//effectButton.setText("Effects");
		effectButton.setImage(new Image(display, "data\\effect.png"));
		effectButton.setHotImage(new Image(display, "data\\effect_hot.png"));
        effectMenu = new Menu(shell, SWT.POP_UP);
        
        echoEffectItem = new MenuItem(effectMenu, SWT.CASCADE);
        echoEffectItem.setText("Echo");
        Menu echoEffectMenu = new Menu(shell, SWT.DROP_DOWN);
        echoEffectItem.setMenu(echoEffectMenu);
        
        echoEffectOff = new MenuItem(echoEffectMenu, SWT.RADIO);
        echoEffectOff.setText("off");
        lightEcho = new MenuItem(echoEffectMenu, SWT.RADIO);
        lightEcho.setText("light");
        intenseEcho = new MenuItem(echoEffectMenu, SWT.RADIO);
        intenseEcho.setText("intense");
        customEcho = new MenuItem(echoEffectMenu, SWT.RADIO);
        customEcho.setText("custom");
        echoEffectOff.setSelection(true);
        
        fadeInEffectItem = new MenuItem(effectMenu, SWT.CHECK);
        fadeInEffectItem.setText("Fade in");
        fadeOutEffectItem = new MenuItem(effectMenu, SWT.CHECK);
        fadeOutEffectItem.setText("Fade out");
        reverseEffectItem = new MenuItem(effectMenu, SWT.CHECK);
        reverseEffectItem.setText("Reverse");
        
        changePitchEffectItem = new MenuItem(effectMenu, SWT.CASCADE);
        changePitchEffectItem.setText("Change Pitch");
        Menu changePitchEffectMenu = new Menu(shell, SWT.DROP_DOWN);
        changePitchEffectItem.setMenu(changePitchEffectMenu);
        
        changePitchEffectOff = new MenuItem(changePitchEffectMenu, SWT.RADIO);
        changePitchEffectOff.setText("off");
        slowPitch = new MenuItem(changePitchEffectMenu, SWT.RADIO);
        slowPitch.setText("lower (-4)");
        fastPitch = new MenuItem(changePitchEffectMenu, SWT.RADIO);
        fastPitch.setText("higher (12)");
        customPitch = new MenuItem(changePitchEffectMenu, SWT.RADIO);
        customPitch.setText("custom");
        changePitchEffectOff.setSelection(true);
		
		gameButton = new ToolItem(toolBarSong, SWT.NONE);
		gameButton.setImage(new Image(display, "data\\osu_new.ico"));
		gameButton.setWidth(2);
		sashForm.setWeights(new int[] { 89, 334 });

		setBackgroundImages();
		addListeners();
		setDefaultValues();
		createContents();
		m_bindingContext = initDataBindings();
	}

	public void setBackgroundImages() {
		final Image img1 = new Image(Display.getDefault(), "data\\shell_skin.png");
		final Image img2 = new Image(Display.getDefault(), "data\\lists_skin.png");
		final Image img3 = new Image(Display.getDefault(), "data\\list_skin.png");

		this.setBackgroundImage(img1);
		tableLists.setBackgroundImage(img2);
		composite_1.setBackgroundImage(img1);
		composite_2.setBackgroundImage(img1);
		tableSongs.setBackgroundImage(img3);
	}
	
	public void setDefaultValues() {
		SongList songs = new SongList("Default");
		SongList songs2 = new SongList("Favorite");
		
		String filePath = "songs\\Believe in the light.mp3";
		tagExtractor.setFilepath(filePath);
		Song song = new Song(tagExtractor.getTitle(), tagExtractor.getArtist(), tagExtractor.getGenre(), filePath);
		songs2.addSong(song);
		filePath = "songs\\Etertica Graffiti.mp3";
		tagExtractor.setFilepath(filePath);
		song = new Song(tagExtractor.getTitle(), tagExtractor.getArtist(), tagExtractor.getGenre(), filePath);
		songs2.addSong(song);
		filePath = "songs\\PSI - missing.mp3";
		tagExtractor.setFilepath(filePath);
		song = new Song(tagExtractor.getTitle(), tagExtractor.getArtist(), tagExtractor.getGenre(), filePath);
		songs2.addSong(song);
		filePath = "songs\\Virtuous suicide.mp3";
		tagExtractor.setFilepath(filePath);
		song = new Song(tagExtractor.getTitle(), tagExtractor.getArtist(), tagExtractor.getGenre(), filePath);
		songs2.addSong(song);
		filePath = "songs\\YugaNiSakase.mp3";
		tagExtractor.setFilepath(filePath);
		song = new Song(tagExtractor.getTitle(), tagExtractor.getArtist(), tagExtractor.getGenre(), filePath);
		songs2.addSong(song);
		
		songLists.addList(songs);
		songLists.addList(songs2);
	}

	public ControlPanelUI(Display display, MusicPlayer player) {
		this(display);
		this.player = player;
		this.analyzer = new Analyzer(player.minim);
		player.setControlPanel(this);
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
		IObservableMap[] observeMaps = BeansObservables.observeMaps(listContentProvider_1.getKnownElements(), Song.class, new String[] { "artist", "title", "style" });
		songViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		songViewer.setContentProvider(listContentProvider_1);

		IObservableValue observeSingleSelectionListViewer = ViewerProperties.singleSelection().observe(listViewer);
		IObservableList listViewerSongsObserveDetailList = BeanProperties.list(SongList.class, "songs", Song.class).observeDetail(observeSingleSelectionListViewer);
		songViewer.setInput(listViewerSongsObserveDetailList);

		return bindingContext;
	}

	private void addListeners() {
		// double click on a song, would trigger songlist and song update
		this.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (player == null)
					return;
				player.stop();
				player.exit();
			}
		});
		songViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				// IStructuredSelection sel = (IStructuredSelection)
				// event.getSelection();
				// Song selectedSong = (Song) sel.getFirstElement();
				// player.setMusicChanged(true);
				// StructuredSelection selected =
				// (StructuredSelection)event.getSelection();
				// player.setSongs(new String[] {selectedSong.getFilename()});
				SongList list = songLists.getLists().get(listViewer.getTable().getSelectionIndex());
				player.setSongListName(list.getName());
				player.setSongs(list.getSongFilenames());
				player.setSongIndex(songViewer.getTable().getSelectionIndex());
				player.setMusicChanged(true);
			}
		});
		// delete an existed list
		deleteListButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
				SongList list = (SongList) selection.getFirstElement();
				boolean confirm = MessageDialog.openConfirm(shell, 
						"Confirm Delete", "Are you sure you want to delete list '" + list.getName() + "'?");
				if (confirm) {
					songLists.removeList(list);
					m_bindingContext.updateModels();
				}
			}
		});
		// create a new list
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
		// drop songs to a list, would update player's songlist if player is using this list
		target.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				String fileList[] = null;
				FileTransfer ft = FileTransfer.getInstance();
				if (ft.isSupportedType(event.currentDataType)) {
					fileList = (String[]) event.data;
				}

				int selectedListIndex = 0;
				IStructuredSelection sel = (IStructuredSelection) listViewer.getSelection();
				if (sel.isEmpty()) {
					listViewer.setSelection(new StructuredSelection(listViewer.getElementAt(selectedListIndex)), true);
				} else {
					selectedListIndex = tableLists.getSelectionIndex();
				}
				SongList selectedList = songLists.getLists().get(selectedListIndex);
				for (int i = 0; i < fileList.length; ++i) {
					String filePath = fileList[i];
					tagExtractor.setFilepath(filePath);
					Song song = new Song(tagExtractor.getTitle(), tagExtractor.getArtist(), tagExtractor.getGenre(), filePath);
					selectedList.addSong(song);
				}
				if (player.getSongListName() != null && player.getSongListName() == selectedList.getName())
					player.setSongs(selectedList.getSongFilenames());
			}
		});
		// add song to a list, would update player's currently using list
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
					int selectedListIndex = 0;
					IStructuredSelection sel = (IStructuredSelection) listViewer.getSelection();
					if (sel.isEmpty()) {
						listViewer.setSelection(new StructuredSelection(listViewer.getElementAt(selectedListIndex)), true);
					} else {
						selectedListIndex = tableLists.getSelectionIndex();
					}
					tagExtractor.setFilepath(filePath);
					Song song = new Song(tagExtractor.getTitle(), tagExtractor.getArtist(), tagExtractor.getGenre(), filePath);

					SongList selectedList = songLists.getLists().get(selectedListIndex);
					selectedList.addSong(song);
					if (player.getSongListName() != null && player.getSongListName().equals(selectedList.getName()))
						player.setSongs(selectedList.getSongFilenames());
				}
				m_bindingContext.updateModels();
			}
		});
		// delete a song from a song list, would update player's song if the list is being used
		deleteSongButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection listSelection = (IStructuredSelection) listViewer.getSelection();
				IStructuredSelection songSelection = (IStructuredSelection) songViewer.getSelection();
				SongList list = (SongList) listSelection.getFirstElement();
				Song song = (Song) songSelection.getFirstElement();
				if (song == null)
					return;
				list.removeSong(song);
				if (player.getSongListName() != null && player.getSongListName().equals(list.getName()))
					player.setSongs(list.getSongFilenames());
				m_bindingContext.updateModels();
			}
		});

		analyzeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                Menu analyzeMenu = new Menu(shell, SWT.POP_UP);

                MenuItem analyzeSongItem = new MenuItem(analyzeMenu, SWT.PUSH);
                analyzeSongItem.setText("Current Song");
                analyzeSongItem.addSelectionListener(new SelectionAdapter() {
		            @Override
		            public void widgetSelected(SelectionEvent e) {						
		            	int style = analyzer.getStyle(songLists.getLists().get(listViewer.getTable().getSelectionIndex()).getSongFilenames().get(songViewer.getTable().getSelectionIndex()));
		            	Song song = songLists.getLists().get(listViewer.getTable().getSelectionIndex()).getSongs().get(songViewer.getTable().getSelectionIndex());
		            	song.setStyle(Config.SYTLE_STRING[style - 1]);
		            	tagExtractor.setFilepath(song.getFilename());
		            	tagExtractor.setStyle(Config.SYTLE_STRING[style - 1]);
		            }
				});

                MenuItem analyzeListItem = new MenuItem(analyzeMenu, SWT.PUSH);
                analyzeListItem.setText("Current Songlist");
                analyzeListItem.addSelectionListener(new SelectionAdapter() {
		            @Override
		            public void widgetSelected(SelectionEvent e) {
		            	ArrayList<String> filenames = songLists.getLists().get(listViewer.getTable().getSelectionIndex()).getSongFilenames();
		            	ArrayList<Integer> styles = analyzer.getStyle(filenames);
		            	System.out.println(styles.toString());
		            }
				});

                analyzeMenu.setVisible(true);
            }
        });
        echoEffectOff.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (echoEffectOff.getSelection())
            		player.removeAudioEffect(Config.ECHO_EFFECT);
            }
		});
        lightEcho.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (lightEcho.getSelection())
            		player.addAudioEffect(Config.ECHO_EFFECT, Config.ECHO_LIGHT_DELAY, Config.ECHO_LIGHT_MULTIPLIER);
            }
		});
        intenseEcho.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (intenseEcho.getSelection())
            		player.addAudioEffect(Config.ECHO_EFFECT, Config.ECHO_INTENSE_DELAY, Config.ECHO_INTENSE_MULTIPLIER);
            }
		});
        customEcho.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (customEcho.getSelection()) {
            		TwoInputDialog dialog = new TwoInputDialog(shell, SWT.APPLICATION_MODAL, "Echo");
    				dialog.setMessage1("Delay time (sec): ");  //1
    				dialog.setMessage2("Decay factor: "); //0.5
    				String[] result = dialog.open();
    				if (result == null || result.length != 2 || result[0] == null || result[1] == null) {
    					echoEffectOff.setSelection(true);
    					customEcho.setSelection(false);
    					return;
    				}
    				float delay = Float.valueOf(result[0]);
    				float multiplier = Float.valueOf(result[1]);
            		player.addAudioEffect(Config.ECHO_EFFECT, delay, multiplier);
            	}
            }
		});
        fadeInEffectItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (fadeInEffectItem.getSelection())
            		player.addAudioEffect(Config.FADEIN_EFFECT);
            	else
            		player.removeAudioEffect(Config.FADEIN_EFFECT);
            }
		});
        fadeOutEffectItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (fadeOutEffectItem.getSelection())
            		player.addAudioEffect(Config.FADEOUT_EFFECT);
            	else
            		player.removeAudioEffect(Config.FADEOUT_EFFECT);
            }
		});
        reverseEffectItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (reverseEffectItem.getSelection())
            		player.addAudioEffect(Config.REVERSE_EFFECT);
            	else
            		player.removeAudioEffect(Config.REVERSE_EFFECT);
            }
		});
        changePitchEffectOff.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (changePitchEffectOff.getSelection())
            		player.removeAudioEffect(Config.CHANGEPITCH_EFFECT);
            }
		});
        slowPitch.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (slowPitch.getSelection())
            		player.addAudioEffect(Config.CHANGEPITCH_EFFECT, -4);
            }
		});
        fastPitch.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (fastPitch.getSelection())
            		player.addAudioEffect(Config.CHANGEPITCH_EFFECT, 12);
            }
		});
        customPitch.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (customPitch.getSelection()) {
    				OneInputDialog dialog = new OneInputDialog(shell, SWT.APPLICATION_MODAL, "Change Pitch Effect");
    				dialog.setMessage("Pitch (-36 ~ 36): ");
    				String value = dialog.open();
            		if (value != null) {
            			player.addAudioEffect(Config.CHANGEPITCH_EFFECT, Float.valueOf(value));
            		} else {
            			changePitchEffectOff.setSelection(true);
            			customPitch.setSelection(false);
            			player.removeAudioEffect(Config.CHANGEPITCH_EFFECT);
            		}
            	}
            }
		});
		effectButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                effectMenu.setVisible(true);
            }
        });
		gameButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				OneInputDialog dialog = new OneInputDialog(shell, SWT.APPLICATION_MODAL, "Set up game difficulty");
				dialog.setMessage("Please enter a value (1 ~ 10): ");
				String value = dialog.open();
				if (value != null && !value.equals("")) {
					int difficulty = Integer.valueOf(value);
					if (difficulty < 10 && difficulty > 0) {
						player.gameDifficulty = difficulty;
						player.startGame();
					}
				}
			}
		});
	}
}
