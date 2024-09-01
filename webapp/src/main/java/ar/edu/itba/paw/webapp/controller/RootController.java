package ar.edu.itba.paw.webapp.controller;

import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
@Component
public class RootController {

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getTreeStructure() {
        // Create the corrected tree structure
        JsonObject tree = createTreeStructure();

        // Build the Response object
        return Response.ok(tree.toString())
                .header("Content-Type", "application/json")
                .build();
    }

    private JsonObject createTreeStructure() {
        // Build the corrected tree structure using javax.json
        JsonObjectBuilder treeBuilder = Json.createObjectBuilder();

        // Neighborhoods
        JsonObjectBuilder neighborhoodsBuilder = Json.createObjectBuilder();
        JsonObjectBuilder idBuilder = Json.createObjectBuilder();
        idBuilder.add("tags", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));
        idBuilder.add("amenities", Json.createObjectBuilder().add("id", Json.createObjectBuilder().add("availability", Json.createObjectBuilder())));
        idBuilder.add("products", Json.createObjectBuilder().add("id", Json.createObjectBuilder().add("requests", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))));
        idBuilder.add("posts", Json.createObjectBuilder().add("id", Json.createObjectBuilder().add("comments", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))));
        idBuilder.add("users", Json.createObjectBuilder().add("id", Json.createObjectBuilder().add("purchases", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))));
        idBuilder.add("resources", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));
        idBuilder.add("contacts", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));
        idBuilder.add("events", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));
        idBuilder.add("channels", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));

        neighborhoodsBuilder.add("id", idBuilder);
        treeBuilder.add("neighborhoods", neighborhoodsBuilder);

        // Other sections
        treeBuilder.add("images", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));
        treeBuilder.add("professions", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));

        // Workers
        JsonObjectBuilder workersBuilder = Json.createObjectBuilder();
        workersBuilder.add("id", Json.createObjectBuilder().add("reviews", Json.createObjectBuilder().add("id", Json.createObjectBuilder())));
        treeBuilder.add("workers", workersBuilder);

        treeBuilder.add("shifts", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));
        treeBuilder.add("likes", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));
        treeBuilder.add("departments", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));
        treeBuilder.add("times", Json.createObjectBuilder().add("id", Json.createObjectBuilder()));

        return treeBuilder.build();
    }
}