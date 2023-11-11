package ar.edu.itba.paw.webapp.form;


import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ReplyForm {

    @NotBlank
    //validator para ver que exista el inquiry?????????????
    private long inquiryId;

    @NotBlank
    @Size(max = 500)
    private String replyMessage;

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public long getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(long inquiryId) {
        this.inquiryId = inquiryId;
    }

    @Override
    public String toString() {
        return "ReplyForm{" +
                "inquiryId=" + inquiryId +
                ", replyMessage='" + replyMessage + '\'' +
                '}';
    }
}
