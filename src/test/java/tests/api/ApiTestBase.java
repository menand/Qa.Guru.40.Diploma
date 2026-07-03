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
}
