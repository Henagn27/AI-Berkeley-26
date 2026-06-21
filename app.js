function App() {
    const [output, setOutput] = React.useState("Output");
    const [loading, setLoading] = React.useState(false);
    const [selectedMode, setSelectedMode] = React.useState("");
    const [selectedAircraft, setSelectedAircraft] = React.useState("");
    const [selectedAirports, setSelectedAirports] = React.useState([]);

    const aircraftChoices = [
        { label: "Cessna 172", value: "cessna-172" },
        { label: "Beechcraft Bonanza", value: "beechcraft-bonanza" }
    ];

    const airportChoices = ["KLAX", "KSFO", "KDEN", "KSAN"];

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
        } catch (error) {
            setOutput("Could not reach Java. Run the server in PowerShell, then open http://localhost:8000");
            console.error(error);
        } finally {
            setLoading(false);
        }
    }

    function chooseMode(mode) {
        setSelectedMode(mode);
        setSelectedAircraft("");
        setSelectedAirports([]);
        setOutput("Choose an aircraft.");
    }

    function chooseAircraft(aircraft) {
        setSelectedAircraft(aircraft);
        setSelectedAirports([]);
        setOutput("Choose two airports to determine the range.");
    }

    function chooseAirport(airport) {
        if (selectedAirports.includes(airport)) {
            return;
        }

        if (selectedAirports.length >= 2) {
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

    return React.createElement(
        "main",
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
        ),
        selectedMode && React.createElement(
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
        ),
        selectedAircraft && React.createElement(
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
        ),
        React.createElement("p", null, output)
    );
}

ReactDOM.createRoot(document.getElementById("root")).render(React.createElement(App));
