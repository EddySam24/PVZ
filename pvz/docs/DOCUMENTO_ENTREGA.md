# Plants vs Zombies · Documento de entrega (corte mínimo)

**Equipo:** Jairo Niño, Jesus Paez, Santiago Del Rio, Samuel Pacheco, Angie Paez
**Alcance:** 8 HU · 41 SP · corte jugable = HU-01 a HU-04 = 11 SP

## 1. Tabla resumen

| ID | Título | SP | Estado |
|---|---|---|---|
| HU-01 | Tablero 5 × 3 | 3 | Aceptada |
| HU-02 | Economía inicial | 2 | Aceptada |
| HU-03 | Inventario funcional | 3 | Aceptada |
| HU-04 | Girasol y generación de soles | 3 | Aceptada |
| HU-05 | Disparos del lanzaguisantes | 8 | Pendiente |
| HU-06 | Zombies avanzando por su fila | 8 | Pendiente |
| HU-07 | Ataque de zombies a plantas | 6 | Pendiente |
| HU-08 | Tres oleadas (5, 8 y 12 zombies) | 8 | Pendiente |
| | **Total** | **41** | Aceptadas: 11 SP |

"Aceptada" se confirma en la sustentación con la evidencia de cada HU.

## 2. Detalle de historias

### HU-01 · Tablero 5 × 3 · 3 SP
Como jugador, quiero ver un tablero de 15 celdas seleccionables, para poder plantar en ubicaciones precisas.
1. Existen exactamente 15 celdas visibles (5 columnas × 3 filas).
2. Cada celda se identifica por fila (1-3) y columna (1-5) y responde al clic.
3. La planta colocada en fila F, columna C siempre aparece en esa misma celda, también tras "Nueva partida".

**SP:** 3. Modelo de datos + render + clic, sin reglas de negocio; más que un campo (2) porque la correspondencia fila/columna debe ser estable para todas las HU siguientes. **Evidencia:** demo seleccionando celdas de dos filas distintas.

### HU-02 · Economía inicial · 2 SP
Como jugador, quiero iniciar con 50 soles visibles, para saber cuánto puedo gastar.
1. Toda partida nueva muestra exactamente 50 soles.
2. El contador es el saldo real: baja 50 al plantar un girasol y sube 25 por cada generación.
3. "Nueva partida" restablece 50 soles y vacía el tablero.

**SP:** 2. Un solo número de estado y un contador; sin dependencias, pero define el saldo que usan HU-03/04. **Evidencia:** nueva partida y comprobación del saldo.

### HU-03 · Inventario funcional · 3 SP
Como jugador, quiero ver y seleccionar plantas con su coste, para elegir qué comprar.
1. El inventario muestra Girasol (50) y Lanzaguisantes (100).
2. Al seleccionar una planta queda resaltada y es la única compra pendiente; seleccionar la otra cambia la selección.
3. Plantar sin selección no coloca nada ni cambia el saldo.
4. Los disparos del lanzaguisantes (HU-05) no existen en este corte.

**SP:** 3. Dos elementos, estado de selección y reglas de interacción con el tablero; sube de 2 por el caso "sin selección". **Evidencia:** selección de ambas plantas y visualización de costes.

### HU-04 · Girasol y generación · 3 SP
Como jugador, quiero plantar un girasol que genere soles, para mantener mi economía.
1. Con 50 soles, plantar un girasol en celda vacía deja el saldo en 0.
2. Cada girasol suma 25 soles cada 10 s (t=10 s → 25; t=20 s → 50).
3. Una celda ocupada rechaza otra planta y no cobra.
4. Nueva partida elimina los girasoles: no siguen generando.

**SP:** 3. Combina compra, validación de celda y temporizador; el tiempo es lo que lo hace más complejo que HU-02. **Evidencia:** demo cronometrada de al menos 20 s.

### HU-05 · Disparos · 8 SP · PENDIENTE
Como jugador, quiero que el lanzaguisantes dispare, para dañar zombies.
1. Dispara 1 guisante cada 1,5 s solo si hay un zombie en su fila.
2. Cada guisante causa 20 de daño; un zombie de 100 HP muere con 5 impactos.
3. El guisante desaparece al impactar o salir por la columna 5.

**SP:** 8. Proyectiles, detección por fila, daño y colisión: es la primera mecánica con entidades móviles.

### HU-06 · Zombies · 8 SP · PENDIENTE
Como jugador, quiero ver zombies avanzar por su fila, para sentir amenaza.
1. El zombie aparece en columna 5 de una fila aleatoria (1-3) con 100 HP.
2. Avanza 1 celda cada 5 s hacia la izquierda.
3. Se detiene si hay una planta en la celda siguiente.

**SP:** 8. Aleatoriedad, movimiento por reloj y detención ante obstáculos.

### HU-07 · Ataque a plantas · 6 SP · PENDIENTE
Como jugador, quiero que los zombies destruyan plantas, para que haya riesgo.
1. Cada planta tiene 100 HP; un zombie le quita 10 HP por segundo.
2. A 0 HP la planta se elimina y la celda queda libre.
3. Al quedar libre el zombie reanuda su avance.

**SP:** 6. Depende de HU-06 pero la lógica es acotada (daño por tiempo).

### HU-08 · Oleadas · 8 SP · PENDIENTE
Como jugador, quiero enfrentar 3 oleadas, para tener reto creciente.
1. Oleadas de exactamente 5, 8 y 12 zombies (25 en total).
2. La siguiente oleada empieza 5 s después de que el último zombie muera.
3. Se muestra "Oleada X de 3".

**SP:** 8. Orquestación de temporizadores y estado global; depende de HU-05 a HU-07.

**Suma pendiente:** 8 + 8 + 6 + 8 = 30 SP → 11 + 30 = **41 SP**.

## 3. Bugs y casos límite

| ID | HU | Caso | Esperado |
|---|---|---|---|
| BUG-01 | HU-03/04 | 50 soles, Lanzaguisantes (100), clic en celda | No se coloca, saldo 50, mensaje "Soles insuficientes" |
| LIM-01 | HU-04 | Saldo exacto 50, Girasol | Se planta, saldo 0 |
| LIM-02 | HU-04 | Girasol sobre celda ocupada con saldo 50 | "Celda ocupada.", saldo 50 |
| LIM-03 | HU-04 | Segundo girasol con saldo 0 | "Soles insuficientes", saldo 0 (nunca negativo) |
| LIM-04 | HU-03 | Clic en celda sin planta seleccionada | Mensaje de selección, sin cambios |
| LIM-05 | HU-02 | Nueva partida con girasol activo | Saldo 50 y sin generación posterior |
| LIM-06 | HU-01 | Celda fuera de rango (API: fila 4 / col 6) | Rechazada, sin cambios |
| LIM-07 | HU-03 | Lanzaguisantes con saldo ≥ 100 | No se coloca ni cobra (HU-05 pendiente) |

## 4. Verificación y entrega

**Desarrollo mínimo.** Flujo: tablero → soles → elegir girasol → plantar → generar soles (HU-01 a HU-04).
**Verificación.** `GameServiceTest` cubre éxito y caso límite por historia con reloj simulado. Para cada prueba manual, registrar pasos, datos, resultado esperado y **obtenido** (completar con el resultado real; los de esta tabla son los esperados):

| Prueba | Pasos | Datos | Esperado | Obtenido |
|---|---|---|---|---|
| T-01 HU-01 | Clic en F1·C1 y F3·C5 | Partida nueva | Plantas en esas celdas | _(registrar)_ |
| T-02 HU-02 | Nueva partida | — | Saldo 50 | _(registrar)_ |
| T-03 HU-03 | Seleccionar ambas plantas | — | Costes 50 y 100 visibles | _(registrar)_ |
| T-04 HU-04 | Plantar girasol, esperar 20 s | Saldo 50 | 0 → 25 → 50 | _(registrar)_ |
| T-05 BUG-01 | Lanzaguisantes con 50 | Saldo 50 | "Soles insuficientes", 50 | _(registrar)_ |
| T-06 Integración | Recorrido del README | — | Sin editar código ni inyectar saldo | _(registrar)_ |

**Alcance pendiente.** HU-05 a HU-08 figuran como PENDIENTE en este documento, en Jira y en `GET /api/pending`. No se cuentan roles, diseños ni futuras oleadas como aceptadas.
**Evidencias obligatorias.** Código en repositorio/ZIP con commit presentado · README · relación HU ↔ clave Jira ↔ responsable ↔ código ↔ evidencia · tabla de pruebas anterior con bugs observados · Jira actualizado (criterios, responsables, fechas, estados) · aporte individual de cada integrante.

## 5. Matriz de roles

| HU | SP | Responsable | Apoyo QA | Prueba | Clave Jira |
|---|---|---|---|---|---|
| HU-01 | 3 | Jairo Niño | Angie Paez | T-01 | RIC-__ |
| HU-02 | 2 | Jesus Paez | Angie Paez | T-02 | RIC-__ |
| HU-03 | 3 | Santiago Del Rio | Angie Paez | T-03 | RIC-__ |
| HU-04 | 3 | Samuel Pacheco | Angie Paez | T-04 | RIC-__ |
| BUG-01 | — | Jesus Paez (corrige) · Angie Paez (verifica) | — | T-05 | RIC-__ |
| Integración / README / Jira | — | Angie Paez | Todos | T-06 | RIC-__ |
| HU-05 | 8 | Samuel Pacheco | Angie Paez | pendiente | RIC-__ |
| HU-06 | 8 | Jairo Niño | Angie Paez | pendiente | RIC-__ |
| HU-07 | 6 | Santiago Del Rio | Angie Paez | pendiente | RIC-__ |
| HU-08 | 8 | Jesus Paez | Angie Paez | pendiente | RIC-__ |

SP aceptados por integrante: Jairo 3 · Jesus 2 · Santiago 3 · Samuel 3 · Angie 0 SP (QA, integración y documentación).

## 6. Corrección de incoherencia (requisito 8)
El sprint anterior nombra a Laura, Carlos y Andrés, que no son del equipo. Equivalencias propuestas: Laura (tablero, inventario, zombies) → Jairo y Santiago; Carlos (plantas, disparos, colisiones) → Samuel; Andrés (soles, oleadas, QA) → Jesus y Angie. La explicación del tablero con "Carrito" y la captura actual del tablero RIC corregido deben aportarlas ustedes: no tengo ese dato.
