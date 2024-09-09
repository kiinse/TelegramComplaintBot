package kiinse.dev.telegram.manager;

import kiinse.dev.telegram.enums.UserPermission;
import kiinse.dev.telegram.mongodb.queries.UserQuery;
import lombok.NonNull;

public class UsersManager {

    private final UserQuery userQuery;

    public UsersManager(@NonNull UserQuery userQuery) {
        this.userQuery = userQuery;
    }

    public void createUser(long chatId) {
        userQuery.createUser(chatId);
    }

    public void setUserPermission(long chatId, UserPermission permission) {
        userQuery.setUserPermission(chatId, permission);
    }

    public boolean isDistrictSelected(Long chatId) {
        return userQuery.isUserDistrictSelected(chatId);
    }

    public void setUserDistrict(@NonNull Long chatId, @NonNull String district) {
        removeDistrict(chatId);
        userQuery.setUserDistrict(chatId, district);
    }

    public void removeDistrict(@NonNull Long chatId) {
        userQuery.removeUserDistrict(chatId);
    }

    public @NonNull String getDistrict(Long chatId) {
        return userQuery.getUserDistrict(chatId);
    }

    public UserPermission getUserPermissions(long chatId) {
        return userQuery.getUserPermission(chatId);
    }


}
