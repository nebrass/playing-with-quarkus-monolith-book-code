package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.domain.enumeration.CartStatus;
import com.targa.labs.quarkushop.domain.enumeration.OrderStatus;
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

        when().get("/orders").then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThanOrEqualTo(2))
                .body(containsString("totalPrice"))
                .body(containsString("999.00"))
                .body(containsString("status"))
                .body(containsString("CREATION"));
    }

    @Test
    void testExistsById() {
        when().get("/orders/exists/1").then()
                .statusCode(OK.getStatusCode())
                .body(is("true"));

        when().get("/orders/exists/100").then()
                .statusCode(OK.getStatusCode())
                .body(is("false"));
    }

    @Test
    void testFindByCustomerId() {
        when().get("/orders/customer/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("jason.bourne@mail.hello"));
    }

    @Test
    void testCreateOrder() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        Integer newCustomerId = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams).post("/customers").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        Integer newCartId = when().post("/carts/customer/" + newCustomerId).then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        requestParams.clear();

        Map<String, Object> cart = new HashMap<>();
        cart.put("id", newCartId);

        requestParams.put("cart", cart);

        Map<String, String> address = new HashMap<>();
        address.put("address1", "413 Circle Drive");
        address.put("city", "Washington, DC");
        address.put("country", "US");
        address.put("postcode", "20004");

        requestParams.put("shipmentAddress", address);

        Map<String, Object> orderResponse = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams).post("/orders").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        Integer newOrderId = (Integer) orderResponse.get("id");
        assertThat(newOrderId).isNotNull();

        assertThat(orderResponse.get("status")).isEqualTo(OrderStatus.CREATION.name());
        assertThat(orderResponse.get("totalPrice")).isEqualTo(0);

        Map<String, Object> cartResponse = (Map) orderResponse.get("cart");
        assertThat(cartResponse.get("id")).isNotNull();
        assertThat(cartResponse.get("status")).isEqualTo(CartStatus.NEW.name());

        Map<String, String> customerResponse = (Map) cartResponse.get("customer");
        assertThat(customerResponse.get("email")).isEqualTo("call.saul@mail.com");
        assertThat(customerResponse.get("firstName")).isEqualTo("Saul");
        assertThat(customerResponse.get("lastName")).isEqualTo("Berenson");

        when().delete("/orders/" + newOrderId).then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().delete("/carts/" + newCartId).then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().delete("/customers/" + newCustomerId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFailCreateOrderWhenCartIdIsNotValid() {
        Map<String, Object> requestParams = new HashMap<>();

        Map<String, Object> cart = new HashMap<>();
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
        when().get("/orders/exists/2").then()
                .statusCode(OK.getStatusCode())
                .body(is("true"));

        when().delete("/orders/2").then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().get("/orders/exists/2").then()
                .statusCode(OK.getStatusCode())
                .body(is("false"));
    }

    @Test
    void testNotFoundById() {
        when().get("/orders/100").then()
                .statusCode(NO_CONTENT.getStatusCode())
                .body(emptyOrNullString());
    }

}
