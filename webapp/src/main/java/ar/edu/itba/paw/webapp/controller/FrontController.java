package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.exceptions.*;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.form.PublishForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.validation.Valid;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;


@Controller
public class FrontController {

    private final PostService ps;
    private final NeighborService ns;
    private final NeighborhoodService nhs;
    private final CommentService cs;
    private final TagService ts;
    private final ChannelService chs;
    private final SubscriptionService ss;
    private final CategorizationService cas;
    @Autowired
    public FrontController(final PostService ps,
                           final NeighborService ns,
                           final NeighborhoodService nhs,
                           final CommentService cs,
                           final TagService ts,
                           final ChannelService chs,
                           final SubscriptionService ss,
                           final CategorizationService cas)
    {
        this.ps = ps;
        this.ns = ns;
        this.nhs = nhs;
        this.cs = cs;
        this.ts = ts;
        this.chs = chs;
        this.ss = ss;
        this.cas = cas;
    }

    // ------------------------------------- FEED --------------------------------------

    @RequestMapping("/")
    public ModelAndView index(@RequestParam(value = "sortBy", required = false) String sortBy) {
        List<Post> postList = null;

        // It would be nice to create an enum of the tags but I cant create an enum dynamically, meaning I cant retrieve the tags and then create an enum
        // Maybe I can create an Enum for ASC and DESC, but it is kinda overkill just for some performance


        if ( sortBy != null ){
            switch (sortBy) {
                case "dateasc":
                    postList = ps.getPostsByDate("asc");
                    break;
                case "datedesc":
                    postList = ps.getPostsByDate("desc");
                    break;
                default:
                    if (sortBy.startsWith("tag")) {
                        String tag = sortBy.substring(3); // Extract the tag part
                        postList = ps.getPostsByTag(tag);
                    }
            }
        }else {
            postList = ps.getPosts(); // Default sorting
        }


        final ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags());
        mav.addObject("postList", postList);

        return mav;
    }

    // ------------------------------------- PUBLISH --------------------------------------


    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public ModelAndView publishForm(@ModelAttribute("publishForm") final PublishForm publishForm) {


        return new ModelAndView("views/publish");
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ModelAndView publish(@Valid @ModelAttribute("publishForm") final PublishForm publishForm,
                                final BindingResult errors,
                                @RequestParam("imageFile") MultipartFile imageFile) {
        if (errors.hasErrors()) {
            return publishForm(publishForm);
        }

        Neighborhood nh = nhs.createNeighborhood(publishForm.getNeighborhood());
        Neighbor n = ns.createNeighbor(publishForm.getEmail(),publishForm.getName(), publishForm.getSurname(), nh.getNeighborhoodId());


        Post p = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Convert the image to base64
                byte[] imageBytes = imageFile.getBytes();
                p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), n.getNeighborId(), 1, imageBytes);
                // Set the base64-encoded image data in the tournamentForm
            } catch (IOException e) {
                System.out.println("Issue uploading the image");
                // Should go to an error page!
            }
        } else {
            p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), n.getNeighborId(), 1, null);
        }
        assert p != null;
        ts.createTagsAndCategorizePost(p.getPostId(), publishForm.getTags());

        final ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags());
        mav.addObject("postList", ps.getPosts());

        return mav;
    }

    @RequestMapping(value = "/publish_admin", method = RequestMethod.GET)
    public ModelAndView publishAdminForm(@ModelAttribute("publishForm") final PublishForm publishForm) {


        return new ModelAndView("views/publish_admin");
    }
    @RequestMapping(value = "/publish_admin", method = RequestMethod.POST)
    public ModelAndView publishAdmin(@Valid @ModelAttribute("publishForm") final PublishForm publishForm,
                                final BindingResult errors,
                                @RequestParam("imageFile") MultipartFile imageFile) {
        if (errors.hasErrors()) {
            return publishForm(publishForm);
        }

        Neighborhood nh = nhs.createNeighborhood(publishForm.getNeighborhood());
        Neighbor n = ns.createNeighbor(publishForm.getEmail(),publishForm.getName(), publishForm.getSurname(), nh.getNeighborhoodId());


        Post p = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Convert the image to base64
                byte[] imageBytes = imageFile.getBytes();
                p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), n.getNeighborId(), 1, imageBytes);
                // Set the base64-encoded image data in the tournamentForm
            } catch (IOException e) {
                System.out.println("Issue uploading the image");
                // Should go to an error page!
            }
        } else {
            p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), n.getNeighborId(), 1, null);
        }

        final ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags());
        mav.addObject("postList", ps.getPosts());

        return mav;
    }

    // ------------------------------------- POSTS --------------------------------------

    @RequestMapping( value ="/posts/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView viewPost(@PathVariable(value = "id") int postId, @ModelAttribute("commentForm") final CommentForm commentForm) {
        ModelAndView mav = new ModelAndView("views/post");

        Optional<Post> optionalPost = ps.findPostById(postId);
        mav.addObject("post", optionalPost.orElseThrow(PostNotFoundException::new));

        Optional<List<Comment>> optionalComments = cs.findCommentsByPostId(postId);
        mav.addObject("comments", optionalComments.orElse(Collections.emptyList()));

        Optional<List<Tag>> optionalTags = ts.findTagsByPostId(postId);
        List<Tag> tags = optionalTags.orElse(Collections.emptyList());
        mav.addObject("tags", tags);

        mav.addObject("commentForm", commentForm);

        return mav;
    }

    @RequestMapping( value = "/posts/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView viewPost(@PathVariable(value = "id") int postId, @Valid @ModelAttribute("commentForm") final CommentForm commentForm,
                                 final BindingResult errors) {
        if (errors.hasErrors()) {
            System.out.println("comment tiene errores");
            return viewPost(postId, commentForm);
        }

        Neighborhood nh = nhs.createNeighborhood(commentForm.getNeighborhood());
        Neighbor n = ns.createNeighbor(commentForm.getEmail(), commentForm.getName(), commentForm.getSurname(), nh.getNeighborhoodId());
        Comment c = cs.createComment(commentForm.getComment(), n.getNeighborId(), postId);


        return new ModelAndView("redirect:/posts/" + postId); // Redirect to the "posts" page
//        return new ModelAndView("views/posts/" + postId);
    }

    // ------------------------------------- RESOURCES --------------------------------------

    @RequestMapping(value = "/postImage/{imageId}")
    @ResponseBody
    public byte[] imageRetriever(@PathVariable long imageId)  {
        Optional<Post> post = ps.findPostById(imageId);
        return post.map(Post::getImageFile).orElseThrow(ResourceNotFoundException::new);
    }

    // ------------------------------------- EXCEPTIONS --------------------------------------
    @ExceptionHandler({NeighborNotFoundException.class, NeighborhoodNotFoundException.class, PostNotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView notFound(NeiboException ex) {
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "404");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler({DuplicatedNeighbor.class, DuplicatedNeighborhood.class, DuplicatedChannel.class, DuplicatedCategory.class, DuplicatedSubscription.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView duplicated(NeiboException ex) {
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "409"); // 409 = Conflict
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    // ------------------------------------- TEST --------------------------------------

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView test() {

        return new ModelAndView("views/index");
    }

    @RequestMapping(value = "/testDuplicatedException", method = RequestMethod.GET)
    public ModelAndView testDuplicatedException() {
        throw new DuplicatedNeighbor();
    }

    @RequestMapping(value = "/testNotFoundException", method = RequestMethod.GET)
    public ModelAndView testNotFoundException() {
        throw new NeighborhoodNotFoundException();
    }


}
