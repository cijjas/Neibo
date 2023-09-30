package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.exceptions.*;
import ar.edu.itba.paw.webapp.form.*;
import enums.BaseChannel;
import enums.Language;
import enums.SortOrder;
import enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    private final AmenityService as;
    private final ReservationService rs;
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
                           final ReservationService rs,
                           final AmenityService as,
                           final EventService es,
                           EventService es1) {
        this.is = is;
        this.ps = ps;
        this.us = us;
        this.nhs = nhs;
        this.cs = cs;
        this.ts = ts;
        this.chs = chs;
        this.ss = ss;
        this.cas = cas;
        this.as = as;
        this.rs = rs;
        this.es = es1;
    }

    // ------------------------------------- FEED --------------------------------------


    private ModelAndView handleChannelRequest(
            String channelName,
            int page,
            int size,
            SortOrder date,
            List<String> tags
    ) {
        List<Post> postList = ps.getPostsByCriteria(channelName, page, size, date, tags, getLoggedNeighbor().getNeighborhoodId());
        int totalPages = ps.getTotalPages(channelName, size, tags, getLoggedNeighbor().getNeighborhoodId());

        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(d -> d.getTime())
                .collect(Collectors.toList());

        ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags());
        mav.addObject("appliedTags", tags);
        mav.addObject("postList", postList);
        mav.addObject("page", page);
        mav.addObject("totalPages", totalPages);
        mav.addObject("channel", channelName);
        mav.addObject("eventDates", eventTimestamps);

        return mav;
    }

    @RequestMapping("/")
    public ModelAndView index(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "date", defaultValue = "DESC", required = false) SortOrder date,
            @RequestParam(value = "tag", required = false) List<String> tags
    ) {
        return handleChannelRequest(BaseChannel.FEED.toString(), page, size, date, tags);
    }

    @RequestMapping("/admin/neighbors")
    public ModelAndView neighbors(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "3") int size
    ) {

        final ModelAndView mav = new ModelAndView("admin/requestManager");
        mav.addObject("totalPages", us.getTotalPages(UserRole.NEIGHBOR, getLoggedNeighbor().getNeighborhoodId(), size ));
        mav.addObject("verifiedList", us.getUsersPage(UserRole.NEIGHBOR,getLoggedNeighbor().getNeighborhoodId(), page, size));
        return mav;
    }

    @RequestMapping("/admin/unverified")
    public ModelAndView unverified(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "3") int size
    ) {

        final ModelAndView mav = new ModelAndView("admin/requestManager");
        mav.addObject("totalPages", us.getTotalPages(UserRole.UNVERIFIED_NEIGHBOR, getLoggedNeighbor().getNeighborhoodId(), size ));
        mav.addObject("unverifiedList", us.getUsersPage(UserRole.UNVERIFIED_NEIGHBOR,getLoggedNeighbor().getNeighborhoodId(), page, size));
        return mav;
    }


    // ------------------------------------- PROFILE --------------------------------------

    @RequestMapping("/profile")
    public ModelAndView profile() {
        ModelAndView mav = new ModelAndView("views/userProfile");
        mav.addObject("neighbor", getLoggedNeighbor());
        //us.updateLanguage(getLoggedNeighbor().getUserId(), "Spanish");
        return mav;
    }
    @RequestMapping (value = "/updateDarkModePreference", method = RequestMethod.POST)
    public String updateDarkModePreference() {
        User user = getLoggedNeighbor();
        us.toggleDarkMode(user.getUserId());
        return "redirect:/profile";
    }

    @RequestMapping (value = "/updateLanguagePreference", method = RequestMethod.POST)
    public String updateLanguagePreference(@RequestParam("language") String language) {
        User user = getLoggedNeighbor();
        return "redirect:/profile";
    }

    @RequestMapping(value = "/applyTagsFilter", method = RequestMethod.POST)
    public ModelAndView applyTagsFilter(@RequestParam("tags") String tags, @RequestParam("currentUrl") String currentUrl) {
        return new ModelAndView("redirect:" + ts.createURLForTagFilter(tags, currentUrl, getLoggedNeighbor().getNeighborhoodId()));
    }

    // ------------------------------------- ANNOUNCEMENTS --------------------------------------

    @RequestMapping("/announcements")
    public ModelAndView announcements(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "date", defaultValue = "DESC", required = false) SortOrder date,
            @RequestParam(value = "tag", required = false) List<String> tags
    ) {
        return handleChannelRequest(BaseChannel.ANNOUNCEMENTS.toString(), page, size, date, tags);
    }

    // ------------------------------------- FORUM --------------------------------------

    @RequestMapping("/complaints")
    public ModelAndView complaints(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "date", defaultValue = "DESC", required = false) SortOrder date,
            @RequestParam(value = "tag", required = false) List<String> tags
    ){
        return handleChannelRequest(BaseChannel.COMPLAINTS.toString(), page, size, date, tags);
    }

    @RequestMapping(value = "/unverified", method = RequestMethod.GET)
    public ModelAndView publishForm() {
        return  new ModelAndView("views/unverified");
    }

    // ------------------------------------- PUBLISH --------------------------------------

    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public ModelAndView publishForm(
            @ModelAttribute("publishForm") final PublishForm publishForm,
            @RequestParam(value = "onChannelId", required = false) Long onChannelId
    ) {
        final ModelAndView mav = new ModelAndView("views/publish");
        mav.addObject("channel", onChannelId);
        mav.addObject("channelList", chs.getNeighborChannels(getLoggedNeighbor().getNeighborhoodId(), getLoggedNeighbor().getUserId()));
        return mav;
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ModelAndView publish(
            @Valid @ModelAttribute("publishForm") final PublishForm publishForm,
            final BindingResult errors,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "onChannelId", required = false) Long onChannelId
    ) {
        if (errors.hasErrors()) {
            return publishForm(publishForm, onChannelId);
        }
        Integer channelId = publishForm.getChannel();

        Post p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), getLoggedNeighbor().getUserId(), channelId, publishForm.getTags(), imageFile);
        ModelAndView mav = new ModelAndView("views/publish");
        mav.addObject("channelId", channelId);
        mav.addObject("showSuccessMessage", true);
        return mav;
    }


    @RequestMapping(value = "/redirectToChannel", method = RequestMethod.POST)
    public ModelAndView redirectToChannel(
            @RequestParam("channelId") int channelId
    ) {
        String channelName= chs.findChannelById(channelId).get().getChannel().toLowerCase();
        if(channelName.equals(BaseChannel.FEED.toString())){
            return new ModelAndView("redirect:/");
        }
        else {
            return new ModelAndView("redirect:/" + channelName);
        }
    }
    @RequestMapping(value = "/publishToChannel", method = RequestMethod.POST)
    public ModelAndView publishToChannel(
            @RequestParam("channel") String channelString
    ) {
        long channelId = chs.findChannelByName(channelString).get().getChannelId();
        return new ModelAndView("redirect:/publish?onChannelId=" + channelId);
    }

    // ------------------------------------- PUBLISH ADMIN --------------------------------------

    @RequestMapping(value = "/admin/publish", method = RequestMethod.GET)
    public ModelAndView publishAdminForm(
            @ModelAttribute("publishForm") final PublishForm publishForm,
            @RequestParam (value = "onChannelId", required = false) Long onChannelId
    ) {
        final ModelAndView mav = new ModelAndView("admin/publishAdmin");
        mav.addObject("channelList", chs.getAdminChannels(getLoggedNeighbor().getNeighborhoodId()));
        return mav;
    }

    @RequestMapping(value = "/admin/publish", method = RequestMethod.POST)
    public ModelAndView publishAdmin(@Valid @ModelAttribute("publishForm") final PublishForm publishForm,
                                     final BindingResult errors,
                                     @RequestParam("imageFile") MultipartFile imageFile,
                                     @RequestParam (value = "onChannelId", required = false) Long onChannelId
                                     ) {
        if (errors.hasErrors()){
            return publishForm(publishForm, onChannelId);
        }

        ps.createAdminPost(getLoggedNeighbor().getNeighborhoodId(), publishForm.getSubject(), publishForm.getMessage(), getLoggedNeighbor().getUserId(), publishForm.getChannel(), publishForm.getTags(), imageFile);
        PublishForm clearedForm = new PublishForm();
        ModelAndView mav = new ModelAndView("admin/publishAdmin");
        mav.addObject("showSuccessMessage", true);
        mav.addObject("channelList", chs.getAdminChannels(getLoggedNeighbor().getNeighborhoodId()));
        mav.addObject("publishForm", clearedForm);

        return mav;
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
        ModelAndView mav = new ModelAndView("redirect:/posts/" + postId);
        mav.addObject("commentForm", new CommentForm());
        return mav;
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

    // ---------------------------------- LOGIN SIGNUP AND SESSION  -----------------------------------


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView logIn(Model model,
                            @ModelAttribute("signupForm") final SignupForm signupform,
                            @RequestParam(value = "error", required = false, defaultValue = "false") boolean error
    ) {
        model.addAttribute("neighbor", new User.Builder());
        ModelAndView mav = new ModelAndView("views/landingPage");
        mav.addObject("error", error);
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
    public ModelAndView signupForm(
            @ModelAttribute("signupForm") final SignupForm signupform,
            @RequestParam(value = "successfullySignup", required = false) boolean successfullySignup
    ) {
        ModelAndView mav = new ModelAndView("views/landingPage");
        mav.addObject("successfullySignup", successfullySignup);
        mav.addObject("neighborhoodsList", nhs.getNeighborhoods());
        return mav;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView signupForm(@Valid @ModelAttribute("signupForm") final SignupForm signupForm,
                              final BindingResult errors) {
        if (errors.hasErrors()) {
            ModelAndView mav = signupForm(signupForm, false);
            mav.addObject("openSignupDialog", true);
            return mav;
        }
        us.createNeighbor(signupForm.getMail(), signupForm.getPassword(), signupForm.getName(), signupForm.getSurname(), signupForm.getNeighborhoodId(), Language.ENGLISH);
        ModelAndView mav = new ModelAndView("redirect:/signup");
        mav.addObject("successfullySignup", true);
        return mav;
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

    // ------------------------------------- AMENITIES --------------------------------------

    @RequestMapping(value = "/admin/amenities", method = RequestMethod.GET)
    public ModelAndView adminAmenities() {
        ModelAndView mav = new ModelAndView("admin/amenities");

        List<Amenity> amenities = as.getAmenities();
        List<AmenityHours> amenityHoursList = new ArrayList<>();

        for (Amenity amenity : amenities) {
            Map<String, DayTime> amenityTimes = as.getAmenityHoursByAmenityId(amenity.getAmenityId());

            AmenityHours amenityHours = new AmenityHours.Builder().amenity(amenity).amenityHours(amenityTimes).build();

            amenityHoursList.add(amenityHours);
        }
        System.out.println("PRINTING THE AMENITIES HOURS LIST: ");
        System.out.println(amenityHoursList);
        mav.addObject("amenitiesHours", amenityHoursList);
        return mav;
    }

    @RequestMapping(value = "/createAmenity", method = RequestMethod.GET)
    public ModelAndView createAmenityForm(@ModelAttribute("amenityForm") final AmenityForm amenityForm) {
        ModelAndView mav = new ModelAndView("admin/createAmenity");

        List<Time> timeList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        try {
            Date startTime = sdf.parse("00:00");
            Date endTime = sdf.parse("23:30");

            long currentTime = startTime.getTime();
            long endTimeMillis = endTime.getTime();

            while (currentTime <= endTimeMillis) {
                timeList.add(new Time(currentTime));
                currentTime += 30 * 60 * 1000; // Add 30 minutes in milliseconds
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mav.addObject("timeList", timeList);

        List<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");
        daysOfWeek.add("Saturday");
        daysOfWeek.add("Sunday");
        mav.addObject("daysOfWeek", daysOfWeek);
        return mav;
    }

    @RequestMapping(value = "/createAmenity", method = RequestMethod.POST)
    public ModelAndView createAmenity(@Valid @ModelAttribute("amenityForm") final AmenityForm amenityForm,
                                      final BindingResult errors) {
        if (errors.hasErrors()) {
            System.out.println("ERRORS: " + errors);
            return createAmenityForm(amenityForm);
        }

        as.createAmenityWrapper(amenityForm.getName(), amenityForm.getDescription(), amenityForm.getMondayOpenTime(), amenityForm.getMondayCloseTime(), amenityForm.getTuesdayOpenTime(), amenityForm.getTuesdayCloseTime(), amenityForm.getWednesdayOpenTime(), amenityForm.getWednesdayCloseTime(), amenityForm.getThursdayOpenTime(), amenityForm.getThursdayCloseTime(), amenityForm.getFridayOpenTime(), amenityForm.getFridayCloseTime(), amenityForm.getSaturdayOpenTime(), amenityForm.getSaturdayCloseTime(), amenityForm.getSundayOpenTime(), amenityForm.getSundayCloseTime());
        return new ModelAndView("redirect:/admin/amenities");
    }

    //------------------------------------- USER AMENITIES & RESERVATIONS --------------------------------------
    @RequestMapping(value = "/amenities", method = RequestMethod.GET)
    public ModelAndView amenities(@ModelAttribute("reservationForm") final ReservationForm reservationForm) {
        ModelAndView mav = new ModelAndView("views/amenities");

        List<Amenity> amenities = as.getAmenities();
        List<AmenityHours> amenityHoursList = new ArrayList<>();

        for (Amenity amenity : amenities) {
            Map<String, DayTime> amenityTimes = as.getAmenityHoursByAmenityId(amenity.getAmenityId());

            AmenityHours amenityHours = new AmenityHours.Builder().amenity(amenity).amenityHours(amenityTimes).build();

            amenityHoursList.add(amenityHours);
        }
        mav.addObject("amenitiesHours", amenityHoursList);
        List<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");
        daysOfWeek.add("Saturday");
        daysOfWeek.add("Sunday");
        mav.addObject("daysOfWeek", daysOfWeek);

        List<Time> timeList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        try {
            Date startTime = sdf.parse("00:00");
            Date endTime = sdf.parse("23:30");

            long currentTime = startTime.getTime();
            long endTimeMillis = endTime.getTime();

            while (currentTime <= endTimeMillis) {
                timeList.add(new Time(currentTime));
                currentTime += 30 * 60 * 1000; // Add 30 minutes in milliseconds
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mav.addObject("timeList", timeList);
        return mav;
    }

    @RequestMapping(value = "/amenities", method = RequestMethod.POST)
    public ModelAndView amenities(@Valid @ModelAttribute("reservationForm") final ReservationForm reservationForm,
                                  final BindingResult errors) {
        if (errors.hasErrors()) {
            System.out.println("ERRORS: " + errors);
            return amenities(reservationForm);
        }

        Reservation res = rs.createReservation(reservationForm.getAmenityId(), getLoggedNeighbor().getUserId(), reservationForm.getDate(), reservationForm.getStartTime(), reservationForm.getEndTime());
        System.out.println("RESERVATION: " + res);
        return new ModelAndView("redirect:/amenities");
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


    // ------------------------------------- TEST --------------------------------------

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView test(
    )
    {
        System.out.println(chs.getChannels(1));
        System.out.println(chs.createChannel(1,"Poker"));
        System.out.println(chs.getChannels(1));

        // rs.createReservation(1,14, java.sql.Date.valueOf("2023-09-25"), Time.valueOf("12:00:00"), Time.valueOf("16:00:00"));

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
}
