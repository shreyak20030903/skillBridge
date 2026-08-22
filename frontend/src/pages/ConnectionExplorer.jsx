import { useState } from 'react'
import { api } from '../api/client.js'

export default function ConnectionExplorer() {
  const [fromId, setFromId] = useState('p-shreya')
  const [toId, setToId] = useState('p-neha')
  const [via, setVia] = useState('any')
  const [path, setPath] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [notFound, setNotFound] = useState(false)

  async function runSearch(e) {
    e.preventDefault()
    if (!fromId.trim() || !toId.trim()) return
    setLoading(true)
    setError(null)
    setNotFound(false)
    setPath(null)
    try {
      const result = await api.getShortestPath(fromId.trim(), toId.trim(), via)
      setPath(result)
    } catch (err) {
      if (err.message.includes('404')) {
        setNotFound(true)
      } else {
        setError(err.message)
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Indirect connections</h2>
        <p>
          Shortest path between two people through any shared skill, project,
          company, or endorsement — a variable-length traversal that would need
          a recursive CTE in a relational database.
        </p>
      </div>

      <form className="search-row" onSubmit={runSearch}>
        <input
          type="text"
          placeholder="From person ID"
          value={fromId}
          onChange={(e) => setFromId(e.target.value)}
        />
        <input
          type="text"
          placeholder="To person ID"
          value={toId}
          onChange={(e) => setToId(e.target.value)}
        />
        <select value={via} onChange={(e) => setVia(e.target.value)}>
          <option value="any">Any connection</option>
          <option value="work">Work history only (projects/companies)</option>
          <option value="skills">Shared skills only</option>
          <option value="endorsements">Endorsements only</option>
        </select>
        <button type="submit" disabled={loading}>
          {loading ? 'Tracing…' : 'Find path'}
        </button>
      </form>

      {loading && <div className="state-panel">Walking the graph for the shortest path…</div>}

      {error && !loading && <div className="state-panel error">{error}</div>}

      {notFound && !loading && (
        <div className="state-panel">No connection found between those two people.</div>
      )}

      {!loading && !error && !notFound && path === null && (
        <div className="state-panel">
          Enter two person IDs to see how they're connected through the network.
        </div>
      )}

      {!loading && path && (
        <div className="path-row">
          {path.nodeNames.map((name, i) => (
            <span key={i} style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <span className="path-node">
                <span className="label">{path.nodeLabels[i]}</span>
                {name}
              </span>
              {i < path.relationshipTypes.length && (
                <span className="path-arrow">
                  —<span className="rel-chip">{path.relationshipTypes[i]}</span>→
                </span>
              )}
            </span>
          ))}
        </div>
      )}

      {!loading && path && (
        <p style={{ color: 'var(--text-muted)', fontSize: 13 }}>
          {path.hops} hop{path.hops === 1 ? '' : 's'} apart.
        </p>
      )}
    </div>
  )
}
