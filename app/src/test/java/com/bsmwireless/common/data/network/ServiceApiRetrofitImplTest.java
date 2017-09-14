package com.bsmwireless.common.data.network;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.models.ELDEvent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.observers.TestObserver;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.fail;

/**
 * Tests for ServiceApi as implemented by Retrofit.
 * <p>
 * These tests confirm the behavior of ServiceApi w.r.t. possible ELD API response
 * situations. These are also meant to help document expected Retrofit behavior for new
 * devs.
 * <p>
 * They aren't expected to ever fail as Retrofit is stable with well-defined behavior.
 * <p>
 * Complete coverage of ServiceApi is not required.
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceApiRetrofitImplTest {

    private ServiceApi mServiceApi;
    private MockWebServer mMockWebServer;


    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        mMockWebServer = new MockWebServer();

        Retrofit retrofit = new Retrofit.Builder()
                    .client(new OkHttpClient())
                    .baseUrl(mMockWebServer.url("").toString())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

        mServiceApi = retrofit.create(ServiceApi.class);
    }

    @After
    public void after() throws Exception {
        mMockWebServer.shutdown();
    }

    @Test
    public void testRetrofitEldEventApiOkWithExtraFields() {

        // given
        String jsonString = "";

        try {
            jsonString = new JSONObject()
                    .put("status", 1)
                    .put("origin", 2)
                    .put("eventType", 5)
                    .put("eventCode", 1)
                    .put("eventTime", 1504720113000L)
                    .put("odometer", 5605)
                    .put("engineHours", 0)
                    .put("lat", 20.14183333333333)
                    .put("lng", -152.98891666666665)
                    .put("distance", 0)
                    .put("location", "No Address Available")
                    .put("boxId", 312648)
                    .put("vehicleId", 111111)
                    .put("id", 29230)
                    .put("tzOffset", 0)
                    .put("timezone", "Canada/Eastern")
                    .put("mobileTime", 1504720113206L)
                    .put("driverId", 31415)
                    .put("sequence", 107)
                    .put("logsheet", 20170906)
                    .put("malfunction", false)
                    .put("diagnostic", false)
                    .put("thisIsAFakeField", 123445) // to be ignored by retrofit
                    .put("yetAnotherToBeIgnored", 123213) // to be ignored by retrofit
                    .toString();
        } catch (JSONException e) {
            fail("JSON Exception: " + e.getMessage());
        }

        mMockWebServer.enqueue(new MockResponse().setBody("[" +
                jsonString +
                "]"));

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();


        // when
        mServiceApi.getELDEvents(12340L, 22222L).subscribe(testObserver);


        // then
        try {
            RecordedRequest request1 = mMockWebServer.takeRequest();
            System.out.println(request1.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        testObserver.assertNoErrors();

        System.out.println("PARSED: " + testObserver.values().toString());
    }

}
