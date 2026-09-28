export class AuthService {
  constructor(private readonly repository: any) {}

  async getAll() {
    return this.repository.findMany();
  }
}
