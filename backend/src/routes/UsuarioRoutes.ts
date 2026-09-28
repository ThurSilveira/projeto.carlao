import { Router } from "express";
import { UsuarioController } from "../controllers/UsuarioController";
import { UsuarioService } from "../services/UsuarioService";
import { usuarioRepository } from "../repositories/UsuarioRepository";

export function createUsuarioRouter(): Router {
  const router = Router();
  const service = new UsuarioService(usuarioRepository);
  const controller = new UsuarioController(service);

  router.get("/", controller.getAll);
  
  return router;
}
