package com.phoenix.otlobbetyshipper.Common;

import com.phoenix.otlobbetyshipper.Model.Push;
import com.phoenix.otlobbetyshipper.Model.Request;
import com.phoenix.otlobbetyshipper.Model.User;
import com.phoenix.otlobbetyshipper.Remote.APIService;
import com.phoenix.otlobbetyshipper.Remote.FCMRetrofitClient;

public class Common {
    public static User currentUser;
    public static Request currentRequest;
    public static Push currentPush;

    public static String UPDATE = "Update";

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService()
    {
        return FCMRetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static final int PICK_IMAGE_REQUEST = 71;
    public static String convertCodeToStatus (String code){
        if (code.equals("0"))
            return "Placed";
        else
            return "Accepted";
    }


}
