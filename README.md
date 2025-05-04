# OCR-Study-Assistant

# OCR AI Study Assistant App - Project Outline

## Project Description
This AI OCR Study Assistant is a mobile application designed to help students digitize handwritten notes, summarize key points using AI, and generate flashcards for studying.

## Problem Addressing
Students often face challenges organizing and summarizing handwritten notes, making study time-consuming. This app automates summarizing and flashcard creation, improving study efficiency while maintaining academic integrity.

## Platforms
- Android (primary)
- Potential for cross-platform (Flutter)
- Cloud integration if free options are available.

## Front/Back End Support
- Front-end: Android Studio (Kotlin), potential Flutter usage.
- Back-end: Python (Flask), Node.js.
- OCR Tools: Google Cloud Vision, Tesseract OCR.
- AI Tools: OpenAI API, Ollama, or DeepSeek (based on free tier availability).

## Functionality
- OCR Scanning for handwritten text
- AI-powered summarization
- AI-generated flashcards
- Local storage of notes and flashcards

## Design (Wireframes)
- Home Screen (scan notes, view past scans)
- OCR Camera interface
- Note editor and AI summarization interface
- Interactive Flashcard screen
- Past notes access with flashcards and summaries

---------------------

## Version Changelog

### Week 3 Update
- **Previous Tasks Completed:**
  - Created GitHub repository and initialized README.md with full project outline.
  - Established basic Android Studio project structure using Kotlin.
  - Integrated initial dependencies, including Google's ML Kit for OCR functionality.

### Week 4 Update (Current Progress)
- **Tasks Accomplished:**
  - Implemented camera functionality allowing users to select images from their device.
  - Successfully integrated Google's ML Kit OCR to accurately scan handwritten and printed text into digital format.
  - Partially implemented OpenAI API integration; able to make basic summarization requests.
  - Created initial layouts for the home screen, OCR results screen, and summarized text view.
  - Conducted basic testing on OCR accuracy and identified areas for improvement in UI responsiveness and error handling.

- **Changes Made:**
  - Switched from original plan of direct camera capture to image gallery selection first for quicker development and testing.
  - Temporarily halted integration of cloud-based storage (Firebase) to focus on perfecting OCR and AI integration first.

### Future Updates (Week 5-7)
- Complete full integration of OpenAI API, refine the summarization and flashcard generation functionality.
- Implement local storage (SQLite) for notes and summaries to ensure offline functionality.
- Optimize OCR processing speed and accuracy, including image pre-processing enhancements.
- Finalize user interface design, improve visual appeal, and overall user experience.
- Consider reintegration of Firebase or other free cloud solutions for optional backups and synchronization features.
