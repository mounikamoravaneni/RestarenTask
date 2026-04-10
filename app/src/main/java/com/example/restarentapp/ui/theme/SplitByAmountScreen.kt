package com.example.restarentapp.ui.theme

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import com.example.restarentapp.model.BillItem
import com.example.restarentapp.model.Person

@Composable
fun SplitByAmountScreen(
) {



    val people = listOf(
        Person("Asha", "A"),
        Person("Rahul", "R"),
        Person("John", "J")
    )
    val items = listOf(
        BillItem("1","Veg Momos (6 pcs)", price = 5.0,quantity=2, total = 10.0),
        BillItem("2","Chicken Momos (6 pcs)", price = 5.0,quantity=6, total = 30.0),
        BillItem("3","Paneer Burger", price = 10.0,quantity=6, total = 30.0),
        BillItem("4","Chicken Burger", price = 5.0,quantity=6, total = 30.0),

    )
    val totalBill = items.sumOf { it.price }
    // state for each person
    val amounts = remember {
        mutableStateListOf(*Array(people.size) { 0 })
    }

    val totalAssigned = amounts.sum()
    val remaining = totalBill - totalAssigned

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // 🔹 HEADER
        Text(
            text = "Split by Amount",
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        // 🔹 TOP SUMMARY CARD
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("Total Bill: ₹$totalBill")
                Text("Assigned: ₹$totalAssigned")
                Text(
                    "Remaining: ₹$remaining",
                    color = if (remaining < 0) Color.Red else Color(0xFF1ABC9C)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
                modifier = Modifier
                    .padding(end = 8.dp)
            ) {

                Text("People", fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(8.dp))

                people.forEachIndexed { index, person ->

                    Card(
                        shape = RoundedCornerShape(12.dp),
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
                                        .background(Color(0xFF1ABC9C), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(person.short, color = Color.White)
                                }

                                Spacer(Modifier.width(8.dp))

                                Text(person.name, fontWeight = FontWeight.Bold)
                            }

                            // amount input
                            OutlinedTextField(
                                value = amounts[index].toString(),
                                onValueChange = { value ->

                                    val newValue = value.toIntOrNull() ?: 0
                                    val tempTotal =
                                        totalAssigned - amounts[index] + newValue

                                    if (tempTotal <= totalBill) {
                                        amounts[index] = newValue
                                    }
                                },
                                modifier = Modifier.width(100.dp),
                                label = { Text("₹") },
                                singleLine = true
                            )
                        }
                    }
                }
            }


            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {

                Text("Items Total", fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(Modifier.padding(12.dp)) {

                        items.forEach {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(it.name)
                                Text("₹${it.price}")
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
                            Text("₹$totalBill", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }



        Spacer(Modifier.height(12.dp))

        if (remaining < 0) {
            Text("⚠ Total exceeds bill!", color = Color.Red)
        } else if (remaining.toInt() == 0) {
            Text("✅ Perfect split!", color = Color(0xFF1ABC9C))
        }
    }
}