package api.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserCredentials {
    String id;
    String apiToken;
    String username;
    String email;
    String password;
}
