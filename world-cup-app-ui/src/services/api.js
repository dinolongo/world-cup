const API_BASE_URL = 'https://world-cup-yzg0.onrender.com/api';

async function fetchAPI(endpoint, options = {}) {
  const url = `${API_BASE_URL}${endpoint}`;
  
  try {
    const response = await fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error(`API call failed: ${endpoint}`, error);
    throw error;
  }
}

export async function getMatches() {
  return fetchAPI('/matches');
}

export async function getMatchById(id) {
  return fetchAPI(`/matches/${id}`);
}

export async function getTeams() {
  return fetchAPI('/teams');
}

export async function getTeamById(id) {
  return fetchAPI(`/teams/${id}`);
}

export async function getGroups() {
  return fetchAPI('/groups');
}

export async function getGroupByName(groupName) {
  return fetchAPI(`/groups/${groupName}`);
}
