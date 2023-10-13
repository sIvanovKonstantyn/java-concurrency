package org.example.nonblocking;

import java.util.HashMap;
import java.util.Map;

import org.example.nonblocking.UserRecord;

public class UserBalanceInMemoryDB {

    private final static UserBalanceInMemoryDB INSTANCE = new UserBalanceInMemoryDB();
    private final Map<String, UserRecord> usersRecords = new HashMap<>();

    UserBalanceInMemoryDB() {
        usersRecords.put("user1", new UserRecord());
    }
    public static UserBalanceInMemoryDB getInstance() {
        return INSTANCE;
    }

    public void updateUserBalance(String userId, int amount) {
        usersRecords.get(userId).updateBalance(amount);
    }

    public int getUserBalance(String userId) {
        return usersRecords.get(userId).getBalance();
    }
}
