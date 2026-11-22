<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Board Details - CollabBoard</title>
<style>
  :root {
    --bg: #f4f6f9;
    --card-bg: #ffffff;
    --border: #e3e7ed;
    --text-dark: #1f2733;
    --text-muted: #6b7684;
    --accent: #4a6cf7;
    --green: #2ecc71;
    --orange: #f39c12;
    --red: #e74c3c;
  }

  * { box-sizing: border-box; margin: 0; padding: 0; }

  body {
    font-family: "Segoe UI", Roboto, Arial, sans-serif;
    background: var(--bg);
    color: var(--text-dark);
    padding: 28px;
  }

  .container {
    max-width: 1000px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  /* ---------- Board Details ---------- */
  .board-header {
    background: var(--card-bg);
    border: 1px solid var(--border);
    border-radius: 14px;
    padding: 24px 28px;
    border-top: 5px solid var(--accent);
  }

  .board-header h1 {
    font-size: 22px;
    font-weight: 700;
    margin-bottom: 6px;
  }

  .board-header p {
    color: var(--text-muted);
    font-size: 14px;
    line-height: 1.5;
    margin-bottom: 16px;
  }

  .board-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 24px;
    font-size: 13px;
    color: var(--text-muted);
  }

  .board-meta strong {
    color: var(--text-dark);
    display: block;
    font-size: 14px;
  }

  .board-progress {
    margin-top: 18px;
  }

  .board-progress-bar-bg {
    height: 8px;
    background: #eef0f4;
    border-radius: 6px;
    overflow: hidden;
    margin-top: 6px;
  }

  .board-progress-bar {
    height: 100%;
    background: var(--accent);
    border-radius: 6px;
  }

  /* ---------- Layout Grid ---------- */
  .grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 24px;
    align-items: start;
  }

  .panel {
    background: var(--card-bg);
    border: 1px solid var(--border);
    border-radius: 14px;
    padding: 22px 24px;
  }

  .panel h2 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
  }

  /* ---------- Task List ---------- */
  .task-list {
    list-style: none;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .task-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 14px;
    border: 1px solid var(--border);
    border-radius: 10px;
    transition: box-shadow 0.15s ease;
  }

  .task-item:hover {
    box-shadow: 0 3px 10px rgba(0,0,0,0.06);
  }

  .task-checkbox {
    width: 18px;
    height: 18px;
    border-radius: 5px;
    accent-color: var(--accent);
    flex-shrink: 0;
  }

  .task-info {
    flex: 1;
    min-width: 0;
  }

  .task-name {
    font-size: 14px;
    font-weight: 500;
  }

  .task-name.done {
    text-decoration: line-through;
    color: var(--text-muted);
  }

  .task-sub {
    font-size: 12px;
    color: var(--text-muted);
    margin-top: 2px;
  }

  .task-tag {
    font-size: 11px;
    font-weight: 600;
    padding: 4px 10px;
    border-radius: 20px;
    white-space: nowrap;
  }

  .tag-todo { background: #eef1ff; color: var(--accent); }
  .tag-progress { background: #fff6e6; color: var(--orange); }
  .tag-done { background: #e9fbf1; color: var(--green); }
  .tag-blocked { background: #fdecec; color: var(--red); }

  /* ---------- Members List ---------- */
  .member-list {
    list-style: none;
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .member-item {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .avatar {
    width: 38px;
    height: 38px;
    border-radius: 50%;
    background: var(--accent);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    font-size: 13px;
    flex-shrink: 0;
  }

  .member-info {
    flex: 1;
    min-width: 0;
  }

  .member-name {
    font-size: 14px;
    font-weight: 600;
  }

  .member-role {
    font-size: 12px;
    color: var(--text-muted);
  }

  .status-dot {
    width: 9px;
    height: 9px;
    border-radius: 50%;
    background: var(--green);
    flex-shrink: 0;
  }

  .status-dot.offline {
    background: #c7ccd4;
  }

  /* ---------- Responsive ---------- */
  @media (max-width: 800px) {
    .grid {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 500px) {
    body {
      padding: 16px;
    }

    .board-header, .panel {
      padding: 18px;
    }

    .board-meta {
      gap: 14px;
    }
  }
</style>
</head>
<body>

<div class="container">

  <!-- ================= Board Details ================= -->
  <div class="board-header">
    <h1><c:out value="${board.projectname}"/></h1>
    <p><c:out value="${board.description}"/></p>

    <div class="board-meta">
      <div>
        <span>Your role</span>
        <strong><c:out value="${boardRole}"/></strong>
      </div>
      <div>
        <span>Created</span>
        <strong><fmt:formatDate value="${board.created_at}" pattern="MMM dd, yyyy"/></strong>
      </div>
      <div>
        <span>Due Date</span>
        <strong><fmt:formatDate value="${board.deadline}" pattern="MMM dd, yyyy"/></strong>
      </div>
      <div>
        <span>Status</span>
        <strong><c:out value="${board.status}"/></strong>
      </div>
    </div>

    <div class="board-progress">
      <span style="font-size:13px; color:var(--text-muted);">Overall Progress &middot; 70%</span>
      <div class="board-progress-bar-bg">
        <div class="board-progress-bar" style="width:70%;"></div>
      </div>
    </div>

    <c:if test="${boardRole == 'OWNER'}">
      <form method="post" action="${pageContext.request.contextPath}/project/update">
        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
        <input type="hidden" name="boardId" value="${board.id}">
        <input name="projectName" value="${board.projectname}" required maxlength="50">
        <select name="status">
          <option value="Planning" <c:if test="${board.status == 'Planning'}">selected</c:if>>Planning</option>
          <option value="Active" <c:if test="${board.status == 'Active'}">selected</c:if>>Active</option>
          <option value="OnHold" <c:if test="${board.status == 'On Hold'}">selected</c:if>>On Hold</option>
          <option value="Completed" <c:if test="${board.status == 'Completed'}">selected</c:if>>Completed</option>
        </select>
        <select name="priority">
          <option value="Low" <c:if test="${board.priority == 'Low'}">selected</c:if>>Low</option>
          <option value="Medium" <c:if test="${board.priority == 'Medium'}">selected</c:if>>Medium</option>
          <option value="High" <c:if test="${board.priority == 'High'}">selected</c:if>>High</option>
        </select>
        <button type="submit">Update Board</button>
      </form>
      <form method="post" action="${pageContext.request.contextPath}/project/delete" onsubmit="return confirm('Delete this board permanently?')">
        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
        <input type="hidden" name="boardId" value="${board.id}">
        <button type="submit">Delete Board</button>
      </form>
      <form method="post" action="${pageContext.request.contextPath}/invite">
        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
        <input type="hidden" name="action" value="send">
        <input type="hidden" name="boardId" value="${board.id}">
        <input name="username" placeholder="Invite username" required>
        <select name="role"><option value="EDITOR">Editor</option><option value="VIEWER">Viewer</option></select>
        <button type="submit">Invite to Board</button>
      </form>
    </c:if>
  </div>

  <div class="grid">

    <!-- ================= Task List ================= -->
    <div class="panel">
      <h2>Tasks</h2>
      <select id="taskStatusFilter" style="margin-bottom:12px">
        <option value="ALL">All statuses</option>
        <option value="Todo">Todo</option>
        <option value="In Progress">In Progress</option>
        <option value="Done">Done</option>
      </select>
      <ul class="task-list">
        <c:forEach var="task" items="${tasks}">
          <li class="task-item" data-status="${task.taskstatus}">
            <div class="task-info">
              <div class="task-name"><c:out value="${task.taskname}"/></div>
              <div class="task-sub"><c:out value="${task.description}"/></div>
              <div class="task-sub">Assigned to <c:out value="${task.assigned_to}"/> &middot; Due <fmt:formatDate value="${task.deadline}" pattern="MMM dd, yyyy"/></div>
            </div>
            <span class="task-tag"><c:out value="${task.taskstatus}"/></span>
            <c:if test="${boardRole != 'VIEWER'}">
              <form method="post" action="${pageContext.request.contextPath}/task/update" style="display:grid;grid-template-columns:1.2fr 2fr 1fr 1fr 90px 1fr auto;gap:6px;width:100%">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                <input type="hidden" name="taskId" value="${task.taskid}">
                <input type="hidden" name="boardId" value="${board.id}">
                <input name="taskName" value="${task.taskname}" required maxlength="100">
                <input name="description" value="${task.description}" required maxlength="1000">
                <select name="taskStatus">
                  <option value="Todo" <c:if test="${task.taskstatus == 'Todo'}">selected</c:if>>Todo</option>
                  <option value="In Progress" <c:if test="${task.taskstatus == 'In Progress'}">selected</c:if>>In Progress</option>
                  <option value="Done" <c:if test="${task.taskstatus == 'Done'}">selected</c:if>>Done</option>
                </select>
                <select name="priority">
                  <option value="Low" <c:if test="${task.priority == 'Low'}">selected</c:if>>Low</option>
                  <option value="Medium" <c:if test="${task.priority == 'Medium'}">selected</c:if>>Medium</option>
                  <option value="High" <c:if test="${task.priority == 'High'}">selected</c:if>>High</option>
                </select>
                <input name="deadlineDays" type="number" min="1" max="365" value="${task.deadline_days > 0 ? task.deadline_days : 7}" required>
                <select name="assignedTo"><c:forEach var="connection" items="${connections}"><option value="${connection.username}" <c:if test="${connection.username == task.assigned_to}">selected</c:if>>@<c:out value="${connection.username}"/></option></c:forEach></select>
                <button type="submit">Save</button>
              </form>
              <form method="post" action="${pageContext.request.contextPath}/task/delete" onsubmit="return confirm('Delete this task permanently?')" style="display:inline">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                <input type="hidden" name="taskId" value="${task.taskid}">
                <input type="hidden" name="boardId" value="${board.id}">
                <button type="submit">Delete</button>
              </form>
            </c:if>
          </li>
        </c:forEach>
        <c:if test="${empty tasks}"><li class="task-item">No tasks have been created.</li></c:if>
      </ul>
    </div>

    <!-- ================= Members List ================= -->
    <div class="panel">
      <h2>Members</h2>
      <ul class="member-list">
        <c:forEach var="member" items="${members}">
          <li class="member-item">
            <div class="avatar"><c:out value="${member.username}"/></div>
            <div class="member-info">
              <div class="member-name"><c:out value="${member.username}"/></div>
              <div class="member-role"><c:out value="${member.role}"/></div>
            </div>
            <div class="status-dot"></div>
          </li>
        </c:forEach>
      </ul>
      <c:if test="${boardRole == 'OWNER' && not empty pendingInvitations}">
        <h3 style="margin-top:18px;font-size:14px">Pending invitations</h3>
        <c:forEach var="invitation" items="${pendingInvitations}">
          <div class="task-sub">@<c:out value="${invitation.username}"/> — <c:out value="${invitation.role}"/></div>
        </c:forEach>
      </c:if>
    </div>

  </div>

  <c:if test="${boardRole != 'VIEWER'}">
    <form id="multipleTaskForm" method="post" action="${pageContext.request.contextPath}/project/task" style="margin-top:20px;border-top:1px solid var(--border);padding-top:18px">
      <h3 style="margin:0 0 12px">Create Multiple Tasks</h3>
      <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
      <input type="hidden" name="board_id" value="${board.id}">
      <div id="multipleTaskRows">
        <div class="task-row" style="display:grid;grid-template-columns:1.5fr 2fr 1fr 90px 1fr 1fr auto;gap:8px;margin-bottom:10px">
          <input name="taskName" placeholder="Task name" required maxlength="100">
          <input name="description" placeholder="Task description" required maxlength="1000">
          <select name="taskStatus"><option value="Todo">Todo</option><option value="In Progress">In Progress</option><option value="Done">Done</option></select>
          <input name="deadlineDays" type="number" min="1" max="365" value="7" required>
          <select name="taskPriority"><option value="Low">Low</option><option value="Medium">Medium</option><option value="High">High</option></select>
          <select name="assignee" required><option value="">Tag user</option><c:forEach var="connection" items="${connections}"><option value="${connection.username}">@<c:out value="${connection.username}"/></option></c:forEach></select>
          <button type="button" class="remove-task-row" title="Remove task">×</button>
        </div>
      </div>
      <button type="button" id="addTaskRow" style="margin-right:8px">+ Add Another Task</button>
      <button type="submit">Create Tasks</button>
    </form>
  </c:if>
</div>

<script>
(function () {
  const addButton = document.getElementById('addTaskRow');
  const rows = document.getElementById('multipleTaskRows');
  if (!addButton || !rows) return;

  addButton.addEventListener('click', function () {
    const firstRow = rows.querySelector('.task-row');
    if (!firstRow) return;
    const copy = firstRow.cloneNode(true);
    copy.querySelectorAll('input').forEach(function (input) { input.value = ''; });
    copy.querySelectorAll('select').forEach(function (select) { select.selectedIndex = 0; });
    const deadline = copy.querySelector('input[name="deadlineDays"]');
    if (deadline) deadline.value = '7';
    rows.appendChild(copy);
  });

  rows.addEventListener('click', function (event) {
    if (!event.target.classList.contains('remove-task-row')) return;
    const allRows = rows.querySelectorAll('.task-row');
    if (allRows.length > 1) event.target.closest('.task-row').remove();
  });

  const statusFilter = document.getElementById('taskStatusFilter');
  if (statusFilter) {
    statusFilter.addEventListener('change', function () {
      document.querySelectorAll('.task-list .task-item[data-status]').forEach(function (item) {
        item.style.display = statusFilter.value === 'ALL' || item.dataset.status === statusFilter.value ? '' : 'none';
      });
    });
  }

  const multipleForm = document.getElementById('multipleTaskForm');
  if (multipleForm) {
    multipleForm.addEventListener('submit', function (event) {
      const names = Array.from(multipleForm.querySelectorAll('[name="taskName"]'));
      const descriptions = Array.from(multipleForm.querySelectorAll('[name="description"]'));
      const assignees = Array.from(multipleForm.querySelectorAll('[name="assignee"]'));
      for (let i = 0; i < names.length; i++) {
        if (names[i].value.trim().length < 2 || descriptions[i].value.trim().length < 3 || !assignees[i].value) {
          event.preventDefault();
          alert('Each task needs a name, description, and assignee.');
          return;
        }
      }
    });
  }
})();
</script>

</body>
</html>
