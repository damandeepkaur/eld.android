package com.bsmwireless.common.utils;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.Carrier;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.SyncConfiguration;
import com.bsmwireless.models.User;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public final class LogHeaderUtils {
    private static final String CARRIER_DELIMITER = ",";
    private static final String DISTANCE_DELIMITER = "/";
    private static final String DRIVERS_NAME_DIVIDER = ", ";

    private final UserInteractor mUserInteractor;

    @Inject
    public LogHeaderUtils(UserInteractor userInteractor){
        mUserInteractor = userInteractor;
    }

    @NonNull
    public String makeDrivername(@NonNull User user) {
        return String.format("%1$s %2$s", user.getFirstName(), user.getLastName());
    }

    @NonNull
    public String getAllExemptions(@NonNull User user, @NonNull SyncConfiguration.Type syncConfiguration) {
        String allExemptions = null;
        List<SyncConfiguration> configurations = user.getConfigurations();
        if (configurations != null) {
            for (SyncConfiguration configuration : configurations) {
                if (syncConfiguration.getName().equals(configuration.getName())) {
                    allExemptions = configuration.getValue();
                    break;
                }
            }
        }

        if (allExemptions == null) {
            allExemptions = "";
        }
        return allExemptions;
    }

    @NonNull
    public String makeCarrierName(@NonNull User user) {
        String carrierName;
        List<Carrier> carriers = user.getCarriers();
        if (carriers != null && !carriers.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Carrier carrier : carriers) {
                sb.append(carrier.getName()).append(CARRIER_DELIMITER);
            }
            carrierName = sb.substring(0, sb.length() - CARRIER_DELIMITER.length());
        } else {
            carrierName = "";
        }
        return carrierName;
    }

    @NonNull
    public OdometerResult calculateOdometersValue(@NonNull List<ELDEvent> events) {
        StringBuilder startValueBuilder = new StringBuilder();
        StringBuilder endValueBuilder = new StringBuilder();
        long distance = 0;

        if (events.isEmpty()) {
            startValueBuilder.append("0");
            endValueBuilder.append("0");
        } else {

            SparseArray<List<ELDEvent>> eventsByBoxId = new SparseArray<>(3);
            for (ELDEvent event : events) {

                List<ELDEvent> eldEvents = eventsByBoxId.get(event.getBoxId());
                if (eldEvents == null) {
                    eldEvents = new ArrayList<>();
                    eventsByBoxId.put(event.getBoxId(), eldEvents);
                }
                eldEvents.add(event);
            }

            for (int i = 0; i < eventsByBoxId.size(); i++) {
                List<ELDEvent> eventsForBox = eventsByBoxId.get(eventsByBoxId.keyAt(i));

                if (eventsForBox.isEmpty()) {
                    continue;
                }

                startValueBuilder.append(eventsForBox.get(0).getOdometer())
                        .append(DISTANCE_DELIMITER);

                Integer endOdometer = eventsForBox.get(eventsForBox.size() - 1).getOdometer();
                endValueBuilder.append(endOdometer == null ? 0 : endOdometer)
                        .append(DISTANCE_DELIMITER);
                distance += calculateOdometer(eventsForBox);
            }

            truncateBuilder(startValueBuilder, DISTANCE_DELIMITER.length());
            truncateBuilder(endValueBuilder, DISTANCE_DELIMITER.length());
        }

        return new OdometerResult(startValueBuilder.toString(),
                endValueBuilder.toString(),
                distance);
    }

    public String getCoDriversName(LogSheetHeader header) {

        if (header == null) return "";

        String codriverStringIds = header.getCoDriverIds();
        List<Integer> coDriversIds = ListConverter.toIntegerList(codriverStringIds);

        if (coDriversIds.isEmpty()) return "";

        List<UserEntity> names = mUserInteractor.getCoDriversName(coDriversIds);

        if (names.isEmpty()) {
            // no users in the database with these ids, workaround - show co-drivers ids
            return codriverStringIds;
        }

        StringBuilder coDriversNames = new StringBuilder();
        for (UserEntity userEntity : names) {
            coDriversNames
                    .append(userEntity.getFirstName()).append(" ").append(userEntity.getLastName())
                    .append(DRIVERS_NAME_DIVIDER);
        }
        return coDriversNames.substring(0, coDriversNames.length() - DRIVERS_NAME_DIVIDER.length());
    }

    private void truncateBuilder(StringBuilder stringBuilder, int length) {
        stringBuilder.delete(
                stringBuilder.length() - length,
                stringBuilder.length());
    }

    private long calculateOdometer(List<ELDEvent> events) {

        long distance = 0;

        long startDrivingOdometer = 0;
        boolean isPreviousDriving = false;
        for (ELDEvent event : events) {

            if (DutyType.DRIVING.isSame(event.getEventType(), event.getEventCode())) {
                // If few driving events meets successively takes only first
                if (!isPreviousDriving) {
                    // start driving event, save odometer value
                    startDrivingOdometer = event.getOdometer() != null ? event.getOdometer() : 0;
                    isPreviousDriving = true;
                }
            } else {

                if (isPreviousDriving) {
                    // stop driving event
                    Integer odometer = event.getOdometer();
                    int currentOdometer = odometer != null ? odometer : 0;
                    distance += currentOdometer - startDrivingOdometer;
                    startDrivingOdometer = 0;
                    isPreviousDriving = false;
                }
            }
        }

        return distance;
    }

    public static final class OdometerResult {
        public final String startValue;
        public final String endValue;
        public final long distance;

        private OdometerResult(String startValue, String endValue, long distance) {
            this.startValue = startValue;
            this.endValue = endValue;
            this.distance = distance;
        }
    }
}
