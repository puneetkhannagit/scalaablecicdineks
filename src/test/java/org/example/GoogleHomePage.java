package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class GoogleHomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By searchBox = By.name("q");
    private final By results = By.id("search");

    public GoogleHomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get("https://www.google.com");
        // Wait until the search box is present (handles consent overlays in many locales implicitly)
        wait.until(ExpectedConditions.presenceOfElementLocated(searchBox));
    }

    public void search(String query) {
        WebElement box = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        box.clear();
        box.sendKeys(query);
        box.submit();
        // wait until results container appears and title contains a part of the query
        wait.until(ExpectedConditions.presenceOfElementLocated(results));
        // sometimes title updates include only the first word - wait for some part of the query
        String firstToken = query.split(" ")[0];
        wait.until(ExpectedConditions.titleContains(firstToken));
    }
}
