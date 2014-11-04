package christmas;

// ZapposItem is a class that represents a single item from Zappos.com.

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ZapposItem {

    private String productId, brandName, productName, thumbnailImageUrl, originalPrice, price, percentOff, productUrl, defaultImageUrl, emailAddress;
    private boolean hasBeenNotified;

    public ZapposItem(String productName, String percentOff, String productId, String brandName) {
        this.productName = productName;
        this.percentOff = percentOff;
        this.productId = productId;
        this.brandName = brandName;
    }

    public ZapposItem() {
        this.productId = "";
        this.productName = "";
        this.brandName = "";
        this.originalPrice = "";
        this.percentOff = "0%";
        this.price = "";
        this.productUrl = "";
    }


    // Product Name
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    // Percent Off
    public String getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(String percentOff) {
        this.percentOff = percentOff;
    }

    // Thumbnail Image URL
    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    // Brand Name
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    // Price
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    // Product URL
    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }


    // pretty(ish) output for debugging
    public String toString() {
        return brandName + " - " + productName + "\n" + price + " - " + percentOff + " off\n" + productUrl;
    }

    public BigDecimal getPriceAsInt() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        try {
            return new BigDecimal(nf.parse(getPrice()).toString());
        } catch (ParseException e) {
            return new BigDecimal(0);
        }
    }
}