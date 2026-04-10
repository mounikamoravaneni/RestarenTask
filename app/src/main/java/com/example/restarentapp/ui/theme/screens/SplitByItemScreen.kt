package com.example.restarentapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // checkbox state (person x item)
    val selections = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(people.size * items.size) {
                add(false)
            }
        }
    }

    fun index(pIndex: Int, itemIndex: Int): Int =
        pIndex * items.size + itemIndex

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

        // ================= PEOPLE =================
        people.forEachIndexed { pIndex, person ->

            // ✅ PERSON TOTAL (ONLY SELECTED ITEMS)

            val personTotal = items.indices.sumOf { itemIndex ->
                val idx = pIndex * items.size + itemIndex

                if (selections[idx]) {
                    items[itemIndex].price
                } else 0.0
            }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {

                Column(Modifier.padding(12.dp)) {

                    // HEADER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
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

                            Text(
                                text = person.name,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = formatCurrency(personTotal),
                            fontWeight = FontWeight.SemiBold,
                            color = if (personTotal > 0)
                                Color(0xFF2B8DBF)
                            else
                                Color.Gray
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // ================= ITEMS =================
                    items.forEachIndexed { itemIndex, item ->

                        val idx = index(pIndex, itemIndex)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = selections[idx],
                                onCheckedChange = { selections[idx] = it }
                            )

                            Spacer(Modifier.width(6.dp))

                            Text(
                                text = item.name,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = if (selections[idx])
                                    formatCurrency(item.price)
                                else
                                    formatCurrency(0.0),
                                color = if (selections[idx])
                                    Color(0xFF2B8DBF)
                                else
                                    Color.Gray,
                                fontWeight = if (selections[idx])
                                    FontWeight.SemiBold
                                else
                                    FontWeight.Normal
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
fun BillSummary(
    items: List<BillItem>,
    selections: List<Boolean>,
    peopleCount: Int
) {

    fun index(pIndex: Int, itemIndex: Int): Int =
        pIndex * items.size + itemIndex

    val itemTotals = items.mapIndexed { itemIndex, item ->

        val count = (0 until peopleCount).count { pIndex ->
            val idx = pIndex * items.size + itemIndex
            selections[idx]
        }

        val total = if (count > 0)
            count * item.price
        else
            0.0

        if (count > 0) {
            item.name to total
        } else {
            null
        }
    }.filterNotNull()

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

private fun formatCurrency(amount: Double): String =
    "₹${"%.2f".format(amount)}"