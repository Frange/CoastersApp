package com.frange.coasters.ui.main

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
import com.frange.coasters.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkListScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()
    var currentParkId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(state) {
        if (state is ParkUiState.Success && currentParkId == null) {
            currentParkId = (state as ParkUiState.Success).availableParks.firstOrNull()?.id
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF2BA9BC))) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.roller_coster_min_blue),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(45.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Coasters app",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Where magic happens",
                            color = Color.White.copy(alpha = 0.9f),
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
                            if (currentParkId != parkInfo.id) {
                                currentParkId = parkInfo.id
                                parkInfo.id?.let { viewModel.requestPark(it) }
                            }
                        },
                        onRefreshClick = {
                            currentParkId?.let { viewModel.requestPark(it) }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // Sincronización directa con el estado del ViewModel
        val isRefreshing = (state as? ParkUiState.Success)?.isRefreshing ?: false

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { currentParkId?.let { viewModel.requestPark(it) } },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            when (val s = state) {
                is ParkUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2BA9BC))
                    }
                }

                is ParkUiState.Success -> {
                    val rides = s.selectedPark?.rideList

                    if (!rides.isNullOrEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                        ) {
                            items(rides) { ride ->
                                RideCard(ride)
                            }
                        }
                    } else {
                        // Mientras se carga el primer parque o se refresca, mostramos el Spinner
                        if (s.isRefreshing || s.selectedPark == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF2BA9BC))
                            }
                        } else {
                            ErrorOrEmptyView(
                                message = "No se encontraron datos",
                                onRetry = { viewModel.requestAllParkInfoList() }
                            )
                        }
                    }
                }

                is ParkUiState.Error -> {
                    ErrorOrEmptyView(
                        message = s.message,
                        onRetry = { viewModel.requestAllParkInfoList() }
                    )
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
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2BA9BC)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Reintentar", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}