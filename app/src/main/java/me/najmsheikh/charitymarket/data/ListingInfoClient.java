package me.najmsheikh.charitymarket.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ListingInfoClient {

    @POST("/vision")
    Call<InfoResponse> getInfo(
            @Body InfoRequest request
    );
}
