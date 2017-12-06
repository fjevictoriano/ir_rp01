/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.irs.ir_p01;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author fespinosa
 */
public class HTMLHandler extends DefaultHandler
{

    private DOMFragmentParser parser = new DOMFragmentParser();

    public Document getDocument(InputStream is)

    {
        DocumentFragment node
              = new HTMLDocumentImpl().createDocumentFragment();

        try
        {
            parser.parse(new InputSource(is), node);

        } catch (SAXException | IOException ex)
        {
            //TODO: throw a custom exception ""Cannot parse HTML document:"
            Logger.getLogger(HTMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        org.apache.lucene.document.Document doc
              = new org.apache.lucene.document.Document();
        StringBuffer sb = new StringBuffer();
        getText(sb, node, "title");
        String title = sb.toString();

        getText(sb, node);
        String text = sb.toString();
        if ((title != null) && (!title.equals("")))
        {
            doc.add(new TextField("title", title, Field.Store.YES));
        }
        if ((text != null) && (!text.equals("")))
        {
            doc.add(new TextField("body", text, Field.Store.YES));
        }
        return doc;
    }

    private void getText(StringBuffer sb, Node node)
    {
        if (node.getNodeType() == Node.TEXT_NODE)
        {
            sb.append(node.getNodeValue());
        }
        NodeList children = node.getChildNodes();
        if (children != null)
        {
            int len = children.getLength();
            for (int i = 0; i < len; i++)
            {
                getText(sb, children.item(i));
            }
        }
    }

    private boolean getText(StringBuffer sb, Node node, String element)
    {

        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
            if (element.equalsIgnoreCase(node.getNodeName()))
            {
                getText(sb, node);
                return true;
            }
        }
        NodeList children = node.getChildNodes();
        if (children != null)
        {
            int len = children.getLength();
            for (int i = 0; i < len; i++)
            {
                if (getText(sb, children.item(i), element))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
