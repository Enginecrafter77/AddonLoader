package addonloader.util.xml;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * XElement provides relatively lightweight implementation of Node type
 * object for {@link XParser} XML parser.
 * @author Enginecrafter77.
 * @version 1.5
 */
public class XElement {
	
	/** An element ID */
	public final String name;
	/** Element's parent. Null for root element. */
	public final XElement parent;
	/** A list of element childs. */
	public final LinkedList<XElement> children;
	/** A list of element variables */
	protected final HashMap<String, String> fields;
	/** The textual content of the node. */
	protected final StringBuilder content;
	
	/**
	 * A common constructor used to initialize the element.
	 * @param parent A parent element.
	 * @param name The name for the element. Example: <br> &lt input &gt will be input.
	 */
	public XElement(XElement parent, String name)
	{
		this.name = name;
		this.parent = parent;
		this.fields = new HashMap<>();
		this.content = new StringBuilder();
		this.children = new LinkedList<>();
	}
	
	/**
	 * Extraordinary constructor used only internally by the {@link XParser}
	 * do create document root reference node.
	 * @param name The desired name of root element.
	 */
	protected XElement(String name)
	{
		this(null, name);
	}
	
	/**
	 * Prints tree in print stream.
	 * @param format The tree item format.
	 * @param out PrintStream to print tree to.
	 */
	public void printTree(PrintStream out)
	{
		printTree(0, out);
	}
	
	/** Private recursive function to print element tree. */
	private void printTree(int indent, PrintStream out)
	{
		for(int i = 0; i < indent; i++) out.print('\t');
		if(indent > 0) out.print("|--> ");
		out.print(this.toString());
		out.println();
		for(int i = 0; i < this.children.size(); i++)
		{
			this.children.get(i).printTree(indent + 1, out);
		}
	}
	
	/**
	 * This function is used to match this element's name against others.
	 * @param obj The other XElement to match against.
	 * @return True if their names are equal, false otherwise.
	 */
	public boolean matches(XElement obj)
	{
		return this.name.equals(obj.name);
	}

	/** @return If element has content. */
	public boolean hasContent()
	{
		return this.content.length() > 0;
	}
	
	/** @return If element has children. */
	public boolean hasChildren()
	{
		return this.children.size() > 0;
	}
	
	/**
	 * Element content is plain text found between the start tag and end tag of element.
	 * The content does not include whitespaces by default.
	 * @return The element's content.
	 */
	public String getContent()
	{
		return this.content.toString();
	}
	
	/**
	 * Returns wether the element has some parameter or not.
	 * @param id The name of the parameter.
	 * @return True if the parameter was specified in the element.
	 */
	public boolean hasValue(String id)
	{
		return this.fields.containsKey(id);
	}
	
	/**
	 * Gets a element's child by it's ID.
	 * @param id
	 * @return 
	 * @throws java.io.IOException 
	 */
	public XElement getChild(String id) throws IOException
	{
		Iterator<XElement> it = children.iterator();
		XElement current = null;
		while(it.hasNext())
		{
			current = it.next();
			if(current.name.equals(id)) return current;
		}
		throw new IOException("Element's child \"" + id + "\" not found.");
	}
	
	/**
	 * Used as a replacement for continously calling <code>getChild()</code> <br>
	 * Exaple: <br>
	 * [root_element].getChild("foo").getChild("something"); => <br>
	 * [root_element].getByPath("foo/something");
	 * @param path
	 * @return A end-path element.
	 * @throws java.io.IOException
	 */
	public XElement getByPath(String path) throws IOException
	{	
		XElement current = this;
		for(String name : path.split("/")) current = current.getChild(name);
		return current;
	}
	
	/**
	 * Gets a value by field's ID
	 * @param id Field's ID
	 * @return Field content.
	 * @throws NoSuchFieldException If the field wasn't found.
	 */
	public String getValue(String id) throws NoSuchFieldException
	{
		String value = fields.get(id);
		if(value == null) throw new NoSuchFieldException("Field " + id + " doesn't exist.");
		return value;
	}
	
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append(this.name);
		if(this.fields.size() > 0) text.append(this.fields.toString());
		if(this.content.length() > 0) text.append("[" + this.content.toString() + "]");
		return text.toString();
	}
}
