# Escala Ministerial — Versão Daniel (POO em Java)

## Por que o sistema roda em Python e não em Java?

O backend original do **Escala Ministerial** foi desenvolvido inicialmente em **Java 17 + Spring Boot 3.2**, seguindo as práticas de mercado para APIs REST robustas.

No entanto, o projeto é hospedado no **[Render](https://render.com)** (plano gratuito), que hiberna o servidor após 15 minutos de inatividade. Na primeira requisição após o sono, o servidor precisa ser "acordado" — e o tempo de cold start do Spring Boot chegava a **3-4 minutos**, tornando a experiência do usuário inaceitável.

A solução foi migrar para **Python 3.12 + FastAPI**, que apresenta:

| Critério | Java + Spring Boot | Python + FastAPI |
|---|---|---|
| Cold start no Render | 10–20 s | 1–3 s |
| Uso de memória | ~250 MB | ~60–80 MB |
| Tamanho da imagem Docker | ~350 MB | ~80 MB |
| Tempo de build no deploy | ~3 min | ~40 s |

A lógica de negócio, os endpoints e o banco de dados PostgreSQL permaneceram **idênticos** — apenas a tecnologia do servidor mudou.

---

## O que é esta branch?

Esta branch (`versao-daniel`) (Refatorada e resumida pelo Claude Code) existe para demonstrar como o domínio do sistema seria modelado em **Java puro**, aplicando os conceitos de **Programação Orientada a Objetos (POO)**.

As classes **não possuem** nenhuma dependência externa — sem Spring, sem JPA, sem banco de dados, sem frontend. São classes Java simples que representam apenas o modelo de domínio do sistema.

---

## Estrutura do código

```
backend-java/
└── main/java/br/com/escalaministerial/
    ├── enums/
    │   ├── FuncaoMinistro.java     — funções litúrgicas possíveis
    │   ├── StatusEscala.java       — ciclo de vida de uma escala
    │   ├── StatusFeedback.java     — ciclo de vida de um feedback
    │   ├── TipoEvento.java         — categorias de eventos litúrgicos
    │   └── TipoAcao.java           — tipos de ação registrados em auditoria
    └── model/
        ├── Ministro.java           — entidade principal do sistema
        ├── Evento.java             — evento litúrgico que recebe uma escala
        ├── Escala.java             — escala de ministros para um evento
        ├── EscalaMinistro.java     — vínculo ministro ↔ escala (com atributos próprios)
        ├── Indisponibilidade.java  — bloqueio de data/hora de um ministro
        ├── Feedback.java           — avaliação de um ministro sobre um evento
        └── LogAuditoria.java       — registro imutável de cada operação do sistema
```

---

## Conceitos POO aplicados

### Encapsulamento
Todos os atributos das classes são `private`. O acesso externo é feito exclusivamente por **getters** (leitura) e **setters** (escrita), protegendo o estado interno de modificações não controladas.

```java
// Acesso direto proibido — passa pelo setter
ministro.setEscalasMes(3);
int total = ministro.getEscalasMes();
```

### Abstração
Cada classe expõe apenas o que é relevante para o domínio. Detalhes de implementação (inicialização de listas, valores padrão, timestamp atual) ficam escondidos nos construtores — quem usa a classe não precisa conhecê-los.

```java
// O caller não precisa saber que escalasMes começa em 0 ou que dataEnvio = now()
Ministro m = new Ministro("João", "joao@email.com", FuncaoMinistro.LEITURA);
Feedback f = new Feedback(ministro, evento, 5, "Ótimo evento");
```

### Herança (via Enum)
Os enums (`FuncaoMinistro`, `StatusEscala`, etc.) são uma forma especial de herança em Java — todas as constantes herdam comportamentos comuns da superclasse `Enum`, como `name()`, `ordinal()` e `toString()`. Garantem que somente valores válidos do domínio existam em tempo de compilação.

```java
StatusEscala status = StatusEscala.PROPOSTA;
// Impossível atribuir "RASCUNHO" — o compilador rejeita
```

### Composição
`Ministro` **é composto** por uma lista de `Indisponibilidade`, e `Escala` **é composta** por uma lista de `EscalaMinistro`. Quando o objeto pai é removido, os filhos deixam de existir — ciclo de vida dependente.

```java
// Indisponibilidade não existe sem Ministro
private List<Indisponibilidade> indisponibilidades = new ArrayList<>();
```

### Associação
`EscalaMinistro` **referencia** tanto `Escala` quanto `Ministro` sem possuir nenhum dos dois — ciclos de vida independentes. O mesmo vale para `Feedback`, que aponta para `Ministro` e `Evento`.

```java
// Associação bidirecional — cada lado conhece o outro
private Escala escala;
private Ministro ministro;
```

### Classe de Associação
`EscalaMinistro` é um exemplo de **classe de associação**: existe para representar um relacionamento N:N (muitos ministros em muitas escalas) que carrega atributos próprios — `confirmacaoMinistro`, `dataConfirmacao` e `substituido`. Um simples relacionamento N:N não seria suficiente.

### Polimorfismo (via Override)
Todas as classes sobrescrevem (`@Override`) métodos herdados de `Object`:

- `equals()` — define o critério de igualdade no domínio (dois ministros com o mesmo e-mail são o mesmo ministro, independente de serem objetos distintos na memória)
- `hashCode()` — sempre consistente com `equals`, obrigatório para uso correto em coleções como `HashMap` e `HashSet`
- `toString()` — representação textual legível do objeto, essencial em logs e depuração

```java
@Override
public boolean equals(Object o) {
    if (!(o instanceof Ministro)) return false;
    return Objects.equals(email, ((Ministro) o).email);
}
```

---

## Relacionamento entre as classes

```
Ministro ──< Indisponibilidade     (composição 1:N — ciclo de vida dependente)
Ministro ──< EscalaMinistro        (associação 1:N — ministro participa de N escalas)
Ministro ──< Feedback              (associação 1:N — ministro envia N avaliações)

Evento   ──< Escala                (associação 1:N — evento pode ter N escalas)
Evento   ──< Feedback              (associação 1:N — evento recebe N avaliações)

Escala   ──< EscalaMinistro        (composição 1:N — ciclo de vida dependente)

EscalaMinistro >── Ministro        (associação N:1)
EscalaMinistro >── Escala          (composição N:1)

Feedback >── Ministro              (associação N:1)
Feedback >── Evento                (associação N:1)

LogAuditoria                       (entidade independente, sem associações)
```

---

## Por que Java seria mais adequado em produção com recursos adequados?

Em um servidor dedicado (sem cold start), o Java oferece vantagens importantes sobre o Python:

- **Tipagem estática** — erros detectados em tempo de compilação, não em produção
- **JVM otimizada** — após o aquecimento, a JVM com JIT compila bytecode para código nativo e supera linguagens interpretadas em throughput
- **Ecossistema maduro** — Spring Boot, Hibernate, ferramentas de build e monitoramento consolidados há décadas
- **Verbosidade como documentação** — construtores, tipos e modificadores de acesso explícitos tornam o código autoexplicativo

O Python + FastAPI foi a escolha **pragmática** para o plano gratuito do Render. O Java seria a escolha natural para um ambiente de produção com recursos adequados.
