import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client.js'

export default function PeopleSearch() {
  const [query, setQuery] = useState('')
  const [people, setPeople] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  async function runSearch(e) {
    e?.preventDefault()
    setLoading(true)
    setError(null)
    try {
      const results = query.trim()
        ? await api.searchPeopleBySkill(query.trim())
        : await api.getAllPeople()
      setPeople(results)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Find people by skill</h2>
        <p>
          Search across everyone in the network. Results are ranked by endorsement
          count and years of experience for that skill — a query MySQL can't do
          without stitching together several joins.
        </p>
      </div>

      <form className="search-row" onSubmit={runSearch}>
        <input
          type="text"
          placeholder="e.g. Spring Boot, React, AWS..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Searching…' : 'Search'}
        </button>
      </form>

      {loading && <div className="state-panel">Looking through the graph…</div>}

      {error && !loading && (
        <div className="state-panel error">{error}</div>
      )}

      {!loading && !error && people === null && (
        <div className="state-panel">
          Search a skill above, or hit Search with an empty box to browse everyone.
        </div>
      )}

      {!loading && !error && people?.length === 0 && (
        <div className="state-panel">No one in the network has that skill yet.</div>
      )}

      {!loading && !error && people?.length > 0 && (
        <div className="card-grid">
          {people.map((p) => (
            <Link to={`/person/${p.id}`} className="person-card" key={p.id} style={{ textDecoration: 'none', color: 'inherit', display: 'block' }}>
              <h3>{p.name}</h3>
              <p className="title">{p.title}</p>
              <p className="location">{p.location}</p>
              {p.mentorAvailable && <span className="mentor-badge">Open to mentoring</span>}
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
