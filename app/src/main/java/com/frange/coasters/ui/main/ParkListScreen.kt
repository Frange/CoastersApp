package com.frange.coasters.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frange.coasters.R // Asegúrate de que apunte a tus recursos


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ParkListScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }
    var currentParkId by remember { mutableStateOf<Int?>(null) }

    // Sincronizar el ID del primer parque cargado
    LaunchedEffect(state) {
        if (state is ParkUiState.Success && currentParkId == null) {
            currentParkId = (state as ParkUiState.Success).availableParks.firstOrNull()?.id
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF00BCD4))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.roller_coster_min_blue),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Column {
                        Text(
                            "Coasters app",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Where magic happens",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }

                (state as? ParkUiState.Success)?.let { successState ->
                    ParkSelectorTopBar(
                        parks = successState.availableParks,
                        selectedParkId = currentParkId,
                        isRefreshing = successState.isRefreshing,
                        onParkSelected = { parkInfo ->
                            currentParkId = parkInfo.id
                            parkInfo.id?.let { viewModel.requestPark(it) }
                        },
                        onRefreshClick = {
                            currentParkId?.let { viewModel.requestPark(it) }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        val isRefreshing = (state as? ParkUiState.Success)?.isRefreshing ?: false

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { currentParkId?.let { viewModel.requestPark(it) } },
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.Black)
        ) {
            when (val s = state) {
                is ParkUiState.Loading -> CircularProgressIndicator(
                    color = Color(0xFF00BCD4),
                    modifier = Modifier.align(Alignment.Center)
                )

                is ParkUiState.Success -> {
                    if (s.selectedPark != null) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            s.selectedPark.rideList?.let { rides ->
                                items(rides) { ride -> RideCard(ride) }
                            }
                            s.selectedPark.landList?.forEach { land ->
                                val isExpanded = expandedStates[land.name] ?: true
                                stickyHeader {
                                    LandHeader(
                                        name = land.name,
                                        isExpanded = isExpanded,
                                        onToggle = { expandedStates[land.name] = !isExpanded }
                                    )
                                }
                                if (isExpanded) {
                                    items(land.rideList ?: emptyList()) { ride -> RideCard(ride) }
                                }
                            }
                        }
                    } else if (!isRefreshing) {
                        ErrorOrEmptyView("No se encontraron datos", onRetry = { viewModel.requestAllParkInfoList() })
                    }
                }

                is ParkUiState.Error -> {
                    ErrorOrEmptyView(s.message, onRetry = { viewModel.requestAllParkInfoList() })
                }
            }
        }
    }
}

@Composable
fun ErrorOrEmptyView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = Color.White.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4))
        ) {
            Text("Reintentar", color = Color.Black)
        }
    }
}