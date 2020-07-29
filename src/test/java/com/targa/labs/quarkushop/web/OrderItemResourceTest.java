package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class OrderItemResourceTest {

    @Test
    void testFindByOrderId() {
        when().get("/order-items/order/1").then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testFindById() {
        when().get("/order-items/1").then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testCreate() {
        Double totalPrice = when().get("/orders/3").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("quantity", 1);
        requestParams.put("productId", 3);
        requestParams.put("orderId", 3);

        assertThat(totalPrice).isEqualTo(0);

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode());

        totalPrice = when().get("/orders/3").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(totalPrice).isEqualTo(1999);
    }

    @Test
    void testDelete() {
        double totalPrice = when().get("/orders/1").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(totalPrice).isEqualTo(999);

        when().delete("/order-items/1").then()
                .statusCode(NO_CONTENT.getStatusCode());

        totalPrice = when().get("/orders/1").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(totalPrice).isEqualTo(0);
    }
}
