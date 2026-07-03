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

    @Key("remoteUrl")
    String remoteUrl();

    @Key("app")
    String app();

    @Key("device")
    String device();

    @Key("osVersion")
    String osVersion();

    @Key("project")
    String project();

    @Key("build")
    String build();
}
