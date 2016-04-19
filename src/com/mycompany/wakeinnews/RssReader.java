package com.mycompany.wakeinnews;

import java.util.*;
import javax.xml.parsers.*;
import android.view.LayoutInflater.*;

public class RssReader
{
    private String rssUrl;

    public RssReader(String rssUrl)
    {
	this.rssUrl = rssUrl;
    }
    
    public List<RssItem> getItems() throws Exception{
	
	SAXParserFactory factory = SAXParserFactory.newInstance();
	SAXParser saxParser = factory.newSAXParser();
	
	RssParseHandler handler = new RssParseHandler();
	
	saxParser.parse(rssUrl, handler);
	
	return handler.getRssItems();
    }
}
