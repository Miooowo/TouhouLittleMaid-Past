package com.github.tartaricacid.touhoulittlemaid.entity.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;

public final class TaskManager {

    private static final Map<String, MaidTask> TASK_MAP = new LinkedHashMap<String, MaidTask>();
    private static List<MaidTask> TASK_INDEX = Collections.emptyList();

    private TaskManager() {}

    public static void init() {
        TASK_MAP.clear();
        for (MaidTask task : MaidTask.values()) {
            TASK_MAP.put(task.namespacedId(), task);
            TASK_MAP.put(task.id, task);
        }
        TASK_INDEX = Collections.unmodifiableList(new ArrayList<MaidTask>(TASK_MAP.values()));
        TouhouLittleMaid.LOGGER.info(
                "Registered {} maid tasks ({} implemented this phase)",
                MaidTask.values().length,
                countImplemented());
    }

    public static MaidTask findTask(String id) {
        MaidTask task = TASK_MAP.get(id);
        return task != null ? task : MaidTask.IDLE;
    }

    public static MaidTask getIdleTask() {
        return MaidTask.IDLE;
    }

    public static List<MaidTask> getTaskIndex() {
        return TASK_INDEX;
    }

    private static int countImplemented() {
        int count = 0;
        for (MaidTask task : MaidTask.values()) {
            if (task.implemented) {
                count++;
            }
        }
        return count;
    }
}
