# دليل نشر Football Mania 2D على Google Play

## المرحلة 1: رفع المشروع إلى GitHub وبناء ملف AAB (حوالي 15 دقيقة)

1. سجّل الدخول إلى github.com، ثم اضغط **New repository**.
   - الاسم: `football-mania-2d`
   - النوع: **Public**، وهو ضروري ليعمل GitHub Pages مجاناً لصفحة الخصوصية.
   - اضغط **Create repository**.
2. في صفحة المستودع اضغط **uploading an existing file**، واسحب **محتويات** مجلد `football-mania-android` كلها:
   `app` و`docs` و`.github` و`build.gradle.kts` و`settings.gradle.kts` و`gradle.properties` و`.gitignore` و`GUIDE-AR.md` و`README.md`.
   - ⚠️ مجلد `.github` قد يكون مخفياً في جهازك. في ويندوز فعّل "إظهار العناصر المخفية"، وفي ماك اضغط Cmd+Shift+.
   - ⚠️ **لا ترفع** مجلد `signing` أبداً.
   - اضغط **Commit changes**.
3. أضف مفاتيح التوقيع: **Settings ← Secrets and variables ← Actions ← New repository secret**. أضف 4 أسرار، قيمها في ملف `signing/SECRETS.txt`:
   - `KEYSTORE_BASE64`: انسخ محتوى الملف `KEYSTORE_BASE64.txt` كاملاً
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`: قيمته `upload`
   - `KEY_PASSWORD`
4. ابدأ البناء: تبويب **Actions ← Build Android App Bundle ← Run workflow**. يستغرق البناء 3 إلى 5 دقائق تقريباً.
5. عند ظهور ✅ افتح البناء، وحمّل من أسفل الصفحة (Artifacts):
   - `football-mania-2d-aab-vN`: ملف AAB تنزّله على Google Play
   - `football-mania-2d-apk-vN`: ملف APK لتجربته على هاتفك مباشرة

> كل مرة تعدّل فيها اللعبة (`app/src/main/assets/index.html`) وترفعها، يُبنى ملف جديد تلقائياً برقم إصدار أعلى.

## المرحلة 2: صفحة سياسة الخصوصية (دقيقتان)

1. افتح `docs/index.html` في GitHub (✏️ Edit)، واستبدل `YOUR_EMAIL_HERE` ببريدك، ثم Commit.
2. افتح **Settings ← Pages**، واختر **Branch: main** والمجلد **/docs**، ثم Save.
3. بعد دقيقة يصبح الرابط جاهزاً: `https://اسم-المستخدم.github.io/football-mania-2d/`

## المرحلة 3: Google Play Console

1. **Create app**: الاسم Football Mania 2D، واللغة الافتراضية English أو العربية، والنوع **Game**، و**Free**.
2. **App content**: املأ الأقسام كما في ملف `STORE-LISTING.md`، وهي سياسة الخصوصية وأمان البيانات وتصنيف المحتوى والإعلانات والجمهور المستهدف.
3. **Main store listing**: انسخ النصوص العربية والإنجليزية، وارفع الأيقونة والصورة الرئيسية ولقطات الشاشة.
4. **Play App Signing**: يتفعّل تلقائياً عند أول رفع. اترك Google تدير مفتاح التطبيق، أما ملفك فهو "مفتاح الرفع" فقط.

## المرحلة 4: الاختبار المغلق (إلزامي للحسابات الشخصية الجديدة)

حسابات المطوّرين الشخصية المُنشأة بعد نوفمبر 2023 يجب أن تجري **اختباراً مغلقاً مع 12 مختبِراً على الأقل لمدة 14 يوماً متواصلة** قبل طلب النشر للعامة.

1. افتح **Testing ← Closed testing ← Create track**، وارفع ملف AAB.
2. أضف قائمة بريدية فيها 12 حساب Gmail أو أكثر (أصدقاء وعائلة وزملاء).
3. أرسل لهم رابط الانضمام، ويجب أن يضغط كل منهم **Become a tester** ويثبّت اللعبة **ويبقى مشتركاً** 14 يوماً.
4. بعد 14 يوماً يظهر زر **Apply for production**. أجب عن الأسئلة، وتراجع Google الطلب خلال أيام عادةً.

> إذا كان حسابك **حساب شركة (Organization)**، أو كان حساباً شخصياً قديماً قبل نوفمبر 2023، فلا يلزمك هذا الاختبار، ويمكنك النشر مباشرة من **Production**.

## المرحلة 5: النشر

**Production ← Create new release ←** ارفع ملف AAB **← Review ← Start rollout**. المراجعة الأولى تستغرق عادة من يوم إلى 7 أيام.

---

### ملاحظات مهمة
- **اسم الحزمة** `com.footballmania2d.game` ثابت ولا يمكن تغييره بعد أول رفع.
- **احتفظ بنسخة** من مجلد `signing` في مكانين آمنين. إذا فُقد يمكن طلب إعادة تعيين مفتاح الرفع من دعم Google Play.
- اللعبة تستهدف **Android 16 (API 36)**، وهو مطلب Google للتطبيقات الجديدة منذ 31 أغسطس 2026.
- اللعبة تعمل دون إنترنت ولا تطلب أي صلاحيات.
