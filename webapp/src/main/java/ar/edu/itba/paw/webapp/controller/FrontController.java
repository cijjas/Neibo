package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.exceptions.*;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.form.PublishForm;
import ar.edu.itba.paw.webapp.form.SignupForm;
import com.sun.org.apache.xpath.internal.operations.Mod;
import enums.Language;
import enums.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

@Controller
public class FrontController {

    private final PostService ps;
    private final UserService us;
    private final NeighborhoodService nhs;
    private final CommentService cs;
    private final TagService ts;
    private final ChannelService chs;
    private final SubscriptionService ss;
    private final CategorizationService cas;
    private final ImageService is;

    private final EventService es;

    @Autowired
    public FrontController(final PostService ps,
                           final UserService us,
                           final NeighborhoodService nhs,
                           final CommentService cs,
                           final TagService ts,
                           final ChannelService chs,
                           final SubscriptionService ss,
                           final CategorizationService cas,
                           final ImageService is,
                           final EventService es
    ) {
        this.is = is;
        this.ps = ps;
        this.us = us;
        this.nhs = nhs;
        this.cs = cs;
        this.ts = ts;
        this.chs = chs;
        this.ss = ss;
        this.cas = cas;
        this.es = es;
    }

    // ------------------------------------- FEED --------------------------------------

    @RequestMapping("/")
    public ModelAndView index(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "date", defaultValue = "DESC", required = false) SortOrder date,
            @RequestParam(value = "tag", required = false) List<String> tags
    ) {

        List<Post> postList = ps.getPostsByCriteria("Feed", page, size, date, tags);
        int totalPages = ps.getTotalPages("Feed", size, tags);

        final ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags());
        mav.addObject("postList", postList);
        mav.addObject("page", page); // Add page parameter to the model
        mav.addObject("totalPages", totalPages); // Add totalPages parameter to the model
        mav.addObject("channel", "Feed");

        return mav;
    }

    @RequestMapping("/hey")
    public ModelAndView hey() {

        final ModelAndView mav = new ModelAndView("admin/requestManager");

        mav.addObject("unverifiedList", us.getUnverifiedNeighborsByNeighborhood(1));
        mav.addObject("verifiedList", us.getNeighborsByNeighborhood(1));
        return mav;
    }

    @RequestMapping("/user")
    public ModelAndView user() {
        ModelAndView mav = new ModelAndView("views/userProfile");
        mav.addObject("neighbor", getLoggedNeighbor());
        //us.updateLanguage(getLoggedNeighbor().getUserId(), "Spanish");
        return mav;
    }
    @RequestMapping (value = "/updateDarkModePreference", method = RequestMethod.POST)
    public String updateDarkModePreference() {
        User user = getLoggedNeighbor();
        us.toggleDarkMode(user.getUserId());

        return "redirect:/user";
    }



    @RequestMapping("/announcements")
    public ModelAndView announcements(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "date", defaultValue = "DESC", required = false) SortOrder date,
            @RequestParam(value = "tag", required = false) List<String> tags
    ) {
        List<Post> postList = ps.getPostsByCriteria("Administracion", page, size, date, tags);
        int totalPages = ps.getTotalPages("Administracion", size, tags);

        final ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags());
        mav.addObject("postList", postList);
        mav.addObject("page", page); // Add page parameter to the model
        mav.addObject("totalPages", totalPages); // Add totalPages parameter to the model
        mav.addObject("channel", "Announcements");

        return mav;
    }

    // ------------------------------------- FORO --------------------------------------

    @RequestMapping("/forum")
    public ModelAndView forum(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "date", defaultValue = "DESC", required = false) SortOrder date,
            @RequestParam(value = "tag", required = false) List<String> tags
    ){
        List<Post> postList = ps.getPostsByCriteria("Foro", page, size, date, tags);
        int totalPages = ps.getTotalPages("Foro", size, tags);

        final ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags());
        mav.addObject("postList", postList);
        mav.addObject("page", page); // Add page parameter to the model
        mav.addObject("totalPages", totalPages); // Add totalPages parameter to the model
        mav.addObject("channel", "Forum");

        return mav;
    }


    // ------------------------------------- PUBLISH --------------------------------------


    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public ModelAndView publishForm(@ModelAttribute("publishForm") final PublishForm publishForm) {
        final ModelAndView mav = new ModelAndView("views/publish");

        Map<String, Channel> channelMap = chs.getChannels().stream()
                .collect(Collectors.toMap(Channel::getChannel, Function.identity()));
        //no queremos que usuarios puedan publicar en el canal de administracion
        channelMap.remove("Administracion");
        mav.addObject("channelList", channelMap);

        return mav;
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ModelAndView publish(@Valid @ModelAttribute("publishForm") final PublishForm publishForm,
                                final BindingResult errors,
                                @RequestParam("imageFile") MultipartFile imageFile) {
        if (errors.hasErrors())
            return publishForm(publishForm);
        Post p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), getLoggedNeighbor().getUserId(), publishForm.getChannel(), publishForm.getTags(), imageFile);
        ModelAndView mav = new ModelAndView("redirect:/posts/" + p.getPostId() + "?success=true");
        return mav;
    }



    @RequestMapping(value = "/publishAdmin", method = RequestMethod.GET)
    public ModelAndView publishAdminForm(@ModelAttribute("publishForm") final PublishForm publishForm) {
        final ModelAndView mav = new ModelAndView("admin/publishAdmin");
        Map<String, Channel> channelMap = chs.getChannels().stream()
                .collect(Collectors.toMap(Channel::getChannel, Function.identity()));
        channelMap.remove("Foro");
        channelMap.remove("Feed");
        //no queremos que usuarios puedan publicar en el canal de administracion
        mav.addObject("channelList", channelMap);

        return mav;
    }

    @RequestMapping(value = "/publishAdmin", method = RequestMethod.POST)
    public ModelAndView publishAdmin(@Valid @ModelAttribute("publishForm") final PublishForm publishForm,
                                     final BindingResult errors,
                                     @RequestParam("imageFile") MultipartFile imageFile) {
        if (errors.hasErrors())
            return publishForm(publishForm);

        ps.createAdminPost(publishForm.getSubject(), publishForm.getMessage(), getLoggedNeighbor().getUserId(), publishForm.getChannel(), publishForm.getTags(), imageFile);

        return new ModelAndView("admin/publishAdmin");
    }


    // ------------------------------------- POSTS --------------------------------------

    @RequestMapping(value = "/posts/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView viewPost(@PathVariable(value = "id") int postId,
                                 @ModelAttribute("commentForm") final CommentForm commentForm,
                                 @RequestParam(value = "success", required = false) boolean success) {
        ModelAndView mav = new ModelAndView("views/post");

        Optional<Post> optionalPost = ps.findPostById(postId);
        mav.addObject("post", optionalPost.orElseThrow(PostNotFoundException::new));

        Optional<List<Comment>> optionalComments = cs.findCommentsByPostId(postId);
        mav.addObject("comments", optionalComments.orElse(Collections.emptyList()));

        Optional<List<Tag>> optionalTags = ts.findTagsByPostId(postId);
        List<Tag> tags = optionalTags.orElse(Collections.emptyList());

        mav.addObject("tags", tags);
        mav.addObject("commentForm", commentForm);

        mav.addObject("showSuccessMessage", success);

        return mav;
    }

    @RequestMapping(value = "/posts/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView viewPost(@PathVariable(value = "id") int postId,
                                 @Valid @ModelAttribute("commentForm") final CommentForm commentForm,
                                 final BindingResult errors) {
        if (errors.hasErrors()) {
            return viewPost(postId, commentForm, false);
        }
        cs.createComment(commentForm.getComment(), getLoggedNeighbor().getUserId(), postId);

        return new ModelAndView("redirect:/posts/" + postId); // Redirect to the "posts" page
    }

    // ------------------------------------- RESOURCES --------------------------------------

    @RequestMapping(value = "/images/{imageId}")
    @ResponseBody
    public byte[] imageRetriever(@PathVariable long imageId) {
        return is.getImage(imageId).map(Image::getImage).orElse(null);
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

    // ---------------------------------- LOGIN BETA -----------------------------------


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView logIn(Model model, @ModelAttribute("signupForm") final SignupForm signupform) {
        model.addAttribute("neighbor", new User.Builder());
        ModelAndView mav = new ModelAndView("views/landingPage");

        mav.addObject("neighborhoodsList", nhs.getNeighborhoods());
        mav.addObject("openSignupDialog", false);
        return mav;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView logIn(@RequestParam("mail") String mail,
                              @RequestParam("password") String password
    ) {
        return new ModelAndView("views/index");
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signupForm(@ModelAttribute("signupForm") final SignupForm signupform) {
        ModelAndView mav = new ModelAndView("views/landingPage");
        mav.addObject("neighborhoodsList", nhs.getNeighborhoods());
        return mav;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView signupForm(@Valid @ModelAttribute("signupForm") final SignupForm signupForm,
                              final BindingResult errors) {
        if (errors.hasErrors()) {
            ModelAndView mav = signupForm(signupForm);
            mav.addObject("openSignupDialog", true);
            return mav;
        }

        us.createNeighbor(signupForm.getMail(), signupForm.getPassword(), signupForm.getName(), signupForm.getSurname(), signupForm.getNeighborhoodId(), Language.ENGLISH);
        return new ModelAndView("redirect:/");
    }


    @ModelAttribute("loggedUser")
    public User getLoggedNeighbor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken)
            return null;

        String email = authentication.getName();

        Optional<User> neighborOptional = us.findUserByMail(email);

        return neighborOptional.orElseThrow(NeighborhoodNotFoundException::new);
    }



    // ------------------------------------- TEST --------------------------------------

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView test(
    )
    {


        System.out.println(ps.getPostsByCriteria("Feed", 1, 10, SortOrder.ASC,null));



        return new ModelAndView("views/index");
    }

    @RequestMapping(value = "/admin/test", method = RequestMethod.GET)
    public ModelAndView adminTest() {
        return new ModelAndView("admin/requestManager");
    }

    @RequestMapping(value = "/testDuplicatedException", method = RequestMethod.GET)
    public ModelAndView testDuplicatedException() {
        throw new DuplicatedNeighbor();
    }

    @RequestMapping(value = "/testNotFoundException", method = RequestMethod.GET)
    public ModelAndView testNotFoundException() {
        throw new NeighborhoodNotFoundException();
    }

    // ------------------------------------- CALENDAR --------------------------------------
    @RequestMapping("/calendar")
    public ModelAndView calendar() {
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());

        ModelAndView mav = new ModelAndView("views/calendar");
        mav.addObject("eventDates", eventTimestamps);
        return mav;
    }

}
