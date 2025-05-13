package org.clevercastle.authforge.repository.dynamodb;

import org.clevercastle.authforge.UserState;
import org.clevercastle.authforge.model.UserLoginItem;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.OffsetDateTime;

// single table design
@DynamoDbBean
public class DynamodbUser {
    public enum Type {
        user,
        loginItem,
        refreshToken
    }

    public static final String TABLE_NAME = "auth-forge";

    public static final String UserLoginItem_UserSub_index = "UserLoginItem_UserSub_index";


    // for user: pk = userId, sk = "user"
    // for user login item: pk = loginIdentifier, sk = "loginItem"
    // for user refresh token: pk = userId, sk="refreshToken#"+ refreshToken
    private String pk;
    private String sk;

    private Type type;

    // region from user table
    private UserState userState;
    private String userHashedPassword;
    private String userResetPasswordCode;
    private OffsetDateTime userResetPasswordCodeExpiredAt;
    // endregion

    // region from user login item table
    private String userLoginItemLoginIdentifierPrefix;
    private UserLoginItem.Type userLoginItemType;
    private String userLoginItemUserSub;
    private String userLoginItemUserId;
    private UserLoginItem.State userLoginItemState;
    private String userLoginItemVerificationCode;
    private OffsetDateTime userLoginItemVerificationCodeExpiredAt;
    // endregion

    private OffsetDateTime userRefreshTokenExpiredAt;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @DynamoDbPartitionKey
    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @DynamoDbSortKey
    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public String getUserHashedPassword() {
        return userHashedPassword;
    }

    public void setUserHashedPassword(String userHashedPassword) {
        this.userHashedPassword = userHashedPassword;
    }

    public String getUserResetPasswordCode() {
        return userResetPasswordCode;
    }

    public void setUserResetPasswordCode(String userResetPasswordCode) {
        this.userResetPasswordCode = userResetPasswordCode;
    }

    public OffsetDateTime getUserResetPasswordCodeExpiredAt() {
        return userResetPasswordCodeExpiredAt;
    }

    public void setUserResetPasswordCodeExpiredAt(OffsetDateTime userResetPasswordCodeExpiredAt) {
        this.userResetPasswordCodeExpiredAt = userResetPasswordCodeExpiredAt;
    }

    public String getUserLoginItemLoginIdentifierPrefix() {
        return userLoginItemLoginIdentifierPrefix;
    }

    public void setUserLoginItemLoginIdentifierPrefix(String userLoginItemLoginIdentifierPrefix) {
        this.userLoginItemLoginIdentifierPrefix = userLoginItemLoginIdentifierPrefix;
    }

    public UserLoginItem.Type getUserLoginItemType() {
        return userLoginItemType;
    }

    public void setUserLoginItemType(UserLoginItem.Type userLoginItemType) {
        this.userLoginItemType = userLoginItemType;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = UserLoginItem_UserSub_index)
    public String getUserLoginItemUserSub() {
        return userLoginItemUserSub;
    }

    public void setUserLoginItemUserSub(String userLoginItemUserSub) {
        this.userLoginItemUserSub = userLoginItemUserSub;
    }

    public String getUserLoginItemUserId() {
        return userLoginItemUserId;
    }

    public void setUserLoginItemUserId(String userLoginItemUserId) {
        this.userLoginItemUserId = userLoginItemUserId;
    }

    public UserLoginItem.State getUserLoginItemState() {
        return userLoginItemState;
    }

    public void setUserLoginItemState(UserLoginItem.State userLoginItemState) {
        this.userLoginItemState = userLoginItemState;
    }

    public String getUserLoginItemVerificationCode() {
        return userLoginItemVerificationCode;
    }

    public void setUserLoginItemVerificationCode(String userLoginItemVerificationCode) {
        this.userLoginItemVerificationCode = userLoginItemVerificationCode;
    }

    public OffsetDateTime getUserLoginItemVerificationCodeExpiredAt() {
        return userLoginItemVerificationCodeExpiredAt;
    }

    public void setUserLoginItemVerificationCodeExpiredAt(OffsetDateTime userLoginItemVerificationCodeExpiredAt) {
        this.userLoginItemVerificationCodeExpiredAt = userLoginItemVerificationCodeExpiredAt;
    }

    public OffsetDateTime getUserRefreshTokenExpiredAt() {
        return userRefreshTokenExpiredAt;
    }

    public void setUserRefreshTokenExpiredAt(OffsetDateTime userRefreshTokenExpiredAt) {
        this.userRefreshTokenExpiredAt = userRefreshTokenExpiredAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
