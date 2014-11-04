package christmas;


import com.google.gson.Gson;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Amit Tikoo on 11/3/14.
 *
 * This is a managed bean, (View Scoped) that contains Actions and modal items for our application.<br/>
 *
 * When a user clicks starts the action we call the Zappos search API.<br/>
 *
 * <b><u>Steps:</u></b><br/>
 *
 * 1. Divide the amount user wants to spend by the item quantity which gives us the approx. amount (rounded to bottom) <br/>
 *    the user wants to spend on each gift.<br/>
 * 2. Search the items on zappos with the amount from Step 1.<br/>
 * 3. If enough items are found (3 times the amount requested by user - since we are showing the user 3 catalogs)<br/>
 *     i. Dividve the List into 3 sublists and display as a catalog that can be switched and viewed using AJAX.<br/>
 * 4. If not enough items, first check if there are more pages (items) for the same price available and use them<br/>
 * 5. If more pages are not available reduce the amount by one cent and retry search.<br/>
 * 6. If enough items are still not available, reduce by $1 and retry.<br/>
 * 7. The best fit will be shown first.<br/>
 *
 * <b><u>Assumptions, Error Conditions and Validations:</u></b><br/>
 *
 * 1. The input should be numbers. Non-negative and greater than 0.<br/>
 * 2. No. of gifts should not exceed 100.<br/>
 * 3. If enough results are not available, show message to user.<br/>
 *
 *
 */

@ManagedBean
@ViewScoped
public class RestInZappos implements ZapConstants {

    private int itemQuantity;
    private int itemTotal;
    private int perItemEven;
    private int catalogIndex;
    private List<ZapposItem[]> catalogs = new LinkedList<ZapposItem[]>();
    private ZapposItem[] items;
    private ZapposItem[] currentCatalog;
    private String addToPrice = ".0";
    private int currentPage = 1;
    private String priceFilter;

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        destinationFile += "../resources/";
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

    /**
     * Call the REST API
     *
     * @param urlStr: The URL string put togethere for REST call.
     * @return Response.
     * @throws IOException
     */
    private static String httpGet(String urlStr) throws IOException {

        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (connection.getResponseCode() != 200) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The was an error while trying to access the web service."));
        }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseString = new StringBuilder();

        String line;
        while ((line = rd.readLine()) != null)
            responseString.append(line);
        rd.close();

        connection.disconnect();
        return responseString.toString();
    }

    /**
     * Initialize data.
     */
    private void init() {
        addToPrice = ".0";
        currentPage = 1;
        catalogs = new ArrayList<ZapposItem[]>();
        catalogIndex = 0;
    }

    /**
     * This Action Listener is called when the user initially enter information and clicks submit button.
     */

    public void startAwesomeness() {
        init();
        perItemEven = itemTotal / itemQuantity;
        createCatalog();
        updateCatalog(0);
    }

    /**
     * Catalog is a paginated table (3 pages) that is presented to the user.
     */
    private void createCatalog() {
        items = new ZapposItem[itemQuantity];
        if (currentPage != 1) {
            System.out.println("Try page page: " + currentPage);
            tryNextPage();
            if (items.length == 0) {
                tryAnotherPrice();
                swapAddToPrice();
                currentPage = 1;
            }
        } else {
            tryAnotherPrice();
            swapAddToPrice();
        }
        //tryAnotherPrice();
        populateCatalog();
    }

    /**
     * We are searching for two price which we believe are most commonplace.
     * 1. Whole number : eg: $100.0
     * 2. a cent less: eg: $99.99
     */
    private void swapAddToPrice() {
        if (addToPrice.equals(".0")) {
            addToPrice = ".99";
        } else {
            addToPrice = ".0";
        }

    }

    /**
     * Call Zappos REST API and enter the required params.
     * The response is parsed using GSON into a List of objects.
     */
    private void tryAnotherPrice() {
        priceFilter = String.format(FILTER, String.valueOf(perItemEven) + addToPrice);
        perItemEven--;
        System.out.println("Try another Price: " + priceFilter);
        String URLRequest = SEARCH + LIMIT + FACET_FILTER + priceFilter + SORT_DESC + API_KEY;
        retrieveAndParseResponse(URLRequest);
    }

    /**
     * Try another page, since the REST API only responds with one page at a time.
     *
     */
    private void tryNextPage() {
        String pageFilter = String.format(PAGE, currentPage);
        priceFilter = String.format(FILTER, String.valueOf(perItemEven) + addToPrice);
        String URLRequest = SEARCH + LIMIT + FACET_FILTER + priceFilter + pageFilter + SORT_DESC + API_KEY;
        retrieveAndParseResponse(URLRequest);
    }

    private void retrieveAndParseResponse(String URLRequest) {
        Gson gson = new Gson();
        try {
            String response = httpGet(URLRequest);
            ZapposSearchResponse theResponse = gson.fromJson(response, ZapposSearchResponse.class);
            items = theResponse.getResults();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * Go through the list returned by Zappos API and create the catalog.
     */
    private void populateCatalog() {
        ZapposItem[] choice;
        int startAt = 0;
        while (catalogIndex < 3 && perItemEven > 0) {
            int endAt = startAt + itemQuantity;
            if (endAt > items.length) {
                //Not enough items to create a perfect match, pause and restart with a lower price (minus 1)
                currentPage++;
                createCatalog();
            } else {
                choice = Arrays.copyOfRange(items, startAt, endAt);
                catalogIndex++;
                if (choice[0] != null) {
                    catalogs.add(choice);
                }
            }

            startAt += itemQuantity;
        }
    }

    /**
     * The Catalog list is paginated. This function implements pagination
     *
     * @param index: the index of the catalog to show.
     * @return null: Placeholder.
     */
    public String updateCatalog(int index) {
        if (catalogs.size() == 0) {
            //Not enough items, put message in Faces Context to be shown tot he user.
            notEnoughItems();
        } else {
            currentCatalog = catalogs.get(index);
        }

        return null;
    }

    /**
     * In case we are can't suggest enough items, show an error message.
     */
    private void notEnoughItems() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Not enough products. Please tweak your choices."));
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public int getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(int itemTotal) {
        this.itemTotal = itemTotal;
    }

    public ZapposItem[] getCurrentCatalog() {
        return currentCatalog;
    }

    public boolean getCatalogPresent() {
        return catalogs.size() > 0;
    }

    public List<ZapposItem[]> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<ZapposItem[]> catalogs) {
        this.catalogs = catalogs;
    }
}
