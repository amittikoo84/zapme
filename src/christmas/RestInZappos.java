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
 */

@ManagedBean
@ViewScoped
public class RestInZappos implements ZapConstants {

    private int itemQuantity;
    private int itemTotal;
    private int perItemEven;
    private int catalogIndex;
    //Declare as list and reverse in the end in order to put the best match first.
    private List<ZapposItem[]> catalogs = new LinkedList<ZapposItem[]>();
    private ZapposItem[] items;
    private ZapposItem[] currentCatalog;
    private String addToPrice = ".0";
    private int currentPage = 1;
    private String priceFilter;


    /**
     * This Action Listener is called when the user initially enter information and clicks submit button.
     */

    public void playSlots() throws IOException {
        addToPrice = ".0";
        currentPage = 1;
        catalogs = new ArrayList<ZapposItem[]>();
        perItemEven = itemTotal / itemQuantity;
        createCatalog();
        updateCatalog(0);
        catalogIndex = 0;

        //saveImage(getCatalogs().get(0)[0].getThumbnailImageUrl(), getCatalogs().get(0)[0].getProductName());
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
    public void tryAnotherPrice() {
        priceFilter = String.format(FILTER, String.valueOf(perItemEven) + addToPrice);
        perItemEven--;
        System.out.println("Try another Price: " + priceFilter);
        String URLRequest = SEARCH + LIMIT + FACET_FILTER + priceFilter + SORT_DESC + API_KEY;
        retrieveAndParseResponse(URLRequest);
    }

    /**
     * Try another page, since the REST API only responds with one page at a time.
     *
     * @return
     */
    private boolean tryNextPage() {
        String pageFilter = String.format(PAGE, currentPage);
        priceFilter = String.format(FILTER, String.valueOf(perItemEven) + addToPrice);
        String URLRequest = SEARCH + LIMIT + FACET_FILTER + priceFilter + pageFilter + SORT_DESC + API_KEY;
        return retrieveAndParseResponse(URLRequest);
    }

    private boolean retrieveAndParseResponse(String URLRequest) {
        Gson gson = new Gson();
        try {
            String response = httpGet(URLRequest);
            ZapposSearchResponse theResponse = gson.fromJson(response, ZapposSearchResponse.class);
            items = theResponse.getResults();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return items.length != 0;
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
                if(choice[0] != null ) {
                    catalogs.add(choice);
                }
            }

            startAt += itemQuantity;
        }
    }

    /**
     * The Catalog list is paginated. This function implements pagination
     *
     * @param index
     * @return
     */
    public String updateCatalog(int index) {
        if (catalogs.size() == 0) {
            //Not enough items
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
     * @param urlStr
     * @return
     * @throws IOException
     */
    private static String httpGet(String urlStr) throws IOException {

        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (connection.getResponseCode() != 200) {
            throw new IOException(connection.getResponseMessage());
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


    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemTotal(int itemTotal) {
        this.itemTotal = itemTotal;
    }

    public int getItemTotal() {
        return itemTotal;
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
