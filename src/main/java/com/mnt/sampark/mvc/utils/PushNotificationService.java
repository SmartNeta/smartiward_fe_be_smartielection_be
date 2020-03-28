package com.mnt.sampark.mvc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import java.util.Objects;

public class PushNotificationService {

    private static final String url = "https://fcm.googleapis.com/fcm/send";
    private static final String key = "AAAAknW8oQc:APA91bE7Hw-kKt88uuydYS91SwifwkaToEQpbO4xhF9yVVmTKUhxfUT20XPRvG1v7U2Jl263BpJlWQUyxg7LFsugxItiotliBJz1v0Q2Ssg8Qs7RVE81b5HGpTZSbkiqYV5z2isLkpnc"; // new 22/11/2018
    //private static final String key = "AAAA5bRE70k:APA91bEXE0ioL-gf-naX38U7WqfHUtE7adn03kDtSWM55uUcy0jO-mNdQbNoQqpd8YypdDH8pR2usTj3x55r_SssAPn8TLc2rnkrrYR4eClqyZWqJejVYtIuFryJO4uB-x09Fna64b9b"; // old

    public static boolean send(String deviceId, String title, String text, String deviceType) {
        if (Objects.nonNull(deviceType) && deviceType.equalsIgnoreCase("IOS")) {
            return sendNotificationToIos(deviceId, title, text);
        }
        if (deviceId == null || deviceId.isEmpty()) {
            return false;
        }
        try {
            HttpRequestWithBody request = Unirest.post(url);
            request.header("Content-Type", "application/json");
            request.header("Authorization", "key=" + key);
            JSONObject body = new JSONObject();

            body.put("to", deviceId);

            Map<String, Object> notification = new HashMap<String, Object>();
            notification.put("title", title);
            notification.put("body", text);
            notification.put("sound", "default");
            notification.put("icon", "push_icon");
            ArrayList<Number> vibrationPattern = new ArrayList<Number>();
            vibrationPattern.add(2000);
            vibrationPattern.add(1000);
            vibrationPattern.add(500);
            vibrationPattern.add(500);
            notification.put("vibrationPattern", vibrationPattern);
            body.put("notification", notification);

            request.body(body);

            request.asJson();
            System.out.println("notification sent successfully..");
            return true;
        } catch (Exception e) {
        }
        System.out.println("notification sent failed..");
        return false;
    }

    private static boolean sendNotificationToIos(String deviceId, String title, String text) {
        try {
            ApnsService service = APNS.newService().withCert("src/main/resources/templates/certificate/CertificatesSamrtNeta.p12", "smartneta1!").withSandboxDestination().build();
            String payload = APNS.newPayload().alertBody(text).sound("default").build();
            System.err.println("Sending push notification...");
            service.push(deviceId, payload);
            System.out.println("The message has been hopefully sent...");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        send("cDEc6KOc1bk:APA91bEOa8mXDkGzBzoP1RdeReJuFJ09wgA0uarlxHaSX5ZtlIYnwB5fF173nXiHL0cTfZ7T446H4-y9UY_q5ajKnTxwZI4tPdbBzEbAKk7x5k2GrkRzsnC7fSRViigadpXUJhOnM1s-", "header", "text", "Android");
    }

}
