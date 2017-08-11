package com.bsmwireless.data.storage.carriers;

import com.bsmwireless.models.Carrier;

import java.util.ArrayList;
import java.util.List;

public class CarrierConverter {
    public static Carrier toCarrier(CarrierEntity entity) {
        Carrier carrier = null;

        if (entity != null) {
            carrier = new Carrier();
            carrier.setDot(entity.getDot());
            carrier.setAddress(entity.getAddress());
            carrier.setLastModified(entity.getLastModified());
            carrier.setName(entity.getName());
            carrier.setOrgId(entity.getOrgId());
        }

        return carrier;
    }

    public static CarrierEntity toEntity(Carrier carrier, Integer userId) {
        CarrierEntity entity = null;

        if (carrier != null) {
            entity = new CarrierEntity();
            entity.setDot(carrier.getDot());
            entity.setAddress(carrier.getAddress());
            entity.setLastModified(carrier.getLastModified());
            entity.setName(carrier.getName());
            entity.setOrgId(carrier.getOrgId());
            entity.setUserId(userId);
        }

        return entity;
    }

    public static List<Carrier> toCarrierList(List<CarrierEntity> entities) {
        List<Carrier> carriers = null;
        if (entities != null) {
            carriers = new ArrayList<>();
            for (CarrierEntity entity : entities) {
                carriers.add(toCarrier(entity));
            }
        }
        return carriers;
    }

    public static List<CarrierEntity> toEntityList(List<Carrier> carriers, Integer userId) {
        List<CarrierEntity> entities = null;
        if (carriers != null) {
            entities = new ArrayList<>();
            for (Carrier carrier : carriers) {
                entities.add(toEntity(carrier, userId));
            }
        }
        return entities;
    }
}
