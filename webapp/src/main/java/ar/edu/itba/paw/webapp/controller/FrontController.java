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

    // Spring permite hacer inyección de otras formas, no solo pasando instancia al constructor.
    // Estas otras incluyen:

    // Inyectar directamente a un campo:
    // @Autowired
    // private UserService us;

    // Inyectar a travez de un método setter:
    // @Autowired
    // public void setUserService(UserService us) { this.us = us; }



    // El RequestMapping se puede configurar para solo funcionar si el request tiene tal header, o es tal método HTTP,
    // o qué produce, etc.
    // @RequestMapping(value = "/", headers = "...", consumes = "...", method = "...", produces = "...")

    // También es posible especificar varias ubicaciones:
    // @RequestMapping(value = {"/", "/index.html"})

    // Por default, cualquier pedido sin importar headers o método HTTP es aceptado por el mapping:
    /*
    @RequestMapping("/")
    public ModelAndView index() {
        List<Post> postList = ps.getAllPosts();

        final ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("postList", postList);

        return mav;
    }

     */

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

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView registerForm() {
        return new ModelAndView("views/register");
    }

    // Una forma de atender requests es tomando un HttpServletRequest, que nos da mucho control sobre el request y
    // cómo respondemos:
    /*@RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(HttpServletRequest req) {
        final User user = us.createUser(req.getParameter("email"), req.getParameter("password"));

        final ModelAndView mav = new ModelAndView("helloworld/index");
        mav.addObject("user", user);

        return mav;
    }*/

    // Pero en general pedimos parámetros fijos y respondemos algo claro, entonces podemos hacer las cosas más simples
    // usando una sintaxis que nos deja especificar los parámetros del http request como parámetros del método:
//    @RequestMapping(value = "/register", method = RequestMethod.POST)
//    public ModelAndView register(
//            @RequestParam(value = "email", required = true) final String email,
//            @RequestParam(value = "password", required = true) final String password
//    ) {
//        // Los @RequestParam pueden ser int, long, etc. Y nos hace la conversión por nosotros :)
//        // Al @RequestParam también le podemos pasar "required = true", "default = 1234", etc.
//        final User user = us.createUser(email, password);
//
//        final ModelAndView mav = new ModelAndView("views/index");
//        mav.addObject("user", user);
//
//        return mav;
//    }

//    @RequestMapping("/post")
//    public ModelAndView postPage() {return new ModelAndView("views/post");}

    // @RequestMapping("/{id}") // El problema con este es que no pone restricciones al valor de "id"!
    /*
    @RequestMapping("/{id:\\d+}") // Antes aceptaba negativos, ahora no!
    // NOTAR: Si pones negativo o texto antes te tiraba 400 bad request, ahora te tira 404 not found.
    public ModelAndView profile(@PathVariable("id") final long userId) {
        final ModelAndView mav = new ModelAndView("views/profile");
        mav.addObject("user", us.findById(userId).orElseThrow(UserNotFoundException::new));
        return mav;
    }
    */

    // ----------------------------------------------------------------------------------------------------------------
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


    @RequestMapping("/posts")
    public ModelAndView posts() {
        System.out.println("moshi moshi");
        List<Post> postList = ps.getAllPosts();
        System.out.println(postList);
        final ModelAndView mav = new ModelAndView("views/posts");
        mav.addObject("postList", postList);
        return mav;
    }

//    // @RequestMapping("/{id}") // El problema con este es que no pone restricciones al valor de "id"!
//    @RequestMapping("/{id:\\d+}") // Antes aceptaba negativos, ahora no!
//    // NOTAR: Si pones negativo o texto antes te tiraba 400 bad request, ahora te tira 404 not found.
//    public ModelAndView post(@PathVariable("id") final long postId) {
//        final ModelAndView mav = new ModelAndView("views/index");
//        System.out.println(ps.findPostById(postId));
//        System.out.println(cs.findCommentsByPostId(postId));
//        mav.addObject("post", ps.findPostById(postId));
//        mav.addObject("comments", cs.findCommentsByPostId(postId));
//        // TIENE EL POST Y LOS COMENTARIOS DE LA RUTA ID
//        return mav;
//    }

    // Handle "/post" and "/post/{id}" URLs
    @RequestMapping( "/{id:\\d+}")
//    @RequestMapping("/post")
    public ModelAndView viewPost(@PathVariable(value = "id") int postId) {
        ModelAndView mav = new ModelAndView("views/post");

        Optional<Post> optionalPost = ps.findPostById(postId);
        mav.addObject("post", optionalPost.orElseThrow(() -> new RuntimeException("Post not found")));

        Optional<List<Comment>> optionalComments = cs.findCommentsByPostId(postId);
        mav.addObject("comments", optionalComments.orElse(Collections.emptyList()));

        Optional<List<Tag>> optionalTags = ts.findTags(postId);
        List<Tag> tags = optionalTags.orElse(Collections.emptyList());
        mav.addObject("tags", tags);

        return mav;
    }



    // ------------------------------------------------------ TEST

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView test() {
        System.out.println(chs.getAllChannels());
        System.out.println(ps.getAllPostsByChannel("Foro"));
        return new ModelAndView("views/index");
    }


}
