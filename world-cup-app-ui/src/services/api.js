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

export async function refreshMatches() {
  return fetchAPI('/matches/refresh');
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

export async function checkDisplayName(displayName) {
  return fetchAPI('/predictions/check-name', {
    method: 'POST',
    body: JSON.stringify({ displayName }),
  });
}

export async function savePrediction(displayName, groupStagePredictions, knockoutPredictions) {
  return fetchAPI('/predictions/save', {
    method: 'POST',
    body: JSON.stringify({
      displayName,
      groupStagePredictions,
      knockoutPredictions,
    }),
  });
}

export async function getPrediction(bracketId) {
  return fetchAPI(`/predictions/${bracketId}`);
}

export async function getAllPredictions() {
  return fetchAPI('/predictions');
}

export async function getLeaderboard() {
  return fetchAPI('/leaderboard');
}
