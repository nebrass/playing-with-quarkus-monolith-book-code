package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class CategoryResourceTest {
    @Test
    void testFindAll() {
        when().get("/categories").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(2))
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"))
                .body(containsString("Computers and Laptops"))
                .body(containsString("PC"));
    }

    @Test
    void testFindById() {
        when().get("/categories/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"));
    }

    @Test
    void testFindProductsByCategoryId() {
        when().get("/categories/1/products").then()
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
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("name", "Cars");
        requestParams.put("description", "New and used cars");

        Map<String, Object> response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/categories")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        Assert.assertNotNull(response.get("id"));

        Integer newProductID = (Integer) response.get("id");

        when().get("/categories/" + newProductID).then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Cars"))
                .body(containsString("New and used cars"));

        when().delete("/categories/" + newProductID).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDelete() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("name", "Home");
        requestParams.put("description", "New and old homes");

        Map<String, Object> response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/categories")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        Assert.assertNotNull(response.get("id"));

        Integer newProductID = (Integer) response.get("id");

        when().get("/categories/" + newProductID).then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Home"))
                .body(containsString("New and old homes"));

        when().delete("/categories/" + newProductID).then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().get("/categories/" + newProductID).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
}
