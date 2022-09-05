package uz.pdp.db;

import uz.pdp.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBase {
    public static List<TgUser> tgUserList = new ArrayList<>();
    public static List<TwilioVerification> twilioVerificationList =new ArrayList<>();
    public static List<SocialNet> socialNetList=new ArrayList<>(Arrays.asList(
            new SocialNet("TELEGRAM","https://t.me/pdpuz"),
            new SocialNet("FACEBOOK","https://www.facebook.com/search/top?q=pdp%20academy"),
            new SocialNet("YOUTUBE","https://www.youtube.com/c/pdpuz"),
            new SocialNet("INSTAGRAM","https://www.instagram.com/pdpuz/")
    ));public static List<Brands> brandsList = new ArrayList<>(Arrays.asList(
            new Brands("Samsung",Arrays.asList(
                    new Product("samsung G5",100,"oq",8,120,"IOS",123,"1200x1900","src\\main\\resources\\photos\\03D1ZXVNpUaqVl6KHkDWl4t-5..v1634846487.jpg"),
                    new Product("samsung g2",120,"OQ",8,120,"MI",123,"1200x1900","src\\main\\resources\\photos\\Apple-iPhone-13-Pro-featured-image-packshot-review.jpg"),
                    new Product("samsung g10",123,"seriy",8,120,"IOS",123,"1200x1900","src\\main\\resources\\photos\\samsung-galaxy-s21-series-2.jpg")
            )),
            new Brands("iPhone",
                    Arrays.asList( new Product("Iphone S12",200,"qora",8,120,"IOS",123,"1200x1900","src\\main\\resources\\photos\\03D1ZXVNpUaqVl6KHkDWl4t-5..v1634846487.jpg"),
                    new Product("Iphone S8",600,"qora",8,120,"mi",123,"1200x1900","src\\main\\resources\\photos\\bfarsace_201106_4269_012.0.jpg"),
                    new Product("Iphone G12",400,"yashil",8,120,"IOS",123,"1200x1900","src\\main\\resources\\photos\\03D1ZXVNpUaqVl6KHkDWl4t-5..v1634846487.jpg")
            )),
            new Brands("Mi",
                       Arrays.asList( new Product("mi23",200,"qora",8,120,"IOS",123,"1200x1900","src\\main\\resources\\photos\\03D1ZXVNpUaqVl6KHkDWl4t-5..v1634846487.jpg"),
                    new Product("mi",600,"qora",8,120,"mi",123,"1200x1900","src\\main\\resources\\photos\\images.jpg"),
                    new Product("mi G12",400,"yashil",8,120,"IOS",123,"1200x1900","src\\main\\resources\\photos\\Apple-iPhone-13-Pro-featured-image-packshot-review.jpg")
            )),
             new Brands("Artel",
                        Arrays.asList( new Product("artel3",200,"qora",8,120,"IOS",123,"1200x1900","src\\main\\resources\\photos\\03D1ZXVNpUaqVl6KHkDWl4t-5..v1634846487.jpg"),
                    new Product("artel3",600,"qora",8,120,"mi",123,"1200x1900","src\\main\\resources\\photos\\images.jpg"),
                    new Product("artel3 G12",400,"yashil",8,120,"IOS",123,"1200x1900","src\\main\\resources\\photos\\Apple-iPhone-13-Pro-featured-image-packshot-review.jpg")
            ))
    ));
    public static List<Order> orderList=new ArrayList<>();
    public static List<ProductWithAmount> productWithAmountList=new ArrayList<>();
    public static List<Payment> paymentList=new ArrayList<>();
    public static Company company=new Company("M_PHONES_UZ","src\\main\\resources\\photos\\3836x2861.jpg","Bizning kompaniyaga xxxx-yilning x-sanasida tashkil qilingan" +
            "bolib ...",69.236503,41.314177,"+998337200210");









}
