package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/web.properties"
})
public interface WebConfig extends Config {

    @Key("baseUrl")
    String baseUrl();

    @Key("browser")
    String browser();

    @Key("browserSize")
    String browserSize();

    /** URL Selenoid/Selenium Grid; пусто — локальный браузер. */
    @Key("remoteUrl")
    @DefaultValue("")
    String remoteUrl();

    @Key("timeout")
    long timeout();

    @Key("headless")
    boolean headless();
}
