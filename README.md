Here's the complete README content in that exact format — copy and paste it:

---

# 📚 Library Management System

A Java Swing desktop application to manage your library books easily. Built as part of the **SoftGrowTech** internship program.

---

## ✨ Features

- Add new books with Title, Author, Publish Year and ISBN
- View all books in a clean, organized table
- Search books by Title, Author or Publish Year
- Delete a specific book by title
- Clear all input fields at once
- Filter and sort books by clicking column headers
- Live statistics panel that updates after every action
- Beautiful modern UI with color coded buttons and status bar

---

## 🚀 How to Run

### Requirements

- Java JDK 17 or above installed
- Download from https://adoptium.net

Verify your installation:
```bash
java -version
javac -version
```

### Steps

1. Download or clone this repository

```bash
git clone https://github.com/chiragsinghcs223/library-management-system
cd library-management-system
```

2. Compile all files:

```bash
javac *.java
```

3. Run the application:

```bash
java LibraryGUI
```

---

## 🛠️ Tech Used

| Technology | Purpose |
|---|---|
| Java | Core programming language |
| Java Swing | GUI framework for the desktop interface |
| Java OOP | Encapsulation, Abstraction, Inheritance, Polymorphism |
| File I/O | Auto save and load books via BufferedReader/BufferedWriter |

---

## 📁 Project Structure

```
LibrarySystem/
├── Book.java                   # Data model — private fields, getters, setters
├── LibraryOperations.java      # Interface — abstract CRUD contract
├── BookNotFoundException.java  # Custom checked exception
├── Library.java                # Business logic — CRUD, file I/O, stats
├── LibraryGUI.java             # Swing GUI — main application window
├── library_data.txt            # Auto generated file to store book data
└── README.md                   # Project documentation
```

---

## 👨‍💻 Author

**Chirag Singh**
GitHub: [@chiragsinghcs223](https://github.com/chiragsinghcs223)

---

## 🏢 Internship

This project was built during an internship at **SoftGrowTech** as part of a hands-on Java development program focused on building real-world desktop applications using Java Swing and core OOP concepts.

---

*Built with Java Swing · Core OOP · SoftGrowTech Internship Project*
