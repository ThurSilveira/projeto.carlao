import cors from "cors";
import express, { Express } from "express";
import {
  errorHandler,
  notFoundHandler,
} from "./middlewares/ErrorHandler";
import { createMinistroRouter } from "./routes/MinistroRoutes";
import type { MinistroServiceContract } from "./services/MinistroService";

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
  app.use(notFoundHandler);
  app.use(errorHandler);

  return app;
}
