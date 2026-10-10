// ==============================================================================
// DOM Element Selection
// TECHNOLOGY: [JavaScript / DOM API (Document Object Model)]
// USE: Queries references to HTML elements by their unique IDs.
// WHY: Enables JavaScript to read user inputs, modify button states, and display messages.
// ==============================================================================
const form = document.getElementById("registrationForm");      // Reference to the <form> element
const message = document.getElementById("message");            // Reference to the <div> for feedback text
const registerButton = document.getElementById("registerButton"); // Reference to the submit <button>

// ==============================================================================
// Form Submit Event Listener
// TECHNOLOGY: [JavaScript / Event Listener] + [Async / Await (ES2017)]
// USE: Intercepts form submission and executes asynchronous registration workflow.
// WHY: 'async' allows using 'await' for the network fetch request without blocking UI thread.
// ==============================================================================
form.addEventListener("submit", async function (event) {

    // [JavaScript / DOM Event]: Prevents the browser's default form submission (page reload)
    // WHY: Keeps user on the same page for a smooth Single Page Application (SPA) experience
    event.preventDefault();

    // [JavaScript / DOM API]: Extract trimmed values from each form input field
    // WHY: .trim() strips accidental leading/trailing spaces from user input
    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    // [JavaScript / DOM Manipulation]: Reset previous feedback message and color
    message.textContent = "";
    message.style.color = "";

    // [JavaScript / Client-Side Validation]: Check if passwords match before making network call
    // WHY: Saves bandwidth and gives instant feedback without waiting for server response
    if (password !== confirmPassword) {
        message.textContent = "Passwords do not match";
        message.style.color = "red";
        return; // Stops further execution
    }

    // [JavaScript / UI State]: Disable button and show spinner/loading text
    // WHY: Prevents user from spamming the register button and creating duplicate requests
    registerButton.disabled = true;
    registerButton.textContent = "Registering...";

    // [JavaScript Object]: Packages input fields into a key-value object matching RegisterRequest DTO
    const userData = {
        name: name,
        email: email,
        phone: phone,
        password: password,
        confirmPassword: confirmPassword
    };

    try {
        // [JavaScript / Dynamic API URL]:
        // Automatically chooses localhost when testing on your laptop (via localhost, 127.0.0.1, or double-clicking index.html),
        // and automatically switches to your live cloud server when deployed on Vercel.
        const isLocal = window.location.hostname === "localhost"
                     || window.location.hostname === "127.0.0.1"
                     || window.location.protocol === "file:";

        const BACKEND_URL = isLocal
            ? "http://localhost:8080/api/users/register"
            : "https://YOUR-RENDER-BACKEND-URL.onrender.com/api/users/register";

        // [JavaScript / Fetch API (HTTP Request)]:
        // USE: Sends asynchronous HTTP POST request to Spring Boot backend.
        // WHY: Transmits userData payload serialized into JSON string to the server endpoint.
        const response = await fetch(
            BACKEND_URL,
            {
                method: "POST", // HTTP Method: POST for creating new resource

                headers: {
                    // Tells Spring Boot backend that the request body is formatted as JSON
                    "Content-Type": "application/json"
                },

                // Converts JavaScript object into JSON string ({"name":"...", "email":"..."})
                body: JSON.stringify(userData)
            }
        );

        // [JavaScript / JSON Parsing]: Parse backend response body as JSON
        const data = await response.json();

        // [JavaScript Condition]: response.ok is true for HTTP status 200 to 299 (e.g., 201 Created)
        if (response.ok) {

            // Display success message from Spring Boot in green
            message.textContent = data.message;
            message.style.color = "green";

            // Clear all input fields in the form for next entry
            form.reset();

        } else {
            // Backend returned an error (HTTP 400 Bad Request, HTTP 409 Conflict, etc.)
            if (data.errors) {
                // If Jakarta validation errors exist, extract the first field error
                const firstError = Object.values(data.errors)[0];
                message.textContent = firstError;
            } else {
                // Otherwise display top-level error message (e.g., "Email is already registered")
                message.textContent = data.message || "Registration failed";
            }

            // Display error message in red
            message.style.color = "red";
        }

    } catch (error) {
        // [JavaScript Error Handling]: Triggers if backend server is offline or network fails
        console.error(error); // Logs full error in browser developer console (F12)

        message.textContent = "Cannot connect to server";
        message.style.color = "red";

    } finally {
        // [JavaScript finally block]: Always executes regardless of success or error
        // WHY: Re-enables the button so user can interact with the form again
        registerButton.disabled = false;
        registerButton.textContent = "Register";
    }
});