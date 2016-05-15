package christian.network.interfaces;

import christian.network.responses.AllChurchResponse;
import christian.network.responses.AllFeedsResponse;
import christian.network.responses.AllUsersResponse;
import christian.network.responses.CommentResponse;
import christian.network.responses.CommonResponse;
import christian.network.responses.ProfileInfoResponse;
import christian.network.responses.SignUpResponse;
import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface APIServiceInterface {
    @FormUrlEncoded
    @POST("signup.php")
    Call<SignUpResponse> signUpUser(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken, @Field("image_url") String image_url, @Field("link") String link);

    @GET("church.php")
    Call<AllChurchResponse> getAllChurch(@Query("authtoken") String authToken);

    @POST("church.php")
    Call<CommonResponse> followChurch(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @GET("follow.php")
    Call<AllUsersResponse> getAllUsers(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @POST("follow.php")
    Call<CommonResponse> followUser(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @DELETE("follow.php")
    Call<CommonResponse> unFollowUser(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @GET("feeds.php")
    Call<AllFeedsResponse> getAllFeed(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @FormUrlEncoded
    @POST("feeds.php")
    Call<CommonResponse> postUserFeed(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken, @Field("image") String image);

    @DELETE("feeds.php")
    Call<CommonResponse> deleteUserFeed(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @POST("likes.php")
    Call<CommonResponse> likeAPost(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @DELETE("likes.php")
    Call<CommonResponse> deleteLike(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @GET("comments.php")
    Call<CommentResponse> getAllComments(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @POST("comments.php")
    Call<CommonResponse> submitCommet(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @DELETE("comments.php")
    Call<CommonResponse> deleteComment(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @GET("profile.php")
    Call<ProfileInfoResponse> getProfileInfo(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

    @POST("profile.php")
    Call<CommonResponse> editProfileInfo(@Query("JSONParam") String jSonParam, @Query("authtoken") String authToken);

}
