package org.example;

import org.testng.annotations.Factory;

public class EnvFactory {

    @Factory
    public Object[] createInstances() {
        // Create test instances for different envs. Each instance will set -Denv via system property before running.
        // We'll create three instances: local, staging, prod. The TestBase and tests read System.getProperty("env") at runtime.
        return new Object[] {
                new EnvDrivenTest("local"),
                new EnvDrivenTest("staging"),
                new EnvDrivenTest("prod")
        };
    }
}
