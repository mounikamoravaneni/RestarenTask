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
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.sp
import com.example.restarentapp.model.BillItem
import kotlin.math.min

@Composable
fun SplitBillScreen(items1: SnapshotStateList<BillItem>) {
    val people = listOf("Asha", "Rahul", "John", "Priya")
    val paidState = remember { mutableStateListOf(false, false, false, false) }
    val selectedState = remember { mutableStateListOf(false, false, false, false) }

    val items = items1
    val subtotal = items.sumOf { it.price * it.quantity }
    val tax = if (subtotal > 0) 50 else 0
    val total = subtotal + tax

    val selectedCount = selectedState.count { it }
    val perPerson = if (selectedCount > 0) total / selectedCount.toDouble() else 0.0
    val paidTotal = people.indices.sumOf { i ->
        if (selectedState[i] && paidState[i]) perPerson else 0.0
    }
    val progress = if (total > 0) (paidTotal / total).toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6F8))
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Split Bill",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Simple, fair, and easy to track",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF113A5C)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Bill", color = Color(0xFFC7D8E7), fontSize = 13.sp)
                Text(
                    text = formatCurrency(total.toDouble()),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    if (selectedCount > 0) {
                        "$selectedCount people selected - ${formatCurrency(perPerson)} each"
                    } else {
                        "Select participants to start splitting"
                    },
                    color = Color(0xFFE6EEF5)
                )
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { min(1f, progress) },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF5CD88A),
                    trackColor = Color(0xFF2A5A82)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Paid: ${formatCurrency(paidTotal)}", color = Color(0xFFDCE7F1), fontSize = 12.sp)
                    Text("Pending: ${formatCurrency(total - paidTotal)}", color = Color(0xFFDCE7F1), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Participants", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                people.forEachIndexed { index, name ->
                    ParticipantRow(
                        name = name,
                        isSelected = selectedState[index],
                        isPaid = paidState[index],
                        amount = if (selectedState[index]) perPerson else 0.0,
                        onSelect = { selectedState[index] = !selectedState[index] },
                        onPay = { if (!paidState[index]) paidState[index] = true }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Bill Summary", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                items.forEachIndexed { index, item ->
                    ItemRow(
                        item = item,
                        onIncrease = { items[index] = item.copy(quantity = item.quantity + 1) },
                        onDecrease = {
                            if (item.quantity > 0) {
                                items[index] = item.copy(quantity = item.quantity - 1)
                            }
                        }
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SummaryRow("Subtotal", formatCurrency(subtotal.toDouble()))
                if (tax > 0) SummaryRow("Tax", formatCurrency(tax.toDouble()))
                SummaryRow("Total", formatCurrency(total.toDouble()), bold = true)
            }
        }
    }
}

@Composable
fun ParticipantRow(
    name: String,
    isSelected: Boolean,
    isPaid: Boolean,
    amount: Double,
    onSelect: () -> Unit,
    onPay: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFF4FAFF) else Color(0xFFF8F8F8),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF2B8DBF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name.first().uppercase(), color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(name, fontWeight = FontWeight.Medium)
                    if (isSelected) {
                        Text(formatCurrency(amount), color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Checkbox(checked = isSelected, onCheckedChange = { onSelect() })

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                if (isPaid) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF43A047)
                    ) {
                        Text(
                            text = "Paid",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp
                        )
                    }
                } else {
                    OutlinedButton(onClick = onPay) {
                        Text("Pay")
                    }
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
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Medium)
            Text("${formatCurrency(item.price)} each", fontSize = 12.sp, color = Color.Gray)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatCurrency(item.price * item.quantity),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEFF2F5)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Button(
                        onClick = onDecrease,
                        modifier = Modifier.size(width = 34.dp, height = 32.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("-")
                    }
                    Text(
                        item.quantity.toString(),
                        modifier = Modifier.padding(horizontal = 10.dp),
                        fontWeight = FontWeight.Medium
                    )
                    Button(
                        onClick = onIncrease,
                        modifier = Modifier.size(width = 34.dp, height = 32.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("+")
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF4D4D4D))
        Text(value, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
    }
}

private fun formatCurrency(amount: Double): String = "₹${"%.2f".format(amount)}"
