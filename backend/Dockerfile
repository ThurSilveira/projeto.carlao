FROM node:20-alpine AS builder

WORKDIR /app

# Instala dependências
COPY package*.json ./
RUN npm ci

# Copia código fonte
COPY . .

# Gera prisma client e constrói o código
RUN npm run prisma:generate
RUN npm run build

# Stage 2: Runtime
FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci --omit=dev

COPY --from=builder /app/dist ./dist
COPY --from=builder /app/prisma ./prisma
# Prisma client gerado fica no node_modules/@prisma/client
COPY --from=builder /app/node_modules/@prisma/client ./node_modules/@prisma/client
COPY --from=builder /app/node_modules/.prisma ./node_modules/.prisma

EXPOSE 3000

# Executa migrações pendentes (se necessário, ou pelo menos gera client novamente)
CMD ["npm", "start"]
