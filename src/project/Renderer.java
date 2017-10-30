package project;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

public class Renderer extends DefaultTreeCellRenderer {
	
	private static final long serialVersionUID = 1L;

	public Renderer() {
	
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    	super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    	Node node = (Node) value;
    	
    	Font font = new Font("Times New Roman", Font.PLAIN, 16);
		setFont(font);
		
		//Colors
		Color white = new Color(1.0f, 1.0f, 1.0f);
		Color gray = new Color(0.8f, 0.8f, 0.8f);
		Color red = new Color(0.9f, 0.2f, 0.2f);
		Color yellow = new Color(0.9f, 0.9f, 0.1f);
		Color green = new Color(0.3f, 0.7f, 0.3f);
		
		//Root
		if (node.isRoot()) {
			node.setColor(white);
			setBackground(node.getColor());
		} 
		
  		//Parent
		if (node.hasChildren() && !node.isRoot()) {
			if (!node.isActive()) {
	    		node.setColor(gray);
	    		setBackground(node.getColor());
	    		Map attributes = font.getAttributes();
	    		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
	    		setFont(new Font(attributes));
	    	} else {
	    		node.setColor(green);
	    		for (int i = 0; i < node.getChildCount(); i++) {
	    			if (((Node) node.getChildAt(i)).getColor().equals(red)) {
	    				node.setColor(red);
	    				break;
	    			} else if (((Node) node.getChildAt(i)).getColor().equals(yellow)) {
	    				if (!node.getColor().equals(red))
	    					node.setColor(yellow);
	    			}
	    		}
	    	}
			setBackground(node.getColor());
			setOpaque(true);
		}
		
		//Leaf
		if (!node.hasChildren() && !node.isRoot()) {
			if (!node.isActive()) {
	    		node.setColor(gray);
	    		setBackground(node.getColor());
	    		Map attributes = font.getAttributes();
	    		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
	    		setFont(new Font(attributes));
	    		setOpaque(true);
	    	} else if (!node.hasCode() && node.needsCode()) {
	    		node.setColor(red);
	    		setBackground(node.getColor());
	    		setOpaque(true);
	    	} else if (!node.hasTestCases()) {
	    		node.setColor(yellow);
	    		setBackground(node.getColor());
	    		setOpaque(true);
	    	} else if (node.isActive()) {
	    		node.setColor(green);
	    		setBackground(node.getColor());
	    		setOpaque(true);
	    	}
		}
		
//		if (node.isActive() && node.hasChildren()) {
//			for (int i = 0; i < node.getChildCount(); i++) {
//				if (node.getChildAt(i).
//			}
//		}
    	
		//Icon
    	if (node.getImportance() == 'A') {
    		setIcon(new ImageIcon("src/icons/aIcon.png"));
    	} else if (node.getImportance() == 'B') {
    		setIcon(new ImageIcon("src/icons/bIcon.png"));
    	} else if (node.getImportance() == 'C') {
    		setIcon(new ImageIcon("src/icons/cIcon.png"));
    	} else if (node.getImportance() == 'D') {
    		setIcon(new ImageIcon("src/icons/dIcon.png"));
    	} else if (node.getImportance() == 'E') {
    		setIcon(new ImageIcon("src/icons/eIcon.png"));
    	}
    	
    	//Selected
    	if (selected) {
    		super.setBackground(getBackgroundSelectionColor());
    		setForeground(getTextSelectionColor());
    		setOpaque(false);

    		if (hasFocus){
    			setBorderSelectionColor(UIManager.getLookAndFeelDefaults().getColor("Tree.selectionBorderColor"));
    		}
    	}

    	return this;
    }
    
}
