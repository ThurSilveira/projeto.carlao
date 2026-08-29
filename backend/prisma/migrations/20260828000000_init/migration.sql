-- CreateSchema
CREATE SCHEMA IF NOT EXISTS "public";

-- CreateTable
CREATE TABLE "Ministro" (
    "id" SERIAL NOT NULL,
    "nome" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "telefone" TEXT,
    "dataNascimento" DATE,
    "observacoes" TEXT,
    "ativo" BOOLEAN NOT NULL DEFAULT true,
    "visitasAoInfermo" BOOLEAN NOT NULL DEFAULT false,
    "statusCurso" BOOLEAN NOT NULL DEFAULT false,
    "escalasMes" INTEGER NOT NULL DEFAULT 0,
    "funcao" TEXT NOT NULL DEFAULT 'LEITURA',
    "funcaoEspecificada" TEXT,

    CONSTRAINT "Ministro_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "Ministro_email_key" ON "Ministro"("email");
