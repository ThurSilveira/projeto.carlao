import { Router } from "express";
import { createMinistroController } from "../controllers/MinistroController";
import type { MinistroServiceContract } from "../services/MinistroService";

export function createMinistroRouter(service: MinistroServiceContract): Router {
  const router = Router();
  const controller = createMinistroController(service);

  router.get("/", controller.list);
  router.get("/:id", controller.getById);
  router.post("/", controller.create);
  router.put("/:id", controller.update);
  router.patch("/:id", controller.update);
  router.delete("/:id", controller.remove);

  return router;
}
