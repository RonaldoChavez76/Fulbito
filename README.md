#   Tracker de Partidos para Wear OS

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Wear%20OS-4285F4?style=for-the-badge&logo=wearos&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white" />
  <img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white" />
</p>

**Fulbito** es una solución integral para la gestión de partidos de fútbol en tiempo real, diseñada específicamente para el ecosistema **Wear OS, Android Móvil y Android TV**. 

### 🎯 Beneficiario del Proyecto
Esta aplicación está dirigida a **Ligas de fútbol amateur, árbitros y capitanes de equipo**. Resuelve el problema de la gestión manual del partido al proporcionar una herramienta digital que el árbitro puede usar directamente desde su muñeca (reloj), mientras que los capitanes pueden ver sus estadísticas en el teléfono y los espectadores pueden ver el marcador en tiempo real en una pantalla (Android TV o Cast).

---

##  Diagrama de Arquitectura

El flujo de información de la aplicación funciona de la siguiente manera:

```mermaid
graph LR
    A[⌚ Sensor/Reloj\nWear OS] -->|Registra Goles y Tarjetas| B[☁️ Backend Node.js\nAPI REST & Socket.io]
    B -->|Sincronización| C[📱 Teléfono\nApp Admin y Jugador]
    B -->|Transmisión en Vivo| D[📺 TV / Cast\nMarcador Android TV]
```

---

##  Características Principales

- **⏱️ Cronómetro Inteligente:** Control total del tiempo de juego (pausa, reanudación y cambio de periodos).
- **⚽ Marcador en Vivo:** Registro instantáneo de goles para local y visitante.
- **🟨 Gestión de Sanciones:** Registro rápido de tarjetas amarillas y rojas.
- **📜 Historial de Eventos:** Consulta todos los sucesos del partido cronológicamente.
- **🔄 Sincronización Real-time:** Conexión con API REST y Socket.io para actualizaciones en vivo.
- **🎨 UI Optimizada:** Interfaz adaptada para pantallas circulares, teléfonos y TV con Jetpack Compose.

---

<div align="center">

## 📸 Capturas de Pantalla

### Wear OS (Reloj)
![img.png](img.png)
<img width="525" height="522" alt="image" src="https://github.com/user-attachments/assets/fa928839-b850-4161-ab42-68b4f81a3794" />
![img_2.png](img_2.png)
<img width="469" height="410" alt="image" src="https://github.com/user-attachments/assets/8fa34a4e-a1a9-49b4-a3a4-7d73ae7adb46" />

### App Móvil (Teléfono)
> 
<!-- Reemplaza el link con tu imagen -->
<img width="606" height="1280" alt="image" src="https://github.com/user-attachments/assets/28b8519c-4ee5-4d36-a074-ca5ebf6de96c" />


### Pantalla / Android TV

<!-- Reemplaza el link con tu imagen -->

<img width="1243" height="747" alt="image" src="https://github.com/user-attachments/assets/09624741-5fe9-49e0-9b87-20c2db45963e" />


---

##  Stack Tecnológico

### **Frontend (Mobile/Wearable)**
- **Kotlin:** Lenguaje de última generación para Android.
- **Jetpack Compose for Wear OS:** UI declarativa y moderna.
- **Room Persistence:** Caché local para funcionamiento offline.
- **Retrofit & OkHttp:** Comunicación eficiente con la API.

### **Backend & Base de Datos**
- **Node.js:** Entorno de ejecución para el servidor.
- **Express:** Framework para la creación de la API REST.
- **MongoDB:** Base de datos NoSQL para almacenamiento flexible de encuentros.

---

##  Instalación y Configuración

1. **Backend:** Asegúrate de tener el servidor Node.js corriendo (por defecto en el puerto `3000`).
2. **Clonación:** `git clone https://github.com/tu-usuario/fulbito.git`
3. **Android Studio:** Abre el proyecto y sincroniza Gradle.
4. **Configuración de IP:** Si usas un dispositivo físico, cambia `BASE_URL` en `RetrofitClient.kt` por la IP de tu servidor.
5. **Ejecución:** Selecciona un emulador de Wear OS o un reloj físico y presiona `Run`.

---
<p align="center">
  <i>Desarrollado para el proyecto de la UTNG_Chavez Piñón Santiago Ronaldo_González Avalos César Fernando_Torres Perez Leonel Alejandro</i>

  <i>Grupo GIDS6093</i>
</p>

## Arquitectura de la aplicación
<img width="905" height="912" alt="imagen" src="https://github.com/user-attachments/assets/6a1ac2b4-9d91-4de0-b15a-ff323a92dbaa" />






