package config;

import org.aeonbits.owner.ConfigFactory;

public final class Configs {

    public static final WebConfig WEB = ConfigFactory.create(WebConfig.class);
    public static final ApiConfig API = ConfigFactory.create(ApiConfig.class);
    public static final BrowserstackConfig BROWSERSTACK = ConfigFactory.create(BrowserstackConfig.class);

    private Configs() {
    }
}
