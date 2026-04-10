package com.example.restarentapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.restarentapp.model.PersonPercent

@Composable
fun SplitByPercentageScreen(totalBill: Double = 100.0) {
    val people = remember {
        mutableStateListOf(
            PersonPercent("1", "Asha"),
            PersonPercent("2", "Rahul"),
            PersonPercent("3", "John"),
            PersonPercent("4", "Kiran")
        )
    }

    val totalPercent = people.filter { it.selected }.sumOf { it.percent }
    val totalAssigned = people.filter { it.selected }.sumOf { (it.percent / 100.0) * totalBill }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6F8))
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            "Split by Percentage",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text("Allocate percentages and auto-calculate amounts", color = Color.Gray)
        Spacer(Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF113A5C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("Total Bill: ${formatCurrency(totalBill)}", color = Color(0xFFC7D8E7))
                Text("Total %: $totalPercent%", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Assigned: ${formatCurrency(totalAssigned)}", color = Color(0xFF8CE0B4))
            }
        }

        Spacer(Modifier.height(12.dp))

        people.forEachIndexed { index, person ->
            val amount = if (!person.selected) 0.0 else (person.percent / 100.0) * totalBill

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = person.selected,
                            onCheckedChange = { people[index] = person.copy(selected = it) }
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(person.name)
                    }

                    OutlinedTextField(
                        value = if (person.percent == 0) "" else person.percent.toString(),
                        onValueChange = { value ->
                            val newValue = value.toIntOrNull() ?: 0
                            people[index] = person.copy(percent = newValue)
                        },
                        label = { Text("%") },
                        modifier = Modifier.width(90.dp),
                        singleLine = true
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatCurrency(amount))
                        Text("${person.percent}%")
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("Assigned: ${formatCurrency(totalAssigned)}")
                if (totalPercent > 100) {
                    Text("Exceeds 100%", color = Color.Red)
                } else if (totalPercent == 100) {
                    Text("Perfect Split", color = Color(0xFF1ABC9C), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String = "₹${"%.2f".format(amount)}"
