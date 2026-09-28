import { Router } from "express";
import { IndisponibilidadeController } from "../controllers/IndisponibilidadeController";
import { IndisponibilidadeService } from "../services/IndisponibilidadeService";
import { indisponibilidadeRepository } from "../repositories/IndisponibilidadeRepository";

export function createIndisponibilidadeRouter(): Router {
  const router = Router();
  const service = new IndisponibilidadeService(indisponibilidadeRepository);
  const controller = new IndisponibilidadeController(service);

  router.get("/", controller.getAll);
  
  return router;
}
