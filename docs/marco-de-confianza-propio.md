# Apuntar la wallet de referencia a tu propio marco de confianza

Manual para quien coge `eudi-app-android-wallet-ui` y quiere que confíe en su
laboratorio en vez de en la infraestructura de la UE. Ordenado por lo único
que importa cuando empiezas: **qué es obligatorio, qué te va a morder, y qué
puedes desactivar sin pensarlo mucho**.

Medido contra `wallet-core` 0.30.2 y `openid4vci-kt` 0.13.1, en septiembre de
2026. Todo lo que aquí se afirma se comprobó ejecutando esas librerías; lo que
es opinión va dicho como opinión.

> **La regla que resume el resto.** La wallet casi nunca te dice qué campo le
> ha molestado. Su log termina en `trusted=true` y el siguiente paso no llega a
> la red. Antes de instalar un APK para descubrir algo, pásale el artefacto a
> sus propias librerías desde tu portátil. En este laboratorio eso está en
> [`trust-lab`](https://github.com/eudiaas/trust-lab) como `npm run parsers`, y
> resolvió cuatro fallos seguidos que el ciclo de APK-y-log no resolvió
> ninguno.

---

## 1. El mapa: dónde vive cada decisión

Casi todo está en **un solo fichero**, el `WalletCoreConfigImpl` de tu flavor
(`core-logic/src/<flavor>/java/.../config/`). Ahí dentro hay cinco bloques que
tocan la confianza:

| bloque | decide |
|---|---|
| `configureEtsiTrust` | de qué listas saca los anclajes de confianza, y cuánto relaja la validación de certificados |
| `configureIssuerTrust` | si exige metadatos firmados, y si comprueba el certificado de registro del emisor |
| `configureWrpRegistrationPolicy` | lo mismo para el verificador |
| `configureReaderTrustStore` | si exige reader auth en presencial |
| `configureDocumentStatusResolver` | qué hace si no puede comprobar la revocación |

Y fuera de ese bloque, dos cosas más: `issuersConfig` (cómo se autentica
contra el emisor) y `documentIssuanceConfig` (**cuántas credenciales pide y si
las reutiliza** — la que más sorprende).

---

## 2. Obligatorio

### 2.1 Las cuatro listas

```kotlin
configureEtsiTrust {
    loteLocations(
        SupportedLists(
            pidProviders   = Uri(BuildConfig.LAB_PID_TRUST_LIST),
            wrpacProviders = Uri(BuildConfig.LAB_WRPAC_TRUST_LIST),
            wrprcProviders = Uri(BuildConfig.LAB_WRPRC_TRUST_LIST),
            pubEaaProviders = Uri(BuildConfig.LAB_PUBEAA_TRUST_LIST),
        )
    )
}
```

Son las únicas cuatro que esta wallet consume. **Pásalas por `BuildConfig`, no
las escribas aquí**: una build por entorno es el mismo código con otros
parámetros, y vas a querer cambiarlas sin recompilar mentalmente.

Dos cosas que cuestan un día si no te las dicen:

- **La lista de proveedores de wallet no va aquí.** Una wallet no valida a su
  propio proveedor. Esa lista la consume **el emisor**, para comprobar la
  Wallet Instance Attestation que la wallet le presenta. Si buscas dónde
  ponerla en la wallet, no está, y no es un olvido.
- **Que tu librería de construcción valide la lista no significa que la wallet
  pueda leerla.** `@owf/eudi-lote` acepta listas que el modelo de datos de EUDI
  rechaza: `TEInformationURI` ausente (opcional para uno, obligatorio para el
  otro), `TEAddress.TEElectronicAddress` vacío, `srv_description` anidado.
  Ninguno de los tres da un error que nombre el campo. **Valida contra el
  consumidor, no contra ti mismo.**

### 2.2 El wallet provider

```kotlin
override val walletProviderHost: String
    get() = BuildConfig.LAB_WALLET_PROVIDER_HOST
```

De ahí salen la WIA y, si exiges key attestation, la atestación de claves
(`POST {host}/key-attestation/jwk-set`). Su firmante tiene que encadenar con
la lista de proveedores que **el emisor** tiene configurada.

---

## 3. Lo que te va a morder

### 3.1 Metadatos firmados del emisor

```kotlin
configureIssuerTrust {
    policy { default(TrustPolicy.Action.ENFORCE) }
    requireSignedMetadata()      // o preferSignedMetadata()
}
```

Esto **no** valida al Document Signer: valida el **certificado de acceso** que
firma los metadatos, contra la lista de WRPAC. Y es de donde cuelga el vínculo
con el certificado de registro: `getMetadataSigningCertificate()` →
`isBoundTo()`, que compara el `organizationIdentifier` (OID 2.5.4.97) del
certificado de acceso con el `sub` del registro. Sin metadatos firmados no hay
certificado de acceso, y la emisión se para con `NOT_BOUND_TO_REQUESTER`.

`prefer` **solo tolera la ausencia**: si llegan firmados y no validan, falla
igual. Empieza por `prefer` mientras montas el emisor y sube a `require`
cuando firme.

> ⚠️ Si tu emisor es EUDIPLO: negocia el `Accept` por igualdad exacta contra
> `application/jwt`, y la wallet manda la **lista** `application/jwt,
> application/json`. Resultado: te sirve JSON sin firmar y no te enteras.
> Requiere parche en el emisor.

### 3.2 Cuántas credenciales pide, y si las reutiliza

**El problema que más desconcierta**, porque parece un fallo de emisión y no
lo es:

```kotlin
documentIssuanceConfig = DocumentIssuanceConfig(
    defaultPolicy = CredentialPolicy.RotatingBatch(
        numberOfCredentials = 1, reissueTriggerLifetimeLeft = 24.hours
    ),
    documentSpecificPolicies = mapOf(
        DocumentIdentifier.MdocPid to CredentialPolicy.OnceOnly(
            numberOfCredentials = 60, reissueTriggerUnused = 2
        ),
    ),
)
```

El PID viene sobrescrito a **`OnceOnly`**: la wallet borra la clave **al
responder una presentación**, antes de saber si el verificador la acepta. Si
tu emisor no anuncia `batch_credential_issuance`, las 60 se quedan en **una**,
y la primera presentación —aunque sea rechazada— deja el documento inservible:
*«the requested document is not available in your EUDI wallet»*.

Tienes dos palancas, y **la del emisor gana**:

| quién | cómo | alcance |
|---|---|---|
| la wallet | quitar la sobrescritura de `MdocPid` y caer al `defaultPolicy` | solo tu APK |
| **el emisor** | publicar `credential_reuse_policy` en la metadata | **cualquier wallet**, sin APK nueva |

Con política del emisor, la wallet emite `MandatoryReusePolicy`, que lleva la
política **ya resuelta** y cuyo `resume` ni siquiera acepta una. Sin ella,
emite `OptionalReusePolicy` y manda la configuración de la app.

Para una credencial estática y reutilizable, el emisor publica:

```json
"credential_reuse_policy": {
  "id": "arf_annex_ii",
  "options": [{ "details": ["limited_time"], "reissue_trigger_lifetime_left": 86400 }]
}
```

`reissue_trigger_lifetime_left` va en **segundos** (86400 → `1d`; medido, no
deducido). Y **sin el `id` el parser lo ignora en silencio** y devuelve `None`
—no falla—, que es el modo de fallo más caro de todos.

El precio de reutilizar: dos verificadores pueden cruzar la misma
presentación. Para un laboratorio compensa; para un PID de producción, no.

La wallet también declara qué políticas acepta, y si no lista la que publica
el emisor, la ignora:

```kotlin
.withSupportedCredentialReusePolicies(
    CredentialReusePolicies.Supported(
        policyTypes = setOf(RotatingBatch, OnceOnly, LimitedTime)
    )
)
```

### 3.3 Autenticación contra el emisor

```kotlin
.withClientAuthenticationType(AttestationBased(clientId = "eudiw-abca"))
```

El `sub` de la WIA tiene que ser **igual** al `client_id`. Y el emisor debe
anunciar `attest_jwt_client_auth` en `token_endpoint_auth_methods_supported`;
si no, la wallet ni lo intenta.

---

## 4. Lo que puedes desactivar

Todo esto es legítimo en un laboratorio. Lo que **no** es legítimo es
desactivarlo y olvidar que lo hiciste, así que cada uno lleva su condición de
salida.

| desactivar | por qué querrías | cuándo volver a activarlo |
|---|---|---|
| `relaxPkixRevocation()` | `PKIXParameters` comprueba revocación por defecto. Si tus certificados no llevan CRLDP ni AIA, o tu CA no publica CRL, la cadena falla con *«Could not determine revocation status»* **antes** de validar nada | el día que publiques datos de revocación. Ni antes ni después |
| `relaxCertificateProfiles()` | perfiles mínimos hechos a mano suelen chocar con comprobaciones de perfil | en cuanto tengas una credencial real emitida contra la que comprobarlo. Quitarlo es un endurecimiento, y solo se confirma con evidencia |
| `IssuerRegistrationPolicy.Disabled` | los certificados de registro (WRPRC) son un escalón aparte: puedes tener emisión funcionando sin ellos | cuando tu registro emita WRPRC de verdad |
| `WrpRegistrationPolicy.Disabled` | lo mismo, del lado del verificador | ídem |
| `ReaderAuthPolicy.EnforceIfPresent` | en presencial, exigir reader auth siempre bloquea pruebas con lectores de juguete | en producción, `Enforce` |
| status resolver con `TrustPolicy.Action.INFORM` | si no puedes comprobar el estado, informar en vez de bloquear | cuando tus listas de estado sean fiables |

Y dos que **no son seguridad** aunque lo parezcan:

```kotlin
fileCacheExpiration(15.minutes)   // por defecto 24 h
cacheTtl(1.minutes)               // por defecto 20 min
```

Los valores de fábrica son de producción. Mientras cableas el laboratorio son
exactamente la forma de «cambié la lista y la app no se entera en un día».
Bájalos, y súbelos al terminar.

---

## 5. Lo que NO se toca en la wallet

Se pierde mucho tiempo buscando en la wallet cosas que están en el emisor:

| | dónde |
|---|---|
| exigir key attestation (`key_attestations_required`) | **emisor**, metadata |
| tamaño de lote (`batch_credential_issuance`) | **emisor**, config de emisión |
| política de reutilización | **emisor**, metadata (gana sobre la wallet) |
| la lista de proveedores de wallet | **emisor**, para validar la WIA |
| qué claims puede pedir un verificador | **certificado de registro**, no la wallet |

Dos trampas del lado del emisor que cuestan una tarde cada una, por si te
tocan:

- `openid4vci-kt` **descarta la metadata entera** si `proof_types_supported`
  no lleva `key_attestations_required`, aunque OpenID4VCI §12.2.4 lo marque
  OPTIONAL. No hay forma de publicar metadata legible sin declarar key
  attestation.
- Una prueba `jwt` **con** key attestation no lleva su clave en la cabecera:
  ni `jwk`, ni `x5c`, ni DID. Solo `key_attestation` y un `kid` que es el
  **índice** dentro de `attested_keys`. Las librerías de emisor que solo
  contemplan `jwk`/`x5c` fallan con *«Signer method 'custom' not supported»*.

---

## 6. El orden en que conviene montarlo

Cada paso se valida solo, y así el fallo siempre está en lo último que tocaste:

1. **Las cuatro listas**, con `relax` puestos y registro desactivado. Pásalas
   por el parser real antes de compilar nada.
2. **Emisión sin firmar**, con `preferSignedMetadata()`. Comprueba que llegas
   al token.
3. **Metadatos firmados** y `requireSignedMetadata()`. Aquí aparece el vínculo
   con el certificado de acceso.
4. **Certificados de registro**, activando las dos políticas.
5. **Key attestation**, si emites PID. Exigirla obliga al emisor a validarla.
6. **Quitar los `relax`**, uno a uno, con evidencia de que ya no hacen falta.

---

## 7. Resumen

| | |
|---|---|
| **Obligatorio** | las cuatro `loteLocations`; `walletProviderHost` |
| **Te va a morder** | `requireSignedMetadata` (el vínculo del certificado de acceso); la política de credencial del PID (`OnceOnly` por defecto); `clientId` = `sub` de la WIA |
| **Desactivable** | los dos `relax`; las dos políticas de registro; reader auth; el trust del status resolver |
| **No está en la wallet** | key attestation, tamaño de lote, política de reutilización, lista de proveedores de wallet |

Para el detalle de este fork concreto —qué se cambió, con qué evidencia y qué
queda pendiente— está [`espuni-fork.md`](./espuni-fork.md) §4.
