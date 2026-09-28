import { Router } from "express";
import { EscalaController } from "../controllers/EscalaController";
import { EscalaService } from "../services/EscalaService";
import { escalaRepository } from "../repositories/EscalaRepository";

export function createEscalaRouter(): Router {
  const router = Router();
  const service = new EscalaService(escalaRepository);
  const controller = new EscalaController(service);

  router.get("/", controller.getAll);
  
  return router;
}
