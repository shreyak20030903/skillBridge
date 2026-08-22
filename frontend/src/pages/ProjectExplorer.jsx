import { useEffect, useState } from 'react'
import { api } from '../api/client.js'
import { Link } from 'react-router-dom'

export default function ProjectExplorer() {
  const [projects, setProjects] = useState(null)
  const [selected, setSelected] = useState(null)
  const [contributors, setContributors] = useState(null)
  const [loading, setLoading] = useState(true)
  const [loadingContributors, setLoadingContributors] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    api.getProjects()
      .then(setProjects)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  function selectProject(project) {
    setSelected(project)
    setLoadingContributors(true)
    api.getProjectContributors(project.id)
      .then(setContributors)
      .catch((err) => setError(err.message))
      .finally(() => setLoadingContributors(false))
  }

  return (
    <div>
      <div className="page-header">
        <h2>Projects</h2>
        <p>
          Browse projects to see who else worked on them — a fast way to find
          people who've solved similar problems to yours.
        </p>
      </div>

      {loading && <div className="state-panel">Loading projects…</div>}
      {error && !loading && <div className="state-panel error">{error}</div>}

      {!loading && !error && projects?.length > 0 && (
        <div className="card-grid" style={{ marginBottom: 28 }}>
          {projects.map((p) => (
            <button
              key={p.id}
              onClick={() => selectProject(p)}
              className="person-card"
              style={{
                textAlign: 'left',
                cursor: 'pointer',
                border: selected?.id === p.id ? '1px solid var(--accent)' : undefined,
              }}
            >
              <h3>{p.name}</h3>
              <p className="location">{p.description}</p>
              <span className="rel-chip">WORKED_ON ← {p.contributorCount} people</span>
            </button>
          ))}
        </div>
      )}

      {selected && (
        <div>
          <h3 style={{ fontFamily: 'var(--font-display)', fontSize: 15, marginBottom: 12 }}>
            Contributors to "{selected.name}"
          </h3>
          {loadingContributors && <div className="state-panel">Loading contributors…</div>}
          {!loadingContributors && contributors?.length === 0 && (
            <div className="state-panel">No one is linked to this project yet.</div>
          )}
          {!loadingContributors && contributors?.length > 0 && (
            <div className="card-grid">
              {contributors.map((c, i) => (
                <Link to={`/person/${c.person.id}`} className="person-card" key={i} style={{ textDecoration: 'none', color: 'inherit', display: 'block' }}>
                  <h3>{c.person.name}</h3>
                  <p className="title">{c.person.title}</p>
                  <p className="location">Role: {c.role}</p>
                </Link>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  )
}
