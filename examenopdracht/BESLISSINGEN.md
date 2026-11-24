# Beslissingen – Maaltijdboxen aan huis

## Week 1 – Servicegrenzen en architectuur

- **Gedaan:** Recipe, config, discoverry en api-gateway services gemaakt (geen functionaliteit) en het c4 model level2.
- **Openstaand:** De services(projecten) maken voor: Order, Inventory, Delivery en Notification. De functionaliteit en databanken van de afzonderlijke services verder implementeren.
- **Beslissing:** Elke microservice krijgt een eigen verantwoordelijkheid en, waar van toepassing, een eigen databank.
- **Waarom:** Hierdoor blijven de services onafhankelijker, beter onderhoudbaar en kunnen ze afzonderlijk aangepast en uitgebreid worden.
- **Extra keuze:** Notification krijgt geen eigen databank, omdat deze service enkel meldingen verstuurt en geen eigen bedrijfsgegevens moet bewaren.

