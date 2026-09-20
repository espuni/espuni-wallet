# El fork espuni de la wallet de referencia

Qué cambia este repositorio respecto a
[`eu-digital-identity-wallet/eudi-app-android-wallet-ui`](https://github.com/eu-digital-identity-wallet/eudi-app-android-wallet-ui),
por qué, y qué queda pendiente.

El fork persigue **diff mínimo con upstream**: cada delta se justifica aquí, y
todo lo que no aparece en este documento debería ser idéntico al original.

> **Índice**
> [1. Alcance](#1-alcance) ·
> [2. Cómo construir](#2-cómo-construir) ·
> [3. Rebranding](#3-rebranding) ·
> [4. Marco de confianza](#4-marco-de-confianza-trust-lab) ·
> [5. Verificación](#5-verificación) ·
> [6. Deltas vs upstream](#6-deltas-vs-upstream) ·
> [7. Pendientes](#7-pendientes)

---

## 1. Alcance

Dos bloques de trabajo, en dos commits separados y sin solaparse:

| Bloque | Commit | Qué toca |
|---|---|---|
| Rebranding a espuni | *Rebrand the reference wallet as espuni* | `resources-logic`, `assembly-logic`, `business-logic`, `ui-logic` |
| Marco de confianza del laboratorio | *Point the dev flavour's ETSI trust at the espuni trust lab* · *Shorten the dev flavour's trust list cache* | `core-logic`, sólo el flavor `dev` |

> Los commits se citan por asunto y no por hash a propósito: esta rama se rebasa
> sobre `upstream/main` cada vez que hay que integrar cambios del original, y en
> cada rebase los hashes cambian. `git log upstream/main..espuni/main` da la
> lista viva en cualquier momento.

Fuente de verdad del primero: `cp-platform/docs/brand-native-criteria.md`, una
destilación auditable del design system de `apps/portal`. Fuente del segundo:
`eudiaas/trust-lab`, `MANUAL.md` §8.

---

## 2. Cómo construir

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
./gradlew :app:assembleDevDebug :app:assembleDemoDebug
```

### APK ligero para instalar en un móvil

El APK de debug pesa **466 MB**, y 341 de ellos son librerías nativas para las
cuatro ABIs. Un teléfono real sólo usa una. Pidiendo sólo `arm64-v8a` baja a
**235 MB**, sin tocar ningún fichero del repositorio:

```bash
./gradlew :app:assembleDevDebug -Pandroid.injected.build.abi=arm64-v8a
```

> Ese flujo —el que usa el IDE para desplegar— escribe en
> `app/build/intermediates/apk/<flavor>/debug/`, **no** en `outputs/`. Y
> cualquier `assemble` posterior sin esa propiedad lo sobrescribe con el
> universal, así que conviene copiarlo fuera del árbol de build antes de usarlo.

### APK desde CI, sin Android SDK local

El workflow `espuni lab APK` (`.github/workflows/espuni-lab-apk.yml`) construye
`devDebug` en GitHub Actions y deja el APK como artefacto. Se lanza a mano
(*Run workflow*) y admite el publisher, el wallet provider y la ABI; por
defecto, el laboratorio de staging y `arm64-v8a`. Antes de subir el artefacto
imprime los cinco valores tal como quedaron compilados, que es la única forma
de saber a dónde apunta un APK sin instalarlo.

### La clave con la que se firma, y por qué importa

Un APK de debug lo firma AGP con `~/.android/debug.keystore`, que **genera si no
existe**. En un runner de CI no existe nunca, así que cada ejecución firma con
una clave distinta y el APK que sale no es una actualización del anterior:
Android lo rechaza al instalarlo encima, con un mensaje que según el
instalador es «paquete inválido» o «app no instalada». Comprobado sobre dos
builds nuestros: certificados `0c042db4…` y `57df21d9…`, los dos
`CN=Android Debug`, emitidos con minutos de diferencia.

El workflow lo resuelve con el secreto `ANDROID_DEBUG_KEYSTORE_B64` (el
keystore en base64). Si está, firma siempre igual; si no, avisa en el resumen
de la ejecución y hay que **desinstalar antes** de instalar. En los dos casos
imprime la huella SHA-256 del firmante, que es lo que hay que comparar cuando
una instalación falla.

Para crear el secreto una vez:

```bash
keytool -genkeypair -v -keystore debug.keystore -storepass android \
  -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 \
  -validity 10950 -dname "CN=Android Debug,O=Android,C=US"
base64 -w0 debug.keystore   # → secreto ANDROID_DEBUG_KEYSTORE_B64
```

### Cota de memoria obligatoria en WSL

**Sin esto la VM de WSL se cae, no el build.** El `gradle.properties` del
proyecto trae `org.gradle.jvmargs=-Xmx8192m`, pensado para una máquina de CI.
La VM de desarrollo tiene **7.7 GiB**, así que un solo JVM puede pedir más
memoria de la que existe — y hay tres: el daemon de Gradle, el de Kotlin (que
hereda ese `-Xmx` si no se le da el suyo) y los workers, que por defecto son
`nproc`.

Cuando eso agota la memoria, el host **mata la utility VM entera**: no queda
registro de OOM en `dmesg` porque no muere un proceso, muere la máquina. La
huella se ve en `journalctl --list-boots`, con arranques que terminan de golpe.

Las cotas van en `~/.gradle/gradle.properties`, que **tiene precedencia sobre
el fichero del proyecto** y no se commitea, así que CI y el resto del equipo no
se ven afectados:

```properties
org.gradle.jvmargs=-Xmx2560m -XX:MaxMetaspaceSize=768m -Dfile.encoding=UTF-8
kotlin.daemon.jvmargs=-Xmx1280m
org.gradle.workers.max=3
org.gradle.parallel=false
```

Con estas cotas, un `clean` + los dos flavours completos tocan un mínimo de
**1083 MiB disponibles** y no llegan a tocar la swap. El arreglo de fondo sería
un `.wslconfig` en el lado Windows dando más memoria; hoy no existe.

---

## 3. Rebranding

Upstream ya describe las tres capas de marca en
[`wiki/THEMING.md`](../wiki/THEMING.md); el trabajo sigue esa estructura y no
inventa mecanismos nuevos.

### 3.1 Color — `resources-logic/.../theme/values/ThemeColors.kt`

Los tokens literales de `apps/portal/app/tokens.css` son ahora la única fuente,
y de ellos se derivan los roles de Material 3. Tres decisiones estructurales que
hay que preservar en cualquier edición futura:

1. **El primario es el color del texto, no el verde.** `--verify` es semántico
   —significa «esta verificación pasó»— y no es nunca una llamada a la acción.
2. **Tres estados, no cinco.** `verify` / `alert` / `pending`. `horizon` y
   `signal` existen pero no son nunca un estado de verificación.
3. **Bordes, no sombras.** `surfaceTint` se iguala a `surface` para neutralizar
   la elevación tonal de M3, que el sistema web no tiene.

Dos consecuencias no obvias:

- **La escala de superficies está invertida en tema claro.** La página es
  `#F5F6F8` y una tarjeta es `#FFFFFF`, es decir *más clara*. Por eso las
  tarjetas van a `surfaceContainerLowest` y no al contenedor que `Card` y
  `Surface` eligen por defecto.
- **`verify`, `alert`, `pending`, `horizon` y `signal` no tienen rol en M3**, ni
  el tercer nivel de texto (`--text-muted`). Viven como propiedades de extensión
  de `ColorScheme`, siguiendo el idiom que el módulo ya usaba para
  `success`/`warning`/`pending`/`divider` — no se introdujo un `CompositionLocal`
  nuevo para no tener dos mecanismos conviviendo.

### 3.2 Tipografía — `ThemeTypography.kt`

Geist y Geist Mono empaquetadas (7 TTF, ~510 KB); Roboto eliminado del módulo al
quedar sin referencias. La escala del portal mapeada a los slots de M3, con el
`letter-spacing` de CSS —que va en `em`— convertido a `sp` absolutos: los valores
negativos crecen con el tamaño y **no se pueden copiar entre slots**.

`displayMedium` y `headlineSmall` están **interpolados**: el sistema web no tiene
nada en esos huecos. Están marcados como tales en el código.

Las cuatro variantes mono (`mono`, `monoBadge`, `monoCaption`, `monoEyebrow`) no
tienen slot en M3 y se exponen como extensiones de `Typography`, con
`fontFeatureSettings = "tnum"`. La mono no es decorativa: es el registro de
«valor de máquina» — ids de sesión, claims, códigos de protocolo.

### 3.3 Forma — `ThemeShapes.kt`

`4 / 6 / 8 / 12 / 12` dp, sustituyendo a `16 / 16 / 16 / 32 / 32`. El sistema no
sube de 12, así que `extraLarge` repite `large`.

Los `SIZE_*` de `ui-logic` (4/8/16/24/48, tarjeta a 8 dp) ya coincidían con la
escala espuni y **no se han tocado**.

> **Discrepancia heredada del web, resuelta aquí:** la página de design system
> documenta 6 px para botones e inputs y el CSS a mano lo cumple, pero los
> primitivos shadcn usan 12 px. Se toma como canon el valor documentado.

### 3.4 Identidad

- **Nombre**: `espuni`, en minúscula, como la marca en todas partes.
  `assembly-logic/build.gradle.kts`.
- **Icono**: dibujado a partir del **contorno real de la «e» de Geist Medium**,
  extraído del propio TTF, más el cuadrado verde. No es un trazado del PNG: es la
  curva del glifo, así que es fiel al wordmark a cualquier tamaño. Se eligió
  Medium porque la proporción del favicon de marca (0.880) coincide con ese peso
  (0.885) y no con Bold (0.950).
  El contenido cabe dentro del círculo de 66 dp que toda máscara de launcher
  respeta; la restricción que manda es la esquina superior derecha del cuadrado,
  a r=32.9 dp de 33.
- **Icono monocromo** para los iconos temáticos de Android 13+. El cuadrado verde
  no sobrevive ahí —el sistema aplica su propio tinte— y la marca prefiere
  perderlo antes que teñir el launcher de verde, que se leería como acción.
- **Splash** día/noche: la «e» sigue el tema, el cuadrado conserva su color.
- **Rasters EU eliminados** (33 ficheros). Con `minSdk = 29` el adaptive icon
  siempre gana, así que eran peso muerto.
- **Wordmark** (`ui-logic/.../EspuniWordmark.kt`): se compone tipográficamente
  —un `Text` y un `Box`— en vez de empaquetar un PNG, así que no pierde nitidez
  ni la relación con el tema. Incluye `EspuniTopAccent`, la línea de 3 dp.
- **Flavor `dev` distinguible**: fondo oscuro en el icono, para diferenciarlo de
  `demo` en la pantalla de inicio.

### 3.4-bis Logos e ilustraciones

La guía de upstream nombra **dos logos de marca** y avisa de que los originales
llevan el color incrustado en cada `<path>`, así que no siguen ningún tema:

| Drawable | Dónde sale |
|---|---|
| `ic_logo_icon.xml` | splash, vía `AppIcons.LogoIcon` |
| `ic_logo_icon_and_text.xml` | cabecera de contenido, vía `AppIcons.LogoIconAndText` |

Los dos son ahora la marca y el wordmark de espuni, con los contornos **reales de
Geist** extraídos de la fuente empaquetada y colocados por sus propios anchos de
avance, con el tracking de `-0.02em` y el cuadrado de `0.4em` a `0.06em` sobre la
línea base. Es el wordmark, no una foto del wordmark.

Sus colores van **por referencia** (`@color/espuni_mark`, `@color/espuni_verify`),
así que las letras siguen el tema y sólo el cuadrado conserva color propio. Ambos
mantienen el viewport del logo al que sustituyen: ninguno de los dos call sites
pasa modificador de tamaño, así que **el tamaño intrínseco es el layout**.

**Y un defecto que la guía no menciona.** Las quince ilustraciones que se dibujan
con `WrapImage` no llevan tinte, así que su azul EU llegaba a pantalla tal cual —
y al no existir `drawable-night`, se pintaba una ilustración de modo claro sobre
el fondo casi negro del tema oscuro. Su paleta se mapea ahora a tokens espuni
mediante colores con nombre que tienen las dos variantes:

| Original | Papel | espuni claro / oscuro |
|---|---|---|
| `#2A5FD9` `#2a5ed9` `#2B5EDA` `#2E293B` | trazo | `--text` |
| `#CAE6FD` `#CCE8FF` `#C8E4FD` `#ECECEC` | relleno decorativo | `--surface-2` |
| `#55953B` | verificado | `--verify` |
| `#B3261E` `#F67875` | fallo | `--alert` |
| `#F39626` | en vuelo | `--pending` |

Los rellenos decorativos colapsan a neutros, que es lo que pide el sistema: el
color se reserva para el significado.

> **Lo que se deja a propósito:** los ~25 iconos de un solo color que aún nombran
> el azul de la UE. `WrapIcon` los tiñe desde `LocalContentColor`, y en Compose el
> tinte de un `Icon` **sustituye** los colores del vector, así que ese valor no
> llega nunca a un píxel. Cambiarlos sería ruido en el diff sin efecto visual.

### 3.5 Sub-SDK RQES — `business-logic/.../EspuniRqesTheme.kt`

El SDK de firma remota (`eudi-lib-android-rqes-ui`) **empaqueta su propia copia
de este sistema de temas**, con la paleta azul de la app de referencia y sus
propias caras de Roboto. Sus pantallas son parte del recorrido de firma, así que
dejarlas por defecto rompía la marca a mitad de camino.

El SDK expone `EudiRQESUiConfig.themeManager`, y `EspuniRqesTheme` traduce
nuestras plantillas a las suyas. La traducción es total y mecánica;
`EspuniTokens` sigue siendo el único sitio donde se escribe un valor de token.

> Su builder acepta colores y tipografía pero **no formas**, así que los radios
> de esas pantallas siguen siendo los suyos. Los tres TTF de Roboto siguen dentro
> del APK porque vienen dentro del AAR: son recursos muertos que no podemos
> borrar sin bifurcar el SDK.

### 3.6 Decisiones abiertas del documento de criterios, resueltas aquí

| § | Pregunta | Decisión |
|---|---|---|
| 6.3 | Activos vectoriales del logo | Redibujado desde el wordmark tipográfico, extrayendo el glifo del TTF |
| 6.4 | La línea verde de 3 px con edge-to-edge | Va **debajo** de la barra de estado; teñir la barra sería teñir de verde el chrome, contra §0.1 |
| 6.5 | Material You / color dinámico | Desactivado (ya lo estaba por defecto); el color del fondo de pantalla sustituiría al primario monocromo |
| 6.6 | Tonos de superficie que faltan | Interpolados y **marcados como invención** en el código |
| 6.8 | Radio de botones e inputs | Gana el 6 px documentado sobre el 12 px de shadcn |

---

## 4. Marco de confianza (trust-lab)

Apunta la configuración ETSI del flavor `dev` al laboratorio de espuni en vez de
a la infraestructura de referencia de `eudiw.dev`.

### 4.1 Las cuatro listas

`core-logic/src/dev/.../WalletCoreConfigImpl.kt`, dentro de `configureEtsiTrust`:

| Slot de `SupportedLists` | Lista | Qué deja validar |
|---|---|---|
| `pidProviders` | `pid-lab.jwt` | el emisor del PID recibido |
| `wrpacProviders` | `wrpac-lab.jwt` | el access certificate del verificador |
| `wrprcProviders` | `wrprc-lab.jwt` | el registration certificate de la petición |
| `pubEaaProviders` | `pubeaa-lab.jwt` | el emisor de una atestación de organismo público |

Las cuatro se comprobaron antes de compilar: `200` + `content-type:
application/jwt`.

**Las URLs no están en el código**: salen de `BuildConfig`, con el laboratorio
de staging por defecto y parámetros de compilación para cambiarlas. Así una
wallet por entorno —o por tenant, que es a donde va esto— es el mismo código
con otros parámetros:

```bash
./gradlew :app:assembleDevDebug \
  -PLAB_PUBLISHER=https://<publisher del lab> \
  -PLAB_WALLET_PROVIDER_HOST=https://<wallet provider>
```

| Parámetro | Por defecto |
|---|---|
| `LAB_PUBLISHER` | `https://trust-lab-publisher-staging.up.railway.app` |
| `LAB_PID_TRUST_LIST` · `LAB_WRPAC_TRUST_LIST` · `LAB_WRPRC_TRUST_LIST` · `LAB_PUBEAA_TRUST_LIST` | `$LAB_PUBLISHER/lote/<lista>.jwt` |
| `LAB_WALLET_PROVIDER_HOST` | `https://wallet-provider-staging.up.railway.app` |

> **Por qué el publisher de staging y no `trust-lab.espuni.com`.** Ese dominio
> no resuelve: no tiene registro DNS. Las listas de staging se sirven en el
> dominio de Railway del publisher, y desde el 19-09-2026 los documentos de
> staging declaran esa URL también dentro de lo firmado (el `sub` de cada status
> list y el puntero de cada lista a sí misma). El día que exista producción con
> su dominio, se compila con `-PLAB_PUBLISHER=https://trust-lab.espuni.com`.

**`wallet-lab` no está cableada en ningún slot**, aunque el laboratorio la
publique y responda: una wallet no valida a su propio proveedor. Esa lista la
consume el **emisor** (EUDIPLO, por `walletProviderTrustLists`) para comprobar la
Wallet Instance Attestation que la wallet presenta.

### 4.1-bis El wallet provider

`walletProviderHost` también sale de `BuildConfig`
(`LAB_WALLET_PROVIDER_HOST`). Es quien firma la Wallet Instance Attestation que
la wallet presenta al pedir una credencial, con una clave publicada en
`wallet-lab`: sin apuntarlo al del laboratorio, la WIA la firmaría el wallet
provider de `eudiw.dev` y el emisor no podría reconocerla.

En staging es el servicio `wallet-provider` del proyecto trust-lab, que firma
con el `wia-signer` del laboratorio y pide a la consola la posición de status
list de cada WIA.

### 4.1-ter Metadatos firmados del emisor: preferidos, no exigidos

La variante `dev` de upstream llama a `requireSignedMetadata()` con política
`ENFORCE`, y con ella la emisión queda **bloqueada antes de empezar**:
*«Issuance blocked — the provider could not be verified by your wallet»*.

**Qué pide la wallet.** `DefaultCredentialIssuerMetadataResolver` de
`eudi-lib-jvm-openid4vci-kt` 0.13.1 pide el `.well-known` con
`accept: application/jwt` y espera una respuesta con ese mismo `Content-Type`:
un JWT `openidvci-issuer-metadata+jwt` con la metadata en el payload. Si vuelve
JSON, es `MissingSignedMetadata`; si el JWT no valida,
`InvalidSignedMetadata`.

**Con qué certificado se firma.** Con el **access certificate** del emisor, no
con su Document Signer: `wallet-core` valida esa firma con
`EtsiCertificateChainTrust` pasando el contexto
`VerificationContext.WalletRelyingPartyAccessCertificate`, y la librería ETSI
resuelve ese contexto contra el caso de uso **WRPAC**, es decir la lista
`wrpac-lab`. `pid-lab` cubre otra cosa: el firmante de la credencial.

**EUDIPLO ya lo hace.** `WellKnownService.getIssuerMetadata()` negocia por
`Accept` y firma el JWT con `certService.find({ type: KeyUsageType.Access })`,
poniendo su cadena en `x5c`. Es exactamente lo que pide la wallet; no falta
nada en EUDIPLO.

Lo que falla es **cuál** de los certificados de acceso coge. `findByUsageType`
hace un `findOne({ tenantId, usageType: 'access' })` **sin orden ni criterio**,
y el tenant tiene más de uno: el que EUDIPLO se autogeneró al arrancar
(`C=DE, CN=espuni`, bajo su propia `espuni Root CA`) y el que importa el
laboratorio desde un WRPAC. En staging coge el autogenerado, que no encadena
con `wrpac-lab`, así que la firma no la avala nadie.

De ahí que de momento se **prefieran** (`preferSignedMetadata()`). Ojo con lo
que eso significa: «preferir» sólo tolera la **ausencia**. Si el emisor
responde `application/jwt` —y EUDIPLO responde—, `requestPreferringSigned`
llama igualmente a `parseAndVerifySignedMetadata` y **propaga el fallo**; no
hay vuelta atrás a los metadatos sin firmar. La diferencia con `require` es
sólo qué pasa cuando el emisor no los firma en absoluto.

Se vuelve a exigir con `-PLAB_REQUIRE_SIGNED_METADATA=true`, cuando el único
certificado de acceso del tenant —o el que EUDIPLO acabe eligiendo— sea uno
emitido bajo `wrpac-ca` y publicado en `wrpac-lab`.

### 4.2 Por qué sólo el flavor `dev`

Los dos flavors traían un bloque `configureEtsiTrust` **idéntico byte a byte** y
sólo difieren en `issuersConfig`. Se toca `dev` porque:

1. `dev` lleva `applicationIdSuffix = ".dev"`, así que **ambas builds conviven en
   el mismo teléfono**: queda la wallet contra `eudiw.dev` al lado de la del
   laboratorio para comparar cuando algo no valide.
2. Diff mínimo: un fichero.
3. Las listas del laboratorio se declaran a sí mismas `(TEST)` en su
   `SchemeName`.

> ⚠ **Consecuencia conocida.** `issuersConfig` de `dev` sigue apuntando a
> emisores de `eudiw.dev`, cuyos Document Signers encadenan con la lista de
> `eudiw.dev`, no con `pid-lab`. Con `TrustPolicy.Action.ENFORCE` y
> `requireSignedMetadata()`, **la emisión desde esos emisores queda rechazada**.
> `dev` es, en el intervalo, una wallet que sólo confía en el laboratorio y sólo
> alcanza emisores que el laboratorio no avala. Se resuelve cuando EUDIPLO emita
> un PID de laboratorio.

### 4.3 Firma de las listas: no se pinea a nadie

El verificador por defecto de wallet-core, `LoteJwtVerifier`, comprueba la firma
del JWS **contra el certificado que la propia lista transporta en su `x5c`** —sin
cadena, sin revocación y sin ancla—, y lo dice en su propia documentación. El
TLSO autofirmado del laboratorio pasa tal cual, así que **no hace falta instalar
ningún ancla**.

Eso es cómodo aquí y es **un modo de fallo, no una virtud**. Cuando la librería
lo cierre, la vía es `jwtSignatureVerifier()`, que la API ya expone.

### 4.4 Los dos `relax`, con evidencia

**`relaxPkixRevocation()` — obligatorio.** Hace
`PKIXParameters.setRevocationEnabled(false)`. Java lo trae en `true`. Se
decodificaron las cuatro listas y se volcaron las extensiones de **todos** los
certificados publicados:

| Lista | Certificado publicado | CRLDP | AIA |
|---|---|---|---|
| `pid-lab` | `CN=Lab PID DS 01` — hoja, EKU `1.0.18013.5.1.2` | — | — |
| `wrpac-lab` | `CN=Lab RPAC CA` — `CA:TRUE, pathlen:0`, autofirmado | — | — |
| `wrprc-lab` | `CN=Lab RPRC DS 01` — hoja, sin EKU | — | — |
| `pubeaa-lab` | `CN=Lab PubEAA Provider` — hoja, EKU `1.0.18013.5.1.2` | — | — |

Ninguno lleva punto de distribución de CRL ni AIA, y ninguna CA del laboratorio
emite CRL. Sin este `relax`, la cadena falla con *«could not determine revocation
status»* antes de poder validar nada. **Quitarlo el día que el laboratorio
publique datos de revocación, no antes.**

**`relaxCertificateProfiles()` — se queda, con condición de retirada.** Reescribe
el `LotEMeta.SvcAndEEProfile` de cada lista para descartar el perfil de
certificado de entidad final declarado. Lo que se salta es el vocabulario de
restricciones de `certs.*` (KeyUsage, EKU, BasicConstraints, QCStatements, SAN…).

> **Trampa a tener presente:** ese vocabulario incluye `GetCrlDistributionPoints`
> y `GetAia`. Si un perfil declarado exigiera un CRLDP, el material del
> laboratorio fallaría *también* por perfil, no sólo por revocación.

El laboratorio emite a dos perfiles deliberadamente mínimos y no añade ningún OID
que la norma no exija, así que probablemente sobre — pero quitarlo es un apriete
cuya única prueba honesta es una credencial real del laboratorio.

### 4.5 Registration certificates: dónde se activan

`configureWrpRegistrationPolicy` y `configureIssuerRegistrationPolicy` se
controlan con `isRegistrationCheckEnabled`, que es un **ajuste en runtime**:

**Ajustes → «Check Registration Certificates»**
(`settings_screen_option_registration_check`)

`SettingsViewModel` → `SettingsMenuItemType.REGISTRATION_CHECK` →
`settingsInteractor.toggleRegistrationCheck()` → `RegistrationCheckProviderImpl`
→ preferencias.

La app muestra después *«Restart the app to apply this change»*, y es literal:
`isRegistrationCheckEnabled` es un `by lazy` con `runBlocking` que se lee al
construir la configuración, y `_config` está memoizado. **Conmutar, matar la app,
relanzar.** Ahí es donde `WrpRegistrationPolicy.Enabled` entra en vigor, que es
lo que hace valer el registration certificate.

`WalletCoreConfig.kt:42` avisa además de que la aplicación debe consultar
`isRegistrationCheckEnabled` y no el provider, porque wallet-core lee los dos.

### 4.6 Caché de listas — aplicado en `dev`

`EtsiTrustConfig` trae `fileCacheExpiration` (24 h, en disco bajo `lote-cache`) y
`cacheTtl` (20 min, en memoria). `demo` no los toca y rige ese default.

Es exactamente el modo de fallo «cambiamos una lista y la app no se entera», y
24 h de caché en disco durante trabajo de laboratorio es mucho. Aplicado en
`dev` (`demo` conserva los defaults, porque apunta a infraestructura que no
cambia bajo nuestros pies):

```kotlin
fileCacheExpiration(15.minutes)
cacheTtl(1.minutes)
```

---

## 5. Verificación

### Hecho

| Comprobación | Resultado |
|---|---|
| URLs del laboratorio antes de compilar | 4/4 → `200` + `application/jwt` |
| `clean` + `assembleDevDebug` + `assembleDemoDebug` | **BUILD SUCCESSFUL**, sin warnings propios |
| Etiqueta de la app, ambos flavours | `espuni` |
| `dev`: URLs de `trust-lab` en el DEX | 4 |
| `dev`: cadenas de `trustedlist.serviceproviders.eudiw.dev` | **0** |
| `demo`: intacto como control | 0 trust-lab / 4 eudiw.dev |
| Fuentes Geist empaquetadas | 7 |
| Vectores espuni | 3 |
| Rasters de launcher EU | **0** |
| Memoria mínima durante el build | 1083 MiB, swap sin tocar |

### No hecho, y por qué

**La comprobación en runtime de la descarga de listas.** Las listas se cargan
**en perezoso**, en la primera validación de cadena: el constructor de
`EtsiTrustProvider` no lanza ninguna corrutina —sólo registra `ETSI trust
provider initialized`, tag `EtsiTrust`— y la app nunca lo invoca directamente.
Arrancar en frío no descarga nada.

La única prueba en runtime que significa algo es la que dispara una validación
real, y ésa necesita que EUDIPLO emita un PID de laboratorio con el Document
Signer del propio laboratorio. Cuando exista:

```bash
adb install -r app/build/outputs/apk/dev/debug/app-dev-debug.apk
adb logcat -c && adb logcat | grep -iE 'EtsiTrust|lote|trust-lab|LoteJwt'
```

---

## 6. Deltas vs upstream

| Fichero | Delta | Justificación |
|---|---|---|
| `resources-logic/.../ThemeColors.kt` | Paleta espuni + extensiones semánticas | §3.1 |
| `resources-logic/.../ThemeTypography.kt` | Escala espuni sobre Geist + estilos mono | §3.2 |
| `resources-logic/.../ThemeShapes.kt` | Radios 4/6/8/12/12 | §3.3 |
| `resources-logic/.../EspuniTokens.kt` | **Nuevo.** Publica la paleta para el SDK RQES | §3.5 |
| `resources-logic/res/font/geist_*.ttf` | **Nuevos** (7). Roboto borrado | §3.2 |
| `resources-logic/res/drawable/ic_espuni_*.xml` | **Nuevos** (3) | §3.4 |
| `resources-logic/res/drawable/ic_logo_icon*.xml` | Marca y wordmark espuni, temables | §3.4-bis |
| `resources-logic/res/drawable/` (20 ilustraciones e iconos) | Paleta por referencia, con variante noche | §3.4-bis |
| `resources-logic/res/mipmap-*/ic_launcher*.webp` | **Borrados** (33) | Muertos con `minSdk 29` |
| `resources-logic/res/values{,-night}/colors.xml`, `themes.xml` | Splash y tema día/noche | §3.4 |
| `assembly-logic/build.gradle.kts` | `appName` → `espuni` | §3.4 |
| `assembly-logic/AndroidManifest.xml` | `Theme.EUDIWallet` → `Theme.Espuni` | §3.4 |
| `business-logic/.../EspuniRqesTheme.kt` | **Nuevo.** Puente de tema al SDK RQES | §3.5 |
| `business-logic/{demo,dev}/.../RQESConfigImpl.kt` | `override val themeManager` | §3.5 |
| `ui-logic/.../EspuniWordmark.kt` | **Nuevo.** Wordmark y línea superior | §3.4 |
| `core-logic/src/dev/.../WalletCoreConfigImpl.kt` | Cuatro URLs, justificación de los `relax`, caché corta | §4 |

**Nada de esto debería ir upstream**: son configuración y marca propias. Con una
excepción, que sí es un fallo del original y merece issue —
[§7](#7-pendientes), punto 1.

---

## 7. Pendientes

### Marco de confianza

1. **`classifications` no declara `pubEAAs`, y por eso `pubeaa-lab` está muerta.**
   `AttestationClassifications` tiene cuatro slots (`pids`, `pubEAAs`, `qEAAs`,
   `eaAs`) y hoy sólo se declara el primero — igual que en upstream, que sí
   declara la URL `pubEaaProviders`. La cadena: `classify()` devuelve `null`
   cuando nada casa; `IsChainTrustedForAttestation` con `null` no produce
   `VerificationContext`; y el `VerificationContext` es **lo que elige de qué
   lista salen las anclas**. Sin declararlo, una PuB-EAA nunca se clasifica y la
   lista no se consulta jamás.

   No se aplica todavía porque falta el dato: el docType concreto de la PuB-EAA
   del laboratorio se decide al configurar EUDIPLO. `DocumentIdentifier` sólo
   conoce `MdocPid`, `SdJwtPid` y `OTHER`. Cuando exista:

   ```kotlin
   pubEAAs = AttestationIdentifierPredicate.any(
       identifiers = setOf(AttestationIdentifier.MDoc(docType = "<docType del lab>"))
   )
   ```

   **Cuidado**: si ese docType solapa con los predicados de `pids`, `classify`
   **lanza** `IllegalStateException`, no elige. → *merece issue upstream.*

2. **Reevaluar `relaxCertificateProfiles()`** contra una credencial real del
   laboratorio (§4.4).
3. **Pinear el firmante de listas** con `jwtSignatureVerifier()` cuando
   `LoteJwtVerifier` deje de aceptar el `x5c` a ciegas (§4.3).
4. **`walletProviderHost`**: el wallet provider del laboratorio aún no está
   desplegado. Otra tanda, y no se ha tocado.
5. **Validar una credencial real** contra el marco, que es lo que cierra el lazo
   y desbloquea la verificación en runtime (§5).

### Rebranding

6. **Contraste (§6.7 del documento de criterios).** Se mantiene paridad byte a
   byte con el web, así que `verify` mide 3.30:1 y `pending` 3.19:1 sobre
   superficie clara. Pasa para los iconos grandes con los que la wallet los usa
   de hecho, pero **no para texto pequeño**, y el Accessibility Scanner lo
   marcará. Opciones: oscurecer sólo para la app, cambiarlos en `tokens.css` para
   ambos, o subir tamaño y peso del texto que los usa.
7. **Pasada por componentes.** No se ha tocado ningún `Wrap*`: `WrapCard` sigue
   usando elevación de Material en vez del borde de 1 dp que pide el sistema, y
   los badges siguen sin ser rectángulos de `radius-xs`. Es el trabajo que falta
   para que la app lea «bordes, no sombras».
8. **Formas del SDK RQES**: su builder no acepta `Shapes`, así que esas pantallas
   conservan sus radios (§3.5).
9. **Decisiones de §6 sin abordar**: modelo de navegación (6.1), familia de
    iconos (6.2), densidad y áreas táctiles (6.9), movimiento (6.10), fuente
    canónica de cadenas (6.11), estados táctiles y ripple (6.12), superficies que
    el web no tiene —notificaciones, widgets, atajos— (6.13), escalado de fuente
    y TalkBack (6.14), tablet y plegables (6.15).
