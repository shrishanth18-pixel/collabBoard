package desktop;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import db.DB;
import db.Delete;
import db.GetBoard;
import db.Update;
import db.enums;
import db.getUsers;
import model.BoardEntity;
import service.BoardService;

public class CollabBoardSwingApp extends JFrame {
    private final AnnotationConfigApplicationContext springContext;
    private final BoardService boardService;

    private Integer userId;
    private String username;

    private final DefaultTableModel boardModel = new DefaultTableModel(
            new Object[]{"ID", "Board", "Status", "Priority", "Role"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable boardTable = new JTable(boardModel);

    private final DefaultTableModel taskModel = new DefaultTableModel(
            new Object[]{"ID", "Task", "Status", "Priority", "Deadline", "Assigned"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable taskTable = new JTable(taskModel);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CollabBoardSwingApp app = new CollabBoardSwingApp();
            app.showLogin();
        });
    }

    public CollabBoardSwingApp() {
        super("CollabBoard Desktop");
        springContext = new AnnotationConfigApplicationContext(config.AppConfig.class);
        boardService = springContext.getBean(BoardService.class);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
    }

    private void showLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField email = new JTextField(24);
        JPasswordField password = new JPasswordField(24);
        JButton login = new JButton("Login");
        JButton register = new JButton("Register");

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        panel.add(new JLabel("CollabBoard Desktop", SwingConstants.CENTER), c);
        c.gridwidth = 1; c.gridy = 1; panel.add(new JLabel("Email"), c);
        c.gridx = 1; panel.add(email, c);
        c.gridx = 0; c.gridy = 2; panel.add(new JLabel("Password"), c);
        c.gridx = 1; panel.add(password, c);
        c.gridx = 0; c.gridy = 3; panel.add(login, c);
        c.gridx = 1; panel.add(register, c);

        login.addActionListener(e -> {
            String mail = email.getText().trim().toLowerCase();
            String pass = new String(password.getPassword());
            if (!mail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") || pass.length() < 8) {
                JOptionPane.showMessageDialog(this, "Enter a valid email and password of at least 8 characters.");
                return;
            }
            String hash = getUsers.verifyPassword(mail, null);
            HashMap<String, String> data = getUsers.selectFromUsers(mail, null, null);
            if (hash == null || !BCrypt.checkpw(pass, hash) || data.get("user_id") == null) {
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            userId = Integer.valueOf(data.get("user_id"));
            username = data.get("username");
            showMain();
        });

        register.addActionListener(e -> showRegister());

        setContentPane(panel);
        setVisible(true);
    }

    private void showRegister() {
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JPasswordField password = new JPasswordField();
        JPasswordField confirm = new JPasswordField();
        Object[] fields = {"Full name:", name, "Email:", email, "Password:", password, "Confirm password:", confirm};
        if (JOptionPane.showConfirmDialog(this, fields, "Register", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String fullName = name.getText().trim();
        String mail = email.getText().trim().toLowerCase();
        String pass = new String(password.getPassword());
        String repeated = new String(confirm.getPassword());
        if (fullName.length() < 2 || fullName.length() > 50) {
            JOptionPane.showMessageDialog(this, "Full name must contain 2 to 50 characters.");
            return;
        }
        if (!mail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address.");
            return;
        }
        if (pass.length() < 8 || !pass.equals(repeated)) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters and passwords must match.");
            return;
        }
        if (getUsers.checkEmail(mail, null) != null) {
            JOptionPane.showMessageDialog(this, "An account already exists with this email.");
            return;
        }
        String hash = BCrypt.hashpw(pass, BCrypt.gensalt(12));
        if (DB.insertUser(fullName, mail, hash, null)) {
            JOptionPane.showMessageDialog(this, "Registration successful. Please log in.");
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed.", "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMain() {
        JTabbedPane tabs = new JTabbedPane();
        JPanel boards = new JPanel(new BorderLayout());
        JPanel boardActions = new JPanel();
        JButton create = new JButton("Create Board");
        JButton update = new JButton("Update Board");
        JButton delete = new JButton("Delete Board");
        JButton refresh = new JButton("Refresh");
        JButton logout = new JButton("Logout");

        boardActions.add(create); boardActions.add(update); boardActions.add(delete); boardActions.add(refresh); boardActions.add(logout);
        boards.add(boardActions, BorderLayout.NORTH);
        boards.add(new JScrollPane(boardTable), BorderLayout.CENTER);

        JPanel tasks = new JPanel(new BorderLayout());
        JPanel taskActions = new JPanel();
        JButton createTask = new JButton("Create Task");
        JButton updateTask = new JButton("Update Task");
        JButton deleteTask = new JButton("Delete Task");
        taskActions.add(createTask); taskActions.add(updateTask); taskActions.add(deleteTask);
        tasks.add(taskActions, BorderLayout.NORTH);
        tasks.add(new JScrollPane(taskTable), BorderLayout.CENTER);

        tabs.addTab("Boards", boards);
        tabs.addTab("Tasks", tasks);

        boardTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refreshTasks();
        });

        create.addActionListener(e -> createBoard());
        refresh.addActionListener(e -> refreshBoards());
        update.addActionListener(e -> updateBoard());
        delete.addActionListener(e -> deleteBoard());
        logout.addActionListener(e -> {
            userId = null; username = null;
            showLogin();
        });
        createTask.addActionListener(e -> createTask());
        updateTask.addActionListener(e -> updateTask());
        deleteTask.addActionListener(e -> deleteTask());

        setTitle("CollabBoard Desktop - " + username);
        setContentPane(tabs);
        revalidate();
        refreshBoards();
    }

    private void refreshBoards() {
        boardModel.setRowCount(0);
        try {
            List<BoardEntity> boards = boardService.getBoardsForUser(userId);
            for (BoardEntity board : boards) {
                boardModel.addRow(new Object[]{board.getId(), board.getProjectName(), board.getStatus(), board.getPriority(), "MEMBER"});
            }
        } catch (Exception ex) {
            for (Map<String, Object> board : GetBoard.getBoardsForUser(userId)) {
                boardModel.addRow(new Object[]{board.get("id"), board.get("projectname"), board.get("status"), board.get("priority"), board.get("role")});
            }
        }
    }

    private int selectedBoardId() {
        int row = boardTable.getSelectedRow();
        if (row < 0) return -1;
        Object value = boardModel.getValueAt(row, 0);
        return value == null ? -1 : Integer.parseInt(value.toString());
    }

    private void createBoard() {
        JTextField name = new JTextField();
        JTextArea description = new JTextArea(4, 30);
        JComboBox<String> status = new JComboBox<>(new String[]{"Planning", "Active", "OnHold", "Completed"});
        JComboBox<String> priority = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        JSpinner months = new JSpinner(new SpinnerNumberModel(1, 1, 36, 1));
        Object[] fields = {"Name:", name, "Description:", new JScrollPane(description), "Status:", status, "Priority:", priority, "Deadline months:", months};
        if (JOptionPane.showConfirmDialog(this, fields, "Create Board", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (name.getText().trim().length() < 2 || description.getText().trim().length() < 3) {
            JOptionPane.showMessageDialog(this, "Enter a valid name and description.");
            return;
        }
        enums.status projectStatus = enums.status.valueOf(status.getSelectedItem().toString());
        enums.priority projectPriority = enums.priority.valueOf(priority.getSelectedItem().toString());
        if (DB.createBoardForUser(name.getText().trim(), projectStatus, projectPriority,
                (Integer) months.getValue(), description.getText().trim(), userId)) {
            JOptionPane.showMessageDialog(this, "Board created successfully.");
            refreshBoards();
        } else {
            JOptionPane.showMessageDialog(this, "Board could not be created.", "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBoard() {
        int boardId = selectedBoardId();
        if (boardId < 0) { JOptionPane.showMessageDialog(this, "Select a board first."); return; }
        String role = getUsers.getUserRole(userId, boardId);
        if (!"OWNER".equals(role)) { JOptionPane.showMessageDialog(this, "Only the OWNER can update this board."); return; }
        JTextField name = new JTextField(boardModel.getValueAt(boardTable.getSelectedRow(), 1).toString());
        JComboBox<String> status = new JComboBox<>(new String[]{"Planning", "Active", "OnHold", "Completed"});
        JComboBox<String> priority = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        Object[] fields = {"Name:", name, "Status:", status, "Priority:", priority};
        if (JOptionPane.showConfirmDialog(this, fields, "Update Board", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (name.getText().trim().length() < 2) { JOptionPane.showMessageDialog(this, "Board name is too short."); return; }
        if (Update.updateBoard(boardId, name.getText().trim(), status.getSelectedItem().toString(), priority.getSelectedItem().toString())) {
            JOptionPane.showMessageDialog(this, "Board updated."); refreshBoards();
        } else JOptionPane.showMessageDialog(this, "Update failed.", "Database error", JOptionPane.ERROR_MESSAGE);
    }

    private void deleteBoard() {
        int boardId = selectedBoardId();
        if (boardId < 0) { JOptionPane.showMessageDialog(this, "Select a board first."); return; }
        if (!"OWNER".equals(getUsers.getUserRole(userId, boardId))) { JOptionPane.showMessageDialog(this, "Only the OWNER can delete this board."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete the selected board?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Delete.deleteBoard(boardId); refreshBoards(); taskModel.setRowCount(0);
        }
    }

    private void refreshTasks() {
        taskModel.setRowCount(0);
        int boardId = selectedBoardId();
        if (boardId < 0) return;
        for (Map<String, Object> task : GetBoard.getTasksForBoard(boardId)) {
            taskModel.addRow(new Object[]{task.get("taskid"), task.get("taskname"), task.get("taskstatus"), task.get("priority"), task.get("deadline"), task.get("assigned_to")});
        }
    }

    private void createTask() {
        int boardId = selectedBoardId();
        if (boardId < 0) { JOptionPane.showMessageDialog(this, "Select a board first."); return; }
        String role = getUsers.getUserRole(userId, boardId);
        if (role == null || "VIEWER".equals(role)) { JOptionPane.showMessageDialog(this, "You cannot create tasks on this board."); return; }
        JTextField name = new JTextField();
        JTextArea description = new JTextArea(4, 30);
        JComboBox<String> status = new JComboBox<>(new String[]{"Todo", "In Progress", "Done"});
        JComboBox<String> priority = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        JSpinner deadline = new JSpinner(new SpinnerNumberModel(7, 1, 365, 1));
        java.util.Map<String, Object> self = new java.util.HashMap<>();
        self.put("username", username);
        java.util.List<java.util.Map<String, Object>> connections = getUsers.getConnections(userId);
        connections.add(0, self);
        JComboBox<String> assignee = new JComboBox<>(connections.stream().map(c -> c.get("username").toString()).toArray(String[]::new));
        Object[] fields = {"Task name:", name, "Description:", new JScrollPane(description), "Status:", status, "Priority:", priority, "Deadline days:", deadline, "Tagged connection:", assignee};
        if (JOptionPane.showConfirmDialog(this, fields, "Create Task", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (name.getText().trim().isEmpty() || assignee.getSelectedItem() == null) { JOptionPane.showMessageDialog(this, "Task name and tagged connection are required."); return; }
        String assigneeName = assignee.getSelectedItem().toString();
        int assigneeId = getUsers.getUserIdByUsername(assigneeName);
        if (assigneeId == -1 || !getUsers.areConnected(userId, assigneeId)) { JOptionPane.showMessageDialog(this, "Select an accepted connection."); return; }
        enums.task_status taskStatus;
        try { taskStatus = enums.task_status.valueOf(status.getSelectedItem().toString().replace(" ", "")); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid task status."); return; }
        enums.priority taskPriority = enums.priority.valueOf(priority.getSelectedItem().toString());
        if (DB.insertToTask(name.getText().trim(), description.getText().trim(), taskStatus, (Integer) deadline.getValue(), taskPriority, boardId, assigneeName)) {
            JOptionPane.showMessageDialog(this, "Task created."); refreshTasks();
        } else JOptionPane.showMessageDialog(this, "Task creation failed.", "Database error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateTask() {
        int taskRow = taskTable.getSelectedRow();
        int boardId = selectedBoardId();
        if (taskRow < 0 || boardId < 0) { JOptionPane.showMessageDialog(this, "Select a task and board first."); return; }
        if ("VIEWER".equals(getUsers.getUserRole(userId, boardId))) { JOptionPane.showMessageDialog(this, "VIEWER users cannot update tasks."); return; }
        int taskId = Integer.parseInt(taskModel.getValueAt(taskRow, 0).toString());
        java.util.Map<String, Object> task = GetBoard.getTaskById(boardId, taskId);
        if (task == null) { JOptionPane.showMessageDialog(this, "Task not found on this board."); return; }
        JTextField name = new JTextField(task.get("taskname").toString());
        JTextArea description = new JTextArea(task.get("description") == null ? "" : task.get("description").toString(), 4, 30);
        JComboBox<String> status = new JComboBox<>(new String[]{"Todo", "In Progress", "Done"});
        JComboBox<String> priority = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        JSpinner deadline = new JSpinner(new SpinnerNumberModel(7, 1, 365, 1));
        java.util.Map<String, Object> self = new java.util.HashMap<>();
        self.put("username", username);
        java.util.List<java.util.Map<String, Object>> connections = getUsers.getConnections(userId);
        connections.add(0, self);
        JComboBox<String> assignee = new JComboBox<>(connections.stream().map(c -> c.get("username").toString()).toArray(String[]::new));
        Object[] fields = {"Task name:", name, "Description:", new JScrollPane(description), "Status:", status, "Priority:", priority, "Deadline days:", deadline, "Assigned:", assignee};
        if (JOptionPane.showConfirmDialog(this, fields, "Update Task", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (name.getText().trim().isEmpty() || description.getText().trim().length() < 3 || assignee.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Task title, description, and assignee are required."); return;
        }
        String assigneeName = assignee.getSelectedItem().toString();
        int assigneeId = getUsers.getUserIdByUsername(assigneeName);
        if (assigneeId == -1 || (assigneeId != userId && !getUsers.areConnected(userId, assigneeId))) {
            JOptionPane.showMessageDialog(this, "Select yourself or an accepted connection."); return;
        }
        String normalizedStatus = status.getSelectedItem().toString().replace(" ", "");
        if (Update.updateTask(boardId, taskId, name.getText().trim(), description.getText().trim(), normalizedStatus,
                (Integer) deadline.getValue(), priority.getSelectedItem().toString(), assigneeName)) {
            JOptionPane.showMessageDialog(this, "Task updated."); refreshTasks();
        } else JOptionPane.showMessageDialog(this, "Task update failed.", "Database error", JOptionPane.ERROR_MESSAGE);
    }

    private void deleteTask() {
        int taskRow = taskTable.getSelectedRow();
        int boardId = selectedBoardId();
        if (taskRow < 0 || boardId < 0) { JOptionPane.showMessageDialog(this, "Select a task and board first."); return; }
        if ("VIEWER".equals(getUsers.getUserRole(userId, boardId))) { JOptionPane.showMessageDialog(this, "VIEWER users cannot delete tasks."); return; }
        int taskId = Integer.parseInt(taskModel.getValueAt(taskRow, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "Delete the selected task?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Delete.deleteTask(taskId, boardId); refreshTasks();
        }
    }
}
