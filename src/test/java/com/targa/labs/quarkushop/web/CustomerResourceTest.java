package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class CustomerResourceTest {

    @Test
    void testAll() {
        get("/customers").then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThanOrEqualTo(3))
                .body(containsString("jason.bourne@mail.hello"))
                .body(containsString("homer.simpson@mail.hello"))
                .body(containsString("peter.quinn@mail.hello"));
    }

    @Test
    void testAllActiveUsers() {
        get("/customers/active").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Simpson"))
                .body(containsString("Homer"));
    }

    @Test
    void testAllInactiveUsers() {
        get("/customers/inactive").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("peter.quinn@mail.hello"));
    }

    @Test
    void testFindById() {
        get("/customers/1").then()
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
                .body(requestParams).post("/customers").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        assertThat(newCustomerId).isNotZero();

        get("/customers/" + newCustomerId).then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Saul"))
                .body(containsString("Berenson"))
                .body(containsString("call.saul@mail.com"));

        delete("/customers/" + newCustomerId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDeleteThenCustomerIsDisabled() {
        var initialActiveCount = get("/customers/active").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("size()");

        var initialInactiveCount = get("/customers/inactive").then()
                .statusCode(OK.getStatusCode())
                .extract().jsonPath()
                .getInt("size()");

        delete("/customers/1").then()
                .statusCode(NO_CONTENT.getStatusCode());

        get("/customers/active").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(initialActiveCount - 1));

        get("/customers/inactive").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(initialInactiveCount + 1))
                .body(containsString("Jason"))
                .body(containsString("Bourne"));
    }
}
