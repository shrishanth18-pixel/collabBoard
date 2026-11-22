<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - CollabBoard</title>
    <style>
        :root {
            --primary: #4f46e5;
            --primary-dark: #4338ca;
            --error-bg: #fef2f2;
            --error-border: #fca5a5;
            --error-text: #b91c1c;
            --success-bg: #f0fdf4;
            --success-border: #86efac;
            --success-text: #15803d;
            --text-dark: #1f2937;
            --text-muted: #6b7280;
            --border-color: #d1d5db;
            --bg-page: #f3f4f6;
        }

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            background:
                radial-gradient(circle at 15% 15%, rgba(79,70,229,.12), transparent 28%),
                linear-gradient(135deg, #eef2ff 0%, #f8fafc 100%);
            display: grid;
            place-items: center;
            padding: 24px;
        }

        .register-card {
            background: #ffffff;
            width: min(520px, 100%);
            border-radius: 20px;
            box-shadow: 0 24px 60px rgba(31,41,55,.14);
            border: 1px solid rgba(79,70,229,.08);
            padding: 38px 44px;
        }

        .brand {
            text-align: center;
            margin-bottom: 24px;
        }

        .brand h1 {
            margin: 0;
            font-size: 24px;
            font-weight: 700;
            color: var(--primary);
            letter-spacing: -0.02em;
        }

        .brand p {
            margin: 6px 0 0;
            font-size: 14px;
            color: var(--text-muted);
        }

        .alert {
            padding: 12px 14px;
            border-radius: 8px;
            font-size: 14px;
            margin-bottom: 20px;
            border: 1px solid transparent;
        }

        .alert-error {
            background-color: var(--error-bg);
            border-color: var(--error-border);
            color: var(--error-text);
        }

        .alert-success {
            background-color: var(--success-bg);
            border-color: var(--success-border);
            color: var(--success-text);
        }

        form {
            display: flex;
            flex-direction: column;
            gap: 18px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        label {
            font-size: 13px;
            font-weight: 600;
            color: var(--text-dark);
        }

        input[type="text"],
        input[type="email"],
        input[type="password"] {
            width: 100%;
            padding: 11px 14px;
            font-size: 14px;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            outline: none;
            transition: border-color 0.15s ease, box-shadow 0.15s ease;
            background-color: #fafafa;
        }

        input[type="text"]:focus,
        input[type="email"]:focus,
        input[type="password"]:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
            background-color: #ffffff;
        }

        .password-wrapper {
            position: relative;
            display: flex;
            align-items: center;
        }

        .password-wrapper input {
            padding-right: 44px;
        }

        .toggle-password {
            position: absolute;
            right: 10px;
            background: none;
            border: none;
            cursor: pointer;
            font-size: 12px;
            font-weight: 600;
            color: var(--primary);
            padding: 4px 6px;
        }

        .toggle-password:hover {
            color: var(--primary-dark);
        }

        .btn-primary {
            margin-top: 6px;
            padding: 12px 16px;
            background-color: var(--primary);
            color: #ffffff;
            font-size: 15px;
            font-weight: 600;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: background-color 0.15s ease, transform 0.05s ease;
        }

        .btn-primary:hover {
            background-color: var(--primary-dark);
        }

        .btn-primary:active {
            transform: scale(0.99);
        }

        .login-link {
            margin-top: 22px;
            text-align: center;
            font-size: 14px;
            color: var(--text-muted);
        }

        .login-link a {
            color: var(--primary);
            font-weight: 600;
            text-decoration: none;
        }

        .login-link a:hover {
            text-decoration: underline;
        }

        .auth-tabs {
            display: grid;
            grid-template-columns: 1fr 1fr;
            margin-bottom: 22px;
            padding: 5px;
            background: #f1f5f9;
            border-radius: 10px;
        }

        .auth-tabs a {
            padding: 10px 12px;
            text-align: center;
            text-decoration: none;
            color: var(--text-muted);
            font-size: 14px;
            font-weight: 700;
            border-radius: 7px;
        }

        .auth-tabs .active {
            color: var(--primary);
            background: #fff;
            box-shadow: 0 2px 8px rgba(31,41,55,.08);
        }

        @media (max-width: 480px) {
            .register-card {
                padding: 32px 22px;
                border-radius: 12px;
            }
        }
    </style>
</head>
<body>

    <main class="register-card">
        <div class="brand">
            <h1>CollabBoard</h1>
            <p>Create your account to start collaborating</p>
        </div>

        <nav class="auth-tabs" aria-label="Authentication options">
            <a href="${pageContext.request.contextPath}/login">Login</a>
            <a class="active" href="${pageContext.request.contextPath}/register">Register</a>
        </nav>

        <c:if test="${not empty error}">
            <div class="alert alert-error" role="alert">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <c:if test="${not empty success}">
            <div class="alert alert-success" role="alert">
                <c:out value="${success}"/>
            </div>
        </c:if>

        <form method="POST" action="${pageContext.request.contextPath}/register" novalidate>
            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">

            <div class="form-group">
                <label for="fullName">Full Name</label>
                <input
                    type="text"
                    id="fullName"
                    name="fullName"
                    placeholder="Enter your full name"
                    required
                    autocomplete="name">
            </div>

            <div class="form-group">
                <label for="email">Email</label>
                <input
                    type="email"
                    id="email"
                    name="email"
                    placeholder="you@example.com"
                    required
                    autocomplete="email">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <div class="password-wrapper">
                    <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Create a password"
                        required
                        autocomplete="new-password">
                    <button type="button" class="toggle-password" data-target="password" aria-label="Show password">Show</button>
                </div>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <div class="password-wrapper">
                    <input
                        type="password"
                        id="confirmPassword"
                        name="confirmPassword"
                        placeholder="Re-enter your password"
                        required
                        autocomplete="new-password">
                    <button type="button" class="toggle-password" data-target="confirmPassword" aria-label="Show password">Show</button>
                </div>
            </div>

            <button type="submit" class="btn-primary">Create Account</button>
        </form>

        <p class="login-link">
            Already have an account? <a href="${pageContext.request.contextPath}/login">Login</a>
        </p>
    </main>

    <script>
        document.querySelectorAll('.toggle-password').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var targetId = btn.getAttribute('data-target');
                var input = document.getElementById(targetId);
                if (input.type === 'password') {
                    input.type = 'text';
                    btn.textContent = 'Hide';
                    btn.setAttribute('aria-label', 'Hide password');
                } else {
                    input.type = 'password';
                    btn.textContent = 'Show';
                    btn.setAttribute('aria-label', 'Show password');
                }
            });
        });

        document.querySelector('form').addEventListener('submit', function (event) {
            const name = document.getElementById('fullName').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            const confirm = document.getElementById('confirmPassword').value;
            if (name.length < 2 || name.length > 50 || !/^[^@]+@[^@]+\.[^@]+$/.test(email)
                    || password.length < 8 || password !== confirm) {
                event.preventDefault();
                alert('Enter a 2-50 character name, valid email, 8+ character password, and matching confirmation.');
            }
        });
    </script>

</body>
</html>
