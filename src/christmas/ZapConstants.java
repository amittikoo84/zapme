package christmas;

/**
 * Created by Amit Tikoo on 11/3/14.
 */
public interface ZapConstants {
    // constants used for searching with the Zappos API
    String PRODUCT_SEARCH = "http://api.zappos.com/Product?id=";
    String SEARCH = "http://api.zappos.com/Search";
    String FACET_FILTER = "&includes=[\"facets\"]&facets=[\"price\"]&filters=";


    String FILTER = "{\"price\":[\"%s\"]}";
    String PAGE = "&page=%d";
    String LIMIT = "?limit=100";

    // add your Zappos API Key here to use Zappify
    String API_KEY = "&key=a73121520492f88dc3d33daf2103d7574f1a3166";
    String SORT_DESC = "&sort={\"price\":\"desc\"}";
}
