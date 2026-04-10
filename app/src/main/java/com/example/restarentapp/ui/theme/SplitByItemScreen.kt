package com.example.restarentapp.ui.theme


import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import com.example.restarentapp.model.BillItem
import com.example.restarentapp.model.Person
import com.example.restarentapp.model.PersonItemSelection

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SplitByItemScreen(items: List<BillItem>) {

    val people = listOf(
        Person("Asha", "A"),
        Person("Rahul", "R"),
        Person("John", "J"),
        Person("Priya", "P")
    )

    // ✅ State: person × item selection (FLAT BOOLEAN LIST)
    val selections = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(people.size * items.size) {
                add(false)
            }
        }
    }

    fun index(pIndex: Int, itemIndex: Int, itemsSize: Int): Int {
        return pIndex * itemsSize + itemIndex
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())

    ) {

        Text(
            text = "Select Items",
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        // ================= PEOPLE =================
        people.forEachIndexed { pIndex, person ->

            val personTotal = items.indices.sumOf { itemIndex ->
                val idx = index(pIndex, itemIndex, items.size)
                if (selections[idx]) items[itemIndex].price.toInt() else 0
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {

                Column(Modifier.padding(12.dp)) {

                    // 🔹 Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF1ABC9C), CircleShape),
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

                        Text("₹$personTotal")
                    }

                    Spacer(Modifier.height(8.dp))

                    // ================= ITEMS =================
                    items.forEachIndexed { itemIndex, item ->

                        val idx = index(pIndex, itemIndex, items.size)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Row(verticalAlignment = Alignment.CenterVertically) {

                                Checkbox(
                                    checked = selections[idx],
                                    onCheckedChange = {
                                        selections[idx] = it
                                    }
                                )

                                Text("${item.name} ₹${item.price}")
                            }

                            if (selections[idx]) {
                                Text("₹${item.price}")
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        BillSummary(items, selections , people.size)
    }
}
@Composable
fun BillSummary(
    items: List<BillItem>,
    selections: List<Boolean>,
    peopleCount: Int
) {

    fun index(pIndex: Int, itemIndex: Int, itemsSize: Int): Int {
        return pIndex * itemsSize + itemIndex
    }

    val itemTotals = items.mapIndexed { itemIndex, item ->

        val count = (0 until peopleCount).count { pIndex ->
            val idx = index(pIndex, itemIndex, items.size)
            selections[idx]
        }

        item.name to (count * item.price)
    }

    val subtotal = itemTotals.sumOf { it.second }
    val tax = if (subtotal > 0) 50 else 0
    val total = subtotal + tax

    Card(shape = RoundedCornerShape(16.dp)) {

        Column(Modifier.padding(12.dp)) {

            Text("Bill Summary", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))

            itemTotals.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.first)
                    Text("₹${it.second}")
                }
            }

            Divider(Modifier.padding(vertical = 8.dp))

            SummaryRow("Subtotal", "₹$subtotal")
            SummaryRow("Tax", "₹$tax")
            SummaryRow("Total", "₹$total", true)
        }
    }
}