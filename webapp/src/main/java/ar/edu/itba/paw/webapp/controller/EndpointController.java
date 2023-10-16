package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/endpoint")
public class EndpointController {

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
    private final ReservationService rs;
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
                         final SubscriptionService ss,
                         final CategorizationService cas,
                         final ImageService is,
                         final ReservationService rs,
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
        this.ss = ss;
        this.cas = cas;
        this.as = as;
        this.rs = rs;
        this.es = es;
        this.res = res;
        this.cos = cos;
        this.ls = ls;
        this.pws = pws;
    }

//    @RequestMapping(value = "/comment", method = RequestMethod.GET)
//    @ResponseBody
//    public String getComment(
//            @RequestParam(value = "id", required = false) int postId
//    ) {
//        return cs.findCommentsByPostId(postId).toString();
//    }

    @RequestMapping(value = "/commentById", method = RequestMethod.GET)
    @ResponseBody
    public String getPostComment(
            @RequestParam(value = "id", required = false) int commentId,
            HttpServletResponse response
    ) {
        response.setCharacterEncoding("UTF-8"); // Set the character encoding for the response
        response.setContentType("text/plain; charset=UTF-8");
        return cs.findCommentById(commentId).get().getComment();
    }

    @RequestMapping(value = "/get-event-timestamps", method = RequestMethod.GET)
    @ResponseBody
    public String getEventTimestamps() {
        return es.getEventTimestampsString(sessionUtils.getLoggedUser().getNeighborhoodId());
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> likePost(
            @RequestParam(value = "postId") long postId
    ) {
        long userId = sessionUtils.getLoggedUser().getUserId();
        ls.addLikeToPost(postId, userId);
        return ResponseEntity.ok("{\"message\": \"Post liked successfully.\"}");
    }

    @RequestMapping(value = "/unlike", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> unlikePost(
            @RequestParam(value = "postId") long postId
    ) {

        long userId = sessionUtils.getLoggedUser().getUserId();
        ls.removeLikeFromPost(postId, userId);
        return ResponseEntity.ok("{\"message\": \"Post unliked successfully.\"}");
    }

    @RequestMapping(value = "/is-liked", method = RequestMethod.GET)
    @ResponseBody
    public String isLiked(
            @RequestParam(value = "postId") long postId
    ) {

        long userId = sessionUtils.getLoggedUser().getUserId();
        if(ls.isPostLiked(postId, userId)){
            return "true";
        }
        return "false";
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    @ResponseBody
    public byte[] getImage(
            @RequestParam(value = "id", required = false) int imageId
    ) {
        return is.getImage(imageId).get().getImage();
    }

    @RequestMapping(value = "/posts", method = RequestMethod.GET)
    @ResponseBody
    public String getPost(
            @RequestParam(value = "id", required = false) int postId
    ) {
        return ps.findPostById(postId).get().toString();
    }

    @RequestMapping(value = "/profession", method = RequestMethod.GET)
    @ResponseBody
    public String getProfessions(
            @RequestParam(value = "id") long workerId
    ) {
        List<String> professions = pws.getWorkerProfessions(workerId);
        StringBuilder professionsString = new StringBuilder();
        for (String profession : professions) {
            if (professionsString.length() > 0) {
                professionsString.append(", ");
            }
            professionsString.append(profession);
        }
        return professionsString.toString();
    }

    @RequestMapping(value = "/user-name", method = RequestMethod.GET)
    @ResponseBody
    public String getUserName(
            @RequestParam(value = "id") long userId
    ) {
        return us.findUserById(userId).orElseThrow(() -> new NotFoundException("User not found")).getName();
    }

    @RequestMapping(value = "/neighborhood-name", method = RequestMethod.GET)
    @ResponseBody
    public String getNeighborhoodName(
            @RequestParam(value = "id") long userId
    ) {
        return nhs.findNeighborhoodById(us.findUserById(userId).orElseThrow(() -> new NotFoundException("User not found"))
                        .getNeighborhoodId()).orElseThrow(() -> new NotFoundException("Neighborhood not found")).getName();
    }

}
