# Ecom Analytics Dashboard (Spring Boot + Angular + H2)

This is a full v1 scaffold based on the spec in `Dev Assignment_ Ecom Analytics Dashboard (v1).docx`.

**Stack**
- Backend: Java 17 + Spring Boot (Maven)
- DB: H2 (in-memory, PostgreSQL compatibility mode)
- Frontend: Angular (manual scaffold)

**Database Setup**
- 🚀 **Quick Setup**: Use `./setup_shopify_analytics.sh` to create dimension/fact tables and rollup operations
- 📖 **Full Guide**: See [SHOPIFY_ANALYTICS_SETUP.md](SHOPIFY_ANALYTICS_SETUP.md) for detailed documentation

**Backend**
1. `cd backend`
2. `mvn spring-boot:run`
3. H2 console: `http://localhost:8080/h2`
   - JDBC URL: `jdbc:h2:mem:analytics`

**Docker Compose (one command)**
- `docker compose up --build`
- Backend: `http://localhost:8080`
- Frontend: `http://localhost:4200`

**Demo Login (JWT)**
- Email: `demo@shop.com`
- Password: `Demo1234!`
- Token is required for all `/api/**` endpoints except `/api/auth/login`.

**User Registration (Admin only)**
- `POST /api/auth/register`
- Body: `{ "email": "viewer@shop.com", "password": "Pass1234!", "role": "VIEWER", "shopId": 1 }`

**Shop Onboarding (OAuth scaffold)**
1. `POST /api/shops/onboard`
   - Body: `{ "shopDomain": "your-shop.myshopify.com", "adminEmail": "owner@shop.com", "adminPassword": "Pass1234!" }`
   - Response returns `shopId` + `oauthUrl`
2. Open `oauthUrl` to approve Shopify
3. Shopify redirects to `/api/shops/onboard/callback?shopId=...&code=...&state=...`
4. Access token is stored on the shop record

**Multi-Shop Access**
- Users can be linked to multiple shops via `user_shops`.
- Dashboard endpoints accept optional `shopId` query param and validate access.

**Grant Access (Admin)**
- `POST /api/shops/grant-access`
- Body: `{ "userId": 7, "shopId": 3 }`

**Revoke Access (Admin)**
- `POST /api/shops/revoke-access`
- Body: `{ "userId": 7, "shopId": 3 }`

**List Users For Shop (Admin)**
- Offset pagination:
  - `GET /api/shops/users?shopId=3&limit=50&offset=0&sortBy=email&sortDir=asc`
  - Optional filter: `&email=gmail.com`
  - Response: `{ items: [...], total: 123, limit: 50, offset: 0, nextCursor: null }`
- Cursor pagination:
  - `GET /api/shops/users?shopId=3&limit=50&sortBy=email&sortDir=asc&cursor=...`
  - Response: `{ items: [...], total: null, limit: 50, offset: null, nextCursor: \"...\" }`

**List Shops For User (Admin)**
- Offset pagination:
  - `GET /api/shops/shops?userId=7&limit=50&offset=0&sortBy=domain&sortDir=asc`
  - Optional filter: `&domain=myshop`
  - Response: `{ items: [...], total: 10, limit: 50, offset: 0, nextCursor: null }`
- Cursor pagination:
  - `GET /api/shops/shops?userId=7&limit=50&sortBy=domain&sortDir=asc&cursor=...`
  - Response: `{ items: [...], total: null, limit: 50, offset: null, nextCursor: \"...\" }`

**Token Encryption**
- Shopify access tokens are encrypted at rest using AES-GCM.
- Configure `security.encryption.key-base64` in `backend/src/main/resources/application.yml`.

**Frontend**
1. `cd frontend`
2. `npm install`
3. `npm run start`
4. Open `http://localhost:4200`

**Frontend Safe Build**
- `npm run build:safe` (skips build if `node_modules` is missing)

**Production Profile**
- Use `-Dspring.profiles.active=prod` or set `SPRING_PROFILES_ACTIVE=prod`
- Config is in `backend/src/main/resources/application-prod.yml`
- Flyway runs migrations from `backend/src/main/resources/db/migration`
- Postgres uses JSONB for raw payload tables in `V1__init.sql`

**API Endpoints** (JWT required)
- `GET /api/dashboard/kpis?shopId=1&from=2026-01-01&to=2026-01-03`
- `GET /api/dashboard/growth?shopId=1&from=2026-01-01&to=2026-01-03`
- `GET /api/dashboard/ltv?shopId=1`
- `GET /api/dashboard/cohorts?shopId=1&from=2025-11-01&to=2025-12-01`
- `GET /api/dashboard/channels?shopId=1&from=2026-01-01&to=2026-01-03`

**Shopify Bulk Backfill (scaffold)**
- Set `shopify.base-url` and `shopify.access-token` in `backend/src/main/resources/application.yml`
- Orders: `GET /api/shopify/backfill/orders?from=2025-01-01&to=2025-01-31`
- Customers: `GET /api/shopify/backfill/customers?from=2025-01-01&to=2025-01-31`

**Notes**
- All metrics follow the definitions in the doc.
- No live Shopify calls on dashboard endpoints.
- Seed data is included in `backend/src/main/resources/data.sql`.
