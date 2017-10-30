package project;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main extends JPanel implements Runnable, ActionListener, TreeSelectionListener {
	
	private static final long serialVersionUID = 1L;
	
	private Tree tree;
	
	private JEditorPane previewCodePane;
	private JEditorPane previewTestCasesPane;
	private JLabel requirementLabel = new JLabel("Requirement:");
	private JCheckBox activeCheckBox;
	private JCheckBox needsCodeCheckBox;
	
	public static File file;
	private Document doc = null;
	private Element xmlRoot;
	
	private int currentLevel = 1;
	private boolean unsavedChanges = false;
	private ArrayList<String> treeNodes = new ArrayList<String>();
	private ArrayList<String> codelessNodes = new ArrayList<String>();
	private ArrayList<String> testcaselessNodes = new ArrayList<String>();
	public static ArrayList<String> fileTypes = new ArrayList<String>();
	
	public Main() {
		super(new BorderLayout());
		
		tree = new Tree();
		tree.setMinimumSize(new Dimension(250, 685));
		tree.tree.addTreeSelectionListener(this);
		tree.tree.setCellRenderer(new Renderer());
		
		ColorKey key = new ColorKey();
		key.setPreferredSize(new Dimension(250, 100));
		
		previewCodePane = new JEditorPane();
		previewCodePane.setEditable(false);
		JScrollPane previewCode = new JScrollPane(previewCodePane);
		previewCode.setPreferredSize(new Dimension(0, 500));
		previewCode.setMinimumSize(new Dimension(150, 150));
		
		previewTestCasesPane = new JEditorPane();
		previewTestCasesPane.setEditable(false);
		JScrollPane previewTestCases = new JScrollPane(previewTestCasesPane);
		previewTestCases.setMinimumSize(new Dimension(150, 150));
		
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftSplitPane.setTopComponent(tree);
		leftSplitPane.setBottomComponent(key);
		
		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightSplitPane.setTopComponent(previewCode);
		rightSplitPane.setBottomComponent(previewTestCases);
		
		requirementLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		requirementLabel.setHorizontalAlignment(JLabel.CENTER);
		
		JButton xmlButton = new JButton("Load tree from xml file");
		xmlButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		xmlButton.addActionListener(this);
		
		JButton addButton = new JButton("Add node");
		addButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		addButton.addActionListener(this);
		
		activeCheckBox = new JCheckBox("Active");
		activeCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		activeCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Node node = (Node) tree.tree.getLastSelectedPathComponent();
				
				boolean before = node.isActive();
				
				if (e.getStateChange() == ItemEvent.SELECTED) {
					node.setActive(true);
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					node.setActive(false);
				}

				boolean after = node.isActive();
				
				if (before != after) {
					unsavedChanges = true;
				}
			}
		});
		JPanel activeCheckBoxPanel = new JPanel();
		activeCheckBoxPanel.setLayout(new FlowLayout());
		activeCheckBoxPanel.add(activeCheckBox);
		
		needsCodeCheckBox = new JCheckBox("Needs code");
		needsCodeCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		needsCodeCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Node node = (Node) tree.tree.getLastSelectedPathComponent();
				
				boolean before = node.needsCode();
				
				if (e.getStateChange() == ItemEvent.SELECTED) {
					node.needsCode(true);
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					node.needsCode(false);
				}
				
				boolean after = node.needsCode();
				
				if (before != after) {
					unsavedChanges = true;
				}
			}
		});
		JPanel needsCodeCheckBoxPanel = new JPanel();
		needsCodeCheckBoxPanel.setLayout(new FlowLayout());
		needsCodeCheckBoxPanel.add(needsCodeCheckBox);
		 
		JButton importanceButton = new JButton("Set importance level");
		importanceButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		importanceButton.addActionListener(this);
		
		JButton linkButton = new JButton("Link to another requirement");
		linkButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		linkButton.addActionListener(this);
		
		JButton saveButton = new JButton("Save");
		saveButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		saveButton.addActionListener(this);
		
		JButton fileTypesButton = new JButton("Change or add file types to be read");
		fileTypesButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		fileTypesButton.addActionListener(this);
		
		JButton reportButton = new JButton("Show report");
		reportButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		reportButton.addActionListener(this);
		
		JButton createTestCaseButton = new JButton("Create test case");
		createTestCaseButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		createTestCaseButton.addActionListener(this);
		
		JButton showNodeAttributesButton = new JButton("Show node attributes");
		showNodeAttributesButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		showNodeAttributesButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0, 5));
		buttonPanel.add(xmlButton);
		buttonPanel.add(fileTypesButton);
		buttonPanel.add(addButton);
		buttonPanel.add(reportButton);
		buttonPanel.add(saveButton);

		JPanel requirementButtonPanelTop = new JPanel();
		requirementButtonPanelTop.setLayout(new GridLayout(0, 2));
		requirementButtonPanelTop.add(activeCheckBoxPanel);
		requirementButtonPanelTop.add(needsCodeCheckBoxPanel);	
		
		JPanel requirementButtonPanelBottom = new JPanel();
		requirementButtonPanelBottom.setLayout(new GridLayout(0, 4));
		requirementButtonPanelBottom.add(linkButton);
		requirementButtonPanelBottom.add(createTestCaseButton);
		requirementButtonPanelBottom.add(showNodeAttributesButton);
		requirementButtonPanelBottom.add(importanceButton);
		
		JPanel rightPanelTopPanel = new JPanel();
		rightPanelTopPanel.setLayout(new GridLayout(3, 0));
		rightPanelTopPanel.add(requirementLabel);
		rightPanelTopPanel.add(requirementButtonPanelTop);
		rightPanelTopPanel.add(requirementButtonPanelBottom);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout());
		leftPanel.add(leftSplitPane);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(rightPanelTopPanel, BorderLayout.NORTH);
		rightPanel.add(rightSplitPane, BorderLayout.CENTER);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(leftPanel);
		splitPane.setRightComponent(rightPanel);
		splitPane.setPreferredSize(new Dimension(1400, 800));

		add(splitPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public void saveTree(Element root, TreeNode node){
		if (node.getChildCount() > 0) {
			for (int i = 0; i < node.getChildCount(); i++) {
				Element element1 = doc.createElement("Node");
				element1.setAttribute("index", ((Node) node.getChildAt(i)).index());
				element1.setAttribute("content", (node.getChildAt(i)) + "");
				element1.setAttribute("active", ((Node) node.getChildAt(i)).isActive() + "");
				element1.setAttribute("importance", ((Node) node.getChildAt(i)).getImportance() + "");
				element1.setAttribute("link", ((Node) node.getChildAt(i)).getLinkIndex());
				element1.setAttribute("testCasesCount", ((Node) node.getChildAt(i)).getTestCases().size() + "");
				element1.setAttribute("needsCode", ((Node) node.getChildAt(i)).needsCode() + "");
				
				root.appendChild(element1);
				
				saveTree(element1, node.getChildAt(i));
			}
		}
	
		Element element2 = doc.createElement("TestCase");
		element2.setAttribute("index", ((Node) node).index());
		for (int i = 0; i < ((Node) node).getTestCases().size(); i++) {
			element2.setAttribute("case" + (i + 1), ((Node) node).getTestCaseAtIndex(i));
		}
		root.appendChild(element2);
	}
	
	public void addChildrenToRoot() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document loadDoc = builder.parse(file);
			loadDoc.getDocumentElement().normalize();
			NodeList nodeList = loadDoc.getElementsByTagName("Node");
			
			for(int i = 0; i < nodeList.getLength(); i++) {
				org.w3c.dom.Node docNode = nodeList.item(i);
				
				if(docNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					Element element = (Element) docNode;
					
					if (element.getAttribute("index").split("\\.").length == currentLevel) {
						Node node = new Node(element.getAttribute("content"));
						
						tree.add(node);
					}
				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		currentLevel++;
	}
	
	public void addChildren(Node n) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document loadDoc = builder.parse(file);
			loadDoc.getDocumentElement().normalize();
			NodeList nodeList = loadDoc.getElementsByTagName("Node");
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				org.w3c.dom.Node docNode = nodeList.item(i);
				
				if(docNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					Element element = (Element) docNode;
					
					if (element.getAttribute("index").split("\\.").length == currentLevel) {
						String[] indexList = element.getAttribute("index").split("\\.");
						int count = 0;
						
						for (int j = 0; j < indexList.length; j++) {
							if (count == indexList.length - 1) {
								Node node = new Node(element.getAttribute("content"));
								
								tree.add((Node) n, node);
							} else if (indexList.length - 1 < j || n.index().split("\\.").length - 1 < j) {
								
							} else if (indexList[j].equals(n.index().split("\\.")[j])) {
								count++;
							}
						}
					}
				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		if (n.hasChildren()) {
			currentLevel++; 
			
			addChildren((Node) n.getChildAt(0));
			
			currentLevel--;
		}

		if (n.getNextSibling() != null) {
			addChildren((Node) n.getNextSibling());
		}
	}
	
	public void setNodeVariables(Node node) {
		if (node.getChildCount() > 0) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document loadDoc = builder.parse(file);
				loadDoc.getDocumentElement().normalize();
				NodeList nodeList = loadDoc.getElementsByTagName("Node");
				
				for(int i = 0; i < nodeList.getLength(); i++) {
					org.w3c.dom.Node docNode = nodeList.item(i);
					
					if(docNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
						Element element = (Element) docNode;
						
						for (int j = 0; j < node.getChildCount(); j++) {
							if (element.getAttribute("index").equals(((Node) node.getChildAt(j)).index())) {
								if (element.getAttribute("active").equals("true")) {
									((Node) node.getChildAt(j)).setActive(true);
								} else {
									((Node) node.getChildAt(j)).setActive(false);
								}
								
								if (element.getAttribute("needsCode").equals("false")) {
									((Node) node.getChildAt(j)).needsCode(false);
								} else {
									((Node) node.getChildAt(j)).needsCode(true);
								}
								
								((Node) node.getChildAt(j)).setImportance(element.getAttribute("importance").charAt(0));
								
								((Node) node.getChildAt(j)).setLink(element.getAttribute("link"));
								
								NodeList testCaseList = loadDoc.getElementsByTagName("TestCase");
								
								for(int k = 0; k < testCaseList.getLength(); k++) {
									org.w3c.dom.Node docNode2 = testCaseList.item(k);
									
									if(docNode2.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
										Element element2 = (Element) docNode2;
										
										if (element2.getAttribute("index").equals(((Node) node.getChildAt(j)).index())) {
											for (int l = 0; l < Integer.parseInt(element.getAttribute("testCasesCount")); l++) {
												((Node) node.getChildAt(j)).addTestCase(element2.getAttribute("case" + (l + 1)));
											}
										}
									}
								}
								
								setNodeVariables(((Node) node.getChildAt(j)));
							}
						}
						
					}
				}
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		Node node = (Node) tree.tree.getLastSelectedPathComponent();
		if (node == null) {
			return;
		}	
		
		requirementLabel.setText("Requirement: " + node.index() + " " + node);
		
		if (node.isActive()) {
			activeCheckBox.setSelected(true);
		} else if (!node.isActive()) {
			activeCheckBox.setSelected(false);
		}

		if (node.needsCode()) {
			needsCodeCheckBox.setSelected(true);
		} else if (!node.needsCode()) {
			needsCodeCheckBox.setSelected(false);
		}

		if (node == tree.root) {
			previewCodePane.setText("Code");
			previewCodePane.setFont(new Font("Courier", Font.PLAIN, 16));
			
			previewTestCasesPane.setText("Test Cases");
			previewTestCasesPane.setFont(new Font("Courier", Font.PLAIN, 16));
		} else if (node.hasChildren()) {
			String codePaneText = "Sub requirements:";
			
			for (int i = 0; i < node.getChildCount(); i++) {
				codePaneText = codePaneText.concat("\n" + node.getChildAt(i));
			}
				
			previewCodePane.setText(codePaneText +  "\n\n" + node.requirementCode());
			previewCodePane.setFont(new Font("Courier", Font.PLAIN, 16));
			
			previewTestCasesPane.setText(node.requirementTestCases());
			previewTestCasesPane.setFont(new Font("Courier", Font.PLAIN, 16));
		} else {
			previewCodePane.setText(node.requirementCode());
			previewCodePane.setFont(new Font("Courier", Font.PLAIN, 16));
			
			previewTestCasesPane.setText(node.requirementTestCases());
			previewTestCasesPane.setFont(new Font("Courier", Font.PLAIN, 16));
		}
	}
	
	public void buildTreeNodesArrayList(Node n) {
		String bullet = "";
		
		for (int i = 0; i < n.getLevel(); i++) {
			bullet = bullet.concat("  ");
		}
		bullet = bullet.concat(n.index());
		
		treeNodes.add(bullet + n);

		for (int i = 0; i < n.getChildCount(); i++) {
			buildTreeNodesArrayList((Node) n.getChildAt(i));
		}
	}
	
	public void codeless(Node n) {
		if (!n.hasCode() && n.isLeaf() && n.isActive()) {
			codelessNodes.add(n.index() + n);
		}
		
		for (int i = 0; i < n.getChildCount(); i++) {
			codeless((Node) n.getChildAt(i));
		}
	}
	
	public void testcaseless(Node n) {
		if (!n.hasTestCases() && n.isLeaf() && n.isActive()) {
			testcaselessNodes.add(n.index() + n);
		}
		
		for (int i = 0; i < n.getChildCount(); i++) {
			testcaseless((Node) n.getChildAt(i));
		}
	}
	
	public void showReport(Tree tree) {
		try {
			PrintWriter writer = new PrintWriter("Report.txt", "UTF-8");
			
			treeNodes.clear();
			codelessNodes.clear();
			testcaselessNodes.clear();
			
			buildTreeNodesArrayList(tree.root);
			codeless(tree.root);
			testcaseless(tree.root);
			
			for (int i = 0; i < treeNodes.size(); i++) {
				writer.println(treeNodes.get(i));
			}
			
			writer.println();
			
			writer.println("Nodes without code");
			for (int i = 0; i < codelessNodes.size(); i++) {
				writer.println(" \u2022" + codelessNodes.get(i));
			}
			
			writer.println();
			
			writer.println("Nodes without test cases");
			for (int i = 0; i < testcaselessNodes.size(); i++) {
				writer.println(" \u2022" + testcaselessNodes.get(i));
			}
			
			writer.close();
			Desktop.getDesktop().open(new File("Report.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		String buttonString = e.getActionCommand();
		if (buttonString.equals("Load tree from xml file")) {
			JButton openButton = new JButton();
			JFileChooser chooser = new JFileChooser();
					
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			chooser.setFileFilter(new FileNameExtensionFilter(".xml", "xml"));
			
			if (chooser.showOpenDialog(openButton) == JFileChooser.APPROVE_OPTION) {
				file = new File(chooser.getSelectedFile().getAbsolutePath());
				
				if (file.exists()) {
					
					try {
						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = factory.newDocumentBuilder();
						Document loadDoc = builder.parse(file);
						loadDoc.getDocumentElement().normalize();
						NodeList fileTypesList = loadDoc.getElementsByTagName("FileTypes");
						NodeList reqCheq = loadDoc.getElementsByTagName("ReqCheqFile");
						
						org.w3c.dom.Node rcNode = reqCheq.item(0);
						
						if (rcNode == null) {
							return;
						}
						
						if (rcNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
							Element element = (Element) rcNode;
							
							if (!element.getAttribute("reqcheqfile").equals("true")) {
								return;
							}
						}

						addChildrenToRoot();
						
						for (int i = 0; i < fileTypesList.getLength(); i++) {
							org.w3c.dom.Node ftNode = fileTypesList.item(i);
							
							if (ftNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
								Element element = (Element) ftNode;
								
								fileTypes.add(element.getAttribute("fileType" + i));
								
								for (int j = 0; j < tree.root.getDepth(); j++) {
									tree.tree.setCellRenderer(new Renderer());
								}
							}
						}
					} catch (SAXException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (ParserConfigurationException e1) {
						e1.printStackTrace();
					}
				
					if (tree.root.hasChildren()) {
						addChildren((Node) tree.root.getChildAt(0));
					
						for (int i = 0; i < tree.tree.getRowCount(); i++) {
							tree.tree.expandRow(i);
						}
					
						setNodeVariables(tree.root);
					}
				} else {
					try {
						file.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				for (int i = 0; i < tree.root.getDepth(); i++) {
					tree.tree.setCellRenderer(new Renderer());
				}
			}
		} else if (buttonString.equals("Add node")) {
			JFrame nameFrame = new JFrame("New Node");
			JPanel namePanel = new JPanel();
			JLabel nameLabel = new JLabel("  Node name:");
			JLabel empty = new JLabel();
			JButton nameButton = new JButton("Enter");
			JTextField nameField = new JTextField();
			nameFrame.getRootPane().setDefaultButton(nameButton);
			
			namePanel.setLayout(new GridLayout(2, 2, 2, 2));
			namePanel.add(nameLabel);
			namePanel.add(nameField);
			namePanel.add(empty);
			namePanel.add(nameButton);
			
			nameButton.addActionListener(new ActionListener(){
				   public void actionPerformed(ActionEvent e){
				      String nodeName = nameField.getText();
				      tree.add(nodeName);
				      nameFrame.dispose();
						
				      unsavedChanges = true;
				   }
			});			
				
			nameFrame.setSize(400, 110);
			nameFrame.add(namePanel);
			nameFrame.setVisible(true);
			nameFrame.setLocationRelativeTo(null);
		} else if (buttonString.equals("Save")) {	      
			save();
		} else if (buttonString.equals("Set importance level")) {
			Node node = (Node) tree.tree.getLastSelectedPathComponent();

			if (node == null || node == tree.root) {
				return;
			}
			
			JFrame importanceFrame = new JFrame("Set importance level");
			JLabel importanceLabelOne = new JLabel(" The importance level is designed to rank how essential the code is ");
			importanceLabelOne.setFont(new Font("Tahoma", Font.PLAIN, 20));
			JLabel importanceLabelTwo = new JLabel(" to the project. The importance level is set to - by default.");
			importanceLabelTwo.setFont(new Font("Tahoma", Font.PLAIN, 20));
			
			JButton aButton = new JButton("A");
			JButton bButton = new JButton("B");
			JButton cButton = new JButton("C");
			JButton dButton = new JButton("D");
			JButton eButton = new JButton("E");
			JButton dashButton = new JButton("-");
			
			JPanel descriptionPanel = new JPanel();
			descriptionPanel.setLayout(new GridLayout (2, 0));
			descriptionPanel.add(importanceLabelOne);
			descriptionPanel.add(importanceLabelTwo);

			JPanel letterPanel = new JPanel();
			letterPanel.setLayout(new GridLayout(0, 6));
			letterPanel.add(aButton);
			letterPanel.add(bButton);
			letterPanel.add(cButton);
			letterPanel.add(dButton);
			letterPanel.add(eButton);
			letterPanel.add(dashButton);

			JPanel importancePanel = new JPanel();
			importancePanel.setLayout(new BorderLayout());
			importancePanel.add(descriptionPanel, BorderLayout.NORTH);
			importancePanel.add(letterPanel, BorderLayout.CENTER);
			
			importanceFrame.setSize(630, 135);
			importanceFrame.add(importancePanel);
			importanceFrame.setVisible(true);
			importanceFrame.setLocationRelativeTo(null);
			
			aButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.setImportance('A');
					tree.tree.setCellRenderer(new Renderer());
					importanceFrame.dispose();
					
					unsavedChanges = true;
				}
			});
			
			bButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.setImportance('B');
					tree.tree.setCellRenderer(new Renderer());
					importanceFrame.dispose();
					
					unsavedChanges = true;
				}
			});
			
			cButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.setImportance('C');
					tree.tree.setCellRenderer(new Renderer());
					importanceFrame.dispose();
					
					unsavedChanges = true;
				}
			});
			
			dButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.setImportance('D');
					tree.tree.setCellRenderer(new Renderer());
					importanceFrame.dispose();
					
					unsavedChanges = true;
				}
			});
			
			eButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.setImportance('E');
					tree.tree.setCellRenderer(new Renderer());
					importanceFrame.dispose();
					
					unsavedChanges = true;
				}
			});
			
			dashButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.setImportance('-');
					tree.tree.setCellRenderer(new Renderer());
					importanceFrame.dispose();
					
					unsavedChanges = true;
				}
			});
		} else if (buttonString.equals("Link to another requirement")) {
			Node node = (Node) tree.tree.getLastSelectedPathComponent();

			if (node == null || node == tree.root) {
				return;
			}
			
			JFrame linkFrame = new JFrame("Link to another requirement");
			JLabel linkLabel = new JLabel("  Node link index:");
			JLabel empty = new JLabel();
			JButton linkButton = new JButton("Enter");
			linkFrame.getRootPane().setDefaultButton(linkButton);
			JTextField linkField = new JTextField();

			JPanel linkPanel = new JPanel();
			linkPanel.setLayout(new BorderLayout());
			linkPanel.setLayout(new GridLayout(2, 2, 2, 2));
			linkPanel.add(linkLabel);
			linkPanel.add(linkField);
			linkPanel.add(empty);
			linkPanel.add(linkButton);	
			
			linkButton.addActionListener(new ActionListener() {
				   public void actionPerformed(ActionEvent e) {
				      String nodeLink = linkField.getText();
				      node.setLink(nodeLink);
				      
				      linkFrame.dispose();
						
				      unsavedChanges = true;
				   }
			});	
				
			linkFrame.setSize(400, 110);
			linkFrame.add(linkPanel);
			linkFrame.setVisible(true);
			linkFrame.setLocationRelativeTo(null);
		} else if (buttonString.equals("Change or add file types to be read")) {
			JFrame fileTypesFrame = new JFrame("Change File Types");
			JPanel fileTypesPanel = new JPanel();
			JLabel fileTypesLabel = new JLabel("  File extension:");
			JLabel empty = new JLabel();
			JButton fileTypesButton = new JButton("Enter");
			JTextField fileTypesField = new JTextField();
			fileTypesFrame.getRootPane().setDefaultButton(fileTypesButton);
			
			fileTypesPanel.setLayout(new GridLayout(2, 2, 2, 2));
			fileTypesPanel.add(fileTypesLabel);
			fileTypesPanel.add(fileTypesField);
			fileTypesPanel.add(empty);
			fileTypesPanel.add(fileTypesButton);
			
			fileTypesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fileTypes.add(fileTypesField.getText());
					
					fileTypesFrame.dispose();
						
					unsavedChanges = true;
					
					for (int i = 0; i < tree.root.getDepth(); i++) {
						tree.tree.setCellRenderer(new Renderer());
					}
				}
			});	
			
			fileTypesFrame.setSize(400, 110);
			fileTypesFrame.add(fileTypesPanel);
			fileTypesFrame.setVisible(true);
			fileTypesFrame.setLocationRelativeTo(null);
		} else if (buttonString.equals("Show report")) {
			showReport(tree);
		} else if (buttonString.equals("Create test case")) {
			Node node = (Node) tree.tree.getLastSelectedPathComponent();
			
			if (node == null || node == tree.root) {
				return;
			}
			
			JFrame testCaseFrame = new JFrame("New test case");
			JPanel testCasePanel = new JPanel();
			JLabel testCaseLabel = new JLabel("  Node test case:");
			JLabel empty = new JLabel();
			JButton testCaseButton = new JButton("Enter");
			JTextField testCaseField = new JTextField();
			testCaseFrame.getRootPane().setDefaultButton(testCaseButton);
			
			testCasePanel.setLayout(new GridLayout(2, 2, 2, 2));
			testCasePanel.add(testCaseLabel);
			testCasePanel.add(testCaseField);
			testCasePanel.add(empty);
			testCasePanel.add(testCaseButton);
			
			testCaseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String nodeTestCase = testCaseField.getText();
					node.addTestCase(nodeTestCase);
				      
					testCaseFrame.dispose();
						
					previewTestCasesPane.setText(node.requirementTestCases());
					previewTestCasesPane.setFont(new Font("Courier", Font.PLAIN, 16));
					
					unsavedChanges = true;
				}
			});	
				
			testCaseFrame.setSize(400, 110);
			testCaseFrame.add(testCasePanel);
			testCaseFrame.setVisible(true);
			testCaseFrame.setLocationRelativeTo(null);
		} else if (buttonString.equals("Show node attributes")) {
			Node node = (Node) tree.tree.getLastSelectedPathComponent();
			
			if (node == null || node == tree.root) {
				return;
			}
			
			//CURRENTLY MAKING IT POSSIBLE TO CHANGE NODE ATTRIBUTES FROM THIS FRAME
			
			JLabel indexLabel = new JLabel("  Index: ");
			indexLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
			JLabel index = new JLabel(node.index());
			index.setFont(new Font("Tahoma", Font.PLAIN, 16));
			
			JLabel contentLabel = new JLabel("  Content: ");
			contentLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
			JTextField contentField = new JTextField("" + node);
			contentField.setFont(new Font("Tahoma", Font.PLAIN, 16));
			
			JLabel activeLabel = new JLabel("  Active: ");
			activeLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
			JCheckBox activeCheckBoxSub = new JCheckBox();
			if (node.isActive()) {
				activeCheckBoxSub.setSelected(true);
			} else if (!node.isActive()) {
				activeCheckBoxSub.setSelected(false);
			}
			activeCheckBoxSub.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					boolean before = node.isActive();
					
					if (e.getStateChange() == ItemEvent.SELECTED) {
						node.setActive(true);
					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
						node.setActive(false);
					}

					boolean after = node.isActive();
					
					if (before != after) {
						unsavedChanges = true;
					}
				}
			});
			
			JLabel testCasesCountLabel = new JLabel("  Number of test cases: ");
			testCasesCountLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
			JLabel testCasesCount = new JLabel("" + node.getTestCases().size());
			testCasesCount.setFont(new Font("Tahoma", Font.PLAIN, 16));
			
			JLabel needsCodeLabel = new JLabel("  Needs code: ");
			needsCodeLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
			JCheckBox needsCodeCheckBoxSub = new JCheckBox();
			if (node.needsCode()) {
				needsCodeCheckBoxSub.setSelected(true);
			} else if (!node.needsCode()) {
				needsCodeCheckBoxSub.setSelected(false);
			}
			needsCodeCheckBoxSub.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					Node node = (Node) tree.tree.getLastSelectedPathComponent();
					
					boolean before = node.needsCode();
					
					if (e.getStateChange() == ItemEvent.SELECTED) {
						node.needsCode(true);
					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
						node.needsCode(false);
					}
					
					boolean after = node.needsCode();
					
					if (before != after) {
						unsavedChanges = true;
					}
				}
			});
			
			JLabel linkLabel = new JLabel("  Link index: ");
			linkLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
			JLabel link = new JLabel("" + node.getLinkIndex());
			link.setFont(new Font("Tahoma", Font.PLAIN, 16));
			
			JLabel importanceLabel = new JLabel("  Importance: ");
			importanceLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
			JLabel importance = new JLabel("" + node.getImportance());
			importance.setFont(new Font("Tahoma", Font.PLAIN, 16));
			
			JFrame nodeAttributesFrame = new JFrame("Node Attributes");
			JPanel nodeAttributesPanel = new JPanel();
			JButton saveChangesButton = new JButton("  Save changes");
			JButton closeButton = new JButton("  Close");
			
			nodeAttributesPanel.setLayout(new GridLayout(8, 2));
			nodeAttributesPanel.add(indexLabel);
			nodeAttributesPanel.add(index);
			nodeAttributesPanel.add(contentLabel);
			nodeAttributesPanel.add(contentField);
			nodeAttributesPanel.add(activeLabel);
			nodeAttributesPanel.add(activeCheckBoxSub);
			nodeAttributesPanel.add(testCasesCountLabel);
			nodeAttributesPanel.add(testCasesCount);
			nodeAttributesPanel.add(needsCodeLabel);
			nodeAttributesPanel.add(needsCodeCheckBoxSub);
			nodeAttributesPanel.add(linkLabel);
			nodeAttributesPanel.add(link);
			nodeAttributesPanel.add(importanceLabel);
			nodeAttributesPanel.add(importance);
			nodeAttributesPanel.add(saveChangesButton);
			nodeAttributesPanel.add(closeButton);
			
			nodeAttributesFrame.setSize(400, 250);
			nodeAttributesFrame.add(nodeAttributesPanel);
			nodeAttributesFrame.setVisible(true);
			nodeAttributesFrame.setLocationRelativeTo(null);
			
			saveChangesButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String newName = contentField.getText();
					node.setUserObject(newName);
					tree.tree.setCellRenderer(new Renderer());
					
					if (node.isActive()) {
						activeCheckBox.setSelected(true);
					} else if (!node.isActive()) {
						activeCheckBox.setSelected(false);
					}
					
					if (node.needsCode()) {
						needsCodeCheckBox.setSelected(true);
					} else if (!node.needsCode()) {
						needsCodeCheckBox.setSelected(false);
					}
					nodeAttributesFrame.dispose();
				}
			});	
			
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					nodeAttributesFrame.dispose();
				}
			});	
		}
	}
	
	public void run() {
		JFrame frame = new JFrame("Requirements");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (unsavedChanges) {
					JFrame closeFrame = new JFrame("Requirements");
					
					JPanel closePanel = new JPanel();
					JLabel closeLabel = new JLabel("Would you like to save your changes?", JLabel.CENTER);
					closeLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
					
					JPanel closeButtonPanel = new JPanel();
					JButton saveButton = new JButton("Save");
					saveButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
					JButton dontSaveButton = new JButton("Don't save");
					dontSaveButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
					JButton cancelButton = new JButton("Cancel");
					cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
					JLabel empty1 = new JLabel("   ");
					JLabel empty2 = new JLabel("   ");
					
					closeButtonPanel.setLayout(new FlowLayout());
					closeButtonPanel.add(saveButton);
					closeButtonPanel.add(empty1);
					closeButtonPanel.add(dontSaveButton);
					closeButtonPanel.add(empty2);
					closeButtonPanel.add(cancelButton);
					
					closePanel.setLayout(new BorderLayout());
					closePanel.add(closeLabel, BorderLayout.NORTH);
					closePanel.add(closeButtonPanel, BorderLayout.CENTER);
					
					closeFrame.setSize(400, 120);
					closeFrame.add(closePanel);
					closeFrame.setVisible(true);
					closeFrame.setLocationRelativeTo(null);
					
					saveButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e1) {
							save();
							
							closeFrame.dispose();
							e.getWindow().dispose();
						}
					});
					
					dontSaveButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e1) {
							closeFrame.dispose();
							e.getWindow().dispose();
						}
					});
					
					cancelButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e1) {
							closeFrame.dispose();
						}
					});
				} else {
					e.getWindow().dispose();
				}
			}
		});
		
		frame.setContentPane(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setIconImage(new ImageIcon("src/icons/imageIcon.png").getImage());
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	public void save() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			xmlRoot = doc.createElement("Root");
			doc.appendChild(xmlRoot);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(doc);
			if (file != null) {
				Element element1 = doc.createElement("ReqCheqFile");
				element1.setAttribute("reqcheqfile", "true");
				element1.setAttribute("version", "1.0");
				xmlRoot.appendChild(element1);
			      
				saveTree(xmlRoot, tree.root);
			      
				Element element3 = doc.createElement("FileTypes");
				for (int i = 0; i < fileTypes.size(); i++) {
					element3.setAttribute("fileType" + i, fileTypes.get(i));
				}
				xmlRoot.appendChild(element3);
			      
				StreamResult result = new StreamResult(file);
				transformer.transform(source, result);
			} else {
				JFrame fileFrame = new JFrame("File name");
				JLabel fileLabel = new JLabel("  File name:");
				JLabel empty = new JLabel();
				JButton fileButton = new JButton("Enter");
				fileFrame.getRootPane().setDefaultButton(fileButton);
				JTextField fileField = new JTextField();

				JPanel filePanel = new JPanel();
				filePanel.setLayout(new BorderLayout());
				filePanel.setLayout(new GridLayout(2, 2, 2, 2));
				filePanel.add(fileLabel);
				filePanel.add(fileField);
				filePanel.add(empty);
				filePanel.add(fileButton);	
				
				fileButton.addActionListener(new ActionListener() {
					   public void actionPerformed(ActionEvent e) {
					      String fileName = fileField.getText();
					      
					      file = new File(fileName + ".xml");
					      
					      try {
					    	  file.createNewFile();
					      } catch (IOException e1) {
					    	  e1.printStackTrace();
					      }
					      
					      Element element1 = doc.createElement("ReqCheqFile");
					      element1.setAttribute("reqcheqfile", "true");
					      xmlRoot.appendChild(element1);
					      
					      saveTree(xmlRoot, tree.root);
					      
					      Element element3 = doc.createElement("FileTypes");
					      for (int i = 0; i < fileTypes.size(); i++) {
					    	  element3.setAttribute("fileType" + i, fileTypes.get(i));
					      }
					      
					      if (element3.hasAttribute("fileType0")) {
					      	xmlRoot.appendChild(element3);
					   	  }
					      
					      try {
						      StreamResult result = new StreamResult(file);
					    	  transformer.transform(source, result);
					      } catch (TransformerException e1) {
					    	  e1.printStackTrace();
					      }
					      
					      fileFrame.dispose();
					   }
				});	
					
				fileFrame.setSize(400, 110);
				fileFrame.add(filePanel);
				fileFrame.setVisible(true);
				fileFrame.setLocationRelativeTo(null);
			}
		} catch (TransformerException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		
		unsavedChanges = false;
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Main());
	}
}
