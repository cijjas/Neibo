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
    private final ResourceService rs1;
    private final ContactService cs1;


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
                           EventService es1,
                           final ResourceService rs1,
                           final ContactService cs1) {
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
        this.rs1 = rs1;
        this.cs1 = cs1;
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
        mav.addObject("tagList", ts.getTags(getLoggedNeighbor().getNeighborhoodId()));
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
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        final ModelAndView mav = new ModelAndView("admin/views/requestManager");
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());
        mav.addObject("eventDates", eventTimestamps);
        mav.addObject("neighbors", true);
        mav.addObject("page", page);
        mav.addObject("totalPages", us.getTotalPages(UserRole.NEIGHBOR, getLoggedNeighbor().getNeighborhoodId(), size ));
        mav.addObject("users", us.getUsersPage(UserRole.NEIGHBOR,getLoggedNeighbor().getNeighborhoodId(), page, size));
        return mav;
    }

    @RequestMapping("/admin/unverified")
    public ModelAndView unverified(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        final ModelAndView mav = new ModelAndView("admin/views/requestManager");
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());
        mav.addObject("eventDates", eventTimestamps);
        mav.addObject("neighbors", false);
        mav.addObject("page", page);
        mav.addObject("totalPages", us.getTotalPages(UserRole.UNVERIFIED_NEIGHBOR, getLoggedNeighbor().getNeighborhoodId(), size));
        mav.addObject("users", us.getUsersPage(UserRole.UNVERIFIED_NEIGHBOR,getLoggedNeighbor().getNeighborhoodId(), page, size));
        return mav;
    }

    @RequestMapping("/unverifyUser")
    public ModelAndView unverifyUser(
            @RequestParam("userId") long userId
    ) {
        us.unverifyNeighbor(userId);
        return new ModelAndView("redirect:/admin/neighbors");
    }

    @RequestMapping("/verifyUser")
    public ModelAndView verifyUser(
            @RequestParam("userId") long userId
    ) {
        us.verifyNeighbor(userId);
        return new ModelAndView("redirect:/admin/unverified");
    }

    // ------------------------------------- PROFILE --------------------------------------

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(@ModelAttribute("profilePictureForm") final ProfilePictureForm profilePictureForm) {
        ModelAndView mav = new ModelAndView("views/userProfile");
        mav.addObject("neighbor", getLoggedNeighbor());
        //us.updateLanguage(getLoggedNeighbor().getUserId(), "Spanish");
        return mav;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView profile(@Valid @ModelAttribute("profilePictureForm") final ProfilePictureForm profilePictureForm,
                                final BindingResult errors) {
        ModelAndView mav = new ModelAndView("redirect: /profile");

        if (errors.hasErrors()) {
            return profile(profilePictureForm);
        }

        us.updateProfilePicture(getLoggedNeighbor().getUserId(), profilePictureForm.getImageFile());
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
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());
        mav.addObject("channel", onChannelId);
        mav.addObject("channelList", chs.getNeighborChannels(getLoggedNeighbor().getNeighborhoodId(), getLoggedNeighbor().getUserId()));
        mav.addObject("eventDates", eventTimestamps);
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
        final ModelAndView mav = new ModelAndView("admin/views/publishAdmin");
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());
        mav.addObject("eventDates", eventTimestamps);
        mav.addObject("channelList", chs.getAdminChannels(getLoggedNeighbor().getNeighborhoodId()));
        return mav;
    }

    @RequestMapping(value = "/admin/publish", method = RequestMethod.POST)
    public ModelAndView publishAdmin(@Valid @ModelAttribute("publishForm") final PublishForm publishForm,
                                     final BindingResult errors,
                                     @RequestParam (value = "onChannelId", required = false) Long onChannelId
                                     ) {
        if (errors.hasErrors()){
            return publishForm(publishForm, onChannelId);
        }

        ps.createAdminPost(getLoggedNeighbor().getNeighborhoodId(), publishForm.getSubject(), publishForm.getMessage(), getLoggedNeighbor().getUserId(), publishForm.getChannel(), publishForm.getTags(), publishForm.getImageFile());
        PublishForm clearedForm = new PublishForm();
        ModelAndView mav = new ModelAndView("admin/views/publishAdmin");
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

        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());


        Optional<Post> optionalPost = ps.findPostById(postId);
        mav.addObject("post", optionalPost.orElseThrow(PostNotFoundException::new));

        Optional<List<Comment>> optionalComments = cs.findCommentsByPostId(postId);
        mav.addObject("comments", optionalComments.orElse(Collections.emptyList()));

        Optional<List<Tag>> optionalTags = ts.findTagsByPostId(postId);
        List<Tag> tags = optionalTags.orElse(Collections.emptyList());

        mav.addObject("tags", tags);
        mav.addObject("commentForm", commentForm);

        mav.addObject("showSuccessMessage", success);
        mav.addObject("eventDates", eventTimestamps);


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
        ModelAndView mav = new ModelAndView("admin/views/amenities");

        List<Amenity> amenities = as.getAmenities();
        List<AmenityHours> amenityHoursList = new ArrayList<>();

        for (Amenity amenity : amenities) {
            Map<String, DayTime> amenityTimes = as.getAmenityHoursByAmenityId(amenity.getAmenityId());

            AmenityHours amenityHours = new AmenityHours.Builder().amenity(amenity).amenityHours(amenityTimes).build();

            amenityHoursList.add(amenityHours);
        }
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());
        mav.addObject("eventDates", eventTimestamps);
        mav.addObject("amenitiesHours", amenityHoursList);
        return mav;
    }

    @RequestMapping(value = "/admin/deleteAmenity/{id}", method = RequestMethod.GET)
    public ModelAndView deleteAmenity(@PathVariable(value = "id") int amenityId) {
        ModelAndView mav = new ModelAndView("redirect:/admin/amenities");
        as.deleteAmenity(amenityId);
        return mav;
    }

    @RequestMapping(value = "/admin/createAmenity", method = RequestMethod.GET)
    public ModelAndView createAmenityForm(@ModelAttribute("amenityForm") final AmenityForm amenityForm) {
        ModelAndView mav = new ModelAndView("admin/views/createAmenity");

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
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());
        mav.addObject("eventDates", eventTimestamps);
        mav.addObject("daysOfWeek", daysOfWeek);
        return mav;
    }

    @RequestMapping(value = "/admin/createAmenity", method = RequestMethod.POST)
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
    public ModelAndView amenities(
            @ModelAttribute("reservationForm") final ReservationForm reservationForm
    ) {
        ModelAndView mav = new ModelAndView("views/amenities");
        mav.addObject("channel", BaseChannel.RESERVATIONS.toString());
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());

        mav.addObject("eventDates", eventTimestamps);

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
    public ModelAndView calendar(
            @RequestParam(required = false, defaultValue = "0") long timestamp) {

        Date selectedDate = new Date(timestamp != 0 ? timestamp : System.currentTimeMillis());

        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());

        List<Event> eventList = es.getEventsByDate(selectedDate, getLoggedNeighbor().getNeighborhoodId());

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
        String selectedMonth = getLoggedNeighbor().getLanguage() == Language.ENGLISH
                ? monthsEnglish[selectedMonthIndex]
                : monthsSpanish[selectedMonthIndex];
        System.out.println(selectedMonth);
        int selectedYear = selectedDate.getYear() + 1900; // getYear() returns years since 1900
        System.out.println(selectedYear);

        ModelAndView mav = new ModelAndView("views/calendar");
        mav.addObject("isAdmin", getLoggedNeighbor().getRole() == UserRole.ADMINISTRATOR);
        mav.addObject("eventDates", eventTimestamps);
        mav.addObject("selectedTimestamp", selectedDate.getTime()); // Pass the selected timestamp
        mav.addObject("selectedDay", selectedDay);
        mav.addObject("selectedMonth", selectedMonth);
        mav.addObject("selectedYear", selectedYear);
        mav.addObject("eventList", eventList);

        return mav;
    }


    @RequestMapping(value = "/admin/addEvent", method = RequestMethod.GET)
    public ModelAndView eventForm(
            @ModelAttribute("eventForm") final EventForm eventForm
    ) {
        final ModelAndView mav = new ModelAndView("views/addEvent");
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());
        mav.addObject("eventDates", eventTimestamps);
        return mav;
    }

    @RequestMapping(value = "/admin/addEvent", method = RequestMethod.POST)
    public ModelAndView addEvent(
            @Valid @ModelAttribute("eventForm") final EventForm eventForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return eventForm(eventForm);
        }

        long duration = 0;
        try {
            duration = Long.parseLong(eventForm.getDuration());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Handle the parsing error, e.g., by returning an error response to the user.
        }

        Event e = es.createEvent(eventForm.getName(), eventForm.getDescription(), eventForm.getDate(), duration, getLoggedNeighbor().getNeighborhoodId());
        ModelAndView mav = new ModelAndView("views/addEvent");
        mav.addObject("showSuccessMessage", true);
        return mav;
    }


    // ------------------------------------- INFORMATION --------------------------------
    @RequestMapping(value = "/information", method = RequestMethod.GET)
    public ModelAndView information() {
        ModelAndView mav = new ModelAndView("views/information");
        mav.addObject("resourceList", rs1.getResources(getLoggedNeighbor().getNeighborhoodId()));
        mav.addObject("phoneNumbersList", cs1.getContacts(getLoggedNeighbor().getNeighborhoodId()));
        mav.addObject("channel", BaseChannel.INFORMATION.toString());

        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());

        mav.addObject("resourceMap", rs1.getResources(getLoggedNeighbor().getNeighborhoodId()));
        mav.addObject("phoneNumbersMap", cs1.getContacts(getLoggedNeighbor().getNeighborhoodId()));
        mav.addObject("eventDates", eventTimestamps);
        return mav;
    }

    @RequestMapping(value = "/admin/information", method = RequestMethod.GET)
    public ModelAndView adminInformation() {
        ModelAndView mav = new ModelAndView("admin/views/information");
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());
        mav.addObject("eventDates", eventTimestamps);
        mav.addObject("resourceList", rs1.getResources(getLoggedNeighbor().getNeighborhoodId()));
        mav.addObject("phoneNumbersList", cs1.getContacts(getLoggedNeighbor().getNeighborhoodId()));
        return mav;
    }

    @RequestMapping(value = "/admin/deleteContact/{id}", method = RequestMethod.GET)
    public ModelAndView deleteContact(@PathVariable(value = "id") int contactId) {
        ModelAndView mav = new ModelAndView("redirect:/admin/information");
        cs1.deleteContact(contactId);
        return mav;
    }

    @RequestMapping(value = "/admin/createContact", method = RequestMethod.GET)
    public ModelAndView createContact(@ModelAttribute("contactForm") final ContactForm contactForm) {
        ModelAndView mav = new ModelAndView("admin/views/createContact");
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());
        mav.addObject("eventDates", eventTimestamps);
        return mav;
    }

    @RequestMapping(value = "/admin/createContact", method = RequestMethod.POST)
    public ModelAndView createContact(@Valid @ModelAttribute("contactForm") final ContactForm contactForm,
                                      final BindingResult errors) {
        if (errors.hasErrors()) {
            System.out.println("ERRORS: " + errors);
            return createContact(contactForm);
        }
        System.out.println(contactForm);
        Contact cont = cs1.createContact(getLoggedNeighbor().getNeighborhoodId(), contactForm.getContactName(), contactForm.getContactAddress(), contactForm.getContactPhone());
        System.out.println("created contact: " + cont);
        return new ModelAndView("redirect:/admin/information");
    }

    @RequestMapping(value = "/admin/deleteResource/{id}", method = RequestMethod.GET)
    public ModelAndView deleteResource(@PathVariable(value = "id") int resourceId) {
        ModelAndView mav = new ModelAndView("redirect:/admin/information");
        rs1.deleteResource(resourceId);
        return mav;
    }

    @RequestMapping(value = "/admin/createResource", method = RequestMethod.GET)
    public ModelAndView createResourceForm(@ModelAttribute("resourceForm") final ResourceForm resourceForm) {
        return new ModelAndView("admin/views/createResource");
    }

    @RequestMapping(value = "/admin/createResource", method = RequestMethod.POST)
    public ModelAndView createResource(@Valid @ModelAttribute("resourceForm") final ResourceForm resourceForm,
                                      final BindingResult errors) {
        if (errors.hasErrors()) {
            System.out.println("ERRORS: " + errors);
            return createResourceForm(resourceForm);
        }
        rs1.createResource(getLoggedNeighbor().getNeighborhoodId(), resourceForm.getTitle(), resourceForm.getDescription(), resourceForm.getImageFile());
        return new ModelAndView("redirect:/admin/information");
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
        ModelAndView mav = new ModelAndView("admin/views/requestManager");
        List<Date> eventDates = es.getEventDates(getLoggedNeighbor().getNeighborhoodId());
        List<Long> eventTimestamps = eventDates.stream()
                .map(date -> date.getTime())
                .collect(Collectors.toList());
        mav.addObject("eventDates", eventTimestamps);
        return mav;
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
