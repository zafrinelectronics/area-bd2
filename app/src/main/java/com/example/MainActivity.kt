package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BanglaStrings
import com.example.ui.EnglishStrings
import com.example.ui.LocalAppStrings
import com.example.ui.components.GitHubSyncDialog
import com.example.ui.components.ScannerDialog
import com.example.ui.screens.AddEditProductScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DuePaymentDialog
import com.example.ui.screens.EditSaleDialog
import com.example.ui.screens.EmployeeScreen
import com.example.ui.screens.ExpenseScreen
import com.example.ui.screens.InvoiceMemoDialog
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.ReturnProductDialog
import com.example.ui.screens.SaleCheckoutDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StockScreen
import com.example.ui.screens.SalesScreen
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.PurchaseScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.SuppliersScreen
import com.example.ui.screens.DeliveryAgencyScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.screens.NoInternetScreen
import com.example.util.NetworkMonitor
import com.example.util.NotificationHelper
import com.example.ui.screens.UserManagementDialog
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.util.ParsedBackupData
import com.example.viewmodel.AppTab
import com.example.viewmodel.InventoryViewModel

import androidx.compose.foundation.layout.imePadding

class MainActivity : ComponentActivity() {
    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkMonitor = NetworkMonitor(applicationContext)
        NotificationHelper.createNotificationChannel(applicationContext)
        enableEdgeToEdge()
        setContent {
            val shopSettings by viewModel.shopSettings.collectAsStateWithLifecycle()
            val currentStrings = if (shopSettings.appLanguage.equals("EN", ignoreCase = true)) EnglishStrings else BanglaStrings
            val currentDensity = LocalDensity.current
            val customDensity = remember(currentDensity, shopSettings.textScale) {
                Density(
                    density = currentDensity.density,
                    fontScale = currentDensity.fontScale * shopSettings.textScale
                )
            }

            MyApplicationTheme(primaryColorHex = shopSettings.themePrimaryColorHex) {
                CompositionLocalProvider(
                    LocalDensity provides customDensity,
                    LocalAppStrings provides currentStrings
                ) {
                    val isOnline by networkMonitor.isOnline.collectAsStateWithLifecycle()
                    if (!isOnline) {
                        NoInternetScreen(
                            onRetry = { networkMonitor.checkConnectivity() }
                        )
                    } else {
                        val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
                        if (!isLoggedIn) {
                            var showLoginGitHubDialog by remember { mutableStateOf(false) }
                            val isGitHubSyncing by viewModel.isGitHubSyncing.collectAsStateWithLifecycle()
                            val lastGitHubSyncTime by viewModel.lastGitHubSyncTime.collectAsStateWithLifecycle()
                            val lastGitHubCommitSha by viewModel.lastGitHubCommitSha.collectAsStateWithLifecycle()
                            val lastGitHubSyncStatus by viewModel.lastGitHubSyncStatus.collectAsStateWithLifecycle()

                            if (showLoginGitHubDialog) {
                                GitHubSyncDialog(
                                    lastSyncTime = lastGitHubSyncTime,
                                    lastCommitSha = lastGitHubCommitSha,
                                    lastSyncStatus = lastGitHubSyncStatus,
                                    isSyncing = isGitHubSyncing,
                                    onDismiss = { showLoginGitHubDialog = false },
                                    onPushNow = { viewModel.pushBackupToGitHub(isManual = true) },
                                    onFetchAndRestore = { viewModel.restoreFromGitHub() },
                                    onSaveConfig = { cfg -> viewModel.saveGitHubConfig(cfg) },
                                    onRestoreRawJson = { raw -> viewModel.restoreRawBackup(raw) },
                                    currentFullBackupJson = { viewModel.getFullBackupJsonSync() }
                                )
                            }

                            LoginScreen(
                                viewModel = viewModel,
                                onOpenGitHubSync = { showLoginGitHubDialog = true },
                                onToggleLanguage = {
                                    val newLang = if (shopSettings.appLanguage.equals("EN", ignoreCase = true)) "BN" else "EN"
                                    viewModel.updateShopSettings(shopSettings.copy(appLanguage = newLang))
                                }
                            )
                        } else {
                            MainAppContent(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.syncFromGitHubSilently()
    }
}

@Composable
fun MainAppContent(viewModel: InventoryViewModel) {
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isAddEditOpen by viewModel.isAddEditOpen.collectAsStateWithLifecycle()
    val editingProduct by viewModel.editingProduct.collectAsStateWithLifecycle()
    val productToSell by viewModel.productToSell.collectAsStateWithLifecycle()
    val activeInvoiceSale by viewModel.activeInvoiceSale.collectAsStateWithLifecycle()
    val editingSale by viewModel.editingSale.collectAsStateWithLifecycle()
    val isScannerOpen by viewModel.isScannerOpen.collectAsStateWithLifecycle()
    val saleForDuePayment by viewModel.saleForDuePayment.collectAsStateWithLifecycle()
    val shopSettings by viewModel.shopSettings.collectAsStateWithLifecycle()

    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val allSales by viewModel.allSales.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val dashboardMetrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()
    val reportData by viewModel.reportData.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val selectedBrandFilter by viewModel.selectedBrandFilter.collectAsStateWithLifecycle()
    val reportPeriod by viewModel.reportPeriod.collectAsStateWithLifecycle()

    val filteredExpenses by viewModel.filteredExpenses.collectAsStateWithLifecycle()
    val cashbookMetrics by viewModel.cashbookMetrics.collectAsStateWithLifecycle()
    val expenseFilterPeriod by viewModel.expenseFilterPeriod.collectAsStateWithLifecycle()
    val allEmployees by viewModel.allEmployees.collectAsStateWithLifecycle()
    val allSalaryPayments by viewModel.allSalaryPayments.collectAsStateWithLifecycle()

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val productToReturn by viewModel.productToReturn.collectAsStateWithLifecycle()
    val isUserManagementOpen by viewModel.isUserManagementOpen.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    val isBatchMode by viewModel.isBatchMode.collectAsStateWithLifecycle()
    val githubConfig by viewModel.githubConfig.collectAsStateWithLifecycle()
    val isGitHubSyncing by viewModel.isGitHubSyncing.collectAsStateWithLifecycle()
    val lastGitHubSyncTime by viewModel.lastGitHubSyncTime.collectAsStateWithLifecycle()
    val lastGitHubCommitSha by viewModel.lastGitHubCommitSha.collectAsStateWithLifecycle()
    val lastGitHubSyncStatus by viewModel.lastGitHubSyncStatus.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showGitHubSyncDialog by remember { mutableStateOf(false) }

    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val currentDensity = LocalDensity.current
    val customDensity = remember(currentDensity, shopSettings.textScale) {
        Density(
            density = currentDensity.density,
            fontScale = currentDensity.fontScale * shopSettings.textScale
        )
    }

    val currentStrings = if (shopSettings.appLanguage.equals("EN", ignoreCase = true)) EnglishStrings else BanglaStrings

    CompositionLocalProvider(
        LocalDensity provides customDensity,
        LocalAppStrings provides currentStrings
    ) {
        // Modal Dialogs
        // GitHub.com Sync & Backup Dialog
        if (showGitHubSyncDialog) {
            GitHubSyncDialog(
                lastSyncTime = lastGitHubSyncTime,
                lastCommitSha = lastGitHubCommitSha,
                lastSyncStatus = lastGitHubSyncStatus,
                isSyncing = isGitHubSyncing,
                onDismiss = { showGitHubSyncDialog = false },
                onPushNow = { viewModel.pushBackupToGitHub(isManual = true) },
                onFetchAndRestore = { viewModel.restoreFromGitHub() },
                onSaveConfig = { cfg -> viewModel.saveGitHubConfig(cfg) },
                onRestoreRawJson = { backupData -> viewModel.restoreRawBackup(backupData) },
                currentFullBackupJson = { viewModel.getFullBackupJsonSync() }
            )
        }

        // 1. Live CameraX Barcode / IMEI Scanner Dialog
        if (isScannerOpen) {
            ScannerDialog(
                onDismiss = { viewModel.closeScanner() },
                onBarcodeScanned = { scannedImei ->
                    viewModel.closeScanner()
                    viewModel.setSearchQuery(scannedImei)
                    viewModel.setTab(AppTab.STOCK)
                }
            )
        }

        // 2. Sale Checkout Dialog
        productToSell?.let { product ->
            SaleCheckoutDialog(
                product = product,
                shopSettings = shopSettings,
                onDismiss = { viewModel.closeSaleCheckout() },
                onConfirmSale = { name, phone, address, salePrice, paidAmount, paymentMethod, warranty, remarks ->
                    viewModel.completeSale(
                        customerName = name,
                        customerPhone = phone,
                        customerAddress = address,
                        salePrice = salePrice,
                        paidAmount = paidAmount,
                        paymentMethod = paymentMethod,
                        warrantyInfo = warranty,
                        remarks = remarks
                    )
                }
            )
        }

        // 3. Digital Cash Memo / Invoice Dialog
        activeInvoiceSale?.let { sale ->
            InvoiceMemoDialog(
                sale = sale,
                shopSettings = shopSettings,
                onDismiss = { viewModel.closeInvoice() },
                onCollectDue = { s ->
                    viewModel.closeInvoice()
                    viewModel.openDuePayment(s)
                },
                onEditSale = { s ->
                    viewModel.closeInvoice()
                    viewModel.openEditSale(s)
                },
                onDeleteSale = { s, restoreStock ->
                    viewModel.deleteSale(s, restoreStock)
                }
            )
        }

        // 4. Edit Sale / Invoice Memo Dialog
        editingSale?.let { sale ->
            EditSaleDialog(
                sale = sale,
                onDismiss = { viewModel.closeEditSale() },
                onSaveSale = { updatedSale -> viewModel.saveEditedSale(updatedSale) },
                onDeleteSale = { s, restoreStock -> viewModel.deleteSale(s, restoreStock) }
            )
        }

        // 5. Due Payment Dialog
        saleForDuePayment?.let { sale ->
            DuePaymentDialog(
                sale = sale,
                onDismiss = { viewModel.closeDuePayment() },
                onPaymentConfirmed = { additionalPaid ->
                    viewModel.recordDuePayment(sale, additionalPaid)
                }
            )
        }

        // 6. Return Product Dialog
        productToReturn?.let { product ->
            ReturnProductDialog(
                product = product,
                onDismiss = { viewModel.closeReturnProduct() },
                onConfirmReturn = { productId, reason, refundAmount, notes ->
                    viewModel.confirmReturnProduct(productId, reason, refundAmount, notes)
                }
            )
        }

        // 7. User Management Dialog (Admin)
        if (isUserManagementOpen) {
            UserManagementDialog(
                users = allUsers,
                currentUser = currentUser,
                onDismiss = { viewModel.closeUserManagement() },
                onAddUser = { fullName, username, password, role, phone, canAdd, canEdit, canDel, canSell, canEditSale, canDelSale, canExp, canEmp, canRep, canSet ->
                    viewModel.addUser(
                        fullName, username, password, role, phone,
                        canAdd, canEdit, canDel, canSell, canEditSale, canDelSale,
                        canExp, canEmp, canRep, canSet
                    )
                },
                onUpdateUser = { user -> viewModel.updateUser(user) },
                onToggleActive = { user, active -> viewModel.toggleUserActive(user, active) },
                onUpdateAdminCredentials = { uName, pass, name -> viewModel.updateAdminCredentials(uName, pass, name) },
                onDeleteUser = { user -> viewModel.deleteUser(user) }
            )
        }

        // If Add/Edit Product is open, show the dedicated full-screen form with BackHandler
        if (isAddEditOpen) {
            BackHandler {
                viewModel.closeAddEdit()
            }
            AddEditProductScreen(
                editingProduct = editingProduct,
                initialBatchMode = isBatchMode,
                shopSettings = shopSettings,
                onUpdateShopSettings = { viewModel.updateShopSettings(it) },
                onSave = { company, model, variant, color, battery, box, supplier, purchasePrice, targetPrice, imei, notes ->
                    viewModel.saveProduct(
                        company = company,
                        model = model,
                        variant = variant,
                        color = color,
                        batteryHealth = battery,
                        boxCondition = box,
                        supplier = supplier,
                        purchasePrice = purchasePrice,
                        targetSalePrice = targetPrice,
                        imei = imei,
                        notes = notes
                    )
                },
                onSaveBatch = { company, model, variant, color, battery, box, supplier, purchasePrice, targetPrice, imeis, notes ->
                    viewModel.saveBatchProducts(
                        company = company,
                        model = model,
                        variant = variant,
                        color = color,
                        batteryHealth = battery,
                        boxCondition = box,
                        supplier = supplier,
                        purchasePrice = purchasePrice,
                        targetSalePrice = targetPrice,
                        imeis = imeis,
                        notes = notes
                    )
                },
                onBack = { viewModel.closeAddEdit() }
            )
            return@CompositionLocalProvider
        }

        // Normal Tab Navigation BackHandler
        if (currentTab != AppTab.DASHBOARD) {
            BackHandler {
                viewModel.setTab(AppTab.DASHBOARD)
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.DASHBOARD,
                        onClick = { viewModel.setTab(AppTab.DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 9.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationBarItem(
                        selected = currentTab == AppTab.EXPENSES,
                        onClick = { viewModel.setTab(AppTab.EXPENSES) },
                        icon = { Icon(Icons.Default.Payments, contentDescription = "Transactions") },
                        label = { Text("লেনদেন", fontSize = 9.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { viewModel.openAddProduct() },
                        icon = { Text("+", fontSize = 25.sp, fontWeight = FontWeight.Bold) },
                        label = { Text("যোগ", fontSize = 9.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationBarItem(
                        selected = currentTab == AppTab.REPORTS,
                        onClick = { viewModel.setTab(AppTab.REPORTS) },
                        icon = { Icon(Icons.Default.Assessment, contentDescription = "Reports") },
                        label = { Text("রিপোর্ট", fontSize = 9.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationBarItem(
                        selected = currentTab == AppTab.MORE,
                        onClick = { viewModel.setTab(AppTab.MORE) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "More") },
                        label = { Text("More", fontSize = 9.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    AppTab.DASHBOARD -> DashboardScreen(
                        metrics = dashboardMetrics,
                        products = filteredProducts,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        shopSettings = shopSettings,
                        onAddProductClick = { viewModel.openAddProduct() },
                        onOpenScanner = { viewModel.openScanner() },
                        onNavigateTab = { viewModel.setTab(it) },
                        onSellProduct = { viewModel.openSaleCheckout(it) },
                        onEditProduct = { viewModel.openEditProduct(it) },
                        onDeleteProduct = { viewModel.deleteProduct(it) },
                        onReturnProduct = { viewModel.openReturnProduct(it) },
                        currentUser = currentUser,
                        onOpenUserManagement = { viewModel.openUserManagement() },
                        onLogout = { viewModel.logout() },
                        isGitHubSyncing = isGitHubSyncing,
                        lastGitHubSyncTime = lastGitHubSyncTime,
                        hasGitHubConfigured = githubConfig.token.isNotBlank() && githubConfig.owner.isNotBlank() && githubConfig.repo.isNotBlank(),
                        onOpenGitHubSync = { showGitHubSyncDialog = true },
                        onTriggerSyncNow = { viewModel.syncFromGitHubSilently(force = true) }
                    )
                    AppTab.STOCK -> StockScreen(
                        products = filteredProducts,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        selectedBrandFilter = selectedBrandFilter,
                        onBrandFilterChange = { viewModel.setBrandFilter(it) },
                        onAddProductClick = { viewModel.openAddProduct(batchMode = false) },
                        onBulkAddClick = { viewModel.openAddProduct(batchMode = true) },
                        onGitHubSyncClick = { showGitHubSyncDialog = true },
                        lastSyncTime = lastGitHubSyncTime,
                        isSyncing = isGitHubSyncing,
                        hasGitHubConfigured = githubConfig.token.isNotBlank() && githubConfig.owner.isNotBlank() && githubConfig.repo.isNotBlank(),
                        onTriggerSyncNow = { viewModel.syncFromGitHubSilently(force = true) },
                        onOpenScanner = { viewModel.openScanner() },
                        onSellProduct = { viewModel.openSaleCheckout(it) },
                        onEditProduct = { viewModel.openEditProduct(it) },
                        onDeleteProduct = { viewModel.deleteProduct(it) },
                        onReturnProduct = { viewModel.openReturnProduct(it) },
                        onRestoreProduct = { viewModel.restoreReturnedProductToStock(it) },
                        selectedStatusFilter = selectedStatusFilter,
                        onStatusFilterChange = { viewModel.setStatusFilter(it) }
                    )
                    AppTab.EXPENSES -> ExpenseScreen(
                        expenses = filteredExpenses,
                        cashbookMetrics = cashbookMetrics,
                        selectedPeriod = expenseFilterPeriod,
                        onPeriodSelect = { viewModel.setExpenseFilterPeriod(it) },
                        onAddTransaction = { type, cat, amt, note, method ->
                            viewModel.addExpense(type, cat, amt, note, method)
                        },
                        onDeleteExpense = { viewModel.deleteExpense(it) }
                    )
                    AppTab.EMPLOYEES -> EmployeeScreen(
                        employees = allEmployees,
                        salaryPayments = allSalaryPayments,
                        onAddEmployee = { name, desig, phone, sal, addr, notes ->
                            viewModel.addEmployee(name, desig, phone, sal, addr, notes)
                        },
                        onUpdateEmployee = { viewModel.updateEmployee(it) },
                        onDeleteEmployee = { viewModel.deleteEmployee(it) },
                        onPaySalary = { emp, month, amt, type, method, note, recExp ->
                            viewModel.recordSalaryPayment(emp, month, amt, type, method, note, recExp)
                        },
                        onDeleteSalaryPayment = { viewModel.deleteSalaryPayment(it) }
                    )
                    AppTab.SALES -> SalesScreen(
                        sales = allSales,
                        onViewInvoice = { viewModel.openInvoice(it) },
                        onCollectDue = { viewModel.openDuePayment(it) },
                        onEditSale = { viewModel.openEditSale(it) }
                    )
                    AppTab.PURCHASE -> PurchaseScreen(
                        products = allProducts,
                        onAddPurchase = { viewModel.openAddProduct() }
                    )
                    AppTab.CUSTOMERS -> CustomersScreen(sales = allSales)
                    AppTab.SUPPLIERS -> SuppliersScreen(products = allProducts)
                    AppTab.DELIVERY -> DeliveryAgencyScreen()
                    AppTab.TRANSACTIONS -> TransactionsScreen(sales = allSales, expenses = allExpenses)
                    AppTab.REPORTS -> ReportsScreen(
                        report = reportData,
                        metrics = dashboardMetrics,
                        selectedPeriod = reportPeriod,
                        onPeriodSelect = { viewModel.setReportPeriod(it) }
                    )
                    AppTab.MORE -> MoreScreen(
                        onNavigate = { viewModel.setTab(it) },
                        onLogout = { viewModel.logout() }
                    )
                    AppTab.SETTINGS -> SettingsScreen(
                        currentSettings = shopSettings,
                        onSaveSettings = { viewModel.updateShopSettings(it) },
                        onResetDefaults = { viewModel.resetShopSettings() },
                        onClearAllData = { viewModel.clearAllDatabaseData() },
                        onLoadDemoData = { viewModel.loadDemoData() },
                        onOpenGitHubSync = { showGitHubSyncDialog = true },
                        lastSyncTime = lastGitHubSyncTime,
                        currentUser = currentUser,
                        onOpenUserManagement = { viewModel.openUserManagement() },
                        onLogout = { viewModel.logout() }
                    )
                }
            }
        }
    }
}
