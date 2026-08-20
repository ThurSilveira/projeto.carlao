package br.com.escalaministerial.enums;

/**
 * POO — Enumeração (tipo especial de classe fechada).
 *
 * Representa o conjunto fixo de funções possíveis para um Ministro.
 * Substituída por uma String no banco de dados, mas modelada como enum
 * para garantir integridade em tempo de compilação.
 */
public enum FuncaoMinistro {
    EUCARISTIA,
    LEITURA,
    ACOLHIMENTO,
    MUSICA,
    CATEQUESE,
    ADORACAO,
    OUTRO
}
