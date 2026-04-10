package com.example.restarentapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.restarentapp.model.BillItem
import com.example.restarentapp.model.Person

@Composable
fun SplitByAmountScreen(items1: SnapshotStateList<BillItem>) {
    val people = listOf(
        Person("Asha", "A"),
        Person("Rahul", "R"),
        Person("John", "J"),
        Person("Priya", "P")
    )
    val totalBill = items1.sumOf { it.price * it.quantity }
    val amounts = remember { mutableStateListOf(*Array(people.size) { 0.0 }) }

    val totalAssigned = amounts.sum()
    val remaining = totalBill - totalAssigned

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6F8))
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Split by Amount",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text("Assign exact amounts to each person", color = Color.Gray)
        Spacer(Modifier.height(14.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF113A5C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("Total Bill: ${formatCurrency(totalBill)}", color = Color(0xFFC7D8E7))
                Text("Assigned: ${formatCurrency(totalAssigned)}", color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    "Remaining: ${formatCurrency(remaining)}",
                    color = if (remaining < 0) Color(0xFFFF8A80) else Color(0xFF8CE0B4)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("People", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        people.forEachIndexed { index, person ->
            Card(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF2B8DBF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(person.short, color = Color.White)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(person.name, fontWeight = FontWeight.Bold)
                    }

                    OutlinedTextField(
                        value = if (amounts[index] == 0.0) "" else amounts[index].toString(),
                        onValueChange = { value ->
                            val newValue = value.toDoubleOrNull() ?: 0.0
                            val tempTotal = totalAssigned - amounts[index] + newValue
                            if (tempTotal <= totalBill) {
                                amounts[index] = newValue
                            }
                        },
                        modifier = Modifier.width(120.dp),
                        label = { Text("₹ Amount") },
                        singleLine = true
                    )
                }
            }
        }

        Text("Items Total", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                items1.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(it.name)
                        Text(formatCurrency(it.price * it.quantity))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Divider()
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text(formatCurrency(totalBill), fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        if (remaining < 0) {
            Text("Total exceeds bill amount", color = Color.Red)
        } else if (remaining == 0.0) {
            Text("Perfect split", color = Color(0xFF1ABC9C), fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun formatCurrency(amount: Double): String = "₹${"%.2f".format(amount)}"
