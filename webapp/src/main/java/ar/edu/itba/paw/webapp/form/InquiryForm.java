package ar.edu.itba.paw.webapp.form;


import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class InquiryForm {

    @Size(max = 500)
    private String questionMessage;

    @Size(max = 500)
    private String answerMessage;

    public String getQuestionMessage() {
        return questionMessage;
    }

    public void setQuestionMessage(String questionMessage) {
        this.questionMessage = questionMessage;
    }

    public String getAnswerMessage() {
        return answerMessage;
    }

    public void setAnswerMessage(String answerMessage) {
        this.answerMessage = answerMessage;
    }

    @Override
    public String toString() {
        return "RequestForm{" +
                ", questionMessage='" + questionMessage + '\'' +
                '}';
    }
}
