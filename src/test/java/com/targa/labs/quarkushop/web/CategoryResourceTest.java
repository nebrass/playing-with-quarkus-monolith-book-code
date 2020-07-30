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
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
public class CategoryResourceTest {

    private static String PREFIX = "";

    @BeforeAll
    static void init() {
        if ("prod".equalsIgnoreCase(ProfileManager.getActiveProfile())) {
            PREFIX = "/api";
        }
    }

    @Test
    void testFindAll() {
        get(PREFIX + "/categories").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(2))
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"))
                .body(containsString("Computers and Laptops"))
                .body(containsString("PC"));
    }

    @Test
    void testFindById() {
        get(PREFIX + "/categories/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"));
    }

    @Test
    void testFindProductsByCategoryId() {
        get(PREFIX + "/categories/1/products").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("categoryId"))
                .body(containsString("description"))
                .body(containsString("id"))
                .body(containsString("name"))
                .body(containsString("price"))
                .body(containsString("reviews"))
                .body(containsString("salesCounter"))
                .body(containsString("status"));
    }

    @Test
    void testCreate() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Cars");
        requestParams.put("description", "New and used cars");

        var response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post(PREFIX + "/categories")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertNotNull(response.get("id"));

        var newProductID = (Integer) response.get("id");

        get(PREFIX + "/categories/" + newProductID).then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Cars"))
                .body(containsString("New and used cars"));

        delete(PREFIX + "/categories/" + newProductID).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDelete() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Home");
        requestParams.put("description", "New and old homes");

        var response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post(PREFIX + "/categories")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertNotNull(response.get("id"));

        var newProductID = (Integer) response.get("id");

        get(PREFIX + "/categories/" + newProductID).then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Home"))
                .body(containsString("New and old homes"));

        delete(PREFIX + "/categories/" + newProductID).then()
                .statusCode(NO_CONTENT.getStatusCode());

        get(PREFIX + "/categories/" + newProductID).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
}
