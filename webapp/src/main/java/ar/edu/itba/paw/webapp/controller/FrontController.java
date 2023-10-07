package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.*;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

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
    private final ResourceService rs1;
    private final ContactService cs1;
    private final AttendanceService as1;
    private final LikeService ls;

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
                           final ResourceService rs1,
                           final ContactService cs1,
                           final AttendanceService as1,
                           final LikeService ls) {
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
        this.es = es;
        this.rs1 = rs1;
        this.cs1 = cs1;
        this.as1 = as1;
        this.ls = ls;
    }

    // ------------------------------------- FEED --------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontController.class); // importante!! Usar la implementacion de sl4j

    private ModelAndView handleChannelRequest(
            String channelName,
            int page,
            int size,
            SortOrder date,
            List<String> tags
    ) {
        List<Post> postList = ps.getPostsByCriteria(channelName, page, size, date, tags, getLoggedUser().getNeighborhoodId());
        int totalPages = ps.getTotalPages(channelName, size, tags, getLoggedUser().getNeighborhoodId());



        ModelAndView mav = new ModelAndView("views/index");
        mav.addObject("tagList", ts.getTags(getLoggedUser().getNeighborhoodId()));
        mav.addObject("appliedTags", tags);
        mav.addObject("postList", postList);
        mav.addObject("page", page);
        mav.addObject("totalPages", totalPages);
        mav.addObject("channel", channelName);


        return mav;
    }

    @RequestMapping("/")
    public ModelAndView index(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "date", defaultValue = "DESC", required = false) SortOrder date,
            @RequestParam(value = "tag", required = false) List<String> tags
    ) {
        LOGGER.info("Registered a new user under the id {}", getLoggedUser().getUserId());

        return handleChannelRequest(BaseChannel.FEED.toString(), page, size, date, tags);
    }


    // ------------------------------------- PROFILE --------------------------------------

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(@ModelAttribute("profilePictureForm") final ProfilePictureForm profilePictureForm) {
        ModelAndView mav = new ModelAndView("views/userProfile");
        mav.addObject("neighbor", getLoggedUser());
        //us.updateLanguage(getLoggedNeighbor().getUserId(), "Spanish");
        return mav;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView profile(@Valid @ModelAttribute("profilePictureForm") final ProfilePictureForm profilePictureForm,
                                final BindingResult errors) {
        ModelAndView mav = new ModelAndView("redirect:/profile");

        if (errors.hasErrors()) {
            return profile(profilePictureForm);
        }

        us.updateProfilePicture(getLoggedUser().getUserId(), profilePictureForm.getImageFile());
        //us.updateLanguage(getLoggedNeighbor().getUserId(), "Spanish");
        return mav;
    }

    @RequestMapping (value = "/updateDarkModePreference", method = RequestMethod.POST)
    public String updateDarkModePreference() {
        User user = getLoggedUser();
        us.toggleDarkMode(user.getUserId());
        return "redirect:/profile";
    }

    @RequestMapping (value = "/change-language", method = RequestMethod.POST)
    public String changeLanguage(
            @RequestParam(value="lang", required = false) String language,
            HttpServletRequest request
    ) {
        User user = getLoggedUser();
        Locale locale = new Locale(language);
        request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        us.toggleLanguage(user.getUserId());
        // Redirect back to the previous page or a specific page
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @RequestMapping(value = "/applyTagsFilter", method = RequestMethod.POST)
    public ModelAndView applyTagsFilter(@RequestParam("tags") String tags, @RequestParam("currentUrl") String currentUrl) {
        return new ModelAndView("redirect:" + ts.createURLForTagFilter(tags, currentUrl, getLoggedUser().getNeighborhoodId()));
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
    public ModelAndView unverified() {
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
        mav.addObject("channelList", chs.getNeighborChannels(getLoggedUser().getNeighborhoodId(), getLoggedUser().getUserId()));
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

        Post p = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), getLoggedUser().getUserId(), channelId, publishForm.getTags(), imageFile);
//        Resource res = rs1.createResource(1, "prueba resource", "prueba descripcion", imageFile);
//        System.out.println("PRINTING RESOURCE" + res);
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
        if(channelName.equals(BaseChannel.FEED.toString().toLowerCase())){
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

    // ------------------------------------- POSTS --------------------------------------

    @RequestMapping(value = "/posts/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView viewPost(@PathVariable(value = "id") int postId,
                                 @ModelAttribute("commentForm") final CommentForm commentForm,
                                 @RequestParam(value = "success", required = false) boolean success) {
        ModelAndView mav = new ModelAndView("views/post");



        Optional<Post> optionalPost = ps.findPostById(postId);
        mav.addObject("post", optionalPost.orElseThrow(() -> new NotFoundException("Post Not Found")));

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
        cs.createComment(commentForm.getComment(), getLoggedUser().getUserId(), postId);
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
        int identification = 0;
        try {
            identification = Integer.parseInt(signupForm.getIdentification());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        us.createNeighbor(signupForm.getMail(), signupForm.getPassword(), signupForm.getName(), signupForm.getSurname(), signupForm.getNeighborhoodId(), Language.ENGLISH, identification);
        ModelAndView mav = new ModelAndView("redirect:/signup");
        mav.addObject("successfullySignup", true);
        return mav;
    }

    @ModelAttribute("loggedUser")
    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken)
            return null;
        String email = authentication.getName();
        Optional<User> neighborOptional = us.findUserByMail(email);
        return neighborOptional.orElseThrow(() -> new NotFoundException("Neighbor Not Found"));
    }

    //------------------------------------- USER AMENITIES & RESERVATIONS --------------------------------------

    @RequestMapping(value = "/amenities", method = RequestMethod.GET)
    public ModelAndView amenities(
            @ModelAttribute("reservationForm") final ReservationForm reservationForm
    ) {
        ModelAndView mav = new ModelAndView("views/amenities");
        mav.addObject("channel", BaseChannel.RESERVATIONS.toString());

        List<Amenity> amenities = as.getAmenities(getLoggedUser().getNeighborhoodId());
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
        mav.addObject("reservationsList", rs.getReservationsByUserId(getLoggedUser().getUserId()));
        return mav;
    }

    @RequestMapping(value = "/amenities", method = RequestMethod.POST)
    public ModelAndView amenities(
            @Valid @ModelAttribute("reservationForm") final ReservationForm reservationForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            System.out.println("ERRORS: " + errors);
            return amenities(reservationForm);
        }
        ModelAndView mav = new ModelAndView("redirect:/amenities");

        Reservation res = rs.createReservation(reservationForm.getAmenityId(), getLoggedUser().getUserId(), reservationForm.getDate(), reservationForm.getStartTime(), reservationForm.getEndTime(), getLoggedUser().getNeighborhoodId());
        if(res == null) {
            mav.addObject("showErrorMessage", true);
        }
        else {
            mav.addObject("showSuccessMessage", true);
        }
        System.out.println("RESERVATION: " + res);
        return mav;
    }

    @RequestMapping(value = "/deleteReservation/{id}", method = RequestMethod.GET)
    public ModelAndView deleteReservation( @PathVariable(value = "id") int reservationId) {
        rs.deleteReservation(reservationId);
        return new ModelAndView("redirect:/amenities");
    }

    // ------------------------------------- CALENDAR --------------------------------------

    @RequestMapping("/calendar")
    public ModelAndView calendar(
            @RequestParam(required = false, defaultValue = "0") long timestamp) {

        Date selectedDate = new Date(timestamp != 0 ? timestamp : System.currentTimeMillis());


        List<Event> eventList = es.getEventsByDate(selectedDate, getLoggedUser().getNeighborhoodId());

        // Define arrays for month names in English and Spanish
        String[] monthsEnglish = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };

        String[] monthsSpanish = {
                "enero", "febrero", "marzo", "abril",
                "mayo", "junio", "julio", "agosto",
                "septiembre", "octubre", "noviembre", "diciembre"
        };

        // Get the selected day, month (word), and year directly from selectedDate
        int selectedDay = selectedDate.getDate(); // getDate() returns the day of the month
        int selectedMonthIndex = selectedDate.getMonth(); // getMonth() returns the month as 0-based index
        String selectedMonth = getLoggedUser().getLanguage() == Language.ENGLISH
                ? monthsEnglish[selectedMonthIndex]
                : monthsSpanish[selectedMonthIndex];
        int selectedYear = selectedDate.getYear() + 1900; // getYear() returns years since 1900

        ModelAndView mav = new ModelAndView("views/calendar");
        mav.addObject("isAdmin", getLoggedUser().getRole() == UserRole.ADMINISTRATOR);
        mav.addObject("selectedTimestamp", selectedDate.getTime()); // Pass the selected timestamp
        mav.addObject("selectedDay", selectedDay);
        mav.addObject("selectedMonth", selectedMonth);
        mav.addObject("selectedYear", selectedYear);
        mav.addObject("eventList", eventList);

        return mav;
    }

    @RequestMapping(value = "/redirectToSite", method = RequestMethod.POST)
    public ModelAndView redirectToSite(
            @RequestParam("site") String site
    ) {
        return new ModelAndView("redirect:/" + site);
    }

    // ------------------------------------- POSTS --------------------------------------

    @RequestMapping(value = "/events/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView viewEvent(@PathVariable(value = "id") int eventId,
                                 @RequestParam(value = "success", required = false) boolean success) {
        ModelAndView mav = new ModelAndView("views/event");

        Optional<Event> optionalEvent = es.findEventById(eventId);

        mav.addObject("event", optionalEvent.orElseThrow(() -> new NotFoundException("Event not found")));
        mav.addObject("attendees", us.getEventUsers(eventId));
        mav.addObject("willAttend", us.isAttending(eventId, getLoggedUser().getUserId()));


        mav.addObject("showSuccessMessage", success);

        return mav;
    }

    @RequestMapping(value = "/attend/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView attendEvent(@PathVariable(value = "id") int eventId) {

        ModelAndView mav = new ModelAndView("redirect:/events/" + eventId);
        as1.createAttendee(getLoggedUser().getUserId(), eventId);

        return mav;
    }

    @RequestMapping(value = "/unattend/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView unattendEvent(@PathVariable(value = "id") int eventId) {

        ModelAndView mav = new ModelAndView("redirect:/events/" + eventId);
        as1.deleteAttendee(getLoggedUser().getUserId(), eventId);

        return mav;
    }

    // ------------------------------------- INFORMATION --------------------------------
    @RequestMapping(value = "/information", method = RequestMethod.GET)
    public ModelAndView information() {
        ModelAndView mav = new ModelAndView("views/information");
        mav.addObject("resourceList", rs1.getResources(getLoggedUser().getNeighborhoodId()));
        mav.addObject("phoneNumbersList", cs1.getContacts(getLoggedUser().getNeighborhoodId()));
        mav.addObject("channel", BaseChannel.INFORMATION.toString());



        mav.addObject("resourceMap", rs1.getResources(getLoggedUser().getNeighborhoodId()));
        mav.addObject("phoneNumbersMap", cs1.getContacts(getLoggedUser().getNeighborhoodId()));
        return mav;
    }




    // ------------------------------------- EXCEPTIONS --------------------------------------

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView notFound(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "404");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler({DuplicateKeyException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView duplicated(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "409"); // 409 = Conflict
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler({InsertionException.class})
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView insertion(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "500");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }


    // ------------------------------------- TEST --------------------------------------

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView test(
    )
    {
        /*
        System.out.println(ps.findPostById(2));

        ls.addLikeToPost(2, getLoggedUser().getUserId());
        ls.addLikeToPost(2, getLoggedUser().getUserId()+1);
        ls.addLikeToPost(2, getLoggedUser().getUserId()+2);
        ls.addLikeToPost(3, getLoggedUser().getUserId());
        ls.removeLikeFromPost(3, getLoggedUser().getUserId());

        System.out.println("lalala");
        System.out.println(ls.isPostLiked(2, getLoggedUser().getUserId()));

         */
        return new ModelAndView("views/testView");
    }

    @RequestMapping(value = "/admin/test", method = RequestMethod.GET)
    public ModelAndView adminTest() {
        ModelAndView mav = new ModelAndView("admin/views/requestManager");
        return mav;
    }

    @RequestMapping(value = "/testDuplicatedException", method = RequestMethod.GET)
    public ModelAndView testDuplicatedException() {
        throw new DuplicateKeyException("testing wubbawubba");
    }

    @RequestMapping(value = "/testNotFoundException", method = RequestMethod.GET)
    public ModelAndView testNotFoundException() {
        throw new NotFoundException("testing wubabidabi");
    }

    @RequestMapping(value = "/testException", method = RequestMethod.GET)
    public ModelAndView testException() {
        throw new InsertionException("An error occurred whilst creating the User");
    }
}
