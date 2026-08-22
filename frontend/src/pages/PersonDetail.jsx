import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { api } from '../api/client.js'

export default function PersonDetail() {
  const { id } = useParams()
  const [person, setPerson] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    setLoading(true)
    setError(null)
    api.getPerson(id)
      .then(setPerson)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <div className="state-panel">Loading profile…</div>
  if (error) return <div className="state-panel error">{error}</div>
  if (!person) return <div className="state-panel">Person not found.</div>

  return (
    <div>
      <Link to="/" style={{ fontSize: 13, color: 'var(--text-muted)' }}>&larr; Back to People &amp; Skills</Link>

      <div className="page-header" style={{ marginTop: 16 }}>
        <h2>{person.name}</h2>
        <p>{person.title} · {person.location}</p>
        {person.mentorAvailable && <span className="mentor-badge">Open to mentoring</span>}
        {person.bio && <p style={{ marginTop: 10 }}>{person.bio}</p>}
      </div>

      <Section title="Skills">
        {person.skills?.length ? (
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
            {person.skills.map((hs, i) => (
              <span className="rel-chip" key={i}>
                HAS_SKILL → {hs.skill?.name} ({hs.level})
              </span>
            ))}
          </div>
        ) : <Empty text="No skills listed yet." />}
      </Section>

      <Section title="Projects worked on">
        {person.projects?.length ? (
          <div className="card-grid">
            {person.projects.map((wo, i) => (
              <div className="person-card" key={i}>
                <h3>{wo.project?.name}</h3>
                <p className="title">Role: {wo.role}</p>
                <p className="location">{wo.project?.description}</p>
              </div>
            ))}
          </div>
        ) : <Empty text="No projects listed yet." />}
      </Section>

      <Section title="Companies">
        {person.companies?.length ? (
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
            {person.companies.map((wa, i) => (
              <span className="rel-chip" key={i}>
                WORKED_AT → {wa.company?.name} ({wa.role})
              </span>
            ))}
          </div>
        ) : <Empty text="No companies listed yet." />}
      </Section>

      <Section title="Endorsements received">
        {person.endorsementsReceived?.length ? (
          <div className="card-grid">
            {person.endorsementsReceived.map((e, i) => (
              <div className="person-card" key={i}>
                <h3>{e.skillName}</h3>
                <p className="title">from {e.endorser?.name}</p>
                {e.note && <p className="location">"{e.note}"</p>}
              </div>
            ))}
          </div>
        ) : <Empty text="No endorsements yet." />}
      </Section>
    </div>
  )
}

function Section({ title, children }) {
  return (
    <div style={{ marginBottom: 32 }}>
      <h3 style={{ fontFamily: 'var(--font-display)', fontSize: 15, marginBottom: 12 }}>{title}</h3>
      {children}
    </div>
  )
}

function Empty({ text }) {
  return <p style={{ color: 'var(--text-muted)', fontSize: 13 }}>{text}</p>
}
