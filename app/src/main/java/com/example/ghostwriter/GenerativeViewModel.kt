package com.example.ghostwriter

import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ghostwriter.data.ProjectDao
import com.example.ghostwriter.data.ProjectEntity
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val html: String) : UiState()
    data class Error(val message: String) : UiState()
}

enum class ExportFramework(val displayName: String) {
    VANILLA_TAILWIND("Vanilla + Tailwind"),
    BOOTSTRAP("Bootstrap"),
    REACT_TAILWIND("React + Tailwind"),
    JETPACK_COMPOSE("Jetpack Compose")
}

class GenerativeViewModel(
    private val projectDao: ProjectDao,
    private val prefs: SharedPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _apiKey = MutableStateFlow(prefs.getString("api_key", "") ?: "")
    val apiKey: StateFlow<String> = _apiKey

    private val _selectedFramework = MutableStateFlow(
        try {
            ExportFramework.valueOf(prefs.getString("framework", ExportFramework.VANILLA_TAILWIND.name) ?: ExportFramework.VANILLA_TAILWIND.name)
        } catch (_: Exception) {
            ExportFramework.VANILLA_TAILWIND
        }
    )
    val selectedFramework: StateFlow<ExportFramework> = _selectedFramework

    private val _brandContext = MutableStateFlow(prefs.getString("brand_context", "") ?: "")
    val brandContext: StateFlow<String> = _brandContext

    val projectHistory: StateFlow<List<ProjectEntity>> = projectDao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _codeHistory = MutableStateFlow<List<String>>(emptyList())
    private val _redoStack = MutableStateFlow<List<String>>(emptyList())
    
    val canUndo = _codeHistory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()).let { historyFlow ->
        MutableStateFlow(false).apply {
            viewModelScope.launch {
                historyFlow.collect { this@apply.value = it.size > 1 }
            }
        }
    }
    
    val canRedo = _redoStack.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()).let { redoFlow ->
        MutableStateFlow(false).apply {
            viewModelScope.launch {
                redoFlow.collect { this@apply.value = it.isNotEmpty() }
            }
        }
    }

    private val _dynamicSuggestions = MutableStateFlow<List<String>>(emptyList())
    val dynamicSuggestions: StateFlow<List<String>> = _dynamicSuggestions

    private var chatSession: Chat? = null
    private var currentProjectId: Long? = null

    fun setApiKey(key: String) {
        _apiKey.value = key
        prefs.edit { putString("api_key", key) }
    }

    fun setSelectedFramework(framework: ExportFramework) {
        _selectedFramework.value = framework
        prefs.edit { putString("framework", framework.name) }
    }

    fun setBrandContext(context: String) {
        _brandContext.value = context
        prefs.edit { putString("brand_context", context) }
    }

    private fun getGenerativeModel(): GenerativeModel? {
        if (_apiKey.value.isBlank()) return null
        return GenerativeModel(
            modelName = "gemini-1.5-flash-latest",
            apiKey = _apiKey.value
        )
    }

    private suspend fun ensureChatSession(contextHtml: String? = null): Chat? {
        if (chatSession != null) return chatSession
        val model = getGenerativeModel() ?: return null
        val session = model.startChat()
        chatSession = session
        if (contextHtml != null) {
            try {
                session.sendMessage("I am loading an existing project. Here is the current code: $contextHtml. Please acknowledge and wait for instructions.")
            } catch (_: Exception) {
                // Ignore context errors
            }
        }
        return session
    }

    fun generateHtmlFromImage(bitmap: Bitmap?) {
        if (bitmap == null) {
            _uiState.value = UiState.Error("Failed to load image.")
            return
        }

        val model = getGenerativeModel()
        if (model == null) {
            // DEMO MODE FALLBACK
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                kotlinx.coroutines.delay(2000)
                val demoHtml = """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <script src="https://cdn.tailwindcss.com"></script>
                        <title>Demo Project</title>
                    </head>
                    <body class="bg-gray-100 min-h-screen flex flex-col items-center justify-center p-4">
                        <div class="bg-white p-8 rounded-2xl shadow-xl max-w-md w-full text-center">
                            <div class="w-20 h-20 bg-blue-500 rounded-full mx-auto mb-6 flex items-center justify-center">
                                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path></svg>
                            </div>
                            <h1 class="text-3xl font-bold text-gray-800 mb-2">Ghost Writer Demo</h1>
                            <p class="text-gray-600 mb-8">This is a simulated result because no API Key was found. Add your key in Settings to use the real AI!</p>
                            <button class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-full transition-all transform hover:scale-105">Get Started</button>
                        </div>
                    </body>
                    </html>
                """.trimIndent()
                _uiState.value = UiState.Success(demoHtml)
                _codeHistory.value = listOf(demoHtml)
                saveToHistory(demoHtml, "Ghost Writer Demo")
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val frameworkPrompt = when (_selectedFramework.value) {
                    ExportFramework.VANILLA_TAILWIND -> "a single-file functional HTML and Tailwind CSS string"
                    ExportFramework.BOOTSTRAP -> "a single-file functional HTML and Bootstrap 5 string"
                    ExportFramework.REACT_TAILWIND -> "a single-file React component (JSX) using Tailwind CSS. Include necessary imports but assume Tailwind is available globally"
                    ExportFramework.JETPACK_COMPOSE -> "a single Kotlin file containing a Jetpack Compose @Composable function. Use Material3 components."
                }

                val brandInstructions = if (_brandContext.value.isNotBlank()) {
                    "Follow this design system/brand context: ${_brandContext.value}. "
                } else ""

                val prompt = content {
                    image(bitmap)
                    text("Transform this hand-drawn UI sketch into $frameworkPrompt. " +
                            brandInstructions +
                            "Output ONLY the code. No markdown formatting or extra text. " +
                            "Make it responsive and look like a modern mobile/web application.")
                }
                
                // Use generateContent for the initial image capture for better stability
                val response = model.generateContent(prompt)
                val htmlContent = response.text?.trim() ?: ""
                val cleanedHtml = cleanHtml(htmlContent)
                
                if (cleanedHtml.isEmpty()) {
                    _uiState.value = UiState.Error("AI returned an empty response. Try a clearer photo.")
                } else {
                    _uiState.value = UiState.Success(cleanedHtml)
                    _codeHistory.value = listOf(cleanedHtml)
                    _redoStack.value = emptyList()
                    
                    // Initialize chat session for future refinements
                    chatSession = model.startChat()
                    
                    val namePrompt = "Briefly describe what this UI is in 3 to 5 words to use as a project title. Output ONLY the title, no extra text."
                    val nameResponse = chatSession?.sendMessage(namePrompt)
                    val projectTitle = nameResponse?.text?.trim()?.removeSurrounding("\"") ?: "New Project"
                    
                    saveToHistory(cleanedHtml, projectTitle)
                    fetchDynamicSuggestions(cleanedHtml)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun refineHtml(instruction: String) {
        val currentHtml = (_uiState.value as? UiState.Success)?.html ?: return
        
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val session = ensureChatSession(currentHtml)
                if (session == null) {
                    _uiState.value = UiState.Error("API Key required for refinements. Check Settings.")
                    return@launch
                }

                val response = session.sendMessage(instruction)
                val htmlContent = response.text?.trim() ?: ""
                val cleanedHtml = cleanHtml(htmlContent)
                
                if (cleanedHtml.isEmpty()) {
                    _uiState.value = UiState.Error("AI returned an empty response.")
                } else {
                    _uiState.value = UiState.Success(cleanedHtml)
                    _codeHistory.value += cleanedHtml
                    _redoStack.value = emptyList()
                    updateCurrentInHistory(cleanedHtml)
                    fetchDynamicSuggestions(cleanedHtml)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadFromHistory(project: ProjectEntity) {
        _uiState.value = UiState.Success(project.html)
        _codeHistory.value = listOf(project.html)
        _redoStack.value = emptyList()
        currentProjectId = project.id
        chatSession = null
    }

    fun updateHtml(html: String) {
        _uiState.value = UiState.Success(html)
        if (_codeHistory.value.lastOrNull() != html) {
            _codeHistory.value += html
        }
        updateCurrentInHistory(html)
    }

    fun undo() {
        val currentHistory = _codeHistory.value
        if (currentHistory.size > 1) {
            val last = currentHistory.last()
            val newHistory = currentHistory.dropLast(1)
            _codeHistory.value = newHistory
            _redoStack.value += last
            val previousHtml = newHistory.last()
            _uiState.value = UiState.Success(previousHtml)
            updateCurrentInHistory(previousHtml)
            fetchDynamicSuggestions(previousHtml)
        }
    }

    fun redo() {
        val currentRedo = _redoStack.value
        if (currentRedo.isNotEmpty()) {
            val next = currentRedo.last()
            _redoStack.value = currentRedo.dropLast(1)
            _codeHistory.value += next
            _uiState.value = UiState.Success(next)
            updateCurrentInHistory(next)
            fetchDynamicSuggestions(next)
        }
    }

    fun applyMagicAssets() {
        applyMagicTransformation("Analyze the following code. Find all image tags or placeholders and replace their 'src' attributes with high-quality, contextually relevant Unsplash image URLs. Return ONLY the full updated code.")
    }

    fun applyMagicCopy() {
        applyMagicTransformation("Analyze the following code. Replace all generic placeholder text, 'Lorem Ipsum', and 'Button 1' style labels with professional, high-converting, contextually relevant marketing copy tailored to this UI's purpose. Return ONLY the full updated code.")
    }

    fun applyAccessibilityAudit() {
        applyMagicTransformation("Audit the following code for accessibility. Add appropriate ARIA roles, ensure proper label associations, improve screen reader compatibility, and fix any color contrast issues while maintaining the design. Return ONLY the full updated code.")
    }

    fun applyInteractiveLogic() {
        applyMagicTransformation("Make the following UI interactive. If it's HTML, add simple Vanilla JS or Alpine.js (assume it's available) for common behaviors like mobile menus, modals, and tab switching. If it's React/Compose, add the necessary state logic. Return ONLY the full updated code.")
    }

    fun applyDarkMode() {
        applyMagicTransformation("Add a smart dark mode implementation to the following code. Don't just invert colors; create a premium, balanced dark theme palette that aligns with modern UI trends. Return ONLY the full updated code.")
    }

    private fun applyMagicTransformation(instruction: String) {
        val currentCode = (_uiState.value as? UiState.Success)?.html ?: return
        
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val session = ensureChatSession(currentCode)
                if (session == null) {
                    _uiState.value = UiState.Error("API Key required for Magic Tools. Check Settings.")
                    return@launch
                }

                val response = session.sendMessage("$instruction\n\nCode: $currentCode")
                val newCode = cleanHtml(response.text ?: "")
                
                if (newCode.isNotEmpty()) {
                    _uiState.value = UiState.Success(newCode)
                    _codeHistory.value += newCode
                    _redoStack.value = emptyList()
                    updateCurrentInHistory(newCode)
                    fetchDynamicSuggestions(newCode)
                } else {
                    _uiState.value = UiState.Success(currentCode)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Magic failed: ${e.message}")
            }
        }
    }

    private fun fetchDynamicSuggestions(html: String) {
        viewModelScope.launch {
            try {
                val session = ensureChatSession(html) ?: return@launch
                val prompt = "Based on this HTML code, suggest 4 short (max 4 words) improvements or features to add. Output ONLY the suggestions separated by commas. Code snippet: ${html.take(1000)}"
                val response = session.sendMessage(prompt)
                val suggestions = response.text?.split(",")?.map { it.trim().removeSurrounding("\"") }?.filter { it.isNotBlank() } ?: emptyList()
                if (suggestions.isNotEmpty()) {
                    _dynamicSuggestions.value = suggestions
                }
            } catch (_: Exception) {
                // Fail silently
            }
        }
    }

    fun renameProject(id: Long, newName: String) {
        viewModelScope.launch {
            projectDao.updateProjectName(id, newName)
        }
    }

    private fun saveToHistory(html: String, name: String? = null) {
        viewModelScope.launch {
            val id = projectDao.insertProject(ProjectEntity(html = html, name = name))
            currentProjectId = id
        }
    }

    private fun updateCurrentInHistory(html: String) {
        val id = currentProjectId ?: return
        viewModelScope.launch {
            projectDao.updateProjectHtml(id, html)
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            projectDao.deleteProject(id)
            if (currentProjectId == id) currentProjectId = null
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            projectDao.clearHistory()
            currentProjectId = null
        }
    }

    private fun cleanHtml(content: String): String {
        return content
            .removePrefix("```html")
            .removePrefix("```jsx")
            .removePrefix("```javascript")
            .removePrefix("```kotlin")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }

    fun resetState() {
        _uiState.value = UiState.Idle
        chatSession = null
        currentProjectId = null
    }
}
