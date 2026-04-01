# 🎸 BG Rock Hub — Цялостен план за проекта

> Единният рок център на България — платформа за групи, фенове, клубове и събития.

---

## 📌 Визия

**BG Rock Hub** е уеб платформа, която обединява цялата рок сцена в България на едно място. Тя дава видимост на малко известни банди, позволява на феновете да оценяват концерти и клубове, и служи като централен хъб за всичко свързано с рок музиката в страната.

---

## 🎯 Функционални изисквания

### 1. Управление на потребители (Auth & Profiles)
- Регистрация и логин с имейл + парола
- Верификация на имейл при регистрация
- OAuth логин (Google)
- Роли: **FAN**, **BAND**, **VENUE**, **ADMIN**
- Профилна страница с аватар, биография, активност
- Смяна на парола и изтриване на акаунт

### 2. Банди (Bands)
- Създаване и редактиране на профил на банда
- Полета: име, жанр, описание, членове, град, основана година
- Качване на снимки (галерия)
- Линкове към Spotify, YouTube, Facebook, Instagram
- Вграден музикален плейър (embed от YouTube/Spotify)
- Фенове могат да следват банда и да я оценяват (1–5 звезди)
- Страница "Непознати банди" — spotlight за малко известни изпълнители
- Търсене и филтриране по жанр, град, рейтинг

### 3. Събития (Events)
- Публикуване на концерти, фестивали, jam сешъни
- Полета: заглавие, описание, дата, място, банди, цена на билет, снимка
- Карта с местоположение (Google Maps embed)
- "Ще присъствам" бутон — показва брой заинтересовани
- Ревюта и оценки **след** провеждане на събитието
- Галерия снимки от събитието (качвана от потребители)
- Филтриране: предстоящи / минали, по град, по жанр

### 4. Места / Клубове (Venues)
- Профил на рок клуб, бар, зала
- Полета: ime, адрес, описание, снимки, капацитет, контакти
- Оценки по критерии: атмосфера, звук, цени, публика
- Текстови ревюта от потребители
- Списък с предстоящи и минали събития на съответното място
- Верифицирани Venue акаунти (значка)

### 5. Новини (News)
- Раздел с новини от рок сцената (публикувани от ADMIN или BAND)
- Коментари под новините
- Таг система (жанр, банда, събитие)

### 6. Оценки и ревюта (Reviews)
- Оценяване на: банди, събития, клубове
- Скала 1–5 звезди + текстово ревю
- Един потребител = едно ревю на обект
- Редактиране и изтриване на собствено ревю
- Модерация от ADMIN

### 7. Социални функции
- Следване на банди и потребители
- Харесване на ревюта и новини
- Лента с активност (feed)
- Известия (нов концерт на следвана банда, отговор на коментар)

### 8. Админ панел
- Управление на потребители (бан, промяна на роля)
- Модерация на ревюта и коментари
- Верификация на банди и venues
- Статистики: потребители, events, ревюта

---

## 🔒 Нефункционални изисквания

### Сигурност
- Пароли, хеширани с BCrypt
- JWT токени за автентикация (access + refresh token)
- Role-based access control на всеки endpoint
- Защита от SQL injection (JPA Parameterized Queries)
- CSRF защита
- Rate limiting на API endpoints
- Валидация на всички входни данни (Bean Validation)

### Производителност
- Пагинация на всички списъци
- Lazy loading на снимки
- Кеширане с Redis (популярни банди, предстоящи events)
- Database индекси на често търсени полета

### Надеждност
- Глобална обработка на грешки (GlobalExceptionHandler)
- Смислени HTTP статус кодове и error response body
- Логване с SLF4J + Logback

### Поддръжка
- REST API с ясна структура
- Swagger/OpenAPI документация
- Разделяне на слоевете (Controller → Service → Repository)
- Тестове: Unit (JUnit 5 + Mockito) и Integration (Testcontainers)

---

## 🏗️ Архитектура

```
┌─────────────────────────────────────────────────────────┐
│                     FRONTEND                            │
│              React + TypeScript + Tailwind              │
└─────────────────────┬───────────────────────────────────┘
                      │ HTTP / REST API
┌─────────────────────▼───────────────────────────────────┐
│                   BACKEND                               │
│                 Spring Boot 3.x                         │
│                                                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐             │
│  │Auth /    │  │Bands /   │  │Events /  │             │
│  │Users     │  │Reviews   │  │Venues    │             │
│  └──────────┘  └──────────┘  └──────────┘             │
│                                                         │
│  ┌──────────────────────────────────┐                  │
│  │     Spring Security + JWT        │                  │
│  └──────────────────────────────────┘                  │
└───────┬─────────────────────┬───────────────────────────┘
        │                     │
┌───────▼──────┐    ┌─────────▼────────┐
│  PostgreSQL  │    │     Redis        │
│  (main DB)   │    │   (cache)        │
└──────────────┘    └──────────────────┘
        
        ┌──────────────────┐
        │   Cloudinary     │
        │  (image upload)  │
        └──────────────────┘
```

### Структура на пакетите (Backend)
```
bg.sofia.bgroockhub/
├── config/          # Security, CORS, Redis config
├── controller/      # REST Controllers
├── dto/             # Request/Response DTOs
├── entity/          # JPA Entities
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── mapper/          # Entity ↔ DTO mappers (MapStruct)
├── repository/      # Spring Data JPA Repositories
├── security/        # JWT filter, UserDetails
└── service/         # Business logic
```

---

## 🛠️ Технологии

### Backend
| Технология | Употреба |
|---|---|
| Java 21 | Основен език |
| Spring Boot 3.x | Framework |
| Spring Security | Auth + Authorization |
| Spring Data JPA | Database слой |
| JWT (jjwt) | Токен-базирана автентикация |
| PostgreSQL | Основна база данни |
| Redis | Кеширане |
| Cloudinary | Качване и съхранение на снимки |
| MapStruct | Entity ↔ DTO mapping |
| Bean Validation | Валидация на входни данни |
| SLF4J + Logback | Логване |
| Swagger/OpenAPI | API документация |

### Frontend
| Технология | Употреба |
|---|---|
| React 18 | UI Framework |
| TypeScript | Типизиран JavaScript |
| Tailwind CSS | Стилове |
| React Router | Навигация |
| Axios | HTTP заявки |
| React Query | Server state management |

### DevOps & Тестване
| Технология | Употреба |
|---|---|
| Maven | Build система |
| JUnit 5 + Mockito | Unit тестове |
| Testcontainers | Integration тестове |
| GitHub Actions | CI/CD |
| Docker + Docker Compose | Локална среда |
| Railway / Render | Deployment (backend) |
| Vercel | Deployment (frontend) |

---

## 🗄️ Database Schema (основни таблици)

```
users
├── id, email, password, role, username
├── avatar_url, bio, city, created_at
└── is_verified, is_active

bands
├── id, name, genre, description, city
├── founded_year, avatar_url
├── spotify_url, youtube_url, facebook_url
├── owner_id (FK → users), is_verified
└── avg_rating, total_ratings

venues
├── id, name, address, city, description
├── capacity, phone, website, cover_img_url
├── owner_id (FK → users), is_verified
└── avg_rating, total_ratings

events
├── id, title, description, date, city
├── ticket_price, cover_img_url, status
├── venue_id (FK → venues)
└── organizer_id (FK → users)

event_bands (many-to-many: events ↔ bands)

reviews
├── id, rating (1-5), content, created_at
├── reviewer_id (FK → users)
├── target_type (BAND / EVENT / VENUE)
└── target_id

follows (users → bands)
├── follower_id (FK → users)
└── band_id (FK → bands)
```

---

## 📋 План за действие (Roadmap)

### Фаза 1 — Основа (2–3 седмици)
- [ ] Инициализиране на Spring Boot проект (Maven)
- [ ] Настройка на PostgreSQL + JPA entities
- [ ] Имплементация на Auth: регистрация, логин, JWT
- [ ] Роли и Spring Security конфигурация
- [ ] Базов REST API за потребители

### Фаза 2 — Основни модули (3–4 седмици)
- [ ] CRUD за Bands (с качване на снимки в Cloudinary)
- [ ] CRUD за Events
- [ ] CRUD за Venues
- [ ] Система за ревюта и оценки
- [ ] Следване на банди

### Фаза 3 — Разширени функции (2–3 седмици)
- [ ] Новини модул
- [ ] Redis кеширане
- [ ] Известия
- [ ] Страница "Непознати банди" (spotlight)
- [ ] Админ панел

### Фаза 4 — Frontend (3–4 седмици)
- [ ] React проект + routing
- [ ] Auth pages (логин, регистрация)
- [ ] Страница за банди, events, venues
- [ ] Профилни страници
- [ ] Responsive дизайн

### Фаза 5 — Quality & Deploy (1–2 седмици)
- [ ] Unit и integration тестове
- [ ] Swagger документация
- [ ] Docker Compose за локална среда
- [ ] Deployment на Railway + Vercel
- [ ] GitHub Actions CI/CD pipeline

---

## 📁 Структура на репото

```
bg-rock-hub/
├── backend/          # Spring Boot проект
├── frontend/         # React проект
├── docs/             # Документация, диаграми
├── docker-compose.yml
└── README.md
```

---

*Проектът е отворен за принос от общността — PR-ове и идеи са добре дошли!*
