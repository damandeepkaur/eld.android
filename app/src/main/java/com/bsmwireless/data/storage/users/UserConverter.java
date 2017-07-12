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
            user.setAddress(entity.getAddress());
            user.setCity(entity.getCity());
            user.setState(entity.getState());
            user.setCountry(entity.getCountry());
            user.setLicense(entity.getLicense());
            user.setSignature(entity.getSignature());
            user.setUpdated(entity.getUpdated());
            user.setOrganization(entity.getOrganization());
            user.setSyncTime(entity.getSyncTime());
            user.setSupervisor(entity.getIsSupervisor());
            user.setFirstName(entity.getFirstName());
            user.setMidName(entity.getMidName());
            user.setLastName(entity.getLastName());
            user.setRuleException(entity.getRuleException());
            user.setApplyDST(entity.getApplyDST());
            user.setUsCycle(entity.getUsCycle());
            user.setCaCycle(entity.getCaCycle());
            user.setCycleCountry(entity.getCycleCountry());
            user.setOrgAddr(entity.getOrgAddr());
            //TODO: set configurations
        }

        return user;
    }

    public static UserEntity toEntity(String accountName, User user) {
        UserEntity entity = null;

        if (user != null) {
            entity = new UserEntity();
            entity.setAccountName(accountName);
            entity.setId(user.getId());
            entity.setTimezone(user.getTimezone());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            entity.setCity(user.getCity());
            entity.setState(user.getState());
            entity.setCountry(user.getCountry());
            entity.setLicense(user.getLicense());
            entity.setSignature(user.getSignature());
            entity.setUpdated(user.getUpdated());
            entity.setOrganization(user.getOrganization());
            entity.setSyncTime(user.getSyncTime());
            entity.setIsSupervisor(user.getSupervisor());
            entity.setFirstName(user.getFirstName());
            entity.setMidName(user.getMidName());
            entity.setLastName(user.getLastName());
            entity.setRuleException(user.getRuleException());
            entity.setApplyDST(user.getApplyDST());
            entity.setUsCycle(user.getUsCycle());
            entity.setCaCycle(user.getCaCycle());
            entity.setCycleCountry(user.getCycleCountry());
            entity.setOrgAddr(user.getOrgAddr());
        }

        return entity;
    }
}
