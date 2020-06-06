package com.chetiwen.cache;

import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.UserAccessor;
import com.chetiwen.db.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCache {
    private Logger logger = LoggerFactory.getLogger(UserCache.class);

    private static UserCache instance;

    private Map<String, User> idMap;
    private Map<String, User> nameMap;

    private UserCache() throws DBAccessException {
        idMap = new HashMap<>();
        nameMap = new HashMap<>();
        reload();
    }

    public static synchronized UserCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new UserCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        idMap.clear();
        nameMap.clear();

        List<User> list = UserAccessor.getInstance().getAllUsers();

        list.forEach(user->{
            idMap.put(user.getPartnerId(), user);
            nameMap.put(user.getUserName(), user);
        });

        logger.info("UserCache reloaded. {} record(s) are refreshed.", idMap.size());
    }

    public User getByKey(String key) {
        return idMap.get(key);
    }

    public User getByName(String userName) {
        return nameMap.get(userName);
    }

    public Map<String, User> getUserMap() {
        return idMap;
    }

    public void refreshCache() throws DBAccessException {
        if (idMap.size() == 0) {
            reload();
        }
    }


    public void addUser(User user) throws DBAccessException {
        logger.info("add into user cache: {}", user);

        UserAccessor.getInstance().addUser(user);
        idMap.put(user.getPartnerId(), user);
        nameMap.put(user.getUserName(), user);
    }

    public void updateUser(User user) throws DBAccessException {
        logger.info("update user cache for user: {}/{}", user.getUserName(), user);

        UserAccessor.getInstance().updateUser(user);
        idMap.put(user.getPartnerId(), user);
        nameMap.put(user.getUserName(), user);
    }

}
