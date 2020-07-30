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
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
public class OrderItemResourceTest {

    private static String PREFIX = "";

    @BeforeAll
    static void init() {
        if ("prod".equalsIgnoreCase(ProfileManager.getActiveProfile())) {
            PREFIX = "/api";
        }
    }

    @Test
    void testFindByOrderId() {
        get(PREFIX + "/order-items/order/1").then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testFindById() {
        get(PREFIX + "/order-items/1").then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testCreate() {
        var totalPrice = get(PREFIX + "/orders/3").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        var requestParams = new HashMap<>();
        requestParams.put("quantity", 1);
        requestParams.put("productId", 3);
        requestParams.put("orderId", 3);

        assertThat(totalPrice).isEqualTo(0);

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post(PREFIX + "/order-items/")
                .then()
                .statusCode(OK.getStatusCode());

        totalPrice = get(PREFIX + "/orders/3").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(totalPrice).isEqualTo(1999);
    }

    @Test
    void testDelete() {
        var totalPrice = get(PREFIX + "/orders/1").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(totalPrice).isEqualTo(999);

        delete(PREFIX + "/order-items/1").then()
                .statusCode(NO_CONTENT.getStatusCode());

        totalPrice = get(PREFIX + "/orders/1").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(totalPrice).isEqualTo(0);
    }
}
