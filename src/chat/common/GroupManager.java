package common;

import java.util.concurrent.ConcurrentHashMap;

public class GroupManager {
    private final ConcurrentHashMap<String, Group> groupList;

    public GroupManager() {
        groupList = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, Group> getGroupList() {
        return groupList;
    }

    public void addGroup(Group group) {
        String groupId = group.getGroupId();
        groupList.putIfAbsent(groupId, group);
    }

    private void removeGroup(String groupId) {
        groupList.remove(groupId);
    }

    public void removeUserFromAllGroups(String userName) {

        for (Group group : groupList.values()) {
            group.removeUser(userName);

            if (group.getSize() != 0) continue;;

            removeGroup(group.getGroupId());
        }
    }

    public boolean isGroupExists(String groupId) {
        return groupList.containsKey(groupId);
    }

    public Group getGroup(String groupId) {
        return groupList.get(groupId);
    }
}
