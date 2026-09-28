import { Request, Response } from "express";
import { EventoService } from "../services/EventoService";

export class EventoController {
  constructor(private readonly service: EventoService) {}

  getAll = async (req: Request, res: Response) => {
    const data = await this.service.getAll();
    res.json(data);
  };
}
