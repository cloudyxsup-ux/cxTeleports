# cxTeleports
O cxTeleport, desenvolvido pela CloudyX, é o sistema de teleporte definitivo para servidores Paper, Purpur e Folia. Com suporte a Folia, TPA com histórico e bloqueio, casas com limite por permissão, warps com categorias, spawn por mundo, RTP com fila e zonas, prisão, proteção PvP, warmup, Vault e placeholders via PlaceholderAPI.

# CXTeleport

<p align="center">
  <strong>O sistema de teleporte definitivo para servidores Paper, Purpur e Folia.</strong>
</p>

<p align="center">
  Desenvolvido para oferecer desempenho máximo, segurança avançada e uma experiência completa para servidores Survival, SMP, SkyBlock, Lifesteal, Prison e Networks.
</p>

---

## Recursos

### Suporte ao Folia
- Compatível com Paper, Purpur e Folia
- Teleportes assíncronos e seguros
- Agendamento otimizado para grandes servidores
- Fluxos de teleporte preparados para ambientes multithread
- Detecção automática do Folia com schedulers regionais

### Sistema de Teleporte entre Jogadores
- Solicitações de teleporte (`/tpa`)
- Solicitações para trazer jogadores (`/tpaaqui`)
- Aceitar ou recusar solicitações
- Cancelamento de solicitações pendentes
- Histórico de teletransportes
- Bloqueio de jogadores
- Sistema de autoaceitar
- Configurações individuais por jogador
- Interface gráfica opcional (GUI)

### Segurança de Teleporte
- Tempo de espera configurável (Warmup)
- Cancelamento ao se mover
- Cancelamento ao tomar dano
- Proteção temporária após teleporte
- Verificação de local seguro
- Bloqueio durante combate (PvP)
- Prevenção contra teleporte para locais perigosos
- Efeitos visuais e sonoros ao teleportar

### Sistema de Casas
- Casas ilimitadas ou limitadas por grupo
- Cooldowns configuráveis
- Custos via Vault
- Interface gráfica de gerenciamento
- Permissões individuais (`cxteleport.home.limit.N`)
- Armazenamento em JSON por jogador

### Sistema de Warps
- Criar, editar e remover warps
- Custos opcionais para utilização
- Interface gráfica moderna
- Categorias de warps
- Permissões individuais por warp
- Efeitos personalizados ao teleportar

### Sistema de Spawn
- Spawn global ou por mundo
- Suporte ao primeiro login
- Spawn para outros jogadores
- Configuração avançada de respawn

### Sistema de Retorno
- Retorne ao local da última morte
- Retorne ao local do último teleporte
- Cooldowns configuráveis
- Custos opcionais
- Integração com proteção PvP

### RTP (Random Teleport)
- Teleporte aleatório por mundo
- Regiões personalizadas
- Blacklist de blocos e biomas
- Sistema de fila
- Efeitos visuais e sonoros
- GUI de seleção de mundo
- Busca assíncrona de local seguro

### RTP por Zona
- Regiões específicas para RTP
- Contagem regressiva personalizada
- Mensagens customizadas
- Restrições avançadas

### Teleporte entre Servidores
- Compatível com Velocity e BungeeCord
- Solicitações TPA entre servidores
- Experiência unificada em networks

### Sistema de Prisão
- Prender e libertar jogadores
- Local configurável da prisão
- Bloqueio de comandos enquanto preso
- Bloqueio de teleporte enquanto preso
- Controle administrativo avançado

### Integração com PlaceholderAPI
- Cooldowns
- Proteção PvP
- Status de prisão
- Contagem de warps
- Jogadores por mundo
- Estatísticas de teleporte

---

## Compatibilidade

| Plataforma | Suporte |
|-----------|---------|
| Paper 1.20+ | ✅ Completo |
| Purpur 1.20+ | ✅ Completo |
| Folia | ✅ Completo |
| Spigot | ❌ Não suportado |

| Dependência | Obrigatório | Função |
|------------|-------------|--------|
| PlaceholderAPI | ❌ | Placeholders |
| Vault | ❌ | Economia (custos) |

---

## Instalação

1. Baixe a versão mais recente nas [Releases](https://github.com/seu-usuario/CXTeleport/releases)
2. Coloque o arquivo `.jar` na pasta `plugins/` do seu servidor
3. Reinicie o servidor
4. Configure os arquivos `config.yml` e `messages.yml` conforme necessário
5. Use `/cxteleport reload` para recarregar sem reiniciar

---

## Comandos

### Teleporte entre Jogadores

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/teleportar <jogador>` | Solicita teleporte para um jogador | `cxteleport.tpa` |
| `/tpaaqui <jogador>` | Solicita que um jogador venha até você | `cxteleport.tpahere` |
| `/tpaceitar [jogador]` | Aceita uma solicitação | `cxteleport.tpaccept` |
| `/tprecusar [jogador]` | Recusa uma solicitação | `cxteleport.tpdeny` |
| `/tpcancelar` | Cancela solicitações pendentes | `cxteleport.tpcancel` |
| `/toggletpa` | Ativa/desativa solicitações de teleporte | `cxteleport.toggle` |
| `/toggletpaaqui` | Ativa/desativa solicitações para vir até você | `cxteleport.toggle` |
| `/tpaauto` | Ativa/desativa autoaceitação | `cxteleport.tpaauto` |
| `/tpahistorico [página]` | Exibe histórico de solicitações | `cxteleport.tpahistory` |
| `/tpbloquear <jogador>` | Bloqueia solicitações de um jogador | `cxteleport.tpblock` |
| `/tpdesbloquear <jogador>` | Remove o bloqueio de um jogador | `cxteleport.tpblock` |

### Casas

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/casa <nome>` | Teleporta para uma casa | `cxteleport.home` |
| `/setcasa <nome>` | Define uma nova casa | `cxteleport.sethome` |
| `/delcasa <nome>` | Remove uma casa | `cxteleport.delhome` |
| `/casas` | Lista todas as casas (GUI) | `cxteleport.homes` |

### Warps

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/warp <nome>` | Teleporta para uma warp | `cxteleport.warp` |
| `/setwarp <nome>` | Cria uma warp | `cxteleport.setwarp` |
| `/delwarp <nome>` | Remove uma warp | `cxteleport.delwarp` |
| `/warps` | Lista todas as warps (GUI) | `cxteleport.warps` |

### Spawn

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/spawn [jogador]` | Teleporta para o spawn | `cxteleport.spawn` |
| `/setspawn` | Define o spawn | `cxteleport.setspawn` |

### RTP

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/rtp [mundo]` | Teleporte aleatório | `cxteleport.rtp` |
| `/rtpfila` | Entra/sai da fila de RTP | `cxteleport.rtp.queue` |

### Retorno

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/voltar` | Retorna para a última localização | `cxteleport.back` |

### Administração

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/tp <jogador>` | Teleporta até um jogador | `cxteleport.tp` |
| `/tphere <jogador>` | Puxa um jogador até você | `cxteleport.tphere` |
| `/tppos <x> <y> <z> [mundo]` | Teleporta para coordenadas | `cxteleport.tppos` |
| `/tpsubir <blocos>` | Teleporta para cima | `cxteleport.tpup` |
| `/tptodos` | Teleporta todos os jogadores | `cxteleport.tpall` |
| `/tplog [página]` | Histórico de teletransportes | `cxteleport.tplog` |
| `/prender <jogador>` | Prende um jogador | `cxteleport.jail` |
| `/soltar <jogador>` | Liberta um jogador | `cxteleport.unjail` |
| `/cxteleport reload` | Recarrega as configurações | `cxteleport.reload` |
| `/cxteleport stats` | Estatísticas do plugin | `cxteleport.admin` |
| `/cxteleport setjail` | Define o local da prisão | `cxteleport.admin` |
| `/cxteleport help` | Lista de ajuda | `cxteleport.admin` |

---

## Permissões

### Jogadores
| Permissão | Descrição | Padrão |
|-----------|-----------|--------|
| `cxteleport.tpa` | Usar /tpa | `true` |
| `cxteleport.tpahere` | Usar /tpaaqui | `true` |
| `cxteleport.tpaccept` | Aceitar solicitações | `true` |
| `cxteleport.tpdeny` | Recusar solicitações | `true` |
| `cxteleport.tpcancel` | Cancelar solicitações | `true` |
| `cxteleport.toggle` | Toggle TPA | `true` |
| `cxteleport.tpaauto` | Autoaceitar | `true` |
| `cxteleport.tpahistory` | Ver histórico | `true` |
| `cxteleport.tpblock` | Bloquear jogadores | `true` |
| `cxteleport.home` | Usar /casa | `true` |
| `cxteleport.sethome` | Definir casas | `true` |
| `cxteleport.delhome` | Deletar casas | `true` |
| `cxteleport.homes` | Listar casas | `true` |
| `cxteleport.warp` | Usar /warp | `true` |
| `cxteleport.warps` | Listar warps | `true` |
| `cxteleport.spawn` | Usar /spawn | `true` |
| `cxteleport.rtp` | Usar /rtp | `true` |
| `cxteleport.rtp.queue` | Usar fila RTP | `true` |
| `cxteleport.back` | Usar /voltar | `true` |
| `cxteleport.back.death` | Voltar ao local da morte | `true` |

### Casas - Limites por Permissão
| Permissão | Descrição | Padrão |
|-----------|-----------|--------|
| `cxteleport.home.unlimited` | Casas ilimitadas | `false` |
| `cxteleport.home.limit.1` | Limite de 1 casa | `false` |
| `cxteleport.home.limit.3` | Limite de 3 casas | `false` |
| `cxteleport.home.limit.5` | Limite de 5 casas | `false` |
| `cxteleport.home.limit.10` | Limite de 10 casas | `false` |

> Para adicionar mais limites, use `cxteleport.home.limit.N` onde N é o número máximo de casas.

### Administração
| Permissão | Descrição | Padrão |
|-----------|-----------|--------|
| `cxteleport.tp` | Teleportar até jogadores | `op` |
| `cxteleport.tphere` | Puxar jogadores | `op` |
| `cxteleport.tppos` | Teleportar para coordenadas | `op` |
| `cxteleport.tpup` | Teleportar para cima | `op` |
| `cxteleport.tpall` | Teleportar todos | `op` |
| `cxteleport.tplog` | Ver histórico admin | `op` |
| `cxteleport.jail` | Prender jogadores | `op` |
| `cxteleport.unjail` | Libertar jogadores | `op` |
| `cxteleport.setwarp` | Criar warps | `op` |
| `cxteleport.delwarp` | Deletar warps | `op` |
| `cxteleport.setspawn` | Definir spawn | `op` |
| `cxteleport.spawn.others` | Teleportar outros ao spawn | `op` |
| `cxteleport.admin` | Comandos administrativos | `op` |
| `cxteleport.reload` | Recarregar configurações | `op` |

### Bypass
| Permissão | Descrição | Padrão |
|-----------|-----------|--------|
| `cxteleport.bypass.cooldown` | Ignora todos os cooldowns | `false` |
| `cxteleport.bypass.warmup` | Ignora warmup de teleporte | `false` |
| `cxteleport.bypass.combat` | Ignora bloqueio de combate | `false` |
| `cxteleport.bypass.jail` | Ignora bloqueio de prisão | `false` |

---

## Placeholders

Requer [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).

| Placeholder | Descrição |
|-------------|-----------|
| `%cxteleport_tpa_cooldown%` | Tempo restante do cooldown de TPA |
| `%cxteleport_tpa_cooldown_seconds%` | Alias do cooldown TPA em segundos |
| `%cxteleport_back_cooldown%` | Tempo restante do cooldown do /voltar |
| `%cxteleport_back_cooldown_seconds%` | Alias do cooldown /voltar em segundos |
| `%cxteleport_warp_count%` | Quantidade total de warps registradas |
| `%cxteleport_pvp_protection%` | Tempo restante da proteção PvP |
| `%cxteleport_pvp_protection_seconds%` | Alias da proteção PvP em segundos |
| `%cxteleport_jailed%` | Retorna `true` ou `false` se o jogador está preso |
| `%cxteleport_world_players_(world)%` | Jogadores online em um mundo específico |
| `%cxteleport_world_players_current%` | Jogadores online no mundo atual |

---

## Configuração

### config.yml

O arquivo de configuração principal contém todas as opções disponíveis:

```yaml
# Idioma das mensagens
language: pt-BR

# Suporte ao Folia (detecção automática)
folia:
  auto-detect: true
  regional-schedulers: true

# Configurações gerais de teleporte
teleport:
  warmup: 5                    # Segundos de espera
  cancel-on-move: true         # Cancelar ao se mover
  cancel-on-damage: true       # Cancelar ao tomar dano
  invulnerability: 5           # Proteção após teleporte (segundos)
  check-safe-location: true    # Verificar local seguro
  sound: ENTITY_ENDERMAN_TELEPORT
  particles: PORTAL

# Proteção PvP
pvp-protection:
  enabled: true
  duration: 15                 # Duração em segundos
  combat-cooldown: 15          # Bloqueio após combate
  cancel-on-attack: true       # Cancelar ao atacar

# Sistema de Casas
homes:
  default-limit: 3
  use-permission-limits: true
  cooldown: 10
  set-cost: 0.0                # Custo via Vault
  teleport-cost: 0.0
  gui-enabled: true

# Sistema de RTP
rtp:
  min-distance: 500
  max-distance: 5000
  cooldown: 300
  max-attempts: 50
  queue:
    enabled: true
    max-size: 50
```

### messages.yml

Todas as mensagens são totalmente configuráveis com suporte a cores hexadecimais (`&#RRGGBB`) e placeholders:

```yaml
prefix: "&8[&bCXTeleport&8] &r"
tpa:
  sent: "&aSolicitação de teleporte enviada para &e{target}&a."
  received: "&e{player} &adeseja se teleportar até você."
homes:
  teleported: "&aTeleportado para casa &e{home}&a!"
  limit-reached: "&cVocê atingiu o limite de &e{limit} &ccasas!"
```

---

## Compilação

### Requisitos
- **JDK 17** ou superior
- **Maven 3.8+**

### Build

```bash
git clone https://github.com/seu-usuario/CXTeleport.git
cd CXTeleport
mvn clean package
```

O arquivo compilado estará em `target/CXTeleport-1.0.0.jar`.

---

## Arquitetura

```
CXTeleport/
├── src/main/java/com/cxteleport/
│   ├── CXTeleport.java              # Classe principal
│   ├── scheduler/
│   │   └── SchedulerAdapter.java    # Abstração Folia/Paper
│   ├── model/
│   │   ├── PlayerData.java          # Dados por jogador
│   │   ├── HomeData.java            # Modelo de casa
│   │   ├── WarpData.java            # Modelo de warp
│   │   ├── TPARequest.java          # Solicitação TPA
│   │   ├── TPAHistoryEntry.java     # Histórico TPA
│   │   ├── RTPZone.java             # Zona de RTP
│   │   └── TeleportEntry.java       # Log de teleporte
│   ├── manager/
│   │   ├── CooldownManager.java     # Sistema de cooldowns
│   │   ├── PlayerDataManager.java   # Gerenciamento de dados
│   │   ├── HomeManager.java         # Sistema de casas
│   │   ├── WarpManager.java         # Sistema de warps
│   │   ├── SpawnManager.java        # Sistema de spawn
│   │   ├── TPAManager.java          # Sistema de TPA
│   │   ├── TeleportManager.java     # Teleporte com warmup
│   │   ├── JailManager.java         # Sistema de prisão
│   │   ├── RTPManager.java          # Sistema de RTP
│   │   └── CrossServerManager.java  # BungeeCord/Velocity
│   ├── commands/
│   │   ├── BaseCommand.java         # Comando base abstrato
│   │   ├── tpa/                     # 11 comandos TPA
│   │   ├── home/                    # 4 comandos de casas
│   │   ├── warp/                    # 4 comandos de warps
│   │   ├── spawn/                   # 2 comandos de spawn
│   │   ├── rtp/                     # 2 comandos de RTP
│   │   ├── back/                    # 1 comando de retorno
│   │   └── admin/                   # 8 comandos admin
│   ├── gui/
│   │   ├── HomesGUI.java            # GUI de casas
│   │   ├── WarpsGUI.java            # GUI de warps
│   │   └── RTPGUI.java              # GUI de RTP
│   ├── listener/
│   │   ├── JoinListener.java        # Evento de login
│   │   ├── DeathListener.java       # Evento de morte
│   │   ├── MoveListener.java        # Cancelar warmup ao mover
│   │   ├── CombatListener.java      # Sistema de combate
│   │   ├── ProtectionListener.java  # Bloqueio de prisão
│   │   └── GUIListener.java         # Clique nas GUIs
│   ├── hook/
│   │   ├── PAPIExpansion.java       # PlaceholderAPI
│   │   └── VaultHook.java           # Vault Economy
│   └── util/
│       ├── ConfigUtil.java          # Utilitário de config
│       ├── MessageUtil.java         # Cores, placeholders, hex
│       └── SafeLocationUtil.java    # Verificação de local seguro
└── src/main/resources/
    ├── plugin.yml
    ├── config.yml
    └── messages.yml
```

---

## Armazenamento

| Tipo | Dados | Formato |
|------|-------|---------|
| Casas | `plugins/CXTeleport/data/homes/<uuid>.json` | JSON |
| Warps | `plugins/CXTeleport/data/warps.json` | JSON |
| Spawn | `plugins/CXTeleport/data/spawns.yml` | YAML |
| Prisão | `plugins/CXTeleport/data/jail.yml` | YAML |

---

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

