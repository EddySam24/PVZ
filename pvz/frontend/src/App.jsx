import { useCallback, useEffect, useState } from 'react';
import { getGame, newGame, selectPlant, placePlant } from './api.js';

const ICONS = { SUNFLOWER: '🌻', PEASHOOTER: '🌱' };

function Inventory({ items, onSelect }) {
  return (
    <div className="inventory">
      {items.map((i) => (
        <button
          key={i.id}
          id={`inv-${i.id}`}
          className={`inv-btn${i.selected ? ' selected' : ''}${i.affordable ? '' : ' poor'}`}
          onClick={() => onSelect(i.id)}
        >
          {ICONS[i.id]} {i.name} ({i.cost})
        </button>
      ))}
    </div>
  );
}

function Cell({ row, col, plant, onClick }) {
  return (
    <div className="cell" id={`cell-${row}-${col}`} onClick={onClick}>
      {plant ? (
        <>
          <span className="cell-icon">{ICONS[plant.type]}</span>
          <small>
            {plant.name}
            {plant.secondsToNextSun != null && ` · sol en ${plant.secondsToNextSun}s`}
          </small>
        </>
      ) : (
        <small>F{row} · C{col}</small>
      )}
    </div>
  );
}

function Board({ state, onCellClick }) {
  const cells = [];
  for (let r = 1; r <= state.rows; r++) {
    for (let c = 1; c <= state.cols; c++) {
      const plant = state.plants.find((p) => p.row === r && p.col === c);
      cells.push(<Cell key={`${r}-${c}`} row={r} col={c} plant={plant} onClick={() => onCellClick(r, c)} />);
    }
  }
  return (
    <div className="board" style={{ gridTemplateColumns: `repeat(${state.cols}, 90px)` }}>
      {cells}
    </div>
  );
}

export default function App() {
  const [state, setState] = useState(null);
  const [message, setMessage] = useState({ text: '', ok: true });

  // Aplica el resultado de una accion: estado nuevo + mensaje.
  const apply = (result) => {
    setState(result.state);
    setMessage({ text: result.message, ok: result.ok });
  };

  const refresh = useCallback(async () => setState(await getGame()), []);

  useEffect(() => {
    refresh();
    const timer = setInterval(refresh, 1000); // sincroniza soles generados
    return () => clearInterval(timer);
  }, [refresh]);

  if (!state) return <p className="loading">Cargando…</p>;

  return (
    <main className="app">
      <h1>Plants vs Zombies · corte mínimo</h1>
      <div className="bar">
        <div className="suns" id="suns">Soles: {state.suns}</div>
        <Inventory items={state.inventory} onSelect={async (id) => apply(await selectPlant(id))} />
        <button className="new-game" onClick={async () => apply(await newGame())}>
          Nueva partida
        </button>
      </div>
      <div className={`msg ${message.ok ? 'ok' : 'err'}`}>{message.text}</div>
      <Board state={state} onCellClick={async (r, c) => apply(await placePlant(r, c))} />
    </main>
  );
}
