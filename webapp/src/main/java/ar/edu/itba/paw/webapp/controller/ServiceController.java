package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.form.EditWorkerProfileForm;
import ar.edu.itba.paw.webapp.form.NeighborhoodsForm;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/services")
public class ServiceController extends GlobalControllerAdvice{

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceController.class);
    private final PostService ps;
    private final NeighborhoodWorkerService nhws;
    private final ProfessionWorkerService pws;
    private final ReviewService rws;
    private final WorkerService ws;


    @Autowired
    public ServiceController(final PostService ps,
                             final UserService us,
                             final NeighborhoodWorkerService nhws,
                             final ProfessionWorkerService pws,
                             final ReviewService rws,
                             final WorkerService ws
    ) {
        super(us);
        this.ps = ps;
        this.nhws = nhws;
        this.pws = pws;
        this.rws = rws;
        this.ws = ws;
    }

    @RequestMapping(value = "/profile/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView serviceProfile(
            @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            @PathVariable(value = "id") long workerId,
            @ModelAttribute("editWorkerProfileForm") final EditWorkerProfileForm editWorkerProfileForm,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "tab", defaultValue = "reviews", required = false) String tab
    ) {
        LOGGER.info("User arriving at '/services/profile/{}'", workerId);

        ModelAndView mav = new ModelAndView("serviceProvider/views/serviceProfile");

        int totalPages;

        if(tab == "posts") {
            totalPages = ps.getWorkerTotalPages(BaseChannel.WORKERS.toString(), size, null, BaseNeighborhood.WORKERS_NEIGHBORHOOD.getId(), PostStatus.none, workerId);
        }
        else{
            totalPages = rws.getReviewsTotalPages(workerId, size);
        }

        int postsSize = ps.getWorkerPostsCountByCriteria(BaseChannel.WORKERS.toString(), null, BaseNeighborhood.WORKERS_NEIGHBORHOOD.getId(), PostStatus.none, workerId);

        mav.addObject("worker", ws.findWorkerById(workerId).orElseThrow(() -> new NotFoundException("Worker not found")));
        mav.addObject("professions", pws.getWorkerProfessions(workerId));
        mav.addObject("reviewsCount", rws.getReviewsCount(workerId));
        mav.addObject("reviews", rws.getReviews(workerId));
        mav.addObject("channel", "Profile");
        mav.addObject("averageRating", rws.getAvgRating(workerId).orElseThrow(() -> new NotFoundException("Average Rating not found")));
        mav.addObject("postList", ps.getWorkerPostsByCriteria(BaseChannel.WORKERS.toString(), page, postsSize, null, BaseNeighborhood.WORKERS_NEIGHBORHOOD.getId(), PostStatus.none, workerId));
        mav.addObject("totalPages", totalPages);
        mav.addObject("contextPath", "/services/profile/" + workerId);
        mav.addObject("page", page);
        return mav;
    }

    @RequestMapping(value = "/profile/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView serviceProfile(
            @PathVariable String id
    ) {
        return new ModelAndView("serviceProvider/views/serviceProfile");
    }

    @RequestMapping(value = "/profile/{id:\\d+}/review", method = RequestMethod.GET)
    public ModelAndView createReview(
            @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            @PathVariable(value = "id") int workerId,
            @ModelAttribute("editWorkerProfileForm") final EditWorkerProfileForm editWorkerProfileForm
    ) {
        LOGGER.info("User arriving at '/services/profile/{}/review'", workerId);

        ModelAndView mav = new ModelAndView("serviceProvider/views/serviceProfile");

        mav.addObject("professions", pws.getWorkerProfessions(workerId));
        mav.addObject("worker", ws.findWorkerById(workerId).orElseThrow(() -> new NotFoundException("Worker not found")));
        mav.addObject("channel", "Profile");
        mav.addObject("reviews", rws.getReviews(workerId));
        mav.addObject("reviewsCount", rws.getReviewsCount(workerId));
        mav.addObject("averageRating", rws.getAvgRating(workerId).orElseThrow(() -> new NotFoundException("Average Rating not found")));
        return mav;
    }

    @RequestMapping(value = "/profile/{id:\\d+}/review", method = RequestMethod.POST)
    public ModelAndView createReview(
            @Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            final BindingResult errors,
            @PathVariable(value = "id") int workerId,
            @ModelAttribute("editWorkerProfileForm") final EditWorkerProfileForm editWorkerProfileForm,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Review Form");
            ModelAndView mav = serviceProfile(reviewForm, workerId, new EditWorkerProfileForm(), page, size, "reviews");
            mav.addObject("openReviewDialog", true);
            return mav;
        }

        rws.createReview(workerId, getLoggedUser().getUserId(), reviewForm.getRating(), reviewForm.getReview());
        return new ModelAndView("redirect:/services/profile/" + workerId);
    }

    @RequestMapping(value = "/profile/edit", method = RequestMethod.GET)
    public ModelAndView editProfile(
            @ModelAttribute("reviewForm") final ReviewForm reviewForm,
            @ModelAttribute("editWorkerProfileForm") final EditWorkerProfileForm editWorkerProfileForm
    ) {
        LOGGER.info("User arriving at '/services/profile/edit'");

        ModelAndView mav = new ModelAndView("serviceProvider/views/serviceProfile");
        long workerId = getLoggedUser().getUserId();

        mav.addObject("worker", ws.findWorkerById(workerId).orElseThrow(() -> new NotFoundException("Worker not found")));
        mav.addObject("professions", pws.getWorkerProfessions(workerId));
        mav.addObject("channel", "Profile" + workerId);
        mav.addObject("reviews", rws.getReviews(workerId));
        mav.addObject("reviewsCount", rws.getReviewsCount(workerId));
        mav.addObject("averageRating", rws.getAvgRating(workerId).orElseThrow(() -> new NotFoundException("Average Rating not found")));
        return mav;

    }

    @RequestMapping(value = "/profile/edit", method = RequestMethod.POST)
    public ModelAndView editProfile(
            @Valid @ModelAttribute("editWorkerProfileForm") final EditWorkerProfileForm editWorkerProfileForm,
            final BindingResult errors,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @ModelAttribute("reviewForm") final ReviewForm reviewForm
    ) {
        long workerId = getLoggedUser().getUserId();
        if (errors.hasErrors()) {
            LOGGER.error("Error in Edit Form");
            ModelAndView mav = serviceProfile(new ReviewForm(), workerId, editWorkerProfileForm, page, size, "reviews");
            mav.addObject("openEditProfileDialog", true);
        }

        ws.updateWorker(workerId, editWorkerProfileForm.getPhoneNumber(), editWorkerProfileForm.getAddress(),
                editWorkerProfileForm.getBusinessName(), editWorkerProfileForm.getImageFile(), editWorkerProfileForm.getBio());

        return new ModelAndView("redirect:/services/profile/" + workerId);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView services(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "profession", required = false) List <String> professions
    ) {
        String profession = "";
        if(professions != null) {
            profession = professions.get(0);
        }

        ModelAndView mav = new ModelAndView("serviceProvider/views/services");
//        Set<Worker> workerList = ws.getWorkersByCriteria(page, size, professions, getLoggedUser().getNeighborhood().getNeighborhoodId(), getLoggedUser().getUserId(), WorkerRole.VERIFIED_WORKER, WorkerStatus.none);
//        mav.addObject("workersList", workerList);
        mav.addObject("channel", "Services");
        mav.addObject("totalPages", ws.getTotalWorkerPagesByCriteria(professions, new long[] {getLoggedUser().getNeighborhood().getNeighborhoodId()}, size, WorkerRole.VERIFIED_WORKER, WorkerStatus.none));
        mav.addObject("contextPath", "/services");
        mav.addObject("page", page);
        mav.addObject("appliedProfessions", professions);
        mav.addObject("professionList", Professions.getProfessionsList());
        mav.addObject("profession", profession);
        mav.addObject("inServices", true);
        return mav;
    }

    @RequestMapping(value = "/neighborhoods", method = RequestMethod.GET)
    public ModelAndView workersNeighborhoods(@ModelAttribute("neighborhoodsForm") final NeighborhoodsForm neighborhoodsForm) {
        LOGGER.info("User arriving at '/services/neighborhoods'");
        ModelAndView mav = new ModelAndView("serviceProvider/views/neighborhoods");
        mav.addObject("channel", "Neighborhoods");
        mav.addObject("associatedNeighborhoods", nhws.getNeighborhoods(getLoggedUser().getUserId()));
        mav.addObject("otherNeighborhoods", nhws.getOtherNeighborhoods(getLoggedUser().getUserId()));
        return mav;
    }

    @RequestMapping(value = "/neighborhoods", method = RequestMethod.POST)
    public ModelAndView addWorkerToNeighborhood(
            @Valid @ModelAttribute("neighborhoodsForm") final NeighborhoodsForm neighborhoodsForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.error("Error in Neighborhoods Form");
            return workersNeighborhoods(neighborhoodsForm);
        }
        long workerId = getLoggedUser().getUserId();
        nhws.addWorkerToNeighborhoods(workerId, neighborhoodsForm.getNeighborhoodIds());
        return new ModelAndView("redirect:/services/neighborhoods");
    }

    @RequestMapping(value = "/neighborhoods/remove/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView removeWorkerFromNeighborhood(
            @PathVariable(value = "id") long neighborhoodId
    ) {
        ModelAndView mav = new ModelAndView("redirect:/services/neighborhoods");
        mav.addObject("channel", "Neighborhood");
        nhws.removeWorkerFromNeighborhood(getLoggedUser().getUserId(), neighborhoodId);
        return mav;
    }

    @RequestMapping(value = "/apply-professions-as-filter", method = RequestMethod.POST)
    public ModelAndView applyProfessionsFilter(
            @RequestParam("professions") String professions
    ) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        String baseUrl = scheme + "://" + serverName + ":" + serverPort + contextPath + "/services";

        return new ModelAndView("redirect:" + pws.createURLForProfessionFilter(professions, baseUrl, getLoggedUser().getNeighborhood().getNeighborhoodId()));
    }

}
