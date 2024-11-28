package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class InquiryDto {

    @NotNull(groups = Null.class)
    @Size(max = 500, groups = Basic.class)
    private String message;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNCreateReferenceConstraint(groups = Authorization.class)
    private String user;

    @Size(max = 500, groups = Basic.class)
    private String reply;

    private Date date;

    private Links _links;

    public static InquiryDto fromInquiry(Inquiry inquiry, UriInfo uriInfo) {
        final InquiryDto dto = new InquiryDto();

        dto.message = inquiry.getMessage();
        dto.reply = inquiry.getReply();
        dto.date = inquiry.getInquiryDate();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(inquiry.getUser().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(inquiry.getProduct().getProductId()))
                .path("inquiries")
                .path(String.valueOf(inquiry.getInquiryId()))
                .build());
        links.setProduct(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(inquiry.getUser().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(inquiry.getProduct().getProductId()))
                .build());
        links.setUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(inquiry.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(inquiry.getUser().getUserId()))
                .build());
        dto.set_links(links);
        return dto;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
