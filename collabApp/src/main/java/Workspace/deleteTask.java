package Workspace;

import db.Delete;


public class deleteTask {
    public static boolean deleteT(int taskId, int boardId) {
        return Delete.deleteTask(taskId, boardId);
    }
}





