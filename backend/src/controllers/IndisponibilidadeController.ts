import { Request, Response } from "express";
import { IndisponibilidadeService } from "../services/IndisponibilidadeService";

export class IndisponibilidadeController {
  constructor(private readonly service: IndisponibilidadeService) {}

  getAll = async (req: Request, res: Response) => {
    const data = await this.service.getAll();
    res.json(data);
  };
}
