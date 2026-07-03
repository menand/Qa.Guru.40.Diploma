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

    @Key("mobileDeviceName")
    String mobileDeviceName();

    @Key("timeout")
    long timeout();

    @Key("headless")
    boolean headless();
}
