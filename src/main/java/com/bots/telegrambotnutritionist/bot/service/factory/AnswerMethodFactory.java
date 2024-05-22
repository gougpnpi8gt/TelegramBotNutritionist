package com.bots.telegrambotnutritionist.bot.service.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.GetMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class AnswerMethodFactory {

    public SendPhoto getSendPhoto(long chatId,
                                  String pathPicture,
                                  String caption,
                                  ReplyKeyboard keyboard
    ) {
        File photo = null;
        try {
            photo = ResourceUtils.getFile("classpath:" + pathPicture);
        } catch (FileNotFoundException e) {
            String error = e.getMessage();
            log.error(error);
            System.out.println(error);
        }
        return SendPhoto.builder()
                .photo(new InputFile(Objects.requireNonNull(photo)))
                .chatId(chatId)
                .caption(caption)
                .replyMarkup(keyboard)
                .build();
    }

    public SendPhoto getSendPhoto(long chatId,
                                  String pathPicture,
                                  ReplyKeyboard keyboard
    ) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(new File(pathPicture)))
                .replyMarkup(keyboard)
                .build();
    }

    public SendMessage getSendMessage(
            Long chatId,
            String text,
            ReplyKeyboard keyboard
    ) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboard)
                .disableWebPagePreview(true)
                .build();
    }

    public EditMessageText getEditMessageText(
            CallbackQuery callbackQuery,
            String text,
            InlineKeyboardMarkup keyboard
    ) {
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(text)
                .replyMarkup(keyboard)
                .disableWebPagePreview(true)
                .build();
    }


    public EditMessageMedia getEditMessagePhoto(CallbackQuery callbackQuery,
                                                String pathImage,
                                                InlineKeyboardMarkup keyboard){
        InputStream inputStream = getClass().getResourceAsStream(pathImage);
        InputMediaPhoto mediaPhoto = new InputMediaPhoto();
        mediaPhoto.setMedia(String.valueOf(inputStream));
        return EditMessageMedia.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .media(mediaPhoto)
                .replyMarkup(keyboard)
                .build();
    }
    public EditMessageText getEditeMessageText(Long chatId,
                                               Integer messageId,
                                               String text) {
        return EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .disableWebPagePreview(true)
                .build();
    }

    public CopyMessage getCopyMessage(Long chatId, Long fromChatId, Integer messageId) {
        return CopyMessage.builder()
                .fromChatId(fromChatId)
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }

    public CopyMessage getCopyMessage(Long chatId, Long fromChatId, Integer messageId, ReplyKeyboard replyKeyboard) {
        return CopyMessage.builder()
                .fromChatId(fromChatId)
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(replyKeyboard)
                .build();
    }

    public EditMessageCaption getEditMessageCaption(Long chatId,
                                                    Integer messageId,
                                                    String caption) {
        return EditMessageCaption.builder()
                .chatId(chatId)
                .caption(caption)
                .messageId(messageId)
                .build();
    }

    public EditMessageReplyMarkup getEditMessageReplyMarkup(CallbackQuery callbackQuery,
                                                            InlineKeyboardMarkup inlineKeyboardMarkup) {
        return EditMessageReplyMarkup.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    public DeleteMessage getDeleteMessage(
            Long chatId,
            Integer messageId
    ) {
        return DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }

    public AnswerCallbackQuery getAnswerCallbackQuery(String callbackQueryId,
                                                      String text) {
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .text(text)
                .build();
    }
    public DeleteMyCommands getDeleteMyCommands(Long chatId){
        return DeleteMyCommands.builder()
                .scope(BotCommandScopeChat.builder()
                        .chatId(chatId)
                        .build())
                .build();
    }
    public SetMyCommands getBotCommandScopeChat(Long chatId,
                                                Map<String, String> commands) {
        List<BotCommand> botCommands = new ArrayList<>();
        for (String commandName : commands.keySet()) {
            botCommands.add(
                    BotCommand.builder()
                            .command(commandName)
                            .description(commands.get(commandName))
                            .build()
            );
        }
        return SetMyCommands.builder()
                .scope(BotCommandScopeChat.builder()
                        .chatId(chatId)
                        .build())
                .commands(botCommands)
                .build();
    }

    public SendMediaGroup getSendMediaGroup(Long chatId, List<File> videoFiles){
        List<InputMedia> medias = new ArrayList<>();
        for (File videoFile : videoFiles) {
            InputMediaVideo inputMediaVideo = new InputMediaVideo();
            inputMediaVideo.setMedia(videoFile, videoFile.getName());
            medias.add(inputMediaVideo);
        }
        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setChatId(chatId);
        mediaGroup.setMedias(medias);
        return mediaGroup;
    }

    public SendVideo getSendVideo(Long chatId,
                                  String path,
                                  String caption,
                                  ReplyKeyboard keyboard){
        return SendVideo.builder()
                .chatId(chatId)
                .video(new InputFile(
                        new File(path)
                        )
                )
                .caption(caption)
                .replyMarkup(keyboard)
                .build();
    }
    public EditMessageMedia getEditMessageVideo(CallbackQuery callbackQuery, String path, InlineKeyboardMarkup keyboard){
        return EditMessageMedia.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .media(new InputMediaVideo(path))
                .replyMarkup(keyboard)
                .build();
    }
}