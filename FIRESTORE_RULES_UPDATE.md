# Atualização das Regras de Firestore

Para as novas funcionalidades funcionarem corretamente, você precisa atualizar as regras de segurança do Firestore.

## Passo 1: Acessar as Regras de Firestore

1. Acesse [Firebase Console](https://console.firebase.google.com/)
2. Selecione seu projeto "Carsai LMS"
3. Vá para **Firestore Database** → **Regras**

## Passo 2: Adicionar Regras para Chat

Adicione estas regras ao seu arquivo de regras:

```javascript
// Mensagens de Chat
match /chat_messages/{messageId} {
  allow read: if isSignedIn();
  allow create: if isSignedIn() && request.resource.data.userId == request.auth.uid;
  allow update, delete: if isSignedIn() && resource.data.userId == request.auth.uid;
}
```

## Passo 3: Regras já Existentes para Notificações

As regras para notificações já devem estar configuradas conforme o arquivo `FIREBASE_SETUP.md`:

```javascript
// Notificações
match /notifications/{notificationId} {
  allow read: if isSignedIn() && request.auth.uid == resource.data.userId;
  allow create: if isAdmin() || isInstructor();
  allow update: if isSignedIn() && request.auth.uid == resource.data.userId;
  allow delete: if isAdmin();
}
```

## Passo 4: Estrutura de Dados

### Chat Messages
```
chat_messages/
  - {messageId}
    - text: string
    - userId: string
    - userEmail: string
    - timestamp: timestamp
```

### Notifications (já existente)
```
notifications/
  - {notificationId}
    - userId: string
    - title: string
    - message: string
    - read: boolean
    - type: 'info' | 'success' | 'warning' | 'error'
    - createdAt: timestamp
```

## Notas Importantes

- Todas as coleções são criadas automaticamente quando você adiciona o primeiro documento
- Não é necessário criar as coleções manualmente
- As regras de segurança protegem seus dados
- Certifique-se de que as funções auxiliares `isSignedIn()`, `isAdmin()` e `isInstructor()` já estejam configuradas conforme o arquivo `FIREBASE_SETUP.md`
