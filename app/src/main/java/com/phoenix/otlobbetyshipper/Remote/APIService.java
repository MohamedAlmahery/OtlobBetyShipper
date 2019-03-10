package com.phoenix.otlobbetyshipper.Remote;


import com.phoenix.otlobbetyshipper.Model.MyResponse;
import com.phoenix.otlobbetyshipper.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAyk8u1Gc:APA91bEMuGFwsUbrQTStJcFxdiADzjC3Sz4GFiWKNXerxIGjBxuLhyBHzlhKhfocCaXzVPw85vH7oIr-DoKAoEF6xrkENj5zEfWMakQzCkKva4nyg4nTzDJZpQnP32iqgGu8j2CgEkJh"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
