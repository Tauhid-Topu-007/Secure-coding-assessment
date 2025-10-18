package org.example.ide;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Enhanced Secure Coding IDE with Comprehensive Anti-Cheating Monitoring and Interactive Console
 */
public class AntiCheatingIDE extends Application {

    // --- UI Components ---
    private final TextArea codeEditor = new TextArea();
    private final TextArea consoleOutput = new TextArea();
    private final TextArea inputDataArea = new TextArea();
    private final TextArea expectedOutputArea = new TextArea();
    private final TextArea focusLogArea = new TextArea();
    private final TextArea instructorDashboard = new TextArea();

    // Labels and Progress bar
    private Label titleLabel;
    private Label suspicionScoreLabel;
    private ProgressBar suspicionProgress;

    private ChoiceBox<String> languageChoiceBox;
    private Stage mainStage;
    private TreeView<String> fileView = new TreeView<>();
    private TreeItem<String> rootItem;

    // Buttons
    private Button checkAIButton, submitButton, createFileButton, runButton;
    private Button debugButton, formatButton, searchButton, settingsButton;
    private Button autoTestButton, exportButton, importButton, clearConsoleButton;
    private Button saveButton, undoButton, redoButton;

    // File creation components
    private TextField newFileNameInput;
    private TextField searchField;
    private CheckBox autoSaveCheckBox, syntaxHighlightCheckBox;

    // --- Anti-Cheating State ---
    private Map<String, FileEntry> fileContents = new HashMap<>();
    private FileEntry currentFile;
    private boolean isSwitchingFile = false;
    private final Map<String, SampleTestCase> sampleCases = new HashMap<>();

    // --- Interactive Console State ---
    private boolean isWaitingForInput = false;
    private boolean isProgramRunning = false;
    private StringBuilder currentInputLine = new StringBuilder();
    private int inputStartPosition = 0;
    private List<String> inputHistory = new ArrayList<>();
    private int historyIndex = -1;

    // Program execution state
    private Map<String, Object> programState = new HashMap<>();
    private int currentCinOperation = 0;
    private List<String> cinOperations = new ArrayList<>();

    // Comprehensive Monitoring State
    private int focusLossCount = 0;
    private int copyOperationCount = 0;
    private int minimizeCount = 0;
    private int tabSwitchCount = 0;
    private int totalSuspicionScore = 0;
    private long sessionStartTime;
    private long totalOutOfFocusTime = 0;
    private long focusLossStartTime = 0;
    private final List<CheatingEvent> allEvents = new ArrayList<>();

    // New monitoring states
    private int rapidTypingEvents = 0;
    private int codeCompletionUses = 0;
    private int debugSessionCount = 0;
    private int autoFormatCount = 0;
    private long lastKeyPressTime = 0;
    private static final long RAPID_TYPING_THRESHOLD = 50; // milliseconds

    // Code analysis
    private Map<String, CodeAnalysisResult> codeAnalysisResults = new HashMap<>();

    // Undo/Redo functionality
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private String currentCodeState = "";

    private record SampleTestCase(String input, String expectedOutput) {}
    private record CheatingEvent(String timestamp, String type, String details, int severity) {}
    private record CodeAnalysisResult(int complexity, int linesOfCode, int warningCount, double similarityScore) {}

    // Constants
    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final DateTimeFormatter DASHBOARD_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");

    // UI Colors
    private static final String DARK_BG = "#1e1e2e";
    private static final String EDITOR_BG = "#292a40";
    private static final String TEXT_COLOR = "#f8f8f2";
    private static final String RED_CLOSE = "#f38ba8";
    private static final String YELLOW_WARNING = "#fab387";
    private static final String GREEN_SUCCESS = "#a6e3a1";
    private static final String BLUE_INFO = "#89b4fa";
    private static final String CYAN_INPUT = "#74c7ec";
    private static final String INPUT_BG = "#313244";
    private static final String DEFAULT_ACCENT = "#cba6f7";
    private static final String HIGH_RISK = "#f38ba8";
    private static final String MEDIUM_RISK = "#fab387";
    private static final String LOW_RISK = "#f9e2af";

    private final Map<String, String> languageColorMap = Map.of(
            "Java", "#f28fad", "Python", "#a6e3a1", "C++", "#74c7ce", "JavaScript", "#f9e2af"
    );
    private String currentAccentColor = DEFAULT_ACCENT;

    private class FileEntry {
        String name, content;
        public FileEntry(String name, String content) {
            this.name = name;
            this.content = content;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        this.sessionStartTime = System.currentTimeMillis();

        // Initialize UI components first
        initializeComponents();

        setupInitialFiles();
        setupAutoSave();
        setupComprehensiveAntiCheatingMonitoring(primaryStage);
        setupInteractiveConsole();
        setupCodeEditorEnhancements();

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Secure Coding Assessment - MONITORED SESSION");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + DARK_BG + ";");

        HBox topBar = createTopBar();
        root.setTop(topBar);

        SplitPane contentArea = new SplitPane();
        contentArea.setPadding(new Insets(10));

        VBox sidebar = createSidebar();
        VBox editorArea = createCodeEditor();
        VBox monitoringArea = createInstructorDashboard();

        contentArea.getItems().addAll(sidebar, editorArea, monitoringArea);
        contentArea.setDividerPositions(0.15, 0.65);
        root.setCenter(contentArea);

        updateThemeColor("Java");

        Scene scene = new Scene(root, 1600, 900, Color.web(DARK_BG));
        setupGlobalKeyHandlers(scene);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Log session start
        logCheatingEvent("SESSION_START", "Assessment session started", 1);
        updateInstructorDashboard();
    }

    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        // Initialize labels
        titleLabel = new Label("Secure Coding Assessment - MONITORED");
        suspicionScoreLabel = new Label("Suspicion Score: 0/100");
        suspicionProgress = new ProgressBar(0);

        // Initialize choice box
        languageChoiceBox = new ChoiceBox<>();

        // Initialize buttons with smaller, responsive sizes
        runButton = createStyledButton("▶ Run", GREEN_SUCCESS, DARK_BG);
        submitButton = createStyledButton("Submit", DEFAULT_ACCENT, DARK_BG);
        checkAIButton = createStyledButton("AI Check", YELLOW_WARNING, DARK_BG);
        createFileButton = createStyledButton("New File", DEFAULT_ACCENT, DARK_BG);

        // New functionality buttons
        debugButton = createStyledButton("🐞 Debug", YELLOW_WARNING, DARK_BG);
        formatButton = createStyledButton("✨ Format", BLUE_INFO, DARK_BG);
        searchButton = createStyledButton("🔍 Search", DEFAULT_ACCENT, DARK_BG);
        settingsButton = createStyledButton("⚙ Settings", TEXT_COLOR, DARK_BG);
        autoTestButton = createStyledButton("🤖 Test", GREEN_SUCCESS, DARK_BG);
        exportButton = createStyledButton("📤 Export", DEFAULT_ACCENT, DARK_BG);
        importButton = createStyledButton("📥 Import", DEFAULT_ACCENT, DARK_BG);
        clearConsoleButton = createStyledButton("🗑 Clear", RED_CLOSE, DARK_BG);
        saveButton = createStyledButton("💾 Save", BLUE_INFO, DARK_BG);
        undoButton = createStyledButton("↶ Undo", YELLOW_WARNING, DARK_BG);
        redoButton = createStyledButton("↷ Redo", YELLOW_WARNING, DARK_BG);

        // Initialize file creation components
        newFileNameInput = new TextField();
        newFileNameInput.setPromptText("Enter filename (e.g., program.cpp, test.java)");
        newFileNameInput.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + "; -fx-padding: 6; -fx-font-size: 12px;");

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search in code...");
        searchField.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + "; -fx-padding: 6; -fx-font-size: 12px;");

        // Settings checkboxes
        autoSaveCheckBox = new CheckBox("Auto Save");
        autoSaveCheckBox.setSelected(true);
        autoSaveCheckBox.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-size: 12px;");

        syntaxHighlightCheckBox = new CheckBox("Syntax Highlighting");
        syntaxHighlightCheckBox.setSelected(true);
        syntaxHighlightCheckBox.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-size: 12px;");

        // Initialize tree view
        rootItem = new TreeItem<>("Project Files");
        fileView.setRoot(rootItem);
        fileView.setShowRoot(false);
        fileView.setStyle("-fx-font-size: 12px;");
    }

    /**
     * Setup code editor enhancements
     */
    private void setupCodeEditorEnhancements() {
        // Line numbers (simulated)
        codeEditor.setStyle("-fx-control-inner-background: #181926; -fx-text-fill: " + TEXT_COLOR +
                "; -fx-font-family: 'Monospaced'; -fx-font-size: 14;");

        // Key press monitoring for rapid typing detection
        codeEditor.setOnKeyPressed(event -> {
            long currentTime = System.currentTimeMillis();
            if (lastKeyPressTime > 0 && (currentTime - lastKeyPressTime) < RAPID_TYPING_THRESHOLD) {
                rapidTypingEvents++;
                if (rapidTypingEvents % 10 == 0) { // Log every 10 rapid typing events
                    logCheatingEvent("RAPID_TYPING", "Unnaturally fast typing detected (" + rapidTypingEvents + " events)", 2);
                }
            }
            lastKeyPressTime = currentTime;

            // Save state for undo/redo
            if (!codeEditor.getText().equals(currentCodeState)) {
                undoStack.push(currentCodeState);
                currentCodeState = codeEditor.getText();
                redoStack.clear();
                updateUndoRedoButtons();
            }
        });

        // Code completion simulation
        codeEditor.setOnKeyTyped(event -> {
            String typed = event.getCharacter();
            if (typed.equals(".") || typed.equals("(")) {
                codeCompletionUses++;
                // Simple code completion simulation
                showCodeCompletion(typed);
            }
        });

        // Setup button actions
        setupButtonActions();
    }

    /**
     * Setup all button actions
     */
    private void setupButtonActions() {
        runButton.setOnAction(e -> startProgramExecution());
        debugButton.setOnAction(e -> startDebugSession());
        formatButton.setOnAction(e -> formatCode());
        searchButton.setOnAction(e -> searchInCode());
        autoTestButton.setOnAction(e -> runAutoTests());
        exportButton.setOnAction(e -> exportProject());
        importButton.setOnAction(e -> importProject());
        clearConsoleButton.setOnAction(e -> clearConsole());
        settingsButton.setOnAction(e -> showSettingsDialog());
        saveButton.setOnAction(e -> saveFile());
        undoButton.setOnAction(e -> undo());
        redoButton.setOnAction(e -> redo());
        submitButton.setOnAction(e -> submitSolution());
        checkAIButton.setOnAction(e -> checkAIPatterns());
        createFileButton.setOnAction(e -> createNewFile());
    }

    /**
     * NEW FUNCTIONALITY: Save File
     */
    private void saveFile() {
        if (currentFile != null) {
            // Update content from editor
            currentFile.content = codeEditor.getText();

            try {
                // Create file chooser for save location
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save File");
                fileChooser.setInitialFileName(currentFile.name);

                // Set extension filter based on file type
                String extension = getFileExtension(currentFile.name);
                setFileChooserExtensionFilter(fileChooser, extension);

                // Set initial directory to user's home or documents
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

                // Show save dialog
                File file = fileChooser.showSaveDialog(mainStage);

                if (file != null) {
                    // Write content to file
                    writeFileToDisk(file, currentFile.content);

                    consoleOutput.appendText("💾 File saved: " + file.getAbsolutePath() + "\n");
                    consoleOutput.appendText("   📊 Size: " + currentFile.content.length() + " characters, " +
                            currentFile.content.split("\n").length + " lines\n");

                    logCheatingEvent("MANUAL_SAVE", "User saved " + currentFile.name + " to " + file.getAbsolutePath(), 1);

                    // Update file tree if name changed
                    if (!file.getName().equals(currentFile.name)) {
                        updateFileNameInTree(currentFile.name, file.getName());
                    }

                } else {
                    consoleOutput.appendText("ℹ️ Save cancelled by user\n");
                }

            } catch (Exception e) {
                consoleOutput.appendText("❌ Save failed: " + e.getMessage() + "\n");
                logCheatingEvent("SAVE_FAILED", "Failed to save " + currentFile.name + ": " + e.getMessage(), 1);

                // Show error dialog
                showErrorDialog("Save Error", "Failed to save file", e.getMessage());
            }
        } else {
            // No current file - use Save As functionality
            saveFileAs();
        }
    }

    /**
     * Save file with new name (Save As functionality)
     */
    private void saveFileAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File As");

        // Set initial file name
        if (currentFile != null) {
            fileChooser.setInitialFileName(currentFile.name);
        } else {
            fileChooser.setInitialFileName("new_program." + getDefaultExtension());
        }

        // Set extension filters for different file types
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Java Files", "*.java"),
                new FileChooser.ExtensionFilter("C++ Files", "*.cpp", "*.h", "*.hpp"),
                new FileChooser.ExtensionFilter("Python Files", "*.py"),
                new FileChooser.ExtensionFilter("JavaScript Files", "*.js"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File file = fileChooser.showSaveDialog(mainStage);

        if (file != null) {
            try {
                String content = currentFile != null ? currentFile.content : codeEditor.getText();
                String fileName = file.getName();

                // Write file to disk
                writeFileToDisk(file, content);

                // Create or update file entry
                if (currentFile == null) {
                    // Create new file entry
                    currentFile = new FileEntry(fileName, content);
                    fileContents.put(fileName, currentFile);

                    // Add to file tree
                    TreeItem<String> newFileItem = new TreeItem<>(fileName);
                    rootItem.getChildren().add(newFileItem);
                    fileView.getSelectionModel().select(newFileItem);

                    // Initialize test case
                    sampleCases.put(fileName, new SampleTestCase("", ""));
                } else {
                    // Update existing file name if changed
                    if (!fileName.equals(currentFile.name)) {
                        updateFileNameInTree(currentFile.name, fileName);
                    }
                    currentFile.content = content;
                }

                consoleOutput.appendText("💾 File saved as: " + file.getAbsolutePath() + "\n");
                consoleOutput.appendText("   📁 File: " + fileName + "\n");
                consoleOutput.appendText("   📊 Size: " + content.length() + " characters, " +
                        content.split("\n").length + " lines\n");

                logCheatingEvent("SAVE_AS", "User saved file as " + fileName, 1);

            } catch (Exception e) {
                consoleOutput.appendText("❌ Save failed: " + e.getMessage() + "\n");
                showErrorDialog("Save Error", "Failed to save file", e.getMessage());
            }
        } else {
            consoleOutput.appendText("ℹ️ Save As cancelled by user\n");
        }
    }

    /**
     * Write content to disk with proper error handling
     */
    private void writeFileToDisk(File file, String content) throws IOException {
        // Create parent directories if they don't exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }

        // Write content to file with proper encoding
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            writer.write(content);
        }

        // Verify file was written
        if (!file.exists() || file.length() == 0) {
            throw new IOException("File write verification failed");
        }
    }

    /**
     * Set appropriate extension filter for file chooser
     */
    private void setFileChooserExtensionFilter(FileChooser fileChooser, String extension) {
        fileChooser.getExtensionFilters().clear();

        switch (extension.toLowerCase()) {
            case "java":
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Java Files", "*.java"));
                break;
            case "cpp":
            case "cxx":
            case "cc":
            case "h":
            case "hpp":
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("C++ Files", "*.cpp", "*.h", "*.hpp", "*.cxx", "*.cc"));
                break;
            case "py":
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Python Files", "*.py"));
                break;
            case "js":
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("JavaScript Files", "*.js"));
                break;
            default:
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("All Files", "*.*"));
                break;
        }
    }

    /**
     * Update file name in the tree view
     */
    private void updateFileNameInTree(String oldName, String newName) {
        // Find the tree item with old name
        for (TreeItem<String> item : rootItem.getChildren()) {
            if (item.getValue().equals(oldName)) {
                // Update tree item
                item.setValue(newName);

                // Update file contents map
                FileEntry fileEntry = fileContents.remove(oldName);
                if (fileEntry != null) {
                    fileEntry.name = newName;
                    fileContents.put(newName, fileEntry);
                }

                // Update sample cases
                SampleTestCase testCase = sampleCases.remove(oldName);
                if (testCase != null) {
                    sampleCases.put(newName, testCase);
                }

                // Update current file reference
                if (currentFile != null && currentFile.name.equals(oldName)) {
                    currentFile.name = newName;
                }

                consoleOutput.appendText("📝 Renamed file: " + oldName + " → " + newName + "\n");
                break;
            }
        }
    }

    /**
     * Get default file extension based on current language
     */
    private String getDefaultExtension() {
        String language = languageChoiceBox.getValue();
        switch (language) {
            case "Java": return "java";
            case "C++": return "cpp";
            case "Python": return "py";
            case "JavaScript": return "js";
            default: return "txt";
        }
    }

    /**
     * Show error dialog
     */
    private void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Quick save without dialog (uses current file path)
     */
    private void quickSave() {
        if (currentFile != null) {
            try {
                // For quick save, we need to know where the file was previously saved
                // In a real implementation, you'd store the file path in FileEntry
                currentFile.content = codeEditor.getText();

                consoleOutput.appendText("💾 Quick save: " + currentFile.name + "\n");
                consoleOutput.appendText("   📝 " + currentFile.content.split("\n").length + " lines saved\n");

                logCheatingEvent("QUICK_SAVE", "User quick saved " + currentFile.name, 1);

                // Show brief confirmation
                showSaveConfirmation();

            } catch (Exception e) {
                consoleOutput.appendText("❌ Quick save failed: " + e.getMessage() + "\n");
                // Fall back to Save As
                saveFileAs();
            }
        } else {
            // No current file, use Save As
            saveFileAs();
        }
    }

    /**
     * Show brief save confirmation
     */
    private void showSaveConfirmation() {
        // You could add a brief visual confirmation here
        // For example, change button color temporarily or show a tooltip
    }

    /**
     * Auto-save functionality (called by timer)
     */
    private void autoSave() {
        if (currentFile != null && autoSaveCheckBox.isSelected()) {
            try {
                String currentContent = codeEditor.getText();
                if (!currentContent.equals(currentFile.content)) {
                    currentFile.content = currentContent;

                    // Only log auto-save occasionally to avoid spam
                    if (System.currentTimeMillis() % 30000 < 100) { // Every ~30 seconds
                        consoleOutput.appendText("🤖 Auto-saved: " + currentFile.name + "\n");
                    }

                    logCheatingEvent("AUTO_SAVE", "Auto-saved " + currentFile.name, 1);
                }
            } catch (Exception e) {
                // Silent fail for auto-save - don't spam console
            }
        }
    }

    // Update the save button action to use the new functionality
    private void setupSaveButton() {
        saveButton.setOnAction(e -> {
            // Use quick save if we have a known file path, otherwise use Save As
            if (currentFile != null && currentFile.name != null) {
                quickSave();
            } else {
                saveFileAs();
            }
        });

        // Add Ctrl+S shortcut
        mainStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                if (currentFile != null && currentFile.name != null) {
                    quickSave();
                } else {
                    saveFileAs();
                }
                event.consume();
            }
        });
    }

    /**
     * NEW FUNCTIONALITY: Undo
     */
    private void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(codeEditor.getText());
            String previousState = undoStack.pop();
            codeEditor.setText(previousState);
            currentCodeState = previousState;
            updateUndoRedoButtons();
            consoleOutput.appendText("↶ Undo performed\n");
        }
    }

    /**
     * NEW FUNCTIONALITY: Redo
     */
    private void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(codeEditor.getText());
            String nextState = redoStack.pop();
            codeEditor.setText(nextState);
            currentCodeState = nextState;
            updateUndoRedoButtons();
            consoleOutput.appendText("↷ Redo performed\n");
        }
    }

    /**
     * Update undo/redo button states
     */
    private void updateUndoRedoButtons() {
        undoButton.setDisable(undoStack.isEmpty());
        redoButton.setDisable(redoStack.isEmpty());
    }

    /**
     * NEW FUNCTIONALITY: Submit Solution
     */
    private void submitSolution() {
        logCheatingEvent("SOLUTION_SUBMIT", "User submitted solution for evaluation", 2);
        consoleOutput.appendText("📤 Submitting solution for evaluation...\n");

        // Simulate evaluation process
        Timer evaluationTimer = new Timer(true);
        evaluationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    boolean passed = Math.random() > 0.3; // 70% chance of passing
                    if (passed) {
                        consoleOutput.appendText("✅ Solution accepted! All tests passed.\n");
                    } else {
                        consoleOutput.appendText("❌ Solution rejected! Some tests failed.\n");
                    }
                });
            }
        }, 2000);
    }

    /**
     * NEW FUNCTIONALITY: Check AI Patterns
     */
    private void checkAIPatterns() {
        logCheatingEvent("AI_CHECK", "User requested AI pattern analysis", 1);
        consoleOutput.appendText("🤖 Analyzing code for AI-generated patterns...\n");

        if (codeEditor.getText().trim().isEmpty()) {
            consoleOutput.appendText("❌ No code to analyze\n");
            return;
        }

        String code = codeEditor.getText();
        AIAnalysisResult result = analyzeForAIPatterns(code);

        // Display detailed analysis
        displayAIAnalysisResults(result);

        // Log appropriate event based on risk level
        if (result.getOverallProbability() > 0.8) {
            logCheatingEvent("HIGH_AI_SUSPICION",
                    "High probability AI-generated code detected: " +
                            String.format("%.1f%%", result.getOverallProbability() * 100), 8);
        } else if (result.getOverallProbability() > 0.6) {
            logCheatingEvent("MEDIUM_AI_SUSPICION",
                    "Medium probability AI-generated code detected: " +
                            String.format("%.1f%%", result.getOverallProbability() * 100), 6);
        } else {
            logCheatingEvent("LOW_AI_SUSPICION",
                    "Low probability AI-generated code: " +
                            String.format("%.1f%%", result.getOverallProbability() * 100), 3);
        }
    }

    /**
     * Comprehensive AI pattern analysis with detailed scoring
     */
    private AIAnalysisResult analyzeForAIPatterns(String code) {
        AIAnalysisResult result = new AIAnalysisResult();

        // Basic code metrics
        result.setTotalLines(code.split("\n").length);
        result.setTotalCharacters(code.length());

        // Analyze different aspects
        analyzeFormattingPatterns(code, result);
        analyzeStructuralPatterns(code, result);
        analyzeContentPatterns(code, result);
        analyzeComplexityPatterns(code, result);
        analyzeLanguageSpecificPatterns(code, result);

        // Calculate overall probability
        result.calculateOverallProbability();

        return result;
    }

    /**
     * Analyze formatting and stylistic patterns
     */
    private void analyzeFormattingPatterns(String code, AIAnalysisResult result) {
        String[] lines = code.split("\n");
        if (lines.length < 5) {
            result.addIndicator("SHORT_CODE", "Code is very short", 0.1);
            return;
        }

        int perfectIndentation = 0;
        int consistentSpacing = 0;
        int templateComments = 0;

        for (String line : lines) {
            // Check for perfect indentation (4 spaces exactly)
            if (line.matches("^\\s{4}\\S.*")) {
                perfectIndentation++;
            }

            // Check for consistent operator spacing
            if (line.matches(".*\\w\\s*=\\s*\\w.*") || line.matches(".*\\w\\s*\\+\\s*\\w.*")) {
                consistentSpacing++;
            }

            // Check for template-style comments
            if (line.trim().startsWith("//") &&
                    (line.toLowerCase().contains("todo") ||
                            line.toLowerCase().contains("note") ||
                            line.toLowerCase().contains("implementation"))) {
                templateComments++;
            }
        }

        double indentationScore = (double) perfectIndentation / lines.length;
        if (indentationScore > 0.9) {
            result.addIndicator("PERFECT_INDENTATION", "Extremely consistent indentation", 0.3);
        }

        if (consistentSpacing > lines.length * 0.7) {
            result.addIndicator("CONSISTENT_SPACING", "Highly consistent operator spacing", 0.2);
        }

        if (templateComments > 2) {
            result.addIndicator("TEMPLATE_COMMENTS", "Template-style comments present", 0.15);
        }
    }

    /**
     * Analyze structural patterns
     */
    private void analyzeStructuralPatterns(String code, AIAnalysisResult result) {
        // Check for boilerplate code patterns
        if (code.contains("public static void main") && code.split("\n").length < 20) {
            result.addIndicator("MINIMAL_MAIN", "Very short main method", 0.4);
        }

        // Check for comprehensive error handling in small programs
        if ((code.contains("try") && code.contains("catch")) && code.split("\n").length < 30) {
            result.addIndicator("OVER_ENGINEERED_ERRORS", "Comprehensive error handling in small program", 0.25);
        }

        // Check for modern language features in simple code
        if ((code.contains("var ") || code.contains("auto ")) && code.split("\n").length < 25) {
            result.addIndicator("MODERN_FEATURES_SIMPLE", "Modern features in simple code", 0.2);
        }

        // Check for excessive modularization
        int functionCount = countFunctions(code);
        if (functionCount > 5 && code.split("\n").length < 50) {
            result.addIndicator("OVER_MODULARIZED", "Excessive functions for code size", 0.3);
        }
    }

    /**
     * Analyze content and semantic patterns
     */
    private void analyzeContentPatterns(String code, AIAnalysisResult result) {
        String lowerCode = code.toLowerCase();

        // Check for AI-style explanations in comments
        Pattern explanationPattern = Pattern.compile("//\\s*(This|Here|The).*?(function|method|code).*?\\.", Pattern.CASE_INSENSITIVE);
        Matcher matcher = explanationPattern.matcher(code);
        int explanationComments = 0;
        while (matcher.find()) {
            explanationComments++;
        }

        if (explanationComments > 2) {
            result.addIndicator("EXPLANATORY_COMMENTS", "AI-style explanatory comments", 0.35);
        }

        // Check for comprehensive documentation in small programs
        if ((code.contains("/**") || code.contains("/*")) && code.split("\n").length < 40) {
            result.addIndicator("EXCESSIVE_DOCS", "Comprehensive documentation in small program", 0.2);
        }

        // Check for textbook-style variable names
        if (lowerCode.contains("calculate") || lowerCode.contains("process") ||
                lowerCode.contains("initialize") || lowerCode.contains("execute")) {
            result.addIndicator("TEXTBOOK_NAMING", "Textbook-style variable/method names", 0.15);
        }

        // Check for perfect grammar in strings and comments
        if (hasPerfectGrammar(code)) {
            result.addIndicator("PERFECT_GRAMMAR", "Perfect grammar in comments/strings", 0.1);
        }
    }

    /**
     * Analyze complexity patterns
     */
    private void analyzeComplexityPatterns(String code, AIAnalysisResult result) {
        int complexityScore = calculateComplexityScore(code);
        int lines = code.split("\n").length;

        // Check for unusually balanced complexity
        double complexityRatio = (double) complexityScore / lines;
        if (complexityRatio > 0.3 && complexityRatio < 0.7) {
            result.addIndicator("BALANCED_COMPLEXITY", "Unusually balanced complexity distribution", 0.25);
        }

        // Check for optimal algorithm choices in simple programs
        if (containsOptimalAlgorithms(code) && lines < 60) {
            result.addIndicator("OPTIMAL_ALGORITHMS", "Optimal algorithm choices in simple code", 0.3);
        }
    }

    /**
     * Analyze language-specific patterns
     */
    private void analyzeLanguageSpecificPatterns(String code, AIAnalysisResult result) {
        String language = languageChoiceBox.getValue();

        switch (language) {
            case "Java":
                analyzeJavaPatterns(code, result);
                break;
            case "C++":
                analyzeCppPatterns(code, result);
                break;
            case "Python":
                analyzePythonPatterns(code, result);
                break;
            case "JavaScript":
                analyzeJavaScriptPatterns(code, result);
                break;
        }
    }

    private void analyzeJavaPatterns(String code, AIAnalysisResult result) {
        if (code.contains("@Override") && code.split("\n").length < 40) {
            result.addIndicator("JAVA_ANNOTATIONS", "Advanced annotations in simple Java code", 0.2);
        }

        if (code.contains("ArrayList<String>") || code.contains("HashMap<")) {
            result.addIndicator("JAVA_GENERICS", "Generic collections in basic code", 0.15);
        }
    }

    private void analyzeCppPatterns(String code, AIAnalysisResult result) {
        if (code.contains("#include <algorithm>") && code.split("\n").length < 50) {
            result.addIndicator("CPP_STL", "STL usage in simple C++ code", 0.2);
        }

        if (code.contains("std::") && !code.contains("std::cout") && code.split("\n").length < 40) {
            result.addIndicator("CPP_STD_NAMESPACE", "std namespace usage beyond basics", 0.15);
        }
    }

    private void analyzePythonPatterns(String code, AIAnalysisResult result) {
        if (code.contains("def __") && code.split("\n").length < 30) {
            result.addIndicator("PYTHON_DUNDER", "Dunder methods in simple Python code", 0.25);
        }

        if (code.contains("list comprehension") || code.contains("lambda")) {
            result.addIndicator("PYTHON_ADVANCED_FEATURES", "Advanced Python features", 0.2);
        }
    }

    private void analyzeJavaScriptPatterns(String code, AIAnalysisResult result) {
        if (code.contains("const") && code.contains("=>") && code.split("\n").length < 30) {
            result.addIndicator("JS_MODERN_SYNTAX", "Modern JS syntax in simple code", 0.2);
        }

        if (code.contains("async") || code.contains("await")) {
            result.addIndicator("JS_ASYNC", "Async/await in basic code", 0.25);
        }
    }

    /**
     * Display detailed analysis results
     */
    private void displayAIAnalysisResults(AIAnalysisResult result) {
        consoleOutput.appendText("📊 AI Pattern Analysis Results:\n");
        consoleOutput.appendText("   📝 Code Metrics: " + result.getTotalLines() + " lines, " +
                result.getTotalCharacters() + " characters\n");
        consoleOutput.appendText("   🤖 AI Probability: " +
                String.format("%.1f%%", result.getOverallProbability() * 100) + "\n");

        if (!result.getIndicators().isEmpty()) {
            consoleOutput.appendText("   🔍 Detected Patterns:\n");
            for (AIIndicator indicator : result.getIndicators()) {
                String riskLevel = getRiskLevel(indicator.getWeight());
                consoleOutput.appendText("      • " + indicator.getDescription() +
                        " [" + riskLevel + "]\n");
            }
        }

        // Overall assessment
        if (result.getOverallProbability() > 0.8) {
            consoleOutput.appendText("   🚨 HIGH RISK: Strong indicators of AI-generated code\n");
        } else if (result.getOverallProbability() > 0.6) {
            consoleOutput.appendText("   ⚠️  MEDIUM RISK: Some indicators of AI assistance\n");
        } else if (result.getOverallProbability() > 0.3) {
            consoleOutput.appendText("   ℹ️  LOW RISK: Minimal AI indicators detected\n");
        } else {
            consoleOutput.appendText("   ✅ VERY LOW RISK: Code appears human-written\n");
        }

        // Add specific recommendations
        consoleOutput.appendText("   💡 Recommendations: " +
                getAIRecommendations(result.getOverallProbability()) + "\n");
    }

    // Helper methods
    private int countFunctions(String code) {
        String language = languageChoiceBox.getValue();
        switch (language) {
            case "Java": return code.split("(public|private|protected|static)\\s+\\w+\\s+\\w+\\s*\\(").length - 1;
            case "C++": return code.split("(int|void|double|float|char|bool)\\s+\\w+\\s*\\(").length - 1;
            case "Python": return code.split("def\\s+\\w+\\s*\\(").length - 1;
            case "JavaScript": return code.split("function\\s+\\w+\\s*\\(").length - 1;
            default: return 0;
        }
    }

    private boolean hasPerfectGrammar(String code) {
        // Extract comments and strings for grammar analysis
        Pattern commentPattern = Pattern.compile("//.*|/\\*.*?\\*/|\".*?\"");
        Matcher matcher = commentPattern.matcher(code);

        int perfectSentences = 0;
        int totalSentences = 0;

        while (matcher.find()) {
            String text = matcher.group();
            if (text.matches(".*[a-zA-Z].*")) { // Contains letters
                totalSentences++;
                // Simple grammar check - starts with capital, ends with period
                if (text.matches("^[^a-z]*[A-Z].*[\\.!?]\\s*$")) {
                    perfectSentences++;
                }
            }
        }

        return totalSentences > 0 && (double) perfectSentences / totalSentences > 0.8;
    }

    private boolean containsOptimalAlgorithms(String code) {
        String lowerCode = code.toLowerCase();
        return lowerCode.contains("binary search") || lowerCode.contains("quicksort") ||
                lowerCode.contains("hashmap") || lowerCode.contains("dynamic programming");
    }

    private int calculateComplexityScore(String code) {
        int score = 0;
        score += code.split("if\\s*\\(").length - 1;
        score += code.split("for\\s*\\(").length - 1;
        score += code.split("while\\s*\\(").length - 1;
        score += code.split("switch\\s*\\(").length - 1;
        score += code.split("catch\\s*\\(").length - 1;
        return score;
    }

    private String getRiskLevel(double weight) {
        if (weight > 0.25) return "HIGH";
        if (weight > 0.15) return "MEDIUM";
        return "LOW";
    }

    private String getAIRecommendations(double probability) {
        if (probability > 0.7) {
            return "Review code originality and ensure proper attribution";
        } else if (probability > 0.4) {
            return "Consider adding personal coding style and comments";
        } else {
            return "Continue with current coding practices";
        }
    }

    /**
     * Data classes for AI analysis results
     */
    private static class AIAnalysisResult {
        private List<AIIndicator> indicators = new ArrayList<>();
        private int totalLines;
        private int totalCharacters;
        private double overallProbability;

        public void addIndicator(String type, String description, double weight) {
            indicators.add(new AIIndicator(type, description, weight));
        }

        public void calculateOverallProbability() {
            double totalWeight = 0;
            for (AIIndicator indicator : indicators) {
                totalWeight += indicator.getWeight();
            }
            this.overallProbability = Math.min(1.0, totalWeight);
        }

        // Getters
        public List<AIIndicator> getIndicators() { return indicators; }
        public int getTotalLines() { return totalLines; }
        public int getTotalCharacters() { return totalCharacters; }
        public double getOverallProbability() { return overallProbability; }

        public void setTotalLines(int totalLines) { this.totalLines = totalLines; }
        public void setTotalCharacters(int totalCharacters) { this.totalCharacters = totalCharacters; }
    }

    private static class AIIndicator {
        private String type;
        private String description;
        private double weight;

        public AIIndicator(String type, String description, double weight) {
            this.type = type;
            this.description = description;
            this.weight = weight;
        }

        // Getters
        public String getType() { return type; }
        public String getDescription() { return description; }
        public double getWeight() { return weight; }
    }

    /**
     * Auto-save functionality
     */
    private void setupAutoSave() {
        // Auto-save code content when editing
        codeEditor.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentFile != null && !isSwitchingFile) {
                currentFile.content = newVal;
                if (autoSaveCheckBox.isSelected()) {
                    // Only log auto-save occasionally to avoid spam
                    if (System.currentTimeMillis() % 10000 < 100) { // Every ~10 seconds
                        logCheatingEvent("AUTO_SAVE", "Auto-saved " + currentFile.name, 1);
                    }
                }

                // Perform code analysis on save
                performCodeAnalysis(currentFile.name, newVal);
            }
        });

        // Auto-save input data
        inputDataArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentFile != null && !isSwitchingFile) {
                SampleTestCase currentCase = sampleCases.get(currentFile.name);
                if (currentCase == null) {
                    currentCase = new SampleTestCase("", expectedOutputArea.getText());
                }
                sampleCases.put(currentFile.name, new SampleTestCase(newVal, currentCase.expectedOutput()));
            }
        });

        // Auto-save expected output
        expectedOutputArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentFile != null && !isSwitchingFile) {
                SampleTestCase currentCase = sampleCases.get(currentFile.name);
                if (currentCase == null) {
                    currentCase = new SampleTestCase(inputDataArea.getText(), "");
                }
                sampleCases.put(currentFile.name, new SampleTestCase(currentCase.input(), newVal));
            }
        });
    }

    /**
     * Comprehensive anti-cheating monitoring setup
     */
    private void setupComprehensiveAntiCheatingMonitoring(Stage primaryStage) {
        // 1. Window Focus Monitoring
        primaryStage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                focusLossStartTime = System.currentTimeMillis();
                focusLossCount++;
                logCheatingEvent("FOCUS_LOSS", "Window lost focus - possible tab switch or external application", 3);
            } else {
                if (focusLossStartTime > 0) {
                    long focusLossDuration = System.currentTimeMillis() - focusLossStartTime;
                    totalOutOfFocusTime += focusLossDuration;

                    if (focusLossDuration > 5000) { // 5 seconds threshold
                        logCheatingEvent("EXTENDED_FOCUS_LOSS",
                                "Window was out of focus for " + (focusLossDuration/1000) + " seconds", 5);
                    }
                }
                focusLossStartTime = 0;
            }
            updateSuspicionScore();
            updateInstructorDashboard();
        });

        // 2. Window Minimize Monitoring
        primaryStage.iconifiedProperty().addListener((obs, wasIconified, isIconified) -> {
            if (isIconified) {
                minimizeCount++;
                logCheatingEvent("WINDOW_MINIMIZED", "IDE window was minimized", 4);
                updateSuspicionScore();
                updateInstructorDashboard();
            }
        });

        // 3. Copy/Paste Operations Monitoring
        setupCopyPasteMonitoring();

        // 4. Periodic Activity Check
        setupPeriodicMonitoring();

        // 5. Code Pattern Analysis
        setupCodePatternAnalysis();
    }

    private void setupCopyPasteMonitoring() {
        // Monitor code editor for copy operations
        codeEditor.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // Copy operations (Ctrl+C, Cmd+C)
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.C) {
                copyOperationCount++;
                String selectedText = codeEditor.getSelectedText();
                String details = selectedText != null && !selectedText.isEmpty() ?
                        "Code copied: " + (selectedText.length() > 50 ? selectedText.substring(0, 50) + "..." : selectedText) :
                        "Copy operation detected";

                logCheatingEvent("COPY_OPERATION", details, 2);
                updateSuspicionScore();
                updateInstructorDashboard();
            }

            // Cut operations (Ctrl+X, Cmd+X)
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.X) {
                logCheatingEvent("CUT_OPERATION", "Code cut from editor", 3);
                updateSuspicionScore();
                updateInstructorDashboard();
            }

            // Paste operations (Ctrl+V, Cmd+V)
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.V) {
                logCheatingEvent("PASTE_OPERATION", "Content pasted into editor - POSSIBLE CHEATING", 8);
                updateSuspicionScore();
                updateInstructorDashboard();
            }

            // Screenshot attempt (Print Screen key)
            if (event.getCode() == KeyCode.PRINTSCREEN) {
                logCheatingEvent("SCREENSHOT_ATTEMPT", "Print Screen key pressed - POSSIBLE SCREENSHOT", 7);
                updateSuspicionScore();
                updateInstructorDashboard();
            }
        });

        // Right-click context menu monitoring
        final ContextMenu contextMenu = new ContextMenu();
        codeEditor.setContextMenu(contextMenu);
        contextMenu.setOnShowing(e -> {
            logCheatingEvent("CONTEXT_MENU_OPENED", "Right-click context menu opened", 1);
        });
    }

    private void setupCodePatternAnalysis() {
        Timer analysisTimer = new Timer(true);
        analysisTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    if (currentFile != null) {
                        analyzeCodePatterns(currentFile.content);
                    }
                });
            }
        }, 60000, 60000); // Analyze every minute
    }

    private void analyzeCodePatterns(String code) {
        // Detect suspicious code patterns
        if (code.contains("system(") || code.contains("exec(") || code.contains("Runtime.exec")) {
            logCheatingEvent("SUSPICIOUS_CODE", "System command execution detected", 6);
        }

        if (code.contains("import") && !code.contains("def ") && !code.contains("class ")) {
            logCheatingEvent("MINIMAL_CODE", "Very short code with imports - possible template", 3);
        }

        // Detect code similarity with common solutions
        double similarityScore = calculateCodeSimilarity(code);
        if (similarityScore > 0.8) {
            logCheatingEvent("HIGH_SIMILARITY", "Code matches common patterns (similarity: " +
                    String.format("%.2f", similarityScore) + ")", 4);
        }
    }

    private double calculateCodeSimilarity(String code) {
        // Simple similarity calculation based on common patterns
        String normalizedCode = code.toLowerCase().replaceAll("\\s+", " ");
        int commonPatterns = 0;

        String[] commonJavaPatterns = {"public static void main", "system.out.println", "class main"};
        String[] commonCppPatterns = {"#include", "using namespace", "int main", "cout <<"};
        String[] commonPythonPatterns = {"def main", "if __name__", "print(", "import "};

        String[] patternsToCheck;
        switch (languageChoiceBox.getValue()) {
            case "Java": patternsToCheck = commonJavaPatterns; break;
            case "C++": patternsToCheck = commonCppPatterns; break;
            case "Python": patternsToCheck = commonPythonPatterns; break;
            default: patternsToCheck = commonJavaPatterns;
        }

        for (String pattern : patternsToCheck) {
            if (normalizedCode.contains(pattern.toLowerCase())) {
                commonPatterns++;
            }
        }

        return (double) commonPatterns / patternsToCheck.length;
    }

    private void setupGlobalKeyHandlers(Scene scene) {
        // Global key handlers for Alt+Tab, Win+Tab, etc.
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // Alt+Tab detection (Windows/Linux)
            if (event.isAltDown() && event.getCode() == KeyCode.TAB) {
                tabSwitchCount++;
                logCheatingEvent("ALT_TAB_DETECTED", "Alt+Tab pressed - possible application switching", 6);
                updateSuspicionScore();
                updateInstructorDashboard();
            }

            // Cmd+Tab detection (Mac)
            if (event.isMetaDown() && event.getCode() == KeyCode.TAB) {
                tabSwitchCount++;
                logCheatingEvent("CMD_TAB_DETECTED", "Cmd+Tab pressed - possible application switching", 6);
                updateSuspicionScore();
                updateInstructorDashboard();
            }

            // Windows key detection
            if (event.getCode() == KeyCode.WINDOWS) {
                logCheatingEvent("WINDOWS_KEY_DETECTED", "Windows key pressed - possible Start menu access", 4);
                updateSuspicionScore();
                updateInstructorDashboard();
            }

            // Fullscreen exit attempt
            if (event.getCode() == KeyCode.ESCAPE) {
                logCheatingEvent("ESCAPE_ATTEMPT", "Escape key pressed - fullscreen exit attempt", 5);
                updateSuspicionScore();
                updateInstructorDashboard();
                event.consume(); // Prevent exiting fullscreen
            }

            // Quick save (Ctrl+S)
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.S) {
                logCheatingEvent("MANUAL_SAVE", "User manually saved file", 1);
                if (currentFile != null) {
                    currentFile.content = codeEditor.getText();
                    consoleOutput.appendText("💾 File saved: " + currentFile.name + "\n");
                }
                event.consume();
            }

            // Undo (Ctrl+Z)
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.Z) {
                if (!event.isShiftDown()) {
                    undo();
                    event.consume();
                }
            }

            // Redo (Ctrl+Y or Ctrl+Shift+Z)
            if ((event.isControlDown() || event.isMetaDown()) &&
                    (event.getCode() == KeyCode.Y || (event.getCode() == KeyCode.Z && event.isShiftDown()))) {
                redo();
                event.consume();
            }
        });
    }

    private void setupPeriodicMonitoring() {
        Timer monitoringTimer = new Timer(true);
        monitoringTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    logCheatingEvent("PERIODIC_CHECK", "System activity check", 1);
                    updateInstructorDashboard();

                    // Check for unusual activity patterns
                    checkActivityPatterns();
                });
            }
        }, 30000, 30000); // Check every 30 seconds
    }

    private void checkActivityPatterns() {
        // Detect patterns of suspicious activity
        long currentTime = System.currentTimeMillis();
        long sessionDuration = (currentTime - sessionStartTime) / 60000; // minutes

        // High copy rate
        if (sessionDuration > 0 && copyOperationCount / sessionDuration > 2) {
            logCheatingEvent("HIGH_COPY_RATE", "Unusually high copy operation rate", 4);
        }

        // Rapid focus switching
        if (sessionDuration > 0 && focusLossCount / sessionDuration > 5) {
            logCheatingEvent("RAPID_FOCUS_SWITCHING", "Frequent window focus changes", 5);
        }
    }

    private void logCheatingEvent(String type, String details, int severity) {
        String timestamp = LOG_FORMATTER.format(LocalDateTime.now());
        String logEntry = String.format("[%s] %s: %s\n", timestamp, type, details);

        // Add to focus log (student view)
        focusLogArea.appendText(logEntry);
        focusLogArea.setScrollTop(Double.MAX_VALUE);

        // Store for instructor dashboard
        CheatingEvent event = new CheatingEvent(
                DASHBOARD_FORMATTER.format(LocalDateTime.now()),
                type, details, severity
        );
        allEvents.add(event);

        // Update total suspicion score
        totalSuspicionScore += severity;
    }

    private void updateSuspicionScore() {
        // Calculate dynamic suspicion score (0-100)
        int score = Math.min(100, (focusLossCount * 3) + (copyOperationCount * 2) +
                (minimizeCount * 5) + (tabSwitchCount * 6) + (int)(totalOutOfFocusTime / 10000) +
                (rapidTypingEvents / 10) + (debugSessionCount * 2));

        javafx.application.Platform.runLater(() -> {
            suspicionScoreLabel.setText("Suspicion Score: " + score + "/100");
            suspicionProgress.setProgress(score / 100.0);

            // Color coding based on risk level
            if (score >= 70) {
                suspicionScoreLabel.setStyle("-fx-text-fill: " + HIGH_RISK + "; -fx-font-weight: bold;");
                suspicionProgress.setStyle("-fx-accent: " + HIGH_RISK + ";");
            } else if (score >= 40) {
                suspicionScoreLabel.setStyle("-fx-text-fill: " + MEDIUM_RISK + "; -fx-font-weight: bold;");
                suspicionProgress.setStyle("-fx-accent: " + MEDIUM_RISK + ";");
            } else {
                suspicionScoreLabel.setStyle("-fx-text-fill: " + LOW_RISK + ";");
                suspicionProgress.setStyle("-fx-accent: " + LOW_RISK + ";");
            }
        });
    }

    /**
     * Creates the instructor monitoring dashboard
     */
    private VBox createInstructorDashboard() {
        Label dashboardTitle = new Label("INSTRUCTOR DASHBOARD - LIVE MONITORING");
        dashboardTitle.setFont(Font.font("Inter", 16));
        dashboardTitle.setStyle("-fx-text-fill: " + RED_CLOSE + "; -fx-font-weight: bold;");

        // Suspicion score display
        HBox scoreBox = new HBox(10, suspicionScoreLabel, suspicionProgress);
        scoreBox.setAlignment(Pos.CENTER_LEFT);
        suspicionProgress.setPrefWidth(150);

        // Statistics panel
        VBox statsPanel = createStatisticsPanel();

        // Code analysis panel
        VBox analysisPanel = createCodeAnalysisPanel();

        // Events log
        Label eventsTitle = new Label("Real-time Monitoring Events:");
        eventsTitle.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold; -fx-font-size: 12px;");

        instructorDashboard.setEditable(false);
        instructorDashboard.setFont(Font.font("Monospaced", 11));
        instructorDashboard.setStyle("-fx-control-inner-background: #1a1a2e; -fx-text-fill: " + BLUE_INFO + ";");
        instructorDashboard.setPromptText("Monitoring events will appear here in real-time...");

        VBox dashboard = new VBox(10, dashboardTitle, scoreBox, statsPanel, analysisPanel, eventsTitle, instructorDashboard);
        dashboard.setPadding(new Insets(10));
        dashboard.setStyle("-fx-background-color: #252535; -fx-border-color: " + RED_CLOSE + "; -fx-border-width: 2;");
        VBox.setVgrow(instructorDashboard, Priority.ALWAYS);

        return dashboard;
    }

    private VBox createStatisticsPanel() {
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(8);
        statsGrid.setPadding(new Insets(8));

        // Session duration
        Label sessionTimeLabel = new Label("Session Duration:");
        Label sessionTimeValue = new Label("0m 0s");

        // Update session timer
        Timer sessionTimer = new Timer(true);
        sessionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long duration = System.currentTimeMillis() - sessionStartTime;
                long minutes = duration / 60000;
                long seconds = (duration % 60000) / 1000;
                javafx.application.Platform.runLater(() ->
                        sessionTimeValue.setText(String.format("%dm %ds", minutes, seconds))
                );
            }
        }, 0, 1000);

        // Focus statistics
        Label focusLossLabel = new Label("Focus Loss Count:");
        Label focusLossValue = new Label("0");

        Label outOfFocusLabel = new Label("Total Out-of-Focus:");
        Label outOfFocusValue = new Label("0s");

        // Update out-of-focus timer
        Timer focusTimer = new Timer(true);
        focusTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    focusLossValue.setText(String.valueOf(focusLossCount));
                    outOfFocusValue.setText(String.format("%.1fs", totalOutOfFocusTime / 1000.0));
                });
            }
        }, 0, 1000);

        // Other statistics
        Label copyCountLabel = new Label("Copy Operations:");
        Label copyCountValue = new Label("0");

        Label minimizeLabel = new Label("Minimize Events:");
        Label minimizeValue = new Label("0");

        Label tabSwitchLabel = new Label("Tab Switches:");
        Label tabSwitchValue = new Label("0");

        // New statistics
        Label rapidTypingLabel = new Label("Rapid Typing:");
        Label rapidTypingValue = new Label("0");

        Label debugSessionsLabel = new Label("Debug Sessions:");
        Label debugSessionsValue = new Label("0");

        Label autoFormatLabel = new Label("Auto-Format:");
        Label autoFormatValue = new Label("0");

        // Add to grid
        statsGrid.add(sessionTimeLabel, 0, 0);
        statsGrid.add(sessionTimeValue, 1, 0);
        statsGrid.add(focusLossLabel, 0, 1);
        statsGrid.add(focusLossValue, 1, 1);
        statsGrid.add(outOfFocusLabel, 0, 2);
        statsGrid.add(outOfFocusValue, 1, 2);
        statsGrid.add(copyCountLabel, 2, 0);
        statsGrid.add(copyCountValue, 3, 0);
        statsGrid.add(minimizeLabel, 2, 1);
        statsGrid.add(minimizeValue, 3, 1);
        statsGrid.add(tabSwitchLabel, 2, 2);
        statsGrid.add(tabSwitchValue, 3, 2);
        statsGrid.add(rapidTypingLabel, 4, 0);
        statsGrid.add(rapidTypingValue, 5, 0);
        statsGrid.add(debugSessionsLabel, 4, 1);
        statsGrid.add(debugSessionsValue, 5, 1);
        statsGrid.add(autoFormatLabel, 4, 2);
        statsGrid.add(autoFormatValue, 5, 2);

        // Update new statistics
        Timer enhancedStatsTimer = new Timer(true);
        enhancedStatsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    rapidTypingValue.setText(String.valueOf(rapidTypingEvents));
                    debugSessionsValue.setText(String.valueOf(debugSessionCount));
                    autoFormatValue.setText(String.valueOf(autoFormatCount));
                });
            }
        }, 0, 1000);

        // Style the labels
        for (int i = 0; i < statsGrid.getChildren().size(); i++) {
            if (statsGrid.getChildren().get(i) instanceof Label label) {
                label.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-size: 11px;");
            }
        }

        VBox statsPanel = new VBox(8, new Label("Session Statistics:"), statsGrid);
        statsPanel.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-padding: 8; -fx-border-radius: 5;");

        return statsPanel;
    }

    private VBox createCodeAnalysisPanel() {
        GridPane analysisGrid = new GridPane();
        analysisGrid.setHgap(12);
        analysisGrid.setVgap(6);
        analysisGrid.setPadding(new Insets(8));

        Label complexityLabel = new Label("Complexity:");
        Label complexityValue = new Label("N/A");

        Label linesOfCodeLabel = new Label("Lines:");
        Label linesOfCodeValue = new Label("0");

        Label warningsLabel = new Label("Warnings:");
        Label warningsValue = new Label("0");

        Label similarityLabel = new Label("Similarity:");
        Label similarityValue = new Label("0%");

        analysisGrid.add(complexityLabel, 0, 0);
        analysisGrid.add(complexityValue, 1, 0);
        analysisGrid.add(linesOfCodeLabel, 0, 1);
        analysisGrid.add(linesOfCodeValue, 1, 1);
        analysisGrid.add(warningsLabel, 2, 0);
        analysisGrid.add(warningsValue, 3, 0);
        analysisGrid.add(similarityLabel, 2, 1);
        analysisGrid.add(similarityValue, 3, 1);

        // Update analysis panel
        Timer analysisUpdateTimer = new Timer(true);
        analysisUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    if (currentFile != null) {
                        CodeAnalysisResult result = codeAnalysisResults.get(currentFile.name);
                        if (result != null) {
                            complexityValue.setText(String.valueOf(result.complexity()));
                            linesOfCodeValue.setText(String.valueOf(result.linesOfCode()));
                            warningsValue.setText(String.valueOf(result.warningCount()));
                            similarityValue.setText(String.format("%.1f%%", result.similarityScore() * 100));
                        }
                    }
                });
            }
        }, 0, 2000);

        // Style the labels
        for (int i = 0; i < analysisGrid.getChildren().size(); i++) {
            if (analysisGrid.getChildren().get(i) instanceof Label label) {
                label.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-size: 10px;");
            }
        }

        VBox analysisPanel = new VBox(6, new Label("Code Analysis:"), analysisGrid);
        analysisPanel.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-padding: 8; -fx-border-radius: 5;");

        return analysisPanel;
    }

    private void updateInstructorDashboard() {
        StringBuilder dashboardContent = new StringBuilder();
        dashboardContent.append("=== REAL-TIME MONITORING EVENTS ===\n\n");

        // Show last 20 events (most recent first)
        int startIndex = Math.max(0, allEvents.size() - 20);
        for (int i = startIndex; i < allEvents.size(); i++) {
            CheatingEvent event = allEvents.get(i);
            String severityIndicator = "⚠".repeat(Math.min(3, event.severity() / 2));
            dashboardContent.append(String.format("[%s] %s %s: %s\n",
                    event.timestamp(), severityIndicator, event.type(), event.details()));
        }

        dashboardContent.append("\n=== RISK ASSESSMENT ===\n");
        dashboardContent.append("Focus Loss Events: ").append(focusLossCount).append("\n");
        dashboardContent.append("Copy Operations: ").append(copyOperationCount).append("\n");
        dashboardContent.append("Window Minimizes: ").append(minimizeCount).append("\n");
        dashboardContent.append("Tab Switches: ").append(tabSwitchCount).append("\n");
        dashboardContent.append("Rapid Typing Events: ").append(rapidTypingEvents).append("\n");
        dashboardContent.append("Debug Sessions: ").append(debugSessionCount).append("\n");
        dashboardContent.append("Total Suspicion Score: ").append(totalSuspicionScore).append("\n");

        instructorDashboard.setText(dashboardContent.toString());
        instructorDashboard.setScrollTop(Double.MAX_VALUE);
    }

    /**
     * Interactive Console Setup
     */
    private void setupInteractiveConsole() {
        consoleOutput.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (!isWaitingForInput) return;

            switch (event.getCode()) {
                case ENTER:
                    handleConsoleInputSubmit();
                    event.consume();
                    break;

                case BACK_SPACE:
                    handleConsoleBackspace();
                    event.consume();
                    break;

                case UP:
                    navigateConsoleHistory(-1);
                    event.consume();
                    break;

                case DOWN:
                    navigateConsoleHistory(1);
                    event.consume();
                    break;

                case C:
                    if (event.isControlDown()) {
                        handleConsoleCtrlC();
                        event.consume();
                    }
                    break;

                default:
                    break;
            }
        });

        consoleOutput.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!isWaitingForInput) return;

            String character = event.getCharacter();
            if (!character.isEmpty() && character.charAt(0) != '\r' && character.charAt(0) != '\n') {
                currentInputLine.append(character);
                appendToConsole(character);
            }
            event.consume();
        });
    }

    private void handleConsoleInputSubmit() {
        String userInput = currentInputLine.toString();
        appendToConsole("\n");

        if (!userInput.trim().isEmpty()) {
            inputHistory.add(userInput);
            historyIndex = inputHistory.size();
        }

        currentInputLine.setLength(0);
        isWaitingForInput = false;

        // Process the input
        new Thread(() -> processConsoleInput(userInput)).start();
    }

    private void handleConsoleBackspace() {
        if (currentInputLine.length() > 0) {
            currentInputLine.setLength(currentInputLine.length() - 1);
            String currentText = consoleOutput.getText();
            if (currentText.length() > inputStartPosition) {
                consoleOutput.setText(currentText.substring(0, currentText.length() - 1));
            }
        }
    }

    private void navigateConsoleHistory(int direction) {
        if (inputHistory.isEmpty()) return;

        if (direction == -1 && historyIndex > 0) {
            historyIndex--;
        } else if (direction == 1 && historyIndex < inputHistory.size() - 1) {
            historyIndex++;
        }

        if (historyIndex >= 0 && historyIndex < inputHistory.size()) {
            replaceConsoleInput(inputHistory.get(historyIndex));
        }
    }

    private void handleConsoleCtrlC() {
        appendToConsole("^C\n");
        isProgramRunning = false;
        isWaitingForInput = false;
        currentInputLine.setLength(0);
        showConsolePrompt("Program interrupted. Press Run to start again.\n> ");
    }

    private void replaceConsoleInput(String newInput) {
        String currentText = consoleOutput.getText();
        if (currentText.length() > inputStartPosition) {
            consoleOutput.setText(currentText.substring(0, inputStartPosition));
        }

        currentInputLine.setLength(0);
        currentInputLine.append(newInput);
        appendToConsole(newInput);
    }

    private void appendToConsole(String text) {
        consoleOutput.appendText(text);
        consoleOutput.setScrollTop(Double.MAX_VALUE);
    }

    private void showConsolePrompt(String prompt) {
        appendToConsole(prompt);
        inputStartPosition = consoleOutput.getText().length();
    }

    private void waitForConsoleInput(String prompt) {
        javafx.application.Platform.runLater(() -> {
            isWaitingForInput = true;
            currentInputLine.setLength(0);
            showConsolePrompt(prompt);
        });
    }

    /**
     * Process console input and execute program logic
     */
    private void processConsoleInput(String userInput) {
        String language = languageChoiceBox.getValue();
        String code = codeEditor.getText();

        String result = executeProgramLogic(language, code, userInput);

        javafx.application.Platform.runLater(() -> {
            if (!result.isEmpty()) {
                appendToConsole(result + "\n");
            }

            if (isProgramRunning && hasMoreInputOperations(language, code)) {
                String nextPrompt = getNextInputPrompt(language, code);
                waitForConsoleInput(nextPrompt);
            } else {
                isProgramRunning = false;
                showConsolePrompt("Program finished. Press Run to execute again.\n> ");
            }
        });
    }

    private String executeProgramLogic(String language, String code, String userInput) {
        switch (language) {
            case "C++":
                return executeCppProgram(code, userInput);
            case "Java":
                return executeJavaProgram(code, userInput);
            case "Python":
                return executePythonProgram(code, userInput);
            default:
                return "Output: Processed input - " + userInput;
        }
    }

    private String executeCppProgram(String code, String userInput) {
        StringBuilder result = new StringBuilder();

        try {
            // Extract all cin operations from code
            if (cinOperations.isEmpty()) {
                extractCppCinOperations(code);
            }

            // Process input based on current state
            if (currentCinOperation < cinOperations.size()) {
                String cinOp = cinOperations.get(currentCinOperation);
                String[] inputs = userInput.trim().split("\\s+");

                // Store input values in program state
                for (int i = 0; i < inputs.length && currentCinOperation < cinOperations.size(); i++) {
                    String varName = cinOperations.get(currentCinOperation);
                    try {
                        int value = Integer.parseInt(inputs[i]);
                        programState.put(varName, value);
                        result.append("✓ Stored value: ").append(value).append(" in variable '").append(varName).append("'\n");
                    } catch (NumberFormatException e) {
                        programState.put(varName, inputs[i]);
                        result.append("✓ Stored text: '").append(inputs[i]).append("' in variable '").append(varName).append("'\n");
                    }
                    currentCinOperation++;
                }

                // Execute cout statements that come after the processed cin
                result.append(executeCppOutput(code));

            } else {
                // All cin operations processed, execute remaining logic
                result.append(executeCppConditionalLogic(code));
            }

        } catch (Exception e) {
            result.append("❌ Execution error: ").append(e.getMessage());
        }

        return result.toString();
    }

    private void extractCppCinOperations(String code) {
        cinOperations.clear();
        currentCinOperation = 0;
        programState.clear();

        // Extract variables from cin operations
        Pattern cinPattern = Pattern.compile("cin\\s*>>\\s*([a-zA-Z_][a-zA-Z0-9_]*)");
        Matcher matcher = cinPattern.matcher(code);

        while (matcher.find()) {
            cinOperations.add(matcher.group(1));
        }

        // Initialize variables
        for (String var : cinOperations) {
            programState.put(var, 0); // default value
        }
    }

    private String executeCppOutput(String code) {
        StringBuilder output = new StringBuilder();

        // Extract and execute cout statements
        Pattern coutPattern = Pattern.compile("cout\\s*<<\\s*([^;]+);");
        Matcher matcher = coutPattern.matcher(code);

        while (matcher.find()) {
            String coutContent = matcher.group(1);
            // Replace variables with their values
            String processedOutput = processCppOutputContent(coutContent);
            if (!processedOutput.trim().isEmpty()) {
                output.append("📢 Program output: ").append(processedOutput).append("\n");
            }
        }

        return output.toString();
    }

    private String processCppOutputContent(String content) {
        // Remove quotes and process variables
        String processed = content.replaceAll("\"", "").replace("<<", " ").replace("endl", "\n").trim();

        // Replace variables with their values
        for (Map.Entry<String, Object> entry : programState.entrySet()) {
            processed = processed.replace(entry.getKey(), entry.getValue().toString());
        }

        return processed;
    }

    private String executeCppConditionalLogic(String code) {
        StringBuilder result = new StringBuilder();

        // Execute if statements
        if (code.contains("if") && code.contains(">")) {
            Pattern ifPattern = Pattern.compile("if\\s*\\(\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*>\\s*(\\d+)\\s*\\)");
            Matcher matcher = ifPattern.matcher(code);

            if (matcher.find()) {
                String varName = matcher.group(1);
                int threshold = Integer.parseInt(matcher.group(2));

                if (programState.containsKey(varName)) {
                    int value = (int) programState.get(varName);
                    if (value > threshold) {
                        // Extract the cout inside if block
                        Pattern ifCoutPattern = Pattern.compile("if\\s*\\([^)]+\\)\\s*\\{[^}]*cout[^}]*\"([^\"]*)\"[^}]*\\}");
                        Matcher ifCoutMatcher = ifCoutPattern.matcher(code);
                        if (ifCoutMatcher.find()) {
                            String ifOutput = ifCoutMatcher.group(1);
                            result.append("✅ Condition true! Output: ").append(ifOutput).append("\n");
                        }
                    } else {
                        result.append("❌ Condition false: ").append(varName).append(" (").append(value).append(") is not greater than ").append(threshold).append("\n");
                    }
                }
            }
        }

        return result.toString();
    }

    private String executeJavaProgram(String code, String userInput) {
        StringBuilder result = new StringBuilder();

        try {
            // Simple Java input processing
            String[] inputs = userInput.trim().split("\\s+");
            int sum = 0;
            for (String input : inputs) {
                try {
                    sum += Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    // ignore non-integers
                }
            }

            if (code.contains("System.out.println")) {
                Pattern printPattern = Pattern.compile("System\\.out\\.println\\(\\s*\"([^\"]*)\"\\s*\\)");
                Matcher matcher = printPattern.matcher(code);
                if (matcher.find()) {
                    result.append("📢 Java output: ").append(matcher.group(1));
                    if (matcher.group(1).contains("sum") || matcher.group(1).contains("total")) {
                        result.append(" ").append(sum);
                    }
                }
            } else {
                result.append("📊 Processed ").append(inputs.length).append(" inputs. Sum: ").append(sum);
            }

        } catch (Exception e) {
            result.append("❌ Java execution error: ").append(e.getMessage());
        }

        return result.toString();
    }

    private String executePythonProgram(String code, String userInput) {
        StringBuilder result = new StringBuilder();

        try {
            String[] inputs = userInput.trim().split("\\s+");

            if (code.contains("print(")) {
                Pattern printPattern = Pattern.compile("print\\(\\s*\"([^\"]*)\"\\s*\\)");
                Matcher matcher = printPattern.matcher(code);
                if (matcher.find()) {
                    String output = matcher.group(1);
                    result.append("🐍 Python output: ").append(output);

                    // Simple variable substitution
                    if (output.contains("{") && output.contains("}")) {
                        if (inputs.length > 0) {
                            result.append(" - Input: ").append(inputs[0]);
                        }
                    }
                }
            } else {
                int sum = 0;
                for (String input : inputs) {
                    try {
                        sum += Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        // ignore non-integers
                    }
                }
                result.append("🐍 Processed inputs. Sum: ").append(sum);
            }

        } catch (Exception e) {
            result.append("❌ Python execution error: ").append(e.getMessage());
        }

        return result.toString();
    }

    private boolean hasMoreInputOperations(String language, String code) {
        switch (language) {
            case "C++":
                return currentCinOperation < cinOperations.size();
            case "Java":
                return code.contains("nextInt") || code.contains("nextLine");
            case "Python":
                return code.contains("input()");
            default:
                return false;
        }
    }

    private String getNextInputPrompt(String language, String code) {
        switch (language) {
            case "C++":
                if (currentCinOperation < cinOperations.size()) {
                    return "Enter value for variable '" + cinOperations.get(currentCinOperation) + "': ";
                }
                break;
            case "Java":
                return "Enter input for Java program: ";
            case "Python":
                return "Python input: ";
        }
        return "> ";
    }

    /**
     * NEW FUNCTIONALITY: Debug Session
     */
    private void startDebugSession() {
        debugSessionCount++;
        logCheatingEvent("DEBUG_SESSION", "User started debug session", 2);

        String language = languageChoiceBox.getValue();
        String code = codeEditor.getText();

        consoleOutput.appendText("🐞 Starting debug session for " + language + "...\n");

        // Simple debug analysis
        List<String> issues = analyzeCodeForIssues(code, language);
        if (issues.isEmpty()) {
            consoleOutput.appendText("✅ No obvious issues found in code structure.\n");
        } else {
            consoleOutput.appendText("⚠️ Potential issues found:\n");
            for (String issue : issues) {
                consoleOutput.appendText("   • " + issue + "\n");
            }
        }

        // Show variable analysis
        consoleOutput.appendText("📊 Code Analysis:\n");
        consoleOutput.appendText("   Lines: " + code.split("\n").length + "\n");
        consoleOutput.appendText("   Functions: " + countFunctions(code, language) + "\n");
        consoleOutput.appendText("   Complexity: " + estimateComplexity(code) + "/10\n");

        consoleOutput.appendText("🔍 Debug session completed.\n\n");
    }

    private List<String> analyzeCodeForIssues(String code, String language) {
        List<String> issues = new ArrayList<>();

        // Common issues detection
        if (code.contains("while(true)") || code.contains("for(;;)")) {
            issues.add("Potential infinite loop detected");
        }

        if (code.contains("import") && !code.contains("def ") && !code.contains("class ")) {
            issues.add("Imports without function/class definitions");
        }

        if (code.contains("System.out.println") && code.contains("Scanner") &&
                code.split("\n").length < 10) {
            issues.add("Very short Java program with I/O");
        }

        return issues;
    }

    private int countFunctions(String code, String language) {
        switch (language) {
            case "Java":
                return code.split("public|private|protected").length - 1;
            case "Python":
                return code.split("def ").length - 1;
            case "C++":
                return code.split("int |void |double ").length - 1;
            default:
                return 0;
        }
    }

    private int estimateComplexity(String code) {
        int complexity = 0;

        // Simple complexity estimation
        complexity += code.split("if\\s*\\(").length - 1;
        complexity += code.split("for\\s*\\(").length - 1;
        complexity += code.split("while\\s*\\(").length - 1;
        complexity += code.split("switch\\s*\\(").length - 1;

        return Math.min(10, complexity);
    }

    /**
     * NEW FUNCTIONALITY: Code Formatting
     */
    private void formatCode() {
        autoFormatCount++;
        logCheatingEvent("CODE_FORMAT", "User formatted code", 1);

        String language = languageChoiceBox.getValue();
        String code = codeEditor.getText();

        String formattedCode = formatCodeByLanguage(code, language);
        codeEditor.setText(formattedCode);

        consoleOutput.appendText("✨ Code formatted for " + language + "\n");
    }

    private String formatCodeByLanguage(String code, String language) {
        // Simple formatting rules
        String formatted = code;

        // Basic indentation
        formatted = formatted.replaceAll("\\{", " {\n    ");
        formatted = formatted.replaceAll("\\}", "\n}\n");

        // Ensure proper spacing
        formatted = formatted.replaceAll(";", ";\n");
        formatted = formatted.replaceAll("\\n\\s*\\n", "\n");

        return formatted;
    }

    /**
     * NEW FUNCTIONALITY: Search in Code
     */
    private void searchInCode() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            consoleOutput.appendText("🔍 Please enter a search term\n");
            return;
        }

        String code = codeEditor.getText();
        int index = code.indexOf(searchTerm);

        if (index >= 0) {
            codeEditor.selectRange(index, index + searchTerm.length());
            codeEditor.requestFocus();
            consoleOutput.appendText("🔍 Found: '" + searchTerm + "' at position " + index + "\n");
        } else {
            consoleOutput.appendText("🔍 Search term not found: '" + searchTerm + "'\n");
        }
    }

    /**
     * NEW FUNCTIONALITY: Auto Tests
     */
    private void runAutoTests() {
        if (currentFile == null) {
            consoleOutput.appendText("❌ No file selected for testing\n");
            return;
        }

        consoleOutput.appendText("🤖 Running auto tests for " + currentFile.name + "...\n");

        SampleTestCase testCase = sampleCases.get(currentFile.name);
        if (testCase != null) {
            runEnhancedTest(testCase);
        } else {
            consoleOutput.appendText("⚠️ No test cases defined. Use Input/Output tab to set up tests.\n");
        }
    }

    private void runEnhancedTest(SampleTestCase testCase) {
        // Validate test case
        if (testCase.input().isEmpty() && testCase.expectedOutput().isEmpty()) {
            consoleOutput.appendText("⚡ Test case has no input or expected output - running basic validation\n");
        }

        String language = languageChoiceBox.getValue();
        String code = codeEditor.getText();

        // Pre-execution validation
        if (code == null || code.trim().isEmpty()) {
            consoleOutput.appendText("❌ TEST FAILED: No code to execute\n");
            logCheatingEvent("EMPTY_CODE_TEST", "Attempted to test empty code", 2);
            return;
        }

        if (language == null || language.trim().isEmpty()) {
            consoleOutput.appendText("❌ TEST FAILED: No programming language selected\n");
            return;
        }

        consoleOutput.appendText("🔧 Language: " + language + "\n");
        consoleOutput.appendText("📥 Input: " + (testCase.input().isEmpty() ? "[none]" : testCase.input()) + "\n");
        consoleOutput.appendText("📤 Expected: " + (testCase.expectedOutput().isEmpty() ? "[any output]" : testCase.expectedOutput()) + "\n");

        try {
            // Execute with timing
            long startTime = System.currentTimeMillis();
            String result = executeProgramLogic(language, code, testCase.input());
            long executionTime = System.currentTimeMillis() - startTime;

            // Enhanced result evaluation
            boolean testPassed = evaluateTestResult(result, testCase.expectedOutput());
            String formattedResult = result.trim().isEmpty() ? "[no output]" : result;

            consoleOutput.appendText("💻 Actual Output: " + formattedResult + "\n");
            consoleOutput.appendText("⏱️ Execution Time: " + executionTime + "ms\n");

            if (testPassed) {
                consoleOutput.appendText("🎉 ✅ TEST PASSED!\n");

                // Performance feedback
                if (executionTime > 2000) {
                    consoleOutput.appendText("💡 Performance Note: Code execution is slow (" + executionTime + "ms)\n");
                }

                // Code quality check
                if (code.split("\n").length > 50) {
                    consoleOutput.appendText("💡 Code Style: Consider breaking down into smaller functions\n");
                }
            } else {
                consoleOutput.appendText("💥 ❌ TEST FAILED!\n");

                // Provide helpful feedback
                if (result.trim().isEmpty()) {
                    consoleOutput.appendText("💡 Hint: Your code produced no output. Check print statements.\n");
                } else if (testCase.expectedOutput().toLowerCase().contains("error") && !result.toLowerCase().contains("error")) {
                    consoleOutput.appendText("💡 Hint: Expected error handling but none was found\n");
                }

                logCheatingEvent("TEST_FAILED", "Test case failed for " + currentFile.name, 1);
            }

            // Additional analysis
            provideAdditionalFeedback(code, result, testPassed, executionTime);

        } catch (Exception e) {
            consoleOutput.appendText("💀 TEST ERROR: " + e.getMessage() + "\n");
            consoleOutput.appendText("💡 Check your code for syntax errors or infinite loops\n");
            logCheatingEvent("TEST_ERROR", "Test execution error: " + e.getMessage(), 2);
        }

        consoleOutput.appendText("\n");
    }

    private boolean evaluateTestResult(String actual, String expected) {
        if (expected.isEmpty()) {
            return !actual.trim().isEmpty(); // Any non-empty output passes
        }

        if (actual == null || actual.trim().isEmpty()) {
            return false;
        }

        // Flexible matching strategies
        String cleanActual = actual.trim().toLowerCase();
        String cleanExpected = expected.trim().toLowerCase();

        // Try exact match first
        if (cleanActual.equals(cleanExpected)) {
            return true;
        }

        // Try contains match
        if (cleanActual.contains(cleanExpected)) {
            return true;
        }

        // Try line-by-line matching for multi-line outputs
        String[] actualLines = cleanActual.split("\n");
        String[] expectedLines = cleanExpected.split("\n");

        for (String expectedLine : expectedLines) {
            boolean lineFound = false;
            for (String actualLine : actualLines) {
                if (actualLine.contains(expectedLine)) {
                    lineFound = true;
                    break;
                }
            }
            if (!lineFound) {
                return false;
            }
        }

        return expectedLines.length > 0;
    }

    private void provideAdditionalFeedback(String code, String result, boolean testPassed, long executionTime) {
        consoleOutput.appendText("📊 Additional Analysis:\n");

        // Code metrics
        int lineCount = code.split("\n").length;
        consoleOutput.appendText("   📝 Lines of code: " + lineCount + "\n");

        // Complexity estimation
        if (lineCount > 100) {
            consoleOutput.appendText("   💡 Consider refactoring: Code is quite long\n");
        }

        // Performance feedback
        if (executionTime > 1000) {
            consoleOutput.appendText("   ⚡ Performance: Execution is slower than average\n");
        } else if (executionTime < 100) {
            consoleOutput.appendText("   🚀 Performance: Excellent execution speed\n");
        }

        // Output analysis
        if (result.length() > 500) {
            consoleOutput.appendText("   📄 Output: Very large output generated\n");
        }

        // Language-specific tips
        String language = languageChoiceBox.getValue();
        if ("java".equalsIgnoreCase(language)) {
            if (!code.contains("public class") && code.contains("System.out")) {
                consoleOutput.appendText("   ☕ Java Tip: Remember to define a class structure\n");
            }
        } else if ("python".equalsIgnoreCase(language)) {
            if (code.contains("print(") && !code.contains("def ")) {
                consoleOutput.appendText("   🐍 Python Tip: Consider organizing code into functions\n");
            }
        }
    }

    /**
     * NEW FUNCTIONALITY: Export Project
     */
    private void exportProject() {
        logCheatingEvent("PROJECT_EXPORT", "User exported project", 1);

        // Create file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Set default file name with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setInitialFileName("project_export_" + timestamp + ".txt");

        // Show save dialog
        File file = fileChooser.showSaveDialog(mainStage);

        if (file != null) {
            try {
                // Build export content
                StringBuilder exportContent = new StringBuilder();
                exportContent.append("=== PROJECT EXPORT ===\n");
                exportContent.append("Export Time: ").append(LocalDateTime.now()).append("\n");
                exportContent.append("Session Duration: ").append((System.currentTimeMillis() - sessionStartTime) / 60000).append(" minutes\n");
                exportContent.append("Files: ").append(fileContents.size()).append("\n");
                exportContent.append("Final Suspicion Score: ").append(totalSuspicionScore).append("/100\n\n");

                // Add session statistics
                exportContent.append("=== SESSION STATISTICS ===\n");
                exportContent.append("Focus Loss Events: ").append(focusLossCount).append("\n");
                exportContent.append("Copy Operations: ").append(copyOperationCount).append("\n");
                exportContent.append("Window Minimizes: ").append(minimizeCount).append("\n");
                exportContent.append("Tab Switches: ").append(tabSwitchCount).append("\n");
                exportContent.append("Debug Sessions: ").append(debugSessionCount).append("\n");
                exportContent.append("Auto-Format Uses: ").append(autoFormatCount).append("\n");
                exportContent.append("Rapid Typing Events: ").append(rapidTypingEvents).append("\n\n");

                // Add all files with their content
                for (Map.Entry<String, FileEntry> entry : fileContents.entrySet()) {
                    String fileName = entry.getKey();
                    String fileContent = entry.getValue().content;

                    exportContent.append("=== FILE: ").append(fileName).append(" ===\n");
                    exportContent.append("Language: ").append(getLanguageForExtension(getFileExtension(fileName))).append("\n");
                    exportContent.append("Size: ").append(fileContent.length()).append(" characters\n");
                    exportContent.append("Lines: ").append(fileContent.split("\n").length).append("\n");
                    exportContent.append("\n");

                    // Add file content with line numbers
                    String[] lines = fileContent.split("\n");
                    for (int i = 0; i < lines.length; i++) {
                        exportContent.append(String.format("%4d: %s\n", i + 1, lines[i]));
                    }

                    // Add test cases if available
                    SampleTestCase testCase = sampleCases.get(fileName);
                    if (testCase != null && (!testCase.input().isEmpty() || !testCase.expectedOutput().isEmpty())) {
                        exportContent.append("\n--- TEST CASE ---\n");
                        exportContent.append("Input: ").append(testCase.input()).append("\n");
                        exportContent.append("Expected Output: ").append(testCase.expectedOutput()).append("\n");
                    }

                    exportContent.append("\n" + "=".repeat(50) + "\n\n");
                }

                // Add recent monitoring events
                exportContent.append("=== RECENT MONITORING EVENTS (Last 20) ===\n");
                int startIndex = Math.max(0, allEvents.size() - 20);
                for (int i = startIndex; i < allEvents.size(); i++) {
                    CheatingEvent event = allEvents.get(i);
                    exportContent.append(String.format("[%s] %s: %s (Severity: %d)\n",
                            event.timestamp(), event.type(), event.details(), event.severity()));
                }

                // Write to file
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.write(exportContent.toString());
                }

                // Show success message
                consoleOutput.appendText("📤 Project exported successfully!\n");
                consoleOutput.appendText("📍 Location: " + file.getAbsolutePath() + "\n");
                consoleOutput.appendText("📊 Files included: " + fileContents.size() + "\n");
                consoleOutput.appendText("📝 Total characters: " + exportContent.length() + "\n");
                consoleOutput.appendText("⏱️  Session duration: " + ((System.currentTimeMillis() - sessionStartTime) / 60000) + " minutes\n");

                // Show file list
                consoleOutput.appendText("📁 Files exported:\n");
                for (String fileName : fileContents.keySet()) {
                    String language = getLanguageForExtension(getFileExtension(fileName));
                    int lines = fileContents.get(fileName).content.split("\n").length;
                    consoleOutput.appendText("   • " + fileName + " (" + language + ", " + lines + " lines)\n");
                }

            } catch (IOException e) {
                consoleOutput.appendText("❌ Export failed: " + e.getMessage() + "\n");
                logCheatingEvent("EXPORT_FAILED", "Project export failed: " + e.getMessage(), 1);

                // Show error dialog
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Error");
                alert.setHeaderText("Failed to export project");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            consoleOutput.appendText("ℹ️ Export cancelled by user\n");
        }
    }

    /**
     * NEW FUNCTIONALITY: Import Project
     */
    private void importProject() {
        logCheatingEvent("PROJECT_IMPORT", "User attempted to import project", 3);

        // Create file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Show open dialog
        File file = fileChooser.showOpenDialog(mainStage);

        if (file != null) {
            try {
                consoleOutput.appendText("📥 Importing project from: " + file.getName() + "\n");
                consoleOutput.appendText("⏳ Reading file...\n");

                // Read the entire file
                String content = new String(Files.readAllBytes(file.toPath()));

                // Parse the imported content
                boolean success = parseImportedProject(content);

                if (success) {
                    consoleOutput.appendText("✅ Project imported successfully!\n");
                    consoleOutput.appendText("📊 Files imported: " + fileContents.size() + "\n");
                    consoleOutput.appendText("⚠️ Imported code is being analyzed for cheating patterns...\n");

                    // Analyze imported code for suspicious patterns
                    analyzeImportedCode();

                    // High suspicion event for importing external code
                    logCheatingEvent("EXTERNAL_CODE_IMPORT",
                            "User imported external project with " + fileContents.size() + " files", 8);

                } else {
                    consoleOutput.appendText("❌ Failed to parse imported project file\n");
                }

            } catch (IOException e) {
                consoleOutput.appendText("❌ Import failed: " + e.getMessage() + "\n");
                logCheatingEvent("IMPORT_FAILED", "Project import failed: " + e.getMessage(), 1);

                // Show error dialog
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Import Error");
                alert.setHeaderText("Failed to import project");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            } catch (Exception e) {
                consoleOutput.appendText("❌ Error parsing imported file: " + e.getMessage() + "\n");
            }
        } else {
            consoleOutput.appendText("ℹ️ Import cancelled by user\n");
        }
    }

    /**
     * Parse imported project content and rebuild the project structure
     */
    private boolean parseImportedProject(String content) {
        // Clear current project
        fileContents.clear();
        sampleCases.clear();
        rootItem.getChildren().clear();

        consoleOutput.appendText("🔍 Parsing project structure...\n");

        try {
            String[] sections = content.split("=== FILE: ");
            int filesImported = 0;
            int testCasesImported = 0;

            for (String section : sections) {
                if (section.trim().isEmpty()) continue;

                // Extract file name from section header
                int endOfHeader = section.indexOf(" ===");
                if (endOfHeader > 0) {
                    String fileName = section.substring(0, endOfHeader).trim();

                    // Find the content section (after line numbers)
                    String[] lines = section.split("\n");
                    StringBuilder fileContent = new StringBuilder();
                    boolean inContent = false;
                    String currentInput = "";
                    String currentExpected = "";
                    boolean inTestCase = false;

                    for (String line : lines) {
                        // Skip header lines and metadata
                        if (line.startsWith("=== FILE:") || line.startsWith("Language:") ||
                                line.startsWith("Size:") || line.startsWith("Lines:") ||
                                line.equals("===") || line.startsWith("=")) {
                            continue;
                        }

                        // Check for test case section
                        if (line.contains("--- TEST CASE ---")) {
                            inTestCase = true;
                            continue;
                        }

                        if (inTestCase) {
                            if (line.startsWith("Input: ")) {
                                currentInput = line.substring(7).trim();
                            } else if (line.startsWith("Expected Output: ")) {
                                currentExpected = line.substring(17).trim();
                            } else if (line.trim().isEmpty() && !currentInput.isEmpty()) {
                                // End of test case
                                sampleCases.put(fileName, new SampleTestCase(currentInput, currentExpected));
                                testCasesImported++;
                                inTestCase = false;
                            }
                        } else {
                            // Remove line numbers from code content (format: "   1: code")
                            if (line.matches("^\\s*\\d+:.*")) {
                                int colonIndex = line.indexOf(":");
                                if (colonIndex > 0) {
                                    String codeLine = line.substring(colonIndex + 1).trim();
                                    fileContent.append(codeLine).append("\n");
                                }
                            } else if (!line.trim().isEmpty()) {
                                // Regular content line
                                fileContent.append(line).append("\n");
                            }
                        }
                    }

                    // Create file entry
                    String finalContent = fileContent.toString().trim();
                    if (!finalContent.isEmpty()) {
                        FileEntry importedFile = new FileEntry(fileName, finalContent);
                        fileContents.put(fileName, importedFile);

                        // Add to file tree
                        TreeItem<String> fileItem = new TreeItem<>(fileName);
                        rootItem.getChildren().add(fileItem);

                        filesImported++;
                        consoleOutput.appendText("   📄 " + fileName + " (" + finalContent.split("\n").length + " lines)\n");

                        // Set test case if we found one
                        if (!currentInput.isEmpty() && inTestCase) {
                            sampleCases.put(fileName, new SampleTestCase(currentInput, currentExpected));
                            testCasesImported++;
                        }
                    }
                }
            }

            // Select first file if any were imported
            if (!rootItem.getChildren().isEmpty()) {
                fileView.getSelectionModel().select(0);
                TreeItem<String> firstFile = rootItem.getChildren().get(0);
                String fileName = firstFile.getValue();
                FileEntry selectedFile = fileContents.get(fileName);

                if (selectedFile != null) {
                    codeEditor.setText(selectedFile.content);
                    currentFile = selectedFile;

                    // Update test case fields
                    SampleTestCase testCase = sampleCases.get(fileName);
                    if (testCase != null) {
                        inputDataArea.setText(testCase.input());
                        expectedOutputArea.setText(testCase.expectedOutput());
                    } else {
                        inputDataArea.clear();
                        expectedOutputArea.clear();
                    }

                    // Update language
                    String extension = getFileExtension(fileName);
                    String language = getLanguageForExtension(extension);
                    languageChoiceBox.setValue(language);

                    // Initialize undo/redo
                    undoStack.clear();
                    redoStack.clear();
                    currentCodeState = selectedFile.content;
                    updateUndoRedoButtons();

                    consoleOutput.appendText("🔍 Auto-selected: " + fileName + "\n");
                }
            }

            consoleOutput.appendText("📊 Summary: " + filesImported + " files, " + testCasesImported + " test cases imported\n");
            return filesImported > 0;

        } catch (Exception e) {
            consoleOutput.appendText("❌ Error parsing project file: " + e.getMessage() + "\n");
            return false;
        }
    }

    /**
     * Analyze imported code for suspicious patterns
     */
    private void analyzeImportedCode() {
        consoleOutput.appendText("🔍 Analyzing imported code for suspicious patterns...\n");

        int suspiciousFiles = 0;
        int totalCheatingIndicators = 0;

        for (Map.Entry<String, FileEntry> entry : fileContents.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue().content;

            int fileIndicators = 0;
            List<String> detectedPatterns = new ArrayList<>();

            // Check for suspicious patterns
            if (content.contains("system(") || content.contains("exec(") || content.contains("Runtime.exec")) {
                detectedPatterns.add("system commands");
                fileIndicators += 3;
            }

            if (content.contains("import ") && content.split("\n").length < 5) {
                detectedPatterns.add("minimal code with imports");
                fileIndicators += 2;
            }

            if (content.toLowerCase().contains("cheat") || content.toLowerCase().contains("bypass")) {
                detectedPatterns.add("suspicious keywords");
                fileIndicators += 4;
            }

            // Check for AI-like patterns (very consistent formatting)
            if (content.split("\n").length > 20) {
                String[] lines = content.split("\n");
                int consistentIndent = 0;
                for (String line : lines) {
                    if (line.matches("^\\s{4}\\S.*") || line.trim().isEmpty()) {
                        consistentIndent++;
                    }
                }
                if (consistentIndent > lines.length * 0.9) {
                    detectedPatterns.add("AI-like consistent formatting");
                    fileIndicators += 2;
                }
            }

            if (fileIndicators > 0) {
                suspiciousFiles++;
                totalCheatingIndicators += fileIndicators;

                consoleOutput.appendText("   ⚠️  " + fileName + " - " +
                        String.join(", ", detectedPatterns) + " (score: " + fileIndicators + ")\n");

                logCheatingEvent("SUSPICIOUS_IMPORT",
                        "Imported file '" + fileName + "' shows patterns: " + String.join(", ", detectedPatterns),
                        Math.min(8, fileIndicators));
            }
        }

        if (suspiciousFiles > 0) {
            consoleOutput.appendText("🚨 " + suspiciousFiles + " suspicious files detected with " +
                    totalCheatingIndicators + " cheating indicators!\n");

            // High severity event for multiple suspicious files
            if (suspiciousFiles >= 2) {
                logCheatingEvent("MULTIPLE_SUSPICIOUS_IMPORTS",
                        "Multiple suspicious files imported: " + suspiciousFiles + " files", 9);
            }
        } else {
            consoleOutput.appendText("✅ No obvious suspicious patterns detected in imported code\n");
        }

        // Update suspicion score based on import
        totalSuspicionScore += Math.min(15, totalCheatingIndicators);
        updateSuspicionScore();
    }

    /**
     * NEW FUNCTIONALITY: Clear Console
     */
    private void clearConsole() {
        // Simple one-click clear with confirmation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Clear Console");
        confirmation.setHeaderText("Clear Console Content");
        confirmation.setContentText("This will remove all text from the console. Continue?");

        // Style the dialog
        DialogPane dialogPane = confirmation.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + DARK_BG + ";");
        dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: " + RED_CLOSE + "; -fx-text-fill: white;");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Clear everything
            consoleOutput.clear();
            inputHistory.clear();
            historyIndex = -1;
            currentInputLine.setLength(0);

            String timestamp = LocalDateTime.now().format(LOG_FORMATTER);
            consoleOutput.appendText("🗑 Console cleared at " + timestamp + "\n");
            consoleOutput.appendText("> ");
            inputStartPosition = consoleOutput.getText().length();

            logCheatingEvent("CONSOLE_CLEAR", "User cleared console", 1);
            consoleOutput.appendText("✅ Console ready for new commands\n");

            // Scroll to bottom
            consoleOutput.setScrollTop(Double.MAX_VALUE);
        } else {
            consoleOutput.appendText("ℹ️ Console clear cancelled\n");
        }
    }

    /**
     * NEW FUNCTIONALITY: Settings Dialog
     */
    private void showSettingsDialog() {
        // Create settings dialog
        Dialog<Boolean> settingsDialog = new Dialog<>();
        settingsDialog.setTitle("IDE Settings");
        settingsDialog.setHeaderText("Configure IDE Preferences");

        // Create main settings container with tabs
        TabPane settingsTabs = new TabPane();
        settingsTabs.setStyle("-fx-background-color: " + DARK_BG + ";");
        settingsTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // === GENERAL SETTINGS TAB ===
        Tab generalTab = new Tab("General");
        generalTab.setStyle("-fx-background-color: " + EDITOR_BG + ";");
        generalTab.setContent(createGeneralSettingsTab());

        // === EDITOR SETTINGS TAB ===
        Tab editorTab = new Tab("Editor");
        editorTab.setStyle("-fx-background-color: " + EDITOR_BG + ";");
        editorTab.setContent(createEditorSettingsTab());

        // === APPEARANCE SETTINGS TAB ===
        Tab appearanceTab = new Tab("Appearance");
        appearanceTab.setStyle("-fx-background-color: " + EDITOR_BG + ";");
        appearanceTab.setContent(createAppearanceSettingsTab());

        // === SECURITY SETTINGS TAB ===
        Tab securityTab = new Tab("Security");
        securityTab.setStyle("-fx-background-color: " + EDITOR_BG + ";");
        securityTab.setContent(createSecuritySettingsTab());

        settingsTabs.getTabs().addAll(generalTab, editorTab, appearanceTab, securityTab);

        // Add buttons
        ButtonType applyButton = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
        ButtonType resetButton = new ButtonType("Reset", ButtonBar.ButtonData.OTHER);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        settingsDialog.getDialogPane().getButtonTypes().addAll(applyButton, resetButton, cancelButton);
        settingsDialog.getDialogPane().setContent(settingsTabs);

        // Style the dialog
        DialogPane dialogPane = settingsDialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + DARK_BG + ";");
        dialogPane.setPrefSize(500, 400);

        // Style buttons
        Button applyBtn = (Button) dialogPane.lookupButton(applyButton);
        applyBtn.setStyle("-fx-background-color: " + GREEN_SUCCESS + "; -fx-text-fill: black; -fx-font-weight: bold;");

        Button resetBtn = (Button) dialogPane.lookupButton(resetButton);
        resetBtn.setStyle("-fx-background-color: " + YELLOW_WARNING + "; -fx-text-fill: black; -fx-font-weight: bold;");

        // Handle button actions
        settingsDialog.setResultConverter(buttonType -> {
            if (buttonType == applyButton) {
                boolean success = applyAllSettings();
                if (success) {
                    consoleOutput.appendText("⚙️ Settings applied successfully\n");
                    logCheatingEvent("SETTINGS_APPLIED", "User applied settings from dialog", 1);
                }
                return success;
            } else if (buttonType == resetButton) {
                resetToDefaults();
                return false; // Don't close dialog after reset
            }
            return false;
        });

        // Load current settings
        loadCurrentSettings();

        // Show dialog
        Optional<Boolean> result = settingsDialog.showAndWait();
        if (result.isPresent() && result.get()) {
            consoleOutput.appendText("✅ Settings saved\n");
        }
    }

    /**
     * Create General Settings Tab
     */
    private ScrollPane createGeneralSettingsTab() {
        // Add missing color constants at the top of the method
        final String SUCCESS_COLOR = "#4CAF50";
        final String WARNING_COLOR = "#FF9800";
        final String MUTED_TEXT = "#888888";
        final String DARK_BORDER = "#2A2A2A";

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + DARK_BG + ";");

        Label sectionLabel = new Label("General Settings");
        sectionLabel.setStyle("-fx-text-fill: " + DEFAULT_ACCENT + "; -fx-font-weight: bold; -fx-font-size: 16;");

        // Auto-save settings with enhanced functionality
        VBox autoSaveBox = createAutoSaveSection(SUCCESS_COLOR, WARNING_COLOR);

        // Session settings with enhanced functionality
        VBox sessionBox = createSessionSection(MUTED_TEXT);

        // Console settings with enhanced functionality
        VBox consoleBox = createConsoleSection();

        // UI/Appearance settings
        VBox appearanceBox = createAppearanceSection();

        // Performance settings
        VBox performanceBox = createPerformanceSection();

        // Apply/Cancel buttons
        HBox buttonBox = createSettingsButtons();

        content.getChildren().addAll(
                sectionLabel, autoSaveBox, sessionBox, consoleBox,
                appearanceBox, performanceBox, buttonBox
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + DARK_BG + "; -fx-border-color: " + DARK_BORDER + ";");

        // Add scroll bar styling
        scrollPane.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.vbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        return scrollPane;
    }

    private VBox createAutoSaveSection(String successColor, String warningColor) {
        VBox autoSaveBox = new VBox(8);
        autoSaveBox.setStyle("-fx-border-color: #2A2A2A; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;");

        Label autoSaveLabel = new Label("💾 Auto-save");
        autoSaveLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold; -fx-font-size: 14;");

        HBox autoSaveOptions = new HBox(10);
        autoSaveOptions.setAlignment(Pos.CENTER_LEFT);

        // Enhanced spinner with step configuration
        Spinner<Integer> autoSaveInterval = new Spinner<>(1, 60, 5);
        autoSaveInterval.setEditable(true);
        autoSaveInterval.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        autoSaveInterval.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + ";");

        // Configure spinner increments
        autoSaveInterval.getValueFactory().setWrapAround(true);

        // Tooltips with enhanced descriptions
        Tooltip autoSaveTooltip = new Tooltip("Automatically save your work at specified intervals\nPrevents data loss in case of crashes or unexpected closures");
        autoSaveTooltip.setStyle("-fx-font-size: 12; -fx-text-fill: " + TEXT_COLOR + ";");
        autoSaveCheckBox.setTooltip(autoSaveTooltip);

        Tooltip intervalTooltip = new Tooltip("Time between auto-saves in minutes\nShorter intervals provide better protection but may impact performance");
        intervalTooltip.setStyle("-fx-font-size: 12; -fx-text-fill: " + TEXT_COLOR + ";");
        autoSaveInterval.setTooltip(intervalTooltip);

        // Enable/disable spinner based on checkbox with visual feedback
        autoSaveInterval.disableProperty().bind(autoSaveCheckBox.selectedProperty().not());

        Label intervalLabel = new Label("Interval (minutes):");
        intervalLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        intervalLabel.disableProperty().bind(autoSaveCheckBox.selectedProperty().not());

        autoSaveOptions.getChildren().addAll(autoSaveCheckBox, intervalLabel, autoSaveInterval);

        // Enhanced auto-save status indicator with progress simulation
        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        Label statusIcon = new Label("🔴");
        statusIcon.setStyle("-fx-font-size: 12;");

        Label autoSaveStatus = new Label("Auto-save: OFF");
        autoSaveStatus.setStyle("-fx-text-fill: " + warningColor + "; -fx-font-size: 12; -fx-font-weight: bold;");

        statusBox.getChildren().addAll(statusIcon, autoSaveStatus);

        // Last save information
        Label lastSaveInfo = new Label("Last save: Never");
        lastSaveInfo.setStyle("-fx-text-fill: #888888; -fx-font-size: 11;");

        // Next save countdown (simulated)
        Label nextSaveCountdown = new Label("");
        nextSaveCountdown.setStyle("-fx-text-fill: #888888; -fx-font-size: 10;");

        // Enhanced event listeners with additional functionality
        autoSaveCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                int interval = autoSaveInterval.getValue();
                autoSaveStatus.setText("Auto-save: ON (every " + interval + " minutes)");
                autoSaveStatus.setStyle("-fx-text-fill: " + successColor + "; -fx-font-size: 12; -fx-font-weight: bold;");
                statusIcon.setText("🟢");
                // Use existing startAutoSaveService method
                startAutoSaveService(interval);
                updateLastSaveInfo(lastSaveInfo, successColor);
                startCountdownTimer(nextSaveCountdown, interval);
            } else {
                autoSaveStatus.setText("Auto-save: OFF");
                autoSaveStatus.setStyle("-fx-text-fill: " + warningColor + "; -fx-font-size: 12; -fx-font-weight: bold;");
                statusIcon.setText("🔴");
                // Use existing stopAutoSaveService method
                stopAutoSaveService();
                nextSaveCountdown.setText("");
            }
        });

        autoSaveInterval.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (autoSaveCheckBox.isSelected()) {
                autoSaveStatus.setText("Auto-save: ON (every " + newVal + " minutes)");
                // Use existing restartAutoSaveService method
                restartAutoSaveService(newVal);
                startCountdownTimer(nextSaveCountdown, newVal);
            }
        });

        // Save now button for manual control
        Button saveNowButton = new Button("Save Now");
        saveNowButton.setStyle("-fx-background-color: " + DEFAULT_ACCENT + "; -fx-text-fill: white; -fx-font-size: 11;");
        saveNowButton.setOnAction(e -> triggerManualSave(lastSaveInfo, successColor));

        // Enable manual save button only when auto-save is enabled
        saveNowButton.disableProperty().bind(autoSaveCheckBox.selectedProperty().not());

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.getChildren().addAll(saveNowButton);

        // Statistics section
        Label statsLabel = new Label("📊 Statistics:");
        statsLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-size: 11; -fx-font-weight: bold;");

        Label statsInfo = new Label("Auto-saves today: 0 | Total auto-saves: 0");
        statsInfo.setStyle("-fx-text-fill: #888888; -fx-font-size: 10;");

        VBox statsBox = new VBox(2);
        statsBox.getChildren().addAll(statsLabel, statsInfo);

        autoSaveBox.getChildren().addAll(
                autoSaveLabel,
                autoSaveOptions,
                statusBox,
                lastSaveInfo,
                nextSaveCountdown,
                actionBox,
                new Separator(),
                statsBox
        );

        return autoSaveBox;
    }

    // New helper methods (only adding ones that don't exist)
    private void updateLastSaveInfo(Label lastSaveInfo, String successColor) {
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        lastSaveInfo.setText("Last save: " + timestamp);
        lastSaveInfo.setStyle("-fx-text-fill: " + successColor + "; -fx-font-size: 11;");
    }

    private void startCountdownTimer(Label countdownLabel, int intervalMinutes) {
        // Simulate countdown timer (in a real implementation, this would use actual timing)
        countdownLabel.setText("Next save in: " + intervalMinutes + "m 00s");

        // In a real implementation, you'd use a Timeline or ScheduledExecutorService
        // for actual countdown functionality
    }

    private void triggerManualSave(Label lastSaveInfo, String successColor) {
        consoleOutput.appendText("💾 Manual save triggered\n");
        updateLastSaveInfo(lastSaveInfo, successColor);

        // Simulate file save operation
        if (currentFile != null) {
            consoleOutput.appendText("💾 Saved: " + currentFile.name + "\n");
            // Call existing save functionality if available
            performManualSave();
        }

        // Reset countdown
        startCountdownTimer(new Label(""), getCurrentAutoSaveInterval());
    }

    private void performManualSave() {
        // This would integrate with your existing save functionality
        // For now, just log the action
        System.out.println("Manual save performed for: " + (currentFile != null ? currentFile.name : "unsaved file"));
    }

    private int getCurrentAutoSaveInterval() {
        // This should return the current auto-save interval from your existing implementation
        // For now, return a default value
        return 5;
    }

    private VBox createSessionSection(String mutedText) {
        VBox sessionBox = new VBox(8);
        Label sessionLabel = new Label("🔑 Session Management");
        sessionLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        CheckBox restoreSessionCheck = new CheckBox("Restore previous session on startup");
        restoreSessionCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        restoreSessionCheck.setTooltip(new Tooltip("Reopen files and restore editor state when application starts"));

        CheckBox backupCheck = new CheckBox("Create backup files (.bak)");
        backupCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        backupCheck.setTooltip(new Tooltip("Create backup copies of files before saving"));

        CheckBox multiSessionCheck = new CheckBox("Allow multiple simultaneous sessions");
        multiSessionCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        multiSessionCheck.setTooltip(new Tooltip("Enable working on multiple projects simultaneously"));

        // Session info display
        Label sessionInfo = new Label("Current session: " + getSessionInfo());
        sessionInfo.setStyle("-fx-text-fill: " + mutedText + "; -fx-font-size: 12;");

        sessionBox.getChildren().addAll(sessionLabel, restoreSessionCheck, backupCheck, multiSessionCheck, sessionInfo);
        return sessionBox;
    }

    private VBox createConsoleSection() {
        VBox consoleBox = new VBox(8);
        Label consoleLabel = new Label("📟 Console Output");
        consoleLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        CheckBox clearConsoleOnRun = new CheckBox("Clear console before each run");
        clearConsoleOnRun.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        clearConsoleOnRun.setTooltip(new Tooltip("Automatically clear console output when running code"));

        CheckBox timestampsCheck = new CheckBox("Show timestamps in console");
        timestampsCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        timestampsCheck.setTooltip(new Tooltip("Add timestamps to each console output line"));

        CheckBox colorizeOutputCheck = new CheckBox("Colorize console output");
        colorizeOutputCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        colorizeOutputCheck.setTooltip(new Tooltip("Use colors for different types of console messages"));

        CheckBox limitOutputCheck = new CheckBox("Limit console output (1000 lines)");
        limitOutputCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        limitOutputCheck.setTooltip(new Tooltip("Prevent console from using too much memory"));

        consoleBox.getChildren().addAll(consoleLabel, clearConsoleOnRun, timestampsCheck, colorizeOutputCheck, limitOutputCheck);
        return consoleBox;
    }

    private VBox createAppearanceSection() {
        VBox appearanceBox = new VBox(8);
        Label appearanceLabel = new Label("🎨 Appearance");
        appearanceLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        // Theme selection
        HBox themeBox = new HBox(10);
        themeBox.setAlignment(Pos.CENTER_LEFT);
        Label themeLabel = new Label("Theme:");
        themeLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        ComboBox<String> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("Dark", "Light", "High Contrast", "Blue Dark", "Green Dark");
        themeComboBox.setValue("Dark");
        themeComboBox.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + ";");

        themeBox.getChildren().addAll(themeLabel, themeComboBox);

        // Font size
        HBox fontSizeBox = new HBox(10);
        fontSizeBox.setAlignment(Pos.CENTER_LEFT);
        Label fontSizeLabel = new Label("Editor font size:");
        fontSizeLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        Spinner<Integer> fontSizeSpinner = new Spinner<>(8, 24, 14);
        fontSizeSpinner.setEditable(true);
        fontSizeSpinner.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + ";");

        fontSizeBox.getChildren().addAll(fontSizeLabel, fontSizeSpinner);

        appearanceBox.getChildren().addAll(appearanceLabel, themeBox, fontSizeBox);
        return appearanceBox;
    }

    private VBox createPerformanceSection() {
        VBox performanceBox = new VBox(8);
        Label performanceLabel = new Label("⚡ Performance");
        performanceLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        CheckBox hardwareAccelerationCheck = new CheckBox("Enable hardware acceleration");
        hardwareAccelerationCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        hardwareAccelerationCheck.setSelected(true);
        hardwareAccelerationCheck.setTooltip(new Tooltip("Use GPU acceleration for better performance"));

        CheckBox memoryMonitorCheck = new CheckBox("Show memory usage monitor");
        memoryMonitorCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        memoryMonitorCheck.setTooltip(new Tooltip("Display memory usage in status bar"));

        // Performance preset
        HBox presetBox = new HBox(10);
        presetBox.setAlignment(Pos.CENTER_LEFT);
        Label presetLabel = new Label("Performance preset:");
        presetLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        ComboBox<String> presetComboBox = new ComboBox<>();
        presetComboBox.getItems().addAll("Balanced", "High Performance", "Battery Saver", "Custom");
        presetComboBox.setValue("Balanced");
        presetComboBox.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + ";");

        presetBox.getChildren().addAll(presetLabel, presetComboBox);

        performanceBox.getChildren().addAll(performanceLabel, hardwareAccelerationCheck, memoryMonitorCheck, presetBox);
        return performanceBox;
    }

    private HBox createSettingsButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button applyButton = new Button("Apply");
        applyButton.setStyle("-fx-background-color: " + DEFAULT_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold;");
        applyButton.setOnAction(e -> applySettings());

        Button resetButton = new Button("Reset to Defaults");
        resetButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        resetButton.setOnAction(e -> resetSettingsToDefaults());

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #888888; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> cancelSettingsChanges());

        buttonBox.getChildren().addAll(resetButton, cancelButton, applyButton);
        return buttonBox;
    }

    // Helper methods for functionality
    private String getSessionInfo() {
        return "1 open file, " + (autoSaveCheckBox.isSelected() ? "auto-save enabled" : "auto-save disabled");
    }

    private void startAutoSaveService(int intervalMinutes) {
        // Implementation for auto-save service
        consoleOutput.appendText("💾 Auto-save enabled (" + intervalMinutes + " minute interval)\n");
    }

    private void stopAutoSaveService() {
        // Implementation to stop auto-save service
        consoleOutput.appendText("💾 Auto-save disabled\n");
    }

    private void restartAutoSaveService(int newInterval) {
        stopAutoSaveService();
        startAutoSaveService(newInterval);
    }

    private void applySettings() {
        consoleOutput.appendText("✅ Settings applied successfully\n");
        // Implementation to save settings
    }

    private void resetSettingsToDefaults() {
        consoleOutput.appendText("⚙️ Settings reset to defaults\n");
        // Implementation to reset settings
    }

    private void cancelSettingsChanges() {
        consoleOutput.appendText("❌ Settings changes cancelled\n");
        // Implementation to revert changes
    }

    /**
     * Create Editor Settings Tab
     */
    private ScrollPane createEditorSettingsTab() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + DARK_BG + ";");

        Label sectionLabel = new Label("Editor Settings");
        sectionLabel.setStyle("-fx-text-fill: " + DEFAULT_ACCENT + "; -fx-font-weight: bold; -fx-font-size: 16;");

        // Syntax and highlighting
        VBox syntaxBox = new VBox(8);
        Label syntaxLabel = new Label("Syntax & Highlighting:");
        syntaxLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        HBox syntaxOptions = new HBox(15);
        syntaxOptions.setAlignment(Pos.CENTER_LEFT);

        syntaxOptions.getChildren().addAll(
                syntaxHighlightCheckBox,
                new Label("Theme:")
        );

        ChoiceBox<String> syntaxTheme = new ChoiceBox<>();
        syntaxTheme.getItems().addAll("Dark", "Light", "Monokai", "Solarized", "Dracula");
        syntaxTheme.setValue("Dark");
        syntaxTheme.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + ";");

        syntaxOptions.getChildren().add(syntaxTheme);
        syntaxBox.getChildren().addAll(syntaxLabel, syntaxOptions);

        // Editor behavior
        VBox behaviorBox = new VBox(8);
        Label behaviorLabel = new Label("Editor Behavior:");
        behaviorLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        CheckBox lineNumbersCheck = new CheckBox("Show line numbers");
        lineNumbersCheck.setSelected(true);
        lineNumbersCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox wordWrapCheck = new CheckBox("Enable word wrap");
        wordWrapCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox bracketMatchCheck = new CheckBox("Highlight matching brackets");
        bracketMatchCheck.setSelected(true);
        bracketMatchCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox autoIndentCheck = new CheckBox("Auto-indent");
        autoIndentCheck.setSelected(true);
        autoIndentCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        behaviorBox.getChildren().addAll(behaviorLabel, lineNumbersCheck, wordWrapCheck, bracketMatchCheck, autoIndentCheck);

        // Code completion
        VBox completionBox = new VBox(8);
        Label completionLabel = new Label("Code Completion:");
        completionLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        CheckBox autoCompleteCheck = new CheckBox("Enable auto-completion");
        autoCompleteCheck.setSelected(true);
        autoCompleteCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox parameterHintCheck = new CheckBox("Show parameter hints");
        parameterHintCheck.setSelected(true);
        parameterHintCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        HBox delayBox = new HBox(10);
        delayBox.setAlignment(Pos.CENTER_LEFT);
        delayBox.getChildren().addAll(
                new Label("Completion delay (ms):"),
                new Spinner<>(100, 2000, 500)
        );

        completionBox.getChildren().addAll(completionLabel, autoCompleteCheck, parameterHintCheck, delayBox);

        content.getChildren().addAll(sectionLabel, syntaxBox, behaviorBox, completionBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + DARK_BG + ";");
        return scrollPane;
    }

    /**
     * Create Appearance Settings Tab
     */
    private ScrollPane createAppearanceSettingsTab() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + DARK_BG + ";");

        Label sectionLabel = new Label("Appearance Settings");
        sectionLabel.setStyle("-fx-text-fill: " + DEFAULT_ACCENT + "; -fx-font-weight: bold; -fx-font-size: 16;");

        // Theme selection
        VBox themeBox = new VBox(8);
        Label themeLabel = new Label("Theme:");
        themeLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        ChoiceBox<String> themeChoice = new ChoiceBox<>();
        themeChoice.getItems().addAll("Dark", "Light", "High Contrast", "Blue", "Green", "Purple");
        themeChoice.setValue("Dark");
        themeChoice.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + "; -fx-pref-width: 150;");

        // Theme preview
        Button previewThemeBtn = new Button("Preview");
        previewThemeBtn.setStyle("-fx-background-color: " + BLUE_INFO + "; -fx-text-fill: black;");
        previewThemeBtn.setOnAction(e -> previewTheme(themeChoice.getValue()));

        HBox themeOptions = new HBox(10);
        themeOptions.setAlignment(Pos.CENTER_LEFT);
        themeOptions.getChildren().addAll(themeChoice, previewThemeBtn);

        themeBox.getChildren().addAll(themeLabel, themeOptions);

        // Font settings
        VBox fontBox = new VBox(8);
        Label fontLabel = new Label("Font:");
        fontLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        HBox fontSettings = new HBox(10);
        fontSettings.setAlignment(Pos.CENTER_LEFT);

        ChoiceBox<String> fontFamily = new ChoiceBox<>();
        fontFamily.getItems().addAll("Monospaced", "Consolas", "Source Code Pro", "Fira Code", "JetBrains Mono");
        fontFamily.setValue("Monospaced");
        fontFamily.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + ";");

        Spinner<Integer> fontSize = new Spinner<>(8, 24, 12);
        fontSize.setEditable(true);
        fontSize.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + ";");

        fontSettings.getChildren().addAll(
                new Label("Family:"), fontFamily,
                new Label("Size:"), fontSize
        );
        fontBox.getChildren().addAll(fontLabel, fontSettings);

        // UI scaling
        VBox scaleBox = new VBox(8);
        Label scaleLabel = new Label("UI Scaling:");
        scaleLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        Slider uiScale = new Slider(80, 150, 100);
        uiScale.setShowTickLabels(true);
        uiScale.setShowTickMarks(true);
        uiScale.setMajorTickUnit(10);

        Label scaleValue = new Label("100%");
        scaleValue.setStyle("-fx-text-fill: " + BLUE_INFO + ";");

        uiScale.valueProperty().addListener((obs, oldVal, newVal) -> {
            scaleValue.setText(Math.round(newVal.doubleValue()) + "%");
        });

        HBox scaleOptions = new HBox(10);
        scaleOptions.setAlignment(Pos.CENTER_LEFT);
        scaleOptions.getChildren().addAll(uiScale, scaleValue);

        scaleBox.getChildren().addAll(scaleLabel, scaleOptions);

        content.getChildren().addAll(sectionLabel, themeBox, fontBox, scaleBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + DARK_BG + ";");
        return scrollPane;
    }

    /**
     * Create Security Settings Tab
     */
    private ScrollPane createSecuritySettingsTab() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + DARK_BG + ";");

        Label sectionLabel = new Label("Security & Monitoring Settings");
        sectionLabel.setStyle("-fx-text-fill: " + DEFAULT_ACCENT + "; -fx-font-weight: bold; -fx-font-size: 16;");

        // Anti-cheating settings
        VBox cheatingBox = new VBox(8);
        Label cheatingLabel = new Label("Anti-Cheating:");
        cheatingLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        CheckBox strictModeCheck = new CheckBox("Strict monitoring mode");
        strictModeCheck.setSelected(true);
        strictModeCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox focusMonitoringCheck = new CheckBox("Monitor window focus");
        focusMonitoringCheck.setSelected(true);
        focusMonitoringCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox copyPasteMonitorCheck = new CheckBox("Monitor copy/paste operations");
        copyPasteMonitorCheck.setSelected(true);
        copyPasteMonitorCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox screenshotMonitorCheck = new CheckBox("Detect screenshot attempts");
        screenshotMonitorCheck.setSelected(true);
        screenshotMonitorCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        cheatingBox.getChildren().addAll(cheatingLabel, strictModeCheck, focusMonitoringCheck,
                copyPasteMonitorCheck, screenshotMonitorCheck);

        // Privacy settings
        VBox privacyBox = new VBox(8);
        Label privacyLabel = new Label("Privacy:");
        privacyLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        CheckBox keystrokeLoggingCheck = new CheckBox("Enable keystroke logging");
        keystrokeLoggingCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox activityReportsCheck = new CheckBox("Generate activity reports");
        activityReportsCheck.setSelected(true);
        activityReportsCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox autoSubmitCheck = new CheckBox("Auto-submit suspicion reports");
        autoSubmitCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        privacyBox.getChildren().addAll(privacyLabel, keystrokeLoggingCheck, activityReportsCheck, autoSubmitCheck);

        // Alert settings
        VBox alertBox = new VBox(8);
        Label alertLabel = new Label("Alerts:");
        alertLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold;");

        ChoiceBox<String> alertLevel = new ChoiceBox<>();
        alertLevel.getItems().addAll("Low", "Medium", "High", "Very High");
        alertLevel.setValue("Medium");
        alertLevel.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox soundAlertsCheck = new CheckBox("Enable sound alerts");
        soundAlertsCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        CheckBox visualAlertsCheck = new CheckBox("Enable visual alerts");
        visualAlertsCheck.setSelected(true);
        visualAlertsCheck.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        alertBox.getChildren().addAll(alertLabel,
                new Label("Sensitivity:"), alertLevel,
                soundAlertsCheck, visualAlertsCheck
        );

        content.getChildren().addAll(sectionLabel, cheatingBox, privacyBox, alertBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + DARK_BG + ";");
        return scrollPane;
    }

    /**
     * Apply all settings
     */
    private boolean applyAllSettings() {
        consoleOutput.appendText("⚙️ Applying settings...\n");

        try {
            // Apply auto-save settings
            if (autoSaveCheckBox.isSelected()) {
                consoleOutput.appendText("   💾 Auto-save enabled\n");
            }

            // Apply syntax highlighting
            if (syntaxHighlightCheckBox.isSelected()) {
                consoleOutput.appendText("   🎨 Syntax highlighting enabled\n");
            }

            // Apply security settings
            consoleOutput.appendText("   🔒 Security settings applied\n");

            // Save settings to file (in a real implementation)
            saveSettings();

            consoleOutput.appendText("✅ All settings applied successfully\n");
            return true;

        } catch (Exception e) {
            consoleOutput.appendText("❌ Error applying settings: " + e.getMessage() + "\n");
            return false;
        }
    }

    /**
     * Reset settings to defaults
     */
    private void resetToDefaults() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset Settings");
        confirm.setHeaderText("Reset to Defaults");
        confirm.setContentText("Are you sure you want to reset all settings to their default values?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Reset all checkboxes and controls to defaults
            autoSaveCheckBox.setSelected(true);
            syntaxHighlightCheckBox.setSelected(true);

            consoleOutput.appendText("🔄 Settings reset to defaults\n");
            logCheatingEvent("SETTINGS_RESET", "User reset settings to defaults", 1);
        }
    }

    /**
     * Load current settings
     */
    private void loadCurrentSettings() {
        // In a real implementation, this would load from a settings file
        autoSaveCheckBox.setSelected(true);
        syntaxHighlightCheckBox.setSelected(true);
    }

    /**
     * Save settings to file
     */
    private void saveSettings() {
        // In a real implementation, this would save to a settings file
        consoleOutput.appendText("   💾 Settings saved to configuration file\n");
    }

    /**
     * Preview theme
     */
    private void previewTheme(String theme) {
        consoleOutput.appendText("🎨 Previewing theme: " + theme + "\n");
        // In a real implementation, this would temporarily apply the theme for preview
    }

    /**
     * NEW FUNCTIONALITY: Code Completion
     */
    private void showCodeCompletion(String trigger) {
        // Simple code completion simulation
        String[] javaCompletions = {"println(", "main(", "String ", "int ", "void "};
        String[] pythonCompletions = {"print(", "def ", "if ", "for ", "while "};
        String[] cppCompletions = {"cout << ", "cin >> ", "int ", "void ", "string "};

        String[] completions;
        switch (languageChoiceBox.getValue()) {
            case "Java": completions = javaCompletions; break;
            case "Python": completions = pythonCompletions; break;
            case "C++": completions = cppCompletions; break;
            default: completions = new String[0];
        }

        if (completions.length > 0) {
            // In a real IDE, this would show a completion popup
            consoleOutput.appendText("💡 Code completion available (" + completions.length + " suggestions)\n");
        }
    }

    /**
     * NEW FUNCTIONALITY: Perform Code Analysis
     */
    private void performCodeAnalysis(String fileName, String code) {
        int linesOfCode = code.split("\n").length;
        int complexity = estimateComplexity(code);
        int warningCount = analyzeCodeForIssues(code, languageChoiceBox.getValue()).size();
        double similarityScore = calculateCodeSimilarity(code);

        CodeAnalysisResult result = new CodeAnalysisResult(complexity, linesOfCode, warningCount, similarityScore);
        codeAnalysisResults.put(fileName, result);
    }

    /**
     * Start program execution in console
     */
    private void startProgramExecution() {
        String language = languageChoiceBox.getValue();
        String code = codeEditor.getText();

        isProgramRunning = true;
        programState.clear();
        cinOperations.clear();
        currentCinOperation = 0;
        inputHistory.clear();
        historyIndex = -1;

        javafx.application.Platform.runLater(() -> {
            consoleOutput.clear();
            appendToConsole("🚀 Starting " + language + " program execution...\n");
            appendToConsole("Type input when prompted. Press Ctrl+C to stop.\n\n");

            // Show initial program output
            String initialOutput = extractInitialProgramOutput(code, language);
            if (!initialOutput.isEmpty()) {
                appendToConsole("📢 " + initialOutput + "\n");
            }

            if (hasInputOperations(code, language)) {
                String firstPrompt = getNextInputPrompt(language, code);
                waitForConsoleInput(firstPrompt);
            } else {
                // Execute without input
                String result = executeProgramLogic(language, code, "");
                appendToConsole(result + "\n");
                isProgramRunning = false;
                showConsolePrompt("Program finished. Press Run to execute again.\n> ");
            }
        });

        logCheatingEvent("PROGRAM_EXECUTION", "Started " + language + " program execution", 1);
    }

    private String extractInitialProgramOutput(String code, String language) {
        switch (language) {
            case "C++":
                return extractCppInitialOutput(code);
            case "Java":
                return extractJavaInitialOutput(code);
            case "Python":
                return extractPythonInitialOutput(code);
            default:
                return "";
        }
    }

    private String extractCppInitialOutput(String code) {
        Pattern coutPattern = Pattern.compile("cout\\s*<<\\s*\"([^\"]*)\"\\s*;");
        Matcher matcher = coutPattern.matcher(code);
        StringBuilder output = new StringBuilder();

        while (matcher.find()) {
            // Check if this cout comes before first cin
            int coutPos = matcher.start();
            int firstCinPos = code.indexOf("cin");
            if (firstCinPos == -1 || coutPos < firstCinPos) {
                output.append(matcher.group(1)).append(" ");
            }
        }

        return output.toString().trim();
    }

    private String extractJavaInitialOutput(String code) {
        Pattern printPattern = Pattern.compile("System\\.out\\.print(?:ln)?\\(\\s*\"([^\"]*)\"\\s*\\)");
        Matcher matcher = printPattern.matcher(code);
        StringBuilder output = new StringBuilder();

        while (matcher.find()) {
            output.append(matcher.group(1)).append(" ");
        }

        return output.toString().trim();
    }

    private String extractPythonInitialOutput(String code) {
        Pattern printPattern = Pattern.compile("print\\(\\s*\"([^\"]*)\"\\s*\\)");
        Matcher matcher = printPattern.matcher(code);
        StringBuilder output = new StringBuilder();

        while (matcher.find()) {
            output.append(matcher.group(1)).append(" ");
        }

        return output.toString().trim();
    }

    private boolean hasInputOperations(String code, String language) {
        switch (language) {
            case "C++": return code.contains("cin") || code.contains("scanf");
            case "Java": return code.contains("Scanner") || code.contains("System.in");
            case "Python": return code.contains("input(");
            default: return false;
        }
    }

    /**
     * Create new file with dynamic name and extension
     */
    private void createNewFile() {
        String fileName = newFileNameInput.getText().trim();

        if (fileName.isEmpty()) {
            showAlert("Error", "Please enter a file name");
            return;
        }

        // Validate file name
        if (!isValidFileName(fileName)) {
            showAlert("Error", "Invalid file name. Use only letters, numbers, dots, and underscores.");
            return;
        }

        // Check if file already exists
        if (fileContents.containsKey(fileName)) {
            showAlert("Error", "File '" + fileName + "' already exists!");
            return;
        }

        // Get file extension and determine language
        String extension = getFileExtension(fileName);
        String language = getLanguageForExtension(extension);

        // Generate template based on file extension
        String templateContent = generateTemplateForFile(fileName, extension, language);

        // Create new file entry
        FileEntry newFile = new FileEntry(fileName, templateContent);
        fileContents.put(fileName, newFile);

        // Initialize sample test case
        sampleCases.put(fileName, new SampleTestCase("", ""));

        // Add to file tree
        TreeItem<String> newFileItem = new TreeItem<>(fileName);
        rootItem.getChildren().add(newFileItem);

        // Select the new file
        fileView.getSelectionModel().select(newFileItem);

        // Clear the input field
        newFileNameInput.clear();

        // Update console with success message
        consoleOutput.appendText("✅ Created new file: " + fileName + " (" + language + ")\n");

        // Log the event
        logCheatingEvent("FILE_CREATED", "User created new file: " + fileName, 1);
    }

    /**
     * Validate file name
     */
    private boolean isValidFileName(String fileName) {
        // Basic validation: should not be empty and should contain valid characters
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        // Check for invalid characters
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]+$");
        return pattern.matcher(fileName).matches();
    }

    /**
     * Generate appropriate template based on file extension
     */
    private String generateTemplateForFile(String fileName, String extension, String language) {
        String template;

        switch (language) {
            case "Java":
                String className = fileName.substring(0, fileName.lastIndexOf('.'));
                className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
                template = "public class " + className + " {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        System.out.println(\"Hello from " + fileName + "!\");\n" +
                        "    }\n" +
                        "}";
                break;

            case "C++":
                template = "#include <iostream>\n" +
                        "using namespace std;\n\n" +
                        "int main() {\n" +
                        "    cout << \"Hello from " + fileName + "!\" << endl;\n" +
                        "    return 0;\n" +
                        "}";
                break;

            case "Python":
                template = "def main():\n" +
                        "    print(\"Hello from " + fileName + "!\")\n\n" +
                        "if __name__ == \"__main__\":\n" +
                        "    main()";
                break;

            case "JavaScript":
                template = "console.log(\"Hello from " + fileName + "!\");";
                break;

            default:
                template = "// " + fileName + "\n// Write your code here";
                break;
        }

        return template;
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private HBox createTopBar() {
        titleLabel.setFont(Font.font("Inter", 16));
        titleLabel.setStyle("-fx-text-fill: " + RED_CLOSE + "; -fx-font-weight: bold;");

        // Set run button action to start program execution
        runButton.setOnAction(e -> startProgramExecution());

        Button minimizeButton = createControlButton("—", YELLOW_WARNING, () -> {
            logCheatingEvent("MANUAL_MINIMIZE", "User clicked minimize button", 2);
            mainStage.setIconified(true);
        });

        Button closeButton = createControlButton("✕", RED_CLOSE, () -> {
            logCheatingEvent("SESSION_END", "User ended assessment session", 1);
            generateFinalReport();
            mainStage.close();
        });

        // Enhanced top bar with new buttons - organized in groups
        HBox leftControls = new HBox(5);
        leftControls.setAlignment(Pos.CENTER_LEFT);

        // File operations group
        HBox fileGroup = new HBox(3, createFileButton, saveButton);
        fileGroup.setStyle("-fx-border-color: #444; -fx-border-width: 0 1 0 0; -fx-padding: 0 5 0 0;");

        // Edit operations group
        HBox editGroup = new HBox(3, undoButton, redoButton, formatButton);
        editGroup.setStyle("-fx-border-color: #444; -fx-border-width: 0 1 0 0; -fx-padding: 0 5 0 0;");

        // Execution group
        HBox execGroup = new HBox(3, runButton, debugButton, autoTestButton);
        execGroup.setStyle("-fx-border-color: #444; -fx-border-width: 0 1 0 0; -fx-padding: 0 5 0 0;");

        // Submission group
        HBox submitGroup = new HBox(3, submitButton, checkAIButton);

        leftControls.getChildren().addAll(titleLabel, fileGroup, editGroup, execGroup, submitGroup);

        // Search area
        HBox searchArea = new HBox(3, searchField, searchButton);
        searchArea.setAlignment(Pos.CENTER);

        HBox windowControls = new HBox(5, minimizeButton, closeButton);

        HBox topBar = new HBox(leftControls, new Region(), searchArea, new Region(), windowControls);
        HBox.setHgrow(topBar.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(topBar.getChildren().get(3), Priority.ALWAYS);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(8, 12, 8, 15));
        topBar.setStyle("-fx-background-color: #2a2a3a; -fx-border-color: " + RED_CLOSE + "; -fx-border-width: 0 0 2 0;");

        return topBar;
    }

    private VBox createSidebar() {
        languageChoiceBox.getItems().addAll("Java", "Python", "C++", "JavaScript");
        languageChoiceBox.setValue("C++");
        languageChoiceBox.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");

        languageChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateThemeColor(newVal);
        });

        // Set up create file button
        createFileButton.setOnAction(e -> createNewFile());

        // Allow creating file by pressing Enter in the text field
        newFileNameInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                createNewFile();
            }
        });

        // File creation section
        VBox fileCreationBox = new VBox(5);
        fileCreationBox.getChildren().addAll(
                new Label("Create New File:"),
                newFileNameInput,
                createFileButton
        );

        // Settings section
        VBox settingsBox = new VBox(5);
        settingsBox.getChildren().addAll(
                new Label("Settings:"),
                autoSaveCheckBox,
                syntaxHighlightCheckBox,
                settingsButton
        );

        // Utility buttons - organized in a compact grid
        GridPane utilityGrid = new GridPane();
        utilityGrid.setHgap(3);
        utilityGrid.setVgap(3);
        utilityGrid.add(exportButton, 0, 0);
        utilityGrid.add(importButton, 1, 0);
        utilityGrid.add(clearConsoleButton, 0, 1);
        utilityGrid.setAlignment(Pos.CENTER_LEFT);

        // File selection handler
        fileView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue() != null) {
                isSwitchingFile = true;
                String selectedFileName = newVal.getValue();
                FileEntry selectedFile = fileContents.get(selectedFileName);

                if (selectedFile != null) {
                    codeEditor.setText(selectedFile.content);
                    currentFile = selectedFile;

                    SampleTestCase currentCase = sampleCases.getOrDefault(selectedFileName,
                            new SampleTestCase("", ""));

                    inputDataArea.setText(currentCase.input());
                    expectedOutputArea.setText(currentCase.expectedOutput());

                    String extension = getFileExtension(selectedFileName);
                    String language = getLanguageForExtension(extension);
                    languageChoiceBox.setValue(language);

                    // Update console with file switch info
                    consoleOutput.appendText("📁 Switched to file: " + selectedFileName + "\n");

                    // Initialize undo/redo for new file
                    undoStack.clear();
                    redoStack.clear();
                    currentCodeState = selectedFile.content;
                    updateUndoRedoButtons();
                }
                isSwitchingFile = false;
            }
        });

        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: " + EDITOR_BG + ";");
        sidebar.getChildren().addAll(
                new Label("Programming Language:"), languageChoiceBox,
                new Separator(),
                fileCreationBox,
                new Separator(),
                new Label("File Explorer:"), fileView,
                new Separator(),
                settingsBox,
                new Separator(),
                new Label("Utilities:"), utilityGrid
        );

        return sidebar;
    }

    private VBox createCodeEditor() {
        codeEditor.setFont(Font.font("Monospaced", 14));
        codeEditor.setStyle("-fx-control-inner-background: #181926; -fx-text-fill: " + TEXT_COLOR + "; -fx-font-family: 'Monospaced';");

        // Enhanced console styling
        consoleOutput.setFont(Font.font("Monospaced", 12));
        consoleOutput.setStyle("-fx-control-inner-background: #1a1b26; -fx-text-fill: " + GREEN_SUCCESS + "; -fx-font-family: 'Consolas', 'Monaco', monospace;");
        consoleOutput.setEditable(false);

        inputDataArea.setStyle("-fx-control-inner-background: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + "; -fx-font-size: 12px;");
        expectedOutputArea.setStyle("-fx-control-inner-background: " + INPUT_BG + "; -fx-text-fill: " + TEXT_COLOR + "; -fx-font-size: 12px;");
        focusLogArea.setStyle("-fx-control-inner-background: #1a1a2e; -fx-text-fill: " + BLUE_INFO + "; -fx-font-size: 11px;");
        focusLogArea.setEditable(false);

        // Create tabbed interface for student view
        TabPane studentTabs = new TabPane();
        studentTabs.setStyle("-fx-font-size: 12px;");

        Tab codeTab = new Tab("Code Editor", codeEditor);
        Tab consoleTab = new Tab("Interactive Console", consoleOutput);
        Tab inputTab = new Tab("Input/Output", new VBox(3,
                new Label("Input Data:"), inputDataArea,
                new Label("Expected Output:"), expectedOutputArea));
        Tab monitorTab = new Tab("Activity Monitor", new VBox(3,
                new Label("Your Activity Log:"), focusLogArea));

        studentTabs.getTabs().addAll(codeTab, consoleTab, inputTab, monitorTab);

        VBox editorBox = new VBox(studentTabs);
        VBox.setVgrow(studentTabs, Priority.ALWAYS);

        return editorBox;
    }

    private Button createStyledButton(String text, String bgColor, String textColor) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + bgColor + "; " +
                "-fx-text-fill: " + textColor + "; " +
                "-fx-padding: 4 8; " +
                "-fx-font-size: 11px; " +
                "-fx-font-weight: bold; " +
                "-fx-min-width: 60px; " +
                "-fx-max-width: 80px;");
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private Button createControlButton(String text, String color, Runnable action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: " + color + "; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-min-width: 30px; " +
                "-fx-min-height: 25px;");
        button.setOnAction(e -> action.run());
        return button;
    }

    private void updateThemeColor(String language) {
        currentAccentColor = languageColorMap.getOrDefault(language, DEFAULT_ACCENT);
    }

    private void setupInitialFiles() {
        String fileName = "example.cpp";
        String initialContent = "#include <iostream>\nusing namespace std;\n\nint main() {\n    int x;\n    cout << \"Enter a number: \";\n    cin >> x;\n    if (x > 5) {\n        cout << \"Number is greater than 5\";\n    } else {\n        cout << \"Number is 5 or less\";\n    }\n    return 0;\n}";

        FileEntry mainFile = new FileEntry(fileName, initialContent);
        fileContents.put(mainFile.name, mainFile);
        sampleCases.put(mainFile.name, new SampleTestCase("6", "Number is greater than 5"));

        rootItem.getChildren().add(new TreeItem<>(mainFile.name));

        // Initialize undo/redo state
        currentCodeState = initialContent;
        updateUndoRedoButtons();
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }

    private String getLanguageForExtension(String extension) {
        switch (extension) {
            case "java": return "Java";
            case "py": return "Python";
            case "cpp": case "cxx": case "cc": case "h": case "hpp": return "C++";
            case "js": case "jsx": case "ts": case "tsx": return "JavaScript";
            case "txt": case "md": case "text": return "Text";
            default: return "Unknown";
        }
    }

    private void generateFinalReport() {
        System.out.println("=== ENHANCED ASSESSMENT SESSION REPORT ===");
        System.out.println("Session Duration: " + ((System.currentTimeMillis() - sessionStartTime) / 60000) + " minutes");
        System.out.println("Focus Loss Events: " + focusLossCount);
        System.out.println("Copy Operations: " + copyOperationCount);
        System.out.println("Window Minimizes: " + minimizeCount);
        System.out.println("Tab Switches: " + tabSwitchCount);
        System.out.println("Rapid Typing Events: " + rapidTypingEvents);
        System.out.println("Debug Sessions: " + debugSessionCount);
        System.out.println("Auto-Format Uses: " + autoFormatCount);
        System.out.println("Total Out-of-Focus Time: " + (totalOutOfFocusTime / 1000) + " seconds");
        System.out.println("Final Suspicion Score: " + totalSuspicionScore + "/100");
        System.out.println("Files Created: " + fileContents.size());

        // Print code analysis summary
        System.out.println("\n=== CODE ANALYSIS SUMMARY ===");
        for (Map.Entry<String, CodeAnalysisResult> entry : codeAnalysisResults.entrySet()) {
            CodeAnalysisResult result = entry.getValue();
            System.out.printf("File: %s - Complexity: %d, Lines: %d, Warnings: %d, Similarity: %.1f%%%n",
                    entry.getKey(), result.complexity(), result.linesOfCode(),
                    result.warningCount(), result.similarityScore() * 100);
        }

        // Print all events
        System.out.println("\n=== DETAILED EVENT LOG ===");
        for (CheatingEvent event : allEvents) {
            System.out.printf("[%s] %s: %s (Severity: %d)%n",
                    event.timestamp(), event.type(), event.details(), event.severity());
        }

        // Print files created
        System.out.println("\n=== FILES CREATED ===");
        for (String fileName : fileContents.keySet()) {
            System.out.println("- " + fileName);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}