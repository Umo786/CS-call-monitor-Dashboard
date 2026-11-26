const http = require("http");
const fs = require("fs");
const path = require("path");

const HOST = "127.0.0.1";
const PORT = 5500;
const PUBLIC_DIR = path.join(__dirname, "public");
const SECRET_FILE = path.join(__dirname, "secret", "odoo-webhook.txt");

function json(res, status, obj) {
  const body = JSON.stringify(obj);
  res.writeHead(status, {
    "Content-Type": "application/json; charset=utf-8",
    "Content-Length": Buffer.byteLength(body),
    "Cache-Control": "no-store"
  });
  res.end(body);
}

function getWebhookUrl() {
  const url = fs.readFileSync(SECRET_FILE, "utf8").trim();
  if (!/^https:\/\/.+\/web\/hook\//i.test(url)) {
    throw new Error("Invalid Odoo webhook URL");
  }
  return url;
}

function readJson(req) {
  return new Promise((resolve, reject) => {
    let body = "";
    req.on("data", chunk => {
      body += chunk;
      if (body.length > 1024 * 1024) reject(new Error("Request too large"));
    });
    req.on("end", () => {
      try { resolve(body ? JSON.parse(body) : {}); }
      catch { reject(new Error("Invalid JSON")); }
    });
    req.on("error", reject);
  });
}

async function createOdooTicket(req, res) {
  try {
    const p = await readJson(req);
    const payload = {
      callerName: String(p.callerName || p.number || "Unknown Caller"),
      number: String(p.number || ""),
      callType: String(p.callType || p.type || ""),
      callTime: String(p.callTime || ""),
      duration: String(p.duration || ""),
      note: String(p.note || "")
    };

    if (!payload.number) {
      return json(res, 400, { success:false, error:"Phone number is required" });
    }

    const response = await fetch(getWebhookUrl(), {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    const responseText = await response.text();
    let result = {};
    try { result = JSON.parse(responseText); } catch {}

    if (!response.ok || result.status !== "ok") {
      return json(res, 502, { success:false, error:"Odoo webhook returned an error" });
    }

    return json(res, 200, { success:true, message:"Odoo Helpdesk ticket created" });
  } catch (e) {
    console.error(e);
    return json(res, 500, { success:false, error:e.message });
  }
}

function serveFile(req, res) {
  let urlPath = decodeURIComponent(req.url.split("?")[0]);
  if (urlPath === "/") urlPath = "/dashboard.html";

  const filePath = path.normalize(path.join(PUBLIC_DIR, urlPath));
  if (!filePath.startsWith(PUBLIC_DIR)) {
    res.writeHead(403);
    return res.end("Forbidden");
  }

  fs.stat(filePath, (err, stat) => {
    if (err || !stat.isFile()) {
      res.writeHead(404, {"Content-Type":"text/plain; charset=utf-8"});
      return res.end("File not found");
    }

    const ext = path.extname(filePath).toLowerCase();
    const types = {
      ".html":"text/html; charset=utf-8",
      ".js":"application/javascript; charset=utf-8",
      ".css":"text/css; charset=utf-8",
      ".png":"image/png",
      ".svg":"image/svg+xml"
    };

    res.writeHead(200, {
      "Content-Type": types[ext] || "application/octet-stream",
      "Cache-Control":"no-cache"
    });
    fs.createReadStream(filePath).pipe(res);
  });
}

const server = http.createServer((req, res) => {
  res.setHeader("X-Content-Type-Options", "nosniff");
  if (req.method === "POST" && req.url.startsWith("/api/create-odoo-ticket")) {
    return createOdooTicket(req, res);
  }
  if (req.method === "GET") return serveFile(req, res);
  return json(res, 405, { success:false, error:"Method not allowed" });
});

server.listen(PORT, HOST, () => {
  console.log("InBody local bridge running at http://" + HOST + ":" + PORT + "/dashboard.html");
  console.log("Keep this window open while using the dashboard.");
});
