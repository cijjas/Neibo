package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.*;
import enums.DayOfTheWeek;
import enums.Pair;
import enums.StandardTime;
import enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final SessionUtils sessionUtils;

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
    private final EventService es;
    private final ResourceService res;
    private final ContactService cos;

    private final ShiftService shs;
    private final AvailabilityService avs;



    @Autowired
    public AdminController(SessionUtils sessionUtils,
                           final PostService ps,
                           final UserService us,
                           final NeighborhoodService nhs,
                           final CommentService cs,
                           final TagService ts,
                           final ChannelService chs,
                           final SubscriptionService ss,
                           final CategorizationService cas,
                           final ImageService is,
                           final AmenityService as,
                           final EventService es,
                           final ResourceService res,
                           final ContactService cos,
                           final ShiftService shs,
                           final AvailabilityService avs
    ) {
        this.sessionUtils = sessionUtils;
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
        this.es = es;
        this.res = res;
        this.cos = cos;
        this.shs = shs;
        this.avs = avs;
    }

    // ------------------------------------- INFORMATION --------------------------------------

    @RequestMapping(value = "/information", method = RequestMethod.GET)
    public ModelAndView adminInformation() {
        ModelAndView mav = new ModelAndView("admin/views/information");

        mav.addObject("panelOption", "Information");
        mav.addObject("resourceList", res.getResources(sessionUtils.getLoggedUser().getNeighborhoodId()));
        mav.addObject("phoneNumbersList", cos.getContacts(sessionUtils.getLoggedUser().getNeighborhoodId()));
        return mav;
    }

    // ------------------------------------- NEIGHBORS LIST --------------------------------------

    @RequestMapping("/neighbors")
    public ModelAndView neighbors(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        final ModelAndView mav = new ModelAndView("admin/views/adminRequestHandler");

        mav.addObject("panelOption", "Neighbors");
        mav.addObject("neighbors", true);
        mav.addObject("page", page);
        mav.addObject("totalPages", us.getTotalPages(UserRole.NEIGHBOR, sessionUtils.getLoggedUser().getNeighborhoodId(), size ));
        mav.addObject("users", us.getUsersPage(UserRole.NEIGHBOR, sessionUtils.getLoggedUser().getNeighborhoodId(), page, size));
        return mav;
    }

    // ------------------------------------- UNVERIFIED LIST --------------------------------------

    @RequestMapping("/unverified")
    public ModelAndView unverified(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "3") int size
    ) {
        final ModelAndView mav = new ModelAndView("admin/views/adminRequestHandler");

        mav.addObject("panelOption", "Requests");
        mav.addObject("neighbors", false);
        mav.addObject("page", page);
        mav.addObject("totalPages", us.getTotalPages(UserRole.UNVERIFIED_NEIGHBOR, sessionUtils.getLoggedUser().getNeighborhoodId(), size));
        mav.addObject("users", us.getUsersPage(UserRole.UNVERIFIED_NEIGHBOR, sessionUtils.getLoggedUser().getNeighborhoodId(), page, size));
        return mav;
    }

    @RequestMapping("/reject-user")
    public ModelAndView rejectUser(
            @RequestParam("userId") long userId
    ) {
        us.rejectNeighbor(userId);
        return new ModelAndView("redirect:/admin/unverified");
    }

    @RequestMapping("/verify-user")
    public ModelAndView verifyUser(
            @RequestParam("userId") long userId
    ) {
        us.verifyNeighbor(userId);
        return new ModelAndView("redirect:/admin/unverified");
    }

    // ------------------------------------- PUBLISH --------------------------------------

    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public ModelAndView publishAdminForm(
            @ModelAttribute("publishForm") final PublishForm publishForm
    ) {
        final ModelAndView mav = new ModelAndView("admin/views/adminPublish");

        mav.addObject("panelOption", "PublishAdmin");
        mav.addObject("channelList", chs.getAdminChannels(sessionUtils.getLoggedUser().getNeighborhoodId()));
        return mav;
    }


    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ModelAndView publishAdmin(
            @Valid @ModelAttribute("publishForm") final PublishForm publishForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()){
            return publishAdminForm(publishForm);
        }

        ps.createAdminPost(sessionUtils.getLoggedUser().getNeighborhoodId(), publishForm.getSubject(), publishForm.getMessage(), sessionUtils.getLoggedUser().getUserId(), publishForm.getChannel(), publishForm.getTags(), publishForm.getImageFile());
        PublishForm clearedForm = new PublishForm();
        ModelAndView mav = new ModelAndView("admin/views/adminPublish");
        mav.addObject("showSuccessMessage", true);
        mav.addObject("channelList", chs.getAdminChannels(sessionUtils.getLoggedUser().getNeighborhoodId()));
        mav.addObject("publishForm", clearedForm);

        return mav;
    }

    // ------------------------------------- AMENITIES --------------------------------------

    @RequestMapping(value = "/amenities", method = RequestMethod.GET)
    public ModelAndView adminAmenities() {
        ModelAndView mav = new ModelAndView("admin/views/amenitiesPanel");

        List<Amenity> amenities = as.getAmenities(sessionUtils.getLoggedUser().getNeighborhoodId());

        mav.addObject("daysPairs", DayOfTheWeek.DAY_PAIRS);
        mav.addObject("timesPairs", StandardTime.TIME_PAIRS);
        mav.addObject("amenities", amenities);
        mav.addObject("panelOption", "Amenities");

        return mav;
    }

    @RequestMapping(value = "/delete-amenity/{id}", method = RequestMethod.GET)
    public ModelAndView deleteAmenity(
            @PathVariable(value = "id") int amenityId
    ) {
        ModelAndView mav = new ModelAndView("redirect:/admin/amenities");
        as.deleteAmenity(amenityId); //JOAAAAAAAAAAAAAAAAAAAEWQDSAFEDAs
        return mav;
    }

    @RequestMapping(value = "/create-amenity", method = RequestMethod.GET)
    public ModelAndView createAmenityForm(
            @ModelAttribute("amenityForm") final AmenityForm amenityForm
    ) {
        ModelAndView mav = new ModelAndView("admin/views/amenitiesCreate");

        mav.addObject("daysPairs", DayOfTheWeek.DAY_PAIRS);
        mav.addObject("timesPairs", StandardTime.TIME_PAIRS);
        return mav;
    }

    @RequestMapping(value = "/create-amenity", method = RequestMethod.POST)
    public ModelAndView createAmenity(
            @RequestParam("selectedShifts") List<String> selectedShifts,
            @Valid @ModelAttribute("amenityForm") final AmenityForm amenityForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return createAmenityForm(amenityForm);
        }
        System.out.println("Amenity name: " + amenityForm.getName());
        System.out.println("Amenity description: " + amenityForm.getDescription());
        System.out.println("Amenity shifts: " + selectedShifts);
        System.out.println("Amenity neighborhood: " + sessionUtils.getLoggedUser().getNeighborhoodId());

        as.createAmenity(amenityForm.getName(), amenityForm.getDescription(), sessionUtils.getLoggedUser().getNeighborhoodId(), selectedShifts);
        return new ModelAndView("redirect:/admin/amenities");
    }





    // ------------------------------------- CALENDAR --------------------------------------

    @RequestMapping(value = "/add-event", method = RequestMethod.GET)
    public ModelAndView eventForm(
            @ModelAttribute("eventForm") final EventForm eventForm
    ) {
        return new ModelAndView("admin/views/eventsCreate");
    }

    @RequestMapping(value = "/add-event", method = RequestMethod.POST)
    public ModelAndView addEvent(
            @Valid @ModelAttribute("eventForm") final EventForm eventForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return eventForm(eventForm);
        }
        es.createEvent(eventForm.getName(), eventForm.getDescription(), eventForm.getDate(), eventForm.getStartTime(), eventForm.getEndTime(), sessionUtils.getLoggedUser().getNeighborhoodId());
        ModelAndView mav = new ModelAndView("redirect:/calendar?timestamp=" + eventForm.getDate().getTime());
        mav.addObject("showSuccessMessage", true);
        return mav;
    }

    @RequestMapping(value = "/delete-event/{id}", method = RequestMethod.GET)
    public ModelAndView deleteEvent(
            @PathVariable(value = "id") int eventId,
            @RequestParam(required = false, defaultValue = "0") long timestamp
    ) {

        ModelAndView mav = new ModelAndView("redirect:/calendar?timestamp=" + timestamp);
        es.deleteEvent(eventId);
        return mav;
    }

    // ------------------------------------- CONTACT --------------------------------------

    @RequestMapping(value = "/create-contact", method = RequestMethod.GET)
    public ModelAndView createContact(
            @ModelAttribute("contactForm") final ContactForm contactForm
    ) {
        return new ModelAndView("admin/views/informationContactCreate");
    }

    @RequestMapping(value = "/create-contact", method = RequestMethod.POST)
    public ModelAndView createContact(
            @Valid @ModelAttribute("contactForm") final ContactForm contactForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return createContact(contactForm);
        }
        cos.createContact(sessionUtils.getLoggedUser().getNeighborhoodId(), contactForm.getContactName(), contactForm.getContactAddress(), contactForm.getContactPhone());
        return new ModelAndView("redirect:/admin/information");
    }

    @RequestMapping(value = "/delete-contact/{id}", method = RequestMethod.GET)
    public ModelAndView deleteContact(
            @PathVariable(value = "id") int contactId
    ) {
        ModelAndView mav = new ModelAndView("redirect:/admin/information");
        cos.deleteContact(contactId);
        return mav;
    }


    // ------------------------------------- CONTACT --------------------------------------

    @RequestMapping(value = "/create-resource", method = RequestMethod.GET)
    public ModelAndView createResourceForm(
            @ModelAttribute("resourceForm") final ResourceForm resourceForm
    ) {
        return new ModelAndView("admin/views/informationResourceCreate");
    }

    @RequestMapping(value = "/create-resource", method = RequestMethod.POST)
    public ModelAndView createResource(
            @Valid @ModelAttribute("resourceForm") final ResourceForm resourceForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return createResourceForm(resourceForm);
        }
        res.createResource(sessionUtils.getLoggedUser().getNeighborhoodId(), resourceForm.getTitle(), resourceForm.getDescription(), resourceForm.getImageFile());
        return new ModelAndView("redirect:/admin/information");
    }

    @RequestMapping(value = "/delete-resource/{id}", method = RequestMethod.GET)
    public ModelAndView deleteResource(
            @PathVariable(value = "id") int resourceId
    ) {
        ModelAndView mav = new ModelAndView("redirect:/admin/information");
        res.deleteResource(resourceId);
        return mav;
    }



    // -------------------------------------------------------------------------------
    // Transition this into AuthenticatedUtils?

   

}
