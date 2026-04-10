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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
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
import com.example.restarentapp.model.BillItem
import com.example.restarentapp.model.Person

@Composable
fun SplitByItemScreen(items: List<BillItem>) {
    val people = listOf(
        Person("Asha", "A"),
        Person("Rahul", "R"),
        Person("John", "J"),
        Person("Priya", "P")
    )

    val selections = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(people.size * items.size) {
                add(false)
            }
        }
    }

    fun index(pIndex: Int, itemIndex: Int, itemsSize: Int): Int = pIndex * itemsSize + itemIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6F8))
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Split by Item",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text("Pick who consumed each item", color = Color.Gray)
        Spacer(Modifier.height(14.dp))

        people.forEachIndexed { pIndex, person ->
            val personTotal = items.indices.sumOf { itemIndex ->
                val idx = index(pIndex, itemIndex, items.size)
                if (selections[idx]) items[itemIndex].price * items[itemIndex].quantity else 0.0
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF2B8DBF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(person.short, color = Color.White)
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(text = person.name, fontWeight = FontWeight.Bold)
                        }
                        Text(formatCurrency(personTotal), fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(8.dp))

                    items.forEachIndexed { itemIndex, item ->
                        val idx = index(pIndex, itemIndex, items.size)
                        val itemTotal = item.price * item.quantity

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(
                                    checked = selections[idx],
                                    onCheckedChange = { selections[idx] = it }
                                )
                                Text("${item.name} (${item.quantity}x)")
                            }

                            Text(
                                if (selections[idx]) formatCurrency(itemTotal) else formatCurrency(0.0),
                                color = if (selections[idx]) Color(0xFF2B8DBF) else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        BillSummary(items, selections, people.size)
    }
}

@Composable
fun BillSummary(items: List<BillItem>, selections: List<Boolean>, peopleCount: Int) {
    fun index(pIndex: Int, itemIndex: Int, itemsSize: Int): Int = pIndex * itemsSize + itemIndex

    val itemTotals = items.mapIndexed { itemIndex, item ->
        val count = (0 until peopleCount).count { pIndex ->
            val idx = index(pIndex, itemIndex, items.size)
            selections[idx]
        }
        item.name to (count * item.price * item.quantity)
    }

    val subtotal = itemTotals.sumOf { it.second }
    val tax = if (subtotal > 0) 50.0 else 0.0
    val total = subtotal + tax

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Bill Summary", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            itemTotals.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.first)
                    Text(formatCurrency(it.second))
                }
            }

            Divider(Modifier.padding(vertical = 8.dp))
            SummaryRow("Subtotal", formatCurrency(subtotal))
            SummaryRow("Tax", formatCurrency(tax))
            SummaryRow("Total", formatCurrency(total), true)
        }
    }
}

private fun formatCurrency(amount: Double): String = "₹${"%.2f".format(amount)}"
