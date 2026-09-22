package com.sc.aipdriver.activities.interfaces;

import com.google.gson.JsonObject;
import com.sc.aipdriver.activities.models.ApiResponse;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.DriverLoginResponse;
import com.sc.aipdriver.activities.models.DriverLoginStatusResponse;
import com.sc.aipdriver.activities.models.DriverPermissionModel;
import com.sc.aipdriver.activities.models.EmailRequest;
import com.sc.aipdriver.activities.models.FarmData;
import com.sc.aipdriver.activities.models.FormDetails;
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData;
import com.sc.aipdriver.activities.models.LatLongData;
import com.sc.aipdriver.activities.models.ListDriverResponse;
import com.sc.aipdriver.activities.models.LogInData;
import com.sc.aipdriver.activities.models.ParentResponse;
import com.sc.aipdriver.activities.models.PermissionModel;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.models.ReceiptDetails;
import com.sc.aipdriver.activities.models.ReceiptResponse;
import com.sc.aipdriver.activities.models.ReceiptResponseMain;
import com.sc.aipdriver.activities.models.RideFinishRequest;
import com.sc.aipdriver.activities.models.RouteListData;
import com.sc.aipdriver.activities.models.RouteLog;
import com.sc.aipdriver.activities.models.StartDataSend;
import com.sc.aipdriver.activities.models.TempBags;
import com.sc.aipdriver.activities.models.UpdateFarmData;
import com.sc.aipdriver.activities.models.VehicleListData;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by dev on 21/7/17.
 */

public interface ApiInterface {

    @GET("/api/Driverlogin")
    Call<BaseResponse<LogInData>> getUserLogin(@Query("username") String uname, @Query("password") String password,
                                               @Query("DeviceId") String deviceid, @Query("DeviceType") String devicetype);

    @GET("/api/Driverlogindetails")
    Call<DriverLoginResponse> getDriverloginDetails(@Query("id") String id);

    @GET("/api/CancelRide")
    Call<BaseResponse> cancelRide(@Query("id") String id,@Query("driverId") String driverId, @Header("Token") String token);

    @GET("/api/ListDrivers")
    Call<ListDriverResponse> getListDriver();

    @GET("/api/ListVehicle")
    Call<BaseResponse<List<VehicleListData>>> getListVehicle(@Query("orgId") String orgId,@Query("driverId") String driverId, @Header("Token") String token);

    @GET("/api/ListRoutes")
    Call<BaseResponse<List<RouteListData>>> getListRouters(@Query("orgId") String orgId,@Query("driverId") String driverId, @Header("Token") String token);

    @GET("/api/ListFarmsByRoutes")
    Call<BaseResponse<List<FarmData>>> getListFarmsByRoutes(@Query("RoutName") String rotename);

    @GET("/api/FarmPriority")
    Call<BaseResponse<List<PriorityFarmData>>> getFarmsWithPriority(@Query("RoutName") String rotename, @Query("orgId") String orgId, @Query("ParentId") String parentId, @Query("driverId") String driverId, @Header("Token") String token);
 @GET("/api/FarmPriorityOD")
    Call<BaseResponse<List<PriorityFarmData>>> getFarmsWithPriorityDateWise(@Query("RoutName") String rotename, @Query("orgId") String orgId, @Query("ParentId") String parentId, @Query("DateName") String date, @Query("driverId") String driverId, @Header("Token") String token);

    @GET("/api/FarmPriorityOD")
    Call<BaseResponse<Integer>> checkFarmPriorityODStatus(
            @Query("RoutName") String rotename,
            @Query("orgId") String orgId,
            @Query("ParentId") String parentId,
            @Query("DateName") String date,
            @Query("driverId") String driverId,
            @Header("Token") String token
    );
@GET("/api/NextFarm")
    Call<BaseResponse<PriorityFarmData>> getNextFarm(@Query("fid") String fid, @Query("RouteId") String routeId, @Query("orderdate") String orderdate, @Query("driverId") String driverId, @Header("Token") String token);
@GET("/api/AllFarmList")
    Call<BaseResponse<List<ImprovedPriorityFarmData>>> getAllFarmList(@Query("RouteId") String routeId, @Query("ParentId") String parentId,@Query("orderdate") String orderdate, @Query("driverId") String driverId, @Header("Token") String token);

    @GET("/api/FarmInformation")
    Call<BaseResponse<List<FormDetails>>> getFarmsInformation(@Query("FarmID") String farmid, @Query("RideId") String rid, @Query("driverId") String driverId, @Header("Token") String token);

    @GET("api/firm")
    Call<ApiResponse> getFarmInfo(@Query("id") String id, @Query("fid") String fId);

    @POST("/api/FarmPriority")
    Call<BaseResponse<String>> updateFarmList(@Body ArrayList<UpdateFarmData> farmlist);

    @POST("/api/startNewRide")
    Call<CommonError> sendData(@Body StartDataSend startDataSend,@Header("Token") String token);

    @GET("/api/startNewRide")
    Call<BaseResponse<LatLongData>> getstartNewRide(@Query("RideId") String rid, @Query("driverId") String driverId, @Header("Token") String token);

    @GET("/api/GetPID")
    Call<ParentResponse> getParentId(@Query("RouteId") String rid, @Query("driverid") String driverid);

    //    @POST("/api/deliveryOrderDetail")
//    Call<CommonError> sendFarmData(@Body ArrayList<DeliveryData> deliveryDataList);
    @POST("/api/deliveryOrderDetail")
    Call<CommonError> sendFarmData(@Body JsonObject jsonObject, @Header("Token") String token);

    @GET("/api/startNewRide")
    Call<CommonError> finishRide(@Query("rideId") String rid, @Query("UID") String uid,
                                 @Query("lat") String lat, @Query("lng") String lng, @Query("endOdometer") String endodometer,
                                 @Query("Address") String address, @Query("State") String state,
                                 @Query("City") String city, @Query("Country") String contry,
                                 @Query("Action") String action, @Query("TotalMiles") String TotalMiles, @Query("driverId") String driverId, @Header("Token") String token);

    @POST("/api/RideLog")
    Call<CommonError> sendLog(@Body RouteLog routeLog, @Header("Token") String token);

    @POST("/api/RideFinish")
    Call<CommonError> rideFinish(@Body RideFinishRequest routeLog, @Query("driverId") String driverId, @Header("Token") String token);
    @POST("/api/RideFinishEmail")
    Call<CommonError> rideFinishEmail(@Body JsonObject routeLog, @Query("driverId") String driverId, @Header("Token") String token);

    @GET("/api/FarmTempBeg")
    Call<BaseResponse<List<TempBags>>> getTempBags(@Query("RouteName") String routename, @Query("FarmID") Integer farmid, @Query("driverId") String driverId, @Header("Token") String token);

    @POST("/api/FarmTempBeg")
    Call<CommonError> sendTempBags(@Body ArrayList<TempBags> tempbags, @Query("driverId") String driverId, @Header("Token") String token);

    @POST("/api/FarmTempBeg")
    Call<CommonError> updateTempBags(@Body ArrayList<TempBags> tempbags, @Query("driverId") String driverId, @Header("Token") String token);

    @GET("/api/ShouldLogout")
    Call<PermissionModel> shouldLogout(@Query("DriverID") Integer driverId,@Header("Token") String token);

    @GET("/api/DriverLogout")
    Call<PermissionModel> shouldLogoutt(@Query("DriverID") Integer driverId, @Header("Token") String token);

    @GET("/api/DriverPermission")
    Call<DriverPermissionModel> driverPermission(@Query("DriverID") Integer driverId, @Query("VehicleID") Integer vechicleId);

    @POST("/api/DriverPermission")
    Call<CommonError> postVehicleStatus(@Query("DriverID") Integer driverId, @Query("VehicleID") Integer vechicleId, @Query("StatusID") Integer statusID, @Query("StatusName") String statusName);

    @POST("/api/DriverRecipt")
    Call<CommonError> ReceiptDetails(@Body ReceiptResponse receiptDetails, @Query("driverId") String driverId, @Header("Token") String token);

    @GET("/api/DriverRecipt")
    Call<ReceiptResponseMain> getReceipts(@Query("ParentId") Integer parentId, @Query("driverId") String driverId, @Header("Token") String token);

    @GET("/api/ShowPermission")

    Call<PermissionModel> showPermission(
         @Query("DriverID") Integer driverId,   @Header("Token") String token);

    @GET("/api/DriverDeviceLoginStatus")
    Call<DriverLoginStatusResponse> getDriverLoginStatus(@Query("DriverId") String driverId, @Query("DeviceId") String deviceId);

    @Multipart
    @POST("/api/startrideimg/{id}")
    Call<ResponseBody> uploadImage(
            @Header("Token") String token,
            @Path("id") String rideId,
            @Part MultipartBody.Part img1,
            @Part MultipartBody.Part img2,
            @Part MultipartBody.Part img3,
            @Part MultipartBody.Part img4,
            @Part MultipartBody.Part img5,
            @Part MultipartBody.Part img6,
            @Part MultipartBody.Part img7,
            @Part MultipartBody.Part img8
    );
    @Multipart
    @POST("/api/EndRideImg/{id}")
    Call<ResponseBody> uploadEndImage(
            @Path("id") String rideId,
            @Part MultipartBody.Part img1,
            @Part MultipartBody.Part img2
    );
    @Multipart
    @POST("/api/ReciptImg/{id}")
    Call<ResponseBody> uploadReceiptImg(
            @Header("Token") String token,
            @Path("id") String rideId,
            @Part MultipartBody.Part img1,
            @Part MultipartBody.Part img2,
            @Part MultipartBody.Part img3,
            @Part MultipartBody.Part img4
    );

    @POST("/api/DispatchETAMail")
    Call<CommonError> dispatchETAMail(
            @Body EmailRequest emailRequest, @Header("Token") String token

    );

    @POST("/api/AllDispatch")
    Call<CommonError> AllDispatch(
            @Body EmailRequest emailRequest, @Header("Token") String token
    );

    @GET("/api/GetRoute")
    Call<BaseResponse<Integer>> getRoute(
            @Query("RouteId") String routeId,
            @Query("orderdate") String orderDate,
            @Query("driverId") String driverId,
            @Header("Token") String token
    );

    @GET("/api/UpdateRoute")
    Call<CommonError> updateRoute(
            @Query("RouteId") String routeId,
            @Query("orderdate") String orderDate,
            @Query("driverId") String driverId,
            @Header("Token") String token
    );

    @Multipart
    @POST("/api/EndFarmImage/{id}")
    Call<ResponseBody> uploadFarmEndImage(
            @Header("Token") String token,
            @Path("id") String rideId,
            @Part MultipartBody.Part img1,
            @Part MultipartBody.Part img2,
            @Part MultipartBody.Part img3,
            @Part MultipartBody.Part img4
    );
}
