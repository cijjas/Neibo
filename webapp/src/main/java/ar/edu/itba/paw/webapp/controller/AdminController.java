package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.*;
import enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
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
    private final ResourceService res;
    private final ContactService cos;


    @Autowired
    public AdminController(final PostService ps,
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
                         final ResourceService res,
                         final ContactService cos) {
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
        this.res = res;
        this.cos = cos;
    }

    // ------------------------------------- INFORMATION --------------------------------------

    @RequestMapping(value = "/information", method = RequestMethod.GET)
    public ModelAndView adminInformation() {
        ModelAndView mav = new ModelAndView("admin/views/information");

        mav.addObject("panelOption", "Information");
        mav.addObject("resourceList", res.getResources(getLoggedUser().getNeighborhoodId()));
        mav.addObject("phoneNumbersList", cos.getContacts(getLoggedUser().getNeighborhoodId()));
        return mav;
    }

    // ------------------------------------- NEIGHBORS LIST --------------------------------------

    @RequestMapping("/neighbors")
    public ModelAndView neighbors(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        final ModelAndView mav = new ModelAndView("admin/views/requestManager");

        mav.addObject("panelOption", "Neighbors");
        mav.addObject("neighbors", true);
        mav.addObject("page", page);
        mav.addObject("totalPages", us.getTotalPages(UserRole.NEIGHBOR, getLoggedUser().getNeighborhoodId(), size ));
        mav.addObject("users", us.getUsersPage(UserRole.NEIGHBOR, getLoggedUser().getNeighborhoodId(), page, size));
        return mav;
    }

    // ------------------------------------- UNVERIFIED LIST --------------------------------------

    @RequestMapping("/unverified")
    public ModelAndView unverified(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        final ModelAndView mav = new ModelAndView("admin/views/requestManager");

        mav.addObject("panelOption", "Requests");
        mav.addObject("neighbors", false);
        mav.addObject("page", page);
        mav.addObject("totalPages", us.getTotalPages(UserRole.UNVERIFIED_NEIGHBOR, getLoggedUser().getNeighborhoodId(), size));
        mav.addObject("users", us.getUsersPage(UserRole.UNVERIFIED_NEIGHBOR, getLoggedUser().getNeighborhoodId(), page, size));
        return mav;
    }

    // Estos deberian ser /admin/unverifyUser... sino un plebeyo puede verificar y desverificar usuarios

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

    // ------------------------------------- PUBLISH --------------------------------------

    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public ModelAndView publishAdminForm(
            @ModelAttribute("publishForm") final PublishForm publishForm,
            @RequestParam(value = "onChannelId", required = false) Long onChannelId
    ) {
        final ModelAndView mav = new ModelAndView("admin/views/publishAdmin");

        mav.addObject("panelOption", "PublishAdmin");
        mav.addObject("channelList", chs.getAdminChannels(getLoggedUser().getNeighborhoodId()));
        return mav;
    }


    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ModelAndView publishAdmin(@Valid @ModelAttribute("publishForm") final PublishForm publishForm,
                                     final BindingResult errors,
                                     @RequestParam (value = "onChannelId", required = false) Long onChannelId
    ) {
        if (errors.hasErrors()){
            return publishAdminForm(publishForm, onChannelId);
        }

        ps.createAdminPost(getLoggedUser().getNeighborhoodId(), publishForm.getSubject(), publishForm.getMessage(), getLoggedUser().getUserId(), publishForm.getChannel(), publishForm.getTags(), publishForm.getImageFile());
        PublishForm clearedForm = new PublishForm();
        ModelAndView mav = new ModelAndView("admin/views/publishAdmin");
        mav.addObject("showSuccessMessage", true);
        mav.addObject("channelList", chs.getAdminChannels(getLoggedUser().getNeighborhoodId()));
        mav.addObject("publishForm", clearedForm);

        return mav;
    }

    // ------------------------------------- AMENITIES --------------------------------------

    @RequestMapping(value = "/amenities", method = RequestMethod.GET)
    public ModelAndView adminAmenities() {
        ModelAndView mav = new ModelAndView("admin/views/amenities");

        List<Amenity> amenities = as.getAmenities(getLoggedUser().getNeighborhoodId());
        List<AmenityHours> amenityHoursList = new ArrayList<>();

        for (Amenity amenity : amenities) {
            Map<String, DayTime> amenityTimes = as.getAmenityHoursByAmenityId(amenity.getAmenityId());

            AmenityHours amenityHours = new AmenityHours.Builder().amenity(amenity).amenityHours(amenityTimes).build();

            amenityHoursList.add(amenityHours);
        }

        mav.addObject("panelOption", "Amenities");
        mav.addObject("amenitiesHours", amenityHoursList);
        return mav;
    }

    @RequestMapping(value = "/deleteAmenity/{id}", method = RequestMethod.GET)
    public ModelAndView deleteAmenity(@PathVariable(value = "id") int amenityId) {
        ModelAndView mav = new ModelAndView("redirect:/admin/amenities");
        as.deleteAmenity(amenityId);
        return mav;
    }

    @RequestMapping(value = "/createAmenity", method = RequestMethod.GET)
    public ModelAndView createAmenityForm(@ModelAttribute("amenityForm") final AmenityForm amenityForm) {
        ModelAndView mav = new ModelAndView("admin/views/createAmenity");

        mav.addObject("timeList", as.getAllTimes());
        mav.addObject("daysOfWeek", rs.getDaysOfWeek());
        return mav;
    }

    @RequestMapping(value = "/createAmenity", method = RequestMethod.POST)
    public ModelAndView createAmenity(@Valid @ModelAttribute("amenityForm") final AmenityForm amenityForm,
                                      final BindingResult errors) {
        if (errors.hasErrors()) {
            return createAmenityForm(amenityForm);
        }

        as.createAmenityWrapper(amenityForm.getName(), amenityForm.getDescription(), amenityForm.getMondayOpenTime(), amenityForm.getMondayCloseTime(), amenityForm.getTuesdayOpenTime(), amenityForm.getTuesdayCloseTime(), amenityForm.getWednesdayOpenTime(), amenityForm.getWednesdayCloseTime(), amenityForm.getThursdayOpenTime(), amenityForm.getThursdayCloseTime(), amenityForm.getFridayOpenTime(), amenityForm.getFridayCloseTime(), amenityForm.getSaturdayOpenTime(), amenityForm.getSaturdayCloseTime(), amenityForm.getSundayOpenTime(), amenityForm.getSundayCloseTime(), getLoggedUser().getNeighborhoodId());
        return new ModelAndView("redirect:/admin/amenities");
    }

    // ------------------------------------- CALENDAR --------------------------------------

    @RequestMapping(value = "/addEvent", method = RequestMethod.GET)
    public ModelAndView eventForm(
            @ModelAttribute("eventForm") final EventForm eventForm
    ) {
        final ModelAndView mav = new ModelAndView("admin/views/addEvent");
        return mav;
    }

    @RequestMapping(value = "/addEvent", method = RequestMethod.POST)
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
        }

        Event e = es.createEvent(eventForm.getName(), eventForm.getDescription(), eventForm.getDate(), duration, getLoggedUser().getNeighborhoodId());
        ModelAndView mav = new ModelAndView("admin/views/addEvent");
        mav.addObject("showSuccessMessage", true);
        return mav;
    }

    @RequestMapping(value = "/deleteEvent/{id}", method = RequestMethod.GET)
    public ModelAndView deleteEvent(
            @PathVariable(value = "id") int eventId,
            @RequestParam(required = false, defaultValue = "0") long timestamp) {

        ModelAndView mav = new ModelAndView("redirect:/calendar?timestamp=" + timestamp);
        es.deleteEvent(eventId);
        return mav;
    }

    // ------------------------------------- CONTACT --------------------------------------

    @RequestMapping(value = "/createContact", method = RequestMethod.GET)
    public ModelAndView createContact(@ModelAttribute("contactForm") final ContactForm contactForm) {
        ModelAndView mav = new ModelAndView("admin/views/createContact");
        return mav;
    }

    @RequestMapping(value = "/createContact", method = RequestMethod.POST)
    public ModelAndView createContact(@Valid @ModelAttribute("contactForm") final ContactForm contactForm,
                                      final BindingResult errors) {
        if (errors.hasErrors()) {
            return createContact(contactForm);
        }
        Contact cont = cos.createContact(getLoggedUser().getNeighborhoodId(), contactForm.getContactName(), contactForm.getContactAddress(), contactForm.getContactPhone());
        return new ModelAndView("redirect:/admin/information");
    }

    @RequestMapping(value = "/deleteContact/{id}", method = RequestMethod.GET)
    public ModelAndView deleteContact(@PathVariable(value = "id") int contactId) {
        ModelAndView mav = new ModelAndView("redirect:/admin/information");
        cos.deleteContact(contactId);
        return mav;
    }


    // ------------------------------------- CONTACT --------------------------------------

    @RequestMapping(value = "/createResource", method = RequestMethod.GET)
    public ModelAndView createResourceForm(
            @ModelAttribute("resourceForm") final ResourceForm resourceForm
    ) {
        ModelAndView mav = new ModelAndView("admin/views/createResource");
        return mav;
    }

    @RequestMapping(value = "/createResource", method = RequestMethod.POST)
    public ModelAndView createResource(@Valid @ModelAttribute("resourceForm") final ResourceForm resourceForm,
                                       final BindingResult errors) {
        if (errors.hasErrors()) {
            return createResourceForm(resourceForm);
        }
        res.createResource(getLoggedUser().getNeighborhoodId(), resourceForm.getTitle(), resourceForm.getDescription(), resourceForm.getImageFile());
        return new ModelAndView("redirect:/admin/information");
    }

    @RequestMapping(value = "/deleteResource/{id}", method = RequestMethod.GET)
    public ModelAndView deleteResource(@PathVariable(value = "id") int resourceId) {
        ModelAndView mav = new ModelAndView("redirect:/admin/information");
        res.deleteResource(resourceId);
        return mav;
    }



    // -------------------------------------------------------------------------------
    // Transition this into AuthenticatedUtils?

    @ModelAttribute("loggedUser")
    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken)
            return null;
        String email = authentication.getName();
        Optional<User> neighborOptional = us.findUserByMail(email);
        return neighborOptional.orElseThrow(() -> new NotFoundException("Neighbor Not Found"));
    }


}
