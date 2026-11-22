# Task 3 — JSP, JSTL, HTML and Validation Source

## Login JSP

**File:** `collabApp/src/main/webapp/WEB-INF/jspFiles/login.jsp`  
**Demonstrates:** JSP page, JSTL core tag `<c:if>`, EL `${error}` and `${pageContext.request.contextPath}`, client-side JavaScript validation, required input validation, server error rendering.

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Login - CollabBoard</title>
    <style>
        :root { --primary:#4f46e5; --primary-dark:#4338ca; --text:#1f2937; --muted:#6b7280; --border:#d9deea; }
        * { box-sizing:border-box; }
        body { margin:0; min-height:100vh; display:grid; place-items:center; padding:24px; font-family:"Segoe UI",Roboto,Arial,sans-serif; background:radial-gradient(circle at 15% 15%,rgba(79,70,229,.12),transparent 28%),linear-gradient(135deg,#eef2ff 0%,#f8fafc 100%); color:var(--text); }
        .auth-shell { width:min(860px,100%); min-height:520px; display:grid; grid-template-columns:42% 58%; overflow:hidden; background:#fff; border:1px solid rgba(79,70,229,.08); border-radius:20px; box-shadow:0 24px 60px rgba(31,41,55,.14); }
        .brand-panel { display:flex; flex-direction:column; justify-content:space-between; padding:46px 38px; color:#fff; background:linear-gradient(145deg,#4f46e5,#312e81); }
        .brand-mark { width:46px; height:46px; display:grid; place-items:center; border-radius:12px; background:rgba(255,255,255,.16); font-size:22px; font-weight:800; }
        .brand-panel h1 { margin:28px 0 10px; font-size:32px; }
        .brand-panel p { margin:0; color:#e0e7ff; line-height:1.7; }
        .brand-panel .feature { font-size:13px; color:#c7d2fe; }
        .form-panel { display:flex; flex-direction:column; justify-content:center; padding:48px 54px; }
        .auth-tabs { display:grid; grid-template-columns:1fr 1fr; margin-bottom:28px; padding:5px; background:#f1f5f9; border-radius:10px; }
        .auth-tabs a { padding:10px 12px; text-align:center; text-decoration:none; color:var(--muted); font-size:14px; font-weight:700; border-radius:7px; }
        .auth-tabs .active { color:var(--primary); background:#fff; box-shadow:0 2px 8px rgba(31,41,55,.08); }
        .form-panel h2 { margin:0 0 6px; font-size:25px; }
        .subtitle { margin:0 0 24px; color:var(--muted); font-size:14px; }
        .form-group { display:flex; flex-direction:column; gap:7px; margin-bottom:16px; }
        label { font-size:13px; font-weight:700; }
        input { width:100%; height:46px; padding:0 14px; border:1px solid var(--border); border-radius:9px; outline:none; font-size:14px; background:#fbfcfe; }
        input:focus { border-color:var(--primary); background:#fff; box-shadow:0 0 0 3px rgba(79,70,229,.12); }
        .btn { width:100%; height:46px; margin-top:6px; border:0; border-radius:9px; background:var(--primary); color:#fff; font-size:15px; font-weight:700; cursor:pointer; }
        .btn:hover { background:var(--primary-dark); }
        .error { margin-bottom:18px; padding:12px 14px; border:1px solid #fca5a5; border-radius:9px; background:#fef2f2; color:#b91c1c; font-size:14px; }
        @media (max-width:720px) { body{padding:16px;} .auth-shell{grid-template-columns:1fr;} .brand-panel{min-height:150px;padding:24px 28px;} .brand-panel h1{margin:12px 0 4px;font-size:24px;} .brand-panel .feature{display:none;} .form-panel{padding:28px 24px 32px;} }
    </style>
</head>
<body>
<main class="auth-shell">
    <section class="brand-panel">
        <div><div class="brand-mark">CB</div><h1>CollabBoard</h1><p>Plan projects, organize tasks, and keep every team member aligned.</p></div>
        <div class="feature">One workspace. Clear progress.</div>
    </section>
    <section class="form-panel">
        <nav class="auth-tabs" aria-label="Authentication options">
            <a class="active" href="${pageContext.request.contextPath}/login">Login</a>
            <a href="${pageContext.request.contextPath}/register">Register</a>
        </nav>
        <h2>Welcome back</h2>
        <p class="subtitle">Sign in to continue to your workspace.</p>
        <c:if test="${not empty error}">
            <div class="error" role="alert"><c:out value="${error}"/></div>
        </c:if>
        <form action="${pageContext.request.contextPath}/login" method="post" onsubmit="return validateLogin()">
            <div class="form-group">
                <label for="email">Email address</label>
                <input id="email" type="email" name="email" required autocomplete="email" placeholder="you@example.com">
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input id="password" type="password" name="password" required minlength="8" autocomplete="current-password" placeholder="Enter your password">
            </div>
            <button class="btn" type="submit">Login to CollabBoard</button>
        </form>
    </section>
</main>
<script>
    function validateLogin() {
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        if (!/^[^@]+@[^@]+\.[^@]+$/.test(email) || password.length < 8) {
            alert('Enter a valid email and a password of at least 8 characters.');
            return false;
        }
        return true;
    }
</script>
</body>
</html>
```

## Create-Board JSP

**File:** `collabApp/src/main/webapp/WEB-INF/jspFiles/createBoard.jsp`  
**Demonstrates:** JSP form, JSTL conditional error rendering, EL, required fields, numeric bounds, JavaScript validation.

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Create Board - CollabBoard</title>
<style>body{font-family:Segoe UI,Arial,sans-serif;background:#f4f6f9;margin:0}.card{max-width:620px;margin:40px auto;background:#fff;border-radius:14px;padding:28px;box-shadow:0 4px 16px #0001}.form-group{margin:16px 0}.form-group label{display:block;font-weight:600;margin-bottom:6px}.form-group input,.form-group textarea,.form-group select{width:100%;box-sizing:border-box;padding:11px;border:1px solid #d8dee8;border-radius:8px}.btn{background:#4a6cf7;color:#fff;border:0;border-radius:8px;padding:12px 18px;font-weight:600}.error{padding:12px;background:#fef2f2;border:1px solid #fca5a5;color:#b91c1c;border-radius:8px}</style>
</head>
<body>
<main class="card">
<h1>Create Board</h1>
<c:if test="${not empty error}"><div class="error">${error}</div></c:if>
<form action="${pageContext.request.contextPath}/project" method="post" onsubmit="return validateBoard()">
<div class="form-group"><label for="name">Project name</label><input id="name" name="project name" required maxlength="50"></div>
<div class="form-group"><label for="description">Description</label><textarea id="description" name="description" required maxlength="1000"></textarea></div>
<div class="form-group"><label for="status">Status</label><select id="status" name="project status" required><option value="Planning">Planning</option><option value="Active">Active</option><option value="OnHold">On Hold</option><option value="Completed">Completed</option></select></div>
<div class="form-group"><label for="priority">Priority</label><select id="priority" name="project priority" required><option value="Low">Low</option><option value="Medium">Medium</option><option value="High">High</option></select></div>
<div class="form-group"><label for="months">Deadline months</label><input id="months" name="months" type="number" min="1" max="36" required></div>
<button class="btn" type="submit">Create Board</button> <a href="${pageContext.request.contextPath}/dashboard">Cancel</a>
</form>
</main>
<script>
function validateBoard(){
 const name=document.getElementById('name').value.trim(),desc=document.getElementById('description').value.trim(),months=document.getElementById('months').value;
 if(name.length<2||desc.length<3||months<1||months>36){alert('Enter a valid name, description, and deadline.');return false;} return true;
}
</script>
</body>
</html>
```

## Dashboard and board JSP collection

The complete actual sources for the remaining dynamic pages are retained verbatim in:

- `collabApp/src/main/webapp/WEB-INF/jspFiles/dashboard.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/boardDetails.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/register.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/friends.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/invitations.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/taskTable.html`

These files demonstrate `<c:choose>`, `<c:when>`, `<c:otherwise>`, `<c:forEach>`, `<c:if>`, EL collection access, `fmt:formatDate`, dynamic navigation, conditional role rendering, task iteration, member iteration and client-side filtering.
