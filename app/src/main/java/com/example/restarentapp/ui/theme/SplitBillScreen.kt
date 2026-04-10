package com.example.restarentapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun SplitBillScreen() {
    val people = listOf("Asha", "Rahul", "John", "Priya")
    val paidState = remember { mutableStateListOf(false, false, false, false) }
    val selectedState = remember {
        mutableStateListOf(false, false, false, false)
    }
    // Item state
    val items = remember {
        mutableStateListOf(
            BillItem("🍔 Burger", 200, 0),
            BillItem("🥤 Drinks", 100, 0),
            BillItem("🍰 Cake", 200, 0)
        )
    }

    val subtotal = items.sumOf { it.price * it.quantity }
    // Apply tax only when there are items
    val tax = if (subtotal > 0) 50 else 0
    val total = subtotal + tax
    // Split only among selected (checked) users
    val selectedCount = selectedState.count { it }

    val perPerson = if (selectedCount > 0) {
        total / selectedCount.toDouble()
    } else 0.0
  //  val perPerson = if (selectedCount > 0) total / selectedCount.toDouble() else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp).systemBarsPadding()
    ) {
        Text(
            text = "Split Bill",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Evenly", "By Item", "By Amount", "Shares", "%").forEachIndexed { index, title ->
                val isSelected = index == 0
                Text(
                    text = title,
                    modifier = Modifier
                        .background(
                            if (isSelected) Color(0xFF1ABC9C) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (isSelected) Color.White else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDFF5EF), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text("${people.size} people splitting ₹$total → ₹${"%.2f".format(perPerson)} each")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Participants
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Participants", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))


                    people.forEachIndexed { index, name ->

                        ParticipantRow(
                            name = name,

                            isSelected = selectedState[index],
                            isPaid = paidState[index],

                            amount = if (selectedState[index]) perPerson else 0.0,

                            // checkbox
                            onSelect = {
                                selectedState[index] = !selectedState[index]
                            },

                            // pay button (only once)
                            onPay = {
                                if (!paidState[index]) {
                                    paidState[index] = true
                                }
                            }
                        )
                    }

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bill Summary
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Bill Summary", fontWeight = FontWeight.Bold)

                items.forEachIndexed { index, item ->
                    ItemRow(
                        item = item,
                        onIncrease = { items[index] = item.copy(quantity = item.quantity + 1) },
                        onDecrease = {
                            if (item.quantity > 0)
                                items[index] = item.copy(quantity = item.quantity - 1)
                        }
                    )
                }

                Divider()

                SummaryRow("Subtotal", "₹$subtotal")
                // Show tax only when applicable
                if (tax > 0) {
                    SummaryRow("Tax", "₹$tax")
                }
                SummaryRow("Total", "₹$total", bold = true)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total: ₹$total")
            val paidTotal = people.indices.sumOf { i ->
                if (selectedState[i] && paidState[i]) perPerson else 0.0
            }

            Text("Paid: ₹${"%.2f".format(paidTotal)}")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    for (i in paidState.indices) paidState[i] = true
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1ABC9C))
            ) {
                Text("Pay All")
            }

            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f)
            ) {
                Text("Settle Later")
            }
        }
    }
}

// Data class
data class BillItem(
    val name: String,
    val price: Int,
    val quantity: Int
)
@Composable
fun ParticipantRow(
    name: String,
    isSelected: Boolean,
    isPaid: Boolean,
    amount: Double,
    onSelect: () -> Unit,
    onPay: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // LEFT
        Row(verticalAlignment = Alignment.CenterVertically) {

            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelect() }
            )

            Text(name)
        }

        // RIGHT
        if (isSelected) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Text("₹${"%.2f".format(amount)}")

                Spacer(modifier = Modifier.width(8.dp))

                if (!isPaid) {
                    Button(onClick = onPay) {
                        Text("Pay")
                    }
                } else {
                    Text(
                        "Paid",
                        color = Color.White,
                        modifier = Modifier
                            .background(Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun ItemRow(item: BillItem, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(item.name)
            Text("₹${item.price} each", fontSize = 12.sp, color = Color.Gray)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("₹${item.price * item.quantity}")

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("-", modifier = Modifier
                    .padding(8.dp)
                    .clickable { onDecrease() })

                Text(item.quantity.toString(), modifier = Modifier.padding(8.dp))

                Text("+", modifier = Modifier
                    .padding(8.dp)
                    .clickable { onIncrease() })
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
    }
}
