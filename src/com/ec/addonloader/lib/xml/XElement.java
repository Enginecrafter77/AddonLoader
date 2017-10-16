package com.ec.addonloader.lib.xml;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import com.ec.addonloader.lib.DoubleObject;
/**
 * This is a class that provides access for simple,
 * modificated xml tree, tags and their variables.
 * @author Enginecrafter77.
 * @version 1.3.4721b
 */
public class XElement {
    
    /** Element's parent. Null for root element. */
    public final XElement parent;
    /** A list of element childs. */
    public final ArrayList<XElement> child;
    /** A list of element variables */
    private final ArrayList<Field> fields;
    /** An element ID */
    public final String name;
	protected final StringBuilder content;
	
    /**
     * A common constructor used to initialize the element.
     * @param parent A parent element.
     * @param name The name for the element. Example: <br> &lt input &gt will be input.
     */
    public XElement(XElement parent, String name)
    {
        this.parent = parent;
        this.child = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.name = name;
		this.content = new StringBuilder();
    }
    
    /**
     * Root element constructor
     */
    protected XElement()
    {
        this(null, "root");
    }
    
    /**
     * Binds the child to it's parent.
     * Protected =&gt used only by the parser.
     * @return this
     */
    protected XElement parent()
    {
        if(parent != null)
        {
            parent.child.add(this);
        }
        return this;
    }
    
    /**
     * Function called by parser to parse values to fields.
     * Protected =&gt used only by the parser.
     * @param s - A tag content.
     */
    protected void parseValues(String s)
    {
        DoubleObject<String, ArrayList<String>> d = XParser.removeSequences(s, '\"');
        String[] a = d.getFirst().split(" ");
        
        for(int i = 0; i < a.length; i++)
        {
            if(i < d.getSecond().size() && !XParser.isStringEmpty(a[i]))
            {
                fields.add(new Field(a[i].substring(0, a[i].lastIndexOf("=")), d.getSecond().get(i)));
            }
        }
    }
	
	public String getContent()
	{
		return this.content.toString();
	}
    
	/**
     * Prints tree in print stream.
     * @param tab A tabulating character.
     * @param p PrintStream to print to.
     */
    public void printTree(String tab, PrintStream p)
    {
    	p.print(tab);
    	p.print(this.name);
    	p.print("[" + this.fields + ']');
    	if(this.hasContent())
    	{
    		p.print("{" + this.content.toString() + '}');
    	}
    	p.println();
    	if(this.hasChild())
    	{
	        for(int i = 0; i < this.child.size(); i++)
	        {
	        	printTree(tab + "  ", p);
	        }
    	}
    }
    
    public boolean hasContent()
    {
    	return this.content.length() > 0;
    }
    
    public boolean equalNames(String s)
    {
        return this.name.equals(s);
    }
    
    /**
     * @return If element has children.
     */
    public boolean hasChild()
    {
    	return this.child.size() > 0;
    }
    
    public XElement[] getChildren()
    {
    	return this.child.toArray(new XElement[0]);
    }
    
    /**
     * Gets a element's child by it's ID.
     * @param id
     * @return 
     * @throws java.io.IOException 
     */
    public XElement getChild(String id) throws IOException
    {
        for(Iterator<XElement> it = child.iterator(); it.hasNext();)
        {
            XElement e = it.next();            
            if(e.name.equals(id))
            {
                return e;
            }
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
        String[] cl = path.split("/");
        XElement res = this;
        for(String c : cl)
        {
            res = res.getChild(c);
        }
        return res;
    }
    
    /**
     * A workaround to simpify <code>getByPath()</code> function.
     * Must be called on root element. (name == root)
     * @param path
     * @return A end-path element.
     * @throws java.io.IOException
     */
    public XElement getFromRoot(String path) throws IOException
    {
        if(this.name.equals("root"))
        {
            return this.getByPath("xml/" + path);
        }
        else
        {
            throw new IOException("Function not called from root");
        }
    }
    
    /**
     * A debug function used to print simple xml hierarchy to console.
     */
    public void printTree()
    {
        printTree("|", System.out);
    }
    
    /**
     * Gets a value by field's ID
     * @param id Field's ID
     * @return Field content.
     * @throws IOException If the field wasn't found.
     */
    public String getValue(String id) throws IOException
    {
        if(fields.contains(new Field(id)))
        {
        	return fields.get(fields.lastIndexOf(new Field(id))).value;
        }
        else
        {
        	throw new IOException("Field " + id + " not found.");
        }
    }
    
    @Override
    public String toString()
    {
    	return this.name + (this.parent != null ? "(" + this.parent.name + ")" : "") + this.fields + "{" + this.getContent() + "}";
    }
    
    public static class Field
    {
        public final String name;
        public final String value;
        
        public Field(String name, String value)
        {
            this.name = name;
            this.value = value;
        }
        
        protected Field(String name)
        {
            this.name = name;
            this.value = null;
        }

        @Override
        public String toString()
        {
            return name + ":" + value;
        }
        
        @Override
        public boolean equals(Object f)
        {
            boolean res = false;
            if(f instanceof Field)
            {
                if(((Field)f).name.equals(this.name))
                {
                    res = true;
                }
            }
            return res;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 47 * hash + Objects.hashCode(this.name);
            return hash;
        }
    }
}
