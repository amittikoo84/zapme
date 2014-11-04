package test;

import christmas.RestInZappos;
import christmas.ZapposItem;

public class RestApiZapStub {

    private ZapposItem[] items = new ZapposItem[8];
    private RestInZappos restInZappos;

    public RestApiZapStub() {
        ZapposItem item;
        item = new ZapposItem("Watch", "10%", "10", "Nike");
        getItems()[0] = (item);
        item = new ZapposItem("Boot", "10%", "10", "Nike");
        getItems()[1] = (item);
        item = new ZapposItem("Watch", "10%", "100", "Addidas");
        getItems()[2] = (item);
        item = new ZapposItem("Band", "10%", "100", "Addidas");
        getItems()[3] = (item);
        item = new ZapposItem("Boot", "10%", "100", "Addidas");
        getItems()[4] = (item);
        item = new ZapposItem("Kitchen", "10%", "49", "D&G");
        getItems()[5] = (item);
        item = new ZapposItem("Boot", "10%", "49", "D&G");
        getItems()[6] = (item);
        item = new ZapposItem("Mug", "10%", "49", "D&G");
        getItems()[7] = (item);
        restInZappos.setItemQuantity(10);
        restInZappos.setItemTotal(1000);
        restInZappos.setItems(items);
    }

    public RestInZappos getRestInZappos() {
        return restInZappos;
    }

    public void setRestInZappos(RestInZappos restInZappos) {
        this.restInZappos = restInZappos;
    }

    public ZapposItem[] getItems() {
        return items;
    }

    public void setItems(ZapposItem[] items) {
        this.items = items;
    }

}
