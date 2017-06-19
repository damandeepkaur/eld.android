package com.bsmwireless.data.storage.users;

import com.bsmwireless.models.User;

public class UserConverter {
    public static User toUser(UserEntity entity) {
        User user = null;

        if (entity != null) {
            user = new User();
            user.setId(entity.getId());
            user.setFirstName(entity.getFirstName());
            user.setLastName(entity.getLastName());
        }

        return user;
    }

    public static UserEntity toEntity(String accountName, User user) {
        UserEntity entity = null;

        if (user != null) {
            entity = new UserEntity();
            entity.setAccountName(accountName);
            entity.setId(user.getId());
            entity.setFirstName(user.getFirstName());
            entity.setLastName(user.getLastName());
        }

        return entity;
    }
}
