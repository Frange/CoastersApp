package com.frange.coasters.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParkListScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            (state as? ParkUiState.Success)?.let { successState ->
                ParkSelectorTopBar(
                    parks = successState.availableParks,
                    onParkSelected = { parkInfo ->
                        viewModel.requestPark(parkInfo.id!!)
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = state) {
                is ParkUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ParkUiState.Success -> {
                    Column {
                        if (s.isRefreshing) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }

                        if (s.selectedPark != null) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {

                                items(s.selectedPark.rideList ?: emptyList()) { ride ->
                                    RideCard(ride)
                                }

                                // 2. Zonas temáticas con sus atracciones
                                s.selectedPark.landList?.forEach { land ->
                                    stickyHeader {
                                        LandHeader(land.name)
                                    }
                                    items(land.rideList ?: emptyList()) { ride ->
                                        RideCard(ride)
                                    }
                                }
                            }
                        } else if (!s.isRefreshing) {
                            Text(
                                "Selecciona un parque para ver los tiempos",
                                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                            )
                        }
                    }
                }

                is ParkUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = s.message, color = MaterialTheme.colorScheme.error)
                        Button(
                            onClick = { viewModel.requestAllParkInfoList() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}