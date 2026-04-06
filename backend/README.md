# Student Task Tracker Backend

Node.js + Express API that uses Supabase PostgreSQL and aligns with the app data model (`users` and `tasks`).

## 1. Setup

```bash
cd backend
npm install
cp .env.example .env
```

Fill `.env`:

- `SUPABASE_URL`
- `SUPABASE_SERVICE_ROLE_KEY`
- `PORT` (optional, default `8080`)
- `CORS_ORIGIN` (optional, default `*`)

## 2. Database Setup (Supabase)

1. Open Supabase project SQL editor.
2. Run `sql/schema.sql`.

## 3. Run Locally

```bash
npm run dev
```

Health check: `GET /health`

## 4. API Endpoints

### Auth

- `POST /api/auth/register`
  - body: `{ "name": "...", "email": "...", "password": "..." }`
- `POST /api/auth/login`
  - body: `{ "email": "...", "password": "..." }`
- `POST /api/auth/forgot-password`
  - body: `{ "email": "...", "newPassword": "..." }`

### Tasks

- `GET /api/tasks?userId=1&status=Pending&search=math&due_date=6/4/2026`
- `GET /api/tasks/completed?userId=1`
- `POST /api/tasks`
  - body: `{ "userId": 1, "title": "...", "description": "...", "due_date": "6/4/2026" }`
- `PATCH /api/tasks/:id/status`
  - body: `{ "status": "Completed" }`
- `DELETE /api/tasks/:id`

## 5. Deploy on Render

1. Push this repo to GitHub.
2. Create a new **Web Service** on Render from your repo.
3. Root directory: `backend`
4. Build command: `npm install`
5. Start command: `npm start`
6. Add environment variables from `.env.example`.

Optional: use `render.yaml` at repo root for blueprint deploy.
