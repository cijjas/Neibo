package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
public class ApiController {

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
    public ApiController(final PostService ps,
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

    @RequestMapping(value = "/comment", method = RequestMethod.GET)
    @ResponseBody
    public String getComment(
            @RequestParam(value = "id", required = false) int postId
    ) {
        return cs.findCommentsByPostId(postId).toString();
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
}
