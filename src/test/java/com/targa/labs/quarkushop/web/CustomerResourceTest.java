package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.utils.TestContainerResource;
import io.quarkus.runtime.configuration.ProfileManager;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class CustomerResourceTest {

    private static String PREFIX = "";

    @BeforeAll
    static void init() {
        if ("prod".equalsIgnoreCase(ProfileManager.getActiveProfile())) {
            PREFIX = "/api";
        }
    }

    @Test
    void testAll() {
        get(PREFIX + "/customers").then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThanOrEqualTo(3))
                .body(containsString("jason.bourne@mail.hello"))
                .body(containsString("homer.simpson@mail.hello"))
                .body(containsString("peter.quinn@mail.hello"));
    }

    @Test
    void testAllActiveUsers() {
        get(PREFIX + "/customers/active").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Simpson"))
                .body(containsString("Homer"));
    }

    @Test
    void testAllInactiveUsers() {
        get(PREFIX + "/customers/inactive").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("peter.quinn@mail.hello"));
    }

    @Test
    void testFindById() {
        get(PREFIX + "/customers/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("Bourne"));
    }

    @Test
    void testCreate() {
        var requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        var newCustomerId = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams).post(PREFIX + "/customers").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        assertNotNull(newCustomerId);

        get(PREFIX + "/customers/" + newCustomerId).then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Saul"))
                .body(containsString("Berenson"))
                .body(containsString("call.saul@mail.com"));

        delete(PREFIX + "/customers/" + newCustomerId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDeleteThenCustomerIsDisabled() {
        var initialActiveCount = get(PREFIX + "/customers/active").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("size()");

        var initialInactiveCount = get(PREFIX + "/customers/inactive").then()
                .statusCode(OK.getStatusCode())
                .extract().jsonPath()
                .getInt("size()");

        delete(PREFIX + "/customers/1").then()
                .statusCode(NO_CONTENT.getStatusCode());

        get(PREFIX + "/customers/active").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(initialActiveCount - 1));

        get(PREFIX + "/customers/inactive").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(initialInactiveCount + 1))
                .body(containsString("Jason"))
                .body(containsString("Bourne"));
    }
}
