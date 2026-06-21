# CloudFlite

CloudFlite is an AI-assisted flight planning prototype. It helps estimate route context, including distance and flight time, then provides an IFR/VFR planning estimate through the OpenAI API.

CloudFlite is a demo planning aid. It does not replace official aviation tools, weather briefings, regulations, ATC guidance, or pilot judgment.

## What It Demonstrates

- React frontend served by a Java backend
- Aircraft and route selection
- Estimated route distance in nautical miles
- Estimated flight time based on aircraft cruise speed
- AI-generated IFR/VFR planning estimate
- Non-AI manual route record path

## Requirements

- Java JDK
- Maven
- PowerShell
- OpenAI API key saved as `OPENAI_API_KEY`

## API Key Setup

Run this once in PowerShell:

```powershell
setx OPENAI_API_KEY "your_api_key_here"
```

Then close PowerShell and open a new PowerShell window.

Do not put your API key in the code, README, screenshots, or GitHub.

## Run The Program

From PowerShell, run:

```powershell
cd "C:\Users\dusty\OneDrive\Documents\Hackathons\AI Berkeley 26"; powershell -ExecutionPolicy Bypass -File .\run.ps1
```

When the server starts, open this in a browser:

```text
http://localhost:8000
```

To stop the server, click the PowerShell window and press:

```text
Ctrl+C
```

## If Port 8000 Is Already In Use

Find the process using port `8000`:

```powershell
netstat -ano | findstr :8000
```

Look for the line that says `LISTENING`. The last number on that line is the process ID.

Stop it with:

```powershell
taskkill /PID THE_LISTENING_PID /F
```

Then run the program again.

## Demo Flow

1. Open `http://localhost:8000`.
2. Choose `AI`.
3. Choose an aircraft.
4. Choose two airports.
5. CloudFlite estimates distance and flight time.
6. CloudFlite asks OpenAI for an IFR/VFR planning estimate.
7. The result appears on the page.

The `Non AI` path shows a manual flight record without calling the OpenAI API.

## Notes

- The project uses Maven, so run it through `run.ps1` instead of `java main`.
- The Java source files are in `src/main/java`.
- The browser frontend is in `app.js` and `webpage.html`.
- Generated files such as `.class` files and Maven `target/` output are ignored by Git.
