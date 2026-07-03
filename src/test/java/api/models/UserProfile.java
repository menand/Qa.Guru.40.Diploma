package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

    private String id;
    private Auth auth;
    private Profile profile;
    private Stats stats;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Auth {
        private Local local;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Local {
        private String username;
        private String email;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stats {
        private Integer lvl;
        private Double hp;
        private Double exp;
        private Double gp;
    }
}
