"use client";
import Scoreboard from "./display_components/scoreboard/scoreboard";
import Timeline from "./display_components/timeline/timeline";

import { useEffect, useState } from 'react';

export default function Home() {

  let [teamData, setTeamData] = useState();
  let [highlightData, setHighlightData] = useState();

  useEffect(() => {
    const eventSource = new EventSource('http://localhost:3000/events');
    eventSource.onmessage = event => {
      let data = JSON.parse(event.data);

      if (!data.hasOwnProperty('team_a')) {
        if (data.hasOwnProperty('team_score')) {
          setTeamData(data['team_score']);
        }
        setHighlightData(data);
      } 
      else {
        setTeamData(data);
      }
    }
    eventSource.onerror = error => {
      console.error("EventSource failed:", error);
      eventSource.close();
    }
    return () => {
      eventSource.close();
    };
  }, [])
  
  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <Scoreboard score={teamData}/>
      <Timeline updates={highlightData} />
    </div>
  );
}
