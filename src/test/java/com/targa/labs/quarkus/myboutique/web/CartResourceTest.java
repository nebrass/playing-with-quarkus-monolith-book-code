package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.domain.enumeration.CartStatus;
import com.targa.labs.quarkus.myboutique.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
import static org.hamcrest.Matchers.greaterThan;

@DisabledOnNativeImage
@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class CartResourceTest {

    private static final String INSERT_WRONG_CART_IN_DB =
            "insert into carts values (9999, current_timestamp, current_timestamp, 'NEW', 3)";

    private static final String DELETE_WRONG_CART_IN_DB =
            "delete from carts where id = 9999";

    @Inject
    DataSource dataSource;

    @Test
    void testFindAll() {
        when().get("/carts").then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThan(0));
    }

    @Test
    void testFindAllActiveCarts() {
        when().get("/carts/active").then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testGetActiveCartForCustomer() {
        when().get("/carts/customer/3").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Peter"));
    }

    @Test
    void testGetActiveCartForCustomerWhenThereAreTwoCartsInDB() {
        executeSql(INSERT_WRONG_CART_IN_DB);

        when().get("/carts/customer/3").then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body(containsString(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body(containsString("Many active carts detected !!!"));

        executeSql(DELETE_WRONG_CART_IN_DB);
    }

    @Test
    void testFindById() {
        when().get("/carts/3").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("status"))
                .body(containsString("NEW"));

        when().get("/carts/100").then()
                .statusCode(NO_CONTENT.getStatusCode())
                .body(emptyOrNullString());
    }

    @Test
    void testCreateCart() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        Integer newCustomerId = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams).post("/customers").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        Map<Object, Object> response = when().post("/carts/customer/" + newCustomerId).then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertThat(response.get("id")).isNotNull();
        assertThat(response).containsEntry("status", CartStatus.NEW.name());


        when().delete("/carts/" + response.get("id")).then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().delete("/customers/" + newCustomerId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFailCreateCartWhileHavingAlreadyActiveCart() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        //noinspection WrapperTypeMayBePrimitive Cant be primitive because null can be returned if the request fails
        Long newCustomerId = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams).post("/customers").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        Long newCartId = when().post("/carts/customer/" + newCustomerId).then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        when().post("/carts/customer/" + newCustomerId).then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body(containsString(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body(containsString("There is already an active cart"));

        assertThat(newCartId).isNotNull();

        when().delete("/carts/" + newCartId).then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().delete("/customers/" + newCustomerId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDelete() {
        when().get("/carts/active").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("NEW"));

        when().delete("/carts/1").then()
                .statusCode(NO_CONTENT.getStatusCode());

        when().get("/carts/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("CANCELED"));
    }

    private void executeSql(String insertWrongCartInDb) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(insertWrongCartInDb);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
