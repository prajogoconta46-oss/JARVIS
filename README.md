# JARVIS — Assistente inteligente PC + Android

Projeto inicial para um assistente pessoal em português, com agente Windows e aplicativo Android. O CI compila APK e pacote Windows automaticamente.

## Compilação automática
1. Abra a aba **Actions**.
2. Abra **JARVIS builds** e clique **Run workflow** (ou faça push).
3. Baixe os artefatos **JARVIS-Android-APK** e **JARVIS-Windows** após execução verde.

## Windows
Python 3.11+: `pip install -r pc/requirements.txt`, depois `python pc/jarvis.py`. Diagnóstico básico e atalhos com lista limitada.

## Android
Kotlin, Android SDK 35 e Gradle/AGP. APK gerado pelo GitHub Actions.

## Segurança / escopo
Protótipo inicial. Não inclui acesso remoto nem execução remota de comandos. Futuras integrações devem usar autenticação, consentimento e criptografia.
