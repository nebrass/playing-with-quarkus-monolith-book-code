package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
public class CustomerResourceTest {

    @Test
    void testAll() {
        when().get("/customers").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(4))
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
    void testDeleteThenCustomerIsDisabled() {
        when().get("/customers/active").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(2));

        when().delete("/customers/1").then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().get("/customers/active").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(1));

        when().get("/customers/inactive").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(3))
                .body(containsString("Jason"))
                .body(containsString("Bourne"));
    }
}
