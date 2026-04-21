# ==========================================
# GERAR SECRETS SEGUROS
# ==========================================

# JWT Secret (Base64 - 32+ caracteres)
openssl rand -base64 32

# Database Password (Hexadecimal)
openssl rand -hex 16

# API Key Secret
openssl rand -hex 32

# ==========================================
# EXEMPLO DE OUTPUT
# ==========================================

# JWT_SECRET (copiar este valor para .env):
# aBc123XyZ9pQ5wEr/sTuVwXyZaBcDeFgHiJkLmNoPqRsT==

# DB_PASSWORD (copiar para .env):
# a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6

# ==========================================
# COMO USAR
# ==========================================

# 1. Gerar JWT Secret
$ openssl rand -base64 32
# Copiar output

# 2. Em production (Render):
#    Settings → Environment Variables
#    JWT_SECRET = <VALOR GERADO ACIMA>
#    DB_PASSWORD = <SENHA DO BANCO>

# 3. Em desenvolvimento (local):
#    backend/.env
#    JWT_SECRET=<VALOR>
#    DB_PASSWORD=<SENHA>

# ==========================================
# VALORES PADRÃO - PARA REFERÊNCIA
# ==========================================

# NÃO USE ESTES EM PRODUÇÃO!
# Apenas para demonstração/desenvolvimento

JWT_SECRET_EXEMPLO=ZWQyMGUyZjU5ZWE0MWJiYzdkNWY1ZTQxYjJjMWY2YTg4YjBkN2VjODJlNDI0Y2RjZWQ2YTBjYWE4MjQzODg1Mw==
