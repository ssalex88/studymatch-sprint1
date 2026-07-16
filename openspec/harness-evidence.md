# Harness Evidence — StudyMatch Sprint 1

This file records lightweight evidence that Sprint 1 work was guided through the Pi / el Gentleman harness and the SDD/OpenSpec artifacts.

## Purpose

Provide a repository-local explanation for the professor showing that implementation was not only generated from chat, but controlled through:

- SDD/OpenSpec documentation;
- scoped implementation slices;
- verification attempts;
- review and lifecycle gates;
- recorded decisions and risks.

## Current SDD source

| Artifact | Purpose |
| --- | --- |
| `openspec/constitution.md` | Main Sprint 1 SDD for registration, login, profile, and admin users. |
| `openspec/harness-evidence.md` | Evidence of harness-guided construction and implementation progress. |

## Harness-controlled delivery evidence

| Step | Evidence |
| --- | --- |
| SDD update committed | Commit `dbc45b4` with message `docs: update Sprint 1 SDD`. |
| SDD update pushed | Pushed to `origin/main`. |
| Review gate | Gentle AI review transaction approved the SDD documentation candidate before commit/push. |
| Implementation mode | User selected auto SDD flow with OpenSpec artifacts and review slices under approximately 400 changed lines. |

## First implementation slice

### Scope

Make HU1 and HU2 reachable through backend HTTP endpoints expected by the frontend.

### Files changed

| File | Purpose |
| --- | --- |
| `studymatch-backend/src/main/java/net/studymatch/api/App.java` | Registers `/api/auth` context. |
| `studymatch-backend/src/main/java/net/studymatch/api/controller/AuthController.java` | Adds registration and login HTTP controller. |

### Implemented behavior

- `POST /api/auth/registrar` receives registration JSON and calls `UsuarioService.registrarUsuario`.
- `POST /api/auth/login` receives login JSON and calls `UsuarioService.iniciarSesion`.
- CORS preflight is handled through the existing `CorsHelper`.
- Registration and login validate required fields.
- Email validation uses generic email format only because the institutional domain is not specified in the SDD.
- Controller does not access SQL directly; it keeps the existing controller → service → repository architecture.

## Verification evidence

| Check | Result |
| --- | --- |
| Java LSP diagnostics for `App.java` and `AuthController.java` | No error diagnostics; only informational spelling notices in Spanish comments. |
| `git diff --check` | Passed for tracked changes. |
| Java/Maven availability | Verified with Java 21.0.11 and Maven 3.9.12 in WSL. |
| `cd studymatch-backend && mvn clean package` | Passed; backend jar with dependencies was built successfully. |

## Residual risks

- End-to-end registration/login must be tested with MySQL running and `schema.sql` applied.
- Institutional email domain validation remains a product decision.
- Authentication now returns safe user data plus an in-memory `sessionToken`; protected user routes require `Authorization: Bearer <token>`.
- HU3 profile refresh, HU4 admin role controls, and backend authorization enforcement are implemented in later slices below.

## Second implementation slice

### Scope

Implement the next HU4 frontend slice for administrator role management without changing backend authorization/security rules.

### Files changed

| File | Purpose |
| --- | --- |
| `studymatch-frontend/src/features/admin/AdminDashboard.jsx` | Adds per-user role controls, role-update loading state, success/error feedback, and local list updates after a successful role change. |
| `studymatch-frontend/src/core/services/userService.js` | Adds `updateUserRole(idUsuario, rol)` for `PATCH /api/usuarios/{id}/rol`. |
| `openspec/harness-evidence.md` | Records implementation evidence, verification, and residual risks for this slice. |

### Implemented behavior

- Admin users can change a listed user's role from the dashboard through the existing backend endpoint.
- Available frontend role labels are aligned with current stored labels and Sprint 1 SDD roles: `Administrador`, `Profesor`, `Organizador`, and `Estudiante`.
- The dashboard clears stale messages before requests, disables the active role selector while the update is in flight, shows success/error feedback, and updates the in-memory user list after success.
- If the admin changes their own role, `currentUser` in `localStorage` is updated so the existing admin gate reflects the new role.
- Invalid `currentUser` JSON is handled by clearing the stale local session instead of throwing during dashboard render.

### Verification evidence for this slice

| Check | Result |
| --- | --- |
| JavaScript/TypeScript diagnostics from edit tool for changed frontend files | Passed after edits; no reported issues. |
| `cd studymatch-frontend && npm ci` | Passed; frontend dependencies installed from lockfile. |
| `cd studymatch-frontend && npm run build` | Passed; Vite production build completed. |
| `cd studymatch-frontend && npm run lint` | Passed; Oxlint completed without reported findings. |
| `git diff --check` | Passed for tracked changes. |

### Residual risks for this slice

- Backend authorization enforcement for HU4 remains intentionally deferred; the frontend still relies on the existing `currentUser.rol === "Administrador"` gate.
- End-to-end role changes still need validation with the Java backend and database running.
- HU3 profile refresh was intentionally deferred from this slice and handled in the third implementation slice.

## Third implementation slice

### Scope

Add the narrow HU3 profile refresh path so the profile view can load fresh user data from the backend instead of relying only on stale `localStorage` after login.

### Files changed

| File | Purpose |
| --- | --- |
| `studymatch-backend/src/main/java/net/studymatch/api/repository/UsuarioRepository.java` | Adds `buscarPorId` with a safe user projection that excludes `contrasena`. |
| `studymatch-backend/src/main/java/net/studymatch/api/service/UsuarioService.java` | Exposes `obtenerUsuarioPorId` as the service bridge for profile refresh. |
| `studymatch-backend/src/main/java/net/studymatch/api/controller/UsuarioController.java` | Adds `GET /api/usuarios/{id}` without SQL in the controller. |
| `studymatch-frontend/src/core/services/userService.js` | Adds `getUserById(idUsuario)`. |
| `studymatch-frontend/src/features/profile/Perfil.jsx` | Refreshes the current profile on load, updates component state, and persists the fresh user in `localStorage`. |
| `openspec/harness-evidence.md` | Records implementation evidence, verification, and residual risks for this slice. |

### Implemented behavior

- The backend now supports `GET /api/usuarios/{id}` through the existing controller -> service -> repository layering.
- The repository query selects only safe user columns and does not read or serialize `contrasena`.
- `Perfil` still initializes from `localStorage` for continuity, then refreshes from the backend when `currentUser.idUsuario` is available.
- On a successful refresh, the profile component state, form state, and `currentUser` in `localStorage` are updated with the backend copy.
- Existing `PUT /api/usuarios/{id}` edit/update behavior remains unchanged.

### Verification evidence for this slice

| Check | Result |
| --- | --- |
| Java diagnostics from edit tool for changed backend files | Passed after edits. |
| JavaScript/TypeScript diagnostics from edit tool for changed frontend files | Passed after edits. |
| `git diff --check` | Passed for tracked changes. |
| `cd studymatch-frontend && npm ci` | Passed; frontend dependencies installed from lockfile. |
| `cd studymatch-frontend && npm run build` | Passed; Vite production build completed. |
| `cd studymatch-frontend && npm run lint` | Passed; Oxlint completed without reported findings. |
| `cd studymatch-backend && mvn clean package` | Passed with Java 21 and Maven 3.9.12. |

### Residual risks for this slice

- This slice originally did not add auth tokens or backend authorization; that limitation is superseded by the fourth authentication hardening slice below.

## MySQL runtime smoke test

### Environment

| Item | Evidence |
| --- | --- |
| MySQL server | MySQL 8.4.10 running through `mysql.service`. |
| Database | `studymatch_db`. |
| Application user | `studymatch_user`. |
| Schema load | `mysql -u studymatch_user -p123456 studymatch_db < studymatch-database/schema.sql` passed. |
| Tables verified | `usuarios`, `horario_disponibilidad`, `circulo_estudio`, `miembrecia_circulo`. |
| Backend runtime | `java -jar target/studymatch-backend-jar-with-dependencies.jar` started on port 8080. |

### API checks executed

| Check | Result |
| --- | --- |
| `POST /api/auth/registrar` | Passed with HTTP 201 and returned the created user without password. |
| `POST /api/auth/login` | Passed with HTTP 200 and returned authenticated user data without password. |
| `PUT /api/usuarios/{id}` | Passed with HTTP 200 and updated academic profile fields. |
| `GET /api/usuarios/{id}` | Passed with HTTP 200 and returned the refreshed profile without password. |
| `PATCH /api/usuarios/{id}/rol` | Passed with HTTP 200 and updated the user role to `Administrador`. |
| `GET /api/usuarios` | Passed with HTTP 200 and showed the updated user in the list. |

### Remaining runtime risks

- The smoke test validates backend/API behavior; full browser interaction should still be demonstrated through the React UI for presentation.
- Backend routes remained unauthenticated at API level at the time of this smoke test; this risk is superseded by the authentication hardening slice below.

## Fourth implementation slice — Sprint 1 authentication hardening

### Scope

Make the Sprint 1 DoD claim "access is protected by authentication" defensible by adding backend-enforced session tokens to the existing registration/login/user flows without adding JWT, a database session table, or production-grade auth infrastructure.

### Files changed

| File | Purpose |
| --- | --- |
| `studymatch-backend/src/main/java/net/studymatch/api/service/SessionService.java` | Adds a Sprint 1 in-memory session-token service using Java `SecureRandom`, `Authorization: Bearer <token>` parsing, and current-user resolution from the database. |
| `studymatch-backend/src/main/java/net/studymatch/api/service/UsuarioService.java` | Creates a session token on successful registration and login. |
| `studymatch-backend/src/main/java/net/studymatch/api/dto/AuthResponseDTO.java` | Adds `sessionToken` to auth responses. |
| `studymatch-backend/src/main/java/net/studymatch/api/controller/UsuarioController.java` | Enforces backend authorization for user routes: admin-only list/role updates, and same-user-or-admin profile reads/updates. |
| `studymatch-backend/src/main/java/net/studymatch/api/config/CorsHelper.java` | Allows `PATCH` and `Authorization` in CORS preflight for the protected role update flow. |
| `studymatch-frontend/src/core/services/userService.js` | Reads the stored session token and sends `Authorization: Bearer <token>` on user API requests. |
| `studymatch-frontend/src/features/profile/Perfil.jsx` | Uses `userService` for protected profile requests and preserves the session token when refreshing profile data. |
| `studymatch-frontend/src/features/auth/Login.jsx` | Persists `sessionToken` from login responses in localStorage for demo session continuity. |
| `studymatch-frontend/src/features/auth/Registro.jsx` | Persists `sessionToken` from registration responses and redirects the newly registered user to their profile. |
| `studymatch-frontend/src/components/Layout.jsx` | Clears both `currentUser` and `sessionToken` on logout. |
| `studymatch-frontend/src/core/services/authService.js` | Documents that auth responses now include `sessionToken`. |
| `openspec/harness-evidence.md` | Records this hardening slice, verification, and remaining limitations. |

### Implemented behavior

- `POST /api/auth/registrar` and `POST /api/auth/login` now return the safe user payload plus `sessionToken`.
- User routes no longer trust `localStorage` or frontend role checks as the sole authorization control.
- Missing or invalid bearer tokens return HTTP 401.
- Authenticated non-admin users receive HTTP 403 when attempting admin-only operations.
- Admin status is resolved from the current database row on each protected request, so role changes are reflected by active in-memory sessions.
- Frontend user API requests send `Authorization: Bearer <sessionToken>` from localStorage; localStorage remains only demo session storage, not the authority for authorization.

### Verification evidence for this slice

| Check | Result |
| --- | --- |
| `cd studymatch-backend && mvn clean package` | Passed; backend compiled and jar with dependencies was built. Maven generated `studymatch-backend/target/`, which should not be included in a commit. |
| `cd studymatch-frontend && npm run build && npm run lint` | Passed; Vite build completed and Oxlint reported no findings. |
| `git diff --check` | Passed. |
| MySQL connectivity | Passed with `studymatch_user` against `studymatch_db`. |
| Runtime API smoke test | Passed against the rebuilt backend jar: registration/login return tokens; `GET /api/usuarios` without token returns 401; student token on admin list returns 403; same-user profile access returns 200; admin list and role patch return 200. |

### Remaining limitations for this slice

- Sessions are in-memory only and disappear when the Java process restarts; this is intentional for the academic Sprint 1 demo and not production-ready.
- There is no backend logout/session revocation endpoint; frontend logout clears localStorage only.
- Tokens are stored in localStorage for demo continuity, which is acceptable for this Sprint 1 scope but not hardened against XSS.
- Password hashing remains the existing SHA-256 implementation; stronger password hashing is outside this focused slice.
- The smoke test used a direct database role update to promote a newly created smoke user to `Administrador` before exercising admin-only endpoints.

## Recommended professor-facing explanation

Saving the chat is useful as supplementary evidence, but it is not enough by itself. The stronger evidence is this repository-local trail:

1. the SDD in `openspec/constitution.md`;
2. this harness evidence file;
3. commit history;
4. verification commands/results;
5. screenshots or terminal captures from local execution.

The chat/session can be exported or shown as supporting context, but the project should rely on files and commits as the main academic evidence.
