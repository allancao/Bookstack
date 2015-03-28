package bookstack.Tools;

import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Parser {
    /** ---------------------  Search TAG --------------------- */
    private static final String KEY_ROOT="Items";
    private static final String KEY_REQUEST_ROOT="Request";
    private static final String KEY_REQUEST_CONTAINER="IsValid";
    private static final String KEY_ITEM="Item";
    private static final String KEY_ID="ASIN";
    private static final String KEY_ITEM_URL="DetailPageURL";
    private static final String KEY_IMAGE_ROOT="MediumImage";
    private static final String KEY_IMAGE_CONTAINER="URL";
    private static final String KEY_ITEM_ATTR_CONTAINER="ItemAttributes";
    private static final String KEY_ITEM_ATTR_TITLE="Title";

    private static final String VALUE_VALID_RESPONCE="True";

    //Tags
    //Items,Request,IsValid,Item,ASIN,DetailPageURL,MediumImage,URL,ItemAttributes,Title


    public NodeList getResponseNodeList(String service_url) {
        String searchResponse = this.getUrlContents(service_url);
//        Log.i("url",""+service_url);
//        Log.i("response",""+searchResponse);
        Document doc;
        NodeList items = null;
        if (searchResponse != null) {
            try {
                doc = this.getDomElement(searchResponse);
                items = doc.getElementsByTagName(KEY_ROOT);
                Element element=(Element)items.item(0);
                if(isResponseValid(element)){
                    items=doc.getElementsByTagName(KEY_ITEM);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return items;
    }

//    public SearchObject getSearchObject(NodeList list,int position){
//        SearchObject object=new SearchObject();
//        Element e=(Element)list.item(position);
//        object.setUrl(this.getValue(e, KEY_ITEM_URL));
//        object.setId(this.getValue(e, KEY_ID));
//        object.setImageUrl(this.getValue((Element)(e.getElementsByTagName(KEY_IMAGE_ROOT).item(0))
//                , KEY_IMAGE_CONTAINER));
//        object.setTitle(this.getValue((Element)(e.getElementsByTagName(KEY_ITEM_ATTR_CONTAINER).item(0))
//                , KEY_ITEM_ATTR_TITLE));
//        return object;
//    }

    public boolean isResponseValid(Element element){
        NodeList nList=element.getElementsByTagName(KEY_REQUEST_ROOT);
        Element e=(Element)nList.item(0);
        if(getValue(e, KEY_REQUEST_CONTAINER).equals(VALUE_VALID_RESPONCE)){
            return true;
        }
        return false;
    }

    /** In app reused functions */

    private String getUrlContents(String theUrl) {
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

    public Document getDomElement(String xml) {
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

    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE
                            || (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    public static List<AmazonItem> parse(final String url)
    {
        final AmazonItem currentItem = new AmazonItem();
        final List<AmazonItem> returnItems = new ArrayList<AmazonItem>();

        final RootElement root = new RootElement("ItemSearchResponse");
        final Element items = root.getChild("Items");
        final Element item = items.getChild("Item");

        item.getChild("DetailPageURL").setEndTextElementListener(new EndTextElementListener()
        {
            public void end(final String body) {
                currentItem.setLink(body);
            }
        });

        final Element attributes = item.getChild("ItemAttributes");

        attributes.getChild("Title").setEndTextElementListener(new EndTextElementListener()
        {
            public void end(final String body) {
                currentItem.setTitle(body);
            }
        });

        item.setEndElementListener(new EndElementListener()
        {
            public void end() {
                returnItems.add(currentMessage.copy());
            }
        });

        try
        {
            final InputStream stream = new URL(url).openConnection().getInputStream();

            Xml.parse(stream, Xml.Encoding.UTF_8, root.getContentHandler());
            stream.close();
        }
        catch (Exception e)
        {
        }

        return returnItems;
    }
}