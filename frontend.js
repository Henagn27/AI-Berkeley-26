document.getElementById("button1")
    .addEventListener("click", async function () {
        const output = document.getElementById("output");
        const action = this.dataset.action;

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
            output.textContent = result.message;
        } catch (error) {
            output.textContent = "Could not reach Java. Run the server in PowerShell, then open http://localhost:8000";
            console.error(error);
        }
    });
