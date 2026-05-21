package com.RealizeStudio.qritik.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.ui.ads.BannerAdView
import com.RealizeStudio.qritik.viewModel.SaveViewModel
import com.RealizeStudio.qritik.viewModel.ScannerResultScreenViewModel
import com.google.zxing.BarcodeFormat

@Composable
fun getKodTypeDisplayName(kodType: String?, type: String?): String {
    if (kodType == null) return " "
    if (type == "QR") {
        return when (kodType) {
            "Metin" -> stringResource(R.string.type_text)
            "URL" -> stringResource(R.string.type_url)
            "Telefon" -> stringResource(R.string.type_phone)
            "Wi-Fi" -> stringResource(R.string.type_wifi)
            "Email Adresi" -> stringResource(R.string.type_email)
            "Kişi" -> stringResource(R.string.type_contact)
            else -> kodType
        }
    }
    return kodType
}

@Composable
fun CreateResultScreen(
    scannerResultScreenViewModel: ScannerResultScreenViewModel = hiltViewModel(),
    saveViewModel: SaveViewModel = hiltViewModel(),
    navController: NavController,
    type: String? = null,
    kodType: String? = null
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    var textKod by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Premium styling states
    var foregroundColor by remember { mutableStateOf(android.graphics.Color.BLACK) }
    var backgroundColor by remember { mutableStateOf(android.graphics.Color.WHITE) }
    var dotShape by remember { mutableStateOf("SQUARE") }
    var eyeShape by remember { mutableStateOf("SQUARE") }
    var isGradient by remember { mutableStateOf(false) }
    var gradientStartColor by remember { mutableStateOf(android.graphics.Color.BLACK) }
    var gradientEndColor by remember { mutableStateOf(android.graphics.Color.BLACK) }
    var showLogo by remember { mutableStateOf(false) }

    // Dynamic category logo
    val logoResId = remember(kodType) {
        when (kodType) {
            "Wi-Fi" -> R.drawable.row_wifi_icon
            "Telefon" -> R.drawable.row_phone_icon
            "URL" -> R.drawable.row_url_icon
            "Email Adresi" -> R.drawable.row_email_icon
            "Kişi" -> R.drawable.row_user_icon
            else -> R.drawable.row_txt_icon
        }
    }
    val logoBitmap = if (showLogo) {
        scannerResultScreenViewModel.getBitmapFromVectorDrawable(context, logoResId)
    } else {
        null
    }

    val selectQRTypeString = remember(kodType) {
        mutableStateOf(
            if (type == "QR") {
                when (kodType) {
                    "Metin" -> "METIN"
                    "URL" -> "URL"
                    "Telefon" -> "TELEFON"
                    "Wi-Fi" -> "WIFI"
                    "Email Adresi" -> "E_POSTA"
                    "Kişi" -> "KONT_BLG"
                    else -> "METIN"
                }
            } else {
                kodType ?: "BARCODE"
            }
        )
    }

    // Dynamic warning and saved messages localization
    val fillFieldsMsg = stringResource(id = R.string.fill_required_fields)
    val enterCodeMsg = stringResource(id = R.string.enter_product_code)
    val savedMsg = stringResource(id = R.string.record_saved_successfully)

    // LaunchedEffect to support real-time styled QR preview regeneration
    LaunchedEffect(foregroundColor, backgroundColor, dotShape, eyeShape, isGradient, gradientStartColor, gradientEndColor, showLogo, textKod) {
        if (bitmap != null && type == "QR" && textKod.trim().isNotEmpty()) {
            bitmap = scannerResultScreenViewModel.generateStyledQrCode(
                content = textKod,
                size = 512,
                foregroundColor = foregroundColor,
                backgroundColor = backgroundColor,
                dotShape = dotShape,
                eyeShape = eyeShape,
                gradientStartColor = if (isGradient) gradientStartColor else null,
                gradientEndColor = if (isGradient) gradientEndColor else null,
                logoBitmap = logoBitmap
            )
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            // Header
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
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            .clickable {
                                navController.navigate("AppScreen")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_left_icon),
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.nav_create),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.05f))
                            .clickable {
                                navController.navigate("AppScreen")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.delet_save),
                            contentDescription = stringResource(id = R.string.cancel),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(if (type == "QR") R.drawable.qr_icon else R.drawable.barcod_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = getKodTypeDisplayName(kodType = kodType, type = type),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dynamic Inputs Field
            Box(modifier = Modifier.fillMaxWidth()) {
                if (type != null) {
                    if (type == "QR") {
                        QRDynamicFields(kodType = kodType ?: "Metin", onValueCompiled = { text ->
                            textKod = text
                        })
                    } else if (type == "Barcod") {
                        BarcodCustomTextField(text = { text ->
                            textKod = text
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Converter Action Button
            Box(modifier = Modifier.fillMaxWidth()) {
                if (type != null) {
                    if (type == "QR") {
                        QRConverterButton(onClick = {
                            if (textKod.trim().isNotEmpty()) {
                                bitmap = scannerResultScreenViewModel.generateStyledQrCode(
                                    content = textKod,
                                    size = 512,
                                    foregroundColor = foregroundColor,
                                    backgroundColor = backgroundColor,
                                    dotShape = dotShape,
                                    eyeShape = eyeShape,
                                    gradientStartColor = if (isGradient) gradientStartColor else null,
                                    gradientEndColor = if (isGradient) gradientEndColor else null,
                                    logoBitmap = logoBitmap
                                )
                            } else {
                                Toast.makeText(context, fillFieldsMsg, Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else if (type == "Barcod") {
                        BarcodConverterButton(onClick = {
                            if (textKod.trim().isEmpty()) {
                                Toast.makeText(context, enterCodeMsg, Toast.LENGTH_SHORT).show()
                                return@BarcodConverterButton
                            }

                            val selectedFormat = when (kodType) {
                                "AZTEC" -> BarcodeFormat.AZTEC
                                "CODE_128" -> BarcodeFormat.CODE_128
                                "EAN_13" -> BarcodeFormat.EAN_13
                                "CODABAR" -> BarcodeFormat.CODABAR
                                "CODE_39" -> BarcodeFormat.CODE_39
                                "CODE_93" -> BarcodeFormat.CODE_93
                                "DATA_MATRIX" -> BarcodeFormat.DATA_MATRIX
                                "EAN_8" -> BarcodeFormat.EAN_8
                                "ITF" -> BarcodeFormat.ITF
                                "PDF_417" -> BarcodeFormat.PDF_417
                                "UPC_A" -> BarcodeFormat.UPC_A
                                "UPC_E" -> BarcodeFormat.UPC_E
                                else -> BarcodeFormat.AZTEC
                            }

                            bitmap = scannerResultScreenViewModel.generateBarcode(textKod, selectedFormat, 250, 400)
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Render Generated Code Image and Actions
            bitmap?.let {
                if (it.width == 100 && it.height == 100) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.read_description_warning),
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Render inside beautiful styled card
                        Surface(
                            modifier = Modifier
                                .size(160.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White,
                            tonalElevation = 4.dp,
                            shadowElevation = 6.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = stringResource(id = R.string.qr_code_image),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Unified circular action pill row
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Save Action Button
                                ResultActionBtn(
                                    painter = painterResource(R.drawable.save_icon),
                                    label = stringResource(id = R.string.records)
                                ) {
                                    saveViewModel.save(
                                        selectQRTypeString.value,
                                        textKod,
                                        scannerResultScreenViewModel.getCurrentDateTime(),
                                        isCreated = true
                                    )
                                    Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                                }

                                // Open URL Action Button (Conditional)
                                if (scannerResultScreenViewModel.isUrl(textKod)) {
                                    ResultActionBtn(
                                        painter = painterResource(R.drawable.open_icon),
                                        label = stringResource(id = R.string.open)
                                    ) {
                                        scannerResultScreenViewModel.openUrl(context, textKod)
                                    }
                                }

                                // Copy Action Button
                                ResultActionBtn(
                                    painter = painterResource(R.drawable.copy_icon),
                                    label = stringResource(id = R.string.copy)
                                ) {
                                    scannerResultScreenViewModel.copyToClipboard(context, textKod)
                                }

                                // Share Action Button
                                ResultActionBtn(
                                    painter = painterResource(R.drawable.share_icon),
                                    label = stringResource(id = R.string.share)
                                ) {
                                    bitmap?.let { bmp ->
                                        scannerResultScreenViewModel.shareBitmap(context, bmp)
                                    } ?: run {
                                        scannerResultScreenViewModel.shareText(context, textKod)
                                    }
                                }

                                // Wifi Action Button (Conditional)
                                if (scannerResultScreenViewModel.isWifiQR(textKod)) {
                                    ResultActionBtn(
                                        painter = painterResource(R.drawable.wifi_icon),
                                        label = stringResource(id = R.string.wifi)
                                    ) {
                                        scannerResultScreenViewModel.connectToWifi(context, textKod)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Customization Options Panel for QR code (Rendered only when active bitmap exists)
            if (bitmap != null && type == "QR") {
                Spacer(modifier = Modifier.height(16.dp))
                QrCustomizerPanel(
                    foregroundColor = foregroundColor,
                    onForeColorChange = { foregroundColor = it },
                    backgroundColor = backgroundColor,
                    onBgColorChange = { backgroundColor = it },
                    dotShape = dotShape,
                    onDotShapeChange = { dotShape = it },
                    eyeShape = eyeShape,
                    onEyeShapeChange = { eyeShape = it },
                    isGradient = isGradient,
                    onGradientToggle = { isGradient = it },
                    gradientStartColor = gradientStartColor,
                    onStartColorChange = { gradientStartColor = it },
                    gradientEndColor = gradientEndColor,
                    onEndColorChange = { gradientEndColor = it },
                    showLogo = showLogo,
                    onLogoToggle = { showLogo = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Informative Help Cards
            Box(modifier = Modifier.fillMaxWidth()) {
                val item = when (kodType) {
                    "AZTEC" -> informativeList[0]
                    "CODE_128" -> informativeList[1]
                    "EAN_13" -> informativeList[2]
                    "CODABAR" -> informativeList[3]
                    "CODE_39" -> informativeList[4]
                    "CODE_93" -> informativeList[5]
                    "DATA_MATRIX" -> informativeList[6]
                    "EAN_8" -> informativeList[7]
                    "ITF" -> informativeList[8]
                    "PDF_417" -> informativeList[9]
                    "UPC_A" -> informativeList[10]
                    "UPC_E" -> informativeList[11]
                    else -> informativeList[0]
                }
                Informative(item)
            }

            Spacer(modifier = Modifier.height(16.dp))
            BannerAdView()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun QrCustomizerPanel(
    foregroundColor: Int,
    onForeColorChange: (Int) -> Unit,
    backgroundColor: Int,
    onBgColorChange: (Int) -> Unit,
    dotShape: String,
    onDotShapeChange: (String) -> Unit,
    eyeShape: String,
    onEyeShapeChange: (String) -> Unit,
    isGradient: Boolean,
    onGradientToggle: (Boolean) -> Unit,
    gradientStartColor: Int,
    onStartColorChange: (Int) -> Unit,
    gradientEndColor: Int,
    onEndColorChange: (Int) -> Unit,
    showLogo: Boolean,
    onLogoToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "QR Tasarımı ve Renkler",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 1. Preset Styles
            Text(
                text = "Hazır Tarzlar (Presets)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetChip(name = "Sunset Gold", colors = listOf(0xFFFF5151.toInt(), 0xFFFFBD59.toInt())) {
                    onGradientToggle(true)
                    onStartColorChange(0xFFFF5151.toInt())
                    onEndColorChange(0xFFFFBD59.toInt())
                    onDotShapeChange("ROUNDED")
                    onEyeShapeChange("ROUNDED")
                }
                PresetChip(name = "Ocean Wave", colors = listOf(0xFF00C6FF.toInt(), 0xFF0072FF.toInt())) {
                    onGradientToggle(true)
                    onStartColorChange(0xFF00C6FF.toInt())
                    onEndColorChange(0xFF0072FF.toInt())
                    onDotShapeChange("CIRCLE")
                    onEyeShapeChange("CIRCLE")
                }
                PresetChip(name = "Royal Velvet", colors = listOf(0xFF9818D6.toInt(), 0xFFFF5151.toInt())) {
                    onGradientToggle(true)
                    onStartColorChange(0xFF9818D6.toInt())
                    onEndColorChange(0xFFFF5151.toInt())
                    onDotShapeChange("ROUNDED")
                    onEyeShapeChange("CIRCLE")
                }
                PresetChip(name = "Classic", colors = listOf(0xFF000000.toInt(), 0xFF000000.toInt())) {
                    onGradientToggle(false)
                    onForeColorChange(0xFF000000.toInt())
                    onBgColorChange(0xFFFFFFFF.toInt())
                    onDotShapeChange("SQUARE")
                    onEyeShapeChange("SQUARE")
                }
            }

            // 2. Custom Colors & Gradient options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Renk Geçişi (Gradient)", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Switch(
                    checked = isGradient,
                    onCheckedChange = onGradientToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isGradient) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Başlangıç Rengi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        ColorPickerRow(selectedColor = gradientStartColor, onColorSelected = onStartColorChange)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Bitiş Rengi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        ColorPickerRow(selectedColor = gradientEndColor, onColorSelected = onEndColorChange)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Gövde Rengi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    ColorPickerRow(selectedColor = foregroundColor, onColorSelected = onForeColorChange)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Dot shape selector
            Text(
                text = "Nokta Şekli",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("SQUARE" to "Kare", "CIRCLE" to "Daire", "ROUNDED" to "Yuvarlak").forEach { (shape, name) ->
                    val selected = dotShape == shape
                    CustomChip(selected = selected, text = name) {
                        onDotShapeChange(shape)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Eye Shape Selector
            Text(
                text = "Köşe Göz Şekli",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("SQUARE" to "Kare", "CIRCLE" to "Daire", "ROUNDED" to "Oval").forEach { (shape, name) ->
                    val selected = eyeShape == shape
                    CustomChip(selected = selected, text = name) {
                        onEyeShapeChange(shape)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Logo toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Merkezi Logo Ekle", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Switch(
                    checked = showLogo,
                    onCheckedChange = onLogoToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
fun CustomChip(selected: Boolean, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PresetChip(name: String, colors: List<Int>, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(
                        if (colors[0] == colors[1]) androidx.compose.ui.graphics.SolidColor(Color(colors[0]))
                        else androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(Color(colors[0]), Color(colors[1]))
                        )
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ColorPickerRow(selectedColor: Int, onColorSelected: (Int) -> Unit) {
    val colors = listOf(
        0xFF000000.toInt(),
        0xFF9818D6.toInt(),
        0xFFFF5151.toInt(),
        0xFF00C6FF.toInt(),
        0xFF10B981.toInt(),
        0xFFF59E0B.toInt()
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        colors.forEach { colorVal ->
            val isSelected = selectedColor == colorVal
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(colorVal))
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onColorSelected(colorVal) }
            )
        }
    }
}

@Composable
fun QRConverterButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = Color.White
        )
    ) {
        Text(
            text = stringResource(id = R.string.convert_to_qr),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun BarcodCustomTextField(text: (String) -> Unit) {
    var textState by remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState,
        onValueChange = {
            textState = it
            text(textState)
        },
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp),
        placeholder = {
            Text(
                text = stringResource(id = R.string.enter_product_code_here),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        },
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            focusedLabelColor = MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
fun BarcodConverterButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(
            text = stringResource(id = R.string.convert_to_barcode),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

val informativeList = listOf<InformativeKod>(
    InformativeKod("AZTEC", R.drawable.aztec_icon, R.string.info_aztec_text1, R.string.info_aztec_text2),
    InformativeKod("CODE_128", R.drawable.codee_icon, R.string.info_code_128_text1, R.string.info_code_128_text2),
    InformativeKod("EAN_13", R.drawable.ean_icon, R.string.info_ean_13_text1, R.string.info_ean_13_text2),
    InformativeKod("CODABAR", R.drawable.codabar_icon, R.string.info_codabar_text1, R.string.info_codabar_text2),
    InformativeKod("CODE_39", R.drawable.codeee_icon, R.string.info_code_39_text1, R.string.info_code_39_text2),
    InformativeKod("CODE_93", R.drawable.codeeee_icon, R.string.info_code_93_text1, R.string.info_code_93_text2),
    InformativeKod("DATA_MATRIX", R.drawable.data_matrix_icon, R.string.info_data_matrix_text1, R.string.info_data_matrix_text2),
    InformativeKod("EAN_8", R.drawable.eann_icon, R.string.info_ean_8_text1, R.string.info_ean_8_text2),
    InformativeKod("ITF", R.drawable.itf_icon, R.string.info_itf_text1, R.string.info_itf_text2),
    InformativeKod("PDF_417", R.drawable.pdf_icon, R.string.info_pdf_417_text1, R.string.info_pdf_417_text2),
    InformativeKod("UPC_A", R.drawable.upc_a_icon, R.string.info_upc_a_text1, R.string.info_upc_a_text2),
    InformativeKod("UPC_E", R.drawable.upc_e_icon, R.string.info_upc_e_text1, R.string.info_upc_e_text2)
)

@Composable
fun Informative(informativeKod: InformativeKod) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Başlık
            Text(
                text = "${informativeKod.name} ${stringResource(id = R.string.info_usage)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // İkon ve kullanım açıklaması
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // İkon container
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(informativeKod.image),
                        contentDescription = "${informativeKod.name} ikonu",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Kullanım açıklaması
                Text(
                    text = stringResource(id = informativeKod.text1ResId),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )
            }

            // Ayırıcı çizgi
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            // Kullanım alanları başlığı
            Text(
                text = stringResource(id = R.string.info_usage_areas),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Kullanım alanları açıklaması
            Text(
                text = stringResource(id = informativeKod.text2ResId),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Normal,
                lineHeight = 18.sp
            )
        }
    }
}

data class InformativeKod(val name: String, val image: Int, val text1ResId: Int, val text2ResId: Int)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun QRDynamicFields(
    kodType: String,
    onValueCompiled: (String) -> Unit
) {
    var textState1 by remember { mutableStateOf("") }
    var textState2 by remember { mutableStateOf("") }
    var textState3 by remember { mutableStateOf("") }

    var wifiSecurity by remember { mutableStateOf("WPA") }

    // OutlinedTextField dynamic styling configurations
    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.secondary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
        focusedLabelColor = MaterialTheme.colorScheme.secondary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        when (kodType) {
            "Metin" -> {
                OutlinedTextField(
                    value = textState1,
                    onValueChange = {
                        textState1 = it
                        onValueCompiled(it)
                    },
                    label = { Text(stringResource(id = R.string.type_text)) },
                    placeholder = { Text(stringResource(id = R.string.enter_text_here)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )
            }
            "URL" -> {
                OutlinedTextField(
                    value = textState1,
                    onValueChange = {
                        textState1 = it
                        val compiled = if (it.trim().isNotEmpty() && !it.startsWith("http://") && !it.startsWith("https://")) {
                            "https://$it"
                        } else {
                            it
                        }
                        onValueCompiled(compiled)
                    },
                    label = { Text(stringResource(id = R.string.web_address_url)) },
                    placeholder = { Text(stringResource(id = R.string.enter_link_here)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )
            }
            "Telefon" -> {
                OutlinedTextField(
                    value = textState1,
                    onValueChange = {
                        textState1 = it
                        onValueCompiled("tel:$it")
                    },
                    label = { Text(stringResource(id = R.string.phone_number)) },
                    placeholder = { Text(stringResource(id = R.string.enter_phone_number)) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )
            }
            "Wi-Fi" -> {
                OutlinedTextField(
                    value = textState1,
                    onValueChange = {
                        textState1 = it
                        onValueCompiled("WIFI:S:$it;T:$wifiSecurity;P:$textState2;;")
                    },
                    label = { Text(stringResource(id = R.string.network_name_ssid)) },
                    placeholder = { Text(stringResource(id = R.string.enter_network_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textState2,
                    onValueChange = {
                        textState2 = it
                        onValueCompiled("WIFI:S:$textState1;T:$wifiSecurity;P:$it;;")
                    },
                    label = { Text(stringResource(id = R.string.password)) },
                    placeholder = { Text(stringResource(id = R.string.enter_network_password)) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                    ),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.security_type), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 14.sp)
                    Row {
                        listOf("WPA", "WEP", "nopass").forEach { sec ->
                            Button(
                                onClick = {
                                    wifiSecurity = sec
                                    onValueCompiled("WIFI:S:$textState1;T:$sec;P:$textState2;;")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (wifiSecurity == sec) MaterialTheme.colorScheme.secondary else Color.LightGray,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .height(32.dp)
                            ) {
                                Text(
                                    text = if (sec == "nopass") stringResource(id = R.string.open_security) else sec,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            "Email Adresi" -> {
                OutlinedTextField(
                    value = textState1,
                    onValueChange = {
                        textState1 = it
                        onValueCompiled("mailto:$it")
                    },
                    label = { Text(stringResource(id = R.string.email_address)) },
                    placeholder = { Text(stringResource(id = R.string.enter_email_address)) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )
            }
            "Kişi" -> {
                OutlinedTextField(
                    value = textState1,
                    onValueChange = {
                        textState1 = it
                        onValueCompiled("BEGIN:VCARD\nVERSION:3.0\nN:$it\nTEL:$textState2\nEMAIL:$textState3\nEND:VCARD")
                    },
                    label = { Text(stringResource(id = R.string.name_surname)) },
                    placeholder = { Text(stringResource(id = R.string.enter_contact_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textState2,
                    onValueChange = {
                        textState2 = it
                        onValueCompiled("BEGIN:VCARD\nVERSION:3.0\nN:$textState1\nTEL:$it\nEMAIL:$textState3\nEND:VCARD")
                    },
                    label = { Text(stringResource(id = R.string.phone_label)) },
                    placeholder = { Text(stringResource(id = R.string.enter_contact_phone)) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textState3,
                    onValueChange = {
                        textState3 = it
                        onValueCompiled("BEGIN:VCARD\nVERSION:3.0\nN:$textState1\nTEL:$textState2\nEMAIL:$it\nEND:VCARD")
                    },
                    label = { Text(stringResource(id = R.string.email_label)) },
                    placeholder = { Text(stringResource(id = R.string.enter_contact_email)) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )
            }
        }
    }
}

@Composable
private fun ResultActionBtn(
    painter: androidx.compose.ui.graphics.painter.Painter,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            )
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}