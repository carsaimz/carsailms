# Guia de Instalação - Carsai LMS

Este guia fornece instruções passo a passo para instalar e configurar o Sistema de Gestão de Aprendizagem (LMS) da Carsai Mozambique.

## Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Node.js** (versão 18 ou superior) - [Baixar Node.js](https://nodejs.org/)
- **Git** - [Baixar Git](https://git-scm.com/)
- **Conta Firebase** - [Criar conta Firebase](https://firebase.google.com/)
- **Editor de Código** (recomendado: VS Code) - [Baixar VS Code](https://code.visualstudio.com/)

## Passo 1: Clonar o Repositório

```bash
# Clone o repositório
git clone <URL_DO_REPOSITORIO>

# Entre na pasta do projeto
cd carsai-lms

# Instale as dependências
npm install
```

## Passo 2: Configurar Firebase

Siga o guia detalhado em `FIREBASE_SETUP.md` para:

1. Criar projeto Firebase
2. Configurar autenticação
3. Configurar Firestore Database
4. Configurar Storage
5. Definir regras de segurança
6. Obter credenciais do projeto

## Passo 3: Configurar Variáveis de Ambiente

1. Abra o arquivo `src/lib/firebase.ts`

2. Substitua os valores de configuração pelas suas credenciais do Firebase:

```typescript
const firebaseConfig = {
  apiKey: "SUA_API_KEY",
  authDomain: "SEU_AUTH_DOMAIN",
  projectId: "SEU_PROJECT_ID",
  storageBucket: "SEU_STORAGE_BUCKET",
  messagingSenderId: "SEU_MESSAGING_SENDER_ID",
  appId: "SEU_APP_ID"
};
```

## Passo 4: Iniciar o Servidor de Desenvolvimento

```bash
# Inicie o servidor de desenvolvimento
npm run dev
```

A aplicação estará disponível em: `http://localhost:8080`

## Passo 5: Criar Primeiro Usuário Admin

1. Acesse `http://localhost:8080`
2. Clique em "Registar"
3. Crie uma conta com email: `admin@carsai.co.mz`
4. Vá ao Firebase Console → Firestore Database
5. Encontre o documento do usuário em `users/{userId}`
6. Altere o campo `role` para `'admin'`

## Passo 6: Adicionar Dados Iniciais (Opcional)

### Categorias de Cursos Sugeridas:
- Programação
- Marketing Digital
- Design
- Data Science
- Gestão de Projetos
- Fotografia
- Idiomas
- Contabilidade

### Criar Cursos de Exemplo:

1. Faça login como admin
2. Vá para o painel administrativo
3. Crie cursos de exemplo usando a interface

## Estrutura do Projeto

```
carsai-lms/
├── src/
│   ├── assets/          # Imagens e recursos estáticos
│   ├── components/      # Componentes React
│   │   ├── ui/         # Componentes UI (shadcn)
│   │   ├── Navbar.tsx
│   │   ├── Footer.tsx
│   │   └── CourseCard.tsx
│   ├── lib/            # Utilitários e configurações
│   │   ├── firebase.ts # Configuração Firebase
│   │   └── utils.ts
│   ├── pages/          # Páginas da aplicação
│   │   ├── Home.tsx
│   │   ├── Login.tsx
│   │   ├── Courses.tsx
│   │   ├── About.tsx
│   │   └── Contact.tsx
│   ├── types/          # Tipos TypeScript
│   │   └── index.ts
│   ├── App.tsx         # Componente principal
│   ├── main.tsx        # Ponto de entrada
│   └── index.css       # Estilos globais
├── public/             # Arquivos públicos
├── FIREBASE_SETUP.md   # Guia de configuração Firebase
├── INSTALACAO.md       # Este arquivo
└── README.md           # Documentação geral
```

## Comandos Disponíveis

```bash
# Iniciar servidor de desenvolvimento
npm run dev

# Criar build de produção
npm run build

# Pré-visualizar build de produção
npm run preview

# Executar linter
npm run lint
```

## Implantação (Deploy)

### Opção 1: Firebase Hosting

```bash
# Instalar Firebase CLI
npm install -g firebase-tools

# Fazer login no Firebase
firebase login

# Inicializar Firebase no projeto
firebase init hosting

# Criar build de produção
npm run build

# Fazer deploy
firebase deploy
```

### Opção 2: Vercel

1. Crie uma conta em [Vercel](https://vercel.com/)
2. Conecte seu repositório Git
3. Configure as variáveis de ambiente
4. Faça o deploy

### Opção 3: Netlify

1. Crie uma conta em [Netlify](https://netlify.com/)
2. Conecte seu repositório Git
3. Configure build command: `npm run build`
4. Configure publish directory: `dist`
5. Faça o deploy

## Solução de Problemas

### Erro: "Module not found"
```bash
# Limpe o cache e reinstale as dependências
rm -rf node_modules
npm install
```

### Erro: Firebase não inicializado
- Verifique se as credenciais em `src/lib/firebase.ts` estão corretas
- Certifique-se de que os serviços Firebase estão habilitados no console

### Problemas de CORS
- Verifique as regras de segurança do Firestore
- Configure corretamente os domínios autorizados no Firebase Console

### Erro de Build
```bash
# Execute o TypeScript check
npx tsc --noEmit

# Corrija os erros apontados
```

## Requisitos de Sistema

### Mínimos:
- CPU: Dual-core 2.0 GHz
- RAM: 4 GB
- Espaço em Disco: 500 MB
- Navegador: Chrome, Firefox, Safari ou Edge (versões atualizadas)

### Recomendados:
- CPU: Quad-core 2.5 GHz ou superior
- RAM: 8 GB ou mais
- SSD: 1 GB de espaço livre
- Conexão à Internet: Banda larga

## Próximos Passos

Após a instalação bem-sucedida:

1. ✅ Configure o primeiro usuário admin
2. ✅ Adicione categorias de cursos
3. ✅ Crie cursos de exemplo
4. ✅ Convide instrutores
5. ✅ Configure métodos de pagamento
6. ✅ Personalize as páginas Sobre e Contacto
7. ✅ Configure notificações por email (opcional)
8. ✅ Implante em produção

## Suporte

Se encontrar problemas durante a instalação:

- **Email**: suporte@carsai.co.mz
- **WhatsApp**: +258 862 414 345
- **Telefone**: +258 844 414 345
- **Documentação**: Consulte README.md e FIREBASE_SETUP.md

## Licença

Este projeto é propriedade da Carsai Mozambique. Todos os direitos reservados.

---

**Carsai Mozambique LMS v3.0** - Construído com paixão e tecnologia para o futuro da educação em Moçambique.
