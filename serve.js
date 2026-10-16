const http = require('http');
const fs = require('fs');
const path = require('path');

const FRONTEND_PORT = 3000;
const BACKEND_PORT = 8080;
const PUBLIC_DIR = path.join(__dirname, 'frontend');

const MIME_TYPES = {
  '.html': 'text/html',
  '.css': 'text/css',
  '.js': 'text/javascript',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.svg': 'image/svg+xml'
};

// 1. Frontend Static Server
const frontendServer = http.createServer((req, res) => {
  let reqPath = req.url.split('?')[0];
  if (reqPath === '/' || reqPath === '') reqPath = '/index.html';

  const filePath = path.join(PUBLIC_DIR, reqPath);

  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('404 Not Found');
      return;
    }
    const ext = path.extname(filePath).toLowerCase();
    res.writeHead(200, { 'Content-Type': MIME_TYPES[ext] || 'application/octet-stream' });
    res.end(data);
  });
});

frontendServer.listen(FRONTEND_PORT, '0.0.0.0', () => {
  console.log(`Frontend running at http://localhost:${FRONTEND_PORT}`);
});

// 2. Mock Backend API Server
const registeredUsers = new Set();

const backendServer = http.createServer((req, res) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  const url = req.url.split('?')[0];

  if (url === '/api/users/health' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      success: true,
      message: 'Backend is running',
      timestamp: new Date().toISOString()
    }));
    return;
  }

  if (url === '/api/users/register' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const payload = JSON.parse(body);
        const { name, email, phone, password, confirmPassword } = payload;

        const errors = {};
        if (!name || name.trim().length < 3) errors.name = "Name must be at least 3 characters";
        if (!email || !/^\S+@\S+\.\S+$/.test(email)) errors.email = "Please enter a valid email address";
        if (!phone || !/^\d{10}$/.test(phone)) errors.phone = "Phone number must be exactly 10 digits";
        if (!password || password.length < 8) errors.password = "Password must be at least 8 characters";
        if (password !== confirmPassword) errors.confirmPassword = "Passwords do not match";

        if (Object.keys(errors).length > 0) {
          res.writeHead(400, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({
            success: false,
            message: "Validation failed",
            errors,
            timestamp: new Date().toISOString()
          }));
          return;
        }

        if (registeredUsers.has(email.toLowerCase())) {
          res.writeHead(409, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({
            success: false,
            message: "Email is already registered",
            timestamp: new Date().toISOString()
          }));
          return;
        }

        registeredUsers.add(email.toLowerCase());
        res.writeHead(201, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
          success: true,
          message: "Registration successful! Welcome aboard.",
          timestamp: new Date().toISOString()
        }));
      } catch (err) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, message: "Invalid JSON format" }));
      }
    });
    return;
  }

  res.writeHead(404, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ success: false, message: "Endpoint Not Found" }));
});

backendServer.listen(BACKEND_PORT, '0.0.0.0', () => {
  console.log(`Backend API running at http://localhost:${BACKEND_PORT}`);
});
