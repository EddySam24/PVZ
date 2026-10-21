# Plants vs Zombies · Corte mínimo (HU-01 a HU-04)

Proyecto académico en Java + Spring Boot. Equipo: Jairo Niño, Jesus Paez, Santiago Del Rio, Samuel Pacheco y Angie Paez.

## Versiones
| Herramienta | Versión |
|---|---|
| Java (JDK) | 17 o superior |
| Spring Boot | 3.3.5 |
| Maven | 3.9+ (o el wrapper/Maven integrado de IntelliJ) |
| IntelliJ IDEA | Community o Ultimate |

## Instalación y ejecución en IntelliJ
1. `File > Open…` y selecciona el archivo `pom.xml` (Open as Project). IntelliJ descargará las dependencias.
2. `File > Project Structure > Project SDK`: elige JDK 17+.
3. Abre `src/main/java/com/pvz/game/PvzApplication.java` y pulsa ▶ junto a `main`.
4. Abre http://localhost:8080 en el navegador.

Por terminal: `mvn spring-boot:run` · Jar ejecutable: `mvn clean package && java -jar target/plants-vs-zombies-0.1.0.jar`.
No hay que modificar código durante la sustentación. El frontend (React) ya viene compilado en `src/main/resources/static`, así que **no hace falta Node** para ejecutar el juego.

## Frontend React + CSS (carpeta `frontend/`)
Solo si quieren modificar la interfaz (requiere Node 18+):
- Desarrollo con recarga en vivo: backend corriendo en 8080 y, en `frontend/`, `npm install` y `npm run dev` → http://localhost:5173 (hace proxy de `/api`).
- Recompilar para Spring Boot: `npm run build` (escribe en `src/main/resources/static`).
- Archivos: `App.jsx` (componentes Inventory, Board, Cell, PendingScope), `api.js` (llamadas REST), `styles.css`.

## Recorrido de prueba (integración HU-01 a HU-04)
1. Abrir la página: tablero de 15 celdas, saldo **50**.
2. Pulsar **Girasol (50)** → plantar en cualquier celda → saldo **0**.
3. Esperar 10 s → saldo **25**; 20 s → **50**.
4. Pulsar la misma celda de nuevo → "Celda ocupada." sin cobro.

## Datos de prueba
- Saldo inicial: 50 · Girasol 50 · Lanzaguisantes 100 · +25 soles cada 10 s por girasol.
- Prueba negativa BUG-01: partida nueva → Lanzaguisantes → clic en celda → "Soles insuficientes", saldo 50.
- Prueba positiva: partida nueva → Girasol (saldo exacto 50) → se planta, saldo 0.

## Pruebas automáticas
`mvn test` (o ▶ en `GameServiceTest`). Usan un reloj simulado: no esperan tiempo real.

## API REST
`GET /api/game` · `POST /api/game/new` · `POST /api/game/select {"plant":"SUNFLOWER"}` · `POST /api/game/place {"row":1,"col":1}` · `GET /api/pending`

## Alcance pendiente (NO aceptado)
HU-05 Disparos · HU-06 Zombies · HU-07 Ataques · HU-08 Oleadas (no se muestran en pantalla; solo en `GET /api/pending` y en la documentación). En este corte el lanzaguisantes se puede ver y seleccionar, pero no colocar.
