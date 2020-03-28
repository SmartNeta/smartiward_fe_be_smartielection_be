package com.mnt.sampark.mvc.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import java.net.URLEncoder;

public class SMSService {

    public static boolean send(String mobile, String message) {
        message = message.replaceAll("( )+", "%20");
        if (mobile != null && mobile.length() == 10) {
            String url = "http://map-alerts.smsalerts.biz/api/web2sms.php?"
                    + "workingkey=Aed9969b0c590999401f5cd030789fee3"
                    + "&sender=SMNETA"
                    + "&to=" + mobile
                    + "&message=" + URLEncoder.encode(message);
            try {
                HttpResponse<String> res = Unirest.get(url).asString();
                String body = res.getBody();
                if (body != null && body.startsWith("Message GID")) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

}
//http://map-alerts.smsalerts.biz/api/web2sms.php?workingkey=Aed9969b0c590999401f5cd030789fee3&sender=SAMPRK&to=&message=
//http://map-alerts.smsalerts.biz/api/web2sms.php?workingkey=Aed9969b0c590999401f5cd030789fee3&sender=IELECT&to=9881490902&message=hi
