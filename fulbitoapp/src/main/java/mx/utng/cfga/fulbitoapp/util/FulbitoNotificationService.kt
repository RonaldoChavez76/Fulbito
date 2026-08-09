package mx.utng.cfga.fulbitoapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * FulbitoNotificationService
 *
 * Gestiona los dos canales de notificación de la app:
 *   - CHANNEL_MATCH_EVENTS : Goles y tarjetas (para jugadores)
 *   - CHANNEL_ADMIN_ALERTS : Partido terminado (para el admin)
 *
 * Uso:
 *   FulbitoNotificationService.init(context)           // en MainActivity.onCreate
 *   FulbitoNotificationService.showGoalNotification(context, "12")
 *   FulbitoNotificationService.showMatchFinished(context, "Real FC", "Atlético", 2, 1)
 */
object FulbitoNotificationService {

    private const val CHANNEL_MATCH_EVENTS = "fulbito_match_events"
    private const val CHANNEL_ADMIN_ALERTS = "fulbito_admin_alerts"

    private var notifIdCounter = 1000

    // ─── Inicialización ────────────────────────────────────────────────────────

    /**
     * Crea los canales de notificación. Debe llamarse en MainActivity.onCreate().
     * En Android < 8.0 los canales no existen pero la función no falla.
     */
    fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Canal 1: Eventos de partido (goles / tarjetas)
            val matchEventsChannel = NotificationChannel(
                CHANNEL_MATCH_EVENTS,
                "Eventos de Partido",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de goles y tarjetas en partidos activos"
                enableVibration(true)
            }

            // Canal 2: Alertas admin (partido terminado)
            val adminAlertsChannel = NotificationChannel(
                CHANNEL_ADMIN_ALERTS,
                "Alertas de Administrador",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando un partido termina"
                enableVibration(true)
            }

            manager.createNotificationChannel(matchEventsChannel)
            manager.createNotificationChannel(adminAlertsChannel)
        }
    }

    // ─── Verificación de permiso ───────────────────────────────────────────────

    private fun canNotify(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // En Android < 13 no se necesita permiso explícito
        }
    }

    // ─── Notificaciones para Jugadores ─────────────────────────────────────────

    /**
     * Muestra notificación de GOL.
     * @param dorsal  Número de dorsal del jugador que anotó.
     */
    fun showGoalNotification(context: Context, dorsal: String) {
        if (!canNotify(context)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_MATCH_EVENTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⚽ ¡GOL REGISTRADO!")
            .setContentText("El jugador #$dorsal anotó un gol en el partido actual.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notifIdCounter++, notification)
    }

    /**
     * Muestra notificación de TARJETA AMARILLA.
     * @param dorsal  Dorsal del jugador amonestado.
     */
    fun showYellowCardNotification(context: Context, dorsal: String) {
        if (!canNotify(context)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_MATCH_EVENTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🟨 Tarjeta Amarilla")
            .setContentText("El jugador #$dorsal recibió una tarjeta amarilla.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notifIdCounter++, notification)
    }

    /**
     * Muestra notificación de TARJETA ROJA.
     * @param dorsal  Dorsal del jugador expulsado.
     */
    fun showRedCardNotification(context: Context, dorsal: String) {
        if (!canNotify(context)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_MATCH_EVENTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🟥 Tarjeta Roja — Expulsión")
            .setContentText("El jugador #$dorsal fue expulsado del partido.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notifIdCounter++, notification)
    }

    // ─── Notificaciones para Administrador ────────────────────────────────────

    /**
     * Muestra notificación de PARTIDO TERMINADO (para el admin).
     * La lista de partidos en el AdminViewModel se refrescará automáticamente
     * al recibir este evento desde MainActivity.
     */
    fun showMatchFinishedNotification(
        context: Context,
        homeTeam: String,
        awayTeam: String,
        homeScore: Int,
        awayScore: Int
    ) {
        if (!canNotify(context)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ADMIN_ALERTS)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle("🏁 Partido Terminado")
            .setContentText("$homeTeam $homeScore — $awayScore $awayTeam")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("El partido entre $homeTeam y $awayTeam ha finalizado.\nMarcador: $homeScore — $awayScore")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notifIdCounter++, notification)
    }
}
