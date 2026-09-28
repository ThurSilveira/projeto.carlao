import { Router } from "express";
import { AuthController } from "../controllers/AuthController";
import { AuthService } from "../services/AuthService";
import { authRepository } from "../repositories/AuthRepository";

export function createAuthRouter(): Router {
  const router = Router();
  const service = new AuthService(authRepository);
  const controller = new AuthController(service);

  router.get("/", controller.getAll);
  
  return router;
}
