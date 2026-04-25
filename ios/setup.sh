#!/usr/bin/env bash
set -euo pipefail

echo "=== Escala Ministerial iOS Setup ==="

# ── 1. Verificar Xcode ────────────────────────────────────────────────────────
if ! xcode-select -p &>/dev/null; then
  echo "❌ Xcode não encontrado. Instale o Xcode pela App Store."
  exit 1
fi
echo "✅ Xcode: $(xcode-select -p)"

# ── 2. Instalar xcodegen via Homebrew ─────────────────────────────────────────
if ! command -v xcodegen &>/dev/null; then
  echo "📦 Instalando xcodegen via Homebrew..."
  if ! command -v brew &>/dev/null; then
    echo "❌ Homebrew não encontrado. Instale em https://brew.sh e tente novamente."
    exit 1
  fi
  brew install xcodegen
fi
echo "✅ xcodegen: $(xcodegen --version)"

# ── 3. Gerar .xcodeproj ───────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "⚙️  Gerando EscalaMinisterial.xcodeproj..."
xcodegen generate --spec project.yml

echo ""
echo "✅ Projeto gerado com sucesso!"
echo ""
echo "Para abrir no Xcode:"
echo "  open EscalaMinisterial.xcodeproj"
echo ""
echo "Certifique-se de que o backend está rodando em http://localhost:8080"
