package uz.pdp.servises;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.model.TgUser;

import java.io.File;

import static uz.pdp.bot.BotService.*;

public class BrandServise {
    public static SendPhoto showBrands(Update update) {
        InputFile file = new InputFile(new File("D:\\tggbot\\src\\main\\resources\\photos\\140-1405222_mobile-phone-brand-mobile-phone-brand-logo-hd.png"));
        SendPhoto sendPhoto = new SendPhoto();
        String chatId = getChatId(update);
        TgUser user = creatorGetUser(chatId);
        sendPhoto.setPhoto(file);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption("BRENDLARDAN BIRINI TANLANG");

        sendPhoto.setReplyMarkup(generateInlineKeyboardMarkup(user));

        return sendPhoto;

    }

}
