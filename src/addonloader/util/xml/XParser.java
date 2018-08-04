package addonloader.util.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Simple XML parser, written at aims for simplicity,
 * minimalism and customizablity and performance.
 * The parsing is done by reading directly from stream, so the
 * performance <b>heavily</b> depends on the cost of reading
 * operation. For example, parsing from RAM is faster than from HTTP URL.
 * <p>Speed: Around <font color='lime'>100ch/ms</font> (from RAM)</p>
 * @author Enginecrafter77
 * @version 1.5
 */
public class XParser implements Callable<XElement> {
	
	/** A reader used to read input data used to parse the XML. */
	private final Reader reader;
	/* An element on which the element tree is built on, usually called doucment root. */
	private final XElement document;
	
	/**
	 * Constructs XParser, working on top of newly created root element, used as document reference.
	 * @param reader The reader used to parse input.
	 */
	public XParser(Reader reader)
	{
		this.reader = reader;
		this.document = new XElement("root");
	}
	
	/**
	 * Constructs XParser, working on top of the element specified by second parameter.
	 * For example, you already have XML tree built, but you have some stub node here,
	 * and you wanna fill it with contents of some other XML. So you can construct XParser
	 * with that node as second argument, and it will behave as it was the document root,
	 * filling it with tree of the parsed XML input.
	 * @param reader The reader used to parse input.
	 * @param inherit The element node on which the tree will be built.
	 */
	public XParser(Reader reader, XElement inherit)
	{
		this.reader = reader;
		this.document = inherit;
	}
	
	public final XElement call() throws IOException
	{
		parseXML(this.document, this.reader);
		return this.document;
	}
	
	/**
	 * Main function of the parser. Builds an xml tree on root element.
	 * @param parent A parent element to add childs to.
	 * @param inBuffer A buffer that will the function read from.
	 * @throws IOException
	 */
	private static void parseXML(XElement parent, Reader input) throws IOException
	{
		StringBuilder varparb = new StringBuilder();
		StringBuilder nameb = new StringBuilder();
		XElement last_parent = parent;
		int parsing = 0; //NONE = 0, NAME = 1, PARAMETERS = 2
		int suspect = 0; //NONE = 0, SINGLE_END = 1, BLOCK_END = 2
		char ommit_quoted = 0;
		
		int point = input.read();
		char ch = (char)point;
		while(point > -1) //Iterate while pointer is not null.
		{
			if(ch == '<' && ommit_quoted == 0) parsing = 1; //The name is first in element declaration, so start parsing name.
			else if(ch == '>' && ommit_quoted == 0) //If we aren't parsing string, end current block.
			{
				if(suspect == 2) //If we expect block end, then make it happen.
				{
					if(!nameb.toString().equals(last_parent.name)) throw new IOException(String.format("Unmatched closing block \"%s\" to \"%s\"", nameb.toString(), last_parent.name));
					last_parent = last_parent.parent;
				}
				else //Else create new element.
				{
					XElement elm = new XElement(last_parent, nameb.toString());
					if(parsing == 2) parseValues(elm, varparb.toString());
					last_parent.children.add(elm);
					if(suspect == 0) last_parent = elm;
				}
				//Unset all variables used to parse this entity.
				varparb.setLength(0);
				nameb.setLength(0);
				suspect = 0;
				parsing = 0;
			}
			else if(ch == '/' && ommit_quoted == 0) suspect = 1; //If we find stray '/', we suspect the element or node is gonna end.
			else if(parsing == 1) //If this is not the beginning of declaration, but rather we are already parsing name.
			{
				if(suspect == 1) suspect++; //Suspect 1 means we already encountered '/', so this means this is closing node.
				if(Character.isWhitespace(ch)) parsing++; //If the character is whitespace, end parsing name.
				else nameb.append(ch); //Or else, just extend name.
			}
			else if(parsing == 2) //We are already parsing the variables here.
			{				
				if((ch == '\"' || ch == '\'') && ommit_quoted == 0) ommit_quoted = ch; //If we encounter some quotes (or apostrophe), begin to ommit them.
				else if(ch == ommit_quoted) ommit_quoted = 0; //And it ends here.
				varparb.append(ch); //Anyway add 'em to parameters.
			}
			else if(!Character.isWhitespace(ch)) last_parent.content.append(ch); //If character is not whitespace, add it to content.
			point = input.read();
			ch = (char)point;
		}
		varparb = null;
		nameb = null;
		System.gc();
	}
	
	/**
	 * Basic key-value pair parser with empty key notation support.
	 * @param node - The node being manipulated with.
	 * @param bundle - The mesh of strings found after node name definition to the end of the node.
	 */
	private static void parseValues(XElement node, String bundle)
	{
		StringBuilder name = new StringBuilder();
		StringBuilder value = new StringBuilder();
		boolean parsing_name = true; //False = parsing value
		boolean ignore_whitespace = false; //If the value is surrounded with quotes.
		
		for(char current : bundle.toCharArray())
		{
			if(!ignore_whitespace && Character.isWhitespace(current) && name.length() > 0) //The key-value definition has ended, priority over parsing_name, because parsing_name uses nowhitespace check.
			{
				node.fields.put(name.toString(), value.toString()); //Add the value to the node
				name.setLength(0); //Cleanup the environment
				value.setLength(0);
				parsing_name = true;
			}
			else if(parsing_name)
			{
				if(current == '=') parsing_name = false; //If we encounter '=', we suppose the value follows.
				else if(!Character.isWhitespace(current)) name.append(current); //James, add this lil' one to the "name" pile! Certainly, sir!
			}
			else //We are definitely parsing value now, and... no, we arent't terminating the key-value pair, sir.
			{
				if(current == '\'' || current == '\"') ignore_whitespace = !ignore_whitespace; //The section is being quoted.
				else value.append(current); //Or when everything else is false, simply append current character to the value field.
			}
		}
		
		if(name.length() > 0) node.fields.put(name.toString(), value.toString()); //We are not done playing with the values.
	}
	
	/**
	 * A shortcut to using XParser. It is recommended to use the default instatination method,
	 * as this way is inflexibile and used only for reference.
	 * @param file A file containing the XML.
	 * @return The XML document root.
	 * @throws IOException If there was exception parsing the file.
	 */
	public static final XElement call(File file) throws IOException
	{
		return new XParser(new FileReader(file)).call();
	}
	
	/**
	 * A shortcut to using XParser. It is recommended to use the default instatination method,
	 * as this way is inflexibile and used only for reference.
	 * @param file An URL containing the XML.
	 * @return The XML document root.
	 * @throws IOException If there was exception parsing the URL contents.
	 */
	public static final XElement call(URL resloc) throws IOException
	{
		return new XParser(new InputStreamReader(resloc.openStream())).call();
	}
}