package com.targa.labs.quarkus.myboutique.web;

import com.targa.labs.quarkus.myboutique.common.Web;
import com.targa.labs.quarkus.myboutique.service.ReviewService;
import com.targa.labs.quarkus.myboutique.web.dto.ReviewDto;

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
@Path(Web.API + "/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

    private final ReviewService reviewService;

    public ReviewResource(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GET
    public List<ReviewDto> findAll() {
        return this.reviewService.findAll();
    }

    @GET
    @Path("/{id}")
    public ReviewDto findById(@PathParam("id") Long id) {
        return this.reviewService.findById(id);
    }

    @POST
    public ReviewDto create(ReviewDto reviewDto) {
        return this.reviewService.createDto(reviewDto);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        this.reviewService.delete(id);
    }
}
