#!/bin/bash

echo "======================================================"
echo "  Git Initialization - Carsai LMS"
echo "======================================================"
echo ""

# Verificar se git está instalado
if ! command -v git &> /dev/null; then
    echo "❌ Git não está instalado!"
    echo "Instale com: sudo apt-get install git"
    exit 1
fi

# Inicializar repositório
if [ ! -d ".git" ]; then
    echo "Inicializando repositório Git..."
    git init
    echo "✓ Repositório inicializado"
else
    echo "✓ Repositório Git já existe"
fi

# Configurar usuário (se necessário)
if [ -z "$(git config user.name)" ]; then
    echo ""
    read -p "Digite seu nome: " git_name
    git config user.name "$git_name"
    echo "✓ Nome configurado: $git_name"
fi

if [ -z "$(git config user.email)" ]; then
    read -p "Digite seu email: " git_email
    git config user.email "$git_email"
    echo "✓ Email configurado: $git_email"
fi

# Adicionar arquivos
echo ""
echo "Adicionando arquivos..."
git add .

# Verificar status
echo ""
echo "Status do repositório:"
git status

# Primeiro commit
echo ""
read -p "Fazer commit inicial? (y/n): " do_commit
if [ "$do_commit" = "y" ]; then
    git commit -m "Initial commit: Carsai LMS v1.0.1

- Aplicativo Android completo
- WebView otimizada
- Firebase integrado
- Menu de navegação
- Verificação de atualizações
- Upload/Download de arquivos
- Modo escuro/claro
- Multilíngue (PT/EN)"
    
    echo "✓ Commit inicial criado"
fi

# Adicionar remote
echo ""
read -p "Adicionar remote do GitHub? (y/n): " add_remote
if [ "$add_remote" = "y" ]; then
    read -p "URL do repositório (https://github.com/user/repo.git): " repo_url
    git remote add origin "$repo_url"
    echo "✓ Remote adicionado: $repo_url"
    
    # Push
    read -p "Fazer push para GitHub? (y/n): " do_push
    if [ "$do_push" = "y" ]; then
        git branch -M main
        git push -u origin main
        echo "✓ Push concluído!"
    fi
fi

echo ""
echo "======================================================"
echo "  ✅ Configuração Git Concluída!"
echo "======================================================"
echo ""
echo "Próximos passos:"
echo "  1. Vá para: https://github.com/carsaimz/carsailms"
echo "  2. Verifique se os arquivos foram enviados"
echo "  3. Crie primeira release (v1.0.1)"
echo ""
