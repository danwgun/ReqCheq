package project;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class Tree extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public Node root;
	public DefaultTreeModel model;
	public JTree tree;

	public Tree() {
		super(new GridLayout(1,0));
	
		root = new Node("Requirements");
		model = new DefaultTreeModel(root);
		model.addTreeModelListener(new Listener());
		
		tree = new JTree(model);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new Renderer());

		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane);
	}
	
//	public void setFonts() {
//		Color white = new Color(1.0f, 1.0f, 1.0f);
//		Color gray = new Color(0.8f, 0.8f, 0.8f);
//		Color red = new Color(0.9f, 0.2f, 0.2f);
//		Color yellow = new Color(0.9f, 0.9f, 0.1f);
//		Color green = new Color(0.3f, 0.7f, 0.3f);
//		
//		//Root
//		if (node.isRoot()) {
//			node.setColor(white);
//			setBackground(node.getColor());
//		} 
//				
//		//Parent
//		if (node.hasChildren() && !node.isRoot()) {
//			if (!node.isActive()) {
//				node.setColor(gray);
//				setBackground(node.getColor());
//				Map attributes = font.getAttributes();
//				attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
//				setFont(new Font(attributes));
//				setOpaque(true);
//			} else {
//				node.setColor(green);
//				for (int i = 0; i < node.getChildCount(); i++) {
//					if (((Node) node.getChildAt(i)).getColor().equals(red)) {
//						node.setColor(red);
//						break;
//					} else if (((Node) node.getChildAt(i)).getColor().equals(yellow)) {
//						if (!node.getColor().equals(red)) {
//							node.setColor(yellow);
//						}
//					}
//				}	
//			}	
//			setBackground(node.getColor());
//			setOpaque(true);
//		}
//				
//		//Leaf
//		if (!node.hasChildren()) {
//			if (!node.isActive()) {
//				node.setColor(gray);
//				setBackground(node.getColor());
//				Map attributes = font.getAttributes();
//				attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
//				setFont(new Font(attributes));
//				setOpaque(true);
//			} else if (!node.hasCode() && node.needsCode()) {
//				node.setColor(red);
//				setBackground(node.getColor());
//				setOpaque(true);
//			} else if (!node.hasTestCases()) {
//				node.setColor(yellow);
//				setBackground(node.getColor());
//				setOpaque(true);
//			} else if (node.isActive()) {
//				node.setColor(green);
//				setBackground(node.getColor());
//				setOpaque(true);
//			}
//		}
//	}
	
//	public void remove() {
//		TreePath currentSelection = tree.getSelectionPath();
//		
//		if (currentSelection != null) {
//			Node currentNode = (Node) (currentSelection.getLastPathComponent());
//			Node parent = (Node) (currentNode.getParent());
//			
//			if(parent != null) {
//				model.removeNodeFromParent(currentNode);
//				return;
//			}
//		}
//		
//		toolkit.beep();
//	}
	
	public Node add(Object child) {
		Node parent = null;
		TreePath parentPath = tree.getSelectionPath();
		
		if(parentPath == null) {
			parent = root;
		} else { 
			parent = (Node) (parentPath.getLastPathComponent());
		}
		
		return add(parent, child, true);
	}
	
	public Node add(Node parent, Object child) {
		return add(parent, child, false);
	}
	
	public Node add(Node parent, Object child, boolean visible) {
		Node childNode = new Node(child);
		
		if (parent == null) {
			parent = root;
		}
		
		model.insertNodeInto(childNode, parent, parent.getChildCount());
		
		if (visible) {
			tree.scrollPathToVisible( new TreePath(childNode.getPath()) );
		}
		
		return childNode;
	}

}
