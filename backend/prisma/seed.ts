import { prisma } from '../src/config/prisma';

async function main() {
  console.log('Seeding data...');

  await prisma.escala.deleteMany();
  await prisma.ministro.deleteMany();

  const m1 = await prisma.ministro.create({
    data: {
      nome: 'João Silva',
      email: 'joao.silva@example.com',
      telefone: '11999999999',
      dataNascimento: new Date('1980-01-01'),
      observacoes: 'Ministro fictício de teste 1',
      ativo: true,
      visitasAoInfermo: false,
      statusCurso: true,
      escalasMes: 2,
      funcao: 'LEITURA'
    }
  });

  const m2 = await prisma.ministro.create({
    data: {
      nome: 'Maria Souza',
      email: 'maria.souza@example.com',
      telefone: '11988888888',
      dataNascimento: new Date('1992-05-15'),
      observacoes: 'Ministro fictício de teste 2',
      ativo: true,
      visitasAoInfermo: true,
      statusCurso: true,
      escalasMes: 4,
      funcao: 'EUCARISTIA'
    }
  });

  await prisma.escala.create({
    data: {
      data: new Date(),
      local: 'Igreja Matriz',
      ministroId: m1.id
    }
  });

  await prisma.escala.create({
    data: {
      data: new Date(Date.now() + 86400000 * 2), // in 2 days
      local: 'Capela São José',
      ministroId: m2.id
    }
  });

  console.log('Seed completed successfully!');
}

main()
  .catch(e => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
