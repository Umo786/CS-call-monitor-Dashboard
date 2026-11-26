# Free local Odoo bridge

Node.js 18+ is required.

1. Renew the Odoo webhook secret.
2. Copy secret/odoo-webhook.example.txt to secret/odoo-webhook.txt and paste the newly created webhook URL into the local file. Never commit the actual URL.
3. Copy the repository's index.html to public/dashboard.html.
4. Run START-DASHBOARD.bat on Windows (or node server.js).
5. Keep the server running.
6. Test using powershell.exe -ExecutionPolicy Bypass -File .\TEST-ODOO-BRIDGE.ps1.

The dashboard POST endpoint is /api/create-odoo-ticket. This local bridge runs on 127.0.0.1:5500. It is not a shared multi-user deployment. For other computers, use a properly authenticated centralized backend instead of exposing this local bridge publicly.

No Firebase Blaze plan is needed by this local server itself. Firestore usage is subject to your Firebase quotas and security rules.
