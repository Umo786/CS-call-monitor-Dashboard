# CS Call Monitor Dashboard

Call-monitoring dashboard prototype for InBody India's technical support operations. This is a **public source-code repository**, not a production deployment.

## Files
- `index.html` — latest dashboard source, including Firestore call monitoring and the Create Odoo Ticket button.
- `legacy/dashboard.html` — earlier dashboard version.
- `.gitignore` — excludes local secrets/configuration.

## Getting started
1. Review and secure Firebase Firestore rules before connecting actual call data.
2. Set up Firebase Web App configuration locally. The public source has placeholder Firebase Web API key and Web App ID values.
3. Serve the HTML through a local development web server.
4. The Create Odoo Ticket button sends `POST /api/create-odoo-ticket`, which requires a separate server-side Odoo bridge. That backend is **not included** because its source archive could not be retrieved for upload.
5. Keep all Odoo credentials on the server, not in browser code.

## Current limitations
- The dashboard code is uploaded but has not been verified as a fully functioning production deployment.
- The local Odoo bridge archive, Android call-logging app, and application binaries were not uploaded.
- Live connection settings are intentionally removed from the public dashboard.

## Security
Do not commit API tokens, passwords, service account files, customer contact information, or call logs. Firebase Web API keys are normally public identifiers, but securing Firestore rules and authorization is critical before connecting real call records.
