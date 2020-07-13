package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.common.Web;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class CartResourceTest {

    @Test
    void testFindAll() {
        when().get(Web.API + "/carts").then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void testFindAllActiveCarts() {
        when().get(Web.API + "/carts/active").then()
                .statusCode(200);
    }

    @Test
    void testGetActiveCartForCustomer() {
        when().get(Web.API + "/carts/customer/3").then()
                .statusCode(200)
                .body(containsString("Peter"));
    }

    @Test
    void testFindById() {
        when().get(Web.API + "/carts/3").then()
                .statusCode(200)
                .body(containsString("status"))
                .body(containsString("NEW"));

        when().get(Web.API + "/carts/100").then()
                .statusCode(204)
                .body(emptyOrNullString());
    }

    @Test
    void testDelete() {
        when().get(Web.API + "/carts/active").then()
                .statusCode(200)
                .body(containsString("Jason"))
                .body(containsString("NEW"));

        when().delete(Web.API + "/carts/1").then()
                .statusCode(204);

        when().get(Web.API + "/carts/1").then()
                .statusCode(200)
                .body(containsString("Jason"))
                .body(containsString("CANCELED"));
    }
}
