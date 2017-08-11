package com.bsmwireless.data.storage.users;

import com.bsmwireless.models.User;

public class UserConverter {
    public static User toUser(UserEntity entity) {
        User user = null;

        if (entity != null) {
            user = new User();
            user.setId(entity.getId());
            user.setTimezone(entity.getTimezone());
            user.setEmail(entity.getEmail());
            user.setLicense(entity.getLicense());
            user.setSignature(entity.getSignature());
            user.setExempt(entity.getExempt());
            user.setUpdated(entity.getUpdated());
            user.setDot(entity.getDot());
            user.setSyncTime(entity.getSyncTime());
            //TODO: set configurations
            user.setFirstName(entity.getFirstName());
            user.setMidName(entity.getMidName());
            user.setLastName(entity.getLastName());
            user.setDutyCycle(entity.getDutyCycle());
            user.setRuleException(entity.getRuleException());
            user.setHomeTermId(entity.getHomeTermId());
            user.setUom(entity.getUom());

        }

        return user;
    }

    public static UserEntity toEntity(User user) {
        UserEntity entity = null;

        if (user != null) {
            entity = new UserEntity();
            entity.setId(user.getId());
            entity.setTimezone(user.getTimezone());
            entity.setEmail(user.getEmail());
            entity.setLicense(user.getLicense());
            entity.setSignature(user.getSignature());
            entity.setExempt(user.getExempt());
            entity.setUpdated(user.getUpdated());
            entity.setDot(user.getDot());
            entity.setSyncTime(user.getSyncTime());
            //TODO: set configurations
            entity.setFirstName(user.getFirstName());
            entity.setMidName(user.getMidName());
            entity.setLastName(user.getLastName());
            entity.setDutyCycle(user.getDutyCycle());
            entity.setRuleException(user.getRuleException());
            entity.setHomeTermId(user.getHomeTermId());
            entity.setUom(user.getUom());
        }

        return entity;
    }
}
