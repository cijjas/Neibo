package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReferenceValidationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceValidationUtils.class);

    @Autowired
    private ProductService prs;

    @Autowired
    private PostService ps;

    @Autowired
    private InquiryService is;

    @Autowired
    private RequestService rs;

    @Autowired
    private CommentService cs;

    @Autowired
    private BookingService bs;

    @Autowired
    private UserService us;


}
