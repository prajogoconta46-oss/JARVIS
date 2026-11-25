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
A versão Android Ultra usa Kotlin, Android SDK 35 e oferece chat local por texto, reconhecimento de voz opcional, leitura de respostas por TTS, diagnóstico de bateria/RAM/armazenamento/estado térmico, auditoria básica de segurança, abertura de aplicações, atalhos para Wi-Fi/Bluetooth/DNS, seletor de ficheiros, lembretes locais e acesso opcional a metadados de notificações e última localização conhecida. A localização e o acesso a notificações exigem autorização do utilizador.

## Segurança e limites
As ferramentas atuais são locais, simples e não executam comandos arbitrários digitados. O monitor de processos é apenas leitura. A pesquisa abre o navegador. Integração com IA, microfone no PC e controle entre PC e celular ainda não estão implementados.

## Evolução planejada

A arquitetura, fases de desenvolvimento, limitações e regras de segurança estão documentadas em [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md). O plano separa agente Windows, agente Android, IA, voz, memória local e comunicação autenticada. Os itens descritos como planejados não estão implementados até que sejam desenvolvidos e testados.

## IA online para Windows (opcional)

O agente Windows agora suporta conversa com uma API compatível com o formato OpenAI Chat Completions. A IA é opcional e não funciona até você configurar uma chave válida. A chave não deve ser incluída no código, em commits, capturas de tela ou compartilhada em chats.

No Windows, configure variáveis de ambiente (substitua pela sua chave privada, sem compartilhar):

1. Abra Iniciar e procure **Editar variáveis de ambiente para sua conta**.
2. Crie uma variável chamada `JARVIS_API_KEY` e defina o valor da chave do seu provedor.
3. Opcional: crie `JARVIS_AI_MODEL` para escolher um modelo suportado pelo provedor (padrão: `gpt-4o-mini`).
4. Opcional: crie `JARVIS_AI_URL` para definir o endpoint compatível (padrão: `https://api.openai.com/v1/chat/completions`).
5. Feche e reabra o terminal/app para carregar as variáveis. Reinicie o JARVIS.

A conversa comum enviada à IA online é transmitida ao provedor configurado. Não envie informações privadas se não desejar compartilhá-las. Esta primeira integração é conversacional; o modelo ainda não aciona ferramentas de PC automaticamente. Comandos locais reconhecidos continuam sendo tratados pelo código local.
