package org.example;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EnvDrivenTest extends TestBase {
    private final String envName;

    public EnvDrivenTest(String envName) {
        this.envName = envName;
        // set env for this instance. Tests and TestBase will read System.getProperty("env") when they call getConfig/load.
        System.setProperty("env", envName);
        System.out.println("Test instance configured for env: " + envName);
    }

    @Test
    public void openBaseUrlAndAssert() {
        String baseUrl = getConfig("baseUrl", "https://example.com");
        System.out.println("[" + envName + "] navigating to " + baseUrl);
        getDriver().get(baseUrl);
        String title = getDriver().getTitle();
        System.out.println("[" + envName + "] title: " + title);
        Assert.assertNotNull(title);
    }

    @Test
    public void envRunModeConsistency() {
        String mode = runMode();
        System.out.println("[" + envName + "] runMode resolved to: " + mode);
        Assert.assertTrue(mode.equals("local") || mode.equals("grid"));
    }
}
