package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/endpoint")
public class EndpointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointController.class);
    private final SessionUtils sessionUtils;
    private final PostService ps;
    private final UserService us;
    private final NeighborhoodService nhs;
    private final CommentService cs;
    private final TagService ts;
    private final ChannelService chs;
    private final CategorizationService cas;
    private final ImageService is;
    private final AmenityService as;
    private final EventService es;
    private final ResourceService res;
    private final ContactService cos;
    private final LikeService ls;
    private final ProfessionWorkerService pws;

    @Autowired
    public EndpointController(SessionUtils sessionUtils, final PostService ps,
                              final UserService us,
                              final NeighborhoodService nhs,
                              final CommentService cs,
                              final TagService ts,
                              final ChannelService chs,
                              final CategorizationService cas,
                              final ImageService is,
                              final AmenityService as,
                              final EventService es,
                              final ResourceService res,
                              final ContactService cos,
                              final LikeService ls,
                              final ProfessionWorkerService pws) {
        this.sessionUtils = sessionUtils;
        this.is = is;
        this.ps = ps;
        this.us = us;
        this.nhs = nhs;
        this.cs = cs;
        this.ts = ts;
        this.chs = chs;
        this.cas = cas;
        this.as = as;
        this.es = es;
        this.res = res;
        this.cos = cos;
        this.ls = ls;
        this.pws = pws;
    }

    @RequestMapping(value = "/commentById", method = RequestMethod.GET)
    @ResponseBody
    public String getPostComment(
            @RequestParam(value = "id", required = false) int commentId,
            HttpServletResponse response
    ) {
        LOGGER.debug("Requesting information from '/endpoint/commentById'");
        response.setCharacterEncoding("UTF-8"); // Set the character encoding for the response
        response.setContentType("text/plain; charset=UTF-8");
        return cs.findCommentById(commentId).orElseThrow(() -> new NotFoundException("Comment Not Found")).getComment();
    }

    @RequestMapping(value = "/get-event-timestamps", method = RequestMethod.GET)
    @ResponseBody
    public String getEventTimestamps() {
        LOGGER.debug("Requesting information from '/endpoint/get-event-timestamps'");
        return es.getEventTimestampsString(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId());
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> likePost(
            @RequestParam(value = "postId") long postId
    ) {
        LOGGER.debug("Requesting information from '/endpoint/like'");
        long userId = sessionUtils.getLoggedUser().getUserId();
        ls.addLikeToPost(postId, userId);
        return ResponseEntity.ok("{\"message\": \"Post liked successfully.\"}");
    }

    @RequestMapping(value = "/unlike", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> unlikePost(
            @RequestParam(value = "postId") long postId
    ) {
        LOGGER.debug("Requesting information from '/endpoint/unlike'");
        long userId = sessionUtils.getLoggedUser().getUserId();
        ls.removeLikeFromPost(postId, userId);
        return ResponseEntity.ok("{\"message\": \"Post unliked successfully.\"}");
    }

    @RequestMapping(value = "/is-liked", method = RequestMethod.GET)
    @ResponseBody
    public String isLiked(
            @RequestParam(value = "postId") long postId
    ) {
        LOGGER.debug("Requesting information from '/endpoint/is-liked'");
        long userId = sessionUtils.getLoggedUser().getUserId();
        if (ls.isPostLiked(postId, userId)) {
            return "true";
        }
        return "false";
    }

    @RequestMapping(value = "/toggle-dark-mode", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> toggleDarkMode() {
        LOGGER.debug("Requesting information from '/endpoint/toggle-dark-mode'");
        sessionUtils.clearLoggedUser();
        us.toggleDarkMode(sessionUtils.getLoggedUser().getUserId());
        return ResponseEntity.ok("{\"message\": \"Dark mode toggled successfully.\"}");
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    @ResponseBody
    public byte[] getImage(
            @RequestParam(value = "id", required = false) int imageId
    ) {
        LOGGER.debug("Requesting information from '/endpoint/image'");
        return is.getImage(imageId).orElseThrow(() -> new NotFoundException("Image Not Found")).getImage();
    }

    @RequestMapping(value = "/posts", method = RequestMethod.GET)
    @ResponseBody
    public String getPost(
            @RequestParam(value = "id", required = false) int postId
    ) {
        LOGGER.debug("Requesting information from '/endpoint/posts'");
        return ps.findPostById(postId).orElseThrow(() -> new NotFoundException("Post Not Found")).toString();
    }

    @RequestMapping(value = "/profession", method = RequestMethod.GET)
    @ResponseBody
    public String getProfessions(
            @RequestParam(value = "id") long workerId
    ) {
        LOGGER.debug("Requesting information from '/endpoint/profession'");
        return pws.getWorkerProfessionsAsString(workerId);
    }

    @RequestMapping(value = "/user-name", method = RequestMethod.GET)
    @ResponseBody
    public String getUserName(
            @RequestParam(value = "id") long userId
    ) {
        LOGGER.debug("Requesting information from '/endpoint/user-name'");
        return us.findUserById(userId).orElseThrow(() -> new NotFoundException("User not found")).getName();
    }

    @RequestMapping(value = "/neighborhood-name", method = RequestMethod.GET)
    @ResponseBody
    public String getNeighborhoodName(
            @RequestParam(value = "id") long userId
    ) {
        LOGGER.debug("Requesting information from '/endpoint/neighborhood-name'");
        return nhs.findNeighborhoodById(us.findUserById(userId).orElseThrow(() -> new NotFoundException("User not found"))
                .getNeighborhood().getNeighborhoodId()).orElseThrow(() -> new NotFoundException("Neighborhood not found")).getName();
    }

    @RequestMapping(value = "/role", method = RequestMethod.GET)
    @ResponseBody
    public String getUserRole() {
        return sessionUtils.getLoggedUser().getRole().toString();
    }

}
