# Create: MoreFix

More building options, connected textures and compatibility fixes for Create.

## Current list of features

*   **Connected blocks and Ctrl textures**
    *   Connect casings, metals, stone, light blocks, glass and panes with **Ctrl** (configurable key), including Copycat coatings.
    *   Combine ordinary and connected placement to keep your own patterns.

![Ordinary, connected and mixed texture patterns](https://raw.githubusercontent.com/MontiSIk/create-morefix/main/docs/media/blocks-ctrl-patterns.gif)

*   **Ladders with supports or railings**
    *   Vanilla, Create, Deco and Copycat ladders accept upright supports **or** railings; climbing and coatings are preserved.
    *   Add matching railings on the left, right and back. One ladder uses one railing material; support wedges are excluded.
    *   **Ctrl + wrench + right-click** removes attachments and returns their items. Metal seams and overlapping surfaces have been corrected.

![Ladder and attachment variants](https://raw.githubusercontent.com/MontiSIk/create-morefix/main/docs/media/ladders-supports-railings.gif)

*   **Catwalks with independent railings**
    *   Six placement directions; built-in railings can use different materials from the catwalk.
    *   Wrench and Create rotation-menu changes carry the railings with the plate; materials survive saves, schematics and refunds.

![Catwalk rotating with independently changing railing materials](https://raw.githubusercontent.com/MontiSIk/create-morefix/main/docs/media/catwalk-rotation-independent.gif)

*   **Pipes inside supports**
    *   Create, Encased and Copycat pipes share block space with Deco supports, preserving fluid transport and coatings.
    *   Place either part first; the wrench and rotation menu rotate the support independently.

![Pipes inside vertical and horizontal supports](https://raw.githubusercontent.com/MontiSIk/create-morefix/main/docs/media/pipes-supports-transparent.gif)

*   **Fluid tanks and item vaults**
    *   Horizontal Create/Encased tanks and boilers, plus vertical item vaults.
    *   Easier structure extension, with corrected connections and shading.

![Horizontal and vertical storage](https://raw.githubusercontent.com/MontiSIk/create-morefix/main/docs/media/tanks-vaults-transparent.gif)

*   **Locomotive boilers**
    *   Vertical placement for all 68 Steam 'n' Rails variants, adjustable through Create's rotation menu.

![Vertical locomotive boilers](https://raw.githubusercontent.com/MontiSIk/create-morefix/main/docs/media/locomotive-boilers-transparent.gif)

*   **Copycat compatibility**
    
    *   All 45 shapes accept bars and grates, including panel variants.
    *   Fixes initial rendering, invisible small parts, texture-atlas edges and unwanted glass faces.
*   **Gantry carriages and sublevels**
    
    *   Horizontal output shafts, synchronized movement and preserved docking.
    *   Glued structures with paired docking ports automatically assemble into sublevels. Includes pulley movement and world/chunk reload fixes.

![Gantry moving a docked sublevel with F3+B bounds](https://raw.githubusercontent.com/MontiSIk/create-morefix/main/docs/media/gantry-sublevel-transparent.gif)

*   **Wireless links and schematics**
    *   Copycat transmitter/receiver shells retain materials, frequencies and signals, including on sublevels; the receiver lamp continues switching.
    *   Coatings appear in Create/Forgematica previews and material lists. Server pasting preserves them with **place + clone** or **place + data modify**, subject to command permissions. **None** does not transfer coatings.
    *   Includes fixes for pasted conveyors and falling blocks.

![Copycat-covered wireless transmitter and working receiver](https://raw.githubusercontent.com/MontiSIk/create-morefix/main/docs/media/wireless-copycat-transparent.gif)

***

**Minecraft 1.21.1 · NeoForge 21.1.228+ · Java 21 · Create 6.0.10**

**Create Deco 2.1.3** and **Copycats 3.0.4+** are optional; their features activate when installed. Additional integrations: Create Encased, Steam 'n' Rails, Forgematica, Sable and Create: Simulated. Newer compatible NeoForge and Copycats builds are supported.

Tested in isolated worlds with grouped checks for placement, rendering, saved data and compatibility. MoreFix remains beta; report problems with mod versions and `latest.log` or a crash report.

By **MontiSIk**, developed with assistance from **OpenAI Codex**. Code licensed under **MIT**; Create and compatible add-ons belong to their respective authors.

**[💬 Message me on Discord](https://discord.com/users/307551324956917760)** · **[GitHub — source, license and issues](https://github.com/MontiSIk/create-morefix)**