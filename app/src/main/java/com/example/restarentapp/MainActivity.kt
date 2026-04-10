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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.restarentapp.model.BillItem
import com.example.restarentapp.ui.theme.RestarentAppTheme
import com.example.restarentapp.ui.theme.screens.ShareSplitScreen
import com.example.restarentapp.ui.theme.screens.SplitBillScreen
import com.example.restarentapp.ui.theme.screens.SplitByAmountScreen
import com.example.restarentapp.ui.theme.screens.SplitByItemScreen
import com.example.restarentapp.ui.theme.screens.SplitByPercentageScreen
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
    val tabs = listOf("Even", "Item", "Amount", "Shares", "Percentage")
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
            .background(Color(0xFFF3F6F8))
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        Text(
            text = "Split Bill",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Choose how you want to divide the order",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )


        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(tabs) { index, title ->
                    val isSelected = selectedTabIndex == index

                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFF2B8DBF) else Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedTabIndex = index }
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else Color(0xFF5A6470),
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = selectedTabIndex,
                label = "tab_animation"
            ) { tab ->
                when (tab) {
                    0 -> SplitBillScreen(items)
                    1 -> SplitByItemScreen(items)
                    2 -> SplitByAmountScreen(items)
                    3 -> ShareSplitScreen()
                    4 -> SplitByPercentageScreen()

                }
            }
        }
    }
}