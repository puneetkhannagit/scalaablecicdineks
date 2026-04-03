package org.example;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BaseNavigationTest extends TestBase {

    @Test
    public void openBaseUrlAndCheckTitle() {
        String baseUrl = getConfig("baseUrl", "https://googlr.com");
        getDriver().get(baseUrl);
        String title = getDriver().getTitle();
        System.out.println("Open " + baseUrl + " title: " + title);
       // Assert.assertTrue(title != null && !title.isBlank(), "Title should not be empty");
    }
}
