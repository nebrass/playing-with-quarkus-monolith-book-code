package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.domain.enums.CartStatus;
import com.targa.labs.quarkushop.domain.enums.OrderStatus;
import com.targa.labs.quarkushop.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class OrderResourceTest {

    @Test
    void testAll() {
        get("/orders").then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThanOrEqualTo(2))
                .body(containsString("totalPrice"))
                .body(containsString("999.00"))
                .body(containsString("status"))
                .body(containsString("CREATION"));
    }

    @Test
    void testExistsById() {
        get("/orders/exists/1").then()
                .statusCode(OK.getStatusCode())
                .body(is("true"));

        get("/orders/exists/100").then()
                .statusCode(OK.getStatusCode())
                .body(is("false"));
    }

    @Test
    void testFindByCustomerId() {
        get("/orders/customer/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("jason.bourne@mail.hello"));
    }

    @Test
    void testCreateOrder() {
        var requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        var newCustomerId = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams).post("/customers").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        var newCartId = post("/carts/customer/" + newCustomerId).then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        requestParams.clear();

        var cart = new HashMap<>();
        cart.put("id", newCartId);

        requestParams.put("cart", cart);

        var address = new HashMap<>();
        address.put("address1", "413 Circle Drive");
        address.put("city", "Washington, DC");
        address.put("country", "US");
        address.put("postcode", "20004");

        requestParams.put("shipmentAddress", address);

        var orderResponse = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams).post("/orders").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        var newOrderId = (Integer) orderResponse.get("id");
        assertThat(newOrderId).isNotNull();

        assertThat(orderResponse).containsEntry("status", OrderStatus.CREATION.name());
        assertThat((Integer) orderResponse.get("totalPrice")).isZero();

        var cartResponse = (Map<String, Object>) orderResponse.get("cart");
        assertThat(cartResponse.get("id")).isNotNull();
        assertThat(cartResponse).containsEntry("status", CartStatus.NEW.name());

        var customerResponse = (Map<String, Object>) cartResponse.get("customer");
        assertThat(customerResponse).containsEntry("email", "call.saul@mail.com")
                .containsEntry("firstName", "Saul")
                .containsEntry("lastName", "Berenson");

        delete("/orders/" + newOrderId).then()
                .statusCode(NO_CONTENT.getStatusCode());

        delete("/carts/" + newCartId).then()
                .statusCode(NO_CONTENT.getStatusCode());

        delete("/customers/" + newCustomerId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFailCreateOrderWhenCartIdIsNotValid() {
        var requestParams = new HashMap<>();

        var cart = new HashMap<>();
        cart.put("id", 99999);

        requestParams.put("cart", cart);

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/orders").then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body(containsString("Internal Server Error"));
    }

    @Test
    void testNotFoundAfterDeleted() {
        get("/orders/exists/2").then()
                .statusCode(OK.getStatusCode())
                .body(is("true"));

        delete("/orders/2").then()
                .statusCode(NO_CONTENT.getStatusCode());

        get("/orders/exists/2").then()
                .statusCode(OK.getStatusCode())
                .body(is("false"));
    }

    @Test
    void testNotFoundById() {
        get("/orders/100").then()
                .statusCode(NO_CONTENT.getStatusCode())
                .body(emptyOrNullString());
    }
}
