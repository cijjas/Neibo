package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.dto.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/posts")
@Component
public class PostController {
    @Autowired
    private PostService ps;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listPosts(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("channel") final String channel,
            @QueryParam("tags") final List<String> tags,
            @QueryParam("poststatus") @DefaultValue("none") final String postStatus,
            @QueryParam("user") @DefaultValue("0") final Long userId) {
        final List<Post> posts = ps.getPostsByCriteria(channel, page, size, tags, neighborhoodId, PostStatus.valueOf(postStatus));
        final List<PostDto> postsDto = posts.stream()
                .map(p -> PostDto.fromPost(p, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/posts";
        PostStatus pstat = PostStatus.valueOf(postStatus);
        int totalPostsPages = ps.getTotalPages(channel, size, tags, neighborhoodId, pstat, userId);
        Link[] links = createPaginationLinks(baseUri, page, size, totalPostsPages);

        return Response.ok(new GenericEntity<List<PostDto>>(postsDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findPostById(@PathParam("id") final long id) {
        Optional<Post> post = ps.findPostById(id);
        if (!post.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(PostDto.fromPost(post.get(), uriInfo)).build();
    }

}
