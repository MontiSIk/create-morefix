# Create: MoreFix

Дополнения и исправления для строительства с Create на Minecraft 1.21.1 / NeoForge.

- Трубы внутри опор Create Deco: установка обеих частей через ПКМ, независимое вращение опоры ключом и меню Create.
- Мостики в шести направлениях со встроенными перилами.
- Горизонтальные резервуары, вертикальные хранилища и локомотивные котлы.
- Соединение текстур через Ctrl с переназначением клавиши: корпуса, металлы, камень, стекло и панели.
- Расширенные покрытия Copycats, включая прутья и решётки с панелью; сохранение и отображение покрытий в схемах и списке материалов.
- Горизонтальный выходной вал каретки, движение и стыковка саблевелов, сохранение настроек беспроводной связи.

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/create-morefix) · [Сообщить об ошибке](https://github.com/MontiSIk/create-morefix/issues) · [Связаться в Discord](https://discord.com/users/307551324956917760)

## Зависимости

Minecraft 1.21.1, NeoForge 21.1.228+, Create 6.0.10, Create Deco 2.1.3, Copycats 3.0.4+. Дополнительная совместимость: Create Encased, Steam ’n’ Rails, Forgematica, Sable и Create: Simulated.

## Сборка исходников 1.5.13

Нужны Windows, PowerShell 7 и JDK 21. Скрипт использует зависимости из установленного игрового профиля; Gradle-проект пока не предоставляется.

Для текущей сборки подготовьте Create, Create Deco, Copycats, Forgematica 0.4.2, MaFgLib 0.4.3, Sable 2.0.3 и Create: Simulated 1.3.0. Имена JAR и версии библиотек перечислены в `tools/build.ps1`. Для дополнительных моделей нужны установленные Create Encased и Steam ’n’ Rails.

```powershell
pwsh -File tools/build.ps1 -Profile 'C:/Minecraft/Profiles/Kriate' -Libraries 'C:/Minecraft/Libraries' -Jdk 'C:/Program Files/Java/jdk-21.0.11'
```

Результат: `dist/create-more-fix-1.5.13-mc1.21.1.jar`. Технический идентификатор `catwalk_orientation` сохранён для совместимости существующих миров. Перед обновлением сохраните резервную копию мира.

Исходники и расширения моделей находятся в `src/main`. Игровые зависимости, миры и персональные настройки в репозиторий не включены.

## Авторство

Проект MontiSIk / HorsDuMunde, разработан с помощью OpenAI Codex. Create и совместимые моды принадлежат их авторам. Лицензия исходников приведена в `LICENSE`.