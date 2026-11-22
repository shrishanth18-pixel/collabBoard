package Workspace;

import db.Delete;

public class deleteProject {
    public static void deleteP(int boardId) {
        Delete.deleteBoard(boardId);
    }
}







