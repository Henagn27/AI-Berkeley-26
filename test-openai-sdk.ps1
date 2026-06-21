# This file tests whether your OpenAI API key works through the official Java SDK.
# Do not paste your real API key into this file.
#
# One-time setup in PowerShell:
# setx OPENAI_API_KEY "paste_your_key_here"
#
# Then close PowerShell, reopen it, and run:
# cd "C:\Users\dusty\OneDrive\Documents\Hackathons\AI Berkeley 26"
# powershell -ExecutionPolicy Bypass -File .\test-openai-sdk.ps1

if (-not $env:OPENAI_API_KEY) {
    Write-Host "Missing OPENAI_API_KEY."
    Write-Host "Set it first with:"
    Write-Host 'setx OPENAI_API_KEY "paste_your_key_here"'
    Write-Host "Then close and reopen PowerShell."
    exit 1
}

mvn compile exec:java "-Dexec.mainClass=OpenAITest"
