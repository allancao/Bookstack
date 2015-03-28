package bookstack.Tools;

import java.util.HashMap;
import java.util.Map;

public class UrlParameterHandler {

    public static UrlParameterHandler paramHandler;
    private UrlParameterHandler() {}


    public static synchronized UrlParameterHandler getInstance(){
        if(paramHandler==null){
            paramHandler=new UrlParameterHandler();
            return paramHandler;
        }
        return paramHandler;
    }

    public  Map<String,String> buildMapForSimilarityLookup(String asin){
        Map<String, String> myparams = new HashMap<String, String>();
        myparams.put("Service", "AWSECommerceService");
        myparams.put("Version", "2009-10-01");
        myparams.put("ContentType", "text/xml");
        myparams.put("MaximumPrice","50");
        myparams.put("ResponseGroup", "Images,Small");
        myparams.put("Operation", "SimilarityLookup");
        myparams.put("ItemId", asin);
        return myparams;
    }

    public  Map<String,String> buildMapForItemLookup(String isbn){
        Map<String, String> myparams = new HashMap<String, String>();
        myparams.put("Service", "AWSECommerceService");
        myparams.put("Version", "2009-10-01");
        myparams.put("ContentType", "text/xml");
        myparams.put("Operation", "ItemLookup");
        myparams.put("IdType", "ISBN");
        myparams.put("IncludeReviewsSummary", "False");
        myparams.put("SearchIndex", "Books");
        myparams.put("ItemId", isbn);
        return myparams;
    }

}