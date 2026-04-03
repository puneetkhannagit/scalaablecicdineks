package org.example;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GoogleSearchTest extends TestBase {

    @DataProvider(name = "queries")
    public Object[][] queries() {
        return new Object[][]{
                {"selenium webdriver"},
                {"openai chatgpt"},
                {"java 17 features"}
        };
    }

    @Test(dataProvider = "queries")
    public void searchProducesResults(String query) {
        GoogleHomePage google = new GoogleHomePage(getDriver());
        google.open();
        google.search(query);
        String title = getDriver().getTitle();
        System.out.println("Search title: " + title);
        Assert.assertTrue(title.toLowerCase().contains(query.split(" ")[0].toLowerCase()));
    }

    @Test(groups = {"smoke"})
    public void quickSmoke() {
        GoogleHomePage google = new GoogleHomePage(getDriver());
        google.open();
        google.search("testng parallel tests");
        String title = getDriver().getTitle();
        Assert.assertTrue(title.toLowerCase().contains("testng"));
    }
}
