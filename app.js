function App() {
    const [output, setOutput] = React.useState("Output");
    const [loading, setLoading] = React.useState(false);
    const [selectedMode, setSelectedMode] = React.useState("");
    const [selectedAircraft, setSelectedAircraft] = React.useState("");
    const [selectedAirports, setSelectedAirports] = React.useState([]);
    const [showMainMenuButton, setShowMainMenuButton] = React.useState(false);

    const aircraftChoices = [
        { label: "Cessna 172", value: "cessna-172" },
        { label: "Beechcraft Bonanza", value: "beechcraft-bonanza" }
    ];

    const airportChoices = ["KLAX", "KSFO", "KDEN", "KSAN"];

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
        setOutput("Output");
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
                        key: airport,
                        onClick: function () {
                            chooseAirport(airport);
                        },
                        disabled: loading || selectedAirports.includes(airport) || selectedAirports.length >= 2
                    },
                    airport
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
        renderMainButtons(),
        renderAircraftButtons(),
        renderAirportButtons(),
        React.createElement("p", null, output),
        renderMainMenuButton()
    );
}

ReactDOM.createRoot(document.getElementById("root")).render(React.createElement(App));
