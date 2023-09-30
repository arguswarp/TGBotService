package com.argus.service.impl;

import com.argus.dao.AppUserDAO;
import com.argus.dao.RawDataDAO;
import com.argus.entity.AppDocument;
import com.argus.entity.AppUser;
import com.argus.entity.RawData;
import com.argus.service.FileService;
import com.argus.service.MainService;
import com.argus.service.ProducerService;
import com.argus.service.enums.ServiceCommand;
import com.argus.service.exceptions.UploadFileException;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static com.argus.entity.enums.UserState.BASIC_STATE;
import static com.argus.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static com.argus.service.enums.ServiceCommand.CANCEL;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDao;
    private final FileService fileService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDao, FileService fileService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDao = appUserDao;
        this.fileService = fileService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getUserState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommand.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO implement email handling
        } else {
            log.error("Unknown user state " + userState);
            output = "Unknown error! Enter /cancel and try again!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);


    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppDocument document = fileService.processDoc(update.getMessage());
            //TODO implement link generation
            var answer = "Document is successfully uploaded! Link to download: http://test.ru/get-doc/777";
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(e);
            String error = "Unfortunately can't download. Try again later.";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getUserState();
        if (!appUser.getIsActive()) {
            var error = "Register or activate your account to download content";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Cancel current command with /cancel to send files";
            sendAnswer(error, chatId);
        }
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        //TODO implement save photo
        var answer = "Photo is successfully uploaded! Link to download: http://test.ru/get-photo/777";
        sendAnswer(answer, chatId);
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String command) {
        var serviceCommand = Optional.ofNullable(ServiceCommand.fromValue(command));
        if (serviceCommand.isPresent()) {
            switch (serviceCommand.get()) {
                case REGISTRATION -> {
                    //TODO add registration
                    return "Temporally unavailable";
                }
                case HELP -> {
                    return help();
                }
                case START -> {
                    return "Hello there! Enter /help to see available commands";
                }
            }
        }
        return "Unknown command! Enter /help to see available commands";
    }

    private String help() {
        return "List of available commands: \n"
                + "/cancel - cancel current command;\n"
                + "/registration - register user.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserDao.save(appUser);
        return "Command canceled!";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDao.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstname(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO change default value after impl of registration
                    .isActive(true)
                    .userState(BASIC_STATE)
                    .build();
            return appUserDao.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder().event(update).build();
        rawDataDAO.save(rawData);
    }
}
