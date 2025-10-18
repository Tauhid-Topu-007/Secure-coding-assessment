# 🧠 Secure Coding Assessment IDE

A **comprehensive, anti-cheating integrated development environment** designed for secure coding assessments and programming exams.  
This IDE provides **real-time monitoring**, **activity analytics**, and **AI-based cheating detection** — while offering a smooth, full-featured coding experience.

---

## 🚀 Features

### 🛡️ Anti-Cheating & Security
- **Real-time Monitoring:** Continuous surveillance of user activity  
- **Focus Detection:** Tracks window focus loss and tab switching  
- **Copy/Paste Monitoring:** Logs copy, cut, and paste operations  
- **Keystroke Analysis:** Detects rapid or irregular typing patterns  
- **Screenshot Prevention:** Detects and logs screenshot attempts  
- **Activity Logging:** Comprehensive event logging with severity scoring  
- **AI Pattern Detection:** Flags AI-generated or copied code  

---

### 💻 Development Features
- **Multi-language Support:** Java, Python, C++, JavaScript  
- **Interactive Console:** Real-time program execution with input/output  
- **Code Completion:** Intelligent code suggestions  
- **Syntax Highlighting:** Language-specific syntax coloring  
- **Auto-Formatting:** Clean code formatting and indentation  
- **Debug Tools:** Integrated runtime debugging and error tracking  
- **File Management:** Create, save, open, and manage multiple files  

---

### 📊 Monitoring & Analytics
- **Instructor Dashboard:** Real-time monitoring of all sessions  
- **Suspicion Scoring:** Dynamic risk assessment system  
- **Code Analysis:** Complexity, similarity, and AI pattern checks  
- **Session Statistics:** Detailed logs and metrics per student  
- **Export Reports:** Generate comprehensive session reports  

---

### ⚙️ Customization
- **Theme Support:** Dark, Light, and High-Contrast modes  
- **Font Customization:** Adjustable font family and size  
- **Auto-save:** Configurable automatic saving intervals  
- **Settings Profiles:** Save and load configuration presets  

---

## 🛠️ Installation

### Prerequisites
- **Java 17** or higher  
- **JavaFX 17** or higher  

---

### Build & Run

**Clone the repository:**
```bash
git clone https://github.com/Tauhid-Topu-007/secure-coding-assessment.git
cd secure-coding-assessment
Compile the project:

bash
Copy code
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml org/example/ide/AntiCheatingIDE.java
Run the application:

bash
Copy code
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml org.example.ide.AntiCheatingIDE
IDE Setup (IntelliJ / Eclipse)
Add JavaFX libraries to your project’s module path

Set VM options:

css
Copy code
--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
Run the AntiCheatingIDE class.

📖 Usage
For Students
Start Session: Launch the IDE for your coding test

Create Files: Use the file toolbar to make new code files

Write Code: Type in the code editor with syntax highlighting

Test Code: Run with input/output simulation

Submit: Use the Submit button when done

For Instructors
Monitor Dashboard: Track real-time student activity

Review Suspicion Scores: Identify high-risk behavior

Analyze Code: Detect AI or copied solutions

Export Reports: Generate detailed performance and behavior logs

🔧 Configuration
Settings Categories
General Settings
Auto-save intervals

Session management

Console behavior

Performance tuning

Editor Settings
Syntax highlighting themes

Line numbers and word wrap

Code completion and indentation

Appearance Settings
Color themes

Font family and size

UI scaling and layout preferences

Security Settings
Monitoring sensitivity

Alert levels

Privacy controls

Report generation rules

🎯 Anti-Cheating Features
Detection Methods
Focus Monitoring: Detects window/tab switching

Input Monitoring: Tracks copy/paste and shortcuts

Pattern Analysis: AI and plagiarism pattern detection

Behavior Analysis: Keystroke and typing rhythm tracking

System Monitoring: Detects screenshots and system commands

Suspicion Scoring
Dynamic score based on:

Focus loss frequency

Copy/paste operations

Window minimization

Tab switching

Typing irregularities

Debug session frequency

Code similarity and AI match

📁 Project Structure
![image alt](https://github.com/Tauhid-Topu-007/Secure-coding-assessment/blob/main/ide.png)
🏗️ Architecture
Key Components
Component	Description
UI Layer	JavaFX-based interface and visual elements
Monitoring Engine	Real-time tracking of user behavior
Code Analysis	Pattern recognition and similarity detection
Security Module	Anti-cheating and AI pattern validation
File Management	Handles file creation, saving, and loading

Data Models
FileEntry – represents code files

CheatingEvent – logs suspicious user activity

CodeAnalysisResult – stores analysis metrics

SampleTestCase – represents problem test cases

🔒 Security Considerations
Privacy
All monitoring is transparent to the user

Logs are stored locally during sessions

No external data transmission without consent

Integrity
Full-screen enforcement prevents external app access

Copy/paste restrictions maintain exam integrity

Real-time validation ensures code authenticity

📊 Performance
System Requirements
Type	Specification
Minimum	4GB RAM, Dual-core CPU
Recommended	8GB RAM, Quad-core CPU
Storage	500MB free space

Optimization
Efficient memory management

Lightweight background monitoring

Smooth JavaFX rendering

🐛 Troubleshooting
Common Issues
❌ JavaFX Not Found

javascript
Copy code
Error: JavaFX runtime components are missing
✅ Solution: Ensure JavaFX is installed and added to your module path.

⚠️ Fullscreen Issues

css
Copy code
Unable to enter fullscreen mode
✅ Solution: Check system permissions and display settings.

🐢 Performance Problems

nginx
Copy code
Slow response or lagging
✅ Solution: Close other apps or increase heap size:

diff
Copy code
-Xmx2g
Logs and Debugging
Real-time status messages appear in console

Activity logs stored in the instructor dashboard

Session reports available after submission

🤝 Contributing
We welcome contributions!
Please read our Contributing Guidelines before submitting PRs.

Development Workflow

Fork the repository

Create a feature branch

Implement and test your changes

Submit a pull request

📄 License
This project is licensed under the MIT License.
See the LICENSE file for full details.

🙏 Acknowledgments
JavaFX Team — for the powerful UI framework

Open Source Community — for libraries and inspiration

Educational Institutions — for feedback during testing

📞 Support
If you need help or have questions:

Open an issue on GitHub

Check the documentation

Contact the development team
