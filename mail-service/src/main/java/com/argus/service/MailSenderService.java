package com.argus.service;

import com.argus.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
