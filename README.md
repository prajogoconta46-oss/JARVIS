# JARVIS — Assistente PC + Android

Protótipo em português para Windows e Android, com builds automatizados no GitHub Actions.

## Windows
A interface do PC é um chat sem botões de atalho. Digite comandos e pressione Enter. Respostas podem ser faladas localmente se a biblioteca de voz estiver disponível.

Comandos suportados no protótipo:
- "diagnóstico do PC" — CPU, RAM, disco e bateria quando detectada.
- "mostrar processos" — lista processos com maior consumo de memória (somente leitura).
- "abre calculadora", "abre bloco de notas", "abre explorador".
- "Downloads", "Configurações", "abre YouTube", "Google".
- "pesquisa na internet [termo]" — abre pesquisa no navegador.
- "que horas são", "que dia é hoje".

Ainda não é uma IA generativa conectada e não possui escuta de microfone. Não execute tarefas destrutivas automaticamente.

### Executar localmente
1. Instale Python 3.11.
2. Na pasta do projeto: `pip install -r pc/requirements.txt`.
3. Execute: `python pc/jarvis.py`.

## Build Windows
No GitHub, abra **Actions** → **JARVIS builds** → escolha uma execução concluída → baixe o artifact **JARVIS-Windows** e extraia o `JARVIS.exe`.

## Android
O protótipo usa Kotlin, Android SDK 35, AGP 8.7.3 e Gradle 8.9. Oferece informações básicas do dispositivo, bateria, configurações e reconhecimento de voz experimental; a fala reconhecida não executa comandos.

## Segurança e limites
As ferramentas atuais são locais, simples e não executam comandos arbitrários digitados. O monitor de processos é apenas leitura. A pesquisa abre o navegador. Integração com IA, microfone no PC e controle entre PC e celular ainda não estão implementados.
