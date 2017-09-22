package com.bsmwireless.data.network;

import com.bsmwireless.models.Auth;
import com.bsmwireless.models.DriverHomeTerminal;
import com.bsmwireless.models.DriverProfileModel;
import com.bsmwireless.models.DriverSignature;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.ELDUpdate;
import com.bsmwireless.models.InspectionReport;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.LoginModel;
import com.bsmwireless.models.PasswordModel;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.RuleSelectionModel;
import com.bsmwireless.models.SyncInspectionCategory;
import com.bsmwireless.models.User;
import com.bsmwireless.models.Vehicle;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServiceApi {

    /**
     * Driver Login
     *
     * @param request - model with login information.
     * @return User Response {@link User}.
     */
    @POST("v1/login/driver")
    Observable<User> loginUser(@Body LoginModel request);

    /**
     * Request to update current token
     *
     * @return Auth data {@link Auth}
     */
    @POST("v1/app/newtoken")
    Observable<Auth> refreshToken();

    /**
     * User logout
     *
     * @param logoutEvent
     * @param token token of user which we want to logout
     * @param driver id of user which we want to logout
     * @return Logout response {@link ResponseMessage}
     */
    @POST("v1/app/logout")
    Observable<ResponseMessage> logout(@Body ELDEvent logoutEvent, @Header("X-Token") String token, @Header("X-Driver") String driver);

    /**
     * User logout
     *
     * @param logoutEvent
     * @return Logout response {@link ResponseMessage}
     */
    @POST("v1/app/logout")
    Observable<ResponseMessage> logout(@Body ELDEvent logoutEvent);

    /**
     * Send ELD event;
     * Send ELD certify event; According doc section 4.5.1.4. It does same thing as dutyevents
     *
     * @param event driver status.
     * @return update driver status response {@link ResponseMessage}.
     */
    @POST("v1/app/driver/certify")
    Observable<ResponseMessage> postNewELDEvent(@Body ELDEvent event);

    /**
     * Send ELD events;
     * Send ELD driver duty status; According doc section 4.5.1.1; 4.5.1.2; 4.5.1.3; 4.5.1.4; 4.5.1.7; It sends a list of new records ordered by event time.
     *
     * @param statusList driver status list.
     * @return update driver status response {@link ResponseMessage}.
     */
    @POST("v1/app/driver/dutyevents")
    Observable<ResponseMessage> postNewELDEvents(@Body List<ELDEvent> statusList);

    /**
     * Update ELD Event which already sent to server
     *
     * @param events driver status list.
     * @return update events response {@link ResponseMessage}.
     */
    @POST("v1/app/driver/updateevents")
    Observable<ResponseMessage> updateELDEvents(@Body List<ELDEvent> events);

    /**
     * Fetch processed driver records.
     *
     * @param startTime start time.
     * @param endTime   end time.
     * @return List of unidentified or changed event records {@link ELDEvent}.
     */
    @GET("v1/sync/records/search/{start}/{end}")
    Observable<List<ELDEvent>> getELDEvents(@Path("start") Long startTime, @Path("end") Long endTime);

    /**
     * Fetch processed driver records.
     *
     * @param startTime start time.
     * @param endTime   end time.
     * @param token token of user
     * @param driver id of user
     * @return List of unidentified or changed event records {@link ELDEvent}.
     */
    @GET("v1/sync/records/search/{start}/{end}")
    Observable<List<ELDEvent>> getELDEvents(@Path("start") Long startTime, @Path("end") Long endTime, @Header("X-Token") String token, @Header("X-Driver") String driver);

    /**
     * Update unidentified records or change record request.
     *
     * @param events events list.
     * @return Response {@link ResponseMessage}.
     */
    @PUT("v1/sync/records/update")
    Observable<ResponseMessage> updateRescords(@Body List<ELDUpdate> events);

    /**
     * Update user profile.
     *
     * @param driverProfile driver information.
     * @return update driver information response {@link ResponseMessage}.
     */
    @PUT("v1/app/driver/profile")
    Observable<ResponseMessage> updateDriverProfile(@Body DriverProfileModel driverProfile);

    /**
     * Update driver password
     *
     * @param passwordModel new password information.
     * @return update driver password response {@link ResponseMessage}.
     */
    @PUT("v1/app/driver/pswd")
    Observable<ResponseMessage> updateDriverPassword(@Body PasswordModel passwordModel);

    /**
     * Update driver signature
     *
     * @param signature new driver signature.
     * @return update driver signature response {@link ResponseMessage}.
     */
    @PUT("v1/app/driver/signature")
    Observable<ResponseMessage> updateDriverSignature(@Body DriverSignature signature);

    /**
     * Update driver's HOS rule selection
     *
     * @param ruleSelectionModel rule selection model.
     * @return update driver rule response {@link ResponseMessage}.
     */
    @PUT("v1/app/driver/rules")
    Single<ResponseMessage> updateDriverRule(@Body RuleSelectionModel ruleSelectionModel);

    /**
     * Update driver's home terminal selection
     *
     * @param driverHomeTerminal home terminal.
     * @return update driver home terminal response {@link ResponseMessage}.
     */
    @PUT("v1/app/driver/home")
    Observable<ResponseMessage> updateDriverHomeTerminal(@Body DriverHomeTerminal driverHomeTerminal);

    /**
     * Inspection Categories from category Ids
     *
     * @param categoryIds list of inspection category ids, comma separated.
     * @return Sync Inspection Items Response {@link SyncInspectionCategory}.
     */
    @GET("v1/sync/inspection_items/search/{categoryIds}")
    Observable<List<SyncInspectionCategory>> getInspectionItemsByCategoryIds(@Path("categoryIds") String categoryIds);

    /**
     * Inspection Categories for the box. It is used after driver selects a vehicle, which maps to a box
     *
     * @param lastUpdate list of inspection category ids, comma separated.
     * @return Sync Inspection Items Response {@link SyncInspectionCategory}.
     */
    @GET("v1/sync/inspection_items/{lastupdate}")
    Observable<List<SyncInspectionCategory>> getInspectionItemsByLastUpdate(@Path("lastupdate") long lastUpdate);

    /**
     * Sync Inspection Report.
     *
     * @param lastUpdate long unix timestamp
     * @param isTrailer  enum: 0 - regular vehicle, 1 - trailer
     * @param beginDate  begin date info
     * @return Inspection Report Response {@link InspectionReport}
     */
    @GET("v1/sync/inspections/report/{lastUpdate}/{isTrailer}/{beginDate}")
    Observable<InspectionReport> syncInspectionReport(@Path("lastUpdate") Long lastUpdate,
                                                      @Path("isTrailer") int isTrailer,
                                                      @Path("beginDate") Long beginDate);

    /**
     * Get LogSheet header information list.
     *
     * @param startLogDay epoch unix time stamp, in miliseconds.
     * @param endLogDay   epoch unix time stamp, in miliseconds.
     * @return LogSheet Response {@link LogSheetHeader}
     */
    @GET("v1/sync/logsheet/headers/{start}/{end}")
    Observable<List<LogSheetHeader>> getLogSheets(@Path("start") Long startLogDay,
                                                  @Path("end") Long endLogDay);

    /**
     * Send LogSheet header information. If logday is same,
     * server will do the update of the existing record.
     * Full record is required for each call.
     *
     * @param logSheetHeader info.
     * @return Update LogSheet header Response {@link ResponseMessage}
     */
    @PUT("v1/sync/logsheet/header")
    Single<ResponseMessage> updateLogSheetHeader(@Body LogSheetHeader logSheetHeader);

    /**
     * Link the driver to the vehicle, fetch unidentified record for update, and carrier's change requests.
     *
     * @param status current driver status
     * @return Pair Vehicle Response {@link ELDEvent}
     */
    @POST("v1/login/pair")
    Observable<List<ELDEvent>> pairVehicle(@Body ELDEvent status);

    /**
     * Search Vehicle.
     *
     * @param keyword search keyword.
     * @return Vehicle Attributes Response {@link Vehicle}.
     */
    @GET("v1/app/vehicles/search/{keyword}")
    Observable<List<Vehicle>> searchVehicles(@Path("keyword") String keyword);

    @POST("v1/app/reports/logsheets/{start}/{end}/{option}")
    Single<ResponseMessage> sendReport(@Path("start") long start, @Path("end") long end, @Path("option") int option, @Body ELDEvent report);
}
