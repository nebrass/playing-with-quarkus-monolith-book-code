package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.common.Web;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.core.Is.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class OrderResourceTest {

    @Test
    void testAll() {
        when().get(Web.API + "/orders").then()
                .statusCode(200)
                .body("size()", Matchers.is(2))
                .body(containsString("totalPrice"))
                .body(containsString("999.00"))
                .body(containsString("status"))
                .body(containsString("CREATION"));
    }

    @Test
    void testExistsById() {
        when().get(Web.API + "/orders/exists/1").then()
                .statusCode(200)
                .body(is("true"));

        when().get(Web.API + "/orders/exists/100").then()
                .statusCode(200)
                .body(is("false"));
    }

    @Test
    void testFindByCustomerId() {
        when().get(Web.API + "/orders/customer/1").then()
                .statusCode(200)
                .body(containsString("jason.bourne@mail.hello"));
    }

    @Test
    void testNotFoundAfterDeleted() {
        when().get(Web.API + "/orders/exists/2").then()
                .statusCode(200)
                .body(is("true"));

        when().delete(Web.API + "/orders/2").then()
                .statusCode(204);

        when().get(Web.API + "/orders/exists/2").then()
                .statusCode(200)
                .body(is("false"));
    }

    @Test
    void testNotFoundById() {
        when().get(Web.API + "/orders/100").then()
                .statusCode(204)
                .body(emptyOrNullString());
    }

}
