import { Request, Response } from "express";
import { EscalaService } from "../services/EscalaService";

export class EscalaController {
  constructor(private readonly service: EscalaService) {}

  getAll = async (req: Request, res: Response) => {
    const data = await this.service.getAll();
    res.json(data);
  };
}
