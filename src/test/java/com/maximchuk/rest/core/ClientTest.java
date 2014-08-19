package com.maximchuk.rest.core;

import com.maximchuk.rest.client.ProductCatalogClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Maxim Maximchuk
 *         date 19.08.2014.
 */
public class ClientTest {

    @Test
    public void testGetTypes() {
        try {
            ProductCatalogClient client = new ProductCatalogClient();
            String response = client.getTypes();
            Assert.assertNotNull(response);
            Assert.assertFalse(response.isEmpty());
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testSearch() {
        try {
            ProductCatalogClient client = new ProductCatalogClient();
            String response = client.search("культ", "crop");
            Assert.assertNotNull(response);
            Assert.assertFalse(response.isEmpty());
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

}
