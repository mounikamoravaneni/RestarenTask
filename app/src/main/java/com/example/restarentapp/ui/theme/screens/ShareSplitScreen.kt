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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.restarentapp.model.PersonShare

@Composable
fun ShareSplitScreen(totalBill: Double = 100.0) {
    val people = remember {
        mutableStateListOf(
            PersonShare("1", "Asha"),
            PersonShare("2", "Rahul"),
            PersonShare("3", "John"),
            PersonShare("4", "Kiran")
        )
    }

    val totalShares = people.filter { it.selected && it.shares > 0 }.sumOf { it.shares }
    val grandTotal = if (totalShares == 0) 0.0 else people
        .filter { it.selected && it.shares > 0 }
        .sumOf { (it.shares.toDouble() / totalShares) * totalBill }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6F8))
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            "Share Based Split",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text("Distribute bill using weighted shares", color = Color.Gray)
        Spacer(Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF113A5C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("Total Bill: ${formatCurrency(totalBill)}", color = Color(0xFFC7D8E7))
                Text("Total Shares: $totalShares", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Assigned: ${formatCurrency(grandTotal)}", color = Color(0xFF8CE0B4))
            }
        }

        Spacer(Modifier.height(12.dp))

        people.forEachIndexed { index, person ->
            val personAmount = if (totalShares == 0) 0.0 else (person.shares.toDouble() / totalShares) * totalBill
            val percent = if (totalShares == 0) 0.0 else (person.shares.toDouble() / totalShares) * 100

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
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color(0xFF2B8DBF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(person.name.first().uppercase(), color = Color.White)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(person.name)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {
                                if (person.shares > 0) {
                                    people[index] = person.copy(shares = person.shares - 1)
                                }
                            },
                            enabled = person.selected
                        ) { Text("-") }
                        Text("${person.shares}", modifier = Modifier.padding(horizontal = 8.dp))
                        Button(
                            onClick = { people[index] = person.copy(shares = person.shares + 1) },
                            enabled = person.selected
                        ) { Text("+") }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatCurrency(personAmount), fontWeight = FontWeight.SemiBold)
                        Text("${"%.1f".format(percent)}%")
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
                Text("Final Assigned: ${formatCurrency(grandTotal)}", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun formatCurrency(amount: Double): String = "₹${"%.2f".format(amount)}"
