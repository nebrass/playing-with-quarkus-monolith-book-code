package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.domain.enumeration.PaymentStatus;
import com.targa.labs.quarkus.myboutique.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
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
        List<Object> payments = when().get("/payments").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList("$");

        assertNotNull(payments);
    }

    @Test
    void testFindById() {
        Map<String, Object> response = when().get("/payments/2").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertEquals(response.get("id"), 2);
        assertEquals(response.get("status"), ACCEPTED.name());
        assertEquals(response.get("paypalPaymentId"), "paymentId");
        assertEquals(response.get("orderId"), 1);
    }

    @Test
    void testCreate() {
        Map<String, Object> requestParams = new HashMap<>();

        requestParams.put("orderId", 3);
        requestParams.put("paypalPaymentId", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        Map<String, Object> response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        Integer createdPaymentId = (Integer) response.get("id");
        assertThat(createdPaymentId).isGreaterThanOrEqualTo(1);
        assertThat(response.get("orderId")).isEqualTo(3);
        assertThat(response.get("paypalPaymentId")).isEqualTo("anotherPaymentId");
        assertThat(response.get("status")).isEqualTo(PaymentStatus.PENDING.name());
    }

    @Test
    void testDelete() {
        Map<String, Object> requestParams = new HashMap<>();

        requestParams.put("orderId", 3);
        requestParams.put("paypalPaymentId", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        Long createdPaymentId = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        when().delete("/payments/" + createdPaymentId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFindByRangeMax() {
        when().get("/payments/price/800").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(1))
                .body(containsString("orderId"))
                .body(containsString("paypalPaymentId"))
                .body(containsString("status"));
    }
}
