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

    return (
        <main>
            <button onClick={() => setShowAircraftChoices(true)} disabled={loading}>
                AI Info
            </button>
            <button onClick={() => sendActionToJava("non-ai-info")} disabled={loading}>
                Non-AI Info
            </button>

            {showAircraftChoices && (
                <section>
                    <button onClick={() => sendActionToJava("ai-cessna-172")} disabled={loading}>
                        Cessna 172
                    </button>
                    <button onClick={() => sendActionToJava("ai-beechcraft-bonbanza")} disabled={loading}>
                        Beechcraft Bonbanza
                    </button>
                </section>
            )}

            <p>{output}</p>
        </main>
    );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
