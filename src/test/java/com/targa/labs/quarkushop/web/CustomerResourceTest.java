package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class CustomerResourceTest {

    @Test
    void testAll() {
        when().get("/customers").then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThanOrEqualTo(3))
                .body(containsString("jason.bourne@mail.hello"))
                .body(containsString("homer.simpson@mail.hello"))
                .body(containsString("peter.quinn@mail.hello"));
    }

    @Test
    void testAllActiveUsers() {
        when().get("/customers/active").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Simpson"))
                .body(containsString("Homer"));
    }

    @Test
    void testAllInactiveUsers() {
        when().get("/customers/inactive").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("peter.quinn@mail.hello"));
    }

    @Test
    void testFindById() {
        when().get("/customers/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("Bourne"));
    }

    @Test
    void testCreate() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        Integer newCustomerId = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams).post("/customers").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        assertNotNull(newCustomerId);

        when().get("/customers/" + newCustomerId).then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Saul"))
                .body(containsString("Berenson"))
                .body(containsString("call.saul@mail.com"));

        when().delete("/customers/" + newCustomerId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDeleteThenCustomerIsDisabled() {
        Integer initialActiveCount = when().get("/customers/active").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("size()");

        Integer initialInactiveCount = when().get("/customers/inactive").then()
                .statusCode(OK.getStatusCode())
                .extract().jsonPath()
                .getInt("size()");

        when().delete("/customers/1").then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().get("/customers/active").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(initialActiveCount - 1));

        when().get("/customers/inactive").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(initialInactiveCount + 1))
                .body(containsString("Jason"))
                .body(containsString("Bourne"));
    }
}
