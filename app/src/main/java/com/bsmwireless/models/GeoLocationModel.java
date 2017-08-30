package com.bsmwireless.models;

import app.bsmuniversal.com.R;

/**
 * List of possibles location directions.
 */
public class GeoLocationModel {

    public enum GeoLocationEnum {
        AT(R.string.geo_location_at_code, R.string.geo_location_at_description),
        NORTH(R.string.geo_location_north_code, R.string.geo_location_north_description),
        NORTH_NORTH_EAST(R.string.geo_location_north_north_east_code, R.string.geo_location_north_north_east_description),
        NORTH_EAST(R.string.geo_location_north_east_code, R.string.geo_location_north_east_description),
        EAST_NORTH_EAST(R.string.geo_location_east_north_east_code, R.string.geo_location_east_north_east_description),
        EAST(R.string.geo_location_east_code, R.string.geo_location_east_description),
        EAST_SOUTH_EAST(R.string.geo_location_east_south_east_code, R.string.geo_location_east_south_east_description),
        SOUTH_EAST(R.string.geo_location_south_east_code, R.string.geo_location_south_east_description),
        SOUTH_SOUTH_EAST(R.string.geo_location_south_south_east_code, R.string.geo_location_south_south_east_description),
        SOUTH(R.string.geo_location_south_code, R.string.geo_location_south_description),
        SOUTH_SOUTH_WEST(R.string.geo_location_south_south_west_code, R.string.geo_location_south_south_west_description),
        SOUTH_WEST(R.string.geo_location_south_west_code, R.string.geo_location_south_west_description),
        WEST_SOUTH_WEST(R.string.geo_location_west_south_west_code, R.string.geo_location_west_south_west_description),
        WEST(R.string.geo_location_west_code, R.string.geo_location_west_description),
        WEST_NORTH_WEST(R.string.geo_location_west_north_west_code, R.string.geo_location_west_north_west_description),
        NORTH_WEST(R.string.geo_location_north_west_code, R.string.geo_location_north_west_description),
        NORTH_NORTH_WEST(R.string.geo_location_north_north_west_code, R.string.geo_location_north_north_west_description),
        INVALID(-1, 0);

        private int mIdResource;

        private int mDescriptionResource;

        GeoLocationEnum(int idResource, int descriptionResource) {
            this.mIdResource = idResource;
            this.mDescriptionResource = descriptionResource;
        }

        public int getIdResource() {
            return mIdResource;
        }

        public void setIdResource(int idResource) {
            this.mIdResource = idResource;
        }

        public int getDescriptionResource() {
            return mDescriptionResource;
        }

        public void setDescriptionResource(int descriptionResource) {
            this.mDescriptionResource = descriptionResource;
        }

        public static GeoLocationEnum getById(int id) {
            GeoLocationEnum[] values = GeoLocationEnum.values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getIdResource() == id) {
                    return values[i];
                }
            }
            return INVALID;
        }
    }
}
