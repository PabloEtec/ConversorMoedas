package com.example.conversormoedas

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.conversormoedas.api.ClientApi
import com.example.conversormoedas.model.FinanceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime

class MainActivity : AppCompatActivity() {

    var cotacaoDolar: Double = 0.0
    var cotacaoEuro: Double = 0.0
    var cotacaoLibra: Double = 0.0
    var cotacaoPeso: Double = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val moedas = arrayOf("Dolar", "Euro", "Libras", "Pesos Argentinos")
        val spinnerMoedas = findViewById<Spinner>(R.id.spinnerMoedas)

        val moedasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moedas)
        spinnerMoedas.adapter = moedasAdapter
        moedasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        carregarCotacoes()

        val tempo = findViewById<TextView>(R.id.textViewTempo)
        tempo.text = saudacao()

        val buttConverter = findViewById<Button>(R.id.buttConverter)
        val txtValor = findViewById<EditText>(R.id.txtValor)
        val resultado = findViewById<TextView>(R.id.txtResultado)

        buttConverter.setOnClickListener {
            val textoValor = txtValor.text.toString()

            if (textoValor.isEmpty()) {
                txtValor.error = "Digite um valor"
                return@setOnClickListener
            }

            val valor = textoValor.toDouble()
            val itemSelecionado = spinnerMoedas.selectedItem.toString()

            val valorCotacao = when (itemSelecionado) {
                "Dolar" -> valor * cotacaoDolar
                "Euro" -> valor * cotacaoEuro
                "Libras" -> valor * cotacaoLibra
                "Pesos Argentinos" -> valor * cotacaoPeso
                else -> 0.0
            }

            resultado.text = "O valor convertido é %.2f".format(valorCotacao)
        }
    }

    private fun carregarCotacoes() {
        ClientApi.api.getCotacoes().enqueue(object : Callback<FinanceResponse> {
            override fun onResponse(
                call: Call<FinanceResponse>,
                response: Response<FinanceResponse>
            ) {
                if (response.isSuccessful) {
                    val moedas = response.body()?.results?.currencies
                    cotacaoDolar = moedas?.USD?.buy ?: 0.0
                    cotacaoEuro = moedas?.EUR?.buy ?: 0.0
                    cotacaoLibra = moedas?.GBP?.buy ?: 0.0
                    cotacaoPeso = moedas?.ARS?.buy ?: 0.0
                }
            }

            override fun onFailure(
                call: Call<FinanceResponse>,
                t: Throwable
            ) {
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saudacao(): String {
        val hora = LocalTime.now().hour
        return when {
            hora < 12 -> "Bom dia!"
            hora < 18 -> "Boa tarde!"
            else -> "Boa noite!"
        }
    }
}