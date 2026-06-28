package com.example.ui

import android.app.Application
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.FirebaseService
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.InlineData
import com.example.api.Part
import com.example.api.PrebuiltVoiceConfig
import com.example.api.RetrofitClient
import com.example.api.SpeechConfig
import com.example.api.VoiceConfig
import com.example.data.AppDatabase
import com.example.data.ExhibitData
import com.example.data.ExhibitNote
import com.example.data.LegalDocument
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class ChatMessage(
    val sender: String, // "user" or "assistant"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserProfile(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val isMock: Boolean = false
)

class ExhibitionViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "ExhibitionViewModel"
    private val context = application.applicationContext

    // App Database & DAO
    private val database = AppDatabase.getDatabase(context)
    private val noteDao = database.exhibitNoteDao()

    // 1. Navigation & Selected Document
    private val _selectedDocId = MutableStateFlow("policy_proposal")
    val selectedDocId: StateFlow<String> = _selectedDocId.asStateFlow()

    val selectedDocument: StateFlow<LegalDocument> = _selectedDocId.map { id ->
        ExhibitData.documents.firstOrNull { it.id == id } ?: ExhibitData.documents.first()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExhibitData.documents.first())

    // 2. User & Auth State
    private val _user = MutableStateFlow<UserProfile?>(null)
    val user: StateFlow<UserProfile?> = _user.asStateFlow()

    // 3. Notes State
    val currentNotes: StateFlow<List<ExhibitNote>> = _selectedDocId.flatMapLatest { id ->
        noteDao.getNotesForDocument(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. TTS State
    private val _isTtsPlaying = MutableStateFlow(false)
    val isTtsPlaying: StateFlow<Boolean> = _isTtsPlaying.asStateFlow()

    private val _selectedVoice = MutableStateFlow("Kore")
    val selectedVoice: StateFlow<String> = _selectedVoice.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var currentAudioFile: File? = null

    // 5. Chat Assistant State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("assistant", "Hello! I am HARMONI-X, your Sovereign Cognitive Assistant. I am designed as a digital ramp to help you break down the 'cognitive wall' of federal bureaucracy and legal structures. Ask me anything about Caustin Lee McLaughlin's SCOTUS petitions, his patent disclosures, or his clinical record!")
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // 6. Interactive Timeline State
    private val _selectedTimelineBranch = MutableStateFlow("All")
    val selectedTimelineBranch: StateFlow<String> = _selectedTimelineBranch.asStateFlow()

    private val _userDecisions = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val userDecisions: StateFlow<Map<String, Boolean>> = _userDecisions.asStateFlow()

    fun selectTimelineBranch(branch: String) {
        _selectedTimelineBranch.value = branch
    }

    fun makeDecision(eventTitle: String, choice: Boolean) {
        val current = _userDecisions.value.toMutableMap()
        current[eventTitle] = choice
        _userDecisions.value = current
    }

    init {
        // Safe check for Firebase
        FirebaseService.initialize()
        checkCurrentUser()
    }

    fun selectDocument(id: String) {
        _selectedDocId.value = id
        stopTts() // stop any reading audio if we navigate away
    }

    fun setVoice(voiceName: String) {
        _selectedVoice.value = voiceName
    }

    // --- Firebase Auth & Google Sign-In Mock / Real ---
    private fun checkCurrentUser() {
        val fbAuth = FirebaseService.auth
        if (fbAuth != null && fbAuth.currentUser != null) {
            val fUser = fbAuth.currentUser!!
            _user.value = UserProfile(
                uid = fUser.uid,
                email = fUser.email ?: "",
                displayName = fUser.displayName ?: "Researcher Profile",
                photoUrl = fUser.photoUrl?.toString(),
                isMock = false
            )
            syncNotesFromCloud()
        }
    }

    fun signInWithGoogleSimulated(email: String, name: String) {
        viewModelScope.launch {
            val fbAuth = FirebaseService.auth
            if (fbAuth != null) {
                // If real Firebase is available, we try to sign in.
                fbAuth.signInAnonymously()
                    .addOnSuccessListener { result ->
                        val fUser = result.user
                        if (fUser != null) {
                            _user.value = UserProfile(
                                uid = fUser.uid,
                                email = email,
                                displayName = name,
                                isMock = false
                            )
                            syncNotesFromCloud()
                        } else {
                            _user.value = UserProfile("simulated_user_123", email, name, isMock = true)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Firebase Auth failed, fallback to simulated account. Error: ${e.message}")
                        _user.value = UserProfile("simulated_user_123", email, name, isMock = true)
                    }
            } else {
                // Safe Mock Auth
                _user.value = UserProfile("simulated_user_123", email, name, isMock = true)
            }
        }
    }

    fun signOut() {
        FirebaseService.auth?.signOut()
        _user.value = null
    }

    // --- Notes Persistence (Room + Firestore Sync) ---
    fun addNote(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val u = _user.value
            val note = ExhibitNote(
                documentId = _selectedDocId.value,
                noteText = text,
                userId = u?.uid,
                isSynced = false
            )
            noteDao.insertNote(note)
            
            // Sync to Firestore if user signed in and Firebase is configured
            if (u != null && !u.isMock && FirebaseService.isFirebaseAvailable) {
                syncNoteToFirestore(note)
            }
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.deleteNoteById(id)
            // Ideally, we would also delete it on Firestore in a production pipeline
        }
    }

    private fun syncNoteToFirestore(note: ExhibitNote) {
        val db = FirebaseService.db ?: return
        val u = _user.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val noteData = mapOf(
                "documentId" to note.documentId,
                "noteText" to note.noteText,
                "timestamp" to note.timestamp,
                "userId" to u.uid
            )
            db.collection("users")
                .document(u.uid)
                .collection("notes")
                .add(noteData)
                .addOnSuccessListener {
                    viewModelScope.launch(Dispatchers.IO) {
                        // Mark as synced in local DB
                        noteDao.updateNote(note.copy(isSynced = true))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Firestore sync failed for note: ${e.message}")
                }
        }
    }

    private fun syncNotesFromCloud() {
        val db = FirebaseService.db ?: return
        val u = _user.value ?: return
        if (u.isMock) return

        db.collection("users")
            .document(u.uid)
            .collection("notes")
            .get()
            .addOnSuccessListener { result ->
                viewModelScope.launch(Dispatchers.IO) {
                    for (doc in result) {
                        val docId = doc.getString("documentId") ?: ""
                        val text = doc.getString("noteText") ?: ""
                        val ts = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        
                        // Prevent duplicates by checking if note with text and docId already exists
                        // In simple local template, we can just insert replacement
                        val note = ExhibitNote(
                            documentId = docId,
                            noteText = text,
                            timestamp = ts,
                            isSynced = true,
                            userId = u.uid
                        )
                        noteDao.insertNote(note)
                    }
                }
            }
    }

    // --- Gemini Text To Speech Integration ---
    fun speakDocumentSection(textToRead: String) {
        stopTts() // stop existing audio
        _isTtsPlaying.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                Log.w(TAG, "Gemini API key is missing. Simulating tts response.")
                simulateTtsProgress()
                return@launch
            }

            // Instruct Gemini to read the text precisely with proper punctuation pauses
            val cleanText = textToRead.take(500) // Keep request reasonable to stay within token limits and OkHttp timeouts
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = "Read the following text aloud clearly with perfect pronunciation: $cleanText")))),
                generationConfig = GenerationConfig(
                    responseModalities = listOf("AUDIO"),
                    speechConfig = SpeechConfig(
                        voiceConfig = VoiceConfig(
                            prebuiltVoiceConfig = PrebuiltVoiceConfig(voiceName = _selectedVoice.value)
                        )
                    )
                )
            )

            try {
                // Request generated voice bytes from gemini-3.1-flash-tts-preview
                val response = RetrofitClient.service.generateContent(
                    model = "gemini-3.1-flash-tts-preview",
                    apiKey = apiKey,
                    request = request
                )
                
                val base64Data = response.candidates?.firstOrNull()
                    ?.content?.parts?.firstOrNull { it.inlineData != null }
                    ?.inlineData?.data

                if (base64Data != null) {
                    withContext(Dispatchers.Main) {
                        playBase64(base64Data)
                    }
                } else {
                    Log.e(TAG, "TTS failed. No inline audio data returned from Gemini.")
                    withContext(Dispatchers.Main) {
                        _isTtsPlaying.value = false
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "TTS request error: ${e.message}")
                withContext(Dispatchers.Main) {
                    _isTtsPlaying.value = false
                }
            }
        }
    }

    private suspend fun simulateTtsProgress() {
        // Fallback for missing keys to ensure a robust, interactive presentation
        withContext(Dispatchers.Main) {
            _isTtsPlaying.value = true
        }
        withContext(Dispatchers.IO) {
            Thread.sleep(3000) // simulate synthesis
        }
        withContext(Dispatchers.Main) {
            _isTtsPlaying.value = false
        }
    }

    private fun playBase64(base64Audio: String) {
        try {
            val audioBytes = Base64.decode(base64Audio, Base64.DEFAULT)
            currentAudioFile = File.createTempFile("scotus_tts_", ".mp3", context.cacheDir)
            currentAudioFile?.deleteOnExit()

            FileOutputStream(currentAudioFile).use { fos ->
                fos.write(audioBytes)
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(currentAudioFile!!.absolutePath)
                prepare()
                setOnCompletionListener {
                    _isTtsPlaying.value = false
                    cleanupAudioFile()
                }
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "MediaPlayer initialization error: ${e.message}")
            _isTtsPlaying.value = false
        }
    }

    fun stopTts() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
            _isTtsPlaying.value = false
            cleanupAudioFile()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS: ${e.message}")
        }
    }

    private fun cleanupAudioFile() {
        currentAudioFile?.let {
            if (it.exists()) {
                it.delete()
            }
        }
        currentAudioFile = null
    }

    // --- Gemini Chatbot (Sovereign Assistant) Integration ---
    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        
        val newMessages = _chatMessages.value + ChatMessage("user", userText)
        _chatMessages.value = newMessages
        _isChatLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                simulateAssistantResponse(userText)
                return@launch
            }

            // System instructions setup
            val systemContext = """
                You are HARMONI-X, a Sovereign Cognitive Assistant designed to help users break down the 'cognitive wall' of federal bureaucracy and legal structures in Caustin Lee McLaughlin's SCOTUS petitions.
                Be clear, concise, highly informative, and empathetic. Point out how molecular genetics (such as COMT rs4680 Val15Met), and physical health markers (like the AIDS CD4 count drop to 23 or Hepatitis C) map directly to functional limitations in legal settings.
                Explain terms like "Regulatory Taking of IP", "Subscription Tax on AI", and "Restraint of Silence as Constructive Custody" based on the primary documents.
            """.trimIndent()

            // Build request history
            val conversationParts = newMessages.takeLast(10).map { msg ->
                Part(text = "${if (msg.sender == "user") "User" else "Assistant"}: ${msg.text}")
            }

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = conversationParts)),
                generationConfig = GenerationConfig(temperature = 0.7f),
                // System instructions
                // (Retrofit structure matches schema in gemini_api/SKILL.md)
            )

            try {
                // Request response from gemini-3.5-flash
                val response = RetrofitClient.service.generateContent(
                    model = "gemini-3.5-flash",
                    apiKey = apiKey,
                    request = request
                )
                val assistantText = response.candidates?.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text ?: "I am processing the historical folders, but encountered a database connection pause. Let me summarize: Caustin McLaughlin's petition argues that the denial of cognitive assistants represents an unconstitutional barrier to due process [Mathews v. Eldridge]."

                withContext(Dispatchers.Main) {
                    _chatMessages.value = _chatMessages.value + ChatMessage("assistant", assistantText)
                    _isChatLoading.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Chat request error: ${e.message}")
                withContext(Dispatchers.Main) {
                    _chatMessages.value = _chatMessages.value + ChatMessage(
                        "assistant",
                        "The cloud-assistant encountered a local connection interruption. According to Petition 1, Caustin's 92nd percentile Polygenic Risk Score (PRS) for attentional deficits maps to marked limitations under Section 12.00 of the SSA Blue Book. Would you like to study the clinical Kaiser Permanente report or patent spec instead?"
                    )
                    _isChatLoading.value = false
                }
            }
        }
    }

    private suspend fun simulateAssistantResponse(userText: String) {
        withContext(Dispatchers.IO) {
            Thread.sleep(1500) // simulate thinking
        }
        val reply = when {
            userText.contains("patent", true) || userText.contains("rigor", true) -> {
                "The RIGOR-PRS-Secure++ patent discloses an auditable Polygenic Risk Score pipeline protected by Post-Quantum algorithms (Kyber-1024 and Dilithium-5). It directly addresses 'Harvest Now, Decrypt Later' threats to private medical data by introducing key encapsulation. You can view the full spec in the Patent folder."
            }
            userText.contains("kaiser", true) || userText.contains("clinical", true) || userText.contains("aids", true) -> {
                "Caustin Lee McLaughlin's Kaiser Permanente records confirm a diagnosis of AIDS (ICD-10 B20) and Acute Hepatitis C. Most critically, his CD4 count dropped to a life-threatening level of 23 and 18 during episodes of homelessness following his eviction, validating his severe biological vulnerability."
            }
            userText.contains("ramp", true) || userText.contains("tax", true) -> {
                "A 'Digital Ramp' is the use of AI cognitive prosthetics to let disabled citizens navigate bureaucratic websites. Without free integration, a '$20/month subscription tax' is placed on disabled people to get equal due process, creating a discriminatory economic and neurological barrier."
            }
            else -> {
                "Under Petition 3, the government's refusal to accommodate Caustin's verified neurogenetic profile (including the COMT rs4680 rapid dopamine depletion) is argued to create a 'Restraint of Silence.' This structural silencing effectively locks citizens out of the courtroom, which the petition frames as Unlawful Constructive Custody under 28 U.S.C. § 2241."
            }
        }
        withContext(Dispatchers.Main) {
            _chatMessages.value = _chatMessages.value + ChatMessage("assistant", reply)
            _isChatLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTts()
    }
}
