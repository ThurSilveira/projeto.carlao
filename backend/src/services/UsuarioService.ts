export class UsuarioService {
  constructor(private readonly repository: any) {}

  async getAll() {
    return this.repository.findMany();
  }
}
