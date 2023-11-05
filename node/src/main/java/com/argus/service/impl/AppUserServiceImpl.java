package com.argus.service.impl;

import com.argus.dao.AppUserDAO;
import com.argus.dto.MailParams;
import com.argus.entity.AppUser;
import com.argus.entity.enums.UserState;
import com.argus.service.AppUserService;
import com.argus.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Service
@Log4j
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return "You are already registered!";
        } else if (appUser.getEmail() != null) {
            return "Letter have already been sent to your mail." +
                    "Follow the link in the letter for confirmation";
        }
        appUser.setUserState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);

        return "Enter your email:";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddress= new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return "Enter the valid email. Enter /cancel to cancel the command";
        }
        var optional = appUserDAO.findByEmail(email);
        if (optional.isEmpty()) {
            appUser.setEmail(email);
            appUser.setUserState(UserState.BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            var cryptoUserId = cryptoTool.hashOf(appUser.getId());
            var response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                var msg = String.format("Unable to send the mail. ", email);
                log.error(msg);
                appUser.setEmail(null);
                appUserDAO.save(appUser);
                return msg;
            }
            return "Letter was sent to you by email. " +
                    "Follow the link to confirm registration";
        } else {
            return "This has alreadu been used. Enter the valid email." +
            "Enter /cancel to cancel the command";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        var request = new HttpEntity<MailParams>(mailParams, headers);
        return restTemplate.exchange(mailServiceUri, HttpMethod.POST, request, String.class);
    }
}
