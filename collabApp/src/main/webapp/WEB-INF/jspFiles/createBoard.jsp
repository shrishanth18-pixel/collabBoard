<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Create Board - CollabBoard</title>
<style>body{font-family:Segoe UI,Arial,sans-serif;background:#f4f6f9;margin:0}.card{max-width:620px;margin:40px auto;background:#fff;border-radius:14px;padding:28px;box-shadow:0 4px 16px #0001}.form-group{margin:16px 0}.form-group label{display:block;font-weight:600;margin-bottom:6px}.form-group input,.form-group textarea,.form-group select{width:100%;box-sizing:border-box;padding:11px;border:1px solid #d8dee8;border-radius:8px}.btn{background:#4a6cf7;color:#fff;border:0;border-radius:8px;padding:12px 18px;font-weight:600}.error{padding:12px;background:#fef2f2;border:1px solid #fca5a5;color:#b91c1c;border-radius:8px}</style>
</head><body><main class="card"><h1>Create Board</h1>
<c:if test="${not empty error}"><div class="error"><c:out value="${error}"/></div></c:if>
<form action="${pageContext.request.contextPath}/project" method="post" onsubmit="return validateBoard()">
<input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
<div class="form-group"><label for="name">Project name</label><input id="name" name="projectName" required maxlength="50"></div>
<div class="form-group"><label for="description">Description</label><textarea id="description" name="description" required maxlength="1000"></textarea></div>
<div class="form-group"><label for="status">Status</label><select id="status" name="projectStatus" required><option value="Planning">Planning</option><option value="Active">Active</option><option value="OnHold">On Hold</option><option value="Completed">Completed</option></select></div>
<div class="form-group"><label for="priority">Priority</label><select id="priority" name="projectPriority" required><option value="Low">Low</option><option value="Medium">Medium</option><option value="High">High</option></select></div>
<div class="form-group"><label for="months">Deadline months</label><input id="months" name="months" type="number" min="1" max="36" required></div>
<button class="btn" type="submit">Create Board</button> <a href="${pageContext.request.contextPath}/dashboard">Cancel</a>
</form></main><script>function validateBoard(){const name=document.getElementById('name').value.trim(),desc=document.getElementById('description').value.trim(),months=document.getElementById('months').value;if(name.length<2||desc.length<3||months<1||months>36){alert('Enter a valid name, description, and deadline.');return false;}return true;}</script></body></html>
