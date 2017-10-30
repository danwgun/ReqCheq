/* This class serves as a color key for the user.
 */

package project;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTree;

public class ColorKey extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public ColorKey() {
		super(new GridLayout());
		
		Node root = new Node("Key");
		
		Node active = new Node("Active, has code, has test cases");
		active.needsCode(false);
		active.addTestCase("");
		
		Node inactive = new Node("Inactive");
		inactive.setActive(false);
		
		Node missingCode = new Node("Missing code");
		missingCode.addTestCase("");
		
		Node missingTestCases = new Node("Missing test cases");
		missingTestCases.needsCode(false);
		
		JTree keyTree = new JTree(root);
		root.add(active);
		root.add(inactive);
		root.add(missingCode);
		root.add(missingTestCases);
		
		keyTree.setCellRenderer(new Renderer());
		
		for (int i = 0; i < keyTree.getRowCount(); i++) {
			keyTree.expandRow(i);
		}
		
		add(keyTree);
	}
}
