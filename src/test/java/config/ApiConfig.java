package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/api.properties"
})
public interface ApiConfig extends Config {

    @Key("apiBaseUrl")
    String apiBaseUrl();

    @Key("clientHeaderSuffix")
    String clientHeaderSuffix();
}
