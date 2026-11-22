package db;

public class enums {
    public enum priority {
        Low,
        Medium,
        High
    }

    public enum status {
        Planning,
        Active,
        OnHold,
        Completed
    }

    public enum task_status {
        Todo,
        InProgress,
        Done
    }

    public enum Role {
        OWNER,
        EDITOR,
        VIEWER
    }

    public static String projectStatusLabel(status value) {
        if (value == status.OnHold) return "On Hold";
        return value.name();
    }

    public static String taskStatusLabel(task_status value) {
        if (value == task_status.InProgress) return "In Progress";
        return value.name();
    }
}
