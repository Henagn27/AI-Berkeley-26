# Run this file from PowerShell while you are inside the project folder:
# cd "C:\Users\dusty\OneDrive\Documents\Hackathons\AI Berkeley 26"
# powershell -ExecutionPolicy Bypass -File .\run.ps1
#
# When it says "Server running", open this in a browser:
# http://localhost:8000
#
# To stop the server, click this PowerShell window and press Ctrl+C.
# If port 8000 gets stuck, find and stop the listening process:
# netstat -ano | findstr :8000
# taskkill /PID THE_LISTENING_PID /F
#
# The AI path also needs OPENAI_API_KEY set before starting the server.

mvn compile exec:java "-Dexec.mainClass=main"
