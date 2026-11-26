# InBody CS Call Monitor Dashboard

Source code for a call-monitoring dashboard, Android call-log app, and local Odoo Helpdesk ticket-creation bridge.

## Project structure

| Path | Description |
| --- | --- |
| `index.html` | Latest call-monitoring dashboard with Firebase/Firestore support and Odoo ticket action |
| `legacy/dashboard.html` | Earlier dashboard |
| `android-app/` | Kotlin Android app that reads call logs and sends them to Firebase Firestore |
| `bridge/server.js` | Local Node.js bridge for Odoo Helpdesk webhook |
| `bridge/START-DASHBOARD.bat` | Windows bridge launcher |
| `bridge/TEST-ODOO-BRIDGE.ps1` | PowerShell request test (uses fake caller data) |
| `bridge/secret/odoo-webhook.example.txt` | Placeholder template, **not** an actual webhook |

## Local dashboard + Odoo bridge

1. Install Node.js 18 or later.
2. Copy `index.html` into `bridge/public/dashboard.html`.
3. Copy `bridge/secret/odoo-webhook.example.txt` to `bridge/secret/odoo-webhook.txt`, and put a newly generated Odoo webhook URL in the local copy. Never commit it.
4. From `bridge/`, run `START-DASHBOARD.bat` on Windows, or `node server.js`.
5. Open `http://127.0.0.1:5500/dashboard.html`. The Odoo button sends `POST /api/create-odoo-ticket` to the local bridge.
6. You must configure the Firebase Web App locally; published HTML intentionally contains placeholders instead of live Firebase app identifiers.

The bridge binds only to the local computer (`127.0.0.1`), not to other engineers' machines. Before multi-user deployment, design a secure central service with authentication, authorization, access controls, and audit logging. Do not expose this bridge directly to the internet.

## Android app

Open `android-app/` as a Gradle project in Android Studio. Obtain `google-services.json` from your Firebase project and place it locally in `android-app/app/`; it is excluded from Git.

**Important:** This archive did not contain `google-services.json`, Gradle wrapper files, or a launcher icon resource referenced by the Android manifest. The project may need these to build. Android call-log permissions are highly restricted by platform and app-distribution policies, and call logging may behave differently by Android version/handset. Do not assume automatic capture works without device testing.

## Security and privacy

This repository is public. Do not commit customer names, phone numbers, actual call logs, Firebase service-account keys, Odoo API tokens, Odoo webhook URLs, secrets, or Android signing credentials. Firebase Web API keys usually identify a project rather than grant administrative access, but they are deliberately replaced with placeholders in this public source. Before enabling call collection, verify Firestore rules, consent/lawful basis and employee/customer data-handling requirements.

## Verification status

Source files from both uploaded archives have been added, except the real/placeholder secret path is represented only by a safe example file. Source upload does **not** mean the Android app was compiled or that the Odoo and Firebase integrations were successfully tested.
