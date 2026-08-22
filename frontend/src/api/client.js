const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

async function request(path) {
  let res
  try {
    res = await fetch(`${API_BASE}${path}`)
  } catch (err) {
    throw new Error('Could not reach the SkillBridge server. Is the backend running?')
  }
  if (!res.ok) {
    if (res.status === 503) {
      throw new Error('The graph database is temporarily unreachable. Please try again shortly.')
    }
    throw new Error(`Request failed (${res.status})`)
  }
  return res.json()
}

export const api = {
  searchPeopleBySkill: (skill) => request(`/api/people?skill=${encodeURIComponent(skill)}`),
  searchPeopleByName: (name) => request(`/api/people?name=${encodeURIComponent(name)}`),
  getAllPeople: () => request('/api/people'),
  getPerson: (id) => request(`/api/people/${id}`),
  findMentors: (requesterId, skill) =>
    request(`/api/mentors?requesterId=${encodeURIComponent(requesterId)}&skill=${encodeURIComponent(skill)}`),
  getShortestPath: (fromId, toId, via = 'any') =>
    request(`/api/connections/path?fromId=${encodeURIComponent(fromId)}&toId=${encodeURIComponent(toId)}&via=${encodeURIComponent(via)}`),
  getEndorsements: (personId) => request(`/api/people/${personId}/endorsements`),
  getProjects: () => request('/api/projects'),
  getProjectContributors: (projectId) => request(`/api/projects/${projectId}/contributors`),
  getLeaderboard: () => request('/api/endorsements/leaderboard'),
}
