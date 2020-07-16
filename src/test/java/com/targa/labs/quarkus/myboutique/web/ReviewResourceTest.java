package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
class ReviewResourceTest {

    @Test
    void testFindAllByProduct() {
        when().get("/reviews/product/1").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(2))
                .body(containsString("id"))
                .body(containsString("title"))
                .body(containsString("rating"))
                .body(containsString("description"));
    }

    @Test
    void testFindById() {
        when().get("/reviews/2").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("id"))
                .body(containsString("title"))
                .body(containsString("rating"))
                .body(containsString("description"));
    }

    @Test
    void testCreate() {
        Integer count = when().get("/reviews/product/3").then()
                .extract()
                .body()
                .path("size()");

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("description", "Wonderful laptop !");
        requestParams.put("rating", 5);
        requestParams.put("title", "Must have for every developer");

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/reviews/product/3")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("id"))
                .body(containsString("Wonderful laptop !"));

        when().get("/reviews/product/3").then()
                .body("size()", is(count + 1));
    }

    @Test
    void testDelete() {
        Integer count = when().get("/reviews/product/2").then()
                .extract()
                .body()
                .path("size()");

        when().delete("/reviews/3").then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().get("/reviews/product/2").then()
                .body("size()", is(count - 1));
    }

    @Test
    void testReviewsDeletedWhenProductIsDeleted() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("name", "Dell G5");
        requestParams.put("description", "Best gaming laptop from Dell");
        requestParams.put("price", 1490);
        requestParams.put("status", "AVAILABLE");
        requestParams.put("salesCounter", 0);
        requestParams.put("categoryId", 2);

        Map<String, Object> response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/products")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        Assert.assertNotNull(response.get("id"));

        Integer newProductID = (Integer) response.get("id");

        requestParams = new HashMap<>();
        requestParams.put("description", "Wonderful laptop !");
        requestParams.put("rating", 5);
        requestParams.put("title", "Must have for every developer");

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/reviews/product/" + newProductID)
                .then()
                .statusCode(OK.getStatusCode());

        given().delete("/products/" + newProductID).then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().get("/reviews/product/" + newProductID).then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(0));
    }
}
