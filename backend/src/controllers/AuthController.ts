import { Request, Response } from "express";
import { AuthService } from "../services/AuthService";

export class AuthController {
  constructor(private readonly service: AuthService) {}

  getAll = async (req: Request, res: Response) => {
    const data = await this.service.getAll();
    res.json(data);
  };
}
