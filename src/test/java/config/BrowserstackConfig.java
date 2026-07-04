package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:config/browserstack.properties"
})
public interface BrowserstackConfig extends Config {

    @Key("BROWSERSTACK_USER")
    String user();

    @Key("BROWSERSTACK_KEY")
    String key();

    /** Ключ намеренно не remoteUrl: CI передаёт -DremoteUrl со значением Selenoid для web-слоя. */
    @Key("browserstackHub")
    @DefaultValue("https://hub.browserstack.com/wd/hub")
    String hubUrl();

    @Key("app")
    String app();

    /** Профиль устройства: pixel / samsung / xiaomi (см. device.* в browserstack.properties). */
    @Key("phone")
    @DefaultValue("pixel")
    String phone();

    @Key("device.${phone}.name")
    String deviceName();

    @Key("device.${phone}.osVersion")
    String osVersion();

    @Key("appiumVersion")
    @DefaultValue("2.6.0")
    String appiumVersion();

    @Key("project")
    String project();

    @Key("build")
    String build();
}
