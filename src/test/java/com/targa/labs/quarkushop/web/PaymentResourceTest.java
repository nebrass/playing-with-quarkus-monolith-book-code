package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.domain.enums.PaymentStatus;
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
import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class PaymentResourceTest {

    @Test
    void testFindAll() {
        var payments = get("/payments").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList("$");

        assertNotNull(payments);
    }

    @Test
    void testFindById() {
        var response = get("/payments/2").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertEquals(2, response.get("id"));
        assertEquals(ACCEPTED.name(), response.get("status"));
        assertEquals("paymentId", response.get("paypalPaymentId"));
        assertEquals(1, response.get("orderId"));
    }

    @Test
    void testCreate() {
        var requestParams = new HashMap<>();

        requestParams.put("orderId", 3);
        requestParams.put("paypalPaymentId", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        var response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        var createdPaymentId = (Integer) response.get("id");
        assertThat(createdPaymentId).isNotZero();
        assertThat(response).containsEntry("orderId", 3)
                .containsEntry("paypalPaymentId", "anotherPaymentId")
                .containsEntry("status", PaymentStatus.PENDING.name());
    }

    @Test
    void testDelete() {
        var requestParams = new HashMap<>();

        requestParams.put("orderId", 3);
        requestParams.put("paypalPaymentId", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        var createdPaymentId = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        delete("/payments/" + createdPaymentId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFindByRangeMax() {
        get("/payments/price/800").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(1))
                .body(containsString("orderId"))
                .body(containsString("paypalPaymentId"))
                .body(containsString("status"));
    }
}