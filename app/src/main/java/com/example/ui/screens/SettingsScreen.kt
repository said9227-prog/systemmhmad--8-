package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.util.scrollToTopOnFocus
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.data.model.StoreSettings
import com.example.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    onNavigateToAlarmSettings: () -> Unit = {},
    onNavigateToBackupRestore: () -> Unit = {},
    onNavigateToAuditManagement: () -> Unit = {}
) {
    val context = LocalContext.current
    val settings by viewModel.storeSettings.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val securityPin by viewModel.securityPin.collectAsState()

    val isDriveSyncEnabled by viewModel.isDriveSyncEnabled.collectAsState()
    val driveAccountEmail by viewModel.driveAccountEmail.collectAsState()
    val lastBackupTimeStr by viewModel.lastBackupTimeStr.collectAsState()
    val driveBackupStatus by viewModel.driveBackupStatus.collectAsState()
    val availableBackupFiles by viewModel.availableBackupFiles.collectAsState()

    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val biometricUserName by viewModel.biometricUserName.collectAsState()

    var showBiometricSetupDialog by remember { mutableStateOf(false) }
    var showEditBiometricUserDialog by remember { mutableStateOf(false) }

    var storeName by remember(settings) { mutableStateOf(settings.storeName) }
    var storePhone by remember(settings) { mutableStateOf(settings.storePhone) }
    var storeAddress by remember(settings) { mutableStateOf(settings.storeAddress) }
    var storeEmail by remember(settings) { mutableStateOf(settings.storeEmail) }
    var currency by remember(settings) { mutableStateOf(settings.currency) }
    var invoicePrefix by remember(settings) { mutableStateOf(settings.invoicePrefix) }
    var lastInvoiceNumber by remember(settings) { mutableStateOf(settings.lastInvoiceNumber.toString()) }
    var isAutoNumberingEnabled by remember(settings) { mutableStateOf(settings.isAutoNumberingEnabled) }
    var lastPaymentNumber by remember(settings) { mutableStateOf(settings.lastPaymentNumber.toString()) }
    var lastSalesReturnNumber by remember(settings) { mutableStateOf(settings.lastSalesReturnNumber.toString()) }
    var lastPurchaseReturnNumber by remember(settings) { mutableStateOf(settings.lastPurchaseReturnNumber.toString()) }
    var showVatAndSubtotal by remember(settings) { mutableStateOf(settings.showVatAndSubtotal) }
    var isAutoPdfBackupEnabled by remember(settings) { mutableStateOf(settings.isAutoPdfBackupEnabled) }
    var autoPdfBackupHour by remember(settings) { mutableStateOf(settings.autoPdfBackupHour) }

    var overdueDaysThresholdText by remember(settings) { mutableStateOf(settings.overdueDaysThreshold.toString()) }
    var fastPayerDaysThresholdText by remember(settings) { mutableStateOf(settings.fastPayerDaysThreshold.toString()) }
    var loyaltyMinInvoicesCountText by remember(settings) { mutableStateOf(settings.loyaltyMinInvoicesCount.toString()) }
    var overdueNoticeTemplate by remember(settings) { mutableStateOf(settings.overdueNoticeTemplate) }
    var loyaltyAppreciationTemplate by remember(settings) { mutableStateOf(settings.loyaltyAppreciationTemplate) }

    var generalReminderEnabled by remember(settings) { mutableStateOf(settings.isGeneralInstallmentReminderEnabled) }
    var generalReminderHour by remember(settings) { mutableStateOf(settings.generalReminderHour) }
    var generalReminderDaysBefore by remember(settings) { mutableStateOf(settings.generalReminderDaysBeforeDue.toString()) }
    var generalReminderOverdueDays by remember(settings) { mutableStateOf(settings.generalReminderDaysAfterOverdue.toString()) }
    var generalReminderScope by remember(settings) { mutableStateOf(settings.generalReminderScope) }
    var customerRemindersEnabled by remember(settings) { mutableStateOf(settings.isCustomerInstallmentReminderEnabled) }

    var showAuditLogsDialog by remember { mutableStateOf(false) }
    var showPinSetupDialog by remember { mutableStateOf(false) }
    var showGoogleSignInDialog by remember { mutableStateOf(false) }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }
    var showBackupFilesListDialog by remember { mutableStateOf(false) }
    var isRestoringData by remember { mutableStateOf(false) }
    var userDriveEmailInput by remember { mutableStateOf(driveAccountEmail ?: "") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 0. Theme & Appearance Section
        item {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.NightsStay else Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("مظهر التطبيق (الوضع النهاري / الليلي)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isDarkMode) "الوضع الحالي: ليلي 🌙" else "الوضع الحالي: نهاري (رسمي أبيض) ☀️",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = if (isDarkMode) "ألوان داكنة مريحة للعين" else "ألوان نهارية رسمية بيضاء وواضحة بخطوط غامقة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            thumbContent = {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.NightsStay else Icons.Default.WbSunny,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        // 1. Store Identity Section
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("معلومات وهوية المتجر", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    Divider()

                    OutlinedTextField(
                        value = storeName,
                        onValueChange = { storeName = it },
                        label = { Text("اسم المتجر / العلامة التجارية *") },
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = storePhone,
                        onValueChange = { storePhone = it },
                        label = { Text("رقم هاتف المتجر") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = storeAddress,
                        onValueChange = { storeAddress = it },
                        label = { Text("عنوان المقر") },
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = storeEmail,
                        onValueChange = { storeEmail = it },
                        label = { Text("البريد الإلكتروني التجاري") },
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth()
                    )
                }
            }
        }

        // 2. Financial & Invoicing Settings
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.PriceChange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("الإعدادات المالية والترقيم", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    Divider()

                    // Currency chooser row
                    Text("العملات المستخدمة:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    val currencyOptions = listOf(
                        "الريال اليمني" to "ر.ي",
                        "الريال السعودي" to "ر.س",
                        "الدولار الأمريكي" to "$"
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currencyOptions.forEach { (name, code) ->
                            val selected = currency == name || currency == code
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { currency = name },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = if (selected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(code, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(name, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = invoicePrefix,
                            onValueChange = { invoicePrefix = it },
                            label = { Text("بادئة الفواتير") },
                            modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().weight(1f)
                        )
                        OutlinedTextField(
                            value = lastInvoiceNumber,
                            onValueChange = { lastInvoiceNumber = it },
                            label = { Text("رقم آخر فاتورة") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().weight(1f)
                        )
                    }

                    Divider()

                    // Auto-numbering toggle for payments and returns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "توليد أرقام السندات والمرتجعات تلقائياً",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (isAutoNumberingEnabled) 
                                    "مفعل: ترقيم آلي لسندات السداد ومرتجع المبيعات ومرتجع المشتريات (تبدأ من 1)"
                                else 
                                    "يدوي (الافتراضي): تسجيل رقم السند ورقم المرتجع يدوياً بمربع فارغ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isAutoNumberingEnabled,
                            onCheckedChange = { checked ->
                                isAutoNumberingEnabled = checked
                                if (checked) {
                                    if ((lastPaymentNumber.toIntOrNull() ?: 0) == 0) lastPaymentNumber = "0"
                                    if ((lastSalesReturnNumber.toIntOrNull() ?: 0) == 0) lastSalesReturnNumber = "0"
                                    if ((lastPurchaseReturnNumber.toIntOrNull() ?: 0) == 0) lastPurchaseReturnNumber = "0"
                                }
                            }
                        )
                    }

                    if (isAutoNumberingEnabled) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "أرقام البداية للتسلسل الآلي (الرقم التالي سيكون +1):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = lastPaymentNumber,
                                    onValueChange = { lastPaymentNumber = it },
                                    label = { Text("آخر رقم سداد") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.scrollToTopOnFocus().weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = lastSalesReturnNumber,
                                    onValueChange = { lastSalesReturnNumber = it },
                                    label = { Text("آخر مرتجع مبيعات") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.scrollToTopOnFocus().weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = lastPurchaseReturnNumber,
                                    onValueChange = { lastPurchaseReturnNumber = it },
                                    label = { Text("آخر مرتجع مشتريات") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.scrollToTopOnFocus().weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    Divider()

                    // VAT & Subtotal toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "إظهار ضريبة القيمة المضافة والمجموع الفرعي",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (showVatAndSubtotal)
                                    "معروض في تفاصيل الفواتير والطباعة ومشاركتها"
                                else
                                    "مخفي (الافتراضي): إخفاء سطري المجموع الفرعي والضريبة لتبسيط الفاتورة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = showVatAndSubtotal,
                            onCheckedChange = { showVatAndSubtotal = it }
                        )
                    }
                }
            }
        }

        // 3. App Security Settings
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("أمان وحماية التطبيق", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("قفل التطبيق برقم PIN سري", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = if (securityPin.isNullOrBlank()) "غير مفعل حالياً" else "مفعل ومحمي",
                                fontSize = 11.sp,
                                color = if (securityPin.isNullOrBlank()) Color.Gray else Color(0xFF059669)
                            )
                        }
                        Switch(
                            checked = !securityPin.isNullOrBlank(),
                            onCheckedChange = { checked ->
                                if (checked) {
                                    showPinSetupDialog = true
                                } else {
                                    viewModel.setSecurityPin(null)
                                    Toast.makeText(context, "تم إيقاف قفل الحماية السري", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("قفل التطبيق بالبصمة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = if (isBiometricEnabled) "مفعل للمستخدم: ${biometricUserName.ifBlank { "غير محدد" }}" else "غير مفعل حالياً",
                                fontSize = 11.sp,
                                color = if (isBiometricEnabled) Color(0xFF059669) else Color.Gray
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isBiometricEnabled) {
                                IconButton(onClick = { showEditBiometricUserDialog = true }) {
                                    Icon(Icons.Default.Edit, contentDescription = "تعديل الاسم", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                            }
                            Switch(
                                checked = isBiometricEnabled,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        if (biometricUserName.isBlank()) {
                                            showBiometricSetupDialog = true
                                        } else {
                                            viewModel.setBiometricSettings(true, biometricUserName)
                                            Toast.makeText(context, "تم تفعيل قفل البصمة", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        viewModel.setBiometricSettings(false, biometricUserName)
                                        Toast.makeText(context, "تم إيقاف قفل البصمة", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }

                    Divider()

                    // Operations Audit Log shortcut
                    Button(
                        onClick = { showAuditLogsDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.HistoryToggleOff, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("عرض سجل عمليات النظام (Audit Log)")
                    }
                }
            }
        }

        // 4. Advanced Backup, Restore & Export Center
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToBackupRestore() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Backup,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "مركز النسخ الاحتياطي والاستعادة والتصدير",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "النسخ التلقائي المجدول، استعادة البيانات الآمنة، وتصدير التقارير (Excel/PDF)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 4a2. Audit Log Retention & Management (أرشفة وتطهير سجل العمليات)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAuditManagement() }
                    .testTag("card_audit_log_management"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "إدارة سجل العمليات والأرشفة والتنظيف الشهري",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "أرشفة شهرية، تطهير آمن، سياسات الاحتفاظ، وفحص واستعادة حزم ZIP",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }


        // 5. Automatic Daily PDF Backup Section for All Clients
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAutoPdfBackupEnabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAutoPdfBackupEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626))
                            Column {
                                Text("تصدير كشوفات العملاء التلقائي (PDF)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("تصدير تقرير شامل لجميع العملاء إلى مجلد Downloads", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isAutoPdfBackupEnabled,
                            onCheckedChange = { isAutoPdfBackupEnabled = it }
                        )
                    }

                    Divider()

                    // Time Picker Hour Selection
                    if (isAutoPdfBackupEnabled) {
                        Text("ميعاد التصدير والحفظ التلقائي اليومي:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        val hourOptions = listOf(
                            0 to "الساعة 12:00 ليلاً (منتصف الليل)",
                            1 to "الساعة 01:00 صباحاً",
                            2 to "الساعة 02:00 صباحاً",
                            8 to "الساعة 08:00 صباحاً",
                            12 to "الساعة 12:00 ظهراً",
                            20 to "الساعة 08:00 مساءً",
                            22 to "الساعة 10:00 مساءً"
                        )

                        var expandedHourDropdown by remember { mutableStateOf(false) }
                        val currentHourLabel = hourOptions.find { it.first == autoPdfBackupHour }?.second ?: "الساعة ${autoPdfBackupHour}:00"

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expandedHourDropdown = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(currentHourLabel, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }

                            DropdownMenu(
                                expanded = expandedHourDropdown,
                                onDismissRequest = { expandedHourDropdown = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                hourOptions.forEach { (hr, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label, fontWeight = if (autoPdfBackupHour == hr) FontWeight.Bold else FontWeight.Normal) },
                                        onClick = {
                                            autoPdfBackupHour = hr
                                            expandedHourDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Text(
                                    "عند التفعيل، يحفظ التطبيق ملف 'نسخة كشوفات العملاء.pdf' تلقائياً يومياً في الساعة المحددة بذاكرة الهاتف الداخلية بمجلد Download.",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Immediate Manual Save Button
                    Button(
                        onClick = {
                            val savedFile = viewModel.generateAndSaveAllClientsPdfNow()
                            if (savedFile != null) {
                                Toast.makeText(
                                    context,
                                    "تم حفظ التقرير الشامل بنجاح!\nاسم الملف: نسخة كشوفات العملاء.pdf\nالمسار: Downloads",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(context, "فشل حفظ ملف PDF", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ وتصدير ملف PDF لجميع العملاء الآن يدوياً", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 6. Loyalty and Payment Management Settings Section (ميزة إدارة السداد والعملاء الأوفياء)
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = Color(0xFFD97706)
                        )
                        Text(
                            text = "إدارة السداد والعملاء الأوفياء ⭐",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = "نظام ذكي لمتابعة وتصنيف سلوك السداد تلقائياً، إرسال تذكيرات التأخر، ورسائل الشكر والتقدير للعملاء الأوفياء.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Divider()

                    // 1. Overdue Threshold
                    OutlinedTextField(
                        value = overdueDaysThresholdText,
                        onValueChange = { overdueDaysThresholdText = it },
                        label = { Text("مهلة تأخر السداد (بالأيام)") },
                        supportingText = { Text("عدد الأيام بعد تاريخ الاستحقاق لتصنيف العميل '⚠️ متأخر بالسداد'") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth()
                    )

                    // 2. Fast Payer Threshold
                    OutlinedTextField(
                        value = fastPayerDaysThresholdText,
                        onValueChange = { fastPayerDaysThresholdText = it },
                        label = { Text("مهلة السداد السريع (بالأيام)") },
                        supportingText = { Text("السداد خلال هذه المدة يمنح العميل شارة '⚡ سريع السداد'") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth()
                    )

                    // 3. Loyalty Min Invoices Count
                    OutlinedTextField(
                        value = loyaltyMinInvoicesCountText,
                        onValueChange = { loyaltyMinInvoicesCountText = it },
                        label = { Text("الحد الأدنى لعدد الفواتير للوفاء") },
                        supportingText = { Text("عدد الفواتير المنتظمة المطلوبة لنيل تصنيف '⭐ عميل وفي'") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth()
                    )

                    // 4. Overdue Notice WhatsApp Template
                    OutlinedTextField(
                        value = overdueNoticeTemplate,
                        onValueChange = { overdueNoticeTemplate = it },
                        label = { Text("قالب رسالة تذكير المتأخرين بالسداد (واتساب)") },
                        supportingText = { Text("المتغيرات: {اسم_العميل}، {اسم_المتجر}، {المبلغ}، {العملة}، {أيام_التأخير}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )

                    // 5. Loyalty Appreciation WhatsApp Template
                    OutlinedTextField(
                        value = loyaltyAppreciationTemplate,
                        onValueChange = { loyaltyAppreciationTemplate = it },
                        label = { Text("قالب رسالة شكر وتقدير للعميل الوفي (واتساب)") },
                        supportingText = { Text("المتغيرات: {اسم_العميل}، {اسم_المتجر}، {عدد_الفواتير}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )
                }
            }
        }

        // 7. Installment Reminder Configuration (إعدادات تنبيهات الأقساط)
        item {
            Card(
                modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column {
                                Text("إعدادات تنبيهات الأقساط", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("ضبط التنبيهات العامة وتنبيهات العملاء المخصصة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Divider()

                    // Toggle General Reminders
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("تفعيل التنبيه العام للأقساط", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("إشعار ملخص يومي بالأقساط المستحقة والمتأخرة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = generalReminderEnabled,
                            onCheckedChange = { generalReminderEnabled = it }
                        )
                    }

                    if (generalReminderEnabled) {
                        // Hour Picker
                        Text("وقت التنبيه العام اليومي:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        val reminderHourOptions = listOf(
                            7 to "الساعة 07:00 صباحاً",
                            8 to "الساعة 08:00 صباحاً",
                            9 to "الساعة 09:00 صباحاً",
                            10 to "الساعة 10:00 صباحاً",
                            12 to "الساعة 12:00 ظهراً",
                            14 to "الساعة 02:00 ظهراً",
                            17 to "الساعة 05:00 مساءً",
                            20 to "الساعة 08:00 مساءً"
                        )
                        var expandedReminderHour by remember { mutableStateOf(false) }
                        val reminderHourLabel = reminderHourOptions.find { it.first == generalReminderHour }?.second ?: "الساعة ${generalReminderHour}:00"

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expandedReminderHour = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(reminderHourLabel, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }

                            DropdownMenu(
                                expanded = expandedReminderHour,
                                onDismissRequest = { expandedReminderHour = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                reminderHourOptions.forEach { (hr, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label, fontWeight = if (generalReminderHour == hr) FontWeight.Bold else FontWeight.Normal) },
                                        onClick = {
                                            generalReminderHour = hr
                                            expandedReminderHour = false
                                        }
                                    )
                                }
                            }
                        }

                        // Scope selection: اليوم / القادمة / المتأخرة / الكل
                        Text("نطاق التنبيه:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        val scopes = listOf("الكل", "اليوم", "القادمة", "المتأخرة")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            scopes.forEach { sc ->
                                FilterChip(
                                    selected = generalReminderScope == sc,
                                    onClick = { generalReminderScope = sc },
                                    label = { Text(sc, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Days Before & Overdue Thresholds
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = generalReminderDaysBefore,
                                onValueChange = { generalReminderDaysBefore = it },
                                label = { Text("أيام قبل الاستحقاق") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().weight(1f)
                            )
                            OutlinedTextField(
                                value = generalReminderOverdueDays,
                                onValueChange = { generalReminderOverdueDays = it },
                                label = { Text("أيام التأخر للتنبيه") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().weight(1f)
                            )
                        }
                    }

                    Divider()

                    // Direct access to dedicated Alarm Profile settings page
                    OutlinedButton(
                        onClick = onNavigateToAlarmSettings,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_alarm_profile_from_settings_btn")
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("فتح صفحة إعدادات منبّه الأقساط (Alarm Profile)", fontWeight = FontWeight.Bold)
                    }

                    // Toggle Customer Specific Reminders
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("تفعيل التنبيهات المخصصة للعملاء", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("السماح بإنشاء ومتابعة تنبيهات مرتبطة بعميل وقسط محدد", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = customerRemindersEnabled,
                            onCheckedChange = { customerRemindersEnabled = it }
                        )
                    }
                }
            }
        }

        // 8. Submit Changes Action
        item {
            Button(
                onClick = {
                    if (storeName.isBlank()) {
                        Toast.makeText(context, "اسم المتجر مطلوب!", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.updateStoreSettings(
                            settings.copy(
                                storeName = storeName,
                                storePhone = storePhone,
                                storeAddress = storeAddress,
                                storeEmail = storeEmail,
                                currency = currency,
                                invoicePrefix = invoicePrefix,
                                lastInvoiceNumber = lastInvoiceNumber.toIntOrNull() ?: settings.lastInvoiceNumber,
                                isAutoNumberingEnabled = isAutoNumberingEnabled,
                                lastPaymentNumber = lastPaymentNumber.toIntOrNull() ?: settings.lastPaymentNumber,
                                lastSalesReturnNumber = lastSalesReturnNumber.toIntOrNull() ?: settings.lastSalesReturnNumber,
                                lastPurchaseReturnNumber = lastPurchaseReturnNumber.toIntOrNull() ?: settings.lastPurchaseReturnNumber,
                                showVatAndSubtotal = showVatAndSubtotal,
                                isAutoPdfBackupEnabled = isAutoPdfBackupEnabled,
                                autoPdfBackupHour = autoPdfBackupHour,
                                overdueDaysThreshold = overdueDaysThresholdText.toIntOrNull() ?: settings.overdueDaysThreshold,
                                fastPayerDaysThreshold = fastPayerDaysThresholdText.toIntOrNull() ?: settings.fastPayerDaysThreshold,
                                loyaltyMinInvoicesCount = loyaltyMinInvoicesCountText.toIntOrNull() ?: settings.loyaltyMinInvoicesCount,
                                overdueNoticeTemplate = overdueNoticeTemplate.ifBlank { settings.overdueNoticeTemplate },
                                loyaltyAppreciationTemplate = loyaltyAppreciationTemplate.ifBlank { settings.loyaltyAppreciationTemplate },
                                isGeneralInstallmentReminderEnabled = generalReminderEnabled,
                                generalReminderHour = generalReminderHour,
                                generalReminderDaysBeforeDue = generalReminderDaysBefore.toIntOrNull() ?: settings.generalReminderDaysBeforeDue,
                                generalReminderDaysAfterOverdue = generalReminderOverdueDays.toIntOrNull() ?: settings.generalReminderDaysAfterOverdue,
                                generalReminderScope = generalReminderScope,
                                isCustomerInstallmentReminderEnabled = customerRemindersEnabled
                            )
                        )
                        Toast.makeText(context, "تم حفظ وتحديث جميع الإعدادات بنجاح!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("حفظ جميع الإعدادات", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Passcode/PIN Dialog Setup
    if (showPinSetupDialog) {
        var pinCode by remember { mutableStateOf("") }
        var pinError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showPinSetupDialog = false },
            title = { Text("إعداد رقم PIN سري للحماية") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("أدخل رمزاً مكوناً من 4 أرقام لتأمين تطبيق حسابات العملاء برو:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = pinCode,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                pinCode = it
                                pinError = false
                            }
                        },
                        label = { Text("رمز القفل PIN") },
                        isError = pinError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinCode.length == 4) {
                            viewModel.setSecurityPin(pinCode)
                            showPinSetupDialog = false
                            Toast.makeText(context, "تم تفعيل القفل برمز PIN بنجاح!", Toast.LENGTH_SHORT).show()
                        } else {
                            pinError = true
                        }
                    }
                ) {
                    Text("تفعيل الحماية")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinSetupDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Security Audit Logs Dialog Display
    if (showAuditLogsDialog) {
        AlertDialog(
            onDismissRequest = { showAuditLogsDialog = false },
            title = { Text("سجل العمليات وحماية النظام (Audit Log)", fontWeight = FontWeight.Bold) },
            text = {
                Box(modifier = Modifier.sizeIn(maxHeight = 400.dp)) {
                    if (auditLogs.isEmpty()) {
                        Text("لا يوجد سجل نشاطات حالياً.")
                    } else {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(auditLogs) { log ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Gray.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(log.operationType, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                                        Text(dateFormat.format(Date(log.timestamp)), fontSize = 9.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("الجدول: ${log.tableName}", fontWeight = FontWeight.Medium, fontSize = 10.sp, color = Color.DarkGray)
                                    Text(log.details, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAuditLogsDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Google Sign In & Drive Permissions Dialog
    if (showGoogleSignInDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleSignInDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFF4285F4))
                    Text("تسجيل الدخول وإذونات Google Drive", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "لتفعيل المزامنة التلقائية بملفات JSON الحية، يرجى تسجيل الدخول إلى حساب Google الخاص بك ومنح التطبيق إذن قراءة وحفظ النسخة الاحتياطية بـ Google Drive:",
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    OutlinedTextField(
                        value = userDriveEmailInput,
                        onValueChange = { userDriveEmailInput = it },
                        label = { Text("البريد الإلكتروني لحساب Google") },
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth(),
                        singleLine = true
                    )
                    Surface(
                        color = Color(0xFF4285F4).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(16.dp))
                            Text("سيتم إنشاء وتحديث ملف JSON باسم 'debt_app_clients_backup.json' تلقائياً.", fontSize = 10.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userDriveEmailInput.isNotBlank()) {
                            viewModel.toggleDriveSync(true, userDriveEmailInput.trim())
                            showGoogleSignInDialog = false
                            Toast.makeText(context, "تم تسجيل الدخول وتفعيل المزامنة مع Google Drive بنجاح!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "يرجى إدخال بريد إلكتروني صالح", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                ) {
                    Icon(Icons.Default.Login, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("موافقة وتسجيل الدخول")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoogleSignInDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Google Drive Restore Confirmation Dialog
    if (showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { if (!isRestoringData) showRestoreConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF059669))
                    Text("استعادة بيانات العملاء من Google Drive", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "هل ترغب في استعادة جميع بيانات العملاء، الفواتير، المقبوضات والأقساط من ملف JSON المحفوظ في Google Drive؟\n\nسيتم قراءة جميع الملفات واستخراج البيانات وتنسيقها داخل التطبيق لاستعادة حساباتك بالكامل.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    if (isRestoringData) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF059669))
                            Text("جاري استعادة وقراءة البيانات من ملفات JSON...", fontSize = 11.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isRestoringData = true
                        viewModel.restoreGoogleDriveBackup(
                            onSuccess = { msg ->
                                isRestoringData = false
                                showRestoreConfirmDialog = false
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            },
                            onError = { err ->
                                isRestoringData = false
                                Toast.makeText(context, "خطأ في الاستعادة: $err", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = !isRestoringData,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("استعادة البيانات الآن")
                }
            },
            dismissButton = {
                if (!isRestoringData) {
                    TextButton(onClick = { showRestoreConfirmDialog = false }) {
                        Text("إلغاء")
                    }
                }
            }
        )
    }

    // Modal List of JSON Backup Files in Accountant Backup Folder
    if (showBackupFilesListDialog) {
        AlertDialog(
            onDismissRequest = { showBackupFilesListDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF4285F4))
                    Text("ملفات النسخ بـ Accountant Backup", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Box(modifier = Modifier.sizeIn(maxHeight = 380.dp)) {
                    if (availableBackupFiles.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                            Text("لم يتم العثور على أية ملفات نسخ احتياطية في مجلد Accountant Backup حالياً.", fontSize = 12.sp, color = Color.Gray)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(availableBackupFiles) { fileInfo ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Default.DataObject, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                                                Text(fileInfo.fileName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            Text(fileInfo.fileSizeFormatted, fontSize = 10.sp, color = Color.Gray)
                                        }

                                        Text("التاريخ: ${fileInfo.backupDate}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                        Text(
                                            "المحتوى: ${fileInfo.clientCount} عميل | ${fileInfo.invoiceCount} فاتورة | ${fileInfo.paymentCount} دفعة",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Button(
                                            onClick = {
                                                viewModel.restoreBackupFromFile(
                                                    fileInfo,
                                                    onSuccess = { msg ->
                                                        showBackupFilesListDialog = false
                                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                    },
                                                    onError = { err ->
                                                        Toast.makeText(context, "خطأ: $err", Toast.LENGTH_LONG).show()
                                                    }
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("استعادة البيانات من هذا الملف", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showBackupFilesListDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    if (showBiometricSetupDialog) {
        var newUserName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showBiometricSetupDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("تفعيل البصمة وتسجيل المستخدم", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "لإتمام تفعيل البصمة لأول مرة، الرجاء إدخال اسمك لربطه بالمصادقة (سيظهر عند قفل التطبيق):",
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = newUserName,
                        onValueChange = { newUserName = it },
                        label = { Text("اسم المستخدم (مثال: محمد، المدير)") },
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newUserName.isNotBlank()) {
                            viewModel.setBiometricSettings(true, newUserName)
                            Toast.makeText(context, "تم تفعيل قفل البصمة بنجاح!", Toast.LENGTH_SHORT).show()
                            showBiometricSetupDialog = false
                        } else {
                            Toast.makeText(context, "الرجاء إدخال الاسم أولاً", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("حفظ وتفعيل البصمة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBiometricSetupDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showEditBiometricUserDialog) {
        var editUserName by remember { mutableStateOf(biometricUserName) }
        AlertDialog(
            onDismissRequest = { showEditBiometricUserDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("تعديل اسم مستخدم البصمة", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("قم بتحديث اسم المستخدم المرتبط ببصمة الأمان:", fontSize = 13.sp)
                    OutlinedTextField(
                        value = editUserName,
                        onValueChange = { editUserName = it },
                        label = { Text("الاسم الجديد") },
                        modifier = Modifier.scrollToTopOnFocus().scrollToTopOnFocus().fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editUserName.isNotBlank()) {
                            viewModel.updateBiometricUserName(editUserName)
                            Toast.makeText(context, "تم تحديث الاسم بنجاح", Toast.LENGTH_SHORT).show()
                            showEditBiometricUserDialog = false
                        }
                    }
                ) {
                    Text("تحديث")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBiometricUserDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
