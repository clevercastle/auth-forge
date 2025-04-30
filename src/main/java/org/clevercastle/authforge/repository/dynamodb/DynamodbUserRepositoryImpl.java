package org.clevercastle.authforge.repository.dynamodb;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.entity.User;
import org.clevercastle.authforge.entity.UserLoginItem;
import org.clevercastle.authforge.entity.UserRefreshTokenMapping;
import org.clevercastle.authforge.exception.CastleException;
import org.clevercastle.authforge.repository.UserRepository;
import org.clevercastle.authforge.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamodbUserRepositoryImpl implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(DynamodbUserRepositoryImpl.class);
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbClient lowClient;


    private volatile DynamoDbTable<DynamodbUser> table;
    private volatile DynamoDbTable<UserLoginItem> userLoginItemTable;

    public DynamodbUserRepositoryImpl(DynamoDbEnhancedClient enhancedClient, DynamoDbClient lowClient) {
        this.enhancedClient = enhancedClient;
        this.lowClient = lowClient;
    }


    @Override
    public void save(User user, UserLoginItem userLoginItem) throws CastleException {
        TransactWriteItem userWrite = TransactWriteItem.builder()
                .put(Put.builder()
                        .tableName(DynamodbUser.TABLE_NAME)
                        .item(getTable().tableSchema().itemToMap(DynamodbUserUtil.fromUser(user), true))
                        .build())
                .build();
        TransactWriteItem userLoginItemWrite = TransactWriteItem.builder()
                .put(Put.builder()
                        .tableName(DynamodbUser.TABLE_NAME)
                        .item(getTable().tableSchema().itemToMap(DynamodbUserUtil.fromUserLogItem(userLoginItem), true))
                        .build())
                .build();

        TransactWriteItemsRequest transactWriteItemsRequest = TransactWriteItemsRequest.builder()
                .transactItems(Arrays.asList(userWrite, userLoginItemWrite))
                .build();
        try {
            lowClient.transactWriteItems(transactWriteItemsRequest);
        } catch (DynamoDbException e) {
            logger.error("Fail to save user", e);
            throw new CastleException();
        }
    }

    @Nonnull
    @Override
    public Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier) {
        DynamodbUser dynamodbUser = getTable().getItem(Key.builder().partitionValue(loginIdentifier).sortValue(DynamodbUser.Type.loginItem.name()).build());
        if (dynamodbUser == null) {
            return Pair.of(null, null);
        }
        UserLoginItem userLoginItem = DynamodbUserUtil.toUserLogItem(dynamodbUser);
        dynamodbUser = getTable().getItem(Key.builder().partitionValue(userLoginItem.getUserId()).sortValue(DynamodbUser.Type.user.name()).build());
        if (dynamodbUser == null) {
            logger.error("");
            return Pair.of(null, null);
        }
        return Pair.of(DynamodbUserUtil.toUser(dynamodbUser), userLoginItem);
    }

    @Nonnull
    @Override
    public Pair<User, UserLoginItem> getByUserSub(String userSub) {
        List<DynamodbUser> dynamodbUsers = getTable().index(DynamodbUser.UserLoginItem_UserSub_index)
                .query(QueryConditional.keyEqualTo(Key.builder().partitionValue(userSub).build()))
                .stream().flatMap(it -> it.items().stream()).collect(Collectors.toList());
        if (dynamodbUsers.isEmpty()) {
            return Pair.of(null, null);
        }
        if (dynamodbUsers.size() > 1) {
            logger.error("More than one user found for user sub {}", userSub);

        }
        DynamodbUser dynamodbUser = dynamodbUsers.get(0);
        UserLoginItem userLoginItem = DynamodbUserUtil.toUserLogItem(dynamodbUser);
        dynamodbUser = getTable().getItem(Key.builder().partitionValue(userLoginItem.getUserId()).sortValue(DynamodbUser.Type.user.name()).build());
        if (dynamodbUser == null) {
            logger.error("");
            return Pair.of(null, null);
        }
        return Pair.of(DynamodbUserUtil.toUser(dynamodbUser), userLoginItem);
    }

    @Override
    public void confirmLoginItem(String loginIdentifier) {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(DynamodbUser.TABLE_NAME)
                .key(Map.of("pk", AttributeValue.fromS(loginIdentifier), "sk", AttributeValue.fromS(DynamodbUser.Type.loginItem.name())))
                .updateExpression("Set userLoginItemState = :state Remove userLoginItemVerificationCode, userLoginItemVerificationCodeExpiredAt")
                .expressionAttributeValues(Map.of(":state", AttributeValue.fromS(UserLoginItem.State.ACTIVE.name())))
                .build();
        this.lowClient.updateItem(updateItemRequest);
    }

    @Override
    public UserRefreshTokenMapping addRefreshToken(User user, String refreshToken, OffsetDateTime expiredAt) {
        UserRefreshTokenMapping mapping = new UserRefreshTokenMapping();
        mapping.setUserId(user.getUserId());
        mapping.setRefreshToken(DynamodbUser.Type.refreshToken + "#" + refreshToken);
        OffsetDateTime now = OffsetDateTime.now();
        mapping.setCreatedAt(now);
        mapping.setExpiredAt(expiredAt);
        getTable().putItem(DynamodbUserUtil.fromUserRefreshTokenMapping(mapping));
        return mapping;
    }

    @Override
    public boolean verifyRefreshToken(User user, String refreshToken) throws CastleException {
        if (StringUtils.isBlank(refreshToken)) {
            return false;
        }
        Key key = Key.builder().partitionValue(user.getUserId()).sortValue(DynamodbUser.Type.refreshToken + "#" + refreshToken).build();
        DynamodbUser dynamodbUser = getTable()
                .getItem(key);
        if (dynamodbUser == null) {
            return false;
        }
        UserRefreshTokenMapping userRefreshTokenMapping = DynamodbUserUtil.toUserRefreshTokenMapping(dynamodbUser);
        if (userRefreshTokenMapping.getExpiredAt() != null && userRefreshTokenMapping.getExpiredAt().isAfter(TimeUtils.now())) {
            getTable().deleteItem(key);
            return true;
        }
        return false;
    }

    private DynamoDbTable<DynamodbUser> getTable() {
        if (table == null) {
            synchronized (this) {
                if (table == null) {
                    TableSchema<DynamodbUser> schema = TableSchema.fromBean(DynamodbUser.class);
                    table = enhancedClient.table(DynamodbUser.TABLE_NAME, schema);
                }
            }
        }
        return table;
    }
}
