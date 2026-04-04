package com.frange.coasters.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frange.coasters.domain.model.ParkInfo

@Composable
fun ParkSelectorTopBar(
    parks: List<ParkInfo>,
    selectedParkId: Int?,
    isRefreshing: Boolean,
    onParkSelected: (ParkInfo) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRefreshClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPark = parks.find { it.id == selectedParkId }
    val selectedName = selectedPark?.name ?: "Seleccionar Parque"

    val blueColor = Color(0xFF00BCD4)
    val dropDownColor = Color(0xFF005560)
    val backgroundColor = Color(0xFF000000)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.SubdirectoryArrowRight,
            contentDescription = null,
            tint = blueColor
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { expanded = true }
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedName,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f, fill = false)
            )

            if (selectedPark?.isFavourite == true) {
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Favorite, null, tint = blueColor, modifier = Modifier.size(14.dp))
            }
        }

        IconButton(onClick = onRefreshClick) {
            Icon(Icons.Default.History, "Refrescar", tint = blueColor)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(dropDownColor).width(250.dp)
        ) {
            parks.forEach { park ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(park.name, color = Color.White, modifier = Modifier.weight(1f))
                            IconButton(onClick = { onToggleFavorite(park.name) }) {
                                Icon(
                                    imageVector = if (park.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (park.isFavourite) blueColor else Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    },
                    onClick = {
                        expanded = false
                        onParkSelected(park)
                    }
                )
            }
        }
    }
}