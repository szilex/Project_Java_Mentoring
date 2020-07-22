package com.euvic.mentoring.service;

import javax.mail.MessagingException;

public interface IMailService {

    void sendMail(String to, String subject, String text, boolean isHtmlContent) throws MessagingException;
}
