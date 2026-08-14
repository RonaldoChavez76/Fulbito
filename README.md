# Tracker de Partidos - Fulbito 360

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Wear%20OS-4285F4?style=for-the-badge&logo=wearos&logoColor=white" />
  <img src="https://img.shields.io/badge/Android%20TV-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white" />
  <img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white" />
  <img src="https://img.shields.io/badge/Socket.io-010101?style=for-the-badge&logo=socket.io&logoColor=white" />
</p>

**Fulbito** es una solución integral y distribuida para la gestión de partidos de fútbol en tiempo real, diseñada específicamente para funcionar en perfecta sincronía a través del ecosistema **Wear OS, Android Móvil y Android TV**. 

### Beneficiario del Proyecto
Esta aplicación está dirigida a **Ligas de fútbol amateur, árbitros, capitanes de equipo y aficionados**. 
Resuelve la ineficiencia de la gestión manual de los partidos proporcionando un flujo digital donde:
- **El árbitro** controla el partido sin sacar el celular, directamente desde su muñeca.
- **Los administradores y jugadores** revisan el rendimiento, las tarjetas y los goles en su teléfono.
- **Los espectadores** viven la experiencia visualizando el marcador del estadio actualizado en vivo desde una pantalla grande.

---

## Diagrama de Arquitectura de Sistemas

El flujo de información de la aplicación es impulsado por eventos de baja latencia a través de WebSockets:

```mermaid
graph TD
    A[Sensor/Reloj Wear OS\nControl del Partido] -->|HTTP POST: Registra Goles/Tarjetas| B[Backend Node.js\nAPI REST & Socket.io]
    B -->|WebSocket: Emite 'match_event' y 'match_updated'| C[Teléfono\nApp Admin y Jugador]
    B -->|WebSocket: Emite 'match_updated'| D[Android TV / Cast\nMarcador en Vivo]
    C -->|HTTP GET/POST: CRUD de Ligas y Equipos| B
    B <-->|Mongoose ODM| E[(MongoDB\nAlmacenamiento Persistente)]
```

---

## Características Principales

- **Cronómetro Inteligente (Wear OS):** Control total del tiempo de juego (pausa, reanudación, cambio de periodos y término del partido) optimizado para bajo consumo de batería.
- **Marcador y Eventos en Vivo:** Registro instantáneo de goles, tarjetas amarillas y rojas con asociación directa a jugadores y equipos.
- **Notificaciones Push Locales:** El teléfono recibe alertas nativas instantáneas vía Socket.io cuando ocurre un gol o el árbitro expulsa a un jugador en el campo.
- **Radar de Rendimiento:** Gráficos de estadísticas dibujados dinámicamente (`Canvas`) para los jugadores, evaluando goles, tarjetas y participación.
- **Gestión de Multimedia:** Subida de escudos y logotipos al servidor usando peticiones `Multipart`.

---

<div align="center">

## Capturas de Pantalla (Ecosistema Completo)

<p align="center">
  <b>Interfaz Wear OS (Reloj Árbitro)</b><br>
  <img src="screenshots/img.png" width="220" style="margin:10px; border-radius: 50%;" />
  <img src="screenshots/img_2.png" width="220" style="margin:10px; border-radius: 50%;" />
  <img src="screenshots/img_3.png" width="220" style="margin:10px; border-radius: 50%;" />
</p>

<p align="center">
  <b>App Móvil (Teléfono Admin/Jugador)</b><br>
  <img src="screenshots/image copy 2.png" width="250" style="margin:10px;" />
  <img src="screenshots/image copy 3.png" width="250" style="margin:10px;" />
  <img src="screenshots/image copy 7.png" width="250" style="margin:10px;" />
</p>

<p align="center">
  <b>Android TV (Marcador en Vivo)</b><br>
  <img src="screenshots/image copy 8.png" width="600" style="margin:10px;" />
</p>

</div>

---

## Estructura del Proyecto Frontend (Árbol de Carpetas)

El proyecto Android es un proyecto multi-módulo que separa claramente la lógica de cada tipo de dispositivo. 

```text
Fulbito/
├── app/                      (Módulo de Wear OS - Reloj)
│   └── src/main/java/.../app/
│       ├── presentation/     (Pantallas e interfaz circular)
│       └── data/             (Lógica de consumo de API)
├── fulbitoapp/               (Módulo de Android Móvil - Teléfono)
│   └── src/main/java/.../fulbitoapp/
│       ├── data/             (Modelos y conexión al servidor)
│       │   └── remote/       (Retrofit, Socket.io y Data Classes)
│       ├── presentation/     (ViewModels y Navegación)
│       │   └── screens/      (Pantallas UI en Jetpack Compose)
│       └── util/             (Servicios como Notificaciones locales)
└── fulbitotv/                (Módulo de Android TV - Pantalla)
    └── src/main/java/.../fulbitotv/
        ├── presentation/     (Pantallas UI en formato panorámico)
        └── data/             (Conexión al servidor)
```

## Función y Detalle de Todos los Archivos Críticos del Frontend

A continuación, se detalla la responsabilidad de todos los archivos Kotlin más críticos que componen la arquitectura MVVM de la plataforma:

### Capa de Datos (Data y Red)
| Archivo | Ubicación Módulo | Descripción / Función Principal |
|---------|------------------|--------------------------------|
| `ApiService.kt` | `data/remote/` | Interfaz maestra de Retrofit. Define más de 15 endpoints HTTP (GET, POST, PUT, DELETE) con subida en Multipart. |
| `SocketManager.kt` | `data/remote/` | Clase Singleton encargada de abrir la conexión bidireccional de WebSockets (`socket.io-client`) y emitir *callbacks* de Kotlin ante eventos como `match_event`. |
| `ApiConfig.kt` | `data/remote/` | Objeto estático (Singleton) que define la dirección IP de host y el puerto centralizado para evitar hardcoding en la app. |
| `Models.kt` | `data/remote/` | Data classes (DTOs) que representan Ligas, Equipos, Jugadores y Partidos devueltos por MongoDB. |
| `AuthModels.kt` | `data/remote/` | Contiene los esquemas JSON de las peticiones y respuestas para el control de inicio de sesión (`AuthRequest`, `AuthResponse`). |

### Capa de Estado y Navegación (ViewModels y Utils)
| Archivo | Ubicación Módulo | Descripción / Función Principal |
|---------|------------------|--------------------------------|
| `AppNavigation.kt` | `presentation/` | Componente central `NavHost`. Inyecta ViewModels de manera reactiva y gestiona el enrutamiento (`NavGraph`) pasando argumentos seguros. |
| `AdminViewModel.kt` | `presentation/` | Gestiona el estado y caché del Administrador (Equipos, Jugadores, Ligas) exponiendo `StateFlow`. Llama corrutinas al backend (`reloadAll`, `uploadImage`). |
| `PlayerViewModel.kt` | `presentation/` | Maneja el estado individual de un jugador autenticado (descarga estadísticas, próximos partidos, info de capitanía). |
| `LoginViewModel.kt` | `presentation/` | Controla los flujos de texto del Login y despacha la mutación segura de inicio de sesión con encriptación. |
| `FulbitoNotificationService.kt`| `util/` | Interfaz nativa de Android que instancia canales de `NotificationManager` para mostrar alertas push en el móvil. |

### Capa de Vistas (Jetpack Compose Screens)
| Archivo | Ubicación Módulo | Descripción / Función Principal |
|---------|------------------|--------------------------------|
| `LoginScreen.kt` | `screens/` | Pantalla de bienvenida. Autentica las credenciales con el Backend antes de enrutar a las pantallas protegidas. |
| `LeagueSelectionScreen.kt` | `screens/` | Panel del administrador para seleccionar o crear el torneo actual y filtrar los equipos. |
| `AdminDashboardScreen.kt` | `screens/` | Menú interactivo central del Administrador que dirige a la gestión de Torneos, Partidos, Equipos y Jugadores. |
| `AdminTeamScreen.kt` | `screens/` | Realiza el CRUD de Equipos y envía los archivos Multipart (logos) seleccionados mediante el `ContentResolver`. |
| `AdminPlayerScreen.kt` | `screens/` | Registra a los jugadores asociándolos directamente a un Equipo y asignándoles un dorsal. |
| `AdminMatchScreen.kt` | `screens/` | Calendariza los encuentros, indicando qué Equipos se enfrentan y a qué hora para sincronizarlo al reloj. |
| `PlayerDashboardScreen.kt` | `screens/` | Vista general del Jugador con información de su próximo partido y un panel especial si tiene el rol de Capitán. |
| `PlayerProfileScreen.kt` | `screens/` | Analiza el desempeño del Jugador mediante algoritmos matemáticos en un lienzo `Canvas`, dibujando un radar de goles y tarjetas. |

---

## Código y Lógica Principal por Dispositivo (Extractos Completos)

### 1. App Móvil (Teléfono) - Reactividad e Inyección de Socket
El teléfono debe actualizarse en tiempo real para mostrar los cambios que el árbitro hace desde el reloj. Usamos Kotlin Flows combinados con un Singleton de **Socket.io**.
*Archivo:* `fulbitoapp/src/main/java/mx/utng/cfga/fulbitoapp/presentation/AdminViewModel.kt`
```kotlin
/**
 * Conecta el dispositivo al servidor WebSocket local y se suscribe a los canales de eventos.
 * Si ocurre un evento de partido (Ej. "GOL"), dispara la notificación push nativa.
 */
fun connectSocket(matchId: String, context: Context) {
    viewModelScope.launch {
        SocketManager.connect()
        
        // Callback asíncrono para interceptar el evento emitido por el reloj
        SocketManager.onMatchEvent { event ->
            // Muta el StateFlow para obligar a Jetpack Compose a repintar la UI
            _eventosPartido.value = _eventosPartido.value + event
            
            // Evaluamos si requiere notificación urgente
            if (event.type == "GOL") {
                FulbitoNotificationService.showNotification(
                    context = context,
                    title = "¡GOL EN VIVO! ⚽",
                    message = "El marcador se acaba de actualizar."
                )
            } else if (event.type == "TARJETA ROJA") {
                FulbitoNotificationService.showNotification(
                    context = context,
                    title = "Expulsión 🟥",
                    message = "Un jugador ha recibido tarjeta roja directa."
                )
            }
        }
    }
}
```

### 2. App Wear OS (Reloj Árbitro) - Corrutinas Persistentes y Cronómetro
El reloj no solo envía peticiones HTTP al registrar goles, sino que maneja un motor de estado complejo y asíncrono para el cronómetro del árbitro (sobreviviendo a apagues de pantalla mediante delegación de Job).
*Archivo:* `app/src/main/java/mx/utng/cfga/fulbitoapp/presentation/viewmodel/MatchControlViewModel.kt`
```kotlin
/**
 * Lanza una corrutina incesante que incrementa el contador de partido cada segundo.
 * Evita la destrucción del estado gracias a viewModelScope y el Job aislado.
 */
fun startTimer() {
    // Si ya existe un timer corriendo, lo cancelamos antes de reiniciar
    timerJob?.cancel()
    
    timerJob = viewModelScope.launch {
        while (true) {
            delay(1000L) // Pausa asíncrona no bloqueante
            // Solo aumentamos el tiempo si el partido no está pausado o en medio tiempo
            if (!isPaused.value && !isHalfTime.value) {
                _elapsedTime.value += 1
                
                // Realizamos respaldos de tiempo al servidor cada 60 segundos 
                // por si el reloj se queda sin batería.
                if (_elapsedTime.value % 60 == 0) {
                    syncTimeWithServer(matchId)
                }
            }
        }
    }
}

/**
 * Consumo asíncrono de API. Mutamos la base de datos de MongoDB desde el reloj
 * indicando el estado exacto del partido.
 */
fun updateMatchStatus(matchId: String, isFinished: Boolean = false) {
    viewModelScope.launch {
        try {
            api.updateMatchStatus(
                id = matchId,
                status = MatchStatusUpdate(
                    homeScore = homeScore.value,
                    awayScore = awayScore.value,
                    elapsedTime = elapsedTime.value,
                    isFinished = isFinished
                )
            )
        } catch (e: Exception) {
            _errorState.value = "Fallo de red en el estadio."
        }
    }
}
```

### 3. Android TV (Pantalla) - Escucha Activa sin Interacción
La televisión actúa como un cliente silencioso que reacciona a los "Pings" del servidor para refrescar todo su StateFlow visual sin que nadie toque un control.
*Archivo:* `fulbitotv/src/main/java/mx/utng/cfga/fulbitoapp/presentation/TvViewModel.kt`
```kotlin
/**
 * Bucle de escucha infinita para la televisión, optimizado para evitar fugas de memoria.
 */
fun listenToMatchUpdates(matchId: String) {
    SocketManager.connect()
    
    // Suscripción al canal 'match_updated'
    SocketManager.onMatchUpdated { updatedMatchId ->
        // Si el evento corresponde a nuestro ID de partido actual, bajamos el JSON fresco
        if (updatedMatchId == matchId) {
            viewModelScope.launch {
                try {
                    // Bajada en segundo plano
                    val freshData = api.getMatchDetails(matchId)
                    
                    // Transición automática del marcador
                    _matchDetails.value = freshData.partido
                    _eventosPartido.value = freshData.eventos
                } catch (e: Exception) {
                    Log.e("TvViewModel", "Error al repintar marcador en TV: ${e.message}")
                }
            }
        }
    }
}
```

---

## Stack Tecnológico

El proyecto está diseñado usando los paradigmas más modernos de desarrollo móvil (Programación Declarativa y Arquitectura Unidireccional).

### Frontend (Mobile, Wearable & TV)
- **Kotlin:** Lenguaje principal 100% moderno y seguro contra nulos.
- **Arquitectura MVVM:** Separación limpia de la vista y la lógica de negocio (Model-View-ViewModel).
- **Jetpack Compose:** Interfaz gráfica 100% declarativa para los tres dispositivos (Mobile, Wear OS y TV).
- **Corrutinas y StateFlow:** Manejo asíncrono avanzado y estado reactivo.
- **Retrofit & OkHttp:** Cliente HTTP seguro para el consumo de la API REST y subida Multipart.
- **Socket.io Client:** Conexión persistente por WebSockets para el tiempo real.
- **Coil:** Carga de imágenes asíncronas optimizada por caché en disco y memoria.

### Backend & Base de Datos
- **Node.js + Express:** Servidor web rápido, no bloqueante y ligero.
- **Socket.io:** Motor de eventos bidireccional de baja latencia.
- **MongoDB + Mongoose:** Base de datos NoSQL flexible, ideal para esquemas de torneos.
- **Bcrypt:** Encriptación de contraseñas por seguridad.

---

## Arquitectura Interna del Frontend (Patrón MVVM)

El código fuente de las aplicaciones Android sigue rigurosamente el patrón **MVVM (Model-View-ViewModel)** recomendado por Google, asegurando un código escalable y fácil de mantener:

1. **Capa de Datos (Data Layer - `data/remote/`)**:
   - `ApiService.kt`: Interfaz de Retrofit que mapea los endpoints REST (GET, POST, PUT, DELETE).
   - `SocketManager.kt`: Singleton que mantiene viva la conexión WebSocket para recibir los eventos en tiempo real (`match_event`, `match_finished`).
   - `Models.kt` / `AuthModels.kt`: Data classes de Kotlin que representan la estructura JSON de la base de datos MongoDB.

2. **Capa de Estado y Lógica (ViewModel Layer - `presentation/`)**:
   - `AdminViewModel.kt`: Mantiene el estado global de Ligas, Equipos y Partidos usando `StateFlow`. Expone funciones asíncronas mediante Corrutinas (`viewModelScope.launch`) para enviar datos al servidor.
   - `PlayerViewModel.kt`: Gestiona la descarga de estadísticas del jugador y procesa la información de capitanía.
   - `TvViewModel.kt`: Se suscribe a los eventos del socket y actualiza el marcador en vivo.

3. **Capa de Presentación (UI Layer - `presentation/screens/`)**:
   - Construida 100% con **Jetpack Compose**. Las pantallas reaccionan automáticamente y se recomponen ante los cambios de los `StateFlow` del ViewModel.
   - Componentes matemáticos avanzados como el `RadarChart` dibujado a la medida usando el `Canvas` nativo de Compose.
   - `AppNavigation.kt`: Define el grafo de navegación usando `Navigation Compose`, asegurando el ruteo seguro entre pantallas.

## Librerías Android Utilizadas

- **Retrofit2 & OkHttp3**: Estándar para peticiones HTTP eficientes. Soporte para peticiones `Multipart` (subida de imágenes).
- **Socket.io-client**: Cliente nativo para Android que permite la recepción de eventos del servidor en cuestión de milisegundos.
- **Coil-Compose**: Sistema moderno de carga de imágenes asíncronas con soporte avanzado para caché en disco y memoria.
- **Material Design 3**: Implementación de temas dinámicos (Claro/Oscuro) y componentes de UI fluidos.

---

## Instalación y Configuración Detallada

Para ejecutar este ecosistema localmente, necesitas **Node.js**, **MongoDB** y **Android Studio**.

### Paso 1: Levantar el Backend (Servidor)
1. Abre una terminal y navega a la carpeta del servidor: 
   ```bash
   cd fulbito-backend
   ```
2. Instala las dependencias necesarias:
   ```bash
   npm install
   ```
3. Ejecuta el servidor (por defecto en el puerto `3000`):
   ```bash
   npm run dev
   ```
   *(Asegúrate de tener el servicio de MongoDB corriendo localmente o proporciona una URI de Mongo Atlas en tu código).*

### Paso 2: Configurar el Frontend (Android)
1. Abre **Android Studio**.
2. Selecciona *Open* y elige la carpeta `fulbit-F/Fulbito/`.
3. Espera a que Gradle sincronice todas las librerías.
4. **Configuración Crítica de Red:** 
   - Ve al archivo `ApiConfig.kt` (ubicado dentro de la carpeta `data/remote/` de cada módulo).
   - Cambia la variable `BASE_URL` por la **dirección IP real de tu computadora** en tu red Wi-Fi (Ej: `http://192.168.1.11:3000/api`). *Nunca dejes localhost si vas a probar en dispositivos físicos.*

### Paso 3: Compilación y Ejecución
- En la barra superior de Android Studio, encontrarás un menú desplegable de "Run Configurations".
- Selecciona el módulo que deseas probar (`app`, `fulbitoapp`, o `fulbitotv`).
- Selecciona el emulador adecuado o conecta tu celular/reloj por cable o depuración inalámbrica.
- Presiona **Run**.

---
<p align="center">
  <i>Desarrollado para el proyecto final de cuatrimestre por:</i><br>
  <b>Santiago Ronaldo Chavez Piñón | César Fernando González Avalos | Leonel Alejandro Torres Perez</b><br>
  <i>UTNG - Grupo GIDS6093</i>
</p>

## Resumen Gráfico de Arquitectura
<img width="905" height="912" alt="imagen" src="https://github.com/user-attachments/assets/6a1ac2b4-9d91-4de0-b15a-ff323a92dbaa" />
