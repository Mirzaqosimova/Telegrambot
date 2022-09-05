package uz.pdp.servises;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import uz.pdp.bot.BotService;
import uz.pdp.bot.BotState;
import uz.pdp.db.DataBase;
import uz.pdp.model.Brands;
import uz.pdp.model.ProductWithAmount;
import uz.pdp.model.TgUser;
import uz.pdp.model.enums.OrderStatus;

import java.io.File;

import static uz.pdp.bot.BotService.generateInlineKeyboardMarkup;
import static uz.pdp.db.DataBase.brandsList;

public class ProductServise {
    public static SendPhoto showProducts(TgUser user) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(user.getChatId());
        Brands brand = new Brands();
        for (Brands br : brandsList) {
            if (br.getBrand().equals(user.getBrands())) {
                brand = br;
                break;
            }

        }
        if (user.getPageNum() == brand.getProduct().size()) {
            user.setPageNum(0);
        }

            InputFile file = new InputFile(new File(brand.getProduct().get(user.getPageNum()).getLink()));
            sendPhoto.setPhoto(file);
            sendPhoto.setCaption(
                    "Model: " + brand.getProduct().get(user.getPageNum()).getModel() +
                    "\nRangi: " + brand.getProduct().get(user.getPageNum()).getColor() +
                    "\nOperativka: " + brand.getProduct().get(user.getPageNum()).getOperativeSystem() +
                    "\nRAM: " + brand.getProduct().get(user.getPageNum()).getRam() +" gb"+
                    "\nStorage: " + brand.getProduct().get(user.getPageNum()).getStorage() +" gb"+
                    "\nCamera: " + brand.getProduct().get(user.getPageNum()).getCamera() +" px"+
                    "\nNarxi: " + brand.getProduct().get(user.getPageNum()).getPrice()+" $"

            );

        sendPhoto.setReplyMarkup(generateInlineKeyboardMarkup(user));
        return sendPhoto;
    }
    public static SendMessage buy(TgUser user) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        String str = "";
        boolean res = false;
        for (ProductWithAmount pr : DataBase.productWithAmountList) {
            if (pr.getOrder().getUser().getChatId().equals(user.getChatId()) &&
                    pr.getOrder().getOrderStatus().equals(OrderStatus.BASKET)) {
                res = true;

                str += "\n=========================================" +
                        "\nModel: " + pr.getProduct().getModel() +
                        "\nRangi: " + pr.getProduct().getColor() +
                        "\nOperativka: " + pr.getProduct().getOperativeSystem() +
                        "\nRAM: " + pr.getProduct().getRam() +" gb"+
                        "\nStorage: " + pr.getProduct().getStorage() +" gb"+
                        "\nCamera: " + pr.getProduct().getCamera() +" px"+
                        "\nNarxi: " + pr.getProduct().getPrice() +" $"+
                        "\n=========================================";


            }
        }
        if (res) {
            sendMessage.setText(str);
            sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(user));
            return sendMessage;
        } else {
            sendMessage.setText("Savatchangiz bo'sh");
            user.setState(BotState.SHOW_MENU);
            BotService.saveUserChanges(user);
            return sendMessage;
        }


    }

}
