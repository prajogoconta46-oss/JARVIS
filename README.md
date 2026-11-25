# JARVIS — Assistente PC + Android

Protótipo inicial em português: agente desktop para Windows e aplicativo Android, com builds automatizados.

## Builds
A cada push na branch `main`, GitHub Actions compila APK debug e executável Windows. Abra **Actions** → **JARVIS builds** → execução concluída → **Artifacts**. Também é possível iniciar manualmente pela opção **Run workflow**.

## Windows (execução local)
1. Instale Python 3.11.
2. Na pasta do projeto: `pip install -r pc/requirements.txt`.
3. Execute `python pc/jarvis.py`.

O painel oferece diagnóstico de CPU, RAM e disco e atalhos de programas.

## Android
O projeto usa Kotlin, Android SDK 35, AGP 8.7.3 e Gradle 8.9. O app fornece informações do dispositivo, armazenamento, bateria, acesso às configurações e uma tentativa de reconhecimento de voz (a fala é exibida, não executa comandos).

## Segurança
Não há controle remoto nem execução de comandos arbitrários. Builds debug são para testes. Integrações futuras devem exigir autenticação, consentimento e criptografia.
