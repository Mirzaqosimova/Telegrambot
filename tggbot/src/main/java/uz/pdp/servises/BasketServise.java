package uz.pdp.servises;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.bot.BotState;
import uz.pdp.db.DataBase;
import uz.pdp.model.Brands;
import uz.pdp.model.Order;
import uz.pdp.model.ProductWithAmount;
import uz.pdp.model.TgUser;
import uz.pdp.model.enums.OrderStatus;

import static uz.pdp.bot.BotService.*;
import static uz.pdp.db.DataBase.brandsList;

public class BasketServise {
    public static SendMessage savatcha(TgUser user) {
        Brands brand = new Brands();
        for (Brands br : brandsList) {
            if (br.getBrand().equals(user.getBrands())) {
                brand = br;
                break;
            }

        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        for (ProductWithAmount pr : DataBase.productWithAmountList) {
            if (pr.getProduct().equals(brand.getProduct().get(user.getPageNum()))&&
            pr.getOrder().getUser().getChatId().equals(user.getChatId())&&pr.getOrder().getOrderStatus().equals(OrderStatus.BASKET)) {
                sendMessage.setText(
                        "Mahsulot savatchada bor❗️ uning qiymatini sotib olayotkanda belgilaysiz!"
                );
                return sendMessage;
            }
        }
        DataBase.productWithAmountList.add(new ProductWithAmount(brand.getProduct().get(user.getPageNum()), brand.getProduct().get(user.getPageNum()).getPrice(), 1, new Order(user, OrderStatus.BASKET)));


        sendMessage.setText(
                "Muvaffaqiyatli qo'shildi!"
        );
        return sendMessage;
    }

    public static SendMessage savatcham(Update update) {
        String chatId = getChatId(update);
        TgUser user = creatorGetUser(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        String str = "";
        boolean res = false;

        for (ProductWithAmount pr : DataBase.productWithAmountList) {

            if (pr.getOrder().getUser().getChatId().equals(user.getChatId()) &&
                    pr.getOrder().getOrderStatus().equals(OrderStatus.BASKET)) {
                res = true;

                str +=  "\n=========================================" +
                        "Model: " + pr.getProduct().getModel() +
                        "\nRangi: " + pr.getProduct().getColor() +
                        "\nOperativka: " +pr.getProduct().getOperativeSystem() +
                        "\nRAM: " + pr.getProduct().getRam() +" gb"+
                        "\nStorage: " + pr.getProduct().getStorage() +" gb"+
                        "\nCamera: " +  pr.getProduct().getCamera() +" px"+
                        "\nNarxi: " + pr.getProduct().getPrice()+" $"+
                        "\nNechta:" + pr.getAmount() +
                        "\n=========================================";

                System.out.println("Savatcha");
            }
        }
        if (res) {
            sendMessage.setText(str);
            sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(user));
            return sendMessage;
        } else {
            sendMessage.setText("Savatchangiz bo'sh");
            user.setState(BotState.SHOW_MENU);
            return sendMessage;
        }
    }
    public static SendMessage chooseEditBasket(TgUser user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        if(user.getState().equals(BotState.DELETE_BAS)){
            sendMessage.setText("O'chirmoqchi bo'lgan mahsulotingizni tanlang kiriting\uD83E\uDE84");
        }
        else  {
            sendMessage.setText("Qo'shmoqchi bolgan mahsulotingizni tanlang\uD83D\uDCE5");
        }
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(user));
        return sendMessage;
    }

    public static SendMessage deleteBas(TgUser user, String prModel) {
        boolean res = false;
        for (ProductWithAmount pr : DataBase.productWithAmountList) {
            if (pr.getOrder().getUser().getChatId().equals(user.getChatId()) &&
                    pr.getOrder().getOrderStatus().equals(OrderStatus.BASKET) &&
                    pr.getProduct().getModel().equals(prModel)) {
                res = true;

                DataBase.productWithAmountList.remove(pr);
                break;
            }


        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        if (!res) {
            sendMessage.setText("Bunday mahsulot mavjud emas!");
            return sendMessage;
        } else {
            sendMessage.setText("Mahsuolot muvaffaqiyatli o'chdi✅");
            return sendMessage;
        }
    }
    public static SendMessage editBasket(TgUser user) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText("Bo'limlardan birini tanlang\uD83D\uDDC2");
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(user));
        return sendMessage;
    }
    public static SendMessage realAdd(TgUser user, String prModel) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        int id = 0;

        for (ProductWithAmount pr : DataBase.productWithAmountList) {
            if (pr.getOrder().getUser().getChatId().equals(user.getChatId()) &&
                    pr.getOrder().getOrderStatus().equals(OrderStatus.BASKET) &&
                    pr.getProduct().getModel().equals(prModel)) {

                break;
            }

            id++;
        }
        DataBase.productWithAmountList.get(id-1).setAmount(Integer.parseInt(prModel));

        sendMessage.setText("Mahsulot muvaffaqiyatli qo'shildi✅");

        return sendMessage;

    }
}