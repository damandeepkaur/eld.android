package com.bsmwireless.data.storage.vehicle;

import com.bsmwireless.models.Vehicle;

public class VehicleConverter {

    public static Vehicle toVehicle(VehicleEntity entity) {
        Vehicle vehicle = null;

        if (entity != null) {
            vehicle = new Vehicle();
            vehicle.setId(entity.getId());
            vehicle.setName(entity.getName());
            vehicle.setBoxId(entity.getBoxId());
            vehicle.setLicense(entity.getLicense());
            vehicle.setProvince(entity.getProvince());
            vehicle.setWeight(entity.getWeight());
            vehicle.setDot(entity.getDot());
        }

        return vehicle;
    }

    public static VehicleEntity toEntity(Vehicle vehicle) {
        VehicleEntity entity = null;

        if (vehicle != null) {
            entity = new VehicleEntity();
            entity.setId(vehicle.getId());
            entity.setName(vehicle.getName());
            entity.setBoxId(vehicle.getBoxId());
            entity.setLicense(vehicle.getLicense());
            entity.setProvince(vehicle.getProvince());
            entity.setWeight(vehicle.getWeight());
            entity.setDot(vehicle.getDot());
        }

        return entity;
    }

}