package com.example.ghostwriter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ghostwriter.data.AppDatabase
import com.example.ghostwriter.data.ProjectDao
import com.example.ghostwriter.data.ProjectEntity
import com.example.ghostwriter.ui.theme.GhostWriterTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import androidx.compose.ui.tooling.preview.Preview as ComposePreview

class MainActivity : ComponentActivity() {
    private val viewModel: GenerativeViewModel by viewModels {
        val dao = AppDatabase.getDatabase(applicationContext).projectDao()
        val prefs = getSharedPreferences("ghost_writer_prefs", Context.MODE_PRIVATE)
        GenerativeViewModelFactory(dao, prefs)
    }
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        enableEdgeToEdge()
        setContent {
            GhostWriterTheme {
                MainScreen(viewModel, cameraExecutor)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

class GenerativeViewModelFactory(private val dao: ProjectDao, private val prefs: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GenerativeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GenerativeViewModel(dao, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@ComposePreview(showBackground = true)
@Composable
fun MainScreenSuccessPreview() {
    GhostWriterTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            HtmlWebView(
                html = "<h1>Ghost Writer Preview</h1><p>This is how the AI output will look.</p>",
                onClose = {}
            )
        }
    }
}

@ComposePreview(showBackground = true)
@Composable
fun MainScreenLoadingPreview() {
    GhostWriterTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(64.dp))
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Ghost Writer is thinking...", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: GenerativeViewModel, cameraExecutor: ExecutorService) {
    val uiState by viewModel.uiState.collectAsState()
    val history by viewModel.projectHistory.collectAsState()
    val selectedFramework by viewModel.selectedFramework.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState(false)
    val canRedo by viewModel.canRedo.collectAsState(false)
    val dynamicSuggestions by viewModel.dynamicSuggestions.collectAsState()
    val brandContext by viewModel.brandContext.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()

    MainScreenContent(
        uiState = uiState,
        history = history,
        selectedFramework = selectedFramework,
        apiKey = apiKey,
        canUndo = canUndo,
        canRedo = canRedo,
        dynamicSuggestions = dynamicSuggestions,
        brandContext = brandContext,
        cameraExecutor = cameraExecutor,
        onGenerateHtmlFromImage = { viewModel.generateHtmlFromImage(it) },
        onLoadFromHistory = { viewModel.loadFromHistory(it) },
        onDeleteProject = { viewModel.deleteProject(it) },
        onRenameProject = { id, name -> viewModel.renameProject(id, name) },
        onSetApiKey = { viewModel.setApiKey(it) },
        onSetSelectedFramework = { viewModel.setSelectedFramework(it) },
        onSetBrandContext = { viewModel.setBrandContext(it) },
        onClearHistory = { viewModel.clearHistory() },
        onRefineHtml = { viewModel.refineHtml(it) },
        onUpdateHtml = { viewModel.updateHtml(it) },
        onUndo = { viewModel.undo() },
        onRedo = { viewModel.redo() },
        onApplyMagicAssets = { viewModel.applyMagicAssets() },
        onApplyMagicCopy = { viewModel.applyMagicCopy() },
        onApplyAccessibilityAudit = { viewModel.applyAccessibilityAudit() },
        onApplyInteractiveLogic = { viewModel.applyInteractiveLogic() },
        onApplyDarkMode = { viewModel.applyDarkMode() },
        onResetState = { viewModel.resetState() }
    )
}

@Composable
fun MainScreenContent(
    uiState: UiState,
    history: List<ProjectEntity>,
    selectedFramework: ExportFramework,
    apiKey: String,
    canUndo: Boolean,
    canRedo: Boolean,
    dynamicSuggestions: List<String>,
    brandContext: String,
    cameraExecutor: ExecutorService,
    onGenerateHtmlFromImage: (Bitmap) -> Unit,
    onLoadFromHistory: (ProjectEntity) -> Unit,
    onDeleteProject: (Long) -> Unit,
    onRenameProject: (Long, String) -> Unit,
    onSetApiKey: (String) -> Unit,
    onSetSelectedFramework: (ExportFramework) -> Unit,
    onSetBrandContext: (String) -> Unit,
    onClearHistory: () -> Unit,
    onRefineHtml: (String) -> Unit,
    onUpdateHtml: (String) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onApplyMagicAssets: () -> Unit,
    onApplyMagicCopy: () -> Unit,
    onApplyAccessibilityAudit: () -> Unit,
    onApplyInteractiveLogic: () -> Unit,
    onApplyDarkMode: () -> Unit,
    onResetState: () -> Unit
) {
    val isInspectionMode = LocalInspectionMode.current
    var hasCameraPermission by remember { mutableStateOf(isInspectionMode) }
    var showSettings by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var isCameraActive by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (granted) isCameraActive = true
        }
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    onGenerateHtmlFromImage(bitmap)
                    isCameraActive = false
                }
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = isCameraActive && hasCameraPermission,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            label = "ScreenTransition"
        ) { active ->
            if (active) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraContent(
                        cameraExecutor = cameraExecutor,
                        onCapture = { bitmap ->
                            onGenerateHtmlFromImage(bitmap)
                            isCameraActive = false
                        },
                        onPickImage = {
                            imagePickerLauncher.launch("image/*")
                        },
                        onOpenSettings = {
                            showSettings = true
                        },
                        onClose = { isCameraActive = false }
                    )

                    IconButton(
                        onClick = { isCameraActive = false },
                        modifier = Modifier
                            .padding(top = 48.dp, start = 16.dp)
                            .background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close Camera", tint = Color.White)
                    }
                }
            } else {
                WelcomeScreen(
                    history = history,
                    onScanDrawing = {
                        if (hasCameraPermission) {
                            isCameraActive = true
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    },
                    onUploadPhoto = {
                        imagePickerLauncher.launch("image/*")
                    },
                    onOpenSettings = { showSettings = true },
                    onOpenHistory = { showHistory = true },
                    onSelectProject = onLoadFromHistory
                )
            }
        }

        if (showHistory) {
            HistoryScreen(
                history = history,
                onClose = { showHistory = false },
                onSelectProject = { project ->
                    onLoadFromHistory(project)
                    showHistory = false
                },
                onDeleteProject = onDeleteProject,
                onRenameProject = onRenameProject
            )
        }

        if (showSettings) {
            SettingsDialog(
                currentApiKey = apiKey,
                currentFramework = selectedFramework,
                currentBrandContext = brandContext,
                onDismiss = { showSettings = false },
                onSave = { key, framework, brandCtx ->
                    onSetApiKey(key)
                    onSetSelectedFramework(framework)
                    onSetBrandContext(brandCtx)
                    showSettings = false
                },
                onClearHistory = onClearHistory
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )

        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(64.dp), strokeWidth = 4.dp)
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(text = "Ghost Writer is thinking...", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Generating your HTML and Tailwind CSS code using Gemini AI.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            is UiState.Success -> {
                HtmlWebView(
                    html = uiState.html,
                    onClose = onResetState,
                    onCopy = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Code copied to clipboard!")
                        }
                    },
                    onRefine = onRefineHtml,
                    onSave = onUpdateHtml,
                    canUndo = canUndo,
                    onUndo = onUndo,
                    canRedo = canRedo,
                    onRedo = onRedo,
                    onMagicAssets = onApplyMagicAssets,
                    onMagicCopy = onApplyMagicCopy,
                    onAccessibilityAudit = onApplyAccessibilityAudit,
                    onInteractiveLogic = onApplyInteractiveLogic,
                    onDarkMode = onApplyDarkMode,
                    dynamicSuggestions = dynamicSuggestions
                )
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(onClick = onResetState) {
                                Text("Dismiss")
                            }
                        }
                    ) {
                        Text(uiState.message)
                    }
                }
            }
            else -> {}
        }
    }
}


@Composable
fun WelcomeScreen(
    history: List<ProjectEntity>,
    onScanDrawing: () -> Unit,
    onUploadPhoto: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    onSelectProject: (ProjectEntity) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(top = 64.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 8.dp,
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Ghost Writer", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.onSurface)
        Text(text = "Sketch to Code, Instantly", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(48.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onScanDrawing()
                },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = MaterialTheme.shapes.large,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Scan Drawing", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onUploadPhoto()
                },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Collections, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Upload Photo", style = MaterialTheme.typography.titleMedium)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedIconButton(
                    onClick = onOpenHistory,
                    modifier = Modifier.weight(1f).height(64.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("History")
                    }
                }
                OutlinedIconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f).height(64.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Settings")
                    }
                }
            }
        }

        if (history.isNotEmpty()) {
            Spacer(modifier = Modifier.height(48.dp))
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Text(text = "Recent Projects", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(history.take(5)) { project ->
                        Card(
                            onClick = { onSelectProject(project) },
                            modifier = Modifier.size(160.dp, 100.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Box(modifier = Modifier.padding(12.dp)) {
                                Column {
                                    Text(text = "Project #${project.id}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = project.html.take(50).replace("\n", " "), style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(
    history: List<ProjectEntity>,
    onClose: () -> Unit,
    onSelectProject: (ProjectEntity) -> Unit,
    onDeleteProject: (Long) -> Unit,
    onRenameProject: (Long, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredHistory = remember(history, searchQuery) {
        if (searchQuery.isBlank()) history
        else history.filter { it.name?.contains(searchQuery, ignoreCase = true) == true }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
            Text(text = "Project History", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search projects...") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        if (filteredHistory.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(if (searchQuery.isEmpty()) "No projects yet" else "No results found", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredHistory, key = { it.id }) { project ->
                    ProjectCard(
                        project = project,
                        onClick = { onSelectProject(project) },
                        onDelete = { onDeleteProject(project.id) },
                        onRename = { onRenameProject(project.id, it) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(project.name ?: "") }
    
    val dateString = remember(project.timestamp) {
        val date = java.util.Date(project.timestamp)
        java.text.SimpleDateFormat("MMM dd, yyyy • HH:mm", java.util.Locale.getDefault()).format(date)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name ?: "Untitled Project",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { 
                newName = project.name ?: ""
                showRenameDialog = true 
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Rename", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Project") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Project Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onRename(newName)
                    showRenameDialog = false
                }) { Text("Rename") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    GhostWriterTheme {
        WelcomeScreen(
            history = listOf(ProjectEntity(id = 1, html = "<h1>Test</h1>")),
            onScanDrawing = {},
            onUploadPhoto = {},
            onOpenSettings = {},
            onOpenHistory = {},
            onSelectProject = {}
        )
    }
}

@Composable
fun CameraContent(
    cameraExecutor: ExecutorService,
    onCapture: (Bitmap) -> Unit,
    onPickImage: () -> Unit,
    onOpenSettings: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var showTemplateOverlay by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        if (showTemplateOverlay) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val color = Color.Cyan.copy(alpha = 0.3f)
                val stroke = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                
                // Navbar area
                drawRect(color, topLeft = Offset(0f, 0f), size = Size(size.width, 60.dp.toPx()), style = stroke)
                
                // Hero / Main content area
                drawRect(color, topLeft = Offset(20.dp.toPx(), 80.dp.toPx()), size = Size(size.width - 40.dp.toPx(), 200.dp.toPx()), style = stroke)
                
                // Content blocks
                val blockWidth = (size.width - 60.dp.toPx()) / 2
                drawRect(color, topLeft = Offset(20.dp.toPx(), 300.dp.toPx()), size = Size(blockWidth, 100.dp.toPx()), style = stroke)
                drawRect(color, topLeft = Offset(size.width - 20.dp.toPx() - blockWidth, 300.dp.toPx()), size = Size(blockWidth, 100.dp.toPx()), style = stroke)
            }
        }

        // Visual frame/guide for sketching
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 2.dp.toPx()
            val color = Color.White.copy(alpha = 0.5f)
            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)

            val margin = 40.dp.toPx()
            val rectWidth = size.width - (margin * 2)
            val rectHeight = size.height - (margin * 4)
            val rectTop = margin * 2

            drawRect(
                color = color,
                topLeft = Offset(margin, rectTop),
                size = Size(rectWidth, rectHeight),
                style = Stroke(width = strokeWidth, pathEffect = dashPathEffect)
            )

            // Corners
            val cornerLen = 30.dp.toPx()
            val cornerStroke = 4.dp.toPx()

            // Top Left
            drawLine(Color.White, Offset(margin, rectTop), Offset(margin + cornerLen, rectTop), cornerStroke)
            drawLine(Color.White, Offset(margin, rectTop), Offset(margin, rectTop + cornerLen), cornerStroke)

            // Top Right
            drawLine(Color.White, Offset(margin + rectWidth, rectTop), Offset(margin + rectWidth - cornerLen, rectTop), cornerStroke)
            drawLine(Color.White, Offset(margin + rectWidth, rectTop), Offset(margin + rectWidth, rectTop + cornerLen), cornerStroke)

            // Bottom Left
            drawLine(Color.White, Offset(margin, rectTop + rectHeight), Offset(margin + cornerLen, rectTop + rectHeight), cornerStroke)
            drawLine(Color.White, Offset(margin, rectTop + rectHeight), Offset(margin, rectTop + rectHeight - cornerLen), cornerStroke)

            // Bottom Right
            drawLine(Color.White, Offset(margin + rectWidth, rectTop + rectHeight), Offset(margin + rectWidth - cornerLen, rectTop + rectHeight), cornerStroke)
            drawLine(Color.White, Offset(margin + rectWidth, rectTop + rectHeight), Offset(margin + rectWidth, rectTop + rectHeight - cornerLen), cornerStroke)
        }

        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).statusBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(
                onClick = { showTemplateOverlay = !showTemplateOverlay },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (showTemplateOverlay) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.5f),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Dashboard, contentDescription = "Toggle Template")
            }
            IconButton(
                onClick = onClose,
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))).padding(bottom = 48.dp, top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPickImage, modifier = Modifier.size(56.dp).background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small)) {
                    Icon(Icons.Default.Collections, contentDescription = "Pick Image", tint = Color.White)
                }

                Surface(
                    modifier = Modifier.size(80.dp).clickable {
                        imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val bitmap = image.toBitmap()
                                onCapture(bitmap)
                                image.close()
                            }
                            override fun onError(exception: ImageCaptureException) {
                                exception.printStackTrace()
                            }
                        })
                    },
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(4.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Box(modifier = Modifier.padding(8.dp).background(Color.White, shape = CircleShape).border(2.dp, Color.Black, shape = CircleShape))
                }

                IconButton(onClick = onOpenSettings, modifier = Modifier.size(56.dp).background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small)) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    currentApiKey: String,
    currentFramework: ExportFramework,
    currentBrandContext: String,
    onDismiss: () -> Unit,
    onSave: (String, ExportFramework, String) -> Unit,
    onClearHistory: () -> Unit = {}
) {
    var apiKey by remember { mutableStateOf(currentApiKey) }
    var selectedFramework by remember { mutableStateOf(currentFramework) }
    var brandContext by remember { mutableStateOf(currentBrandContext) }
    var showClearConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("AI Configuration", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("Gemini API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    trailingIcon = {
                        IconButton(onClick = { /* Open link to get key */ }) {
                            Icon(Icons.Default.Info, contentDescription = "Get Key")
                        }
                    }
                )

                Text("Design System / Brand Context", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(
                    value = brandContext,
                    onValueChange = { brandContext = it },
                    placeholder = { Text("e.g. Use #FF5733 for buttons, 'Roboto' font, rounded-lg style") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    maxLines = 3
                )

                Text("Export Framework", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium).padding(4.dp)) {
                    ExportFramework.entries.forEach { framework ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small).clickable { selectedFramework = framework }.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedFramework == framework, onClick = { selectedFramework = framework })
                            Text(text = framework.displayName, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Data Management", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                TextButton(
                    onClick = { showClearConfirm = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear All Projects")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(apiKey, selectedFramework, brandContext) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear History?") },
            text = { Text("This will permanently delete all your projects. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HtmlWebView(
    html: String,
    onClose: () -> Unit,
    onCopy: () -> Unit = {},
    onRefine: (String) -> Unit = {},
    onSave: (String) -> Unit = {},
    canUndo: Boolean = false,
    onUndo: () -> Unit = {},
    canRedo: Boolean = false,
    onRedo: () -> Unit = {},
    onMagicAssets: () -> Unit = {},
    onMagicCopy: () -> Unit = {},
    onAccessibilityAudit: () -> Unit = {},
    onInteractiveLogic: () -> Unit = {},
    onDarkMode: () -> Unit = {},
    dynamicSuggestions: List<String> = emptyList()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var refinementText by remember { mutableStateOf("") }
    var showCodeEditor by remember { mutableStateOf(false) }
    var currentHtml by remember(html) { mutableStateOf(html) }
    
    var viewportWidth by remember { mutableStateOf(1f) } // 1f = full, 0.7f = tablet, 0.4f = mobile
    var selectedViewport by remember { mutableStateOf("Desktop") }
    var showMagicMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        Box(modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(viewportWidth)
                    .align(Alignment.Center)
                    .background(Color.White)
                    .shadow(if (viewportWidth < 1f) 8.dp else 0.dp)
            ) {
                if (showCodeEditor) {
                    CodeEditor(
                        code = currentHtml,
                        onCodeChange = { currentHtml = it },
                        onClose = { showCodeEditor = false }
                    )
                } else {
                    if (LocalInspectionMode.current) {
                        Text(
                            text = "WebView Preview ($selectedViewport)",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    } else {
                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    webViewClient = WebViewClient()
                                    @Suppress("SetJavaScriptEnabled")
                                    settings.javaScriptEnabled = true
                                    loadDataWithBaseURL(null, currentHtml, "text/html", "UTF-8", null)
                                }
                            },
                            update = { webView ->
                                webView.loadDataWithBaseURL(null, currentHtml, "text/html", "UTF-8", null)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Control Panel (Top Bar)
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shape = CircleShape,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Viewport Toggles (Left)
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            Triple("Mobile", 0.4f, Icons.Default.Smartphone),
                            Triple("Tablet", 0.7f, Icons.Default.Tablet),
                            Triple("Desktop", 1f, Icons.Default.DesktopWindows)
                        ).forEach { (name, width, icon) ->
                            IconButton(
                                onClick = {
                                    viewportWidth = width
                                    selectedViewport = name
                                },
                                modifier = Modifier.size(36.dp).background(
                                    if (selectedViewport == name) MaterialTheme.colorScheme.primary 
                                    else Color.Transparent,
                                    CircleShape
                                )
                            ) {
                                Icon(
                                    icon, 
                                    contentDescription = name, 
                                    modifier = Modifier.size(18.dp),
                                    tint = if (selectedViewport == name) MaterialTheme.colorScheme.onPrimary 
                                           else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Iteration Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onUndo,
                            enabled = canUndo,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Undo, 
                                contentDescription = "Undo", 
                                tint = if (canUndo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                        IconButton(
                            onClick = onRedo,
                            enabled = canRedo,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Redo, 
                                contentDescription = "Redo",
                                tint = if (canRedo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }

                    VerticalDivider(modifier = Modifier.height(24.dp))

                    // Action Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { 
                                if (showCodeEditor) onSave(currentHtml)
                                showCodeEditor = !showCodeEditor 
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                if (showCodeEditor) Icons.Default.Visibility else Icons.Default.Code, 
                                contentDescription = "Toggle Preview/Code",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = {
                                scope.launch {
                                    val zipFile = saveHtmlToZip(context, currentHtml)
                                    if (zipFile != null) {
                                        shareFile(context, zipFile)
                                    }
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.IosShare, contentDescription = "Export ZIP", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(currentHtml))
                                onCopy()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box {
                            IconButton(
                                onClick = { showMagicMenu = !showMagicMenu },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.AutoFixHigh, 
                                    contentDescription = "Magic Tools", 
                                    modifier = Modifier.size(20.dp), 
                                    tint = if (showMagicMenu) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showMagicMenu,
                                onDismissRequest = { showMagicMenu = false },
                                modifier = Modifier.width(200.dp)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Magic Images") },
                                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                                    onClick = {
                                        onMagicAssets()
                                        showMagicMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Magic Copy") },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.TextSnippet, contentDescription = null) },
                                    onClick = {
                                        onMagicCopy()
                                        showMagicMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Accessibility Audit") },
                                    leadingIcon = { Icon(Icons.Default.Accessibility, contentDescription = null) },
                                    onClick = {
                                        onAccessibilityAudit()
                                        showMagicMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Add Interaction") },
                                    leadingIcon = { Icon(Icons.Default.Gesture, contentDescription = null) },
                                    onClick = {
                                        onInteractiveLogic()
                                        showMagicMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Dark Mode Implementation") },
                                    leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null) },
                                    onClick = {
                                        onDarkMode()
                                        showMagicMenu = false
                                    }
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Surface(tonalElevation = 4.dp, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.navigationBarsPadding().imePadding()) {
                val suggestions = if (dynamicSuggestions.isNotEmpty()) {
                    dynamicSuggestions
                } else {
                    listOf(
                        "Add dark mode",
                        "Make it responsive",
                        "Add a login form"
                    )
                }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(suggestions) { suggestion ->
                        SuggestionChip(
                            onClick = { onRefine(suggestion) },
                            label = { Text(suggestion, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = refinementText,
                        onValueChange = { refinementText = it },
                        placeholder = { Text("Describe changes...") },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (refinementText.isNotBlank()) {
                                onRefine(refinementText)
                                refinementText = ""
                            }
                        })
                    )
                    FloatingActionButton(
                        onClick = {
                            if (refinementText.isNotBlank()) {
                                onRefine(refinementText)
                                refinementText = ""
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun CodeEditor(
    code: String,
    onCodeChange: (String) -> Unit,
    onClose: () -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = code,
                selection = TextRange(code.length)
            )
        )
    }

    LaunchedEffect(code) {
        if (code != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = code)
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E1E))
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                if (it.text != code) {
                    onCodeChange(it.text)
                }
            },
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp).padding(top = 48.dp),
            textStyle = TextStyle(color = Color(0xFFD4D4D4), fontFamily = FontFamily.Monospace, fontSize = 14.sp, lineHeight = 20.sp),
            visualTransformation = HtmlSyntaxHighlighter(),
            cursorBrush = SolidColor(Color.White)
        )

        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF252526)).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("HTML Editor", style = MaterialTheme.typography.labelMedium, color = Color.LightGray)
            IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

class HtmlSyntaxHighlighter : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        return TransformedText(highlightHtml(text.text), OffsetMapping.Identity)
    }

    private fun highlightHtml(code: String): androidx.compose.ui.text.AnnotatedString {
        return buildAnnotatedString {
            val tagColor = Color(0xFF569CD6)      // Blue
            val attrColor = Color(0xFF9CDCFE)     // Light Blue
            val stringColor = Color(0xFFCE9178)   // Orange/Brown
            val commentColor = Color(0xFF6A9955)  // Green
            val bracketColor = Color(0xFF808080)  // Gray
            val textColor = Color(0xFFD4D4D4)    // Light Gray

            var i = 0
            while (i < code.length) {
                when {
                    code.startsWith("<!--", i) -> {
                        val end = code.indexOf("-->", i + 4)
                        val commentEnd = if (end == -1) code.length else end + 3
                        withStyle(SpanStyle(color = commentColor)) { append(code.substring(i, commentEnd)) }
                        i = commentEnd
                    }
                    code.startsWith("<!", i) -> {
                        val end = code.indexOf(">", i)
                        val doctypeEnd = if (end == -1) code.length else end + 1
                        withStyle(SpanStyle(color = tagColor)) { append(code.substring(i, doctypeEnd)) }
                        i = doctypeEnd
                    }
                    code[i] == '<' -> {
                        withStyle(SpanStyle(color = bracketColor)) { append("<") }
                        i++
                        if (i < code.length && code[i] == '/') {
                            withStyle(SpanStyle(color = bracketColor)) { append("/") }
                            i++
                        }
                        val tagStart = i
                        while (i < code.length && code[i] !in " \n\r\t>/") i++
                        withStyle(SpanStyle(color = tagColor)) { append(code.substring(tagStart, i)) }
                        while (i < code.length && code[i] != '>') {
                            if (code[i] in " \n\r\t") {
                                append(code[i])
                                i++
                            } else if (code[i] == '/') {
                                withStyle(SpanStyle(color = bracketColor)) { append("/") }
                                i++
                            } else {
                                val attrStart = i
                                while (i < code.length && code[i] !in " \n\r\t=>/") i++
                                withStyle(SpanStyle(color = attrColor)) { append(code.substring(attrStart, i)) }
                                if (i < code.length && code[i] == '=') {
                                    append("=")
                                    i++
                                    if (i < code.length && code[i] == '"') {
                                        val valStart = i
                                        i++
                                        while (i < code.length && code[i] != '"') i++
                                        if (i < code.length) i++
                                        withStyle(SpanStyle(color = stringColor)) { append(code.substring(valStart, i)) }
                                    }
                                }
                            }
                        }
                    }
                    code[i] == '>' -> {
                        withStyle(SpanStyle(color = bracketColor)) { append(">") }
                        i++
                    }
                    else -> {
                        val start = i
                        while (i < code.length && code[i] !in "<>") i++
                        withStyle(SpanStyle(color = textColor)) { append(code.substring(start, i)) }
                    }
                }
            }
        }
    }
}

suspend fun saveHtmlToZip(context: Context, html: String): File? = withContext(Dispatchers.IO) {
    try {
        val exportDir = File(context.cacheDir, "export")
        if (!exportDir.exists()) exportDir.mkdirs()
        
        val zipFile = File(exportDir, "ghostwriter_project_${System.currentTimeMillis()}.zip")
        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            val entry = ZipEntry("index.html")
            zos.putNextEntry(entry)
            zos.write(html.toByteArray())
            zos.closeEntry()
        }
        zipFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun shareFile(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/zip"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Project ZIP"))
}

@ComposePreview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    GhostWriterTheme {
        HistoryScreen(
            history = listOf(
                ProjectEntity(id = 1, html = "<h1>Project 1</h1><p>Description of project 1</p>"),
                ProjectEntity(id = 2, html = "<div><h2>Project 2</h2><button>Click me</button></div>")
            ),
            onClose = {},
            onSelectProject = {},
            onDeleteProject = {},
            onRenameProject = { _, _ -> }
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
fun SettingsDialogPreview() {
    GhostWriterTheme {
        SettingsDialog(
            currentApiKey = "AIza...",
            currentFramework = ExportFramework.VANILLA_TAILWIND,
            currentBrandContext = "Use orange buttons",
            onDismiss = {},
            onSave = { _, _, _ -> }
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
fun CodeEditorPreview() {
    GhostWriterTheme {
        CodeEditor(
            code = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Sample Page</title>
                    <style>
                        body { font-family: sans-serif; }
                        h1 { color: blue; }
                    </style>
                </head>
                <body>
                    <h1>Hello GhostWriter!</h1>
                    <p>This is a sample HTML code for the editor.</p>
                </body>
                </html>
            """.trimIndent(),
            onCodeChange = {},
            onClose = {}
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
fun CameraContentPreview() {
    GhostWriterTheme {
        CameraContent(
            cameraExecutor = Executors.newSingleThreadExecutor(),
            onCapture = {},
            onPickImage = {},
            onOpenSettings = {},
            onClose = {}
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
fun ProjectCardPreview() {
    GhostWriterTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProjectCard(
                project = ProjectEntity(
                    id = 1,
                    name = "E-commerce Dashboard",
                    html = "<html></html>",
                    timestamp = System.currentTimeMillis()
                ),
                onClick = {},
                onDelete = {},
                onRename = {}
            )
        }
    }
}

@ComposePreview(showBackground = true)
@Composable
fun MainScreenContentPreview() {
    GhostWriterTheme {
        MainScreenContent(
            uiState = UiState.Success("<h1>Hello World</h1><p>Previewing Main Screen Content.</p>"),
            history = listOf(
                ProjectEntity(id = 1, name = "Sample Project", html = "...")
            ),
            selectedFramework = ExportFramework.VANILLA_TAILWIND,
            apiKey = "",
            canUndo = true,
            canRedo = false,
            dynamicSuggestions = listOf("Add dark mode", "Make it responsive", "Add a login form"),
            brandContext = "",
            cameraExecutor = Executors.newSingleThreadExecutor(),
            onGenerateHtmlFromImage = {},
            onLoadFromHistory = {},
            onDeleteProject = {},
            onRenameProject = { _, _ -> },
            onSetApiKey = {},
            onSetSelectedFramework = {},
            onSetBrandContext = {},
            onClearHistory = {},
            onRefineHtml = {},
            onUpdateHtml = {},
            onUndo = {},
            onRedo = {},
            onApplyMagicAssets = {},
            onApplyMagicCopy = {},
            onApplyAccessibilityAudit = {},
            onApplyInteractiveLogic = {},
            onApplyDarkMode = {},
            onResetState = {}
        )
    }
}

