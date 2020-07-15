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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
public class OrderResourceTest {

    @Test
    void testAll() {

        when().get("/orders").then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThanOrEqualTo(2))
                .body(containsString("totalPrice"))
                .body(containsString("999.00"))
                .body(containsString("status"))
                .body(containsString("CREATION"));
    }

    @Test
    void testExistsById() {
        when().get("/orders/exists/1").then()
                .statusCode(OK.getStatusCode())
                .body(is("true"));

        when().get("/orders/exists/100").then()
                .statusCode(OK.getStatusCode())
                .body(is("false"));
    }

    @Test
    void testFindByCustomerId() {
        when().get("/orders/customer/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("jason.bourne@mail.hello"));
    }

    @Test
    void testNotFoundAfterDeleted() {
        when().get("/orders/exists/2").then()
                .statusCode(OK.getStatusCode())
                .body(is("true"));

        when().delete("/orders/2").then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().get("/orders/exists/2").then()
                .statusCode(OK.getStatusCode())
                .body(is("false"));
    }

    @Test
    void testNotFoundById() {
        when().get("/orders/100").then()
                .statusCode(NO_CONTENT.getStatusCode())
                .body(emptyOrNullString());
    }

}
