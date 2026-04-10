package com.example.restarentapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

    val totalBill = items1.sumOf { it.total }

    // ✅ IMPORTANT: keep raw string input
    val amounts = remember {
        mutableStateListOf(*Array(people.size) { "" })
    }

    // ✅ total assigned (safe conversion only here)
    val totalAssigned by remember {
        derivedStateOf {
            amounts.sumOf { it.toDoubleOrNull() ?: 0.0 }
        }
    }

    val remaining = totalBill - totalAssigned

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6F8))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            "Split by Amount",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text("Assign exact amounts", color = Color.Gray)

        Spacer(Modifier.height(14.dp))

        // ================= SUMMARY =================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF113A5C))
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("Total: ${formatCurrency(totalBill)}", color = Color(0xFFC7D8E7))
                Text("Assigned: ${formatCurrency(totalAssigned)}", color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    "Remaining: ${formatCurrency(remaining)}",
                    color = if (remaining < 0) Color.Red else Color(0xFF8CE0B4)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ================= PEOPLE =================
        people.forEachIndexed { index, person ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(14.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
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

                    // ================= INPUT FIELD =================
                    OutlinedTextField(
                        value = amounts[index],
                        onValueChange = { value ->

                            // allow only numbers + decimal
                            if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                amounts[index] = value
                            }
                        },
                        modifier = Modifier
                            .width(140.dp)
                            .height(60.dp),

                        singleLine = true,

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),

                        // ================= DESIGN IMPROVEMENT =================
                        shape = RoundedCornerShape(12.dp),

                        label = {
                            Text("Amount")
                        },

                        leadingIcon = {
                            Text(
                                text = "₹",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2B8DBF)
                            )
                        },

                        placeholder = {
                            Text("0")
                        },

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2B8DBF),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF2B8DBF),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ================= ITEMS =================
        Text("Items Total", fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
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
                        Text(formatCurrency(it.price ))
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

        // ================= STATUS =================
        when {
            remaining < 0 -> Text("Total exceeds bill", color = Color.Red)
            remaining == 0.0 -> Text("Perfect split", color = Color(0xFF1ABC9C))
        }
    }
}

private fun formatCurrency(amount: Double): String =
    "₹${"%.2f".format(amount)}"