import { Router } from "express";
import { EventoController } from "../controllers/EventoController";
import { EventoService } from "../services/EventoService";
import { eventoRepository } from "../repositories/EventoRepository";

export function createEventoRouter(): Router {
  const router = Router();
  const service = new EventoService(eventoRepository);
  const controller = new EventoController(service);

  router.get("/", controller.getAll);
  
  return router;
}
