package mx.utng.cfga.fulbitoapp.data.remote

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

/**
 * SocketManager: Singleton que mantiene la conexión Socket.io activa
 * mientras la app está en primer plano.
 *
 * Escucha dos eventos del backend:
 *  - 'match_event'    → gol o tarjeta registrada (notifica al jugador)
 *  - 'match_finished' → partido terminado (notifica al admin)
 */
object SocketManager {

    private const val TAG = "SocketManager"
    private var socket: Socket? = null

    /** Conecta al servidor Socket.io. Llama desde MainActivity.onCreate() */
    fun connect() {
        if (socket?.connected() == true) return

        try {
            // Derivar la URL base del socket desde ApiConfig (quitar "/api")
            val socketUrl = ApiConfig.BASE_URL.removeSuffix("/api")
            val options = IO.Options.builder()
                .setTransports(arrayOf("websocket"))
                .build()

            socket = IO.socket(socketUrl, options)
            socket?.connect()

            socket?.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "✅ Conectado a Socket.io: $socketUrl")
            }
            socket?.on(Socket.EVENT_DISCONNECT) {
                Log.d(TAG, "❌ Desconectado de Socket.io")
            }
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e(TAG, "⚠️ Error de conexión Socket.io: ${args.firstOrNull()}")
            }

            Log.d(TAG, "Intentando conexión a: $socketUrl")
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear socket: ${e.message}")
        }
    }

    /** Desconecta el socket. Llama desde MainActivity.onDestroy() */
    fun disconnect() {
        socket?.disconnect()
        socket?.off()
        socket = null
        Log.d(TAG, "Socket desconectado y limpiado")
    }

    /**
     * Registra un listener para eventos de partido (gol / tarjeta).
     * El backend emite: { matchId, type, teamId, playerDorsal }
     */
    fun onMatchEvent(callback: (JSONObject) -> Unit) {
        socket?.on("match_event") { args ->
            try {
                val data = args.firstOrNull() as? JSONObject ?: return@on
                Log.d(TAG, "match_event recibido: $data")
                callback(data)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing match_event: ${e.message}")
            }
        }
    }

    /**
     * Registra un listener para cuando un partido termina.
     * El backend emite: { matchId, homeTeam, awayTeam, homeScore, awayScore }
     */
    fun onMatchFinished(callback: (JSONObject) -> Unit) {
        socket?.on("match_finished") { args ->
            try {
                val data = args.firstOrNull() as? JSONObject ?: return@on
                Log.d(TAG, "match_finished recibido: $data")
                callback(data)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing match_finished: ${e.message}")
            }
        }
    }

    /** Elimina todos los listeners (evita duplicados si se llama varias veces) */
    fun clearListeners() {
        socket?.off("match_event")
        socket?.off("match_finished")
    }

    val isConnected: Boolean
        get() = socket?.connected() == true
}
