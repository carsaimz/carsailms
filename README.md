# 📱 Carsai LMS - Android App

![Version](https://img.shields.io/badge/version-1.0.1-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)
![License](https://img.shields.io/badge/license-MIT-purple.svg)
[![CodeQL Advanced](https://github.com/carsaimz/carsailms/actions/workflows/codeql.yml/badge.svg)](https://github.com/carsaimz/carsailms/actions/workflows/codeql.yml)

**Carsai LMS** é um aplicativo Android nativo para acesso ao sistema de gestão de aprendizagem Carsai LMS.

## ✨ Funcionalidades

- 🔗 **Deep Linking** - Abre automaticamente ao acessar links do site
- 📥 **Downloads Organizados** - Salva arquivos na pasta "Carsai LMS"
- 🌐 **Links Inteligentes** - Abre links externos em apps apropriados (WhatsApp, Drive, YouTube)
- 📤 **Upload de Arquivos** - Suporte completo para upload de imagens, vídeos e documentos
- 🔥 **Firebase Integrado** - Analytics, Messaging e Remote Config
- 🎨 **Modo Escuro** - Tema claro, escuro ou seguir sistema
- 🌍 **Multilíngue** - Português e Inglês
- 🔄 **Atualizações Automáticas** - Verificação de novas versões via GitHub
- 📱 **Responsivo** - Suporte a orientação landscape para vídeos
- 🚀 **Performance** - WebView otimizado com cache inteligente

## 📸 Screenshots

_Em breve_

## 📋 Requisitos

- Android 7.0 (API 24) ou superior
- Conexão à internet
- ~10MB de espaço livre

## 🚀 Download

### Última Versão

[![Download APK](https://img.shields.io/badge/Download-APK-brightgreen.svg)](https://github.com/carsaimz/carsailms/releases/latest)

Baixe a versão mais recente na página de [Releases](https://github.com/carsaimz/carsailms/releases).

## 🔧 Compilação

### Pré-requisitos

- JDK 17
- Android SDK 34
- AIDE ou Android Studio

### Passos

1. Clone o repositório:
```bash
git clone https://github.com/carsaimz/carsailms.git
cd carsailms
```

2. Configure Firebase:
   - Crie projeto no [Firebase Console](https://console.firebase.google.com)
   - Baixe `google-services.json`
   - Coloque em `app/google-services.json`

3. Define o caminho de chaves em `app/build.gradle` para apontar onde está `keys/release.jks`
3. Compile:
```bash
./gradlew assembleRelease (se for release) ou assembleDebug (se for debug)
```

4. O APK estará em:
```
app/build/outputs/apk/release/app-release.apk
```

## 🔥 Configuração Firebase

### Analytics
O app registra automaticamente:
- Abertura do app
- Cliques em menus
- Downloads
- Navegação entre telas

### Cloud Messaging
- Notificações push automáticas
- Inscrição em tópicos: `general`, `updates`

### Remote Config
Parâmetros configuráveis:
- `maintenance_mode` - Modo manutenção
- `force_update` - Forçar atualização
- `min_version` - Versão mínima suportada

## 📝 Changelog

### v1.0.1 (Build 2) - 2025-01-24
- ✅ Corrigido upload de arquivos
- ✅ Adicionado menu de navegação inferior
- ✅ Implementada verificação de atualizações via GitHub
- ✅ Melhorado Firebase Analytics e Messaging
- ✅ Corrigido deep linking com URLs específicas
- ✅ Adicionada tela Sobre com changelog
- ✅ Melhorada seleção de arquivos (múltiplos arquivos)

### v1.0.0 (Build 1) - 2025-01-20
- 🎉 Versão inicial
- ✅ WebView integrado
- ✅ Sistema de downloads
- ✅ Modo escuro/claro
- ✅ Suporte multilíngue

**Para mais detalhes, veja [CHANGELOG](CHANGELOG.md)**

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor:

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 🐛 Reportar Bugs

Encontrou um bug? [Abra uma issue](https://github.com/carsaimz/carsailms/issues/new) com:
- Descrição detalhada
- Passos para reproduzir
- Versão do Android
- Screenshots (se aplicável)

## 📄 Licença

Este projeto está sob a licença MIT. Veja [LICENSE](LICENSE) para mais detalhes.

## 👥 Equipe

Desenvolvido pela **Equipe CarsaiDev**

- 🌐 Website: [carsailms.linkpc.net](https://carsailms.linkpc.net)
- 📧 Email: suporte@carsai.com
- 📱 GitHub: [@carsaimz](https://github.com/carsaimz)

## 🙏 Agradecimentos

- Firebase - Plataforma backend
- Material Design - Guidelines de design
- Todos os contribuidores

---

**Feito com ❤️ pela Equipe CarsaiDev**
