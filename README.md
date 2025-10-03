# Carsai Mozambique LMS - Sistema de Gestão de Aprendizagem

Sistema de aprendizagem online completo desenvolvido com React, TypeScript e Firebase.

## 🚀 Funcionalidades Implementadas

### ✅ Autenticação Completa
- Login/Registro com validação
- Gestão de sessão Firebase Auth
- Proteção de rotas por role (Admin, Instrutor, Estudante)
- Navbar dinâmica baseada no estado de autenticação

### ✅ Páginas Públicas
- **Home**: Hero section, cursos em destaque (dados reais do Firebase)
- **Cursos**: Catálogo com dados do Firestore
- **Blog**: Sistema de posts dinâmico do Firebase
- **Post Detail**: Visualização completa de posts
- **Sobre**: Missão, visão, valores
- **Contacto**: Formulário com métodos de pagamento MZ
- **Termos de Uso**: Documento legal completo
- **Política de Privacidade**: GDPR compliant

### ✅ Dashboards
- **Estudante**: Progresso, cursos, certificados
- **Instrutor**: Gestão de cursos e alunos
- **Admin**: Visão geral da plataforma

### ✅ Segurança
- Validação com Zod (email, senhas, formulários)
- Input sanitization
- Firebase Authentication
- Role-based access control

## 🔧 Configuração Rápida

### 1. Instalar Dependências
```bash
npm install
```

### 2. Configurar Firebase

1. Crie um projeto no [Firebase Console](https://console.firebase.google.com/)

2. Ative os serviços:
   - **Authentication** → Email/Password
   - **Firestore Database** → Modo produção
   - **Storage** → Para uploads

3. Copie as credenciais e atualize `src/lib/firebase.ts`:
```typescript
const firebaseConfig = {
  apiKey: "SUA_API_KEY",
  authDomain: "SEU_AUTH_DOMAIN",
  projectId: "SEU_PROJECT_ID",
  storageBucket: "SEU_STORAGE_BUCKET",
  messagingSenderId: "SEU_SENDER_ID",
  appId: "SEU_APP_ID"
};
```

### 3. Configurar Firestore (Regras de Segurança)

Veja `FIREBASE_SETUP.md` para regras de segurança completas.

### 4. Iniciar Projeto
```bash
npm run dev
```

## 📚 Documentação Adicional

- **FIREBASE_SETUP.md** - Configuração detalhada Firebase
- **INSTALACAO.md** - Guia de instalação completo

## 🎨 Tecnologias

- React 18 + TypeScript
- Firebase (Auth, Firestore, Storage)
- Tailwind CSS + shadcn/ui
- React Router Dom
- Zod (validação)
- Date-fns (datas)

## 📞 Suporte

- Email: suporte@carsai.co.mz
- WhatsApp: +258 862 414 345
- Telefone: +258 844 414 345

## 📝 Métodos de Pagamento Suportados

- M-Pesa: 844414345 / 842846463
- e-Mola: 862414345
- MKesh: 835020143
- PayPal: carsaimozambique@gmail.com

---

**Carsai Mozambique LMS v3.0** - Powered by React & Firebase
