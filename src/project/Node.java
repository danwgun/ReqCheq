package project;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

public class Node extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 1L;

	private boolean active;
	private char importance;
	private String link;
	private ArrayList<String> testCases;
	private boolean needsCode;
	private Color color;

	public Node() {
		super();
		active = true;
		importance = '-';
		link = "";
		testCases = new ArrayList<String>();
		needsCode = true;
		color = new Color(1.0f, 1.0f, 1.0f);
	}
	
	public Node(Object userObject) {
		super(userObject);
		active = true;
		importance = '-';
		link = "";
		testCases = new ArrayList<String>();
		needsCode = true;
		color = new Color(1.0f, 1.0f, 1.0f);
	}
	
	public Node(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
		active = true;
		importance = '-';
		link = "";
		testCases = new ArrayList<String>();
		needsCode = true;
		color = new Color(1.0f, 1.0f, 1.0f);
	}
	
	public String index() {
		String returnedString;
		
		if (this.getParent() == this.getRoot()) {
			returnedString = ((int) this.getParent().getIndex(this) + 1) + ".";
		} else if (this.getLevel() == 0) {
			return "";
		} else {
			returnedString = ((Node) this.getParent()).index() + ((int) this.getParent().getIndex(this) + 1) + ".";
		}
		
		return returnedString;
	}
	
	public void setActive(boolean b) {
		active = b;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setImportance(char c) {
		importance = c;
	}
	
	public char getImportance() {
		return importance;
	}
	
	public void setLink(String s) {
		link = s;
	}
	
	public String getLinkIndex() {
		return link;
	}
	
	public void addTestCase(String s) {
		testCases.add(s);
	}
	
	public String getTestCaseAtIndex(int i) {
		return testCases.get(i);
	}
	
	public ArrayList<String> getTestCases() {
		return testCases;
	}
	
	public boolean needsCode() {
		return needsCode;
	}
	
	public void needsCode(boolean b) {
		needsCode = b;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color c) {
		color = c;
	}

	public boolean hasChildren() {
		if (this.getChildCount() > 0) {
			return true;
		}
		return false;
	}
	
	public boolean hasCode() {
		if (this.requirementCode().length() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasTestCases() {
		if (this.getTestCases().size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
//	public String getAllSubRequirements() {
//		for 
//	}
	
	public void setFont(Node node) {
		if (this.hasChildren()) {
			for (int i = 0; i < this.getChildCount(); i++) {
				
			}
		}
	}
	
	public String requirementCode() {
		String code = "";
	
		String startSearch = "//" + "RB" + this.index();
		String endSearch = "//" + "RE" + this.index();
		
		String startSearch2 = null;
		String endSearch2 = null;
		
		if (this.getLinkIndex().length() > 0) {
			startSearch2 = "//" + "RB" + this.getLinkIndex();
			endSearch2 = "//" + "RE" + this.getLinkIndex();
		}
		
		
		File folder = new File(System.getProperty("user.dir"));
		File[] files = folder.listFiles();

		for (int i = 0; i < files.length; i++) {
			for (int j = 0; j < Main.fileTypes.size(); j++) {
				if (files[i].isFile() && (files[i].getName().endsWith(Main.fileTypes.get(j)))) {
					try (BufferedReader br = new BufferedReader(new FileReader(files[i]))) {
					    String line;
					    
					    boolean copy = false;
					    boolean end = false;
					    boolean start = false;
					    
					    int counter = 0;
					    int lineNumber = 1;
					    
					    while ((line = br.readLine()) != null) {
					    	String lineCopy = line;
					    	lineCopy = lineCopy.replace(" ", "");
					    	lineCopy = lineCopy.replace("*", "/");
					    	lineCopy = lineCopy.concat("    ");
					    	
					    	if (counter % 2 == 0 && lineCopy.indexOf(startSearch) != -1 && 
					    		!(lineCopy.charAt(lineCopy.indexOf(startSearch) + startSearch.length()) + "").matches("\\d+")) {
					    		start = true;
					    		copy = true;
					    		counter++;
					    	}
					    
							if (counter % 2 == 1 && lineCopy.indexOf(endSearch) != -1) {
					    		end = true;
					    		copy = false;
					    		counter++;
					    	}
						    
						    if (start) {
						    	code = code.concat("File: " + files[i] + "\n");
						    	start = false;
						    }
						    
					    	if (copy) {
					    		code = code.concat((lineNumber + "           ").substring(0, 4) + " " + line + "\n");
					    	}
					    	
					    	if (end) {
					    		code = code.concat((lineNumber + "           ").substring(0, 4) + " " + line + "\n\n");
					    		end = false;
					    	}
					    	
					    	lineNumber++;
					    }
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					if (startSearch2 != null) {
						try (BufferedReader br = new BufferedReader(new FileReader(files[i]))) {
						    String line;
						    
						    boolean copy = false;
						    boolean end = false;
						    boolean start = false;
						    
						    int counter = 0;
						    int lineNumber = 1;
						    
						    while ((line = br.readLine()) != null) {
						    	String lineCopy = line;
						    	lineCopy = lineCopy.replace(" ", "");
						    	lineCopy = lineCopy.replace("*", "/");
						    	lineCopy = lineCopy.concat("    ");
						    	
						    	if (counter % 2 == 0 && lineCopy.indexOf(startSearch2) != -1 && 
						    		!(lineCopy.charAt(lineCopy.indexOf(startSearch2) + startSearch2.length()) + "").matches("\\d+")) {
						    		start = true;
						    		copy = true;
						    		counter++;
						    	}
						    
								if (counter % 2 == 1 && lineCopy.indexOf(endSearch2) != -1) {
						    		end = true;
						    		copy = false;
						    		counter++;
						    	}
							    
							    if (start) {
							    	code = code.concat("File: " + files[i] + "\n");
							    	start = false;
							    }
						    	
							    if (copy) {
						    		code = code.concat(("    " + lineNumber).substring(4) + " " + line + "\n");
						    	}
						    	
						    	if (end) {
						    		code = code.concat(("    " + lineNumber).substring(4) + " " + line + "\n\n");
						    		end = false;
						    	}
						    	
						    	lineNumber++;
						    }
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	
		
		return code;
	}
	
	public String requirementTestCases() {
		String testCases = "";
		
		for (int i = 0; i < this.getTestCases().size(); i++) {
			testCases = testCases.concat(this.getTestCaseAtIndex(i) + "\n");
		}
		
		return testCases;
	}
		
}