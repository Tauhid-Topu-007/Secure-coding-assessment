Secure Coding Assessment IDE
A comprehensive, anti-cheating integrated development environment designed for secure coding assessments and programming exams. This IDE provides real-time monitoring and cheating detection while offering a full-featured coding environment.

🚀 Features
🛡️ Anti-Cheating & Security
Real-time Monitoring: Continuous surveillance of user activity

Focus Detection: Monitors window focus loss and tab switching

Copy/Paste Monitoring: Tracks all copy, cut, and paste operations

Keystroke Analysis: Detects rapid typing patterns and suspicious input

Screenshot Prevention: Monitors screenshot attempts

Activity Logging: Comprehensive event logging with severity scoring

AI Pattern Detection: Analyzes code for AI-generated patterns

💻 Development Features
Multi-language Support: Java, Python, C++, JavaScript

Interactive Console: Real-time program execution with input/output handling

Code Completion: Intelligent code suggestions

Syntax Highlighting: Language-specific syntax coloring

Auto-Formatting: Code formatting and indentation

Debug Tools: Built-in debugging and analysis

File Management: Create, save, and manage multiple files

📊 Monitoring & Analytics
Instructor Dashboard: Real-time monitoring interface

Suspicion Scoring: Dynamic risk assessment algorithm

Code Analysis: Complexity, similarity, and pattern analysis

Session Statistics: Comprehensive activity metrics

Export Reports: Detailed session reports

⚙️ Customization
Theme Support: Multiple color themes (Dark, Light, High Contrast, etc.)

Font Customization: Adjustable font family and size

Auto-save: Configurable automatic saving

Settings Profiles: Save and load configuration profiles

🛠️ Installation
Prerequisites
Java 17 or higher

JavaFX 17 or higher

Build & Run
Clone the repository

bash
git clone https://github.com/Tauhid-Topu-007/Secure-coding-assessment.git
cd secure-coding-ide
Compile the project

bash
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml org/example/ide/AntiCheatingIDE.java
Run the application

bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml org.example.ide.AntiCheatingIDE
IDE Setup (IntelliJ/Eclipse)
Ensure JavaFX is properly configured in your IDE

Add JavaFX libraries to your project module path

Set VM options: --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml

📖 Usage
For Students
Start Session: Launch the IDE to begin monitored coding session

Create Files: Use the sidebar to create new files with appropriate extensions

Write Code: Use the code editor with syntax highlighting and auto-completion

Test Code: Use the interactive console to run and test programs

Submit: Use the submit button when finished

For Instructors
Monitor Dashboard: Watch real-time activity in the instructor panel

Review Suspicion Scores: Track risk levels and suspicious activities

Analyze Code: Review code patterns and AI detection results

Export Reports: Generate comprehensive session reports

🔧 Configuration
Settings Categories
General Settings
Auto-save intervals

Session management

Console preferences

Performance settings

Editor Settings
Syntax highlighting themes

Line numbers

Word wrap

Code completion

Auto-indentation

Appearance Settings
Color themes

Font family and size

UI scaling

Layout preferences

Security Settings
Monitoring sensitivity

Alert levels

Privacy controls

Report generation

🎯 Anti-Cheating Features
Detection Methods
Focus Monitoring: Detects window switching and minimization

Input Monitoring: Tracks copy/paste and keyboard shortcuts

Pattern Analysis: Identifies AI-generated code patterns

Behavior Analysis: Monitors typing speed and activity patterns

System Monitoring: Detects screenshot attempts and system commands

Suspicion Scoring
The system calculates a dynamic suspicion score based on:

Focus loss events

Copy/paste operations

Window minimizes

Tab switches

Rapid typing patterns

Debug session frequency

Code similarity scores

📁 Project Structure
![image alt]('https://github.com/Tauhid-Topu-007/Secure-coding-assessment/blob/main/ide.png')
🏗️ Architecture
Key Components
UI Layer: JavaFX-based interface with custom controls

Monitoring Engine: Real-time activity tracking and analysis

Code Analysis: Pattern recognition and similarity detection

Security Module: Cheating detection and prevention

File Management: Project and file handling system

Data Models
FileEntry: Represents code files and their content

CheatingEvent: Logs suspicious activities

CodeAnalysisResult: Stores code analysis metrics

SampleTestCase: Manages test cases and expected outputs

🔒 Security Considerations
Privacy
All monitoring is transparent to the user

Activity logs are stored locally during session

No external data transmission without consent

Integrity
Full-screen enforcement prevents external application access

Copy/paste restrictions maintain assessment integrity

Real-time validation of code authenticity

📊 Performance
System Requirements
Minimum: 4GB RAM, Dual-core processor

Recommended: 8GB RAM, Quad-core processor

Storage: 500MB free space

Optimization
Efficient memory management for large codebases

Background monitoring with minimal performance impact

Optimized UI rendering for smooth experience

🐛 Troubleshooting
Common Issues
JavaFX Not Found

text
Error: JavaFX runtime components are missing
Solution: Ensure JavaFX is properly installed and added to module path.

Fullscreen Issues

text
Unable to enter fullscreen mode
Solution: Check system permissions and display settings.

Performance Problems

text
Slow response or lagging
Solution: Close other applications, increase heap size with -Xmx2g

Logs and Debugging
Console output shows real-time application status

Activity logs are available in the instructor dashboard

Session reports provide detailed debugging information

🤝 Contributing
We welcome contributions! Please see our Contributing Guidelines for details.

Development Setup
Fork the repository

Create a feature branch

Make your changes

Add tests for new functionality

Submit a pull request

📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

🙏 Acknowledgments
JavaFX team for the robust UI framework

Open-source community for inspiration and libraries

Educational institutions for testing and feedback

📞 Support
For support and questions:

Create an issue on GitHub

Check the documentation

Contact the development team

