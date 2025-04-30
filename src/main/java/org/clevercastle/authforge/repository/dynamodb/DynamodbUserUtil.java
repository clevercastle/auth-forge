package org.clevercastle.authforge.repository.dynamodb;

import org.clevercastle.authforge.entity.User;
import org.clevercastle.authforge.entity.UserLoginItem;
import org.clevercastle.authforge.entity.UserRefreshTokenMapping;

public class DynamodbUserUtil {

    public static DynamodbUser fromUser(User user) {
        DynamodbUser dynamodbUser = new DynamodbUser();
        dynamodbUser.setPk(user.getUserId());
        dynamodbUser.setSk(DynamodbUser.Type.user.name());

        dynamodbUser.setUserState(user.getUserState());
        dynamodbUser.setUserHashedPassword(user.getHashedPassword());
        dynamodbUser.setUserResetPasswordCode(user.getResetPasswordCode());
        dynamodbUser.setUserResetPasswordCodeExpiredAt(user.getResetPasswordCodeExpiredAt());

        dynamodbUser.setCreatedAt(user.getCreatedAt());
        dynamodbUser.setUpdatedAt(user.getUpdatedAt());
        return dynamodbUser;
    }

    public static DynamodbUser fromUserLogItem(UserLoginItem userLoginItem) {
        DynamodbUser dynamodbUser = new DynamodbUser();
        dynamodbUser.setPk(userLoginItem.getLoginIdentifier());
        dynamodbUser.setSk(DynamodbUser.Type.loginItem.name());

        dynamodbUser.setUserLoginItemLoginIdentifierPrefix(userLoginItem.getLoginIdentifierPrefix());
        dynamodbUser.setUserLoginItemType(userLoginItem.getType());
        dynamodbUser.setUserLoginItemUserSub(userLoginItem.getUserSub());
        dynamodbUser.setUserLoginItemUserId(userLoginItem.getUserId());
        dynamodbUser.setUserLoginItemState(userLoginItem.getState());
        dynamodbUser.setUserLoginItemVerificationCode(userLoginItem.getVerificationCode());
        dynamodbUser.setUserLoginItemVerificationCodeExpiredAt(userLoginItem.getVerificationCodeExpiredAt());

        dynamodbUser.setCreatedAt(userLoginItem.getCreatedAt());
        dynamodbUser.setUpdatedAt(userLoginItem.getUpdatedAt());
        return dynamodbUser;
    }

    public static DynamodbUser fromUserRefreshTokenMapping(UserRefreshTokenMapping userRefreshTokenMapping) {
        DynamodbUser dynamodbUser = new DynamodbUser();
        dynamodbUser.setPk(userRefreshTokenMapping.getUserId());
        dynamodbUser.setSk(userRefreshTokenMapping.getRefreshToken());
        dynamodbUser.setUserRefreshTokenExpiredAt(userRefreshTokenMapping.getExpiredAt());

        dynamodbUser.setCreatedAt(userRefreshTokenMapping.getCreatedAt());
        return dynamodbUser;
    }

    public static User toUser(DynamodbUser dynamodbUser) {
        User user = new User();
        user.setUserId(dynamodbUser.getPk());
        user.setUserState(dynamodbUser.getUserState());
        user.setHashedPassword(dynamodbUser.getUserHashedPassword());
        user.setResetPasswordCode(dynamodbUser.getUserResetPasswordCode());
        user.setResetPasswordCodeExpiredAt(dynamodbUser.getUserResetPasswordCodeExpiredAt());
        user.setCreatedAt(dynamodbUser.getCreatedAt());
        user.setUpdatedAt(dynamodbUser.getUpdatedAt());
        return user;
    }

    public static UserLoginItem toUserLogItem(DynamodbUser dynamodbUser) {
        UserLoginItem userLoginItem = new UserLoginItem();
        userLoginItem.setLoginIdentifier(dynamodbUser.getPk());
        userLoginItem.setLoginIdentifierPrefix(dynamodbUser.getUserLoginItemLoginIdentifierPrefix());
        userLoginItem.setType(dynamodbUser.getUserLoginItemType());
        userLoginItem.setUserSub(dynamodbUser.getUserLoginItemUserSub());
        userLoginItem.setUserId(dynamodbUser.getUserLoginItemUserId());
        userLoginItem.setState(dynamodbUser.getUserLoginItemState());
        userLoginItem.setVerificationCode(dynamodbUser.getUserLoginItemVerificationCode());
        userLoginItem.setVerificationCodeExpiredAt(dynamodbUser.getUserLoginItemVerificationCodeExpiredAt());
        userLoginItem.setCreatedAt(dynamodbUser.getCreatedAt());
        userLoginItem.setUpdatedAt(dynamodbUser.getUpdatedAt());
        return userLoginItem;
    }

    public static UserRefreshTokenMapping toUserRefreshTokenMapping(DynamodbUser dynamodbUser) {
        UserRefreshTokenMapping userRefreshTokenMapping = new UserRefreshTokenMapping();
        userRefreshTokenMapping.setUserId(dynamodbUser.getPk());
        userRefreshTokenMapping.setRefreshToken(dynamodbUser.getSk());
        userRefreshTokenMapping.setExpiredAt(dynamodbUser.getUserRefreshTokenExpiredAt());

        userRefreshTokenMapping.setCreatedAt(dynamodbUser.getCreatedAt());
        return userRefreshTokenMapping;
    }
}
