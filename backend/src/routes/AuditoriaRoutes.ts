import { Router } from "express";
import { AuditoriaController } from "../controllers/AuditoriaController";
import { AuditoriaService } from "../services/AuditoriaService";
import { auditoriaRepository } from "../repositories/AuditoriaRepository";

export function createAuditoriaRouter(): Router {
  const router = Router();
  const service = new AuditoriaService(auditoriaRepository);
  const controller = new AuditoriaController(service);

  router.get("/", controller.getAll);
  
  return router;
}
