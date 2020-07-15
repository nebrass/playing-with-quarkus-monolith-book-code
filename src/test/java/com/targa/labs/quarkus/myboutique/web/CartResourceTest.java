package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
public class CartResourceTest {

    @Test
    void testFindAll() {
        when().get("/carts").then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThan(0));
    }

    @Test
    void testFindAllActiveCarts() {
        when().get("/carts/active").then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testGetActiveCartForCustomer() {
        when().get("/carts/customer/3").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Peter"));
    }

    @Test
    void testFindById() {
        when().get("/carts/3").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("status"))
                .body(containsString("NEW"));

        when().get("/carts/100").then()
                .statusCode(NO_CONTENT.getStatusCode())
                .body(emptyOrNullString());
    }

    @Test
    void testDelete() {
        when().get("/carts/active").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("NEW"));

        when().delete("/carts/1").then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().get("/carts/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("CANCELED"));
    }
}
