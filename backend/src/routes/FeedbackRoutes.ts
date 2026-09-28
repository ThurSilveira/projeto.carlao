import { Router } from "express";
import { FeedbackController } from "../controllers/FeedbackController";
import { FeedbackService } from "../services/FeedbackService";
import { feedbackRepository } from "../repositories/FeedbackRepository";

export function createFeedbackRouter(): Router {
  const router = Router();
  const service = new FeedbackService(feedbackRepository);
  const controller = new FeedbackController(service);

  router.get("/", controller.getAll);
  
  return router;
}
