package br.com.escalaministerial.enums;

/**
 * POO — Enumeração.
 *
 * Ciclo de vida de uma Escala: PROPOSTA → APROVADA → CONFIRMADA | CANCELADA.
 * Garante que somente transições válidas sejam representadas no domínio.
 */
public enum StatusEscala {
    PROPOSTA,
    APROVADA,
    CONFIRMADA,
    CANCELADA
}
