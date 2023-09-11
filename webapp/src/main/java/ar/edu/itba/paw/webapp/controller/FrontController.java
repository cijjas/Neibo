package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.form.PublishForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Controller
public class FrontController {

    private final NeighborService us;
    private final PostService ps;
    private final NeighborService ns;
    private final NeighborhoodService nhs;
    private final CommentService cs;
    private final TagService ts;
    private final ChannelService chs;
    @Autowired
    public FrontController(final NeighborService us, // remove eventually
                           final PostService ps,
                           final NeighborService ns,
                           final NeighborhoodService nhs,
                           final CommentService cs,
                           final TagService ts,
                           final ChannelService chs) {
        this.us = us;
        this.ps = ps;
        this.ns = ns;
        this.nhs = nhs;
        this.cs = cs;
        this.ts = ts;
        this.chs = chs;
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
                    postList = ps.getAllPostsByDate("asc");
                    break;
                case "datedesc":
                    postList = ps.getAllPostsByDate("desc");
                    break;
                default:
                    if (sortBy.startsWith("tag")) {
                        String tag = sortBy.substring(3); // Extract the tag part
                        postList = ps.getAllPostsByTag(tag);
                    }
            }
        }else {
            postList = ps.getAllPosts(); // Default sorting
        }

        System.out.println(postList);

        final ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getAllTags());
        mav.addObject("postList", postList);

        return mav;
    }

    // ------------------------------------- REGISTER --------------------------------------
    // ------------------------------------- DEPRECATE --------------------------------------

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView registerForm() {
        return new ModelAndView("views/register");
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
        Neighbor n = ns.findNeighborByMail(publishForm.getEmail()).orElse(null);
        if(n == null){
            n = ns.createNeighbor(publishForm.getEmail(),publishForm.getName(), publishForm.getSurname(), nh.getNeighborhoodId());
        }

        Post p = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Convert the image to base64
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                // Set the base64-encoded image data in the tournamentForm
                p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), n.getNeighborId(), 1, base64Image);
            } catch (IOException e) {
                System.out.println("Issue uploading the image");
                // Should go to an error page!
            }
        } else {
            p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), n.getNeighborId(), 1, null);
        }

        System.out.println(nh);
        System.out.println(n);
        System.out.println(p);


        final ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getAllTags());
        mav.addObject("postList", ps.getAllPosts());

        return mav;
    }

    // ------------------------------------- POSTS --------------------------------------

    @RequestMapping("/posts")
    public ModelAndView posts() {
        System.out.println("moshi moshi");
        List<Post> postList = ps.getAllPosts();
        System.out.println(postList);
        final ModelAndView mav = new ModelAndView("views/posts");
        mav.addObject("postList", postList);
        return mav;
    }


    @RequestMapping( value ="/posts/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView viewPost(@PathVariable(value = "id") int postId, @ModelAttribute("commentForm") final CommentForm commentForm) {
        ModelAndView mav = new ModelAndView("views/post");

        Optional<Post> optionalPost = ps.findPostById(postId);
        mav.addObject("post", optionalPost.orElseThrow(() -> new RuntimeException("Post not found")));

        Optional<List<Comment>> optionalComments = cs.findCommentsByPostId(postId);
        mav.addObject("comments", optionalComments.orElse(Collections.emptyList()));

        Optional<List<Tag>> optionalTags = ts.findTags(postId);
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
        Comment c = cs.create(commentForm.getComment(), n.getNeighborId(), postId);

        System.out.println("testing: comment - " + c);

        return new ModelAndView("redirect:/posts/" + postId); // Redirect to the "posts" page
//        return new ModelAndView("views/posts/" + postId);
    }

    // ------------------------------------- TEST --------------------------------------

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView test() {
        return new ModelAndView("views/index");
    }
}
