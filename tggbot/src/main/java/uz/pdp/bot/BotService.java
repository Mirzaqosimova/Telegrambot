package uz.pdp.bot;

import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.db.DataBase;
import uz.pdp.model.*;
import uz.pdp.model.enums.OrderStatus;
import uz.pdp.model.enums.PayType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static uz.pdp.db.DataBase.tgUserList;
import static uz.pdp.servises.TwilioServise.code;
import static uz.pdp.servises.TwilioServise.sendCode;

public class BotService {



    @SneakyThrows
    public static SendPhoto showMenu(TgUser user) {

InputFile file = new InputFile(new File("src/main/resources/photos/photo_2022-01-02_12-44-41.jpg"));
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(user.getChatId());
        sendPhoto.setPhoto(file);
        user.setState(BotState.SHOW_MENU);
        BotService.saveUserChanges(user);
        sendPhoto.setCaption("\uD83D\uDC47BOLIMLARDAN BIRINI TANLANG\uD83D\uDC47");
            sendPhoto.setReplyMarkup(generateInlineKeyboardMarkup(user));

        return sendPhoto;
    }


    public static SendMessage kirish(Update update) {
        String chatId = getChatId(update);
        TgUser user = creatorGetUser(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (user.getState().equals(BotState.NAME)) {
            sendMessage.setText("Ismingizni kiriting");

            return sendMessage;
        }else
        if (user.getState().equals(BotState.SHARE_CONTACT)) {
            sendMessage.setText("Kontaktingizni jo'nating\uD83D\uDCF2");
            sendMessage.setReplyMarkup(generateReplyKeyboardMarkup(user));
            return sendMessage;
        }else{

            user.setState(BotState.START);
            BotService.saveUserChanges(user);
            sendMessage.setText("Assalomu alaykum \uD83D\uDC4Bhurmatli " + update.getMessage().getChat().getFirstName() +
                    " botdan foydalanish uchun avval ro'yhatdan o'ting!");
            sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(user));
            return sendMessage;
        }

    }

    public static SendMessage start(Update update) {
        return kirish(update);
    }

    public static SendMessage getContact(Contact contact, TgUser user) {
        String phoneNumber = checkPhoneNumber(contact.getPhoneNumber());
        user.setPhoneNumber(phoneNumber);
        String sendCode = sendCode(user);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        user.setState(BotState.ENTER_CODE);
        saveUserChanges(user);
        sendMessage.setText(
                "Sizning tel raqamingizga kod jo'natildi. Iltimos, tasdiqlash kodini kiriting\uD83D\uDCE9 : " + code);
//        if (sendCode) {
//            sendMessage.setText(
//                    "Sizning tel raqamingizga kod jo'natildi. Iltimos, tasdiqlash kodini kiriting : " );
//        } else {
//
//            sendMessage.setText("We don't have token to real SMS service so we send code to bot : "+code);
//        }
        sendMessage.setReplyMarkup(generateReplyKeyboardMarkup(user));
        return sendMessage;
    }

    public static String checkPhoneNumber(String phoneNumber) {
        return phoneNumber.startsWith("+") ? phoneNumber : "+" + phoneNumber;
    }

    public static TgUser creatorGetUser(String id) {
        for (TgUser user : tgUserList) {
            if (user.getChatId().equals(id)) {
                return user;
            }
        }
        TgUser user = new TgUser(id, BotState.START);
        tgUserList.add(user);
        return user;
    }

    public static String getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId().toString();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId().toString();
        }
        return "";
    }

    /**
     * Inline
     */
    public static InlineKeyboardMarkup generateInlineKeyboardMarkup(TgUser user) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton row1Button1 = new InlineKeyboardButton();
        if (user.getState().equals(BotState.START)) {
            row1Button1.setText(
                    "\uD83D\uDC49REGISTRATSIYA\uD83D\uDC48");
            row1Button1.setCallbackData("REGISTRATSIYA");
            row1.add(row1Button1);
            rowList.add(row1);
        }
        else if (user.getState().equals(BotState.NAME)) {
            row1Button1.setText("Ha✅");
            row1Button1.setCallbackData("HA");
            row1.add(row1Button1);
            row1Button1 = new InlineKeyboardButton();
            row1Button1.setText("Yoq❌");
            row1Button1.setCallbackData("YOQ");
            row1.add(row1Button1);
            rowList.add(row1);
        }
        else if (user.getState().equals(BotState.SHOW_MENU)) {
            row1Button1.setText("Mahsulotlarimiz\uD83D\uDCF1");
            row1Button1.setCallbackData("SHOW_BRANDS");
            row1.add(row1Button1);


            InlineKeyboardButton row1Button2 = new InlineKeyboardButton();
            row1Button2.setText("Savatcham\uD83D\uDECD");
            row1Button2.setCallbackData("SHOW_BASKET");
            row1.add(row1Button2);
            rowList.add(row1);

            List<InlineKeyboardButton> row3 = new ArrayList<>();
            InlineKeyboardButton row3Button1 = new InlineKeyboardButton();
            row3Button1.setText(
                    "Bizning kompaniya⛪️");
            row3Button1.setCallbackData("ABOUT_US");
            row3.add(row3Button1);


            InlineKeyboardButton row4Button1 = new InlineKeyboardButton();
            row4Button1.setText(
                    "Buyurtmalar tarixi\uD83C\uDF9E");
            row4Button1.setCallbackData("SHOW_ORDER_HISTORY");
            row3.add(row4Button1);
            rowList.add(row3);

            List<InlineKeyboardButton> row5 = new ArrayList<>();
            InlineKeyboardButton row5Button1 = new InlineKeyboardButton();
            row5Button1.setText(
                    "Biz ijtimoiy tarmoqlarimiz\uD83D\uDD17");
            row5Button1.setCallbackData("SOCIAL_NETS");
            row5.add(row5Button1);


            InlineKeyboardButton row6Button1 = new InlineKeyboardButton();
            row6Button1.setText(
                    "Sotib olish⚜️");
            row6Button1.setCallbackData("BUY");
            row5.add(row6Button1);
            rowList.add(row5);


        }
        else if (user.getState().equals(BotState.SHOW_BRANDS)) {
            int i = 0;
            for (Brands br : DataBase.brandsList) {
                row1Button1 = new InlineKeyboardButton();
                row1Button1.setText(br.getBrand());
                row1Button1.setCallbackData(br.getBrand());
                row1.add(row1Button1);
                if (i % 2 != 0 || i == DataBase.brandsList.size() - 1) {
                    if (i == DataBase.brandsList.size() - 1) {
                        rowList.add(row1);
                        row1 = new ArrayList<>();
                        row1.add(back());
                        rowList.add(row1);

                    } else {
                        rowList.add(row1);
                        row1 = new ArrayList<>();
                    }
                }
                i++;
            }

        }
        else if (user.getState().equals(BotState.SOCIAL_NETS)) {
            int count = 0;
            for (int i = 0; i < DataBase.socialNetList.size(); i++) {
                SocialNet socialNet = DataBase.socialNetList.get(i);
                if (i == 0) {
                    count++;
                    row1Button1.setText(
                            socialNet.getNameUz());
                    row1Button1.setUrl(socialNet.getLink());
                    row1.add(row1Button1);
                } else {
                    count++;
                    row1Button1 = new InlineKeyboardButton();
                    row1Button1.setText(
                            socialNet.getNameUz());
                    row1Button1.setUrl(socialNet.getLink());
                    row1.add(row1Button1);

                }
                if (count % 2 == 0) {
                    rowList.add(row1);
                    row1 = new ArrayList<>();
                } else if (count == DataBase.socialNetList.size()) {
                    rowList.add(row1);
                }
            }
            row1 = new ArrayList<>();
            row1.add(back());
            rowList.add(row1);

        }
        else if (user.getState().equals(BotState.ABOUT_US)||user.getState().equals(BotState.BUY_PESHKOM)) {
            row1Button1.setText("Lokatsiya olish\uD83D\uDDFA");
            row1Button1.setCallbackData("GET_LOCATION");
            row1.add(row1Button1);
            row1Button1 = new InlineKeyboardButton();
            row1Button1.setText("Biz bilan boglanish");
            row1Button1.setCallbackData("BOGLANISH");
            row1.add(row1Button1);
            rowList.add(row1);
            row1 = new ArrayList<>();

            row1.add(back());
            rowList.add(row1);
        }
        else if (user.getState().equals(BotState.GET_LOCATION)) {
            row1.add(back());
            rowList.add(row1);
        }
        else if (user.getState().equals(BotState.SHOW_PRODUCTS)) {
            if (user.getPageNum() >= 1) {
                row1Button1.setText("◀️");
                row1Button1.setCallbackData("Back");
                row1.add(row1Button1);
            }
            row1.add(back());
            row1Button1 = new InlineKeyboardButton();
            row1Button1.setText("➡️");
            row1Button1.setCallbackData("Next");
            row1.add(row1Button1);
            rowList.add(row1);
            row1 = new ArrayList<>();
            row1Button1 = new InlineKeyboardButton();
            row1Button1.setText("Savat\uD83D\uDCE5");
            row1Button1.setCallbackData("Savat");
            row1.add(row1Button1);
            rowList.add(row1);

        }
        else if (user.getState().equals(BotState.SHOW_BASKET)) {
            row1 = new ArrayList<>();
            row1Button1 = new InlineKeyboardButton();
            row1Button1.setText("Tahrirlash\uD83D\uDEE0");
            row1Button1.setCallbackData("EDIT");
            row1.add(row1Button1);
            row1Button1 = new InlineKeyboardButton();
            row1Button1.setText("Sotib olish⚜️");
            row1Button1.setCallbackData("BUY");
            row1.add(row1Button1);
            rowList.add(row1);
            row1 = new ArrayList<>();
            row1.add(back());
            rowList.add(row1);

        }
        else if (user.getState().equals(BotState.DELETE_BAS)||user.getState().equals(BotState.ADD_NUMBER)) {
            int i = 0;
            for (ProductWithAmount pr : DataBase.productWithAmountList) {
                if (pr.getOrder().getUser().getChatId().equals(user.getChatId()) &&
                        pr.getOrder().getOrderStatus().equals(OrderStatus.BASKET)) {

                    row1Button1 = new InlineKeyboardButton();
                    row1Button1.setText(pr.getProduct().getModel());
                    row1Button1.setCallbackData(pr.getProduct().getModel());
                    row1.add(row1Button1);
                    if (i % 2 != 0 ) {
                            rowList.add(row1);
                            row1 = new ArrayList<>();

                    }
                    i++;
                }

            }
            row1.add(back());
            rowList.add(row1);
        }
        else if(user.getState().equals(BotState.BUY)){
            row1 = new ArrayList<>();
            row1Button1 = new InlineKeyboardButton();
            row1Button1.setText("Yoq❌");
            row1Button1.setCallbackData("NO");
            row1.add(row1Button1);
            row1Button1 = new InlineKeyboardButton();
            row1Button1.setText("Ha✅");
            row1Button1.setCallbackData("YES");
            row1.add(row1Button1);
            rowList.add(row1);

        }
        else if(user.getState().equals(BotState.EDIT)){
            row1 = new ArrayList<>();
            row1Button1 = new InlineKeyboardButton();
            row1Button1.setText("Mahsulot sonini qoshish\uD83D\uDCE5");
            row1Button1.setCallbackData("ADD_NUMBER");
            row1.add(row1Button1);
            row1Button1 = new InlineKeyboardButton();
            row1Button1.setText("O'chirish\uD83D\uDDD1");
            row1Button1.setCallbackData("DELETE");
            row1.add(row1Button1);
            rowList.add(row1);
            row1 = new ArrayList<>();
            row1.add(back());
            rowList.add(row1);

        }
        else if (user.getState().equals(BotState.CHOOSE_PAY_TYPE)){
            PayType[] values = PayType.values();
            for (PayType payType : values) {
                row1=new ArrayList<>();
                row1Button1=new InlineKeyboardButton();
                row1Button1.setText(payType.name());
                row1Button1.setCallbackData("PAY_TYPE:"+payType.name());
                row1.add(row1Button1);
                rowList.add(row1);
            }
        }
        else if(user.getState().equals(BotState.CHOOSE_MONEY)){

            row1Button1=new InlineKeyboardButton();
            row1Button1.setText("Dastavka\uD83C\uDFCE");
            row1Button1.setCallbackData("DASTAVKA");
            row1.add(row1Button1);
            row1Button1=new InlineKeyboardButton();
            row1Button1.setText("Borib olish\uD83D\uDEB6");
            row1Button1.setCallbackData("SOTIB_OLISH");
            row1.add(row1Button1);
            rowList.add(row1);
            row1=new ArrayList<>();
            row1.add(back());
            rowList.add(row1);

        }
        else if (user.getState().equals(BotState.FINISH_ORDER)||user.getState().equals(BotState.SHOW_ORDER_HISTORY)){
            row1.add(back());
            rowList.add(row1);
        }

        markup.setKeyboard(rowList);
        return markup;
    }

    public static InlineKeyboardButton back() {

        InlineKeyboardButton row1Button1 = new InlineKeyboardButton();
        row1Button1.setText("Menu♻️");
        row1Button1.setCallbackData("Back to Main");
        return row1Button1;
    }

    public static ReplyKeyboardMarkup generateReplyKeyboardMarkup(TgUser user) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton row1Button1 = new KeyboardButton();
        if (user.getState().equals(BotState.SHARE_CONTACT)) {
            row1Button1.setText(
                    "JO'NATISH"
            );
            row1Button1.setRequestContact(true);
            row1.add(row1Button1);
            rowList.add(row1);
        }else if (user.getState().equals(BotState.SHARE_LOCATION)){
            row1Button1.setText(
                    "JO'NATISH");
            row1Button1.setRequestLocation(true);
            row1.add(row1Button1);
            rowList.add(row1);
        }
        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public static void saveUserChanges(TgUser changedUser) {
        for (TgUser user : tgUserList) {
            if (user.getChatId().equals(changedUser.getChatId())) {
                tgUserList.remove(user);
                tgUserList.add(changedUser);
                break;
            }
        }

    }

    public static SendMessage showSocialNets(Update update) {
        String chatId = getChatId(update);
        TgUser user = creatorGetUser(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText(
                "\uD83D\uDCA0Bizni ijtimoiy tarmoqlarda\uD83D\uDCA0"
        );
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(user));
        return sendMessage;


    }

    public static SendLocation sendCompLocation(Update update) {
        String chatId = getChatId(update);
        TgUser user = creatorGetUser(chatId);
        SendLocation sendMessage = new SendLocation();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setLatitude(DataBase.company.getLat());
        sendMessage.setLongitude(DataBase.company.getLan());
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(user));
        return sendMessage;
    }

    public static SendPhoto aboutUs(Update update) {
        String chatId = getChatId(update);
        TgUser user = creatorGetUser(chatId);
        user.setState(BotState.ABOUT_US);
        saveUserChanges(user);
        Company company = DataBase.company;
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setCaption(
                company.getNameUz() + "\n" + company.getDescriptionUz());
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(new File(company.getPhotoUrl())));
        sendPhoto.setReplyMarkup(generateInlineKeyboardMarkup(user));
        return sendPhoto;
    }

    public static SendMessage askLocation( TgUser tgUser) {
        tgUser.setState(BotState.SHARE_LOCATION);
        saveUserChanges(tgUser);
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText("Manzilingizni tashlang\uD83D\uDDFA");
        sendMessage.setReplyMarkup(generateReplyKeyboardMarkup(tgUser));
        return sendMessage;
    }

    public static SendMessage getLocationOrAddress(Update update, TgUser user) {

            for (ProductWithAmount pr : DataBase.productWithAmountList) {
                if (pr.getOrder().getUser().getChatId().equals(user.getChatId()) &&
                        pr.getOrder().getOrderStatus().equals(OrderStatus.BASKET)) {
                    if(update.getMessage().hasLocation()) {
                    Location location = update.getMessage().getLocation();
                    pr.getOrder().setLan(location.getLongitude().floatValue());
                    pr.getOrder().setLat(location.getLatitude().floatValue());
                    }else{
                   pr.getOrder().setAddress(String.valueOf(update));
                    }
                }
            }
        user.setState(BotState.CHOOSE_PAY_TYPE);
        saveUserChanges(user);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText("PayType tanlang\uD83D\uDCB3");
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(user));
        return sendMessage;
    }


    public static SendMessage getPayType(Update update, TgUser tgUser) {
        tgUser.setState(BotState.FINISH_ORDER);
        saveUserChanges(tgUser);
        Payment payment=new Payment();
        String data = update.getCallbackQuery().getData();
        data=data.substring(data.indexOf(":")+1);
        payment.setPayType(PayType.valueOf(data));
        double totalSum=0;

        for (ProductWithAmount pr : DataBase.productWithAmountList) {
            if (pr.getOrder().getUser().getChatId().equals(tgUser.getChatId()) &&
                    pr.getOrder().getOrderStatus().equals(OrderStatus.BASKET)) {
                totalSum += pr.getProduct().getPrice()* pr.getAmount();
pr.getOrder().setOrderStatus(OrderStatus.NEW);
 payment.setPaySum(totalSum);
pr.getOrder().setOrderDate(new Timestamp(new java.util.Date().getTime()));
    pr.getOrder().setPayment(payment);
totalSum = 0;
            }


        }

        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText("\uD83D\uDE0ABizning hizmatdan foydalanganingiz uchun rahmat\uD83D\uDE0A");
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
      return sendMessage;

    }

    public static SendMessage showhistory(TgUser tgUser) {
        String str = "";
        boolean res = false;
        for (ProductWithAmount pr : DataBase.productWithAmountList) {
            if (pr.getOrder().getUser().getChatId().equals(tgUser.getChatId()) &&
                    pr.getOrder().getOrderStatus().equals(OrderStatus.NEW)){
                res = true;
            str+= "\n========================================="+
                    "\n\uD83D\uDE4D\u200D♀️/\uD83D\uDE4E\u200D♂️Haridor: "+tgUser.getFio()+
                    "\n\uD83D\uDCF1Model: "+pr.getProduct().getModel() +
                    "\n\uD83E\uDDEESoni: "+pr.getAmount()+
                    "\n\uD83D\uDCB3Tolov vositasi: "+ pr.getOrder().getPayment().getPayType()+
                    "\n\uD83D\uDCB8Umumiy summa: "+pr.getOrder().getPayment().getPaySum()+
                    "\n\uD83D\uDD52Sana: "+pr.getOrder().getOrderDate()+
                    "\n=========================================";
            }
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        if(res){

            sendMessage.setText(str);

        return sendMessage;
        }
        else {
        sendMessage.setText("Hali harid amalga oshirilmadi\uD83D\uDE45");
            tgUser.setState(BotState.SHOW_MENU);
            BotService.saveUserChanges(tgUser);
        return sendMessage;
        }
        }

        @SneakyThrows
    public static SendDocument sendDocument(TgUser user){
File file = new File("src/main/resources/fayl/history.pdf");

            PdfWriter pdfWriter = new PdfWriter("src/main/resources/fayl/history.pdf");
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);
            Paragraph paragraph = new Paragraph();
            paragraph.add("M_phones_uz bilan qilgan barcha haridlaringiz ro'yhati:").setTextAlignment(TextAlignment.CENTER);
            document.add(paragraph);
            Table table=new Table(7);
            table.addCell("id");
            table.addCell("Haridor");
            table.addCell("Model");
            table.addCell("Soni");
            table.addCell("Tolov vositasi:");
            table.addCell("Umumiy summa: ");
            table.addCell("Sana");
int i=0;
            for (ProductWithAmount pr : DataBase.productWithAmountList) {
                if (pr.getOrder().getUser().getChatId().equals(user.getChatId()) &&
                        pr.getOrder().getOrderStatus().equals(OrderStatus.NEW)) {

                        table.addCell(String.valueOf(i+1));
                        table.addCell(String.valueOf(user.getFio()));
                        table.addCell(String.valueOf(pr.getProduct().getModel()));
                        table.addCell(String.valueOf(pr.getAmount()));
                        table.addCell(String.valueOf(pr.getOrder().getPayment().getPayType()));
                        table.addCell(String.valueOf(pr.getOrder().getPayment().getPaySum()));
                        table.addCell(String.valueOf(pr.getOrder().getOrderDate()));


                i++;
                }
            document.add(table);
                document.close();
        }
            InputFile inputFile = new InputFile(file);
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(user.getChatId());
sendDocument.setDocument(inputFile);
sendDocument.setReplyMarkup(generateInlineKeyboardMarkup(user));
return  sendDocument;
        }

    public static SendContact sendCompTel(Update update) {
        String chatId = getChatId(update);
        TgUser user = creatorGetUser(chatId);
        SendContact sendContact = new SendContact();
        sendContact.setChatId(user.getChatId());
        sendContact.setPhoneNumber(DataBase.company.getPhoneNum());
        sendContact.setFirstName(DataBase.company.getNameUz());
        user.setState(BotState.GET_LOCATION);
        BotService.saveUserChanges(user);
        sendContact.setReplyMarkup(generateInlineKeyboardMarkup(user));
        return sendContact;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    @SneakyThrows
    public static InputFile imageIoWrite() {
        URL url = new URL("https://www.cnet.com/a/img/iJxo9AIxiXHqVoqm6nGISKtKwPI=/2020/08/18/b7168aea-9f7e-47bb-9f31-4cb8ad92fbc7/lg-note-20-ultra-5g-iphone-11-se-google-pixel-4a-lg-velvet-6133.jpg");

        BufferedImage bImage = null;
        try {

            bImage = ImageIO.read(url);

            ImageIO.write(bImage, "jpg", new File("src/main/resources/"+bImage.getData()));

        } catch (IOException e) {
            System.out.println("Exception occured :" + e.getMessage());
        }
        System.out.println("Images were written succesfully.");
        InputFile file = new InputFile( new File("src/main/resources/"+bImage.getData()));
        return file;
    }
}