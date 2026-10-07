# MoreFix

Building improvements and compatibility fixes for Create on Minecraft 1.21.1 / NeoForge.

- **Pipes inside supports:** combine Create, Create Encased and Copycats fluid pipes with Create Deco supports. Place either part first, keep native fluid transport and materials, and rotate the support independently with a wrench or Create's rotation menu.
- **Catwalks and railings:** six-way placement with embedded railings that follow the catwalk's orientation.
- **Ladders with supports and railings:** add both parts with right-click while keeping the ladder climbable. Supports all registered vanilla, Create, Create Deco and Copycats ladder variants, with their materials and orientations preserved.
- **Storage and boilers:** horizontal fluid tanks, vertical item vaults and vertical Steam 'n' Rails locomotive boilers, including supported variants.
- **Connected textures:** casings, metals, stone, glass and panes. Toggle with Ctrl; the key can be rebound.
- **Copycat materials:** improved bars and panel-backed grates, small-part visibility, glass connections, and preservation of materials in schematics, previews and resource lists.
- **Mechanisms and sublevels:** a horizontal output shaft for gantry carriages; moving docking ports and automatic assembly of glued sublevel structures; Copycat-covered wireless links retain their settings, materials and signals.

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/create-morefix) · [GitHub](https://github.com/MontiSIk/create-morefix) · [Contact on Discord](https://discord.com/users/307551324956917760)

## Requirements

The current published release is **1.5.15**. It requires **Minecraft 1.21.1, NeoForge 21.1.228+, Java 21 and Create 6.0.10**.

**Create Deco 2.1.3 and Copycats 3.0.4+ are optional.** Their integrations activate only when the respective addon is installed. Other integrations support Create Encased, Steam 'n' Rails, Forgematica, Sable 2.0.3 and Create: Simulated 1.3.0.

Use the same MoreFix version and compatible addon set on the server and clients. Back up your world before updating.

The **unreleased 1.5.16 development build** has also been tested with NeoForge 21.1.256, Copycats 3.0.9, Create Encased 1.9.0-ht3, Steam 'n' Rails 0.2.1, Sable 2.0.6 and Simulated 1.3.2. Create 6.0.10 and Create Deco 2.1.3 remain the tested versions for Minecraft 1.21.1. Compatible patch releases are allowed within the declared dependency ranges; future releases still need testing.

## What's new in 1.5.16 (unreleased)

- Combined ladder, support and railing assemblies, including Copycat material preservation.
- The technical mod ID is now `morefix`, and the display name is **MoreFix**. Registry aliases load blocks and block entities from older worlds.
- Updated dependency ranges for newer Minecraft 1.21.1 addon releases.
- Corrected physical and visual alignment of docking ports on rotating bearings.
- Safe cleanup when a docked sublevel is removed, preventing updates to a nonexistent plot.

For **Forgematica server pasting**, use **Paste NBT Restore Behavior → Place and clone** (`place_clone`). The `none` mode omits coatings, while restoring fields separately can cause Copycats to reject an incomplete material/item pair. Pasting commands require the appropriate server permissions.

## What's new in 1.5.15

- Optional Create Deco and Copycats dependencies, with conditional registration, client models and mixins.
- Create Deco supports on Copycat fluid pipes, preserving materials and native transport.
- A fix for invalid Copycats texture coordinates at atlas edges, including invisible small parts covered with zinc bars.

## Verification

Release 1.5.15 passed **18,842 functional assertions** and world-loading tests for all four Deco/Copycats combinations. Development checks additionally cover 1,136 ladder combinations with actual client models before and after reopening the world, dedicated-server loading, 45 Copycat types pasted through server clone commands, and moving docking ports on gantries, pulleys, elevators, pistons and bearings. This is an actively developed beta.

## Building from source

The build script uses **Windows, PowerShell 7 and JDK 21**, with libraries from an installed Minecraft profile. A Gradle project is not provided yet.

Compile-time dependencies include Create, Create Deco, Copycats, Forgematica 0.4.2, MaFgLib 0.4.3, Sable 2.0.3 and Create: Simulated 1.3.0. Exact JAR names and library versions are listed in `tools/build.ps1`. Installed Create Encased and Steam 'n' Rails assets are needed for their additional models. Optional runtime dependencies are still needed to compile their integrations.

```powershell
pwsh -File tools/build.ps1 -Profile 'C:/Minecraft/Profiles/Kriate' -Libraries 'C:/Minecraft/Libraries' -Jdk 'C:/Program Files/Java/jdk-21.0.11'
```

The current development source builds `dist/create-more-fix-1.5.16-mc1.21.1.jar`. Its technical mod ID and resource namespace are `morefix`; a compatibility adapter handles older saved identifiers.

Sources and model extensions are under `src/main`. Game dependencies, worlds, credentials and personal settings are excluded from this repository. Derived Create textures are generated locally from the user's installed Create assets.

## Credits and license

A project by **MontiSIk**, developed with substantial assistance from **OpenAI Codex**. Create and compatible addons belong to their respective authors. MoreFix's source code is licensed under **MIT**; see [LICENSE](LICENSE).
