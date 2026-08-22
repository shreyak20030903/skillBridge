import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client.js'

export default function MentorFinder() {
  const [requesterId, setRequesterId] = useState('p-shreya')
  const [skill, setSkill] = useState('AWS')
  const [mentors, setMentors] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  async function runSearch(e) {
    e.preventDefault()
    if (!requesterId.trim() || !skill.trim()) return
    setLoading(true)
    setError(null)
    try {
      const results = await api.findMentors(requesterId.trim(), skill.trim())
      setMentors(results)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Find a mentor</h2>
        <p>
          A 2-hop traversal: people who already share a project with you, and
          who know the skill you want to learn at an advanced level or higher.
        </p>
      </div>

      <form className="search-row" onSubmit={runSearch}>
        <input
          type="text"
          placeholder="Your person ID (e.g. p-shreya)"
          value={requesterId}
          onChange={(e) => setRequesterId(e.target.value)}
        />
        <input
          type="text"
          placeholder="Skill you want a mentor for"
          value={skill}
          onChange={(e) => setSkill(e.target.value)}
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Searching…' : 'Find mentors'}
        </button>
      </form>

      {loading && <div className="state-panel">Tracing shared-project connections…</div>}

      {error && !loading && <div className="state-panel error">{error}</div>}

      {!loading && !error && mentors === null && (
        <div className="state-panel">
          Enter your person ID and a target skill to see who in your network can mentor you.
        </div>
      )}

      {!loading && !error && mentors?.length === 0 && (
        <div className="state-panel">
          No shared-project mentors found for that skill yet — try browsing People &amp; Skills instead.
        </div>
      )}

      {!loading && !error && mentors?.length > 0 && (
        <div className="card-grid">
          {mentors.map((m, i) => (
            <Link to={`/person/${m.mentor.id}`} className="person-card" key={i} style={{ textDecoration: 'none', color: 'inherit', display: 'block' }}>
              <h3>{m.mentor.name}</h3>
              <p className="title">{m.mentor.title}</p>
              <p className="location">{m.mentor.location}</p>
              <span className="rel-chip">HAS_SKILL → {m.skillLevel}</span>
              <p style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 10 }}>
                Shared project: {m.connectingProjectName} · {m.endorsementCount} endorsement
                {m.endorsementCount === 1 ? '' : 's'}
              </p>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
