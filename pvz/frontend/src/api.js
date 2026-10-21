async function request(path, method = 'GET', body) {
  const res = await fetch('/api' + path, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: body ? JSON.stringify(body) : undefined,
  });
  return res.json();
}

export const getGame = () => request('/game');
export const newGame = () => request('/game/new', 'POST');
export const selectPlant = (plant) => request('/game/select', 'POST', { plant });
export const placePlant = (row, col) => request('/game/place', 'POST', { row, col });
export const getPending = () => request('/pending');
