package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.MainEntities.Amenity;
import ar.edu.itba.paw.models.MainEntities.User;
import ar.edu.itba.paw.models.MainEntities.Worker;
import ar.edu.itba.paw.webapp.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private final PostService ps;
    private final UserService us;
    private final TagService ts;
    private final ChannelService chs;
    private final AmenityService as;
    private final EventService es;
    private final ResourceService res;
    private final ContactService cos;
    private final AvailabilityService avs;
    private final NeighborhoodWorkerService nws;
    private final WorkerService ws;

    // ------------------------------------- INFORMATION --------------------------------------

    @Autowired
    public AdminController(final PostService ps,
                           final UserService us,
                           final TagService ts,
                           final ChannelService chs,
                           final AmenityService as,
                           final EventService es,
                           final ResourceService res,
                           final ContactService cos,
                           final AvailabilityService avs,
                           final NeighborhoodWorkerService nws,
                           final WorkerService ws
    ) {
        super(us);
        this.ps = ps;
        this.us = us;
        this.ts = ts;
        this.chs = chs;
        this.as = as;
        this.es = es;
        this.res = res;
        this.cos = cos;
        this.avs = avs;
        this.nws = nws;
        this.ws = ws;
    }

    @RequestMapping(value = "/information", method = RequestMethod.GET)
    public ModelAndView adminInformation() {
        LOGGER.info("User arriving at '/admin/information'");

        ModelAndView mav = new ModelAndView("admin/views/information");

        mav.addObject("panelOption", "Information");
        mav.addObject("resourceList", res.getResources(getLoggedUser().getNeighborhood().getNeighborhoodId()));
        mav.addObject("phoneNumbersList", cos.getContacts(getLoggedUser().getNeighborhood().getNeighborhoodId()));
        return mav;
    }

    // ----------------------------------------------- NEIGHBORS LIST --------------------------------------------------

    @RequestMapping("/neighbors")
    public ModelAndView neighbors(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "verified", defaultValue = "true") boolean verified
    ) {
        LOGGER.info("User arriving at '/admin/neighbors'");

        final ModelAndView mav = new ModelAndView("admin/views/adminRequestHandler");

        int totalPages;
        List<User> users;

        if(verified){
            totalPages = us.getTotalPages(UserRole.NEIGHBOR, getLoggedUser().getNeighborhood().getNeighborhoodId(), size);
            users = us.getUsersPage(UserRole.NEIGHBOR, getLoggedUser().getNeighborhood().getNeighborhoodId(), page, size);
        }
        else {
            totalPages = us.getTotalPages(UserRole.REJECTED, getLoggedUser().getNeighborhood().getNeighborhoodId(), size);
            users = us.getUsersPage(UserRole.REJECTED, getLoggedUser().getNeighborhood().getNeighborhoodId(), page, size);
        }

        mav.addObject("panelOption", "Neighbors");
        mav.addObject("neighbors", true);
        mav.addObject("page", page);
        mav.addObject("totalPages", totalPages);
        mav.addObject("users", users);
        mav.addObject("contextPath", "/admin/neighbors");
        mav.addObject("verified", verified);
        return mav;
    }

    // ----------------------------------------------- UNVERIFIED LIST -------------------------------------------------

    @RequestMapping("/unverified")
    public ModelAndView unverified(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        LOGGER.info("User arriving at '/admin/unverified'");

        final ModelAndView mav = new ModelAndView("admin/views/adminRequestHandler");

        mav.addObject("panelOption", "Requests");
        mav.addObject("neighbors", false);
        mav.addObject("page", page);
        mav.addObject("totalPages", us.getTotalPages(UserRole.UNVERIFIED_NEIGHBOR, getLoggedUser().getNeighborhood().getNeighborhoodId(), size));
        mav.addObject("users", us.getUsersPage(UserRole.UNVERIFIED_NEIGHBOR, getLoggedUser().getNeighborhood().getNeighborhoodId(), page, size));
        mav.addObject("contextPath", "/admin/unverified");
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

    @RequestMapping("/verify-rejected-user")
    public ModelAndView verifyRejectedUser(
            @RequestParam("userId") long userId
    ) {
        us.verifyNeighbor(userId);
        return new ModelAndView("redirect:/admin/neighbors");
    }

    // --------------------------------------------------- WORKERS LIST ------------------------------------------------

    @RequestMapping("/workers")
    public ModelAndView workers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "verified", defaultValue = "true") boolean verified
    ) {
        LOGGER.info("User arriving at '/admin/workers'");

        final ModelAndView mav = new ModelAndView("admin/views/adminWorkerRequestHandler");

        int totalPages;
        List<Worker> workers;

        if(verified){
            totalPages = ws.getTotalWorkerPagesByCriteria(null, new long[] {getLoggedUser().getNeighborhood().getNeighborhoodId()}, size, WorkerRole.VERIFIED_WORKER, WorkerStatus.none);
            workers = ws.getWorkersByCriteria(page, size, null, getLoggedUser().getNeighborhood().getNeighborhoodId(), getLoggedUser().getUserId(), WorkerRole.VERIFIED_WORKER, WorkerStatus.none);
        }
        else {
            totalPages = ws.getTotalWorkerPagesByCriteria(null, new long[] {getLoggedUser().getNeighborhood().getNeighborhoodId()}, size, WorkerRole.REJECTED, WorkerStatus.none);
            workers = ws.getWorkersByCriteria(page, size, null, getLoggedUser().getNeighborhood().getNeighborhoodId(), getLoggedUser().getUserId(), WorkerRole.REJECTED, WorkerStatus.none);
        }

        mav.addObject("panelOption", "Workers");
        mav.addObject("inWorkers", true);
        mav.addObject("page", page);
        mav.addObject("totalPages", totalPages);
        mav.addObject("workers", workers);
        mav.addObject("contextPath", "/admin/workers");
        mav.addObject("verified", verified);
        return mav;
    }

    @RequestMapping("/unverified-workers")
    public ModelAndView unverifiedWorkers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        LOGGER.info("User arriving at '/admin/unverified-workers'");

        final ModelAndView mav = new ModelAndView("admin/views/adminWorkerRequestHandler");

        mav.addObject("panelOption", "WorkerRequests");
        mav.addObject("inWorkers", false);
        mav.addObject("page", page);
        mav.addObject("totalPages", ws.getTotalWorkerPagesByCriteria(null, new long[] {getLoggedUser().getNeighborhood().getNeighborhoodId()}, size, WorkerRole.UNVERIFIED_WORKER, WorkerStatus.none));
        mav.addObject("workers", ws.getWorkersByCriteria(page, size, null, getLoggedUser().getNeighborhood().getNeighborhoodId(), getLoggedUser().getUserId(), WorkerRole.UNVERIFIED_WORKER, WorkerStatus.none));
        mav.addObject("contextPath", "/admin/unverified-workers");
        return mav;
    }

    @RequestMapping("/reject-worker")
    public ModelAndView rejectWorker(
            @RequestParam("workerId") long workerId
    ) {
        nws.rejectWorkerFromNeighborhood(workerId, getLoggedUser().getNeighborhood().getNeighborhoodId());
        return new ModelAndView("redirect:/admin/unverified-workers");
    }

    @RequestMapping("/unverify-worker")
    public ModelAndView unverifyWorker(
            @RequestParam("workerId") long workerId
    ) {
        nws.unverifyWorkerFromNeighborhood(workerId, getLoggedUser().getNeighborhood().getNeighborhoodId());
        return new ModelAndView("redirect:/admin/unverified-workers");
    }

    @RequestMapping("/verify-worker")
    public ModelAndView verifyWorker(
            @RequestParam("workerId") long workerId
    ) {
        nws.verifyWorkerInNeighborhood(workerId, getLoggedUser().getNeighborhood().getNeighborhoodId());
        return new ModelAndView("redirect:/admin/unverified-workers");
    }

    @RequestMapping("/verify-rejected-worker")
    public ModelAndView verifyRejectedWorker(
            @RequestParam("workerId") long workerId
    ) {
        nws.verifyWorkerInNeighborhood(workerId, getLoggedUser().getNeighborhood().getNeighborhoodId());
        return new ModelAndView("redirect:/admin/workers");
    }

    // ----------------------------------------------------- PUBLISH ---------------------------------------------------

    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public ModelAndView publishAdminForm(
            @ModelAttribute("publishForm") final PublishForm publishForm
    ) {
        LOGGER.info("User arriving at '/admin/publish'");

        final ModelAndView mav = new ModelAndView("admin/views/adminPublish");

        mav.addObject("panelOption", "PublishAdmin");
        mav.addObject("tagList", ts.getTags(getLoggedUser().getNeighborhood().getNeighborhoodId()));
        mav.addObject("channelList", chs.getAdminChannels(getLoggedUser().getNeighborhood().getNeighborhoodId()));
        return mav;
    }


    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ModelAndView publishAdmin(
            @Valid @ModelAttribute("publishForm") final PublishForm publishForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Publish Form");
            return publishAdminForm(publishForm);
        }

        ps.createAdminPost(getLoggedUser().getNeighborhood().getNeighborhoodId(), publishForm.getSubject(), publishForm.getMessage(), getLoggedUser().getUserId(), publishForm.getChannel(), publishForm.getTags(), publishForm.getImageFile());
        PublishForm clearedForm = new PublishForm();
        ModelAndView mav = new ModelAndView("admin/views/adminPublish");
        mav.addObject("showSuccessMessage", true);
        mav.addObject("channelList", chs.getAdminChannels(getLoggedUser().getNeighborhood().getNeighborhoodId()));
        mav.addObject("publishForm", clearedForm);

        return mav;
    }

    // ------------------------------------------------- AMENITIES -----------------------------------------------------

    @RequestMapping(value = "/amenities", method = RequestMethod.GET)
    public ModelAndView adminAmenities(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/admin/amenities'");

        ModelAndView mav = new ModelAndView("admin/views/amenitiesPanel");

        List<Amenity> amenities = as.getAmenities(getLoggedUser().getNeighborhood().getNeighborhoodId(), page, size);

        mav.addObject("totalPages", as.getTotalAmenitiesPages(getLoggedUser().getNeighborhood().getNeighborhoodId(), size));
        mav.addObject("daysPairs", DayOfTheWeek.DAY_PAIRS);
        mav.addObject("timesPairs", StandardTime.TIME_PAIRS);
        mav.addObject("amenities", amenities);
        mav.addObject("panelOption", "Amenities");
        mav.addObject("contextPath", "/admin/amenities");
        mav.addObject("page", page);

        return mav;
    }

    @RequestMapping(value = "/delete-amenity/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView deleteAmenity(
            @PathVariable(value = "id") Long amenityId
    ) {
        ModelAndView mav = new ModelAndView("redirect:/admin/amenities");
        as.deleteAmenity(amenityId);
        return mav;
    }

    @RequestMapping(value = "/create-amenity", method = RequestMethod.GET)
    public ModelAndView createAmenityForm(
            @ModelAttribute("amenityForm") final AmenityForm amenityForm
    ) {
        LOGGER.info("User arriving at '/admin/create-amenity'");

        ModelAndView mav = new ModelAndView("admin/views/amenitiesCreate");

        mav.addObject("daysPairs", DayOfTheWeek.DAY_PAIRS);
        mav.addObject("timesPairs", StandardTime.TIME_PAIRS);
        return mav;
    }

    @RequestMapping(value = "/create-amenity", method = RequestMethod.POST)
    public ModelAndView createAmenity(
            @RequestParam(value = "selectedShifts", required = false) List<String> selectedShifts,
            @Valid @ModelAttribute("amenityForm") final AmenityForm amenityForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Create Amenity Form");
            return createAmenityForm(amenityForm);
        }


        as.createAmenity(amenityForm.getName(), amenityForm.getDescription(), getLoggedUser().getNeighborhood().getNeighborhoodId(), selectedShifts);
        return new ModelAndView("redirect:/admin/amenities");
    }
    @RequestMapping(value = "/edit-amenity/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView editAmenity(
            @PathVariable(value = "id") Long amenityId,
            @ModelAttribute("amenityForm") final AmenityForm amenityForm
    ) {
        LOGGER.info("User arriving at '/admin/edit-amenity'");

        ModelAndView mav = new ModelAndView("admin/views/amenitiesEdit");
        mav.addObject("amenity", as.findAmenityById(amenityId).orElseThrow(() -> new NotFoundException("Amenity not found")));
        mav.addObject("daysPairs", DayOfTheWeek.DAY_PAIRS);
        mav.addObject("timesPairs", StandardTime.TIME_PAIRS);
        return mav;
    }

    @RequestMapping(value = "/edit-amenity/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView saveEditAmenity(
            @PathVariable(value = "id") Long amenityId,
            @RequestParam(value = "selectedShifts", required = false) List<String> selectedShifts,
            @Valid @ModelAttribute("amenityForm") final AmenityForm amenityForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Edit Amenity Form ");
            return createAmenityForm(amenityForm);
        }
        as.updateAmenity(amenityId, amenityForm.getName(), amenityForm.getDescription());
        avs.updateAvailability(amenityId, selectedShifts);
        return new ModelAndView("redirect:/admin/amenities");
    }
    // -------------------------------------------------- CALENDAR -----------------------------------------------------

    @RequestMapping(value = "/add-event", method = RequestMethod.GET)
    public ModelAndView eventForm(
            @ModelAttribute("eventForm") final EventForm eventForm
    ) {
        LOGGER.info("User arriving at '/admin/add-event'");

        return new ModelAndView("admin/views/eventsCreate");
    }

    @RequestMapping(value = "/add-event", method = RequestMethod.POST)
    public ModelAndView addEvent(
            @Valid @ModelAttribute("eventForm") final EventForm eventForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Event Form");
            return eventForm(eventForm);
        }
        es.createEvent(eventForm.getName(), eventForm.getDescription(), eventForm.getDate(), eventForm.getStartTime(), eventForm.getEndTime(), getLoggedUser().getNeighborhood().getNeighborhoodId());
        ModelAndView mav = new ModelAndView("redirect:/calendar?timestamp=" + eventForm.getDate().getTime());
        mav.addObject("showSuccessMessage", true);
        return mav;
    }

    @RequestMapping(value = "/delete-event/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView deleteEvent(
            @PathVariable(value = "id") int eventId,
            @RequestParam(required = false, defaultValue = "0") long timestamp
    ) {

        ModelAndView mav = new ModelAndView("redirect:/calendar?timestamp=" + timestamp);
        es.deleteEvent(eventId);
        return mav;
    }

    // ------------------------------------------------ CONTACT --------------------------------------------------------

    @RequestMapping(value = "/create-contact", method = RequestMethod.GET)
    public ModelAndView createContact(
            @ModelAttribute("contactForm") final ContactForm contactForm
    ) {
        LOGGER.info("User arriving at '/admin/create-contact'");
        return new ModelAndView("admin/views/informationContactCreate");
    }

    @RequestMapping(value = "/create-contact", method = RequestMethod.POST)
    public ModelAndView createContact(
            @Valid @ModelAttribute("contactForm") final ContactForm contactForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Contact Form");
            return createContact(contactForm);
        }
        cos.createContact(getLoggedUser().getNeighborhood().getNeighborhoodId(), contactForm.getContactName(), contactForm.getContactAddress(), contactForm.getContactPhone());
        return new ModelAndView("redirect:/admin/information");
    }

    @RequestMapping(value = "/delete-contact/{id}", method = RequestMethod.GET)
    public ModelAndView deleteContact(
            @PathVariable(value = "id") int contactId
    ) {
        LOGGER.info("User arriving at '/admin/delete-contact/{}'", contactId);
        ModelAndView mav = new ModelAndView("redirect:/admin/information");
        cos.deleteContact(contactId);
        return mav;
    }


    // ------------------------------------------------ RESOURCES ------------------------------------------------------

    @RequestMapping(value = "/create-resource", method = RequestMethod.GET)
    public ModelAndView createResourceForm(
            @ModelAttribute("resourceForm") final ResourceForm resourceForm
    ) {
        LOGGER.info("User arriving at '/admin/create-resource'");
        return new ModelAndView("admin/views/informationResourceCreate");
    }

    @RequestMapping(value = "/create-resource", method = RequestMethod.POST)
    public ModelAndView createResource(
            @Valid @ModelAttribute("resourceForm") final ResourceForm resourceForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Resource Form");
            return createResourceForm(resourceForm);
        }
        res.createResource(getLoggedUser().getNeighborhood().getNeighborhoodId(), resourceForm.getTitle(), resourceForm.getDescription(), resourceForm.getImageFile());
        return new ModelAndView("redirect:/admin/information");
    }

    @RequestMapping(value = "/delete-resource/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView deleteResource(
            @PathVariable(value = "id") int resourceId
    ) {
        LOGGER.info("User arriving at '/admin/delete-resource/{}'", resourceId);
        ModelAndView mav = new ModelAndView("redirect:/admin/information");
        res.deleteResource(resourceId);
        return mav;
    }
}
