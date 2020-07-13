package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.common.Web;
import com.targa.labs.quarkus.myboutique.service.OrderItemService;
import com.targa.labs.quarkus.myboutique.web.dto.OrderItemDto;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Nebrass Lamouchi
 */
@ApplicationScoped
@Path(Web.API + "/order-items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderItemResource {

    private final OrderItemService itemService;

    public OrderItemResource(OrderItemService itemService) {
        this.itemService = itemService;
    }

    @GET
    public List<OrderItemDto> findAll() {
        return this.itemService.findAll();
    }

    @GET
    @Path("/{id}")
    public OrderItemDto findById(@PathParam("id") Long id) {
        return this.itemService.findById(id);
    }

    @POST
    public OrderItemDto create(OrderItemDto orderItemDto) {
        return this.itemService.create(orderItemDto);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        this.itemService.delete(id);
    }
}
