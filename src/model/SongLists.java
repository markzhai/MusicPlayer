package model;

import java.util.ArrayList;
import java.util.List;

public class SongLists extends AbstractModelObject {
	private final List<SongList> lists = new ArrayList();
	
	public void addList(SongList list) {
		lists.add(list);
		firePropertyChange("lists", null, this.lists);
	}
	
	public void removeList(SongList list) {
		lists.remove(list);
		firePropertyChange("lists", null, this.lists);
	}
	
	public List<SongList> getLists() {
		return lists;
	}
}