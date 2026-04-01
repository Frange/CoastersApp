package com.frange.coasters.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
    onRefreshClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = parks.find { it.id == selectedParkId }?.name ?: "Seleccionar Parque"

    val blueColor = Color(0xFF00BCD4)
    val dropDownColor = Color(0xFF1B89AC)
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

        Text(
            text = selectedName,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .clickable { expanded = true }
                .padding(start = 12.dp),
            fontSize = 16.sp
        )

        IconButton(onClick = onRefreshClick) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Refrescar",
                tint = blueColor
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(
                dropDownColor
            )
        ) {
            parks.forEach { park ->
                DropdownMenuItem(
                    text = { Text(park.name, color = Color.White) },
                    onClick = {
                        expanded = false
                        onParkSelected(park)
                    }
                )
            }
        }
    }
}