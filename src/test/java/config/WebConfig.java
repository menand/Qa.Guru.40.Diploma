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

    /** chrome или firefox. */
    @Key("browser")
    String browser();

    /** Версия браузера для Selenoid/Grid; пусто — любая доступная. */
    @Key("browserVersion")
    @DefaultValue("")
    String browserVersion();

    @Key("browserSize")
    String browserSize();

    /** URL Selenoid/Selenium Grid; пусто — локальный браузер. */
    @Key("remoteUrl")
    @DefaultValue("")
    String remoteUrl();

    /** Запись видео в Selenoid (учитывается только вместе с remoteUrl). */
    @Key("videoEnabled")
    @DefaultValue("true")
    boolean videoEnabled();

    @Key("timeout")
    long timeout();

    @Key("headless")
    boolean headless();
}
