import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client.js'

export default function Leaderboard() {
  const [entries, setEntries] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    api.getLeaderboard()
      .then(setEntries)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  return (
    <div>
      <div className="page-header">
        <h2>Top endorsed</h2>
        <p>The most-endorsed people across the whole network, ranked by total endorsements received.</p>
      </div>

      {loading && <div className="state-panel">Tallying endorsements…</div>}
      {error && !loading && <div className="state-panel error">{error}</div>}
      {!loading && !error && entries?.length === 0 && (
        <div className="state-panel">No endorsements in the network yet.</div>
      )}

      {!loading && !error && entries?.length > 0 && (
        <div className="card-grid">
          {entries.map((e, i) => (
            <Link to={`/person/${e.person.id}`} className="person-card" key={i} style={{ textDecoration: 'none', color: 'inherit', display: 'block' }}>
              <h3>#{i + 1} {e.person.name}</h3>
              <p className="title">{e.person.title}</p>
              <span className="rel-chip">ENDORSED ← {e.endorsementCount}</span>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
