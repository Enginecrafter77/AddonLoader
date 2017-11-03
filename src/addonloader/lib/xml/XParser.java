package addonloader.lib.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import addonloader.lib.DoubleObject;
import addonloader.lib.ResourceLocation;
/**
 * A class that can parse custom xml-trees.
 * Note that when it encounters some errors
 * in parsing customly typed xml file, it won't
 * produce exception, but will act strangely.
 * Speed: 20ms/file.
 * @author Enginecrafter77
 * @version 1.3.4721b
 */
public class XParser {
    
    public static final boolean debug = false;
    public final XElement rootElement;
	
    public static final XElement call(File f) throws IOException
    {
    	XParser p = new XParser(new FileReader(f));
        return p.rootElement;
    }
    
    public static final XElement call(ResourceLocation rl) throws IOException
    {
    	XParser p = new XParser(new InputStreamReader(rl.address().openStream()));
        return p.rootElement;
    }
    
    public static final void recursiveNL(XElement el, char nlname)
    {
    	if(el.hasContent())
    	{
    		for(int i = 0; i < el.content.length(); i++)
    		{
    			if(el.content.charAt(i) == nlname)
    			{
    				el.content.setCharAt(i, '\n');
    			}
    		}
    	}
    	
    	if(el.hasChild())
    	{
    		for(int i = 0; i < el.child.size(); i++)
    		{
    			recursiveNL(el.child.get(i), nlname);
    		}
    	}
    }
    
    public XParser(Reader rd) throws IOException
    {
        rootElement = new XElement();
        parseXML(rootElement, this.readFile(rd));
    }
    
    /**
     * Reads a file and outputs it as ArrayList
     * @return ArrayList
     * @throws IOException
     */
    private ArrayList<String> readFile(Reader rd) throws IOException
    {
        BufferedReader b = new BufferedReader(rd);
        ArrayList<String> file = new ArrayList<>();
        String line;
        while((line = b.readLine()) != null)
        {
            if(line != null)
            {
				if(line.startsWith("<?xml") && line.endsWith("?>"))
				{
					continue;
				}
				
                if(!isStringEmpty(line) && !line.startsWith("<!--"))
                {
                    file.add(line.trim());
                }
            }
        }
        b.close();
        return file;
    }
    
    /**
     * Main function of the parser. Builds an xml tree on root element.
     * @param parent A parent element to add childs to.
     * @param inBuffer A buffer that will the function read from.
     * @throws IOException
     */
    private void parseXML(XElement parent, ArrayList<String> inBuffer) throws IOException
    {
        StringBuilder contentb = new StringBuilder();
        StringBuilder nameb = new StringBuilder();
        XElement lastParent = parent;
        boolean parsingName = false;
        boolean parsingContent = false;
        
        for(int row = 0; row < inBuffer.size(); row++)
        {
            char[] chars = inBuffer.get(row).toCharArray();
            for(int c = 0; c < chars.length; c++)
            {
                char ch = chars[c];
                if(ch == '<')
                {
                    parsingName = true;
                }
                else if(parsingName)
                {
                    if(ch == ' ')
                    {
                        parsingName = false;
                        parsingContent = true;
                    }
                    else if(ch == '>')
                    {
                        parsingName = false;
                        XElement e = new XElement(lastParent, nameb.toString()).parent();
                        lastParent = e;
                        contentb.setLength(0);
                        nameb.setLength(0);
                    }
                    else if(ch == '/' && chars[c + 1] == '>')
                    {
                        parsingName = false;
                        new XElement(lastParent, nameb.toString()).parent();
                        contentb.setLength(0);
                        nameb.setLength(0);
						c++;
                    }
                    else if(ch == '/' && chars[c + 1] != '>')
                    {
                        parsingName = false;
                        StringBuilder temp = new StringBuilder();
						for(c++; true; c++)
                        {
                            if(c < chars.length)
                            {
                                if(chars[c] == '>')
                                {
                                    break;
                                }
                                else
                                {
                                    temp.append(chars[c]);
                                }
                            }
                        }
                        if(lastParent.equalNames(temp.toString()))
                        {
                            lastParent = lastParent.parent;
                        }
                    }
                    else
                    {
                        nameb.append(ch);
                    }
                }
                else if(parsingContent)
                {                    
                    if(ch == '>')
                    {
                        parsingContent = false;
                        XElement e = new XElement(lastParent, nameb.toString());
                        e.parseValues(contentb.toString());
                        e.parent();
                        lastParent = e;
                        contentb.setLength(0);
                        nameb.setLength(0);
                    }
                    else if(ch == '/' && chars[c + 1] == '>')
                    {
                        parsingContent = false;
                        XElement e = new XElement(lastParent, nameb.toString());
                        e.parseValues(contentb.toString());
                        e.parent();
                        contentb.setLength(0);
                        nameb.setLength(0);
						c++;
                    }
                    else
                    {
                        contentb.append(ch);
                    }
                }
				else
				{
					lastParent.content.append(ch);
				}
            }
            
            if(parsingName)
            {
                parsingName = false;
                parsingContent = true;
            }
        }
        contentb = null;
        nameb = null;
        System.gc();
    }
    
    /**
     * Ommits a sequence embedded in <code>seq</code> from string <code>s</code>
     * @param s String to ommit sequence from.
     * @param seq An embedding string.
     * @return DoubleObject containing 1.(Output clean string) 2.(A list containing ommited sequences.)
     */
    protected static DoubleObject<String, ArrayList<String>> removeSequences(String s, char seq)
    {
        ArrayList<String> ommited = new ArrayList<>();
        String res = "";        
        boolean inspectingValue = false;
        String buffer = "";
        String val = "";
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(c == seq)
            {
                if(inspectingValue)
                {
                    res += val + " ";
                    ommited.add(buffer);
                    val = "";
                    buffer = "";
                }
                
                inspectingValue = !inspectingValue;
            }
            else if(inspectingValue)
            {
                buffer += c;
            }
            else if(c != ' ')
            {
                val += c;
            }
        }
        return new DoubleObject<>(res.trim(), ommited);
    }
    
    /**
     * Returns true if string is empty. Also Returns true when string contains only new lines, or spaces.
     * @param s String to compare.
     * @return -||-
     */
    protected static boolean isStringEmpty(String s)
    {
        if(s == null || s.equals(""))
        {
            return true;
        }
        else
        {
            boolean isEmpty = true;
            for(int i = 0; i < s.length(); i++)
            {
                char c = s.charAt(i);
                if(c != ' ' && c != '\n')
                {
                    isEmpty = false;
                }
            }
            return isEmpty;
        }
    }
}