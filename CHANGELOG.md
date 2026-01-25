# Changelog - Carsai LMS

## [1.0.1] - 24/01/2026

### ✅ Adicionado
- Botão de menu na barra de navegação inferior
- Menu popup com 4 opções:
  - Tela Inicial
  - Sobre
  - Definições
  - Verificar Atualizações
- Verificação automática de atualizações via GitHub Releases
- Suporte para deep linking completo (abre URLs específicos)
- Dialog "Sobre" mostrando versão do app

### 🔧 Corrigido
- Upload de arquivos agora funciona corretamente
- Suporte para múltiplos arquivos
- Permissão de câmera para upload de imagens
- ProgressBar alterado para FrameLayout (corrige ClassCastException)

### 🚀 Melhorado
- Firebase Analytics com tracking de eventos
- Firebase Remote Config com modo manutenção
- Firebase Cloud Messaging otimizado
- Melhor tratamento de erros

### 📱 Técnico
- BuildConfig habilitado
- Remote Config defaults em XML
- Novos ícones Material Design
- Strings multilíngue atualizadas

### 🎯 **Diferenças da v1.0 → v1.0.1:**

| Funcionalidade | v1.0 | v1.0.1 |
|---------------|------|------|
| Upload de arquivos | ❌ Não funciona | ✅ Funciona |
| Menu | ❌ Não tem | ✅ 4 opções |
| Deep linking | ⚠️ Básico | ✅ Completo |
| Verificar updates | ❌ Não tem | ✅ GitHub API |
| Firebase Analytics | ⚠️ Básico | ✅ Completo |
| Remote Config | ❌ Não | ✅ Sim |
| BuildConfig | ❌ Não | ✅ Sim |

## [1.0] - Versão Inicial
- 🎉 Versão inicial
- ✅ WebView integrado
- ✅ Sistema de downloads
- ✅ Modo escuro/claro
