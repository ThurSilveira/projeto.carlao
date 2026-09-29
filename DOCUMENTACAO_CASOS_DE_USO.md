# Documentação de Casos de Uso - Escala Ministerial

Este documento detalha os Casos de Uso do sistema Escala Ministerial, os Atores envolvidos e apresenta o diagrama UML de Casos de Uso.

## Atores

O sistema possui diferentes perfis de acesso, que são representados como atores no diagrama de Casos de Uso, além de integrações externas:

1.  **Administrador:** Possui acesso integral ao sistema. É responsável por gerenciar os usuários do sistema, perfis de acesso, além de ter permissão para visualizar auditoria e todas as funcionalidades de gestão (Coordenador).
2.  **Coordenador:** Gerencia a operação ministerial. É responsável pelo CRUD de ministros, eventos, escalas, feedbacks e visualização de indisponibilidades. Pode também consultar a auditoria do sistema.
3.  **Consulta:** Acesso de apenas leitura aos dados operacionais. Pode visualizar ministros, eventos, escalas e feedbacks, sem a capacidade de alterá-los ou visualizar a auditoria.
4.  **Ministro:** Usuário que atua nos eventos. Tem acesso restrito através do seu "Portal do Ministro" para visualizar seu próprio calendário de escalas, gerenciar suas indisponibilidades (avisar quando não pode participar) e gerenciar/visualizar seus feedbacks.
5.  **Sistema (Google Calendar):** Ator sistêmico/externo que interage com a aplicação enviando convites para os eventos quando as escalas são aprovadas.

## Diagrama de Casos de Uso (UML)

```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle

actor "Administrador" as admin
actor "Coordenador" as coord
actor "Consulta" as consulta
actor "Ministro" as ministro
actor "Google Calendar" as gcal <<System>>

rectangle "Escala Ministerial" {

  ' Casos de Uso do Ministro
  usecase "Acessar Portal do Ministro" as UC_Portal
  usecase "Visualizar Calendário Pessoal" as UC_Ver_Calendario
  usecase "Gerenciar Indisponibilidades" as UC_Gerenciar_Indisp
  usecase "Visualizar/Responder Feedbacks" as UC_Gerenciar_Feedbacks

  ' Casos de Uso de Consulta
  usecase "Visualizar Ministros" as UC_Ver_Ministros
  usecase "Visualizar Eventos" as UC_Ver_Eventos
  usecase "Visualizar Escalas" as UC_Ver_Escalas
  usecase "Visualizar Feedbacks" as UC_Ver_Feedbacks_Geral

  ' Casos de Uso do Coordenador
  usecase "Gerenciar Ministros" as UC_Gerenciar_Ministros
  usecase "Gerenciar Eventos" as UC_Gerenciar_Eventos
  usecase "Gerenciar Escalas" as UC_Gerenciar_Escalas
  usecase "Gerar Escala Automática" as UC_Gerar_Escala
  usecase "Aprovar Escala" as UC_Aprovar_Escala
  usecase "Realizar Substituição" as UC_Substituir
  usecase "Gerenciar Feedbacks" as UC_Gerenciar_Feedbacks_Geral
  usecase "Consultar Auditoria" as UC_Consultar_Auditoria

  ' Casos de Uso do Administrador
  usecase "Gerenciar Usuários" as UC_Gerenciar_Usuarios
  usecase "Gerenciar Perfis de Acesso" as UC_Gerenciar_Perfis
  usecase "Configurar Integrações" as UC_Configurar
}

' Relacionamentos - Ministro
ministro --> UC_Portal
UC_Portal ..> UC_Ver_Calendario : <<include>>
UC_Portal ..> UC_Gerenciar_Indisp : <<include>>
UC_Portal ..> UC_Gerenciar_Feedbacks : <<include>>

' Herança de Atores (Hierarquia de Permissões)
admin -|> coord
coord -|> consulta

' Relacionamentos - Consulta
consulta --> UC_Ver_Ministros
consulta --> UC_Ver_Eventos
consulta --> UC_Ver_Escalas
consulta --> UC_Ver_Feedbacks_Geral

' Relacionamentos - Coordenador
coord --> UC_Gerenciar_Ministros
coord --> UC_Gerenciar_Eventos
coord --> UC_Gerenciar_Escalas
coord --> UC_Gerenciar_Feedbacks_Geral
coord --> UC_Consultar_Auditoria

' Casos de uso estendidos ou inclusos na gestão de escalas
UC_Gerenciar_Escalas ..> UC_Gerar_Escala : <<extend>>
UC_Gerenciar_Escalas ..> UC_Aprovar_Escala : <<extend>>
UC_Gerenciar_Escalas ..> UC_Substituir : <<extend>>

' Integração Google Calendar
UC_Aprovar_Escala ..> gcal : <<communicate>>

' Relacionamentos - Administrador
admin --> UC_Gerenciar_Usuarios
admin --> UC_Gerenciar_Perfis
admin --> UC_Configurar

@enduml
```

## Descrição dos Casos de Uso

### Grupo: Ministro

*   **Acessar Portal do Ministro:** Autenticação no sistema para acessar funcionalidades exclusivas da própria conta.
*   **Visualizar Calendário Pessoal:** Visualizar os eventos em que o ministro foi escalado.
*   **Gerenciar Indisponibilidades:** Registrar, editar ou excluir datas em que o ministro não poderá participar de eventos. Esta informação é utilizada na geração de escalas para evitar conflitos.
*   **Visualizar/Responder Feedbacks:** Visualizar feedbacks recebidos pelas participações e responder ou comentar os mesmos.

### Grupo: Consulta

*   **Visualizar Ministros, Eventos, Escalas e Feedbacks:** Acesso de apenas leitura aos dados. Permite a visualização das listas, detalhes e históricos, mas sem permissão de alteração (CRUD).

### Grupo: Coordenador

*   **Gerenciar Ministros:** Adicionar (com criação de conta de usuário atrelada), editar, desativar ou remover cadastro de ministros do sistema.
*   **Gerenciar Eventos:** Criar, editar, cancelar e excluir eventos da agenda.
*   **Gerenciar Escalas:** Visualizar, criar manualmente, cancelar ou deletar escalas de eventos.
    *   **Gerar Escala Automática (<<extend>>):** O sistema utiliza as regras de negócio (disponibilidade dos ministros, histórico, etc.) para propor uma escala para o evento.
    *   **Realizar Substituição (<<extend>>):** Em caso de necessidade (como uma indisponibilidade de última hora), permite trocar um ministro escalado por outro, validando disponibilidade.
    *   **Aprovar Escala (<<extend>>):** Confirma a escala, tornando-a oficial. Este caso de uso aciona a integração com o Google Calendar.
*   **Gerenciar Feedbacks:** Criar feedbacks sobre as participações dos ministros, editar e excluir registros de avaliação geral do evento.
*   **Consultar Auditoria:** Acessar logs do sistema que registram quem fez o que e quando, para fins de rastreabilidade.

### Grupo: Administrador

*   **Gerenciar Usuários:** Criar, editar, desativar ou excluir (se permitido) contas de usuários administrativos no sistema, gerindo senhas e status de ativação.
*   **Gerenciar Perfis de Acesso:** Atribuir os perfis (Administrador, Coordenador, Consulta, Ministro) aos usuários, definindo suas permissões no sistema. (Herança: o Administrador também pode fazer todas as ações do Coordenador).
*   **Configurar Integrações:** Parametrizar dados de ambiente (por exemplo, OAuth2 para o Google Calendar).

### Interações Externas

*   **Integração Google Calendar:** Ao Aprovar uma Escala, o sistema sincroniza os dados do evento criando um convite no Google Calendar da conta organizadora e adicionando os ministros como convidados para que recebam e-mails e lembretes.
