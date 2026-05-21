package com.RealizeStudio.qritik.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.data.entity.QRsavesItem
import com.RealizeStudio.qritik.viewModel.SaveViewModel
import com.RealizeStudio.qritik.viewModel.ScannerResultScreenViewModel

@Composable
fun FavoritesScreen(
    viewModel: SaveViewModel = hiltViewModel(),
    scannerResultScreenViewModel: ScannerResultScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val saveList = viewModel.saveList.collectAsState()

    val favoriteList = remember(saveList.value) {
        saveList.value.filter { it.isFavorite }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Modern Header Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF9818D6),
                                    Color(0xFFFF5151)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.save_selected),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.favorites),
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF9818D6),
                                Color(0xFFFF5151)
                            )
                        ),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.save_selected),
                    contentDescription = "",
                    tint =  Color(0xFF9818D6),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.favorite_records),
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (favoriteList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.no_favorite_records_yet),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(favoriteList) { item ->
                        FavoriteRow(item, viewModel, scannerResultScreenViewModel, context)
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteRow(
    list: QRsavesItem,
    viewModel: SaveViewModel,
    screenViewModel: ScannerResultScreenViewModel,
    context: Context
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val iconBgColor = when (list.QR_Type) {
        "URL" -> if (isDark) Color(0xFF1B5E20).copy(alpha = 0.15f) else Color(0xFFE8F5E9)
        "WIFI" -> if (isDark) Color(0xFF0D47A1).copy(alpha = 0.15f) else Color(0xFFE3F2FD)
        "TELEFON" -> if (isDark) Color(0xFFE65100).copy(alpha = 0.15f) else Color(0xFFFFF3E0)
        "E_POSTA" -> if (isDark) Color(0xFF37474F).copy(alpha = 0.15f) else Color(0xFFECEFF1)
        "KONT_BLG" -> if (isDark) Color(0xFF4A148C).copy(alpha = 0.15f) else Color(0xFFF3E5F5)
        else -> if (isDark) Color(0xFFF57F17).copy(alpha = 0.15f) else Color(0xFFFFF8E1)
    }
    val iconTint = when (list.QR_Type) {
        "URL" -> Color(0xFF2E7D32)
        "WIFI" -> Color(0xFF1565C0)
        "TELEFON" -> Color(0xFFE65100)
        "E_POSTA" -> Color(0xFF607D8B)
        "KONT_BLG" -> Color(0xFF8E24AA)
        else -> Color(0xFFF57F17)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(iconBgColor)
                    .clickable {
                        when (list.QR_Type) {
                            "METIN" -> screenViewModel.copyToClipboard(context, list.QR_contents.toString())
                            "URL" -> screenViewModel.openUrl(context, list.QR_contents.toString())
                            "KONT_BLG" -> screenViewModel.copyToClipboard(context, list.QR_contents.toString())
                            "E_POSTA" -> screenViewModel.copyToClipboard(context, list.QR_contents.toString())
                            "TELEFON" -> screenViewModel.openDialerWithNumber(context, list.QR_contents.toString())
                            "SMS" -> screenViewModel.copyToClipboard(context, list.QR_contents.toString())
                            "WIFI" -> screenViewModel.connectToWifi(context, list.QR_contents.toString())
                            "KONUM" -> screenViewModel.copyToClipboard(context, list.QR_contents.toString())
                            else -> screenViewModel.copyToClipboard(context, list.QR_contents.toString())
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        when (list.QR_Type) {
                            "METIN" -> R.drawable.row_txt_icon
                            "URL" -> R.drawable.row_url_icon
                            "KONT_BLG" -> R.drawable.row_user_icon
                            "E_POSTA" -> R.drawable.row_email_icon
                            "TELEFON" -> R.drawable.row_phone_icon
                            "SMS" -> R.drawable.qrcode
                            "WIFI" -> R.drawable.row_wifi_icon
                            "KONUM" -> R.drawable.qrcode
                            "AZTEC", "CODE_128", "EAN_13", "CODABAR", "CODE_39", "CODE_93", "DATA_MATRIX", "EAN_8", "ITF", "PDF_417", "UPC_A", "UPC_E" -> R.drawable.row_product_icon
                            else -> R.drawable.qrcode
                        }
                    ),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = getQrTypeDisplayName(list.QR_Type.toString()),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = list.QR_contents ?: "",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(id = R.string.remove_favorite),
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.toggleFavorite(list) }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                painter = painterResource(R.drawable.delet_save),
                contentDescription = stringResource(id = R.string.delete),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.delete(list.id) }
            )
        }
    }
}
