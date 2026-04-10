package com.example.restarentapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.restarentapp.model.BillItem
import com.example.restarentapp.ui.theme.RestarentAppTheme
import com.example.restarentapp.ui.theme.SplitBillScreen
import com.example.restarentapp.ui.theme.SplitByAmountScreen
import com.example.restarentapp.ui.theme.SplitByItemScreen
import com.example.restarentapp.util.OrderUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestarentAppTheme {
                HomeScreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun HomeScreen(viewmodel: OrderViewModel= hiltViewModel()) {
    var state = viewmodel.uiState.collectAsState().value
    val tabs = listOf("Even","Item", "Amount", "Shares", "Percentage")
    var selectedTabIndex by remember { mutableStateOf(0) }
    val items = remember { mutableStateListOf<BillItem>() }

    when (state) {

        is OrderUiState.Loading -> {
            Text("Loading...")
        }

        is OrderUiState.Error -> {
            Text("Error: ${state.message}")
        }

        is OrderUiState.Success -> {
            val apiItems = state.items

            if (items.isEmpty()) {
                items.addAll(
                    apiItems.map {
                        BillItem(
                            id = it.id,
                            name = it.name,
                            price = it.price,
                            quantity = 0, total = it.total
                        )
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
            .systemBarsPadding()
    ) {

        Text(
            text = "Split Bill",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // 🔹 Tabs Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(tabs) { index, title ->

                    val isSelected = selectedTabIndex == index

                    Text(
                        text = title,
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFF1ABC9C) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedTabIndex = index }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Tab Content
            AnimatedContent(
                targetState = selectedTabIndex,
                label = "tab_animation"
            ) { tab ->

                when (tab) {

                    0 -> SplitBillScreen(items)
                    1 -> SplitByItemScreen(items)
                    2 -> SplitByAmountScreen()

                }
            }
        }
    }
}