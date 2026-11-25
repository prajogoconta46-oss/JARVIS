# Estrutura do Android

O aplicativo Android mantém o identificador `com.jarvis.assistant` e usa o código upstream do Open Jarvis como submódulo Git.

## Pastas

- `android/app/build.gradle.kts`: configuração do app, SDK, Java/Kotlin, Compose e dependências.
- `android/app/src/main/AndroidManifest.xml`: manifesto de integração do JARVIS, atividades e serviços.
- `android/open-jarvis/`: código-fonte e recursos do projeto upstream `tokenarc/open-jarvis`; não editar uma cópia paralela desse código nesta pasta.
- `android/settings.gradle.kts`: configuração dos módulos Gradle.
- `android/gradle.properties`: opções de compilação.
- `.github/workflows/build.yml`: compilação automatizada do APK e do executável Windows.
- `docs/`: documentação técnica do projeto.

## Clonar corretamente

O submódulo é necessário para compilar o Android. Clone o repositório com:

```bash
git clone --recurse-submodules https://github.com/prajogoconta46-oss/JARVIS.git
```

Se o repositório já estiver clonado:

```bash
git submodule update --init --recursive
```

## Compilar Android

Com JDK 17 e Android SDK instalados, execute:

```bash
cd android
gradle --no-daemon clean :app:compileDebugKotlin :app:assembleDebug
```

O APK de depuração é gerado em `android/app/build/outputs/apk/debug/app-debug.apk`.

## Dependências de tema

O projeto usa Compose Material3 e também temas XML Material Components. São bibliotecas diferentes: `androidx.compose.material3:material3` atende os componentes Compose; `com.google.android.material:material` fornece os estilos XML `Theme.Material3.*` e atributos Material usados pelos recursos do submódulo.

## Regras de manutenção

- Manter o código upstream dentro do submódulo; não copiar classes para outra pasta.
- Alterar permissões e serviços no manifesto de integração e confirmar os serviços declarados pelo upstream.
- Fixar versões de plugins e dependências; evitar versões dinâmicas.
- Validar `clean`, `compileDebugKotlin` e `assembleDebug` no GitHub Actions antes de considerar o APK pronto.
