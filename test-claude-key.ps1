# This file tests whether your Claude / Anthropic API key works.
# Do not paste your real API key into this file.
#
# Use it like this from PowerShell:
# cd "C:\Users\dusty\OneDrive\Documents\Hackathons\AI Berkeley 26"
# $env:ANTHROPIC_API_KEY="paste_your_key_here"
# powershell -ExecutionPolicy Bypass -File .\test-claude-key.ps1

if (-not $env:ANTHROPIC_API_KEY) {
    Write-Host "Missing API key."
    Write-Host "Set it first with:"
    Write-Host '$env:ANTHROPIC_API_KEY="paste_your_key_here"'
    exit 1
}

$model = "claude-sonnet-4-6"

$body = @{
    model = $model
    max_tokens = 80
    messages = @(
        @{
            role = "user"
            content = "A basket has 3 apples and 2 oranges. If I add 4 apples, how many pieces of fruit are in the basket total? Answer in one short sentence."
        }
    )
} | ConvertTo-Json -Depth 10

try {
    Write-Host "Testing Claude model: $model"

    $response = Invoke-RestMethod `
        -Uri "https://api.anthropic.com/v1/messages" `
        -Method Post `
        -Headers @{
            "x-api-key" = $env:ANTHROPIC_API_KEY
            "anthropic-version" = "2023-06-01"
            "content-type" = "application/json"
        } `
        -Body $body

    Write-Host "Claude API key works and Claude processed the test question."
    Write-Host "Claude said:"
    Write-Host $response.content[0].text
} catch {
    Write-Host "Claude API key test failed."
    Write-Host "Reason:"
    Write-Host $_.Exception.Message
}
