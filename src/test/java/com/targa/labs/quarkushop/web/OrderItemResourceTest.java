package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class OrderItemResourceTest {

    @Test
    void testFindByOrderId() {
        get("/order-items/order/1").then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testFindById() {
        get("/order-items/1").then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testCreate() {
        var totalPrice = get("/orders/3").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        var requestParams = new HashMap<>();
        requestParams.put("quantity", 1);
        requestParams.put("productId", 3);
        requestParams.put("orderId", 3);

        assertThat(totalPrice).isZero();

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode());

        totalPrice = get("/orders/3").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(totalPrice).isEqualTo(1999);
    }

    @Test
    void testDelete() {
        var totalPrice = get("/orders/1").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(totalPrice).isEqualTo(999);

        delete("/order-items/1").then()
                .statusCode(NO_CONTENT.getStatusCode());

        totalPrice = get("/orders/1").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(totalPrice).isZero();
    }
}
