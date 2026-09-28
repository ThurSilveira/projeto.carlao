export class EventoService {
  constructor(private readonly repository: any) {}

  async getAll() {
    return this.repository.findMany();
  }
}
