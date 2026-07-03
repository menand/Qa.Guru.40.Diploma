package api.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class RegisterRequest {
    String username;
    String email;
    String password;
    String confirmPassword;
}
