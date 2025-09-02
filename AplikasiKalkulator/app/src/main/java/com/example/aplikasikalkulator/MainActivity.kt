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

class CalculatorLogic {
    private var displayFormula = ""  // Buat nyimpen formula yang ditampilin
    private var operands = mutableListOf<Double>()  // Buat nyimpen angka-angka
    private var operators = mutableListOf<String>()  // Buat nyimpen operator
    private var currentInput = ""  // Buat nyimpen input saat ini
    private var result: String = ""  // Buat nyimpen hasil akhir

    fun onNumberClick(number: String): String {
        if (result.isNotEmpty() && !result.contains("Error")) {
            // Reset semua kalau ada hasil sebelumnya
            reset()
            if (number == ".") {
                currentInput = "0."
            } else {
                currentInput = number
            }
        } else {
            currentInput += number
        }
        return getCurrentDisplay()
    }

    fun onOperationClick(op: String): String {
        if (result.isNotEmpty() && !result.contains("Error")) {
            // Kalau ada hasil sebelumnya, pakai itu sebagai starting point
            currentInput = result
            result = ""
        }

        if (currentInput.isNotEmpty()) {
            // Simpan angka yang sedang diinput
            operands.add(currentInput.toDoubleOrNull() ?: 0.0)
            currentInput = ""
        } else if (operands.isEmpty()) {
            // Kalau belum ada angka sama sekali, pakai 0
            operands.add(0.0)
        }

        // Simpan operator
        operators.add(op)

        return getCurrentDisplay()
    }

    fun onEqualsClick(): String {
        if (result.isNotEmpty() && !result.contains("Error")) {
            return result
        }

        // Tambahin angka terakhir ke list
        if (currentInput.isNotEmpty()) {
            operands.add(currentInput.toDoubleOrNull() ?: 0.0)
            currentInput = ""
        } else if (operands.isEmpty()) {
            operands.add(0.0)
        }

        // Hitung semua operasi
        if (operands.isNotEmpty() && operators.isNotEmpty()) {
            try {
                var calculationResult = operands[0]

                for (i in operators.indices) {
                    val nextOperand = if (i + 1 < operands.size) operands[i + 1] else 0.0
                    calculationResult = when (operators[i]) {
                        "+" -> calculationResult + nextOperand
                        "-" -> calculationResult - nextOperand
                        "×" -> calculationResult * nextOperand
                        "÷" -> {
                            if (nextOperand != 0.0) {
                                calculationResult / nextOperand
                            } else {
                                throw ArithmeticException("Cannot divide by zero")
                            }
                        }
                        else -> nextOperand
                    }
                }

                result = if (calculationResult == calculationResult.toLong().toDouble()) {
                    calculationResult.toLong().toString()
                } else {
                    String.format("%.8f", calculationResult).trimEnd('0').trimEnd('.')
                }

                // Reset untuk perhitungan berikutnya
                operands.clear()
                operators.clear()

            } catch (e: Exception) {
                result = "Error"
                reset()
            }
        } else if (currentInput.isNotEmpty()) {
            result = currentInput
            currentInput = ""
        } else if (operands.isNotEmpty()) {
            result = operands[0].toString()
            operands.clear()
        }

        return result
    }

    fun onClearClick(): String {
        reset()
        return "0"
    }

    fun onDeleteClick(): String {
        if (result.isNotEmpty() && !result.contains("Error")) {
            reset()
            return "0"
        } else if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            return if (currentInput.isEmpty()) "0" else getCurrentDisplay()
        } else if (operators.isNotEmpty()) {
            operators.removeLast()
            return getCurrentDisplay()
        } else if (operands.isNotEmpty()) {
            operands.removeLast()
            return if (operands.isEmpty()) "0" else getCurrentDisplay()
        }
        return "0"
    }

    private fun reset() {
        operands.clear()
        operators.clear()
        currentInput = ""
        result = ""
    }

    private fun getCurrentDisplay(): String {
        return when {
            result.isNotEmpty() -> result
            else -> {
                val formula = StringBuilder()
                for (i in operands.indices) {
                    val operand = operands[i]
                    val formattedOperand = if (operand == operand.toLong().toDouble()) {
                        operand.toLong().toString()
                    } else {
                        operand.toString()
                    }
                    formula.append(formattedOperand)
                    if (i < operators.size) {
                        formula.append(" ").append(operators[i]).append(" ")
                    }
                }

                if (currentInput.isNotEmpty()) {
                    if (formula.isNotEmpty()) {
                        formula.append(" ").append(currentInput)
                    } else {
                        formula.append(currentInput)
                    }
                } else if (operators.isNotEmpty() && operators.size > operands.size) {
                    val lastOperator = operators.last()
                    formula.append(" ").append(lastOperator)
                }

                if (formula.isEmpty()) {
                    "0"
                } else {
                    formula.toString()
                }
            }
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
