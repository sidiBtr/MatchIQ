
import { useState, useEffect } from "react";
import Login from "./components/Login.jsx";
import Signup from "./components/Signup.jsx";

function App() {
  // Team list and initial loading
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Selected teams
  const [firstTeamId, setFirstTeamId] = useState("");
  const [secondTeamId, setSecondTeamId] = useState("");

  // Comparison results
  const [comparison, setComparison] = useState(null);
  const [headToHead, setHeadToHead] = useState(null);
  const [comparing, setComparing] = useState(false);
  const [compareError, setCompareError] = useState("");

  // AI-generated analysis
  const [aiAnalysis, setAiAnalysis] = useState("");
  const [aiLoading, setAiLoading] = useState(false);
  const [aiError, setAiError] = useState("");

  // Authentication
  const [accessToken, setAccessToken] = useState(null);
  const [showLogin, setShowLogin] = useState(false);
  const [showSignup, setShowSignup] = useState(false);
  const [signupSuccess, setSignupSuccess] = useState(false);

  // Fetch teams when the component loads
  useEffect(() => {
    fetch("/api/teams")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch teams");
        }
        return response.json();
      })
      .then((data) => {
        setTeams(data);
      })
      .catch((err) => {
        setError(err.message);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  // Handle successful login
  function handleLoginSuccess(token) {
    setAccessToken(token);
    setShowLogin(false);
    setShowSignup(false);
    setSignupSuccess(false);
    setAiError("");
  }

  // Handle successful signup
  function handleSignupSuccess() {
    setShowSignup(false);
    setShowLogin(true);
    setSignupSuccess(true);
  }

  // Handle logout
  function handleLogout() {
    setAccessToken(null);
    setShowLogin(false);
    setShowSignup(false);
    setSignupSuccess(false);
    setAiAnalysis("");
    setAiError("");
  }

  // Clear previous results when team selection changes
  function handleFirstTeamChange(event) {
    setFirstTeamId(event.target.value);
    setComparison(null);
    setHeadToHead(null);
    setCompareError("");
    setAiAnalysis("");
    setAiError("");
  }

  function handleSecondTeamChange(event) {
    setSecondTeamId(event.target.value);
    setComparison(null);
    setHeadToHead(null);
    setCompareError("");
    setAiAnalysis("");
    setAiError("");
  }

  // Fetch comparison and head-to-head data
  async function handleCompare() {
    if (!firstTeamId || !secondTeamId || firstTeamId === secondTeamId) {
      return;
    }

    setComparing(true);
    setCompareError("");
    setComparison(null);
    setHeadToHead(null);
    setAiAnalysis("");
    setAiError("");

    try {
      const comparisonUrl =
        `/api/teams/compare?firstTeamId=${firstTeamId}&secondTeamId=${secondTeamId}`;

      const headToHeadUrl =
        `/api/teams/head-to-head?firstTeamId=${firstTeamId}&secondTeamId=${secondTeamId}`;

      const [comparisonResponse, headToHeadResponse] = await Promise.all([
        fetch(comparisonUrl),
        fetch(headToHeadUrl),
      ]);

      if (!comparisonResponse.ok || !headToHeadResponse.ok) {
        throw new Error("Failed to load team comparison");
      }

      const [comparisonData, headToHeadData] = await Promise.all([
        comparisonResponse.json(),
        headToHeadResponse.json(),
      ]);

      setComparison(comparisonData);
      setHeadToHead(headToHeadData);
    } catch (err) {
      setCompareError(err.message);
    } finally {
      setComparing(false);
    }
  }

  // Generate AI analysis for selected teams
  async function handleGenerateAnalysis() {
    if (!comparison || aiLoading) return;

    // Require login before generating AI analysis
    if (!accessToken) {
      setShowLogin(true);
      setShowSignup(false);
      setSignupSuccess(false);
      setAiError("Please log in to generate AI analysis.");
      return;
    }

    setAiLoading(true);
    setAiError("");
    setAiAnalysis("");

    try {
      const url =
        `/api/ai/analyze?firstTeamId=${firstTeamId}&secondTeamId=${secondTeamId}`;

      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });

      if (response.status === 401) {
        setAccessToken(null);
        setShowLogin(true);
        setShowSignup(false);

        throw new Error(
          "Your session has expired. Please log in again."
        );
      }

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);

        throw new Error(
          errorData?.error ||
            "Could not generate AI analysis. Please try again."
        );
      }

      const data = await response.json();

      if (typeof data.analysis !== "string" || !data.analysis.trim()) {
        throw new Error("The AI service returned an empty analysis.");
      }

      setAiAnalysis(data.analysis);
    } catch (err) {
      setAiError(err.message);
    } finally {
      setAiLoading(false);
    }
  }

  // Helper function for recent form colors
  function getFormColor(result) {
    if (result === "W") return "bg-green-600";
    if (result === "D") return "bg-slate-500";
    return "bg-red-600";
  }

  return (
    <div className="min-h-screen bg-slate-100">
      {/* Header */}
      <header className="bg-slate-900 p-6 text-white">
        <div className="mx-auto flex max-w-5xl items-center justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold">MatchIQ</h1>
            <p className="mt-1 text-slate-300">
              AI-Powered Soccer Analytics
            </p>
          </div>

          {/* Authentication buttons */}
          <div className="flex items-center gap-3">
            {!accessToken ? (
              <>
                <button
                  type="button"
                  onClick={() => {
                    setShowLogin(!showLogin);
                    setShowSignup(false);
                    setSignupSuccess(false);
                  }}
                  className="rounded-lg bg-slate-700 px-4 py-2 font-medium text-white hover:bg-slate-600"
                >
                  {showLogin ? "Cancel" : "Log in"}
                </button>

                <button
                  type="button"
                  onClick={() => {
                    setShowSignup(!showSignup);
                    setShowLogin(false);
                    setSignupSuccess(false);
                  }}
                  className="rounded-lg bg-blue-600 px-4 py-2 font-medium text-white hover:bg-blue-700"
                >
                  {showSignup ? "Cancel" : "Sign up"}
                </button>
              </>
            ) : (
              <button
                type="button"
                onClick={handleLogout}
                className="rounded-lg bg-slate-700 px-5 py-2 font-medium text-white hover:bg-slate-600"
              >
                Log out
              </button>
            )}
          </div>
        </div>
      </header>

      {/* Main content */}
      <main className="mx-auto max-w-5xl p-4 sm:p-8">
        {/* Login form */}
        {showLogin && !accessToken && (
          <div className="mb-8">
            {signupSuccess && (
              <p
                role="status"
                className="mb-4 rounded-lg bg-green-50 p-3 text-green-800"
              >
                Account created successfully! Please log in.
              </p>
            )}

            <Login onLoginSuccess={handleLoginSuccess} />
          </div>
        )}

        {/* Signup form */}
        {showSignup && !accessToken && (
          <div className="mb-8">
            <Signup onSignupSuccess={handleSignupSuccess} />
          </div>
        )}

        {loading && (
          <p className="text-slate-600">Loading teams...</p>
        )}

        {error && (
          <p className="text-red-600">{error}</p>
        )}

        {!loading && !error && (
          <>
            {/* Team selection */}
            <div className="rounded-xl bg-white p-6 shadow">
              <h2 className="mb-3 text-2xl font-bold">
                Compare Premier League Teams
              </h2>

              <p className="mb-6 text-slate-600">
                Select two teams to compare their season statistics,
                recent form, and head-to-head performance.
              </p>

              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                {/* First team */}
                <div>
                  <label
                    htmlFor="firstTeam"
                    className="mb-2 block font-medium"
                  >
                    First Team
                  </label>

                  <select
                    id="firstTeam"
                    value={firstTeamId}
                    onChange={handleFirstTeamChange}
                    disabled={aiLoading}
                    className="w-full rounded-lg border border-slate-300 bg-white p-3"
                  >
                    <option value="">Select first team</option>

                    {teams.map((team) => (
                      <option key={team.id} value={team.id}>
                        {team.name}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Second team */}
                <div>
                  <label
                    htmlFor="secondTeam"
                    className="mb-2 block font-medium"
                  >
                    Second Team
                  </label>

                  <select
                    id="secondTeam"
                    value={secondTeamId}
                    onChange={handleSecondTeamChange}
                    disabled={aiLoading}
                    className="w-full rounded-lg border border-slate-300 bg-white p-3"
                  >
                    <option value="">Select second team</option>

                    {teams.map((team) => (
                      <option key={team.id} value={team.id}>
                        {team.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Compare button */}
              <button
                type="button"
                onClick={handleCompare}
                disabled={
                  !firstTeamId ||
                  !secondTeamId ||
                  firstTeamId === secondTeamId ||
                  comparing ||
                  aiLoading
                }
                className="mt-6 rounded-lg bg-slate-900 px-6 py-3 font-medium text-white hover:bg-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {comparing ? "Comparing..." : "Compare Teams"}
              </button>

              {compareError && (
                <p className="mt-4 text-red-600">
                  {compareError}
                </p>
              )}
            </div>

            {/* Season statistics and recent form */}
            {comparison && (
              <div className="mt-8 rounded-xl bg-white p-6 shadow">
                <h2 className="mb-6 text-2xl font-bold">
                  Team Comparison
                </h2>

                {/* Team names */}
                <div className="mb-4 grid grid-cols-2 gap-4 text-center">
                  <h3 className="text-xl font-bold text-blue-800">
                    {comparison.firstTeam.teamName}
                  </h3>

                  <h3 className="text-xl font-bold text-blue-800">
                    {comparison.secondTeam.teamName}
                  </h3>
                </div>

                {/* Season statistics */}
                {[
                  ["Matches Played", "matchesPlayed"],
                  ["Wins", "wins"],
                  ["Draws", "draws"],
                  ["Losses", "losses"],
                  ["Goals Scored", "goalsScored"],
                  ["Goals Conceded", "goalsConceded"],
                  ["Goal Difference", "goalDifference"],
                ].map(([label, key]) => (
                  <div
                    key={key}
                    className="grid grid-cols-3 items-center border-b border-slate-200 py-4 text-center"
                  >
                    <span className="font-semibold">
                      {comparison.firstTeam[key]}
                    </span>

                    <span className="text-sm text-slate-500">
                      {label}
                    </span>

                    <span className="font-semibold">
                      {comparison.secondTeam[key]}
                    </span>
                  </div>
                ))}

                {/* Win percentage */}
                <div className="grid grid-cols-3 items-center border-b border-slate-200 py-4 text-center">
                  <span className="font-semibold">
                    {comparison.firstTeam.winPercentage.toFixed(1)}%
                  </span>

                  <span className="text-sm text-slate-500">
                    Win Percentage
                  </span>

                  <span className="font-semibold">
                    {comparison.secondTeam.winPercentage.toFixed(1)}%
                  </span>
                </div>

                {/* Recent form */}
                <div className="mt-8 grid grid-cols-1 gap-6 sm:grid-cols-2">
                  <div className="text-center">
                    <h4 className="mb-3 font-semibold">
                      {comparison.firstTeam.teamName} Recent Form
                    </h4>

                    <div className="flex flex-wrap justify-center gap-2">
                      {comparison.firstTeamForm.recentForm.map(
                        (result, index) => (
                          <span
                            key={index}
                            className={`rounded px-3 py-2 text-sm font-bold text-white ${getFormColor(result)}`}
                          >
                            {result}
                          </span>
                        )
                      )}
                    </div>
                  </div>

                  <div className="text-center">
                    <h4 className="mb-3 font-semibold">
                      {comparison.secondTeam.teamName} Recent Form
                    </h4>

                    <div className="flex flex-wrap justify-center gap-2">
                      {comparison.secondTeamForm.recentForm.map(
                        (result, index) => (
                          <span
                            key={index}
                            className={`rounded px-3 py-2 text-sm font-bold text-white ${getFormColor(result)}`}
                          >
                            {result}
                          </span>
                        )
                      )}
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Head-to-head results */}
            {headToHead && (
              <div className="mt-8 rounded-xl bg-white p-6 shadow">
                <h2 className="mb-3 text-2xl font-bold">
                  Head-to-Head Results
                </h2>

                <p className="mb-6 text-slate-600">
                  Meetings between {headToHead.firstTeamName}
                  {" "}and {headToHead.secondTeamName}
                  {" "}in the imported season.
                </p>

                {/* Wins and draws */}
                <div className="grid grid-cols-3 gap-3 text-center">
                  <div className="rounded-lg bg-blue-50 p-4">
                    <p className="text-3xl font-bold text-blue-800">
                      {headToHead.firstTeamWins}
                    </p>

                    <p className="mt-2 text-sm">
                      {headToHead.firstTeamName} Wins
                    </p>
                  </div>

                  <div className="rounded-lg bg-slate-100 p-4">
                    <p className="text-3xl font-bold">
                      {headToHead.draws}
                    </p>

                    <p className="mt-2 text-sm">
                      Draws
                    </p>
                  </div>

                  <div className="rounded-lg bg-blue-50 p-4">
                    <p className="text-3xl font-bold text-blue-800">
                      {headToHead.secondTeamWins}
                    </p>

                    <p className="mt-2 text-sm">
                      {headToHead.secondTeamName} Wins
                    </p>
                  </div>
                </div>

                {/* Additional H2H statistics */}
                <div className="mt-6 border-t border-slate-200 pt-4">
                  <div className="flex justify-between py-2">
                    <span>Matches Played</span>
                    <span className="font-semibold">
                      {headToHead.matchesPlayed}
                    </span>
                  </div>

                  <div className="flex justify-between py-2">
                    <span>
                      {headToHead.firstTeamName} Goals
                    </span>
                    <span className="font-semibold">
                      {headToHead.firstTeamGoals}
                    </span>
                  </div>

                  <div className="flex justify-between py-2">
                    <span>
                      {headToHead.secondTeamName} Goals
                    </span>
                    <span className="font-semibold">
                      {headToHead.secondTeamGoals}
                    </span>
                  </div>
                </div>
              </div>
            )}

            {/* AI analysis */}
            {comparison && (
              <section className="mt-8 rounded-xl bg-white p-6 shadow">
                <h2 className="text-2xl font-bold">
                  AI Team Analysis
                </h2>

                <p className="mt-2 text-slate-600">
                  Get an AI-generated explanation of the 2024–25 season statistics
                  for {comparison.firstTeam.teamName} and {comparison.secondTeam.teamName}.
                </p>

                {!accessToken && (
                  <p className="mt-4 text-sm text-blue-700">
                    Log in to generate AI-powered analysis.
                    Team comparisons are available without an account.
                  </p>
                )}

                <button
                  type="button"
                  onClick={handleGenerateAnalysis}
                  disabled={aiLoading || comparing}
                  className="mt-5 rounded-lg bg-blue-700 px-6 py-3 font-medium text-white hover:bg-blue-600 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {aiLoading
                    ? "Generating analysis..."
                    : "Generate AI Analysis"}
                </button>

                {aiLoading && (
                  <p role="status" className="mt-4 text-slate-600">
                    Analyzing the teams. This may take a few seconds...
                  </p>
                )}

                {aiError && (
                  <p role="alert" className="mt-4 text-red-600">
                    {aiError}
                  </p>
                )}

                {aiAnalysis && (
                  <div className="mt-6 rounded-lg border border-blue-100 bg-blue-50 p-5">
                    <h3 className="mb-3 text-lg font-semibold text-blue-900">
                      AI-Generated Comparison
                    </h3>

                    <p className="whitespace-pre-line leading-7 text-slate-800">
                      {aiAnalysis}
                    </p>

                    <p className="mt-4 text-xs text-slate-500">
                      AI-generated interpretation; verify conclusions against
                      the statistics above.
                    </p>
                  </div>
                )}
              </section>
            )}
          </>
        )}
      </main>
    </div>
  );
}

export default App;
