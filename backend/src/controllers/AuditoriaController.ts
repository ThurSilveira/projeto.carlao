import { Request, Response } from "express";
import { AuditoriaService } from "../services/AuditoriaService";

export class AuditoriaController {
  constructor(private readonly service: AuditoriaService) {}

  getAll = async (req: Request, res: Response) => {
    const data = await this.service.getAll();
    res.json(data);
  };
}
