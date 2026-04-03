package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestBase {
    // Use a ThreadLocal WebDriver so tests running in parallel (methods/threads) don't share the same instance.
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    // Optional environment properties loaded from src/test/resources/env/{env}.properties or classpath
    private final Properties envProperties = new Properties();
    private boolean envPropertiesLoaded = false;

    protected WebDriver getDriver() {
        return DRIVER.get();
    }

    // Configuration resolution priority:
    // 1) System property (-DrunMode=...)
    // 2) Environment variable (RUN_MODE)
    // 3) Environment properties file (if -Denv=staging or ENV=staging provided)
    // 4) Default
    // This allows running based on an 'env' (e.g. staging, prod) where you can place a properties file under
    // src/test/resources/env/{env}.properties with keys like runMode, headless, gridUrl.

    protected String getConfig(String key, String defaultValue) {
        // 1. system property
        String v = System.getProperty(key);
        if (v != null && !v.isBlank()) {
            return v;
        }
        // 2. env var (upper-case, dots to underscores)
        String envKey = key.toUpperCase().replace('.', '_');
        v = System.getenv(envKey);
        if (v != null && !v.isBlank()) {
            return v;
        }
        // 3. env properties file
        if (!envPropertiesLoaded) {
            loadEnvProperties();
        }
        v = envProperties.getProperty(key);
        if (v != null && !v.isBlank()) {
            return v;
        }
        // 4. default
        return defaultValue;
    }

    protected void loadEnvProperties() {
        envPropertiesLoaded = true;
        String envName = getEnvName();
        if (envName == null || envName.isBlank()) {
            return;
        }
        String resourcePath = "env/" + envName + ".properties";
        // First try classpath
        try (InputStream is = TestBase.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                envProperties.load(is);
                System.out.println("Loaded env properties from classpath: " + resourcePath);
                return;
            }
        } catch (IOException ignored) {
        }
        // Next try file system relative to project root (useful when running from IDE)
        String userDir = System.getProperty("user.dir");
        File f = new File(userDir + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + resourcePath);
        if (f.exists() && f.isFile()) {
            try (InputStream is = new FileInputStream(f)) {
                envProperties.load(is);
                System.out.println("Loaded env properties from file: " + f.getAbsolutePath());
            } catch (IOException ignored) {
            }
        }
    }

    protected String getEnvName() {
        String name = System.getProperty("env");
        if (name != null && !name.isBlank()) {
            return name;
        }
        name = System.getenv("ENV");
        return (name == null || name.isBlank()) ? null : name;
    }

    protected String runMode() {
        return getConfig("runMode", "local").toLowerCase();
    }

    protected boolean headless() {
        String val = getConfig("headless", "false");
        return val.equalsIgnoreCase("true") || val.equals("1");
    }

    protected String gridUrl() {
        return getConfig("gridUrl", "http://localhost:4444/wd/hub");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws MalformedURLException {
        if (runMode().equals("grid")) {
            setupRemote();
        } else {
            setupLocal();
        }
        // common defaults
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        getDriver().manage().window().maximize();
    }

    protected void setupLocal() {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        ChromeOptions options = new ChromeOptions();
        List<String> args = new ArrayList<>();
        // CI-friendly arguments
        args.add("--no-sandbox");
        args.add("--disable-dev-shm-usage");
        args.add("--disable-gpu");
        args.add("--window-size=1920,1080");
        // allow recent chromedriver/chrome cross-origin init issues
        args.add("--remote-allow-origins=*");

        if (headless) {
            // use new headless mode when available; fallback to legacy if needed
            args.add("--headless=new");
        }

        options.addArguments(args);

        // Ensure chromedriver binary matches installed chrome
        WebDriverManager.chromedriver().setup();

        // Enable verbose chromedriver logs for diagnostics in CI
        ChromeDriverService service = new ChromeDriverService.Builder()
                .withVerbose(true)
                .withLogFile(new File("target/chromedriver.log"))
                .build();

        WebDriver wd = new ChromeDriver(service, options);
        wd.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wd.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
        driver.set(wd);
    }

    protected void setupRemote() throws MalformedURLException {
        MutableCapabilities capabilities = new MutableCapabilities();
        ChromeOptions options = new ChromeOptions();
        if (headless()) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        URL remoteUrl = java.net.URI.create(gridUrl()).toURL();
        DRIVER.set(new RemoteWebDriver(remoteUrl, (Capabilities) capabilities));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver webDriver = DRIVER.get();
        if (webDriver != null) {
            try {
                webDriver.quit();
            } catch (Exception ignored) {
            } finally {
                DRIVER.remove();
            }
        }
    }
}
