import { HashRouter, Routes, Route, NavLink } from 'react-router-dom'
import PeopleSearch from './pages/PeopleSearch.jsx'
import MentorFinder from './pages/MentorFinder.jsx'
import ConnectionExplorer from './pages/ConnectionExplorer.jsx'
import PersonDetail from './pages/PersonDetail.jsx'
import ProjectExplorer from './pages/ProjectExplorer.jsx'
import Leaderboard from './pages/Leaderboard.jsx'

export default function App() {
  return (
    <HashRouter>
      <div className="app-shell">
        <header className="topbar">
          <div className="brand">
            <span className="brand-mark">🌉</span>
            <div>
              <h1>SkillBridge</h1>
              <p>Skill &amp; Mentor Matching Network</p>
            </div>
          </div>
          <nav>
            <NavLink to="/" end>People &amp; Skills</NavLink>
            <NavLink to="/mentors">Find a Mentor</NavLink>
            <NavLink to="/projects">Projects</NavLink>
            <NavLink to="/leaderboard">Top Endorsed</NavLink>
            <NavLink to="/connections">Indirect Connections</NavLink>
          </nav>
        </header>

        <main>
          <Routes>
            <Route path="/" element={<PeopleSearch />} />
            <Route path="/mentors" element={<MentorFinder />} />
            <Route path="/projects" element={<ProjectExplorer />} />
            <Route path="/leaderboard" element={<Leaderboard />} />
            <Route path="/connections" element={<ConnectionExplorer />} />
            <Route path="/person/:id" element={<PersonDetail />} />
          </Routes>
        </main>

        <footer>
          Built on CognoDB (graph database) via the official Neo4j driver.
        </footer>
      </div>
    </HashRouter>
  )
}
