package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.common.Web;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class CustomerResourceTest {

    @Test
    void testAll() {
        when().get(Web.API + "/customers").then()
                .statusCode(200)
                .body("size()", is(3))
                .body(containsString("jason.bourne@mail.hello"))
                .body(containsString("homer.simpson@mail.hello"))
                .body(containsString("peter.quinn@mail.hello"));
    }

    @Test
    void testAllActiveUsers() {
        when().get(Web.API + "/customers/active").then()
                .statusCode(200)
                .body(containsString("Simpson"))
                .body(containsString("Homer"));
    }

    @Test
    void testAllInactiveUsers() {
        when().get(Web.API + "/customers/inactive").then()
                .statusCode(200)
                .body(containsString("peter.quinn@mail.hello"));
    }

    @Test
    void testFindById() {
        when().get(Web.API + "/customers/1").then()
                .statusCode(200)
                .body(containsString("Jason"))
                .body(containsString("Bourne"));
    }

    @Test
    void testDeleteThenCustomerIsDisabled() {
        when().get(Web.API + "/customers/active").then()
                .statusCode(200)
                .body("size()", is(2));

        when().delete(Web.API + "/customers/1").then()
                .statusCode(204);

        when().get(Web.API + "/customers/active").then()
                .statusCode(200)
                .body("size()", is(1));

        when().get(Web.API + "/customers/inactive").then()
                .statusCode(200)
                .body("size()", is(2))
                .body(containsString("Jason"))
                .body(containsString("Bourne"));
    }
}
