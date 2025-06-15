import { useEffect, useState } from 'react';

export default function Timeline( { updates }) {

    const [timelineUpdates, setTimelineUpdates] = useState([]);
    const [highlightLogs, setHighlightLogs] = useState([]);
    
    useEffect(() => {
        if (updates) {
            console.log("Timeline updates:", updates);
            console.log(updates['player'])
            console.log(updates['minute'])
            console.log(updates['outcome'])
            setHighlightLogs(prevLogs => [updates.highlight, ...prevLogs]);
        }
    }, [updates])

    return (
        <div style={{ display: "flex", flexDirection: "column", gap: "8px", padding: "8px", border: "1px solid #ccc", borderRadius: "4px" }}>
        {highlightLogs.length === 0 ? (
            <p>No highlight logs available.</p>
        ) : (
            highlightLogs.map((log, index) => (
            <div key={index} style={{ padding: "8px", backgroundColor: "#fefcbd", border: "1px solid #f1e05a", borderRadius: "4px" }}>
                {log}
            </div>
            ))
        )}
        </div>
    )

}