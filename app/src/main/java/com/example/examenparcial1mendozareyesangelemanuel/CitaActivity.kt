package com.example.examenparcial1mendozareyesangelemanuel

import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CitaActivity : AppCompatActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var btnAgendar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cita)

        datePicker = findViewById(R.id.datePicker)
        timePicker = findViewById(R.id.timePicker)
        btnAgendar = findViewById(R.id.btnAgendar)

        btnAgendar.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month + 1
            val year = datePicker.year
            val hour = timePicker.hour
            val minute = timePicker.minute

            val fechaHora = "Fecha: $day/$month/$year Hora: $hour:$minute"
            Toast.makeText(this, fechaHora, Toast.LENGTH_LONG).show()
        }
    }
}