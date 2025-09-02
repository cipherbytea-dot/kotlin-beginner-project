package com.example.aplikasikalkulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

// LOGIC YANG SEBENERNYA BENER BRO - KALKULATOR STANDARD
class CalculatorLogic {
    private var currentNumber = ""
    private var previousNumber = ""
    private var operation: String = ""
    private var result: String = ""

    fun onNumberClick(number: String): String {
        // Reset jika ada hasil sebelumnya
        if (result.isNotEmpty() && !result.contains("Error")) {
            if (number == ".") {
                currentNumber = "0."
            } else {
                currentNumber = number
            }
            result = ""
        } else {
            currentNumber += number
        }
        return getCurrentDisplay()
    }

    fun onOperationClick(op: String): String {
        // Jika ada hasil sebelumnya, pakai itu sebagai starting number
        if (result.isNotEmpty() && !result.contains("Error")) {
            previousNumber = result
            currentNumber = ""
            operation = op
            result = ""
        }
        // Jika sudah ada previous, current, dan operation, HITUNG DULU baru chaining
        else if (previousNumber.isNotEmpty() && currentNumber.isNotEmpty() && operation.isNotEmpty()) {
            try {
                val prev = previousNumber.toDouble()
                val current = currentNumber.toDouble()
                val resultValue = when (operation) {
                    "+" -> prev + current
                    "-" -> prev - current
                    "×" -> prev * current
                    "÷" -> {
                        if (current != 0.0) prev / current
                        else throw ArithmeticException("Cannot divide by zero")
                    }
                    else -> current
                }

                // SIMPAN HASIL SEBAGAI PREVIOUS UNTUK CHAINING
                previousNumber = if (resultValue == resultValue.toLong().toDouble()) {
                    resultValue.toLong().toString()
                } else {
                    String.format("%.8f", resultValue).trimEnd('0').trimEnd('.')
                }

                currentNumber = ""
                operation = op  // UPDATE OPERATOR BARU
                result = ""
            } catch (e: Exception) {
                result = "Error"
                reset()
            }
        }
        // Jika baru pertama kali input: 2 [×]
        else if (currentNumber.isNotEmpty()) {
            previousNumber = currentNumber
            currentNumber = ""
            operation = op
        }
        // Jika belum ada angka tapi klik operator (misal klik + dulu)
        else if (previousNumber.isEmpty() && currentNumber.isEmpty()) {
            previousNumber = "0"
            operation = op
        }
        // Jika sudah ada previous number dan operation, update operation aja
        else if (previousNumber.isNotEmpty() && currentNumber.isEmpty()) {
            operation = op
        }

        return getCurrentDisplay()
    }

    fun onEqualsClick(): String {
        // Hitung hanya saat klik =
        if (previousNumber.isNotEmpty() && currentNumber.isNotEmpty() && operation.isNotEmpty()) {
            try {
                val prev = previousNumber.toDouble()
                val current = currentNumber.toDouble()
                val resultValue = when (operation) {
                    "+" -> prev + current
                    "-" -> prev - current
                    "×" -> prev * current
                    "÷" -> {
                        if (current != 0.0) prev / current
                        else throw ArithmeticException("Cannot divide by zero")
                    }
                    else -> current
                }

                result = if (resultValue == resultValue.toLong().toDouble()) {
                    resultValue.toLong().toString()
                } else {
                    String.format("%.8f", resultValue).trimEnd('0').trimEnd('.')
                }

                currentNumber = ""
                previousNumber = ""
                operation = ""
            } catch (e: Exception) {
                result = "Error"
                reset()
            }
        }
        return getCurrentDisplay()
    }

    fun onClearClick(): String {
        reset()
        return "0"
    }

    fun onDeleteClick(): String {
        if (result.isNotEmpty() && !result.contains("Error")) {
            reset()
            return "0"
        } else if (currentNumber.isNotEmpty()) {
            currentNumber = currentNumber.dropLast(1)
            return if (currentNumber.isEmpty()) {
                if (previousNumber.isNotEmpty() && operation.isNotEmpty()) {
                    "$previousNumber $operation"
                } else {
                    "0"
                }
            } else {
                getCurrentDisplay()
            }
        } else if (operation.isNotEmpty()) {
            operation = ""
            return previousNumber.ifEmpty { "0" }
        } else if (previousNumber.isNotEmpty()) {
            previousNumber = ""
            return "0"
        }
        return "0"
    }

    private fun reset() {
        currentNumber = ""
        previousNumber = ""
        operation = ""
        result = ""
    }

    private fun getCurrentDisplay(): String {
        return when {
            result.isNotEmpty() -> result
            currentNumber.isNotEmpty() -> {
                if (previousNumber.isNotEmpty() && operation.isNotEmpty()) {
                    "$previousNumber $operation $currentNumber"
                } else {
                    currentNumber
                }
            }
            operation.isNotEmpty() -> {
                if (previousNumber.isNotEmpty()) {
                    "$previousNumber $operation"
                } else {
                    "0 $operation"
                }
            }
            previousNumber.isNotEmpty() -> previousNumber
            else -> "0"
        }
    }
}


// SEMUA UI DALAM COMPOSABLE
@Composable
fun CalculatorScreen() {
    val calculatorLogic = remember { CalculatorLogic() }
    var displayText by remember { mutableStateOf("0") }

    val onNumberClick: (String) -> Unit = { number ->
        displayText = calculatorLogic.onNumberClick(number)
    }

    val onOperationClick: (String) -> Unit = { operation ->
        displayText = calculatorLogic.onOperationClick(operation)
    }

    val onEqualsClick: () -> Unit = {
        displayText = calculatorLogic.onEqualsClick()
    }

    val onClearClick: () -> Unit = {
        displayText = calculatorLogic.onClearClick()
    }

    val onDeleteClick: () -> Unit = {
        displayText = calculatorLogic.onDeleteClick()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2F))
            .padding(16.dp)
            .padding(top = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Display Area
            DisplayArea(displayText)

            Spacer(modifier = Modifier.height(32.dp))

            // Button Grid
            ButtonGrid(
                onNumberClick = onNumberClick,
                onOperationClick = onOperationClick,
                onEqualsClick = onEqualsClick,
                onClearClick = onClearClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun DisplayArea(displayText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color(0xFF2D2D44), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = displayText,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.End,
            maxLines = 1
        )
    }
}

@Composable
private fun ButtonGrid(
    onNumberClick: (String) -> Unit,
    onOperationClick: (String) -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ROW 1: C, ⌫, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "C",
                backgroundColor = Color(0xFF3A3A5A),
                textColor = Color(0xFFFF6B6B),
                onClick = onClearClick,
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "⌫",
                backgroundColor = Color(0xFF3A3A5A),
                textColor = Color(0xFFFFD93D),
                onClick = onDeleteClick,
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "÷",
                backgroundColor = Color(0xFF3A3A5A),
                textColor = Color(0xFF6B6BFF),
                onClick = { onOperationClick("÷") },
                modifier = Modifier.weight(1f)
            )
        }

        // ROW 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "7",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick("7") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "8",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick("8") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "9",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick("9") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "×",
                backgroundColor = Color(0xFF3A3A5A),
                textColor = Color(0xFF6B6BFF),
                onClick = { onOperationClick("×") },
                modifier = Modifier.weight(1f)
            )
        }

        // ROW 3: 4, 5, 6, -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "4",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick("4") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "5",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick("5") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "6",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick("6") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "-",
                backgroundColor = Color(0xFF3A3A5A),
                textColor = Color(0xFF6B6BFF),
                onClick = { onOperationClick("-") },
                modifier = Modifier.weight(1f)
            )
        }

        // ROW 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "1",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick("1") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "2",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick("2") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "3",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick("3") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "+",
                backgroundColor = Color(0xFF3A3A5A),
                textColor = Color(0xFF6B6BFF),
                onClick = { onOperationClick("+") },
                modifier = Modifier.weight(1f)
            )
        }

        // ROW 5: 0, ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "0",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick("0") },
                modifier = Modifier.weight(2f)
            )
            CalculatorButton(
                text = ".",
                backgroundColor = Color(0xFF2D2D44),
                textColor = Color.White,
                onClick = { onNumberClick(".") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "=",
                backgroundColor = Color(0xFF6B6BFF),
                textColor = Color.White,
                onClick = onEqualsClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalculatorButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(70.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}