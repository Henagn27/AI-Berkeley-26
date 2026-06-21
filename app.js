function App() {
    const [output, setOutput] = React.useState("Welcome!");
    const [loading, setLoading] = React.useState(false);
    const [selectedMode, setSelectedMode] = React.useState("");
    const [selectedAircraft, setSelectedAircraft] = React.useState("");
    const [selectedAirports, setSelectedAirports] = React.useState([]);
    const [showMainMenuButton, setShowMainMenuButton] = React.useState(false);

    const aircraftChoices = [
        { label: "Cessna 172", value: "cessna-172" },
        { label: "Beechcraft Bonanza", value: "beechcraft-bonanza" },
        { label: "Boeing 737 MAX", value: "boeing-737-max" }
    ];

    const airportChoices = [
        { label: "Los Angeles International (KLAX)", value: "KLAX" },
        { label: "San Francisco International (KSFO)", value: "KSFO" },
        { label: "Denver International (KDEN)", value: "KDEN" },
        { label: "San Diego International (KSAN)", value: "KSAN" },
        { label: "Harry Reid International (KLAS)", value: "KLAS" },
        { label: "Phoenix Sky Harbor International (KPHX)", value: "KPHX" },
        { label: "Seattle-Tacoma International (KSEA)", value: "KSEA" },
        { label: "Chicago O'Hare International (KORD)", value: "KORD" },
        { label: "Dallas/Fort Worth International (KDFW)", value: "KDFW" },
        { label: "Hartsfield-Jackson Atlanta International (KATL)", value: "KATL" },
        { label: "John F. Kennedy International (KJFK)", value: "KJFK" },
        { label: "Miami International (KMIA)", value: "KMIA" }
    ];

    // Sends the completed user choice to Java and displays Java's response.
    async function sendActionToJava(action) {
        setLoading(true);
        setOutput("Asking Java...");

        try {
            const response = await fetch("http://localhost:8000/decide", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ action: action })
            });

            if (!response.ok) {
                throw new Error("Java returned status " + response.status);
            }

            const result = await response.json();
            setOutput(result.message);

            if (action.startsWith("route|ai|")) {
                setShowMainMenuButton(true);
            }
        } catch (error) {
            setOutput("Could not reach Java. Run the server in PowerShell, then open http://localhost:8000");
            console.error(error);
        } finally {
            setLoading(false);
        }
    }

    // Resets the page to the first AI / Non AI choice.
    function returnToMainMenu() {
        setSelectedMode("");
        setSelectedAircraft("");
        setSelectedAirports([]);
        setShowMainMenuButton(false);
        setOutput("Welcome!");
    }

    function chooseMode(mode) {
        setSelectedMode(mode);
        setSelectedAircraft("");
        setSelectedAirports([]);
        setShowMainMenuButton(false);
        setOutput("Choose an aircraft.");
    }

    function chooseAircraft(aircraft) {
        setSelectedAircraft(aircraft);
        setSelectedAirports([]);
        setShowMainMenuButton(false);
        setOutput("Choose two airports to determine the range.");
    }

    // The second airport click completes the route and triggers the backend.
    function chooseAirport(airport) {
        if (selectedAirports.includes(airport) || selectedAirports.length >= 2) {
            return;
        }

        const nextAirports = selectedAirports.concat(airport);
        setSelectedAirports(nextAirports);

        if (nextAirports.length === 2) {
            sendActionToJava(
                "route|" + selectedMode + "|" + selectedAircraft + "|" + nextAirports[0] + "|" + nextAirports[1]
            );
            return;
        }

        setOutput("Choose one more airport.");
    }

    function renderMainButtons() {
        return React.createElement(
            "section",
            null,
            React.createElement(
                "button",
                {
                    onClick: function () {
                        chooseMode("ai");
                    },
                    disabled: loading
                },
                "AI"
            ),
            React.createElement(
                "button",
                {
                    onClick: function () {
                        chooseMode("non-ai");
                    },
                    disabled: loading
                },
                "Non AI"
            )
        );
    }

    function renderAircraftButtons() {
        if (!selectedMode) {
            return null;
        }

        return React.createElement(
            "section",
            null,
            aircraftChoices.map(function (aircraft) {
                return React.createElement(
                    "button",
                    {
                        key: aircraft.value,
                        onClick: function () {
                            chooseAircraft(aircraft.value);
                        },
                        disabled: loading
                    },
                    aircraft.label
                );
            })
        );
    }

    function renderAirportButtons() {
        if (!selectedAircraft) {
            return null;
        }

        return React.createElement(
            "section",
            null,
            airportChoices.map(function (airport) {
                return React.createElement(
                    "button",
                    {
                        key: airport.value,
                        onClick: function () {
                            chooseAirport(airport.value);
                        },
                        disabled: loading || selectedAirports.includes(airport.value) || selectedAirports.length >= 2
                    },
                    airport.label
                );
            })
        );
    }

    function renderMainMenuButton() {
        if (!showMainMenuButton) {
            return null;
        }

        return React.createElement(
            "button",
            {
                onClick: returnToMainMenu,
                disabled: loading
            },
            "Main Menu"
        );
    }

    return React.createElement(
        "main",
        null,
        React.createElement("h1", null, "CloudFlite"),
        React.createElement(
            "p",
            { className: "subtitle" },
            "An AI-assisted flight data manager designed to work alongside official tools for planning decisions and records."
        ),
        renderMainButtons(),
        renderAircraftButtons(),
        renderAirportButtons(),
        React.createElement("div", { className: "output-box" }, output),
        React.createElement(
            "p",
            { className: "disclaimer" },
            "CloudFlite is a demo planning aid and does not replace official aviation tools, weather briefings, regulations, ATC guidance, or pilot judgment."
        ),
        renderMainMenuButton()
    );
}

ReactDOM.createRoot(document.getElementById("root")).render(React.createElement(App));
