@echo off
cd /d "%~dp0"

where node >nul 2>&1
if errorlevel 1 (
  echo Node.js was not found.
  pause
  exit /b 1
)

if not exist "secret\odoo-webhook.txt" (
  echo Missing secret\odoo-webhook.txt
  pause
  exit /b 1
)

if not exist "public\dashboard.html" (
  echo Missing public\dashboard.html
  echo Copy your working dashboard HTML into public and rename it dashboard.html.
  pause
  exit /b 1
)

start "" "http://127.0.0.1:5500/dashboard.html"
node server.js
pause
