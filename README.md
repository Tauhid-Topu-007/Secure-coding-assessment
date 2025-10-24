# Secure Coding Assessment IDE

A comprehensive, anti-cheating integrated development environment designed for secure coding assessments and programming exams. This IDE provides real-time monitoring and cheating detection while offering a full-featured coding environment.

---

## 🚀 Features

### 🛡️ Anti-Cheating & Security
- **Real-time Monitoring:** Continuous surveillance of user activity.
- **Focus Detection:** Monitors window focus loss and tab switching.
- **Copy/Paste Monitoring:** Tracks all copy, cut, and paste operations.
- **Keystroke Analysis:** Detects rapid typing patterns and suspicious input.
- **Screenshot Prevention:** Monitors screenshot attempts.
- **Activity Logging:** Comprehensive event logging with severity scoring.
- **AI Pattern Detection:** Analyzes code for AI-generated patterns.

### 💻 Development Features
- **Multi-language Support:** Java, Python, C++, JavaScript.
- **Interactive Console:** Real-time program execution with input/output handling.
- **Code Completion:** Intelligent code suggestions.
- **Syntax Highlighting:** Language-specific syntax coloring.
- **Auto-Formatting:** Code formatting and indentation.
- **Debug Tools:** Built-in debugging and analysis.
- **File Management:** Create, save, and manage multiple files.

### 📊 Monitoring & Analytics
- **Instructor Dashboard:** Real-time monitoring interface.
- **Suspicion Scoring:** Dynamic risk assessment algorithm.
- **Code Analysis:** Complexity, similarity, and pattern analysis.
- **Session Statistics:** Comprehensive activity metrics.
- **Export Reports:** Detailed session reports.

### ⚙️ Customization
- **Theme Support:** Multiple color themes (Dark, Light, High Contrast, etc.).
- **Font Customization:** Adjustable font family and size.
- **Auto-save:** Configurable automatic saving.
- **Settings Profiles:** Save and load configuration profiles.

## 🛠️ Installation

### Prerequisites
- Java 17 or higher
- JavaFX 17 or higher

### Build & Run

```bash
git clone https://github.com/Tauhid-Topu-007/secure-coding-assessment.git
cd secure-coding-ide
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml org/example/ide/AntiCheatingIDE.java
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml org.example.ide.AntiCheatingIDE
```

### IDE Setup (IntelliJ/Eclipse)
- Ensure JavaFX is properly configured in your IDE.
- Add JavaFX libraries to your project module path.
- Set VM options: `--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`

## 📖 Usage

### For Students
- **Start Session:** Launch the IDE to begin monitored coding session.
- **Create Files:** Use the sidebar to create new files with appropriate extensions.
- **Write Code:** Use the code editor with syntax highlighting and auto-completion.
- **Test Code:** Use the interactive console to run and test programs.
- **Submit:** Use the submit button when finished.

### For Instructors
- **Monitor Dashboard:** Watch real-time activity in the instructor panel.
- **Review Suspicion Scores:** Track risk levels and suspicious activities.
- **Analyze Code:** Review code patterns and AI detection results.
- **Export Reports:** Generate comprehensive session reports.

## 🔒 Security Considerations
- **Privacy:** All monitoring is transparent to the user. Activity logs are stored locally. No external data transmission without consent.
- **Integrity:** Full-screen enforcement, copy/paste restrictions, and real-time code validation ensure assessment integrity.

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments
- JavaFX team for the robust UI framework.
- Open-source community for inspiration and libraries.
- Educational institutions for testing and feedback.

## 📞 Support
- Create an issue on GitHub.
- Check the documentation.
- Contact the development team.

> Note: This IDE is designed for educational and assessment purposes. Ensure compliance with local privacy laws and institutional policies when deploying.
