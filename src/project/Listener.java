package project;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class Listener implements TreeModelListener {

	public void treeNodesChanged(TreeModelEvent e) {
		Node node;
		node = (Node) (e.getTreePath().getLastPathComponent());
			int index = e.getChildIndices()[0];
			node = (Node) (node.getChildAt(index));
	}
	
	public void treeNodesInserted(TreeModelEvent e) {
		
	}
	
	public void treeNodesRemoved(TreeModelEvent e) {
			
	}
	
	public void treeStructureChanged(TreeModelEvent e) {
		
	}
	
}