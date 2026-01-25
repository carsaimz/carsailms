# 📸 Screenshots - Guia de Captura

## Como Tirar Screenshots

### Preparação
1. Compile e instale o app
2. Configure modo claro E escuro
3. Use dispositivo com tela limpa (sem notificações)
4. Resolução recomendada: 1080x1920 ou superior

### Screenshots Necessários

#### Light Mode (Modo Claro)
1. **splash_light.png**
   - Tela de splash (logo + loading)
   - 3 segundos após abrir app

2. **home_light.png**
   - Tela principal com site carregado
   - Mostrar barra de navegação inferior

3. **menu_light.png**
   - Bottom sheet do menu aberto
   - Mostrar todas as 4 opções

4. **about_light.png**
   - Tela "Sobre" completa
   - Mostrar changelog visível

5. **settings_light.png**
   - Tela de configurações
   - Mostrar opções de tema e idioma

6. **offline_light.png** (opcional)
   - Tela de offline
   - Ative modo avião antes

#### Dark Mode (Modo Escuro)
Repita as mesmas capturas em modo escuro:
1. **splash_dark.png**
2. **home_dark.png**
3. **menu_dark.png**
4. **about_dark.png**
5. **settings_dark.png**
6. **offline_dark.png** (opcional)

### Como Capturar

#### Android (Nativo)
```
Power + Volume Down (ao mesmo tempo)
```

#### ADB
```bash
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png
```

### Edição Recomendada

#### Remover Barra de Status
Use ferramentas como:
- [Figma](https://figma.com) - Gratuito
- [Canva](https://canva.com) - Gratuito
- Photoshop / GIMP

#### Adicionar Moldura de Dispositivo
Use [Device Art Generator](https://developer.android.com/distribute/marketing-tools/device-art-generator)

### Especificações

- **Formato**: PNG
- **Tamanho**: Original do dispositivo
- **Qualidade**: Alta (sem compressão)
- **Nome**: snake_case (ex: splash_light.png)
- **Pasta**: `/screenshots/`

### Exemplo de Organização

```
screenshots/
├── README.md (este arquivo)
├── light/
│   ├── splash_light.png
│   ├── home_light.png
│   ├── menu_light.png
│   ├── about_light.png
│   └── settings_light.png
├── dark/
│   ├── splash_dark.png
│   ├── home_dark.png
│   ├── menu_dark.png
│   ├── about_dark.png
│   └── settings_dark.png
└── feature/
    ├── upload_demo.png
    ├── download_demo.png
    └── deeplink_demo.png
```

### Depois de Capturar

1. Mova todos os screenshots para `/screenshots/`
2. Verifique que README.md está referenciando corretamente
3. Commit:
   ```bash
   git add screenshots/
   git commit -m "Add: Screenshots do app"
   git push
   ```

### Preview no README

O README.md já está configurado para mostrar:
```markdown
<img src="screenshots/splash_light.png" width="200"/>
```

As imagens aparecerão automaticamente no GitHub!

---

**Dica**: Tire screenshots extras para usar em:
- Google Play Store (se publicar)
- Website
- Apresentações
- Marketing
