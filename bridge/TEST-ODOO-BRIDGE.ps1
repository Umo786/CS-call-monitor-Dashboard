$body = @{
  callerName = "Test Customer"
  number     = "+910000000000"
  callType   = "incoming"
  callTime   = "25-Nov-2026 12:15"
  duration   = "2m 30s"
  note       = "Local bridge test"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://127.0.0.1:5500/api/create-odoo-ticket" -Method Post -ContentType "application/json" -Body $body
