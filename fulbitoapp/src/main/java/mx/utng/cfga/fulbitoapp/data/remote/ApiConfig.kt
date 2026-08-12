package mx.utng.cfga.fulbitoapp.data.remote

/**
 * Objeto de configuración de red.
 *
 * Centraliza la URL base de conexión al backend Node.js.
 * Debe actualizarse con la IP local de la máquina que ejecuta el servidor
 * cuando se prueba en un dispositivo físico.
 */

object ApiConfig {
    // Cambia esta según dónde estés probando xd
    
    // Para emulador (Android Studio usa 10.0.2.2 para referirse a localhost de la PC)
    // const val BASE_URL = "http://10.0.2.2:3000/api"
    
    // Para dispositivo físico (reemplazar con la IP de la PC en la red local)
    const val BASE_URL = "http://10.29.192.78:3000/api"
}
