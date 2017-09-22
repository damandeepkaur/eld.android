package com.bsmwireless.screens.roadside;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.models.Carrier;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.HomeTerminal;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.User;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.screens.roadside.dagger.DaggerRoadsideComponent;
import com.bsmwireless.screens.roadside.dagger.RoadsideModule;
import com.bsmwireless.widgets.logs.calendar.CalendarLayout;
import com.bsmwireless.widgets.logs.graphview.GraphLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public final class RoadsideFragment extends BaseFragment implements RoadsideView {

    @BindView(R.id.roadside_events)
    RecyclerView mEventsView;

    @BindView(R.id.roadside_headers)
    RecyclerView mHeadersView;

    @BindView(R.id.roadside_calendar)
    CalendarLayout mCalendarLayout;

    @BindView(R.id.roadside_graph)
    GraphLayout mGraphLayout;

    @Inject
    RoadsidePresenter mPresenter;

    private RoadsideAdapter mEventsAdapter;
    private RoadsideAdapter mHeadersAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roadside, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        DaggerRoadsideComponent.builder().appComponent(App.getComponent()).roadsideModule(new RoadsideModule(this)).build().inject(this);

        init();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.onViewCreated();
    }


    private void init() {
        mCalendarLayout.setOnItemSelectedListener(log -> mPresenter.onDateChanged(mCalendarLayout.getCurrentItem().getCalendar()));

        mHeadersAdapter = new RoadsideAdapter(7, null, new ArrayList<>(Arrays.asList(0, 2, 4, 6)), false);
        mHeadersView.setLayoutManager(new CustomGridLayoutManager(mContext, 7));
        mHeadersView.setAdapter(mHeadersAdapter);

        mEventsAdapter = new RoadsideAdapter(7, null, new ArrayList<>(Collections.singletonList(0)), true);
        mEventsView.setLayoutManager(new CustomGridLayoutManager(mContext, 7));
        mEventsView.setAdapter(mEventsAdapter);
    }

    @Override
    public void showEvents(List<String> events) {
        mEventsAdapter.setData(events);
    }

    @Override
    public void showHeaders(List<String> headers) {
        mHeadersAdapter.setData(headers);
    }

    @Override
    public void showGraph(List<EventLogModel> eventLogs, ELDEvent event) {
        mGraphLayout.setPrevDayEvent(event);
        mGraphLayout.setELDEvents(eventLogs);
        mGraphLayout.invalidate();
    }

    public List<String> getEventsData(List<ELDEvent> events) {
        List<String> data = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.US);

        String last = "";
        String current = "";

        if (!events.isEmpty()) {
            data.add(getString(R.string.roadside_time));
            data.add(getString(R.string.roadside_location));
            data.add(getString(R.string.roadside_odometer));
            data.add(getString(R.string.roadside_engine));
            data.add(getString(R.string.roadside_event));
            data.add(getString(R.string.roadside_origin));
            data.add(getString(R.string.roadside_comment));
        }

        for (ELDEvent event : events) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(event.getTimezone()));
            timeFormat.setTimeZone(TimeZone.getTimeZone(event.getTimezone()));

            //write date
            current = dateFormat.format(event.getEventTime());
            if (!current.equals(last)) {
                data.add(current);
                data.add(getString(R.string.roadside_empty));
                data.add(getString(R.string.roadside_empty));
                data.add(getString(R.string.roadside_empty));
                data.add(getString(R.string.roadside_empty));
                data.add(getString(R.string.roadside_empty));
                data.add(getString(R.string.roadside_empty));

                last = current;
            }

            //write event
            data.add(timeFormat.format(event.getEventTime()));
            data.add(event.getLocation());
            data.add(String.valueOf(event.getOdometer()));
            data.add(String.valueOf(event.getEngineHours()));
            data.add(ELDEvent.getEvent(event.getEventType(), event.getEventCode()));
            data.add(ELDEvent.EventOrigin.getOriginByCode(event.getOrigin()).name());
            data.add(event.getComment());
        }

        return data;
    }

    public List<String> getHeadersData(LogSheetHeader header, ELDEvent lastEvent, Vehicle vehicle) {
        List<String> data = new ArrayList<>();

        long time = DateUtils.convertLogDayToUnixMs(header.getLogDay());

        HomeTerminal terminal = header.getHomeTerminal();
        TimeZone timeZone = TimeZone.getTimeZone(terminal == null ? "UTC" : header.getHomeTerminal().getTimezone());

        long startDate = DateUtils.getStartDayTimeInMs(timeZone.getID(), time);
        long endDate = DateUtils.getEndDayTimeInMs(timeZone.getID(), time);

        User driver = mPresenter.getUser(header.getDriverId());
        List<User> coDrivers = mPresenter.getCoDrivers(header.getCoDriverIds());

        data.addAll(getCarrierData(timeZone, time, driver.getCarriers()));
        data.addAll(getDriverData(driver, coDrivers));
        data.addAll(getVehicleData(lastEvent, vehicle, header.getShippingId()));

        String location = lastEvent == null ? null : lastEvent.getLocation();

        data.addAll(getStatusesData(
                location,
                driver.getExempt(),
                mPresenter.getMalfunctionsCount(header.getDriverId(), startDate, endDate) > 0,
                mPresenter.getDiagnosticsCount(header.getDriverId(), startDate, endDate) > 0));

        return data;
    }

    private List<String> getCarrierData(TimeZone timeZone, long time, List<Carrier> carriers) {
        List<String> data = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy", Locale.US);

        StringBuilder carrierBuilder = new StringBuilder();
        StringBuilder carrierDotBuilder = new StringBuilder();

        for (Carrier carrier : carriers) {
            carrierBuilder.append(carrier.getName());
            carrierBuilder.append("\n");

            carrierDotBuilder.append(carrier.getDot());
            carrierDotBuilder.append("\n");
        }

        data.add(mContext.getString(R.string.roadside_date));
        data.add(mContext.getString(R.string.roadside_starting_time));
        data.add(mContext.getString(R.string.roadside_time_zone));
        data.add(mContext.getString(R.string.roadside_carrier_name));
        data.add(mContext.getString(R.string.roadside_carrier_dot));
        data.add(mContext.getString(R.string.roadside_empty));
        data.add(mContext.getString(R.string.roadside_empty));

        data.add(dateFormat.format(time));
        data.add(mContext.getString(R.string.roadside_midnight));
        data.add(DateUtils.getFullTimeZone(timeZone.getID(), time));
        data.add(carrierBuilder.toString());
        data.add(carrierDotBuilder.toString());
        data.add(mContext.getString(R.string.roadside_empty));
        data.add(mContext.getString(R.string.roadside_empty));

        return data;
    }

    private List<String> getDriverData(User driver, List<User> coDrivers) {
        List<String> data = new ArrayList<>();

        StringBuilder coDriversBuilder = new StringBuilder();
        StringBuilder coDriversIdBuilder = new StringBuilder();

        for (User coDriver : coDrivers) {
            coDriversBuilder.append(coDriver.getLastName());
            coDriversBuilder.append(", ");
            coDriversBuilder.append(coDriver.getFirstName());
            coDriversBuilder.append("\n");

            coDriversIdBuilder.append(coDriver.getId());
            coDriversIdBuilder.append("\n");
        }

        data.add(mContext.getString(R.string.roadside_driver_name));
        data.add(mContext.getString(R.string.roadside_driver_id));
        data.add(mContext.getString(R.string.roadside_driver_state));
        data.add(mContext.getString(R.string.roadside_driver_license));
        data.add(mContext.getString(R.string.roadside_codriver_name));
        data.add(mContext.getString(R.string.roadside_codriver_id));
        data.add(mContext.getString(R.string.roadside_empty));

        data.add(String.format("%s, %s", driver.getLastName(), driver.getFirstName()));
        data.add(String.valueOf(driver.getId()));
        data.add(mContext.getString(R.string.roadside_undefined));
        data.add(driver.getLicense());
        data.add(coDriversBuilder.toString());
        data.add(coDriversIdBuilder.toString());
        data.add(mContext.getString(R.string.roadside_empty));

        return data;
    }

    private List<String> getVehicleData(ELDEvent latestEvent, Vehicle vehicle, String shippingId) {
        List<String> data = new ArrayList<>();

        data.add(mContext.getString(R.string.roadside_odometer));
        data.add(mContext.getString(R.string.roadside_engine));
        data.add(mContext.getString(R.string.roadside_eld_provider));
        data.add(mContext.getString(R.string.roadside_eld_id));
        data.add(mContext.getString(R.string.roadside_truck_id));
        data.add(mContext.getString(R.string.roadside_truck_vin));
        data.add(mContext.getString(R.string.roadside_shipping_id));

        data.add(latestEvent == null ? mContext.getString(R.string.roadside_not_set) : String.valueOf(latestEvent.getOdometer()));
        data.add(latestEvent == null ? mContext.getString(R.string.roadside_not_set) : String.valueOf(latestEvent.getEngineHours()));
        data.add(mContext.getString(R.string.roadside_undefined));
        data.add(vehicle == null ? mContext.getString(R.string.roadside_not_set) : String.valueOf(vehicle.getBoxId()));
        data.add(vehicle == null? mContext.getString(R.string.roadside_not_set) : String.valueOf(vehicle.getId()));
        data.add(vehicle == null? mContext.getString(R.string.roadside_not_set) : vehicle.getDot());
        data.add(shippingId);

        return data;
    }

    private List<String> getStatusesData(String location, boolean isExempt, boolean hasMulfunctions, boolean hasDiagnostic) {
        List<String> data = new ArrayList<>();
        data.add(mContext.getString(R.string.roadside_location));
        data.add(mContext.getString(R.string.roadside_unidentified_records));
        data.add(mContext.getString(R.string.roadside_exempt));
        data.add(mContext.getString(R.string.roadside_malfunctions));
        data.add(mContext.getString(R.string.roadside_diagnostic));
        data.add(mContext.getString(R.string.roadside_empty));
        data.add(mContext.getString(R.string.roadside_empty));

        data.add(location == null ? mContext.getString(R.string.roadside_not_set) : location);
        data.add(mContext.getString(R.string.roadside_undefined));
        data.add(isExempt ? mContext.getString(R.string.roadside_yes) : mContext.getString(R.string.roadside_no));
        data.add(hasMulfunctions ? mContext.getString(R.string.roadside_yes) : mContext.getString(R.string.roadside_no));
        data.add(hasDiagnostic ? mContext.getString(R.string.roadside_yes) : mContext.getString(R.string.roadside_no));
        data.add(mContext.getString(R.string.roadside_empty));
        data.add(mContext.getString(R.string.roadside_empty));

        return data;
    }

    private static class CustomGridLayoutManager extends GridLayoutManager {
        CustomGridLayoutManager(Context context, int count) {
            super(context, count);
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }
}
