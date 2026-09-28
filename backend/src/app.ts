import cors from "cors";
import express, { Express } from "express";
import { errorHandler, notFoundHandler } from "./middlewares/ErrorHandler";

import { createMinistroRouter } from "./routes/MinistroRoutes";
import type { MinistroServiceContract } from "./services/MinistroService";

import { createEventoRouter } from "./routes/EventoRoutes";
import { createEscalaRouter } from "./routes/EscalaRoutes";
import { createUsuarioRouter } from "./routes/UsuarioRoutes";
import { createIndisponibilidadeRouter } from "./routes/IndisponibilidadeRoutes";
import { createFeedbackRouter } from "./routes/FeedbackRoutes";
import { createAuditoriaRouter } from "./routes/AuditoriaRoutes";
import { createAuthRouter } from "./routes/AuthRoutes";

export function createApp(ministroService: MinistroServiceContract): Express {
  const app = express();

  app.disable("x-powered-by");
  app.use(cors());
  app.use(express.json({ limit: "1mb" }));

  app.get("/", (_request, response) => {
    response.json({ status: "API no ar" });
  });

  app.get("/api/public/health", (_request, response) => {
    response.json({ status: "ok" });
  });

  app.use("/api/ministros", createMinistroRouter(ministroService));
  app.use("/api/eventos", createEventoRouter());
  app.use("/api/escalas", createEscalaRouter());
  app.use("/api/usuarios", createUsuarioRouter());
  app.use("/api/indisponibilidades", createIndisponibilidadeRouter());
  app.use("/api/feedbacks", createFeedbackRouter());
  app.use("/api/auditoria", createAuditoriaRouter());
  app.use("/api/auth", createAuthRouter());

  app.use(notFoundHandler);
  app.use(errorHandler);

  return app;
}
