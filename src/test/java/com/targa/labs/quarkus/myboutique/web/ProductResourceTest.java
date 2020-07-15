package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
public class ProductResourceTest {

    @Test
    public void testFindAll() {
        when().get("/products").then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThan(0))
                .body(containsString("name"))
                .body(containsString("description"))
                .body(containsString("price"))
                .body(containsString("categoryId"));
    }

    @Test
    public void testFindById() {
        when().get("/products/3").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("MacBook Pro 13"))
                .body(containsString("1999.00"))
                .body(containsString("categoryId"))
                .body(containsString("AVAILABLE"));
    }

    @Test
    public void testCreate() {
        Long count = when().get("/products/count").then()
                .extract()
                .body()
                .as(Long.class);

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("name", "Dell G5");
        requestParams.put("description", "Best gaming laptop from Dell");
        requestParams.put("price", 1490);
        requestParams.put("status", "AVAILABLE");
        requestParams.put("salesCounter", 0);
        requestParams.put("categoryId", 2);

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/products")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("id"))
                .body(containsString("Dell G5"));

        when().get("/products/count").then()
                .body(containsString(String.valueOf(count + 1)));
    }

    @Test
    public void testDelete() {
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

        given().delete("/products/" + newProductID).then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().get("/products/" + newProductID).then()
                .statusCode(NO_CONTENT.getStatusCode())
                .body(is(emptyString()));
    }

    @Test
    public void testFindByCategoryId() {
        List<Long> ids = when().get("/products/category/1").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList("id", Long.class);

        assertThat(ids.size()).isEqualTo(2);
    }
}
