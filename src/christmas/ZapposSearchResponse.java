package christmas;

public class ZapposSearchResponse {

    private String statusCode;
    private ZapposItem[] results;

    public ZapposSearchResponse(String statusCode, ZapposItem[] results) {
        this.statusCode = statusCode;
        this.results = results;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public ZapposItem[] getResults() {
        return results;
    }
}