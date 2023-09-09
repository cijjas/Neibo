package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.NeighborService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class FrontController {

    private final NeighborService us;
    private final PostService ps;
    private final NeighborService ns;
    private final NeighborhoodService nhs;

    @Autowired
    public FrontController(final NeighborService us, final PostService ps, NeighborService ns, NeighborhoodService nhs) {
        this.us = us;
        this.ps = ps;
        this.ns = ns;
        this.nhs = nhs;
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
    @RequestMapping("/")
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("helloworld/index");
        return mav;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView registerForm() {
        return new ModelAndView("helloworld/register");
    }

    @RequestMapping("/post")
    public ModelAndView postPage() {return new ModelAndView("helloworld/post");}
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
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(
            @RequestParam(value = "email", required = true) final String email,
            @RequestParam(value = "password", required = true) final String password
    ) {
        // Los @RequestParam pueden ser int, long, etc. Y nos hace la conversión por nosotros :)
        // Al @RequestParam también le podemos pasar "required = true", "default = 1234", etc.
        final User user = us.createUser(email, password);

        final ModelAndView mav = new ModelAndView("helloworld/index");
        mav.addObject("user", user);

        return mav;
    }

    // @RequestMapping("/{id}") // El problema con este es que no pone restricciones al valor de "id"!
    @RequestMapping("/{id:\\d+}") // Antes aceptaba negativos, ahora no!
    // NOTAR: Si pones negativo o texto antes te tiraba 400 bad request, ahora te tira 404 not found.
    public ModelAndView profile(@PathVariable("id") final long userId) {
        final ModelAndView mav = new ModelAndView("helloworld/profile");
        mav.addObject("user", us.findById(userId).orElseThrow(UserNotFoundException::new));
        return mav;
    }

    // ----------------------------------------------------------------------------------------------------------------
    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public ModelAndView publishForm() {
        ModelAndView mav = new ModelAndView("helloworld/publish");
        mav.addObject("neighborPostWrapper", new NeighborPostNeighborhoodWrapper());

        return mav;
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ModelAndView publish(
            @RequestParam(value = "neighbor.mail", required = true) final String mail,
            @RequestParam(value = "neighbor.name", required = true) final String name,
            @RequestParam(value = "neighbor.surname", required = true) final String surname,
            @RequestParam(value = "neighborhood.name", required = true) final String neighborhood,
            @RequestParam(value = "post.title", required = true) final String title,
            @RequestParam(value = "post.description", required = true) final String description
    ) {
        // Los @RequestParam pueden ser int, long, etc. Y nos hace la conversión por nosotros :)
        // Al @RequestParam también le podemos pasar "required = true", "default = 1234", etc.
        //final User user = us.createUser(email, password);
        System.out.println("Information Logged In :\n");
        System.out.println("Mail : " + mail);
        System.out.println("Name : " + name);
        System.out.println("Surname : " + surname);
        System.out.println("Neighborhood : " + neighborhood);
        System.out.println("Title : " + title);
        System.out.println("Description : " + description);

        Neighborhood nh = nhs.createNeighborhood(neighborhood);
        Neighbor n = ns.createNeighbor(mail,name,surname, nh.getNeighborhoodId());
        Post p = ps.createPost(title, description, n.getNeighborId());

        System.out.println("\n -------------------------------------------------- \n");

        System.out.println(nh);
        System.out.println(n);
        System.out.println(p);

        ModelAndView mav = new ModelAndView("helloworld/index");
        return mav;
    }
}
