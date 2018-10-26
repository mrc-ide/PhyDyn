package phydyn.analysis;

import java.io.FileWriter;
import java.io.IOException;

public class XMLFileWriter {
	private FileWriter writer;
	private static String[] tabs = {"", "  ", "    ", "      ","        ","          " };
	private int tab;
	
	public XMLFileWriter(String fileName) throws IOException {
		writer = new FileWriter(fileName);
		tab = 0;
	}
	
	public void EOL() throws IOException {
		writer.append("\n");
	}
	
	public void tab() { tab++; }
	
	public void untab() { 
		tab--;
		if (tab < 0) tab=0;
	}
	
	public XMLFileWriter tabAppend(String s) throws IOException {
		writer.append(tabs[Integer.min(tab, tabs.length-1)]+s);
		return this;
	}
	
	public XMLFileWriter append(String s) throws IOException {
		writer.append(s);
		return this;
	}
	
	public XMLFileWriter comment(String s) throws IOException {
		writer.append("<!-- "+s+" -->");
		return this;
	}
	
	public void close() throws IOException {
		writer.close();
	}

}
