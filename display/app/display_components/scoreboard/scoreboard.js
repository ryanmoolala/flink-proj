"use client";
import { useState, useEffect } from 'react';

export default function Scoreboard({ score }) {
    
    const [teamAName, setTeamAName] = useState("Team A");
    const [teamBName, setTeamBName] = useState("Team B");
    const [teamAScore, setTeamAScore] = useState(0);
    const [teamBScore, setTeamBScore] = useState(0);

    useEffect(() => {
        if (score) {
            if (score.hasOwnProperty('team_a')) {
                console.log("Parsed data:", score);
                setTeamAName(score['team_a']);
                setTeamBName(score['team_b']);
            } else {
                console.log("Parsed data:", score);
                setTeamAScore(score[teamAName]);
                setTeamBScore(score[teamBName]);
            }
        }
    }, [score])

    return (
        <div>
            <div>
                <h1>Score</h1>
            </div>

            <div>
                <div>
                    <h2>{teamAName}</h2>
                    <p>{teamAScore}</p> 
                </div>

                <div>
                    <h2>{teamBName}</h2>
                    <p>{teamBScore}</p>
                </div>
            </div>
        </div>
    );
}