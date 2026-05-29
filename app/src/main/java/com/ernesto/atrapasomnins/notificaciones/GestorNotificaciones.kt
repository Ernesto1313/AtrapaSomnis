// Gestiona las notificaciones de recordatorio matutino.
// Usa AlarmManager para programar una alarma diaria a la hora elegida.

package com.ernesto.atrapasomnins.notificaciones

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ernesto.atrapasomnins.R
import java.util.Calendar

// ID del canal de notificaciones
const val CANAL_RECORDATORIO = "canal_recordatorio_suenos"
const val ID_NOTIFICACION = 1001

// BroadcastReceiver que recibe la alarma y lanza la notificación
class ReceptorRecordatorio : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        mostrarNotificacionRecordatorio(context)
    }
}

// Crea el canal de notificaciones (necesario desde Android 8)
fun crearCanalNotificaciones(context: Context) {
    val canal = NotificationChannel(
        CANAL_RECORDATORIO,
        "Recordatorio de sueños",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Recordatorio matutino para registrar tus sueños"
    }
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(canal)
}

// Muestra la notificación de recordatorio
fun mostrarNotificacionRecordatorio(context: Context) {
    val notificacion = NotificationCompat.Builder(context, CANAL_RECORDATORIO)
        .setSmallIcon(android.R.drawable.ic_menu_edit)
        .setContentTitle("¿Qué soñaste anoche? 🌙")
        .setContentText("Abre AtrapaSomnis y registra tu sueño antes de que lo olvides")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    val manager = context.getSystemService(NotificationManager::class.java)
    manager.notify(ID_NOTIFICACION, notificacion)
}

// Programa una alarma diaria a la hora indicada
fun programarRecordatorioDiario(context: Context, hora: Int, minuto: Int) {
    val alarmManager = context.getSystemService(AlarmManager::class.java)

    val intent = Intent(context, ReceptorRecordatorio::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Calculamos cuándo es la próxima alarma
    val calendario = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hora)
        set(Calendar.MINUTE, minuto)
        set(Calendar.SECOND, 0)
        // Si ya ha pasado la hora hoy, la programamos para mañana
        if (before(Calendar.getInstance())) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    // Alarma que se repite cada día
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendario.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

// Cancela el recordatorio si el usuario lo desactiva
fun cancelarRecordatorio(context: Context) {
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val intent = Intent(context, ReceptorRecordatorio::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}
