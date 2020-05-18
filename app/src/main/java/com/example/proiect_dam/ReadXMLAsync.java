package com.example.proiect_dam;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


import androidx.fragment.app.FragmentManager;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ReadXMLAsync extends AsyncTask<URL, Void, List<News>> {

    InputStream ist = null;
    List<News> news;

    @Override
    protected List<News> doInBackground(URL... urls) {

        HttpURLConnection conn = null;

        try {

            conn = (HttpURLConnection) urls[0].openConnection();
            conn.setRequestMethod("GET");
            ist = conn.getInputStream();
            news = new ArrayList<>();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document domDoc = db.parse(ist);
                domDoc.getDocumentElement().normalize();

                Node entry = getNodeByName("feed", domDoc.getDocumentElement());
                if (entry != null) {
                    // String data = getAttributeValue(entry, "title");
                    NodeList childList = entry.getChildNodes();
                    System.out.println(childList.getLength());
                    for (int i = 0; i < childList.getLength(); i++) {
                        Node node = childList.item(i);

                        if (node.getNodeName().equals("entry")) {
                            NodeList childChildren = node.getChildNodes();
                            String link="";
                            String title="";
                            String description="";
                            for (int j = 0; j < childChildren.getLength(); j++) {
                                Node childNode = childChildren.item(j);

                                if (childNode.getNodeName().equals("title"))
                                {
                                    title = childNode.getTextContent();
                                }
                                if (childNode.getNodeName().equals("link"))
                                {
                                    link = getAttributeValue(childNode, "href");
                                }
                                if (childNode.getNodeName().equals("category"))
                                {
                                    description = getAttributeValue(childNode, "term");
                                }

                                //System.out.println(link+title+description);
                            }
                            news.add(new News(link, title, description));
                        }
                    }
                }
        } catch (Exception e) {

            Log.e("doInBackground", e.getMessage());

        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return news;
    }


    private Node getNodeByName(String nodeName, Node parentNode) {

        if (parentNode.getNodeName().equals(nodeName)) {
            return parentNode;
        }

        NodeList list = parentNode.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = getNodeByName(nodeName, list.item(i));
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    private String getAttributeValue(Node node, String attrName) {
        try {
            return ((Element) node).getAttribute(attrName);
        } catch (Exception e) {
            return "";
        }
    }




}