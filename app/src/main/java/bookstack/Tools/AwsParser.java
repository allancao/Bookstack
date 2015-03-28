package bookstack.Tools;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import bookstack.Book;

public class AwsParser {

    public static String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = (Document) db.parse(is);
            String message = doc.getDocumentElement().getTextContent();

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }

        return doc;
    }

    public List<Book> asinSimilarityLookup(String asin) throws Exception{
        Map<String, String> map = UrlParameterHandler.getInstance()
                .buildMapForSimilarityLookup(asin);
        SignedRequestsHelper requestHelper = new SignedRequestsHelper();
        String url = requestHelper.sign(map);

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = AwsParser.getDomElement(AwsParser.getUrlContents(url));
        List<Book> aItemList = new ArrayList<>();

        NodeList nodeList = document.getDocumentElement().getChildNodes();
        boolean isBook = false;
        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);
            if (node.getNodeName() == "Items") {
                NodeList itemList = node.getChildNodes();

                for (int j = 0; j < itemList.getLength(); j++) {
                    Node item = itemList.item(j);

                    if (item.getNodeName() == "Item") {
                        NodeList itemChildList = item.getChildNodes();
                        Book aItem = new Book();

                        for (int k = 0; k < itemChildList.getLength(); k++) {
                            Node itemChild = itemChildList.item(k);
                            String content = itemChild.getLastChild().
                                    getTextContent().trim();

                            switch (itemChild.getNodeName()) {
                                case "ASIN":
                                    aItem.setAsin(content);
                                    break;
                                case "DetailPageURL":
                                    aItem.setDetailPageUrl(content);
                                    break;
                                case "SmallImage":
                                    aItem.setSmallImage(itemChild.getFirstChild().getTextContent().trim());
                                    break;
                                case "ItemAttributes":
                                    NodeList childChildList = itemChild.getChildNodes();
                                    for (int l = 0; l < childChildList.getLength(); l++) {

                                        Node childChild = childChildList.item(l);
                                        String attributes = childChild.getLastChild()
                                                .getTextContent().trim();
                                        switch (childChild.getNodeName()) {
                                            case "Author":
                                                if (aItem.getAuthor() != null) {
                                                    aItem.setAuthor(aItem.getAuthor() + "," + attributes);
                                                } else {
                                                    aItem.setAuthor(attributes);
                                                }
                                                break;
                                            case "Title":
                                                aItem.setTitle(attributes);
                                                break;
                                            case "ProductGroup":
                                                if (attributes.equals("Book")) {
                                                    isBook = true;
                                                }
                                        }
                                    }
                            }
                        }
                        if (isBook && aItem.getAuthor() != null) {
                            aItemList.add(aItem);
                        }
                        isBook = false;
                    }
                }
            }
        }

        for (Book book : aItemList) {
            System.out.println(book.toString());
        }

        return aItemList;
    }

    public static void main(String[] args) throws Exception {
        AwsParser parser = new AwsParser();
        parser.asinSimilarityLookup("0201633612");
    }
}
