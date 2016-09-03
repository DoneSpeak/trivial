package helper;

import android.app.Activity;
import android.content.SharedPreferences;

import donespeak.com.mail.MainActivity;

/**
 * Created by glorior on 2016/6/21.
 */
public class Helper {
    //判断email格式是否正确
    public static boolean isEmail(String email) {
        String regex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        return email.matches(regex);
    }

    //判断登陆用户是否更改
    public static boolean isUserChange(SharedPreferences sharedPreferences){
//        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        String name =sharedPreferences.getString("userEmail", "");
        if(name.equals(MainActivity.userEmail)){
            return false;
        }else{
            return true;
        }
    }
//    public static final int BOXTYPE_DELETED = 4;
//    public static final int BOXTYPE_SPAM = 3;
//    public static final int BOXTYPE_DRAFTS = 2;
//    public static final int BOXTYPE_SENT = 1;
//    public static final int BOXTYPE_INBOX = 0;

    //163邮箱 ,sina邮箱
    String[] boxTypes_163 = {"INBOX","已发送","草稿箱","垃圾邮件","已删除","病毒文件夹"};
    String[] boxTypes_sina = {"INBOX","已发送","草稿夹","垃圾邮件","已删除","其它邮件","订阅邮件","星标邮件","商讯信息","网站通知"};
    //qq邮箱

    public static String getBoxType(String userEmail,String type){
        return null;
    }
}

