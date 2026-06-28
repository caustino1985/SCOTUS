package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ExhibitData
import com.example.data.ExhibitNote
import com.example.data.LegalDocument
import com.example.data.TimelineEvent
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

sealed class Screen(val route: String, val title: String) {
    object Gallery : Screen("gallery", "Exhibits")
    object Timeline : Screen("timeline", "Timeline")
    object Assistant : Screen("assistant", "HARMONI-X")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExhibitionApp(viewModel: ExhibitionViewModel) {
    var currentTab by remember { mutableStateOf<Screen>(Screen.Gallery) }
    var viewingDocumentId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            if (viewingDocumentId == null) {
                NavigationBar(
                    containerColor = SlateSurface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 8.dp
                ) {
                    val tabs = listOf(Screen.Gallery, Screen.Timeline, Screen.Assistant)
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = when (tab) {
                                        Screen.Gallery -> Icons.Default.List
                                        Screen.Timeline -> Icons.Default.Info
                                        Screen.Assistant -> Icons.Default.Person
                                    },
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(tab.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LegalGold,
                                selectedTextColor = LegalGold,
                                indicatorColor = SlateSurface.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (viewingDocumentId != null) {
                DocumentReaderScreen(
                    docId = viewingDocumentId!!,
                    viewModel = viewModel,
                    onBack = { viewingDocumentId = null }
                )
            } else {
                Crossfade(targetState = currentTab, label = "tab_fade") { screen ->
                    when (screen) {
                        Screen.Gallery -> GalleryScreen(
                            viewModel = viewModel,
                            onDocumentClick = { docId ->
                                viewModel.selectDocument(docId)
                                viewingDocumentId = docId
                            }
                        )
                        Screen.Timeline -> TimelineScreen(
                            viewModel = viewModel,
                            onEventDocClick = { docId ->
                                viewModel.selectDocument(docId)
                                viewingDocumentId = docId
                            }
                        )
                        Screen.Assistant -> AssistantScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GalleryScreen(viewModel: ExhibitionViewModel, onDocumentClick: (String) -> Unit) {
    val documents = remember { ExhibitData.documents }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(QuantumCyan.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(200f, 100f)
                    )
                )
                .border(1.dp, LegalGold.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = LegalGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THE SUPREME COURT OF THE UNITED STATES",
                        color = LegalGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Caustin Lee McLaughlin v. United States",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Consolidated Petitions Exhibit Archive",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Explore the primary legal briefs, scientific patent disclosures, and clinical evidence bridging molecular genetics, post-quantum security, and federal civil rights.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Grid/Categories Section
        Text(
            text = "EXHIBIT DIRECTORY",
            color = QuantumCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        documents.forEach { doc ->
            ExhibitCard(doc = doc, onClick = { onDocumentClick(doc.id) })
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ExhibitCard(doc: LegalDocument, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("exhibit_card_${doc.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (doc.category) {
                                "Policy" -> QuantumCyan.copy(alpha = 0.15f)
                                "Patent" -> ClinicalGreen.copy(alpha = 0.15f)
                                "SCOTUS Petition" -> LegalGold.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = doc.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (doc.category) {
                            "Policy" -> QuantumCyan
                            "Patent" -> ClinicalGreen
                            "SCOTUS Petition" -> LegalGold
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }

                Text(
                    text = doc.citation,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = doc.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = doc.summary,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = LegalGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Inspect Exhibit Document",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LegalGold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentReaderScreen(docId: String, viewModel: ExhibitionViewModel, onBack: () -> Unit) {
    val doc by viewModel.selectedDocument.collectAsStateWithLifecycle()
    val notes by viewModel.currentNotes.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isTtsPlaying.collectAsStateWithLifecycle()
    val selectedVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()

    var showNotesSection by remember { mutableStateOf(false) }
    var newNoteText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // App Bar / Top controls
        TopAppBar(
            title = {
                Text(
                    text = doc.title,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                // Voice configuration drop-down button (simplified selector)
                IconButton(
                    onClick = {
                        val nextVoice = when (selectedVoice) {
                            "Kore" -> "Puck"
                            "Puck" -> "Charon"
                            "Charon" -> "Fenrir"
                            else -> "Kore"
                        }
                        viewModel.setVoice(nextVoice)
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Select Voice",
                            tint = QuantumCyan
                        )
                        Text(
                            text = selectedVoice,
                            fontSize = 8.sp,
                            color = QuantumCyan,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }

                // AI Speech synthesis toggle
                IconButton(
                    onClick = {
                        if (isPlaying) viewModel.stopTts() else viewModel.speakDocumentSection(doc.contentMarkdown)
                    }
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = "Speech Synthesis",
                        tint = if (isPlaying) ErrorRed else LegalGold
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SlateSurface
            )
        )

        // Main reading content and study notes pane
        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Warning Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(LegalGold.copy(alpha = 0.1f))
                        .border(1.dp, LegalGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Security Warning",
                                tint = LegalGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sovereign Cognitive Aid Enabled",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LegalGold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "This exhibit supports integrated text-to-speech rendering utilizing model gemini-3.1-flash-tts-preview as a reasonable cognitive accommodation.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Legal/Brief Text Styling (Courier/Monospace inspired headers, tidy formatting)
                Text(
                    text = doc.contentMarkdown,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            // Bottom drawer/pane handle for study notes
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .offset(y = if (showNotesSection) 0.dp else (-40).dp),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = SlateSurface,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showNotesSection = !showNotesSection },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = null,
                                tint = LegalGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Personal Researcher Study Notes (${notes.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = LegalGold
                            )
                        }
                        Icon(
                            imageVector = if (showNotesSection) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = "Toggle Notes",
                            tint = TextSecondary
                        )
                    }

                    if (showNotesSection) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Text input for new note
                        OutlinedTextField(
                            value = newNoteText,
                            onValueChange = { newNoteText = it },
                            placeholder = { Text("Record study annotations, citations, or legal theories...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("note_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LegalGold,
                                cursorColor = LegalGold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (newNoteText.isNotBlank()) {
                                    viewModel.addNote(newNoteText)
                                    newNoteText = ""
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("add_note_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = LegalGold)
                        ) {
                            Text("Save Annotation", color = ObsidianBackground)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Render list of study notes
                        if (notes.isEmpty()) {
                            Text(
                                text = "No personal annotations recorded for this document yet.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            Box(modifier = Modifier.heightIn(max = 200.dp)) {
                                LazyColumn {
                                    items(notes) { note ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.surfaceVariant.copy(
                                                        alpha = 0.4f
                                                    ), RoundedCornerShape(8.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = note.noteText,
                                                    fontSize = 12.sp,
                                                    color = TextPrimary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = if (note.isSynced) Icons.Default.Check else Icons.Default.Warning,
                                                        contentDescription = null,
                                                        tint = if (note.isSynced) ClinicalGreen else LegalGold,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = if (note.isSynced) "Synced to Cloud" else "Offline Cache Only",
                                                        fontSize = 9.sp,
                                                        color = TextSecondary
                                                    )
                                                }
                                            }
                                            IconButton(onClick = { viewModel.deleteNote(note.id) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete note",
                                                    tint = ErrorRed,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
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
fun TimelineScreen(viewModel: ExhibitionViewModel, onEventDocClick: (String) -> Unit) {
    val timelineEvents = remember { ExhibitData.timeline }
    val selectedBranch by viewModel.selectedTimelineBranch.collectAsStateWithLifecycle()
    val userDecisions by viewModel.userDecisions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CASE TIMELINE",
                    color = LegalGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Explore branching neurogenetic, post-quantum, and procedural pathways.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Segmented Branch Selector
        Text(
            text = "SELECT COGNITIVE RESEARCH PATHWAY:",
            color = QuantumCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val branches = listOf(
            "All" to "Comprehensive",
            "Clinical/ADHD" to "Clinical & ADHD",
            "Quantum/PQC" to "Quantum & PQC",
            "Procedural/Monell" to "Procedural & Retaliation"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            branches.forEach { (branchKey, branchName) ->
                val isSelected = selectedBranch == branchKey
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) LegalGold.copy(alpha = 0.2f) else SlateSurface)
                        .border(
                            1.dp,
                            if (isSelected) LegalGold else BorderColor.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.selectTimelineBranch(branchKey) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = branchName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) LegalGold else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val filteredEvents = remember(selectedBranch) {
            if (selectedBranch == "All") timelineEvents else timelineEvents.filter { it.branch == selectedBranch }
        }

        if (filteredEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events found under this branch filter.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(filteredEvents) { event ->
                    TimelineItem(
                        event = event,
                        onDocClick = onEventDocClick,
                        viewModel = viewModel,
                        userDecisions = userDecisions
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineItem(
    event: TimelineEvent,
    onDocClick: (String) -> Unit,
    viewModel: ExhibitionViewModel,
    userDecisions: Map<String, Boolean>
) {
    val isTtsPlaying by viewModel.isTtsPlaying.collectAsStateWithLifecycle()
    val decisionChoice = userDecisions[event.title]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // Timeline stem graphics
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Timeline dot
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(
                        when (event.branch) {
                            "Clinical/ADHD" -> ClinicalGreen
                            "Quantum/PQC" -> QuantumCyan
                            "Procedural/Monell" -> ErrorRed
                            else -> LegalGold
                        }
                    )
            )
            // Stems
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                when (event.branch) {
                                    "Clinical/ADHD" -> ClinicalGreen
                                    "Quantum/PQC" -> QuantumCyan
                                    "Procedural/Monell" -> ErrorRed
                                    else -> LegalGold
                                }.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Card containing event details
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
                .testTag("timeline_card_${event.title.lowercase().replace(" ", "_")}"),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.date,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (event.branch) {
                            "Clinical/ADHD" -> ClinicalGreen
                            "Quantum/PQC" -> QuantumCyan
                            "Procedural/Monell" -> ErrorRed
                            else -> LegalGold
                        },
                        fontFamily = FontFamily.Monospace
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (event.audioSnippetText != null) {
                            IconButton(
                                onClick = {
                                    if (isTtsPlaying) {
                                        viewModel.stopTts()
                                    } else {
                                        viewModel.speakDocumentSection(event.audioSnippetText)
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isTtsPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                                    contentDescription = "Read Audio Snippet",
                                    tint = LegalGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BorderColor.copy(alpha = 0.4f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when (event.branch) {
                                    "Clinical/ADHD" -> "ADHD/CLINICAL"
                                    "Quantum/PQC" -> "QUANTUM/PQC"
                                    "Procedural/Monell" -> "PROCEDURAL/MONELL"
                                    else -> "ALL"
                                },
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = event.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )

                // Render play action indicator if audio exists
                if (event.audioSnippetText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(LegalGold.copy(alpha = 0.08f))
                            .clickable {
                                if (isTtsPlaying) viewModel.stopTts() else viewModel.speakDocumentSection(event.audioSnippetText)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = LegalGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Listen to Case Briefing Snippet",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LegalGold
                        )
                    }
                }

                // Cross Reference Document Button
                if (event.docIdRef != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onDocClick(event.docIdRef) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                tint = LegalGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cross-Reference Document",
                                fontSize = 10.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Interactive Decision Points/Branching paths
                if (event.decisionLabel != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderColor.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "DECISION NODE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuantumCyan,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = event.decisionLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.makeDecision(event.title, true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (decisionChoice == true) QuantumCyan else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = "YES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (decisionChoice == true) ObsidianBackground else TextPrimary
                            )
                        }

                        Button(
                            onClick = { viewModel.makeDecision(event.title, false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (decisionChoice == false) ErrorRed else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = "NO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (decisionChoice == false) ObsidianBackground else TextPrimary
                            )
                        }
                    }

                    // Display education outcome based on choice
                    if (decisionChoice != null && event.decisionOutcome != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (decisionChoice) QuantumCyan.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f)
                                )
                                .border(
                                    1.dp,
                                    if (decisionChoice) QuantumCyan.copy(alpha = 0.3f) else ErrorRed.copy(alpha = 0.3f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = event.decisionOutcome,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssistantScreen(viewModel: ExhibitionViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val userProfile by viewModel.user.collectAsStateWithLifecycle()

    var inputMessage by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Researcher Authentication Header
        if (userProfile == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStroke(1.dp, LegalGold.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "RESEARCHER ACCESS PORTAL (Real Firebase Sync)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LegalGold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Authenticate with a researcher profile to unlock cloud replication of annotations and case notes.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            placeholder = { Text("Name") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LegalGold,
                                cursorColor = LegalGold
                            )
                        )
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            placeholder = { Text("Email") },
                            modifier = Modifier
                                .weight(1.5f)
                                .padding(start = 4.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LegalGold,
                                cursorColor = LegalGold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (emailInput.isNotBlank() && nameInput.isNotBlank()) {
                                viewModel.signInWithGoogleSimulated(emailInput, nameInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LegalGold),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Connect Profile", color = ObsidianBackground)
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStroke(1.dp, ClinicalGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ClinicalGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUTHORIZED RESEARCHER PROFILE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClinicalGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userProfile!!.displayName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${userProfile!!.email} ${if (userProfile!!.isMock) "(Offline Local Cache)" else "(Cloud Firestore Active)"}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }

                    IconButton(onClick = { viewModel.signOut() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = ErrorRed
                        )
                    }
                }
            }
        }

        // Sovereign Chatbot Assistant Column
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(SlateSurface.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    reverseLayout = false
                ) {
                    items(messages) { message ->
                        ChatBubble(message = message)
                    }
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = LegalGold
                                )
                            }
                        }
                    }
                }

                // Divider line
                HorizontalDivider(color = BorderColor.copy(alpha = 0.3f))

                // Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = { Text("Ask about molecular indices, constructive custody...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputMessage.isNotBlank()) {
                                    viewModel.sendChatMessage(inputMessage)
                                    inputMessage = ""
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LegalGold,
                            cursorColor = LegalGold
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputMessage.isNotBlank()) {
                                viewModel.sendChatMessage(inputMessage)
                                inputMessage = ""
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(LegalGold)
                            .testTag("send_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = ObsidianBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "user"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 0.dp,
                            bottomEnd = if (isUser) 0.dp else 12.dp
                        )
                    )
                    .background(
                        if (isUser) LegalGold.copy(alpha = 0.2f) else SlateSurface
                    )
                    .border(
                        1.dp,
                        if (isUser) LegalGold.copy(alpha = 0.4f) else BorderColor.copy(alpha = 0.3f),
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 0.dp,
                            bottomEnd = if (isUser) 0.dp else 12.dp
                        )
                    )
                    .padding(12.dp)
                    .widthIn(max = 260.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isUser) "Researcher" else "Sovereign Assistant",
                fontSize = 9.sp,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
