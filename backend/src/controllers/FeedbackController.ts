import { Request, Response } from "express";
import { FeedbackService } from "../services/FeedbackService";

export class FeedbackController {
  constructor(private readonly service: FeedbackService) {}

  getAll = async (req: Request, res: Response) => {
    const data = await this.service.getAll();
    res.json(data);
  };
}
