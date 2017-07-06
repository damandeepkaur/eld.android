package com.bsmwireless.data.network;

import com.bsmwireless.models.Auth;
import com.bsmwireless.models.CUDTripInfo;
import com.bsmwireless.models.Driver;
import com.bsmwireless.models.DriverLog;
import com.bsmwireless.models.ELDDriverStatus;
import com.bsmwireless.models.EmailReport;
import com.bsmwireless.models.Event;
import com.bsmwireless.models.HOSAlert;
import com.bsmwireless.models.InspectionReport;
import com.bsmwireless.models.Location;
import com.bsmwireless.models.LoginData;
import com.bsmwireless.models.NewRule;
import com.bsmwireless.models.Registry;
import com.bsmwireless.models.RegistryInformation;
import com.bsmwireless.models.Report;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.Rule;
import com.bsmwireless.models.SyncInspectionCategory;
import com.bsmwireless.models.Trailer;
import com.bsmwireless.models.User;
import com.bsmwireless.models.Vehicle;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServiceApi {

    /**
     * Login request Vehicle.
     *
     * @param request - model with login information
     * @return User Response {@link User}.
     */
    @POST("v1/login/driver")
    Observable<User> loginUser(@Body LoginData request);

    /**
     * Search Vehicle.
     *
     * @param field   search field enum: 0 - SAP, 1 - legacy number, 2 - equip number,
     *                3 - description, 4 - license plate, 5 - boxId
     * @param keyword search keyword.
     * @param isScan  enum: 0 - search vehicle, 1 - scan vehicle
     * @return Vehicle Attributes Response {@link Vehicle}.
     */
    @GET("v1/app/vehicles/{field}/{keyword}/{isscan}")
    Observable<List<Vehicle>> searchVehicles(@Path("field") int field,
                                             @Path("keyword") String keyword,
                                             @Path("isscan") int isScan);

    /**
     * Get Vehicle by boxId.
     *
     * @param boxId id of the box paired with the vehicle.
     * @return Vehicle Attributes Response {@link Vehicle}.
     */
    @GET("v1/app/vehicles/{boxId}")
    Observable<Vehicle> getVehicleByBoxId(@Path("boxId") Integer boxId);

    /**
     * Inspection Categories from category Ids
     *
     * @param boxId       id of the box paired with the vehicle.
     * @param categoryIds list of inspection category ids, comma separated.
     * @return Sync Inspection Items Response {@link SyncInspectionCategory}.
     */
    @GET("v1/sync/inspection_items/search/{categoryIds}")
    Observable<List<SyncInspectionCategory>> getInspectionItemsByCategoryIds(@Header("X-Box") Integer boxId,
                                                                             @Path("categoryIds") String categoryIds);

    /**
     * Inspection Categories for the box. It is used after driver selects a vehicle, which maps to a box
     *
     * @param boxId      id of the box paired with the vehicle.
     * @param lastUpdate list of inspection category ids, comma separated.
     * @return Sync Inspection Items Response {@link SyncInspectionCategory}.
     */
    @GET("v1/sync/inspection_items/{lastupdate}")
    Observable<List<SyncInspectionCategory>> getInspectionItemsByLastUpdate(@Header("X-Box") Integer boxId,
                                                                            @Path("lastupdate") long lastUpdate);

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
                                                      @Path("beginDate") Long beginDate,
                                                      @Header("X-Box") int boxId);

    /**
     * Sync current driver status.
     * According doc section 4.5.1.1;4.5.1.2; 4.5.1.3; 4.5.1.4; 4.5.1.7; It sends a single new record.
     *
     * @param status driver status.
     * @param boxId  box identifier (required).
     * @return update driver status response {@link ResponseMessage}.
     */
    @POST("v1/app/driver/status")
    Observable<ResponseMessage> syncDriverStatus(@Body ELDDriverStatus status, @Header("X-Box") int boxId);

    /**
     * Sync current driver statuses.
     * Send ELD driver duty status; According doc section 4.5.1.1;4.5.1.2; 4.5.1.3; 4.5.1.4; 4.5.1.7; It sends a list of new records ordered by event time.
     *
     * @param statusList driver status list.
     * @param boxId      box identifier (optional).
     * @return update driver status response {@link ResponseMessage}.
     */
    @POST("v1/app/driver/statuses")
    Observable<ResponseMessage> syncDriverStatuses(@Body List<ELDDriverStatus> statusList, @Header("X-Box") int boxId);

    /**
     * Get last 14 days of driver time log, trip info and rule selection history.
     *
     * @return
     */
    //TODO: Check this request with real server and update if necessary
    @GET("v1/sync/dlogs")
    Observable<Object> syncDriverLogs();

    /**
     * Get assigned rule selections to the driver.
     *
     * @param boxId      id of the box paired with the vehicle.
     * @param lastUpdate last update epoch time (UTC).
     * @return Sync Rules Response {@link Rule}.
     */
    //TODO: Check this request with real server and update if necessary
    @GET("v1/sync/rules/{boxid}/{lastupdate}")
    Observable<Rule> syncRules(@Path("boxid") Integer boxId, @Path("lastupdate") String lastUpdate);

    /**
     * For driver to add a trailer on the fly.
     *
     * @param trailer description of the trailer.
     * @return
     */
    //TODO: Check this request with real server and update if necessary
    @GET("v1/app/trailers")
    Observable<Object> createTrailer(@Body Trailer trailer);

    /**
     * Resolving addresses from the latitude/longitude pairs.
     *
     * @param latLng Coordinate list.
     * @return
     */
    //TODO: Check this request with real server and update if necessary
    @GET("v1/app/addresses/[{latlng}]")
    Observable<List<Location>> geocoding(@Path("latlng") List<Location> latLng);

    /**
     * Retrieve last 24 hour inspection report.
     *
     * @param boxId id of the box paired with the vehicle.
     * @return Inspection Report Response {@link Report}.
     */
    //TODO: Check this request with real server and update if necessary
    @GET("v1/app/inspections/{boxid}")
    Observable<List<Report>> inspectionReport(@Path("boxid") Integer boxId);

    /**
     * Submit driver time log.
     *
     * @param logs driver logs list.
     * @return delete driver logs response {@link ResponseMessage}.
     */
    //TODO: Check this request with real server and update if necessary
    @DELETE("v1/app/dlogs")
    Observable<ResponseMessage> deleteDriverLogs(@Body List<DriverLog> logs);

    /**
     * Submit driver time log.
     *
     * @param logs driver logs list.
     * @return update driver logs response {@link ResponseMessage}.
     */
    //TODO: Check this request with real server and update if necessary
    @PUT("v1/app/dlogs")
    Observable<ResponseMessage> updateDriverLogs(@Body List<DriverLog> logs);

    /**
     * Submit pre-trip or post-trip inspection, including defects and images.
     *
     * @param cudReport report information.
     * @return delete report response {@link ResponseMessage}.
     */
    //TODO: Check this request with real server and update if necessary
    @DELETE("v1/app/inspections")
    Observable<ResponseMessage> deleteCUDInspection(@Body Report cudReport);

    /**
     * Submit pre-trip or post-trip inspection, including defects and images.
     *
     * @param cudReport report information.
     * @return update report response {@link ResponseMessage}.
     */
    //TODO: Check this request with real server and update if necessary
    @PUT("v1/app/inspections")
    Observable<ResponseMessage> updateCUDInspection(@Body Report cudReport);

    /**
     * Submit driver profile.
     *
     * @param driver driver information.
     * @return update driver information response {@link ResponseMessage}.
     */
    //TODO: Check this request with real server and update if necessary
    @PUT("v1/app/drivers")
    Observable<ResponseMessage> updateDriver(@Body Driver driver);

    /**
     * Submit (add) HOS alert.
     *
     * @param alert alert information.
     * @return Add HOS alert response {@link ResponseMessage}.
     */
    @POST("v1/app/hos/alerts")
    //TODO: Check this request with real server and update if necessary
    Observable<ResponseMessage> addHOSAlert(@Body HOSAlert alert);

    /**
     * Submit trip information (Delete).
     *
     * @param tripInfo trip info.
     * @return Update trip reponse {@link ResponseMessage}.
     */
    @DELETE("v1/app/trips")
    //TODO: Check this request with real server and update if necessary
    Observable<ResponseMessage> deleteCUDTripInfo(@Body CUDTripInfo tripInfo);

    /**
     * Submit trip information (Update).
     *
     * @param tripInfo trip info.
     * @return Update trip reponse {@link ResponseMessage}.
     */
    @PUT("v1/app/trips")
    //TODO: Check this request with real server and update if necessary
    Observable<ResponseMessage> updateCUDTripInfo(@Body CUDTripInfo tripInfo);

    /**
     * Submit newRule selection.
     *
     * @param newRule newRule information.
     * @return Add newRule response {@link ResponseMessage}.
     */
    @POST("v1/app/rules")
    //TODO: Check this request with real server and update if necessary
    Observable<ResponseMessage> addRule(@Body NewRule newRule);

    /**
     * Submit emailing report request.
     *
     * @param report report information.
     * @return Email report response {@link ResponseMessage}.
     */
    @POST("v1/app/reports")
    //TODO: Check this request with real server and update if necessary
    Observable<ResponseMessage> emailReport(@Body EmailReport report);

    /**
     * Submit event, including box WIFI connect/disconnect, sensor failure.
     *
     * @param event event information.
     * @return Add Event ResponseMessage {@link ResponseMessage}.
     */
    @POST("v1/app/events")
    //TODO: Check this request with real server and update if necessary
    Observable<ResponseMessage> addEvent(@Body Event event);

    /**
     * This service doesn’t require session token.
     *
     * @param registry registry information.
     * @return Registry ResponseMessage {@link RegistryInformation}.
     */
    @POST("/registry/v1/sd")
    //TODO: Check this request with real server and update if necessary
    Observable<RegistryInformation> registry(@Body Registry registry);

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
     * @param status
     * @return Logout response {@link ResponseMessage}
     */
    @POST("v1/app/logout")
    Observable<ResponseMessage> logout(@Body ELDDriverStatus status);

}
