package uz.pdp.bot;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.pdp.db.DataBase;
import uz.pdp.model.Brands;
import uz.pdp.model.TgUser;
import uz.pdp.servises.BasketServise;
import uz.pdp.servises.BrandServise;
import uz.pdp.servises.ProductServise;
import uz.pdp.servises.TwilioServise;

import static uz.pdp.bot.BotService.generateInlineKeyboardMarkup;
import static uz.pdp.bot.BotService.getChatId;
import static uz.pdp.servises.TwilioServise.code;


public class MyBot extends TelegramLongPollingBot {
    public static Integer s;
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            TgUser user = BotService.creatorGetUser(getChatId(update));
            if (update.getMessage().hasContact()) {
                if (user.getState().equals(BotState.SHARE_CONTACT)) {
                 deleteMessage(user);

                  execute(BotService.getContact(update.getMessage().getContact(), user));


                }
            }
            else if (update.getMessage().hasLocation()) {
                if (user.getState().equals(BotState.SHARE_LOCATION)) {
                    deleteMessage(user);
                    deleteM(user);
                    user.setMessageId(execute(BotService.getLocationOrAddress(update, user)).getMessageId());
                    deleteUp(update,user);

                }
            }

            String text = update.getMessage().getText();
            if (text.equals("/start")) {

                if(user.getState().equals(BotState.START)||user.getState().equals(BotState.NAME)
                ||user.getState().equals(BotState.SHARE_CONTACT)||user.getState().equals(BotState.ENTER_CODE)){
                  execute(BotService.start(update)).getMessageId();
                }
             else  {
                 user.setMessageId(execute(BotService.showMenu(BotService.creatorGetUser(user.getChatId()))).getMessageId());
                }

            }
            else if (user.getState().equals(BotState.NAME)) {
                String chatId = getChatId(update);
                user.setFio(update.getMessage().getText());
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Sizning ismingiz \"" + update.getMessage().getText() + "\" to'g'ri yozildimi?");
                sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(user));
               user.setMessageId(execute(sendMessage).getMessageId());;

                BotService.saveUserChanges(user);
            }
            else if (user.getState().equals(BotState.ENTER_CODE)) {
                boolean verifiedCode = TwilioServise.getVerifiedCode(update, user);
                if (verifiedCode) {
                    user.setMessageId(execute(BotService.showMenu(BotService.creatorGetUser(user.getChatId()))).getMessageId());
                } else {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(user.getChatId());
                    sendMessage.setText(
                            "Kiritilgan kod xato. Iltimos, to'g'ri kod kiriting ❗"+code);
                    deleteUp(update,user);
                    execute(sendMessage);

                }

            }
            else if (user.getState().equals(BotState.ADD_NUMBER1)) {

                user.setState(BotState.ADD_NUMBER);
                BotService.saveUserChanges(user);
                deleteM(user);
                user.setMessageId(execute(BasketServise.realAdd(user, text)).getMessageId());
                Thread.sleep(2000);
                deleteM(user);
                user.setMessageId(execute(BasketServise.chooseEditBasket(user)).getMessageId());
                deleteUp(update,user);
            }
            else if (user.getState().equals(BotState.SHARE_LOCATION)) {
                deleteM(user);
                deleteMessage(user);
                user.setMessageId(execute(BotService.getLocationOrAddress(update, user)).getMessageId());
                deleteUp(update,user);

            }
            else {
              deleteUp(update,user);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(user.getChatId());
                sendMessage.setText("Bunday amal mavjud emas!");
                user.setMessageId(execute(sendMessage).getMessageId());
                Thread.sleep(1000);
                deleteM(user);

            }

        }

        else if (update.hasCallbackQuery()) {
            TgUser tgUser = BotService.creatorGetUser(getChatId(update));
            String data = update.getCallbackQuery().getData();
            if (data.equals("REGISTRATSIYA")) {
                if (tgUser.getState().equals(BotState.START)) {
                    deleteM(tgUser);
                    tgUser.setState(BotState.NAME);
                    BotService.saveUserChanges(tgUser);
                   execute(BotService.kirish(update));

                }
            }
            else if (data.equals("Back to Main")) {

                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(tgUser.getChatId());
                deleteM(tgUser);
                if (s != null) {
                    deleteMessage.setMessageId(s);
                    execute(deleteMessage);
                    s = null;
                }

                    tgUser.setPageNum(0);
                    tgUser.setState(BotState.SHOW_MENU);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(BotService.showMenu(BotService.creatorGetUser(tgUser.getChatId()))).getMessageId());


            }
            else if (data.equals("YES")) {
                if (tgUser.getState().equals(BotState.BUY)) {
                    deleteM(tgUser);
                    tgUser.setState(BotState.CHOOSE_MONEY);
                    BotService.saveUserChanges(tgUser);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(tgUser.getChatId());
                    sendMessage.setText("Bo'limlardan birini tanlang:");
                    sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
                    tgUser.setMessageId(execute(sendMessage).getMessageId());
                }
            }
            else if (data.equals("NO")) {
                if (tgUser.getState().equals(BotState.BUY)) {
                    tgUser.setPageNum(0);
                    tgUser.setState(BotState.SHOW_MENU);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(BotService.showMenu(BotService.creatorGetUser(tgUser.getChatId()))).getMessageId());


                }
            }
            else if (data.equals("HA")) {
                if (tgUser.getState().equals(BotState.NAME)) {
                    deleteM(tgUser);
                    tgUser.setState(BotState.SHARE_CONTACT);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(BotService.kirish(update)).getMessageId());                    deleteUp(update,tgUser);


                }
            }
            else if (data.equals("YOQ")) {
                if (tgUser.getState().equals(BotState.NAME)) {

                    deleteM(tgUser);
                    execute(BotService.kirish(update));
                }
            }

            else if (data.equals("SHOW_BRANDS")) {
                deleteM(tgUser);
                if (tgUser.getState().equals(BotState.SHOW_MENU)) {
                    tgUser.setState(BotState.SHOW_BRANDS);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(
                            execute(BrandServise.showBrands(update))
                                    .getMessageId());
                }
            }
            else if (data.equals("SOCIAL_NETS")) {
                deleteM(tgUser);
                if (tgUser.getState().equals(BotState.SHOW_MENU)) {

                    tgUser.setState(BotState.SOCIAL_NETS);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(BotService.showSocialNets(update)).getMessageId());
                }
            }
            else if (data.equals("ABOUT_US")) {

                if (tgUser.getState().equals(BotState.SHOW_MENU)) {
                    deleteM(tgUser);
                    tgUser.setMessageId(execute(BotService.aboutUs(update)).getMessageId());
                }
            }

            else if (data.equals("SHOW_BASKET")) {
                deleteM(tgUser);
                if (tgUser.getState().equals(BotState.SHOW_MENU)) {
                    tgUser.setState(BotState.SHOW_BASKET);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(BasketServise.savatcham(update)).getMessageId());
                    if (tgUser.getState().equals(BotState.SHOW_MENU)) {

                        Thread.sleep(2000);
                        deleteM(tgUser);
                        tgUser.setMessageId(execute(BotService.showMenu(BotService.creatorGetUser(tgUser.getChatId()))).getMessageId());


                    }

                }
            }
            else if (data.equals("GET_LOCATION")) {
                if (tgUser.getState().equals(BotState.ABOUT_US)||tgUser.getState().equals(BotState.BUY_PESHKOM)) {
                    s = tgUser.getMessageId();
                    tgUser.setState(BotState.GET_LOCATION);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(BotService.sendCompLocation(update)).getMessageId());

                }
            }
            else if (data.equals("BOGLANISH")) {
                if (tgUser.getState().equals(BotState.ABOUT_US) || tgUser.getState().equals(BotState.BUY_PESHKOM)) {
                    s = tgUser.getMessageId();
                   BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(BotService.sendCompTel(update)).getMessageId());
                }
            }

            else if (data.equals("SHOW_ORDER_HISTORY")) {
                deleteM(tgUser);
                if (tgUser.getState().equals(BotState.SHOW_MENU)) {

                    tgUser.setState(BotState.FINISH_ORDER);
                    tgUser.setMessageId(execute(BotService.showhistory(tgUser)).getMessageId());
                    if (tgUser.getState().equals(BotState.SHOW_MENU)) {
                        Thread.sleep(2000);
                        deleteM(tgUser);
                        tgUser.setMessageId(execute(BotService.showMenu(BotService.creatorGetUser(tgUser.getChatId()))).getMessageId());

                    } else {
                        s = tgUser.getMessageId();
                        tgUser.setMessageId(execute(BotService.sendDocument(tgUser)).getMessageId());
                    }
                }
            }
            else if (data.equals("DASTAVKA")) {
                if (tgUser.getState().equals(BotState.CHOOSE_MONEY)) {
                    deleteM(tgUser);
                    tgUser.setMessageId(execute(BotService.askLocation(tgUser)).getMessageId());
                }

            }
            else if (data.equals("SOTIB_OLISH")) {
                if (tgUser.getState().equals(BotState.CHOOSE_MONEY)) {
                    deleteM(tgUser);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(tgUser.getChatId());
                    sendMessage.setText("\uD83D\uDC47BO'LIMLARDAN BIRINI TANLANG\uD83D\uDC47");
                    tgUser.setState(BotState.BUY_PESHKOM);
                    sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
                    tgUser.setMessageId(execute(sendMessage).getMessageId());

                }

            }

            if (data.equals("Next")) {
                if (tgUser.getState().equals(BotState.SHOW_PRODUCTS)) {
                    deleteM(tgUser);
                    tgUser.setPageNum(tgUser.getPageNum() + 1);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(ProductServise.showProducts(BotService.creatorGetUser(tgUser.getChatId()))).getMessageId());
                }
            }
            else if (data.equals("Back")) {
                if (tgUser.getState().equals(BotState.SHOW_PRODUCTS)) {
                    deleteM(tgUser);
                    tgUser.setPageNum(tgUser.getPageNum() - 1);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(ProductServise.showProducts(BotService.creatorGetUser(tgUser.getChatId()))).getMessageId());
                }
            }
            else if (data.equals("Savat")) {
                if (tgUser.getState().equals(BotState.SHOW_PRODUCTS)) {
                    Integer id = tgUser.getMessageId();
                    tgUser.setMessageId(execute(BasketServise.savatcha(tgUser)).getMessageId());
                    Thread.sleep(2000);
                    deleteM(tgUser);
                    tgUser.setMessageId(id);


                }
            }
            else if (data.equals("EDIT")) {
                if (tgUser.getState().equals(BotState.SHOW_BASKET)) {
                    deleteM(tgUser);
                    tgUser.setState(BotState.EDIT);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(BasketServise.editBasket(tgUser)).getMessageId());
                }
            }
            else if (data.equals("BUY")) {
                if (tgUser.getState().equals(BotState.SHOW_BASKET)||tgUser.getState().equals(BotState.SHOW_MENU)) {
                    deleteM(tgUser);
                    tgUser.setState(BotState.BUY);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(ProductServise.buy(tgUser)).getMessageId());
                    if (tgUser.getState().equals(BotState.SHOW_MENU)) {
                        Thread.sleep(2000);
                        deleteM(tgUser);
                        tgUser.setMessageId(execute(BotService.showMenu(BotService.creatorGetUser(tgUser.getChatId()))).getMessageId());
                    }
                }
            }
            else if (data.equals("DELETE")) {
                if (tgUser.getState().equals(BotState.EDIT)) {
                    deleteM(tgUser);
                    tgUser.setState(BotState.DELETE_BAS);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(BasketServise.chooseEditBasket(tgUser)).getMessageId());
                }
            }
            else if (data.equals("ADD_NUMBER")) {
                if (tgUser.getState().equals(BotState.EDIT)) {
                    deleteM(tgUser);
                    tgUser.setState(BotState.ADD_NUMBER);
                    BotService.saveUserChanges(tgUser);
                    tgUser.setMessageId(execute(BasketServise.chooseEditBasket(tgUser)).getMessageId());
                }

            }
            else if (tgUser.getState().equals(BotState.ADD_NUMBER)) {
                deleteM(tgUser);
                tgUser.setState(BotState.ADD_NUMBER1);
                BotService.saveUserChanges(tgUser);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(tgUser.getChatId());
                String s = data + "dan qancha miqdorda olmoqchisiz?";
                sendMessage.setText(s);
                tgUser.setMessageId(execute(sendMessage).getMessageId());
            }
            else if (tgUser.getState().equals(BotState.DELETE_BAS)) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(tgUser.getChatId());
                if (tgUser.getMessageId() != null) {
                    deleteMessage.setMessageId(tgUser.getMessageId());
                    execute(deleteMessage);
                }
                tgUser.setMessageId(execute(BasketServise.deleteBas(tgUser, data)).getMessageId());
                deleteMessage.setChatId(tgUser.getChatId());
                if (tgUser.getMessageId() != null) {
                    deleteMessage.setMessageId(tgUser.getMessageId());
                    execute(deleteMessage);
                }
                tgUser.setMessageId(execute(BasketServise.chooseEditBasket(tgUser)).getMessageId());

            }
            else if (data.startsWith("PAY_TYPE:")) {
                deleteM(tgUser);
                tgUser.setMessageId(execute(BotService.getPayType(update, tgUser)).getMessageId());

            }
            else
                if (tgUser.getState().equals(BotState.SHOW_BRANDS)) {
                        for (Brands br : DataBase.brandsList) {
                        if (data.equals(br.getBrand())) {
                          deleteM(tgUser);
                            tgUser.setBrands(br.getBrand());
                            tgUser.setState(BotState.SHOW_PRODUCTS);
                            BotService.saveUserChanges(tgUser);
                            tgUser.setMessageId(execute(ProductServise.showProducts(BotService.creatorGetUser(tgUser.getChatId()))).getMessageId());

                            break;
                        }
                    }

                }



        }

    }

    @Override
    public String getBotUsername() {
        return "tajriba2323bot";
    }

    @Override
    public String getBotToken() {
        return "5005447561:AAGF8qziuxA0oA4GT1G9-NgedNbjGsIncW4";
    }
    @SneakyThrows
    public void deleteMessage(TgUser user) {
        SendMessage sendMessageRemove = new SendMessage();
        sendMessageRemove.setChatId(user.getChatId());
        sendMessageRemove.setText("wait... ");
        sendMessageRemove.setReplyMarkup(new ReplyKeyboardRemove(true));
        Message message = execute(sendMessageRemove);
        Thread.sleep(1000);
        DeleteMessage deleteMessage = new DeleteMessage(user.getChatId(), message.getMessageId());
        execute(deleteMessage);
    }
    @SneakyThrows
    public void deleteM(TgUser user){
    DeleteMessage deleteMessage = new DeleteMessage();
    deleteMessage.setChatId(user.getChatId());
    if (user.getMessageId() != null) {
        deleteMessage.setMessageId(user.getMessageId());
       execute(deleteMessage);
    }
}

@SneakyThrows
public void deleteUp(Update update, TgUser user){
    DeleteMessage deleteMessage = new DeleteMessage();
    deleteMessage.setChatId(user.getChatId());
    deleteMessage.setMessageId(update.getMessage().getMessageId());
    execute(deleteMessage);
}
}
