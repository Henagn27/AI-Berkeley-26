function App() {
    const [output, setOutput] = React.useState("Output");
    const [loading, setLoading] = React.useState(false);
    const [showAircraftChoices, setShowAircraftChoices] = React.useState(false);

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

    return React.createElement(
        "main",
        null,
        React.createElement(
            "button",
            {
                onClick: function () {
                    setShowAircraftChoices(true);
                },
                disabled: loading
            },
            "AI"
        ),
        React.createElement(
            "button",
            {
                onClick: function () {
                    sendActionToJava("non-ai-info");
                },
                disabled: loading
            },
            "Non AI"
        ),
        showAircraftChoices && React.createElement(
            "section",
            null,
            React.createElement(
                "button",
                {
                    onClick: function () {
                        sendActionToJava("ai-cessna-172");
                    },
                    disabled: loading
                },
                "Cessna 172"
            ),
            React.createElement(
                "button",
                {
                    onClick: function () {
                        sendActionToJava("ai-beechcraft-bonanza");
                    },
                    disabled: loading
                },
                "Beechcraft Bonanza"
            )
        ),
        React.createElement("p", null, output)
    );
}

ReactDOM.createRoot(document.getElementById("root")).render(React.createElement(App));
