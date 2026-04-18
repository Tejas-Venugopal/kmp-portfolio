# Deployment

## Web (Kotlin/Wasm)

```powershell
./gradlew :composeApp:wasmJsBrowserDistribution
```

Output: `composeApp/build/dist/wasmJs/productionExecutable/`
Contains: `index.html`, `composeApp.js`, `composeApp.wasm`, `skiko.wasm`, etc.

### GitHub Pages

```powershell
# 1. Build
./gradlew :composeApp:wasmJsBrowserDistribution

# 2. Push the dist folder to a `gh-pages` branch
$dist = "composeApp/build/dist/wasmJs/productionExecutable"
git worktree add ../gh-pages gh-pages
Copy-Item "$dist/*" ../gh-pages -Recurse -Force
Set-Location ../gh-pages
git add -A; git commit -m "deploy"; git push origin gh-pages
```

Then enable Pages → Source: `gh-pages` branch in repo settings.

### Vercel

1. `vercel init` in the project root.
2. `vercel.json`:

   ```json
   {
     "buildCommand": "./gradlew :composeApp:wasmJsBrowserDistribution",
     "outputDirectory": "composeApp/build/dist/wasmJs/productionExecutable",
     "headers": [
       {
         "source": "/(.*)\\.wasm",
         "headers": [{ "key": "Content-Type", "value": "application/wasm" }]
       }
     ]
   }
   ```
3. `vercel --prod`.

### Compression

Both GitHub Pages and Vercel automatically serve `.wasm` / `.js` with **Brotli or Gzip**, which typically shrinks the Wasm payload by **3–5×**. No extra configuration is required. If you self-host (Nginx/Cloudflare), enable:

```nginx
gzip on;
gzip_types application/wasm application/javascript text/css;
brotli on;
brotli_types application/wasm application/javascript text/css;
```

## Android (release APK)

```powershell
./gradlew :composeApp:assembleRelease
# Signed APK lands in composeApp/build/outputs/apk/release/
```

Upload `composeApp-release.apk` to a GitHub Release and link it from the site so visitors can A/B the **native** build against the **Wasm** build.

## Pre-publish checklist

- [x] `FontFamily.Monospace` used for tool chips, sub-headline, and footer (engineer aesthetic).
- [x] `PortfolioViewModel.init { dispatch(PortfolioIntent.LoadData) }` — screen never starts empty.
- [x] Hosting serves `.wasm` with Gzip/Brotli (Pages & Vercel: automatic).
- [x] `index.html` canvas stretches to `100vw / 100vh` with zero margin.
- [x] Remote project images via Coil 3 → no large assets in `/resources`.

