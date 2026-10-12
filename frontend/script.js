// ==============================================================================
// Production Registration Form Logic
// ==============================================================================

const form = document.getElementById("registrationForm");
const statusAlert = document.getElementById("statusAlert");
const registerButton = document.getElementById("registerButton");

// Field references
const fields = {
    name: document.getElementById("name"),
    email: document.getElementById("email"),
    phone: document.getElementById("phone"),
    password: document.getElementById("password"),
    confirmPassword: document.getElementById("confirmPassword")
};

const errors = {
    name: document.getElementById("nameError"),
    email: document.getElementById("emailError"),
    phone: document.getElementById("phoneError"),
    password: document.getElementById("passwordError"),
    confirmPassword: document.getElementById("confirmPasswordError")
};

// Toggle password visibility
setupPasswordToggle("togglePasswordBtn", fields.password);
setupPasswordToggle("toggleConfirmPasswordBtn", fields.confirmPassword);

function setupPasswordToggle(buttonId, inputElement) {
    const btn = document.getElementById(buttonId);
    if (!btn || !inputElement) return;

    btn.addEventListener("click", () => {
        const isPassword = inputElement.type === "password";
        inputElement.type = isPassword ? "text" : "password";
        btn.textContent = isPassword ? "Hide" : "Show";
    });
}

function clearAllErrors() {
    statusAlert.style.display = "none";
    statusAlert.className = "status-alert";
    statusAlert.textContent = "";

    Object.keys(fields).forEach(key => {
        fields[key].classList.remove("input-invalid");
        if (errors[key]) {
            errors[key].textContent = "";
        }
    });
}

function setFieldError(fieldKey, message) {
    if (fields[fieldKey]) {
        fields[fieldKey].classList.add("input-invalid");
    }
    if (errors[fieldKey]) {
        errors[fieldKey].textContent = message;
    }
}

function showBanner(type, message) {
    statusAlert.textContent = message;
    statusAlert.className = `status-alert ${type}`;
    statusAlert.style.display = "block";
}

// Clear individual errors on input
Object.keys(fields).forEach(key => {
    fields[key].addEventListener("input", () => {
        fields[key].classList.remove("input-invalid");
        if (errors[key]) {
            errors[key].textContent = "";
        }
    });
});

form.addEventListener("submit", async function (event) {
    event.preventDefault();
    clearAllErrors();

    const name = fields.name.value.trim();
    const email = fields.email.value.trim();
    const phone = fields.phone.value.trim();
    const password = fields.password.value;
    const confirmPassword = fields.confirmPassword.value;

    let hasClientError = false;

    // Client-side validations
    if (name.length < 3) {
        setFieldError("name", "Name must be at least 3 characters");
        hasClientError = true;
    }

    if (!/^\S+@\S+\.\S+$/.test(email)) {
        setFieldError("email", "Please enter a valid email address");
        hasClientError = true;
    }

    if (!/^\d{10}$/.test(phone)) {
        setFieldError("phone", "Phone number must be exactly 10 digits");
        hasClientError = true;
    }

    if (password.length < 8) {
        setFieldError("password", "Password must be at least 8 characters");
        hasClientError = true;
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(password)) {
        setFieldError("password", "Must include uppercase, lowercase, and a number");
        hasClientError = true;
    }

    if (password !== confirmPassword) {
        setFieldError("confirmPassword", "Passwords do not match");
        hasClientError = true;
    }

    if (hasClientError) {
        return;
    }

    registerButton.disabled = true;
    registerButton.querySelector(".btn-text").textContent = "Creating Account...";

    const payload = {
        name,
        email,
        phone,
        password,
        confirmPassword
    };

    try {
        const isLocal = window.location.hostname === "localhost"
                     || window.location.hostname === "127.0.0.1"
                     || window.location.protocol === "file:";

        const baseUrl = window.API_BASE_URL || (isLocal
            ? "http://localhost:8080"
            : "https://YOUR-RENDER-BACKEND-URL.onrender.com");

        const response = await fetch(`${baseUrl}/api/users/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok) {
            showBanner("success", data.message || "Account created successfully!");
            form.reset();
        } else if (response.status === 400 && data.errors) {
            // Field-level validation errors from Jakarta Validator
            Object.entries(data.errors).forEach(([field, msg]) => {
                setFieldError(field, msg);
            });
            showBanner("error", data.message || "Please correct the highlighted fields");
        } else if (response.status === 409) {
            // Email conflict
            setFieldError("email", data.message || "Email is already registered");
            showBanner("error", data.message || "Email is already registered");
        } else if (response.status === 429) {
            // Rate limiting
            showBanner("error", data.message || "Too many requests. Please wait a minute and try again.");
        } else {
            showBanner("error", data.message || "Registration failed. Please try again.");
        }

    } catch (err) {
        console.error("Network or connection error:", err);
        showBanner("error", "Cannot connect to server. Please check your internet connection.");
    } finally {
        registerButton.disabled = false;
        registerButton.querySelector(".btn-text").textContent = "Create Account";
    }
});