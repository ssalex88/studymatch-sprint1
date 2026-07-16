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
- Authentication currently returns user data but does not implement token/session security.
- HU3 and HU4 still need follow-up implementation slices for stronger profile refresh, admin role controls, and backend authorization enforcement.

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

- This slice intentionally does not add auth tokens or backend authorization; `GET /api/usuarios/{id}` is currently unauthenticated like the existing user routes.

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
- Backend routes remain unauthenticated at API level; Sprint 1 currently relies on frontend/localStorage checks for admin UI gating.

## Recommended professor-facing explanation

Saving the chat is useful as supplementary evidence, but it is not enough by itself. The stronger evidence is this repository-local trail:

1. the SDD in `openspec/constitution.md`;
2. this harness evidence file;
3. commit history;
4. verification commands/results;
5. screenshots or terminal captures from local execution.

The chat/session can be exported or shown as supporting context, but the project should rely on files and commits as the main academic evidence.
