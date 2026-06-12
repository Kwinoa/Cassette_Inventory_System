# Cassette Media Archive & Player
By Kalina Tran

A niche management platform for cassette tape collectors that bridges the gap between physical media and digital streaming. This project explores the integration of multiple third-party APIs (Discogs, Spotify, and OpenAI) within a modern full-stack architecture to provide a seamless cataloging and listening experience.

## Key Features
- **AI-Powered Recommendations**: "Smart Search" utilizes OpenAI to analyze your collection's genres and styles, suggesting new music that fits your specific taste profile.
- **Vintage Spotify Player**: A custom-styled Spotify player that mimics a 1980s cassette deck. It allows full playback control of albums in your collection directly through the browser.
- **Discogs Cataloging**: Instant search and import functionality via Discogs, allowing users to add albums with full tracklists and official cover art in seconds.
- **Dynamic Inventory Management**: Real-time filtering and searching of your personal collection. Supports custom image uploads for rare or bootleg tapes not found in global databases.
- **Secure User Sessions**: Full session-based authentication and user-specific data isolation.

## My Role
As the Full-Stack Developer, I was responsible for the end-to-end design and implementation of the system.
- Developed a RESTful Spring Boot backend with JPA/Hibernate for persistent storage.
- Built a dynamic, responsive React 19 frontend using Vite and CSS Modules.
- Integrated the Discogs API for automated album metadata and cover art retrieval.
- Implemented a "Smart Search" recommendation engine using the OpenAI API.
- Designed and coded a custom, vintage-themed Spotify web player using the Spotify Playback SDK.
- Configured secure session-based authentication and user-specific data isolation.

## Technical Stack
- **Backend**: Spring Boot 3, Spring Security, JPA/Hibernate, PostgreSQL
- **Frontend**: React 19, Vite, Axios, React Router, CSS Modules
- **APIs**: Discogs, Spotify Web Playback SDK, OpenAI

## Architecture
The system utilizes a decoupled architecture. The Spring Boot backend serves as a centralized API gateway and business logic layer, handling interactions with the PostgreSQL database and external services. The React frontend consumes these endpoints, providing a smooth, single-page application (SPA) experience. Communication is facilitated via Axios with reactive data handling through React Context.

## API Integration & Design
One of the project's highlights is the "Smart Search" feature. The backend constructs prompts based on the user's existing collection and sends them to OpenAI, which returns intelligent recommendations. Simultaneously, the Discogs API is used to fetch high-quality metadata, ensuring the user's digital inventory matches their physical shelf accurately. The Spotify SDK integration requires complex OAuth flows, which are managed through a dedicated `SpotifyService` on the backend.

## Challenges & Solutions
A major challenge was synchronizing the Spotify Web Playback SDK with the React state. I solved this by implementing a global `Player` Context that monitors playback status (play/pause/track info) across the entire application. Another challenge was handling hardcoded tokens and URIs; I transitioned sensitive configurations to environment variables and `.properties` files to improve security and maintainability.

## How to Run the Application

### 1. Backend Setup
Navigate to the backend directory and run the Spring Boot application using the Maven wrapper.

```bash
cd cassette-inventory-backend
.\mvnw.cmd spring-boot:run
```

The backend will start on `http://127.0.0.1:8080`.

### 2. Frontend Setup
In a separate terminal, navigate to the frontend directory, install dependencies, and start the development server.

```bash
cd cassette-inventory-frontend
npm install
npm run dev
```

The frontend will be available at `http://127.0.0.1:3000`.

## Environment Variables
The application requires environment variables for API keys and configuration.

### Backend (`cassette-inventory-backend/.env`)
Create a `.env` file in the `cassette-inventory-backend` root directory with the following variables:

```
FRONTEND_URL="http://127.0.0.1:3000"
DATASOURCE_URL="jdbc:postgresql://localhost:5432/your_db_name"
DATASOURCE_USER="your_db_user"
DATASOURCE_PASSWORD="your_db_password"
OPENAI_KEY="your_openai_api_key"
SPOTIFY_CLIENT_ID="your_spotify_client_id"
SPOTIFY_CLIENT_SECRET="your_spotify_client_secret"
```

### Frontend (`cassette-inventory-frontend/.env`)
The frontend uses a `.env` file that is already present in the directory.

```
VITE_CASSETTE_API_URL=http://127.0.0.1:8080
VITE_FRONTEND_URL=http://127.0.0.1:3000
```

## Results & Learnings
The final application is a specialized tool that combines the nostalgia of physical collecting with modern AI and streaming technologies. This project significantly deepened my understanding of OAuth2 flows and the complexities of managing state in real-time media applications.

- Mastered multi-API orchestration, ensuring data consistency across Discogs, Spotify, and local storage.
- Gained expertise in React 19’s new features and advanced Context API patterns for global state.
- Improved backend security through Spring Security and session-based authentication.

