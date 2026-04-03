package org.example;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EnvPropertiesTest extends TestBase {

    @Test
    public void envPropertiesLoaded() {
        String baseUrl = getConfig("baseUrl", null);
        System.out.println("Resolved baseUrl: " + baseUrl);
        Assert.assertNotNull(baseUrl, "baseUrl should be provided by env properties or system properties");
    }
}
