package com.bsmwireless.data.network;

import com.bsmwireless.models.CUDTripInfo;
import com.bsmwireless.models.Category;
import com.bsmwireless.models.Driver;
import com.bsmwireless.models.DriverLog;
import com.bsmwireless.models.DriverStatus;
import com.bsmwireless.models.EmailReport;
import com.bsmwireless.models.Event;
import com.bsmwireless.models.HOSAlert;
import com.bsmwireless.models.Location;
import com.bsmwireless.models.LoginRequest;
import com.bsmwireless.models.NewRule;
import com.bsmwireless.models.Registry;
import com.bsmwireless.models.RegistryInformation;
import com.bsmwireless.models.Report;
import com.bsmwireless.models.Response;
import com.bsmwireless.models.Rule;
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
    Observable<User> loginUser(@Body LoginRequest request);

    /**
     * Search Vehicle.
     *
     * @param field  search field enum: 0 - SAP, 1 - legacy number, 2 - equip number,
     *                3 - description, 4 - license plate, 5 - boxId
     * @param keyword search keyword
     * @param isScan  enum: 0 - search vehicle, 1 - scan vehicle
     * @return Vehicle Attributes Response {@link Vehicle}.
     */
    @GET("v1/sync/vehicles/{field}/{keyword}/{isscan}")
    Observable<List<Vehicle>> searchVehicles(@Path("field") int field,
                                             @Path("keyword") String keyword,
                                             @Path("isscan") int isScan);

    /**
     * Get Vehicle by boxId.
     *
     * @param boxId id of the box paired with the vehicle.
     * @return Vehicle Attributes Response {@link Vehicle}.
     */
    @GET("v1/sync/vehicles/{boxId}")
    Observable<Vehicle> getVehicleByBoxId(@Path("boxId") Integer boxId);

    /**
     * Sync Inspection Items.
     *
     * @param boxId       id of the box paired with the vehicle.
     * @param categoryIds list of inspection category ids, comma separated.
     * @param language    language code such as en, fr or es.
     * @return Sync Inspection Items Response {@link Category}.
     */
    @GET("v1/sync/inspection_items/categories/{boxid}/{categoryids}/{language}")
    Observable<List<Category>> syncInspectionItems(@Path("boxid") Integer boxId,
                                                   @Path("categoryIds") String categoryIds,
                                                   @Path("language") String language);

    /**
     * Sync Inspection Items (Box).
     *
     * @param boxId      id of the box paired with the vehicle.
     * @param lastUpdate last update epoch time (UTC).
     * @param language   language code such as en, fr or es.
     * @return Sync Inspection Items Response {@link Category}.
     */
    @GET("v1/sync/inspection_items/box/{boxid}/{lastupdate}/{language}")
    Observable<List<Category>> syncInspectionBoxes(@Path("boxid") Integer boxId,
                                                   @Path("lastupdate") String lastUpdate,
                                                   @Path("language") String language);

    /**
     * Sync Inspection Items (Trailer).
     *
     * @param trailerId id of the box paired with the trailer (TBD: boxid or trailer?).
     * @return Sync Inspection Items Response {@link Category}.
     */
    @GET("v1/sync/inspection_items/trailer/{trailerid}")
    Observable<List<Category>> syncInspectionTrailers(@Path("trailerid") Integer trailerId);

    /**
     * Get last 14 days of driver time log, trip info and rule selection history.
     * TODO: API not ready
     *
     * @return
     */
    @GET("v1/sync/dlogs")
    Observable<Object> syncDriverLogs();

    /**
     * Get assigned rule selections to the driver.
     *
     * @param boxId      id of the box paired with the vehicle.
     * @param lastUpdate last update epoch time (UTC).
     * @return Sync Rules Response {@link Rule}.
     */
    @GET("v1/sync/rules/{boxid}/{lastupdate}")
    Observable<Rule> syncRules(@Path("boxid") Integer boxId, @Path("lastupdate") String lastUpdate);

    /**
     * For driver to add a trailer on the fly.
     * TODO: API not ready
     *
     * @param trailer description of the trailer.
     * @return
     */
    @GET("v1/app/trailers")
    Observable<Object> createTrailer(@Body Trailer trailer);

    /**
     * Resolving addresses from the latitude/longitude pairs.
     * TODO: What does it mean [{latlng}]?
     *
     * @param latLng Coordinate list.
     * @return
     */
    @GET("v1/app/addresses/[{latlng}]")
    Observable<List<Location>> geocoding(@Path("latlng") List<Location> latLng);

    /**
     * Retrieve last 24 hour inspection report.
     *
     * @param boxId id of the box paired with the vehicle.
     * @return Inspection Report Response {@link Report}.
     */
    @GET("v1/app/inspections/{boxid}")
    Observable<List<Report>> inspectionReport(@Path("boxid") Integer boxId);

    /**
     * Submit driver time log.
     *
     * @param logs driver logs list.
     * @return delete driver logs response {@link Response}.
     */
    @DELETE("v1/app/dlogs")
    Observable<Response> deleteDriverLogs(@Body List<DriverLog> logs);

    /**
     * Submit driver time log.
     *
     * @param logs driver logs list.
     * @return update driver logs response {@link Response}.
     */
    @PUT("v1/app/dlogs")
    Observable<Response> updateDriverLogs(@Body List<DriverLog> logs);

    /**
     * Submit pre-trip or post-trip inspection, including defects and images.
     *
     * @param cudReport report information.
     * @return delete report response {@link Response}.
     */
    @DELETE("v1/app/inspections")
    Observable<Response> deleteCUDInspection(@Body Report cudReport);

    /**
     * Submit pre-trip or post-trip inspection, including defects and images.
     *
     * @param cudReport report information.
     * @return update report response {@link Response}.
     */
    @PUT("v1/app/inspections")
    Observable<Response> updateCUDInspection(@Body Report cudReport);

    /**
     * Submit driver profile.
     *
     * @param driver driver information.
     * @return update driver information response {@link Response}.
     */
    @PUT("v1/app/drivers")
    Observable<Response> updateDriver(@Body Driver driver);

    /**
     * Certify driver.
     *
     * @param status driver status.
     * @return certify driver response {@link Response}.
     */
    @POST("v1/sync/driver/certify")
    Observable<Response> certifyDriver(@Body DriverStatus status, @Header("X-Box") int boxId);

    /**
     * Logout current driver.
     *
     * @param status driver status.
     * @return driver logout response {@link Response}.
     */
    @POST("v1/sync/driver/logout")
    Observable<Response> logoutDriver(@Body DriverStatus status, @Header("X-Box") int boxId);

    /**
     * Sync current driver status.
     *
     * @param status driver status.
     * @return update driver status response {@link Response}.
     */
    @POST("v1/sync/driver/status")
    Observable<Response> syncDriverStatus(@Body List<DriverStatus> status);

    /**
     * Submit (add) HOS alert.
     *
     * @param alert alert information.
     * @return Add HOS alert response {@link Response}.
     */
    @POST("v1/app/hos/alerts")
    Observable<Response> addHOSAlert(@Body HOSAlert alert);

    /**
     * Submit trip information (Delete).
     *
     * @param tripInfo trip info.
     * @return Update trip reponse {@link Response}.
     */
    @DELETE("v1/app/trips")
    Observable<Response> deleteCUDTripInfo(@Body CUDTripInfo tripInfo);

    /**
     * Submit trip information (Update).
     *
     * @param tripInfo trip info.
     * @return Update trip reponse {@link Response}.
     */
    @PUT("v1/app/trips")
    Observable<Response> updateCUDTripInfo(@Body CUDTripInfo tripInfo);

    /**
     * Submit newRule selection.
     *
     * @param newRule newRule information.
     * @return Add newRule response {@link Response}.
     */
    @POST("v1/app/rules")
    Observable<Response> addRule(@Body NewRule newRule);

    /**
     * Submit emailing report request.
     *
     * @param report report information.
     * @return Email report response {@link Response}.
     */
    @POST("v1/app/reports")
    Observable<Response> emailReport(@Body EmailReport report);

    /**
     * Submit event, including box WIFI connect/disconnect, sensor failure.
     *
     * @param event event information.
     * @return Add Event Response {@link Response}.
     */
    @POST("v1/app/events")
    Observable<Response> addEvent(@Body Event event);

    /**
     * This service doesn’t require session token.
     *
     * @param registry registry information.
     * @return Registry Response {@link RegistryInformation}.
     */
    @POST("/registry/v1/sd")
    Observable<RegistryInformation> registry(@Body Registry registry);
}
