package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Inquiry;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;
import java.util.List;

public class InquiryDto {

    private String message;
    private String reply;
    private Date inquiryDate;
    private URI self;
    private URI product; // localhost:8080/neighborhood/{id}
    private URI user; // localhost:8080/amenities/{id}/availability

    public static InquiryDto fromInquiry(Inquiry inquiry, UriInfo uriInfo){
        final InquiryDto dto = new InquiryDto();

        dto.message = inquiry.getMessage();
        dto.reply = inquiry.getMessage();
        dto.inquiryDate = inquiry.getInquiryDate();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("products")
                .path(String.valueOf(inquiry.getProduct().getProductId()))
                .path("inquiries")
                .path(String.valueOf(inquiry.getInquiryId()))
                .build();
        dto.product = uriInfo.getBaseUriBuilder()
                .path("products")
                .path(String.valueOf(inquiry.getProduct().getProductId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("users")
                .path(String.valueOf(inquiry.getUser().getUserId()))
                .build();

        return dto;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
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

    public Date getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(Date inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    public URI getProduct() {
        return product;
    }

    public void setProduct(URI product) {
        this.product = product;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }
}
