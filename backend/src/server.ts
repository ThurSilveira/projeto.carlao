import "dotenv/config";
import { createApp } from "./app";
import { prisma } from "./config/prisma";
import { ministroRepository } from "./repositories/MinistroRepository";
import { MinistroService } from "./services/MinistroService";

const configuredPort = Number(process.env.PORT ?? 3000);
const port = Number.isInteger(configuredPort) && configuredPort > 0
  ? configuredPort
  : 3000;

const app = createApp(new MinistroService(ministroRepository));
const server = app.listen(port, () => {
  console.log(`API disponível em http://localhost:${port}`);
});

async function shutdown(signal: string): Promise<void> {
  console.log(`Encerrando a API após ${signal}...`);
  server.close(async () => {
    await prisma.$disconnect();
    process.exit(0);
  });
}

process.once("SIGINT", () => void shutdown("SIGINT"));
process.once("SIGTERM", () => void shutdown("SIGTERM"));
