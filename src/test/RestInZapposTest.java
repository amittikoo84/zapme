package test;

import christmas.RestInZappos;
import org.junit.Before;
import org.junit.Test;

public class RestInZapposTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCreateCatalog() {
        RestInZappos bean = new RestInZappos();
        bean.setItemQuantity(10);
        bean.setItemTotal(100);
        RestApiZapStub apiZapStub = new RestApiZapStub();
        //TODO Add Validations and Asserts
    }

}
