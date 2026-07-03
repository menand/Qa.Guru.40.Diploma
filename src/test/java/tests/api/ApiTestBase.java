package tests.api;

import api.models.UserCredentials;
import helpers.TestUsers;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.Tag;

@Tag("api")
@Owner("menand")
@Epic("Habitica REST API")
public abstract class ApiTestBase {

    protected static final UserCredentials USER = TestUsers.shared();

    /** Идентификаторы и токены Habitica — UUID v4. */
    protected static final String UUID_REGEX =
            "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
}
