package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
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
import java.util.Base64;
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
        // En vez de hacer mav = new ModelAndView(...); y mav.addObject("form", userForm), puedo simplemente
        // agregar al parámetro userForm el @ModelAttribute() con el nombre de atributo a usar, y cuando retorne va a
        // automáticamente agregar al ModelAndView retornado ese objeto como atributo con nombre "form".
        return new ModelAndView("views/publish");
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ModelAndView publish(@Valid @ModelAttribute("publishForm") final PublishForm publishForm,
                                final BindingResult errors,
                                @RequestParam("imageFile") MultipartFile imageFile) {
        if (errors.hasErrors()) {
            System.out.println("errors!!!");
            return publishForm(publishForm);
        }

        Neighborhood nh = nhs.createNeighborhood(publishForm.getNeighborhood());
        Neighbor n = ns.createNeighbor(publishForm.getEmail(),publishForm.getName(), publishForm.getSurname(), nh.getNeighborhoodId());

        System.out.println("ASssssssssssssss");
        Post p = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Convert the image to base64
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                // Set the base64-encoded image data in the tournamentForm
                p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), n.getNeighborId(), 1, base64Image);
            } catch (IOException e) {
                System.out.println("imagen't");
            }
        }

        System.out.println(nh);
        System.out.println(n);
        System.out.println(p);

        return new ModelAndView("views/index");
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

    @RequestMapping( "/{id:\\d+}")
//    @RequestMapping("/post")
    public ModelAndView viewPost(@PathVariable(value = "id") int postId) {
        ModelAndView mav = new ModelAndView("views/post");

            // TO-DO: add no post exception and better error handling!

            Optional<Post> optionalPost = ps.findPostById(postId);
            mav.addObject("post", optionalPost.isPresent()? optionalPost.get() : new RuntimeException());

            Optional<List<Comment>> optionalComments = cs.findCommentsByPostId(postId);
            mav.addObject("comments", optionalComments.isPresent()? optionalComments.get() : new RuntimeException());

            Optional<List<Tag>> optionalTags = ts.findTags(postId);
            System.out.println(optionalTags);
            mav.addObject("tags", optionalTags.isPresent()? optionalTags.get() : new RuntimeException());

        return mav;
    }

    // ------------------------------------- TEST --------------------------------------

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView test() {
        System.out.println(cs.create("lovely", 1, 1));
        System.out.println(LocalDate.now());
        return new ModelAndView("views/index");
    }


}
