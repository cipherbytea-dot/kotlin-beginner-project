package com.example.myfirstapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myfirstapplication.ui.theme.MyFirstApplicationTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

// Data barang disimpan di luar Composable, ini sudah benar!
val dataBarang = arrayOf(Pair("Baju", 50000), Pair("Sepatu", 100000), Pair("Tas", 70000))

// Fungsi untuk menghitung total diskon, ini juga sudah benar!
fun hitungTotalDiskon(): Double {
    var totalDiskon = 0.0

    for (barang in dataBarang) {
        val hargaBarang = barang.second.toDouble()
        if (hargaBarang == 50000.0) {
            totalDiskon += 0.50 * hargaBarang
        } else if (hargaBarang == 100000.0) {
            totalDiskon += 0.20 * hargaBarang
        } else if (hargaBarang == 70000.0) {
            totalDiskon += 0.30 * hargaBarang
        }
    }
    return totalDiskon
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFirstApplicationTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // PENTING: State untuk menampilkan total harga
                    var totalHargaAwal by remember { mutableStateOf(0.0) }
                    var totalDiskon by remember { mutableStateOf(0.0) }
                    var totalHargaAkhir by remember { mutableStateOf(0.0) }
                    var apakahDihitung by remember { mutableStateOf(false) }

                    Button(
                        onClick = {
                            // 1. Hitung total harga awal
                            var sumHarga = 0.0
                            for (barang in dataBarang) {
                                sumHarga += barang.second
                            }
                            totalHargaAwal = sumHarga

                            // 2. Panggil fungsi diskon yang sudah lo buat
                            totalDiskon = hitungTotalDiskon()

                            // 3. Hitung harga akhir
                            totalHargaAkhir = totalHargaAwal - totalDiskon

                            // 4. Ubah status agar hasil tampil
                            apakahDihitung = true
                        }
                    ) {
                        Text(text = "Hitung Total Belanja")
                    }

                    // PENTING: Tampilkan hasil hanya jika sudah dihitung
                    if (apakahDihitung) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Total Harga Awal: Rp ${totalHargaAwal.toInt()}")
                        Text(text = "Total Diskon: Rp ${totalDiskon.toInt()}")
                        Text(text = "Total Harga Akhir: Rp ${totalHargaAkhir.toInt()}")
                    }
                }
            }
        }
    }
}