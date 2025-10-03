# Configuração do Firebase para Carsai LMS

## Passo 1: Criar Projeto Firebase

1. Acesse [Firebase Console](https://console.firebase.google.com/)
2. Clique em "Adicionar projeto"
3. Nomeie o projeto como "Carsai LMS"
4. Siga os passos de configuração

## Passo 2: Configurar Autenticação

1. No console Firebase, vá para **Authentication**
2. Clique em "Começar"
3. Habilite os seguintes provedores:
   - Email/Senha
   - Google (opcional)

## Passo 3: Configurar Firestore Database

1. No console Firebase, vá para **Firestore Database**
2. Clique em "Criar banco de dados"
3. Escolha modo "Produção"
4. Selecione uma localização próxima (ex: europe-west)

### Estrutura de Coleções

Crie as seguintes coleções no Firestore:

```
users/
  - {userId}
    - email: string
    - name: string
    - role: 'admin' | 'instructor' | 'student'
    - avatar: string (URL)
    - createdAt: timestamp

courses/
  - {courseId}
    - title: string
    - description: string
    - thumbnail: string (URL)
    - instructorId: string
    - instructorName: string
    - category: string
    - level: 'beginner' | 'intermediate' | 'advanced'
    - price: number
    - featured: boolean
    - students: number
    - rating: number
    - reviews: number
    - createdAt: timestamp

lessons/
  - {lessonId}
    - courseId: string
    - title: string
    - description: string
    - type: 'video' | 'text' | 'pdf'
    - content: string (URL ou texto)
    - duration: number
    - order: number
    - createdAt: timestamp

enrollments/
  - {enrollmentId}
    - userId: string
    - courseId: string
    - progress: number (0-100)
    - enrolledAt: timestamp
    - completedAt: timestamp (opcional)

payments/
  - {paymentId}
    - userId: string
    - courseId: string
    - amount: number
    - method: 'mpesa' | 'emola' | 'mkesh' | 'paypal'
    - status: 'pending' | 'approved' | 'rejected'
    - proofUrl: string (URL)
    - createdAt: timestamp
    - processedAt: timestamp (opcional)

reviews/
  - {reviewId}
    - userId: string
    - userName: string
    - courseId: string
    - rating: number (1-5)
    - comment: string
    - createdAt: timestamp

notifications/
  - {notificationId}
    - userId: string
    - title: string
    - message: string
    - read: boolean
    - type: 'info' | 'success' | 'warning' | 'error'
    - createdAt: timestamp

posts/
  - {postId}
    - title: string
    - content: string
    - excerpt: string
    - thumbnail: string (URL)
    - authorId: string
    - authorName: string
    - category: string
    - published: boolean
    - createdAt: timestamp
```

## Passo 4: Configurar Storage

1. No console Firebase, vá para **Storage**
2. Clique em "Começar"
3. Configure as regras de segurança

### Estrutura de Pastas no Storage

```
/courses/{courseId}/thumbnail.jpg
/lessons/{lessonId}/{filename}
/certificates/{userId}/{certificateId}.pdf
/avatars/{userId}/avatar.jpg
/posts/{postId}/thumbnail.jpg
/payments/{paymentId}/proof.jpg
```

## Passo 5: Regras de Segurança Firestore

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Função auxiliar para verificar se o usuário está autenticado
    function isSignedIn() {
      return request.auth != null;
    }
    
    // Função para verificar se é admin
    function isAdmin() {
      return isSignedIn() && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin';
    }
    
    // Função para verificar se é instrutor
    function isInstructor() {
      return isSignedIn() && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'instructor';
    }
    
    // Usuários
    match /users/{userId} {
      allow read: if isSignedIn();
      allow create: if isSignedIn();
      allow update: if isSignedIn() && (request.auth.uid == userId || isAdmin());
      allow delete: if isAdmin();
    }
    
    // Cursos
    match /courses/{courseId} {
      allow read: if true; // Público
      allow create: if isAdmin() || isInstructor();
      allow update: if isAdmin() || (isInstructor() && resource.data.instructorId == request.auth.uid);
      allow delete: if isAdmin();
    }
    
    // Lições
    match /lessons/{lessonId} {
      allow read: if isSignedIn();
      allow create, update, delete: if isAdmin() || isInstructor();
    }
    
    // Inscrições
    match /enrollments/{enrollmentId} {
      allow read: if isSignedIn() && (request.auth.uid == resource.data.userId || isAdmin() || isInstructor());
      allow create: if isSignedIn();
      allow update: if isSignedIn() && request.auth.uid == resource.data.userId;
      allow delete: if isAdmin();
    }
    
    // Pagamentos
    match /payments/{paymentId} {
      allow read: if isSignedIn() && (request.auth.uid == resource.data.userId || isAdmin());
      allow create: if isSignedIn();
      allow update: if isAdmin();
      allow delete: if isAdmin();
    }
    
    // Avaliações
    match /reviews/{reviewId} {
      allow read: if true; // Público
      allow create: if isSignedIn();
      allow update, delete: if isSignedIn() && request.auth.uid == resource.data.userId;
    }
    
    // Notificações
    match /notifications/{notificationId} {
      allow read: if isSignedIn() && request.auth.uid == resource.data.userId;
      allow create: if isAdmin() || isInstructor();
      allow update: if isSignedIn() && request.auth.uid == resource.data.userId;
      allow delete: if isAdmin();
    }
    
    // Posts
    match /posts/{postId} {
      allow read: if resource.data.published == true || isAdmin();
      allow create, update, delete: if isAdmin();
    }
  }
}
```

## Passo 6: Regras de Segurança Storage

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /courses/{courseId}/{filename} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    match /lessons/{lessonId}/{filename} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
    
    match /certificates/{userId}/{filename} {
      allow read: if request.auth != null && request.auth.uid == userId;
      allow write: if request.auth != null;
    }
    
    match /avatars/{userId}/{filename} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    match /posts/{postId}/{filename} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    match /payments/{paymentId}/{filename} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

## Passo 7: Atualizar Configuração no Código

1. No console Firebase, vá para **Configurações do Projeto** (ícone de engrenagem)
2. Em "Seus apps", clique no ícone **</>** para adicionar um app web
3. Registre o app com nome "Carsai LMS Web"
4. Copie as credenciais fornecidas

5. Atualize o arquivo `src/lib/firebase.ts` com suas credenciais:

```typescript
const firebaseConfig = {
  apiKey: "SUA_API_KEY_AQUI",
  authDomain: "SEU_AUTH_DOMAIN_AQUI",
  projectId: "SEU_PROJECT_ID_AQUI",
  storageBucket: "SEU_STORAGE_BUCKET_AQUI",
  messagingSenderId: "SEU_MESSAGING_SENDER_ID_AQUI",
  appId: "SEU_APP_ID_AQUI"
};
```

## Passo 8: Criar Primeiro Usuário Admin

Após configurar a autenticação, você pode criar o primeiro usuário admin:

1. Registre-se na aplicação com email: `admin@carsai.co.mz`
2. No Firebase Console, vá para **Firestore Database**
3. Encontre o documento do usuário criado em `users/{userId}`
4. Edite o campo `role` para `'admin'`

## Notas Importantes

- Mantenha as credenciais do Firebase **SEGURAS**
- Não compartilhe as credenciais publicamente
- Em produção, considere usar variáveis de ambiente
- Revise as regras de segurança regularmente
- Faça backups regulares do Firestore

## Suporte

Para questões relacionadas ao Firebase:
- [Documentação Firebase](https://firebase.google.com/docs)
- [Firebase Support](https://firebase.google.com/support)

Para suporte específico da Carsai LMS:
- Email: suporte@carsai.co.mz
- WhatsApp: +258 862 414 345
