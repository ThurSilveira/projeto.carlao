export class AuditoriaService {
  constructor(private readonly repository: any) {}

  async getAll() {
    return this.repository.findMany();
  }
}
