package com.example.examenparcial1mendozareyesangelemanuel

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCurp: EditText
    private lateinit var etDomicilio: EditText
    private lateinit var etCantidadDeIngresos: EditText
    private lateinit var spTipoDePrestamo: Spinner
    private lateinit var btnValidar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnLimpiar: Button

    private val CHANNEL_ID = "prestamo_notificaciones"
    private val NOTIFICATION_ID = 1
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etNombre = findViewById(R.id.etNombre)
        etApellidos = findViewById(R.id.etApellidos)
        etCurp = findViewById(R.id.etCurp)
        etDomicilio = findViewById(R.id.etDomicilio)
        etCantidadDeIngresos = findViewById(R.id.etCantidadDeIngresos)
        spTipoDePrestamo = findViewById(R.id.spTipoDePrestamo)
        btnValidar = findViewById(R.id.btnValidar)
        btnSalir = findViewById(R.id.btnSalir)
        btnLimpiar = findViewById(R.id.btnLimpiar)

        val tiposDePrestamo = arrayOf("personal", "negocio", "vivienda")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposDePrestamo)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoDePrestamo.adapter = adapter

        btnValidar.setOnClickListener { validarDatos() }
        btnSalir.setOnClickListener { finish() }
        btnLimpiar.setOnClickListener { limpiarCampos() }

        createNotificationChannel()
    }

    private fun validarDatos() {
        val nombre = etNombre.text.toString()
        val apellidos = etApellidos.text.toString()
        val curp = etCurp.text.toString()
        val domicilio = etDomicilio.text.toString()
        val cantidadDeIngresosStr = etCantidadDeIngresos.text.toString()
        val tipoDePrestamo = spTipoDePrestamo.selectedItem.toString()

        if (nombre.isEmpty() || apellidos.isEmpty() || curp.isEmpty() || domicilio.isEmpty() || cantidadDeIngresosStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val cantidadDeIngresos = cantidadDeIngresosStr.toDoubleOrNull()
        if (cantidadDeIngresos == null) {
            Toast.makeText(this, "Por favor, ingrese una cantidad válida de ingresos", Toast.LENGTH_SHORT).show()
            return
        }

        val solicitud = Solicitud(nombre, apellidos, curp, domicilio, cantidadDeIngresos, tipoDePrestamo)
        val esApto = solicitud.validarIngreso()

        if (esApto) {
            Toast.makeText(this, "Felicidades, eres apto para el préstamo $tipoDePrestamo", Toast.LENGTH_SHORT).show()
            checkNotificationPermissionAndShow()
        } else {
            Toast.makeText(this, "Lo sentimos, no eres apto para el préstamo $tipoDePrestamo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkNotificationPermissionAndShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                mostrarNotificacion()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        } else {
            mostrarNotificacion()
        }
    }

    @SuppressLint("MissingPermission")
    private fun mostrarNotificacion() {
        val citaIntent = Intent(this, CitaActivity::class.java)
        val citaPendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, citaIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val beneficiosIntent = Intent(this, BeneficiosActivity::class.java)
        val beneficiosPendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, beneficiosIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Notificaciones Préstamo")
            .setContentText("¿Deseas conocer más o agendar una cita con un asesor?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.baseline_notifications_active_24, "Cita", citaPendingIntent)
            .addAction(R.drawable.baseline_notifications_active_24, "Préstamos", beneficiosPendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun limpiarCampos() {
        etNombre.text.clear()
        etApellidos.text.clear()
        etCurp.text.clear()
        etDomicilio.text.clear()
        etCantidadDeIngresos.text.clear()
        spTipoDePrestamo.setSelection(0)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Préstamo Notificaciones"
            val descriptionText = "Canal para notificaciones de préstamo"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                mostrarNotificacion()
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}