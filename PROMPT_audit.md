# EasyCloud PHP → Java Migration Audit

## Objective
Audit the PHP-to-Java/Vue migration for completeness. Compare the original PHP code against the new Java backend and Vue frontend. Find and fix any missing features, endpoints, logic, or parity gaps.

## Scope
- **PHP source (reference):** `admin/`, `api/`, `includes/`, `template/`, root `*.php` files
- **Java backend (target):** `backend/src/main/java/com/easycloud/`
- **Vue frontend (target):** `frontend/src/`
- **Database schema:** `install/simple.sql` (PHP) vs `backend/src/main/resources/schema.sql` (Java)

## Audit Checklist (iterate through ALL)

### 1. API Endpoints Parity
Compare every PHP API endpoint in `api/` and root `*.php` against Java controllers:
- `api/api/ini.php` → `IniHandler.java`
- `api/api/notice.php` → `NoticeHandler.java`
- `api/api/getfile.php` → `GetfileHandler.java`
- `api/api/kmlogon.php` → `KmlogonHandler.java`
- `api/api/kmunmachine.php` → `KmunmachineHandler.java`
- `api/app.php` → `ApiController.java`
- `api/index.php` → `ApiController.java`

Check: parameters, validation, business logic, response format, error handling, encryption modes.

### 2. Admin Features Parity
Compare every PHP admin page in `admin/` against Java admin controllers + Vue admin views:
- Login/auth
- App CRUD + toggles + security settings
- Km management (generate, list, filter, batch ops, clear)
- File management
- System settings
- Stats/dashboard
- User management

### 3. Public Pages Parity
Compare PHP templates in `template/` against Vue public views:
- Homepage (index)
- Login / Register
- User center (check-in, work orders, points, withdrawal)
- API docs page
- Download page

### 4. Business Logic Parity
Compare `includes/` PHP classes against Java services/common:
- `db.class.php` → MyBatis-Plus mappers
- `common.php` → `ConfigService`, utility classes
- `cache.class.php` → Redis caching
- `mi_rc4` crypto → `ApiCrypto.java`
- RSA encryption → `ApiCrypto.java`
- Signature verification → `ApiSignature.java`
- Email sending → `MailService.java`
- Lanzou cloud resolver → `LanzouResolver.java`
- Captcha → `CaptchaService.java`
- User auth → `UserAuthInterceptor.java`, `JwtUtil.java`

### 5. Database Schema Parity
Compare `install/simple.sql` tables against `schema.sql` entities:
- All tables present?
- All columns present?
- Column types compatible?
- Indexes preserved?

### 6. Missing Features Detection
Check for PHP features NOT present in Java:
- File download endpoint (`download.php`)
- Doc page endpoint (`doc.php`)
- Jump/redirect endpoint (`jump.php`)
- Update check endpoint (`update.php`)
- Payment integration
- SMS service
- Invite/referral system
- Point system
- Withdrawal (tixian) system
- Work order system
- Check-in system
- Message/notification system

## Process
1. Study the PHP source code for each category above
2. Study the corresponding Java/Vue code
3. Identify gaps, bugs, or logic mismatches
4. Fix each issue by editing the Java/Vue code
5. After each fix, verify the change is correct
6. Commit after each logical fix group

## Rules
- Do NOT delete or modify PHP files — they are reference only
- Preserve backward compatibility with existing API clients
- Match PHP behavior exactly for the legacy API layer
- Use existing project patterns (MyBatis-Plus, Element Plus, etc.)
- Keep code clean and well-documented

## Completion
When ALL audit items are verified complete and no gaps remain, write "RALPH_DONE" to signal completion.
