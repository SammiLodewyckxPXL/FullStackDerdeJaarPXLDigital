# Plan van aanpak -- Maaltijdboxen aan huis

## 1\. Doel en aanpak

Voor de applicatie **Maaltijdboxen aan huis** bouwen we een
Angular-frontend en een backend op basis van Spring Boot-microservices.
De architectuur wordt vooraf vastgelegd, zodat de verantwoordelijkheden
van de services en hun onderlinge communicatie duidelijk zijn voordat de
implementatie start.

De frontend communiceert via één centraal toegangspunt: de API Gateway.
De backend gebruikt synchrone communicatie wanneer een service
onmiddellijk een antwoord nodig heeft en asynchrone communicatie wanneer
verdere verwerking onafhankelijk mag gebeuren.

## 2\. Projectstructuur

De opdracht wordt ondergebracht in de bestaande repository:

``` text
examenopdracht/
├── frontend/
│   └── Angular-applicatie
├── backend/
│   ├── api-gateway/
│   ├── discovery-service/
│   ├── config-service/
│   ├── recipe-service/
│   ├── order-service/
│   ├── inventory-service/
│   ├── delivery-service/
│   └── notification-service/
├── README.md
└── BESLISSINGEN.md
```

## 3\. Servicegrenzen en verantwoordelijkheden

Elke businessservice is verantwoordelijk voor een afgebakend onderdeel
van het domein en beheert zijn eigen gegevens. Een service leest of
wijzigt niet rechtstreeks de database van een andere service.

\---

Service           Verantwoordelijkheid   Entiteiten /       Database
gegevens

\---

Recipe Service    Recepten en            Recipe, Ingredient Recipe DB
ingrediënten van  
recepten beheren

Order Service     Bestellingen en        Order, OrderLine   Order DB
bestelregels beheren

Inventory Service Beschikbare voorraad   StockItem          Inventory DB
beheren en  
ingrediënten  
reserveren of  
vrijgeven

Delivery Service  Leverslots en          DeliverySlot,      Delivery DB
leveringen beheren     DeliveryRun

Notification      Meldingen versturen    Geen eigen         Geen database
Service           naar klanten           domeinentiteiten  
vereist
---

**Motivatie van de grenzen:** recepten, bestellingen, voorraad,
leveringen en meldingen hebben elk een eigen verantwoordelijkheid en
kunnen afzonderlijk evolueren. De service die eigenaar is van bepaalde
gegevens, blijft verantwoordelijk voor het lezen en wijzigen ervan.
Andere services gebruiken de API of berichten van die service.

## 4\. Infrastructuur en architectuur

De oplossing bevat naast de vijf businessservices ook de volgende
infrastructuurcomponenten:

* **Angular Frontend:** gebruikersinterface voor klanten, medewerkers
en chauffeurs.
* **API Gateway:** enige ingang voor de frontend. Routeert
HTTP-aanvragen naar de juiste backendservice. CORS wordt op de
Gateway ingesteld.
* **Discovery Service (Eureka):** registreert welke services
beschikbaar zijn. Services registreren zich bij Eureka; de Gateway
en services kunnen de geregistreerde locaties opvragen.
* **Config Service:** levert centrale configuratie aan de Gateway en
de microservices. De configuratie kan in een
Git-configuratierepository worden bijgehouden.
* **RabbitMQ:** message broker voor asynchrone events tussen services.
* **Feign:** wordt gebruikt voor synchrone REST-aanroepen tussen
microservices.
* **Circuit breaker:** beschermt synchrone communicatie wanneer een
afhankelijke service niet beschikbaar is.
* **Micrometer Tracing:** helpt om aanvragen en hun trace-ID doorheen
de verschillende services te volgen.

### Communicatieoverzicht

<img width="739" height="722" alt="image" src="https://github.com/user-attachments/assets/45696c5b-01bf-43fa-9363-d2b075357db6" />


In het definitieve C4 Level 2-diagram worden de businessservices,
databases en infrastructuur als afzonderlijke containers getoond.
Synchrone REST/Feign-verbindingen worden met volle pijlen aangeduid.
Asynchrone RabbitMQ-berichten en infrastructuurverbindingen worden met
gestippelde pijlen aangeduid.

## 5\. Communicatie tussen services

### 5.1 Synchrone communicatie

Bij synchrone communicatie wacht de aanroepende service op een antwoord.
Dit gebruiken we wanneer het antwoord nodig is om de huidige bewerking
te kunnen afronden.

Voorbeeld bij het plaatsen van een bestelling: - Order Service vraagt
via Feign aan Inventory Service of de benodigde voorraad beschikbaar
is. - Order Service vraagt via Feign aan Delivery Service of er nog
capaciteit is in het gekozen leverslot. - Als een controle niet slaagt
of de vereiste service niet tijdig antwoordt, wordt de bestelling niet
bevestigd. - Bij onvoldoende voorraad of onvoldoende capaciteit krijgt
de frontend een duidelijke foutmelding, bijvoorbeeld HTTP 409 Conflict.

### 5.2 Asynchrone communicatie

Bij asynchrone communicatie publiceert een service een bericht op
RabbitMQ. De producer hoeft niet te wachten tot alle consumers hun werk
hebben uitgevoerd.

Na een succesvolle bestelling publiceert Order Service het event
`order.confirmed`. Drie services verwerken dit event onafhankelijk: -
Inventory Service reserveert de nodige ingrediënten. - Delivery Service
plant de levering. - Notification Service verstuurt een bevestiging.

Deze vervolgstappen mogen de initiële bevestiging van de bestelling niet
blokkeren of doen mislukken. De consumers moeten berichten veilig kunnen
verwerken, zodat een opnieuw afgeleverd bericht niet tot dubbele
verwerking leidt.

## 6\. User stories en flows

De onderstaande flows beschrijven hoe de user stories door de
architectuur lopen. De API-paden zijn een eerste voorstel en worden vóór
de frontend- en backendimplementatie gezamenlijk vastgelegd.

### US1 -- Weekrecepten bekijken

1. De klant opent de receptenpagina in de Angular-frontend.
2. Angular stuurt een aanvraag naar de API Gateway.
3. De Gateway routeert de aanvraag naar Recipe Service.
4. Recipe Service haalt de recepten en bijhorende ingrediënten op uit
Recipe DB.
5. Recipe Service stuurt de gegevens terug via de Gateway naar Angular.

**Communicatie:** synchrone HTTP/REST.

Voorgestelde API:

``` http
GET /api/recipes
GET /api/recipes/{id}
```

### US2 -- Een box samenstellen en bestellen

1. De klant selecteert recepten, het aantal personen en een leverslot
in Angular.
2. Angular stuurt de bestelling naar de API Gateway.
3. De Gateway routeert de aanvraag naar Order Service.
4. Order Service vraagt via Feign aan Inventory Service of de benodigde
ingrediënten beschikbaar zijn.
5. Order Service vraagt via Feign aan Delivery Service of het gekozen
leverslot nog capaciteit heeft.
6. Als voorraad of capaciteit onvoldoende is, wordt de bestelling niet
aanvaard. Order Service geeft een duidelijke fout terug,
bijvoorbeeld HTTP 409 Conflict.
7. Als beide controles slagen, slaat Order Service de bestelling en
bestelregels op in Order DB en bevestigt de bestelling.
8. Order Service publiceert het event `order.confirmed` naar RabbitMQ.
9. Inventory Service, Delivery Service en Notification Service
consumeren het event en voeren hun eigen vervolgactie uit.

**Communicatie:** - Voorraadcontrole: synchroon via Feign. -
Capaciteitscontrole: synchroon via Feign. - Verdere verwerking na
bevestiging: asynchroon via RabbitMQ.

Voorgestelde API:

``` http
POST /api/orders
```

### US3 -- Bestelling opvolgen

1. De klant vraagt de status van een bestelling op in Angular.
2. Angular stuurt de aanvraag via de API Gateway naar Order Service.
3. Order Service haalt de bestelling en de actuele bestelinformatie op
uit Order DB.
4. Order Service stuurt de gegevens terug via de Gateway naar Angular.

**Communicatie:** synchrone HTTP/REST.

Voorgestelde API:

``` http
GET /api/orders/{id}
```

### US4 -- Voorraad beheren

1. Een medewerker opent het voorraadbeheer in Angular.
2. Angular vraagt via de API Gateway de voorraad op bij Inventory
Service.
3. Inventory Service leest de voorraadgegevens uit Inventory DB.
4. De medewerker past een voorraaditem aan.
5. Angular stuurt de wijziging via de Gateway naar Inventory Service.
6. Inventory Service valideert de wijziging en slaat ze op in Inventory
DB.

**Communicatie:** synchrone HTTP/REST.

Voorgestelde API:

``` http
GET /api/inventory
PATCH /api/inventory/{id}
```

### US5 -- Levering uitvoeren

1. De chauffeur of medewerker vraagt de geplande leveringen op via
Angular.
2. Angular stuurt de aanvraag via de API Gateway naar Delivery Service.
3. Delivery Service haalt de leveringen op uit Delivery DB.
4. De chauffeur voert een levering uit en registreert de nieuwe status.
5. Delivery Service valideert en bewaart de statuswijziging in Delivery
DB.
6. Als de klant over de statuswijziging geïnformeerd moet worden,
publiceert Delivery Service een event naar RabbitMQ.
7. Notification Service verwerkt het event en verstuurt de melding.

**Communicatie:** - Leveringen ophalen en status wijzigen: synchrone
HTTP/REST. - Melding versturen: asynchroon via RabbitMQ.

Voorgestelde API:

``` http
GET /api/deliveries
PATCH /api/deliveries/{id}/status
```

### US6 -- Bestelling annuleren

1. De klant vraagt via Angular om een bestelling te annuleren.
2. Angular stuurt de aanvraag via de API Gateway naar Order Service.
3. Order Service controleert of de bestelling volgens de bedrijfsregels
nog geannuleerd mag worden.
4. Als annuleren niet meer toegelaten is, geeft Order Service een
duidelijke foutmelding terug.
5. Als annuleren wel toegelaten is, wijzigt Order Service de status in
Order DB.
6. Order Service publiceert een annulerings-event, bijvoorbeeld
`order.cancelled`, naar RabbitMQ.
7. Inventory Service verwerkt het event en geeft eventueel
gereserveerde voorraad vrij.
8. Delivery Service verwerkt het event en annuleert de geplande
levering indien dat nog mogelijk is.
9. Notification Service kan de klant informeren over de annulering.

**Communicatie:** - Annulering controleren en registreren: synchrone
HTTP/REST. - Vrijgeven van voorraad, aanpassen van levering en melding:
asynchroon via RabbitMQ.

Voorgestelde API:

``` http
PATCH /api/orders/{id}/cancel
```

## 7\. Eerste API-overzicht

\---

Service           Methode           Pad                             Doel

\---

Recipe            GET               `/api/recipes`                  Recepten opvragen

Recipe            GET               `/api/recipes/{id}`             Eén recept
opvragen

Order             POST              `/api/orders`                   Bestelling
plaatsen

Order             GET               `/api/orders/{id}`              Bestelling en
status opvragen

Order             PATCH             `/api/orders/{id}/cancel`       Bestelling
annuleren

Inventory         GET               `/api/inventory`                Voorraad opvragen

Inventory         PATCH             `/api/inventory/{id}`           Voorraad
aanpassen

Delivery          GET               `/api/deliveries`               Leveringen
opvragen

Delivery          PATCH             `/api/deliveries/{id}/status`   Leveringsstatus
aanpassen
---

Dit zijn voorgestelde paden. De request- en response-DTO's,
validatieregels en foutstructuren worden verder uitgewerkt in het
API-contract.

## 8\. Foutafhandeling en betrouwbaarheid

* Bij onvoldoende voorraad of geen beschikbare capaciteit wordt een
bestelling niet bevestigd.
* De synchrone Feign-aanroepen krijgen time-outs en een passende
foutafhandeling.
* Een circuit breaker voorkomt dat een onbeschikbare afhankelijke
service onbeperkt nieuwe aanvragen veroorzaakt.
* Fouten bij asynchrone verwerking mogen de reeds bevestigde
bestelling niet ongedaan maken.
* Consumers verwerken events idempotent, zodat dubbele levering van
een bericht geen dubbele reservatie of melding veroorzaakt.
* Micrometer Tracing wordt gebruikt om aanvragen over de verschillende
services te kunnen opvolgen.

## 9\. Bewuste vereenvoudigingen

* Er wordt één API Gateway gebruikt als toegangspunt voor de
Angular-frontend.
* Elke businessservice beheert één eigen database; Notification
Service heeft geen database.
* De leverplanning gebruikt een eenvoudige indeling in leverslots en
leverrondes. Complexe routeoptimalisatie wordt niet voorzien.
* De voorraad- en capaciteitscontrole gebeuren synchroon vóór de
bestelling wordt bevestigd.
* De verdere verwerking van een bevestigde bestelling gebeurt
asynchroon via RabbitMQ.
* De API-paden en DTO's worden eenvoudig gehouden en afgestemd op de
user stories.

## 10\. Beslissingen en voortgang

De wekelijkse voortgang en architectuurbeslissingen worden bijgehouden
in `BESLISSINGEN.md`. Dat bestand wordt bij elke projectweek aangevuld
met drie tot vijf regels over wat uitgevoerd is, wat nog openstaat en
welke keuzes gemaakt zijn.

Eerste beslissing: - De backend wordt opgesplitst in Recipe, Order,
Inventory, Delivery en Notification Service. - Elke businessservice is
eigenaar van haar eigen gegevens en database; Notification Service
krijgt geen database. - De API Gateway wordt het enige toegangspunt voor
de frontend. - Feign wordt gebruikt voor synchrone controles; RabbitMQ
voor asynchrone events. - Deze servicegrenzen worden gekozen om
verantwoordelijkheden duidelijk te scheiden en services onafhankelijk te
kunnen ontwikkelen.

## 11\. Nog te verfijnen vóór implementatie

* De precieze acceptatiecriteria van US1 tot en met US6 controleren
tegenover de opdrachtbeschrijving.
* De entiteiten, relaties en databasevelden per service uitwerken.
* De request- en response-DTO's en foutcodes vastleggen.
* De exacte eventstructuren voor `order.confirmed` en
`order.cancelled` vastleggen.
* De C4 Level 2-containerdiagrammen en de flows controleren voordat de
services worden geïmplementeerd.

