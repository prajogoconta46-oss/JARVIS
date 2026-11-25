# JARVIS — Arquitetura e plano de implementação

**Nome:** JARVIS — Assistente Inteligente  
**Idioma:** Português do Brasil  
**Dispositivos:** Windows e Redmi 13C (Android)  

Este documento define a arquitetura incremental para transformar o protótipo atual em assistente com ferramentas reais. Recursos são considerados concluídos somente após implementação e testes; itens deste plano não são promessas de recursos já existentes.

## Arquitetura

- **Interface:** painel escuro responsivo, chat, avatar/estado (idle, ouvindo, processando, falando), histórico e páginas PC, Android, rede, automações e segurança.
- **Orquestrador:** interpreta pedidos, escolhe ferramentas permitidas, verifica requisitos e apresenta resultado/erro. Nunca executa texto arbitrário do modelo como shell.
- **Agente Windows (Python):** módulos isolados para métricas (psutil), apps/URLs, arquivos, áudio, Defender/Firewall, rede, tarefas e diagnósticos.
- **Agente Android (Kotlin):** APIs oficiais Android; acesso limitado às permissões e políticas do sistema. BatteryManager, StorageManager/StatFs, ConnectivityManager, PackageManager (respeitando visibilidade de apps), intents de Settings e SpeechRecognizer quando disponível.
- **Ponte local:** pairing explícito, QR/código de pareamento, TLS, autenticação por tokens rotativos, comandos permitidos, confirmação no dispositivo receptor, desconexão e revogação.
- **IA:** interface de provedor separada para IA online e modelo local (quando configurado). A chave de API deve ficar fora do Git, no armazenamento local protegido ou backend apropriado. Contexto enviado ao provedor deve ser transparente e consentido.
- **Memória:** preferências não sensíveis e projetos em armazenamento local, com controles para visualizar/exportar/apagar. Não armazenar segredos.
- **Auditoria:** registros locais de ações e resultado, com minimização de dados e opção de limpar histórico.

## Regras de segurança obrigatórias

1. Comandos shell não são construídos diretamente a partir da resposta da IA.
2. Ferramentas devem usar lista de ações e argumentos validados.
3. Ações destrutivas, alteração de segurança, instalações, limpeza, movimentação/remoção de arquivos e comandos administrativos exigem confirmação explícita.
4. Ações elevadas (SFC/DISM, Defender, firewall) explicam o efeito e pedem confirmação; solicitar elevação Windows somente quando necessária.
5. Nunca guardar ou transmitir senha, tokens, mensagens ou arquivos privados sem autorização.
6. A ponte não deve abrir portas públicas nem usar uma senha fixa. Preferir rede local e TLS; impedir comandos de terceiros e replay.
7. Android: respeitar APIs, permissões, limites de execução em background e restrições de apps; não contornar bloqueios.
8. Exibir erro, indisponibilidade e limitação reais, sem inventar métricas (temperatura pode não ser exposta).

## Plano por fases

### Fase 1 — Base segura Windows
- Separar UI, comandos, monitoramento, voz e configuração em módulos.
- Métricas de CPU/RAM/disco/processos e inicialização, somente leitura.
- Abrir apps/Settings/URLs e volume com APIs documentadas.
- Pesquisa local de arquivos com escopo e limite configuráveis.
- Relatórios locais; rotinas de arquivo com prévia e confirmação.
- Estado Defender/Firewall, rede e diagnósticos; operações administrativas com confirmação.

### Fase 2 — Conversação, IA e voz
- Provedor online configurável e opção de modelo local.
- Conversação com contexto limitado, preferências locais, explicação das ferramentas e confirmação.
- Reconhecimento de voz por interface suportada no Windows, push-to-talk inicialmente; palavra-chave opcional depois.
- TTS configurável, silenciar/interromper, histórico local.

### Fase 3 — Android
- Painel de bateria/estado de carregamento, armazenamento, device info e conectividade.
- Abertura de apps disponíveis e páginas de configurações via intents.
- Lembretes locais, notificações e monitoramento somente com permissões apropriadas.
- Diagnóstico e recomendações; sem alegar otimização de bateria ou controle privilegiado não permitido.

### Fase 4 — Pairing e comunicação
- Pareamento QR/ código de uso único, validação em ambos os dispositivos.
- TLS e tokens de dispositivo; renovação, revogação e status de conexão.
- API de ferramentas allowlist, payloads tipados, timestamps e prevenção de replay.
- Ações remotas requerem confirmação no dispositivo alvo; teste primeiro em LAN.

### Fase 5 — Interface, automações e estabilidade
- Dashboard, eventos, tarefas, alertas, dispositivos e histórico.
- Scheduler de tarefas com cancelamento e limites; lembretes.
- Testes unitários, logs rotativos, tratamento de falhas, instaladores e documentação.

## Status do protótipo atual

O repositório possui protótipo Python para Windows e protótipo Kotlin Android, com builds via GitHub Actions. O PC atualmente tem chat simples, fala opcional TTS, métricas básicas, listagem de processos e abertura de alguns aplicativos/URLs. Ainda não possui IA generativa, entrada de voz no PC ou ponte PC-Android implementada. O protótipo Android ainda não implementa o conjunto completo de ferramentas ou comunicação remota. Consulte o README e os builds antes de considerar qualquer recurso operacional.
