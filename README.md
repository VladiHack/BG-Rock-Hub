# 🎸 BG Rock Hub

Единният рок център на България — платформа за групи, фенове, клубове и събития.

## 💡 За проекта

BG Rock Hub е уеб платформа, която обединява цялата рок сцена в България на едно място.

- 🎵 **Открий банди** — известни и непознати, с профили, музика и история
- 🎟️ **Следи събития** — концерти, фестивали, jam сешъни из цялата страна
- 🏚️ **Оценявай клубове** — атмосфера, звук, цени, публика
- ⭐ **Ревюта от реални хора** — не алгоритми, а общност

## 🛠️ Технологии

### Backend
- Java 21 + Spring Boot 3
- Spring Security + JWT
- PostgreSQL + Spring Data JPA
- Redis (кеширане)
- Cloudinary (снимки)
- MapStruct + Bean Validation
- Swagger / OpenAPI 3

### Frontend
- React 18 + TypeScript
- Tailwind CSS
- React Query + Axios
- React Router v6

### DevOps
- Docker + Docker Compose
- GitHub Actions (CI/CD)
- Railway (backend) + Vercel (frontend)

## 👥 Роли в системата

| Роля  | Описание                                          |
|-------|---------------------------------------------------|
| FAN   | Разглежда, оценява, следва банди                  |
| BAND  | Управлява профил на банда, публикува events       |
| VENUE | Управлява профил на клуб/зала                     |
| ADMIN | Модерация и управление на платформата             |

## 🚀 Стартиране

### Изисквания
- Docker & Docker Compose
- Java 21 (за локална разработка)
- Node.js 20+ (за локална разработка)

### С Docker Compose

```bash
# Clone
git clone https://github.com/YOUR_USERNAME/bg-rock-hub.git
cd bg-rock-hub

# Копирай и попълни environment variables
cp .env.example .env

# Стартирай всичко
docker compose up -d
```

Приложението ще бъде достъпно на:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

### Локална разработка

```bash
# Backend
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend (в нов терминал)
cd frontend
npm install
npm run dev
```

## 📁 Структура на репото

```
bg-rock-hub/
├── backend/          # Spring Boot проект
│   └── src/main/java/bg/sofia/bgrockHub/
│       ├── config/       # Security, CORS, Redis, Swagger
│       ├── controller/   # REST Controllers
│       ├── dto/          # Request/Response DTOs
│       ├── entity/       # JPA Entities
│       ├── exception/    # Custom exceptions + GlobalExceptionHandler
│       ├── mapper/       # MapStruct mappers
│       ├── repository/   # Spring Data JPA Repositories
│       ├── security/     # JWT filter, UserDetails
│       └── service/      # Business logic
├── frontend/         # React + TypeScript проект
│   └── src/
│       ├── api/          # Axios instances & API calls
│       ├── components/   # Reusable UI components
│       ├── pages/        # Page components
│       ├── hooks/        # Custom React hooks
│       ├── store/        # Auth state (Context)
│       └── types/        # TypeScript types
├── docs/             # Документация, диаграми
├── docker-compose.yml
└── README.md
```

## 📋 API Endpoints (основни)

| Method | Endpoint                   | Описание                    | Роля         |
|--------|----------------------------|-----------------------------|--------------|
| POST   | /api/auth/register         | Регистрация                 | Public       |
| POST   | /api/auth/login            | Логин                       | Public       |
| POST   | /api/auth/refresh          | Обнови токен                | Authenticated|
| GET    | /api/bands                 | Всички банди                | Public       |
| POST   | /api/bands                 | Създай банда                | BAND         |
| GET    | /api/events                | Всички събития              | Public       |
| POST   | /api/events                | Създай събитие              | BAND/VENUE   |
| GET    | /api/venues                | Всички клубове              | Public       |
| POST   | /api/reviews               | Добави ревю                 | FAN/BAND     |
| POST   | /api/bands/{id}/follow     | Следвай банда               | Authenticated|

Пълна документация: http://localhost:8080/swagger-ui.html

## 🗺️ Roadmap

- [x] Планиране и архитектура
- [x] Auth модул (регистрация, логин, JWT)
- [x] Bands модул
- [x] Events модул
- [x] Venues модул
- [x] Reviews & Ratings
- [x] React Frontend
- [ ] Redis кеширане
- [ ] Известия
- [ ] Admin панел
- [ ] Deploy

## 📄 Лиценз

MIT License — виж [LICENSE](LICENSE)

---

Проектът е в активна разработка. ⚡ PR-ове и идеи са добре дошли!
