package uz.pdp.servises;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.bot.BotService;
import uz.pdp.bot.BotState;
import uz.pdp.model.TgUser;
import uz.pdp.model.TwilioVerification;

import static uz.pdp.bot.BotService.generateReplyKeyboardMarkup;
import static uz.pdp.bot.BotService.saveUserChanges;
import static uz.pdp.db.DataBase.twilioVerificationList;

public class TwilioServise {
    private static final String TWILIO_SID = "AC83df35ded23a19c01c0f4febb48bff35";
    private static final String TWILIO_TOKEN = "3d48f4a92b19a17d6574ad470c09c1a6";
    private static final String TWILIO_PHONE = "+16572206792";
    public static String code="";

    public static TwilioVerification getOrCreateTwilioVerification(TgUser user) {
        for (TwilioVerification verification : twilioVerificationList) {
            if (verification.getUser().getChatId().equals(user.getChatId())) {
                return verification;
            }
        }
        TwilioVerification verification = new TwilioVerification(user);
        twilioVerificationList.add(verification);
        return verification;
    }
    public static String sendCode(TgUser user) {
       // Twilio.init(TWILIO_SID, TWILIO_TOKEN);
        code = String.valueOf((int)((Math.random() * (999999 -100000)) + 100000));
        try {
//            Message message = Message.creator(
//                    new PhoneNumber(user.getPhoneNumber()),
//                    new PhoneNumber(TWILIO_PHONE),
//                    "Tasdiqlash kodi : " + code ).create();
         TwilioVerification verification = getOrCreateTwilioVerification(user);
        verification.setCode(code);
        verification.setVerified(false);
           saveVerificationChanges(verification);
            return code;
        } catch (Exception e) {
//            TwilioVerification verification = getOrCreateTwilioVerification(user);
//            verification.setCode(code);
//            verification.setVerified(false);
//            saveVerificationChanges(verification);
            return code;
        }
    }
    public static void saveVerificationChanges(TwilioVerification changedVerification) {
        for (TwilioVerification verification : twilioVerificationList) {
            if (verification.getId().equals(changedVerification.getId())) {
               twilioVerificationList.remove(verification);
               twilioVerificationList.add(changedVerification);
            }
        }
    }

    public static boolean getVerifiedCode(Update update, TgUser user) {
        TwilioVerification verification = getOrCreateTwilioVerification(user);
        if (verification.getCode().equals(update.getMessage().getText())) {
            verification.setVerified(true);
            saveVerificationChanges(verification);
            user.setState(BotState.SHOW_MENU);
            saveUserChanges(user);
            return true;
        } else {
            return false;
        }
    }

}
