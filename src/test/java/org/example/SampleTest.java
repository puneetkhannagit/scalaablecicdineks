package org.example;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SampleTest extends TestBase {

    @Test(groups = {"release1"})
    public void openExampleDotCom() {
        getDriver().get("https://google.com");
        String title = getDriver().getTitle();
        System.out.println("Page title: ===>" + title);
        //Assert.assertTrue(title.toLowerCase().contains("Google"),"The title doesnt match");
    }
}
