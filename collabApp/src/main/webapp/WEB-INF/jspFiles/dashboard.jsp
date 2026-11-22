<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Dashboard - CollabBoard</title>
<style>
  :root {
    --bg: #f4f6f9;
    --sidebar-bg: #1e2733;
    --sidebar-text: #b8c2cc;
    --sidebar-active: #2c3a4b;
    --accent: #4a6cf7;
    --accent-hover: #3a5ce0;
    --card-bg: #ffffff;
    --border: #e3e7ed;
    --text-dark: #1f2733;
    --text-muted: #6b7684;
  }

  * {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

  body {
    font-family: "Segoe UI", Roboto, Arial, sans-serif;
    background: var(--bg);
    color: var(--text-dark);
  }

  .layout {
    display: flex;
    min-height: 100vh;
  }

  /* ---------- Sidebar ---------- */
  .sidebar {
    width: 240px;
    background: var(--sidebar-bg);
    color: var(--sidebar-text);
    display: flex;
    flex-direction: column;
    flex-shrink: 0;
    transition: transform 0.25s ease;
  }

  .sidebar-header {
    padding: 22px 20px;
    font-size: 20px;
    font-weight: 700;
    color: #fff;
    border-bottom: 1px solid rgba(255,255,255,0.08);
    letter-spacing: 0.5px;
  }

  .sidebar-header span {
    color: var(--accent);
  }

  .sidebar-nav {
    flex: 1;
    padding: 16px 0;
    list-style: none;
  }

  .sidebar-nav li {
    padding: 12px 22px;
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    font-size: 14.5px;
    border-left: 3px solid transparent;
  }

  .sidebar-nav li:hover {
    background: var(--sidebar-active);
    color: #fff;
  }

  .sidebar-nav li.active {
    background: var(--sidebar-active);
    color: #fff;
    border-left: 3px solid var(--accent);
  }

  .sidebar-nav .icon {
    width: 18px;
    text-align: center;
  }

  .sidebar-footer {
    padding: 18px 20px;
    border-top: 1px solid rgba(255,255,255,0.08);
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .avatar {
    width: 32px;
    height: 32px;
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

  /* ---------- Mobile sidebar toggle ---------- */
  .menu-toggle {
    display: none;
    background: none;
    border: none;
    font-size: 22px;
    cursor: pointer;
    color: var(--text-dark);
  }

  /* ---------- Main content ---------- */
  .main {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;
  }

  .topbar {
    background: var(--card-bg);
    border-bottom: 1px solid var(--border);
    padding: 16px 28px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
  }

  .topbar-left {
    display: flex;
    align-items: center;
    gap: 14px;
  }

  .topbar h1 {
    font-size: 20px;
    font-weight: 600;
  }

  .search-box {
    flex: 1;
    max-width: 320px;
  }

  .search-box input {
    width: 100%;
    padding: 9px 14px;
    border: 1px solid var(--border);
    border-radius: 8px;
    font-size: 14px;
    outline: none;
  }

  .search-box input:focus {
    border-color: var(--accent);
  }

  .content {
    padding: 28px;
    flex: 1;
  }

  .content-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 22px;
    gap: 12px;
    flex-wrap: wrap;
  }

  .content-header h2 {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-dark);
  }

  .content-header p {
    font-size: 13.5px;
    color: var(--text-muted);
    margin-top: 4px;
  }

  /* ---------- Create Board Button ---------- */
  .btn-create {
    background: var(--accent);
    color: #fff;
    border: none;
    padding: 11px 20px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    transition: background 0.15s ease, transform 0.1s ease;
    white-space: nowrap;
  }

  .btn-create:hover {
    background: var(--accent-hover);
  }

  .btn-create:active {
    transform: scale(0.97);
  }

  /* ---------- Board Cards Grid ---------- */
  .board-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
  }

  .board-card {
    background: var(--card-bg);
    border: 1px solid var(--border);
    border-radius: 12px;
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 12px;
    cursor: pointer;
    transition: box-shadow 0.15s ease, transform 0.15s ease;
    border-top: 4px solid var(--accent);
  }

  .board-card:hover {
    box-shadow: 0 6px 18px rgba(0,0,0,0.08);
    transform: translateY(-2px);
  }

  .board-card.color-green { border-top-color: #2ecc71; }
  .board-card.color-orange { border-top-color: #f39c12; }
  .board-card.color-purple { border-top-color: #9b59b6; }
  .board-card.color-red { border-top-color: #e74c3c; }

  .board-card-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-dark);
  }

  .board-card-desc {
    font-size: 13px;
    color: var(--text-muted);
    line-height: 1.4;
    flex: 1;
  }

  .board-card-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 12.5px;
    color: var(--text-muted);
    border-top: 1px solid var(--border);
    padding-top: 12px;
  }

  .board-card-progress {
    height: 6px;
    background: #eef0f4;
    border-radius: 4px;
    overflow: hidden;
  }

  .board-card-progress-bar {
    height: 100%;
    background: var(--accent);
    border-radius: 4px;
  }

  .members {
    display: flex;
  }

  .members .avatar {
    width: 24px;
    height: 24px;
    font-size: 10.5px;
    margin-left: -8px;
    border: 2px solid var(--card-bg);
  }

  .members .avatar:first-child {
    margin-left: 0;
  }

  /* ---------- Create Board Card (dashed) ---------- */
  .board-card-new {
    background: transparent;
    border: 2px dashed var(--border);
    border-top: 2px dashed var(--border);
    align-items: center;
    justify-content: center;
    text-align: center;
    color: var(--text-muted);
    min-height: 150px;
  }

  .board-card-new:hover {
    border-color: var(--accent);
    color: var(--accent);
  }

  .board-card-new .plus {
    font-size: 26px;
    font-weight: 300;
  }

  /* ---------- Responsive ---------- */
  @media (max-width: 900px) {
    .sidebar {
      position: fixed;
      top: 0;
      left: 0;
      bottom: 0;
      z-index: 100;
      transform: translateX(-100%);
      box-shadow: 2px 0 12px rgba(0,0,0,0.2);
    }

    .sidebar.open {
      transform: translateX(0);
    }

    .menu-toggle {
      display: inline-block;
    }

    .topbar h1 {
      font-size: 17px;
    }
  }

  @media (max-width: 600px) {
    .content {
      padding: 18px;
    }

    .topbar {
      padding: 14px 16px;
    }

    .search-box {
      display: none;
    }

    .content-header {
      flex-direction: column;
      align-items: flex-start;
    }

    .btn-create {
      width: 100%;
      justify-content: center;
    }

    .board-grid {
      grid-template-columns: 1fr;
    }
  }

  .overlay {
    display: none;
    position: fixed;
    inset: 0;
    background: rgba(0,0,0,0.4);
    z-index: 90;
  }

  .overlay.show {
    display: block;
  }
</style>
</head>
<body>

<div class="layout">

  <!-- ================= Sidebar ================= -->
  <aside class="sidebar" id="sidebar">
    <div class="sidebar-header">Collab<span>Board</span></div>
    <ul class="sidebar-nav">
      <li class="active"><span class="icon">&#9635;</span> Dashboard</li>
      <li><span class="icon">&#128203;</span> My Boards</li>
      <li><a href="${pageContext.request.contextPath}/friends" style="color:inherit;text-decoration:none"><span class="icon">&#128101;</span> Connections</a></li>
      <li><a href="${pageContext.request.contextPath}/invite" style="color:inherit;text-decoration:none"><span class="icon">&#9993;</span> Invitations</a></li>
      <li><span class="icon">&#128276;</span> Notifications</li>
      <li><span class="icon">&#9881;</span> Settings</li>
    </ul>
    <div class="sidebar-footer">
      <div class="avatar">SP</div>
      <div>
        <div style="color:#fff; font-weight:600;"><c:out value="${sessionScope.username}"/></div>
        <a href="${pageContext.request.contextPath}/logout" style="font-size:11.5px;color:#fff">Logout</a>
      </div>
    </div>
  </aside>

  <div class="overlay" id="overlay"></div>

  <!-- ================= Main ================= -->
  <div class="main">

    <div class="topbar">
      <div class="topbar-left">
        <button class="menu-toggle" id="menuToggle">&#9776;</button>
        <h1>Dashboard</h1>
      </div>
      <div class="search-box">
        <input type="text" placeholder="Search boards...">
      </div>
    </div>

    <div class="content">
      <div class="content-header">
        <div>
          <h2>Your Boards</h2>
          <p>Track and manage all your project boards in one place.</p>
        </div>
        <button class="btn-create" id="createBoardBtn">
          <span>+</span> Create Board
        </button>
      </div>

      <div class="board-grid">
        <c:choose>
          <c:when test="${not empty boards}">
            <c:forEach var="board" items="${boards}">
              <a href="${pageContext.request.contextPath}/board?id=${board.id}" style="text-decoration:none;color:inherit">
                <div class="board-card">
                  <div class="board-card-title"><c:out value="${board.projectName}"/><c:if test="${empty board.projectName}"><c:out value="${board.projectname}"/></c:if></div>
                  <div class="board-card-desc"><c:out value="${board.description}"/></div>
                  <div class="board-card-meta">
                    <span><c:out value="${board.status}"/> &middot; <c:out value="${board.priority}"/></span>
                    <span><c:out value="${board.role}"/></span>
                  </div>
                </div>
              </a>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <p>No boards yet. Create your first board or <a href="${pageContext.request.contextPath}/friends">connect with friends</a> to begin.</p>
          </c:otherwise>
        </c:choose>

        <!-- Create Board Card -->
        <div class="board-card board-card-new" id="createBoardCard">
          <div class="plus">+</div>
          <div>Create New Board</div>
        </div>

      </div>
    </div>
  </div>
</div>

<script>
  // Sidebar toggle for mobile
  var sidebar = document.getElementById('sidebar');
  var overlay = document.getElementById('overlay');
  var menuToggle = document.getElementById('menuToggle');

  function toggleSidebar() {
    sidebar.classList.toggle('open');
    overlay.classList.toggle('show');
  }

  menuToggle.addEventListener('click', toggleSidebar);
  overlay.addEventListener('click', toggleSidebar);

  // Create board actions (hook up to servlet as needed)
  document.getElementById('createBoardBtn').addEventListener('click', function() {
    window.location.href = '${pageContext.request.contextPath}/project';
  });
  document.getElementById('createBoardCard').addEventListener('click', function() {
    window.location.href = '${pageContext.request.contextPath}/project';
  });
</script>

</body>
</html>
