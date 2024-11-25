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
        JsonObjectBuilder treeBuilder = Json.createObjectBuilder();

        // First layer entities
        addEndpoints(treeBuilder, "affiliations", "departments", "images", "neighborhoods",
                "languages", "product-statuses", "professions", "post-statuses", "shifts",
                "shift-statuses", "transaction-types", "user-roles", "worker-roles", "workers",
                "request-status");

        // Second layer: Nested within neighborhoods
        treeBuilder.add("neighborhoods", Json.createObjectBuilder()
                .add("id", Json.createObjectBuilder()
                        .add("amenities", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))
                        .add("bookings", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))
                        .add("channels", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))
                        .add("contacts", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))
                        .add("events", Json.createObjectBuilder()
                                .add("id", Json.createObjectBuilder()
                                        .add("attendance", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))))
                        .add("posts", Json.createObjectBuilder()
                                .add("id", Json.createObjectBuilder()
                                        .add("comments", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))))
                        .add("products", Json.createObjectBuilder()
                                .add("id", Json.createObjectBuilder()
                                        .add("inquiries", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))))
                        .add("requests", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))
                        .add("resources", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))
                        .add("tags", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))
                        .add("users", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))));

        // Second layer: Nested within workers
        treeBuilder.add("workers", Json.createObjectBuilder()
                .add("id", Json.createObjectBuilder()
                        .add("reviews", Json.createObjectBuilder().add("id", Json.createObjectBuilder()))));

        return treeBuilder.build();
    }

    private void addEndpoints(JsonObjectBuilder treeBuilder, String... endpoints) {
        for (String endpoint : endpoints) {
            treeBuilder.add(endpoint, Json.createObjectBuilder().add("id", Json.createObjectBuilder()));
        }
    }
}
