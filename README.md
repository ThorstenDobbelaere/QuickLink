# QuickLink Docs

- Naam student: Thorsten Dobbelaere
- Naam framework: QuickLink
- Type framework: Java DI

QuickLink is een eenvoudig Dependency Injection framework voor java op basis van annotaties. Het is ontworpen om licht 
en configureerbaar te zijn. Dit betekent wel dat je als eindgebruiker zelf bepaalde conversions implementeert. Denk aan 
JSON of HTML mapping. Deze zijn niet ingebouwd in het framework.

## Demo
Om de demo als HTTP API uit te voeren, kan je volgend commando uitvoeren:

```shell
./gradlew run --args="--port=4040 --shutdown=/stop"
```

Om de demo als console app uit te voeren, verander je de arguments en enable je console:
```shell
./gradlew run --console=plain --args="--console --shutdown=/stop"
```
### Build
Je kan de demo app builden met:
```shell
./gradlew build
```

### Tests
Je kan tests uitvoeren met:
```shell
./gradlew test
```

## Installatie en setup
Je initialiseert Quicklink in te main methode:
```java
public class DemoProject {

    public static void main(String[] args) {
        ListenerConfiguration listenerConfiguration = new ListenerConfiguration();
        listenerConfiguration.setPort(4040);
        listenerConfiguration.setShutdownUrl("/stop");

        QuickLinkContextConfiguration config = new QuickLinkContextConfiguration();
        config.setHttpConfiguration(listenerConfiguration);
        config.setRunMode(RunMode.HTTP);

        QuickLink.run(DemoProject.class, config);
    }
}
```

## Mappings
Om een request aan een URL te linken, moet een mapping gebeuren. 
Dit kan aan de hand van verschillende Mapping annotations. 
Mapping van methoden gebeurt enkel wanneer de methode in een Controller staat. 
De URL wordt bepaald door de value die aan de Controller en Mapping annotation hebben. 
Deze worden aaneen geplakt en dit vormt de base URL van de methode.
Input parameters worden na de mapping toegevoegd en gesplitst met een slash.

- In RunMode.HTTP wordt de URL verondersteld UTF-8 encoded te zijn.
- In RunMode.CONSOLE wordt de input string unencoded verwacht

Er zijn 3 soorten mapping die je kan gebruiken:

### InputMapping
Een methode met @InputMapping wordt verondersteld geen return type te hebben, en kan parameters aannemen.
Deze methodes geven een OK zonder body terug.
Een voorbeeldscenario:

```java
@Controller("/warehouse")
public class WarehouseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseController.class);

    @InputMapping(value = "/log")
    public void log(String message){
        LOGGER.info("{}", message);
    }
}
```

Wanneer de call "/warehouse/log/Goede%20Dag" binnenkomt, wordt "Goede Dag" gelogd.

### OutputMapping
Een methode met @OutputMapping kan een return type hebben. Deze wordt naar een string omgezet, 
en er wordt een HTTP response van gemaakt. 
(zie [ResponseEntity](#response-entities) en [OutputConverter](#outputconverter))



### IOMapping
Een methode met @IOMapping doet hetzelfde als OutputMapping, maar er is een semantisch verschil.
IOMapping wordt verondersteld het systeem te wijzigen. (~ POST)

```java
    @IOMapping("/create-resource")
    public Resource createResource(String name, String referenceName, double description){
        return resourceService.createResource(name, referenceName, description);
    }
```

## Verwerking Requests
Bij het verwerken van binnenkomende requests wordt alleen gekeken naar de URL. De header en body
worden genegeerd. Zie [InputMapping](#inputmapping) voor details rond parameters.

## Configureerbaarheid

### Context Configuraties
Er zijn verschillende settings die je aan het framework kan meegeven om het gedrag te wijzigen. Deze kunnen worden
meegegeven aan het framework met de QuickLinkContextConfiguration. Dit kan je doen aan de hand van een QuickLinkContextConfiguration
object, dat je mee kan geven bij het aanroepen van de run methode. Alle Configuration objecten kunnen apart aangemaakt
worden en in een QuickLinkContextConfiguration geïnjecteerd worden, om vervolgens meegegeven te worden aan de run-methode
van het framework.

```java
public class DemoProject {

    public static void main(String[] args) {
        ListenerConfiguration listenerConfiguration = new ListenerConfiguration();
        listenerConfiguration.setShutdownUrl("/stop");

        QuickLinkContextConfiguration config = new QuickLinkContextConfiguration();
        config.setHttpConfiguration(listenerConfiguration);
        config.setRunMode(RunMode.CONSOLE);

        QuickLink.run(DemoProject.class, config);
    }
}
```

#### Run Mode
De Run Mode bepaalt de request-response interface. RunMode.CONSOLE betekent dat de applicatie naar System.in luistert voor input,
en output door de logger wordt getoond. Bij RunMode.HTTP wordt er een HTTP listener op de aangegeven poort geregistreerd.
Default: HTTP

```java
QuickLinkContextConfiguration config = new QuickLinkContextConfiguration();
config.setRunMode(RunMode.CONSOLE);
```

#### ListenerConfiguration
Dit is de configuratiecomponent voor alles wat met listeners of inputverwerking te maken heeft.

- Shutdown URL: als deze wordt aangeroepen, sluit het programma zichzelf af. Default: /shutdown
- Port: Geeft de poort aan voor de HTTP listener. Enkel van belang bij RunMode.HTTP. Default: 8080

```java
ListenerConfiguration listenerConfiguration = new ListenerConfiguration();
listenerConfiguration.setPort(4040);
listenerConfiguration.setShutdownUrl("/stop");

QuickLinkContextConfiguration config = new QuickLinkContextConfiguration();

config.setListenerConfiguration(listenerConfiguration);
```

#### LogFormatter
De LogFormatter bepaalt op welke manier de logging van highlights wordt voorzien. Highlights worden gebruikt tijdens de
opstart om de resultaten van de component scanning en stappen in de mapping duidelijk weer te geven. Je kan de LogFormatter
overwriten met je eigen implementatie te injecteren. By default wordt de string van enkele newlines en kleurcodes voorzien,
waardoor ze als cyan wordt weergegeven in de console. Om deze formatting uit te zetten, kan je het volgende doen:

```java
QuickLinkContextConfiguration config = new QuickLinkContextConfiguration();
config.setLogFormatter((new LogFormatter(){
    @Override 
    public String highlight(String message) {
        return message;
    }
}));
```

### OutputConverter
Als je een object op een custom manier wil teruggeven, dan kan je hiervoor een OutputConverter gebruiken. De 
OutputConverter wordt gebruikt om het uitgaande object om te zetten in een string, en om te bepalen welke Content Type
de HTTP response krijgt. Een JSON response kan er zo uitzien:

```java
public class JsonOutputConverter implements OutputConverter {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String stringify(Object o) {
        return gson.toJson(o);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.JSON;
    }
}
```

```java
@Config
public class OutputConverterConfig {

    @Bean
    public JsonOutputConverter jsonOutputConverter() {
        return new JsonOutputConverter();
    }
}
```

In de controller methode kan je deze converter dan selecteren als volgt (Zorg ervoor dat je exact hetzelfde type gebruikt dat
je Bean teruggeeft):

```java
    @IOMapping(value="/resource", stringifier = JsonOutputConverter.class)
    public Resource getResource(int id){
        return resourceService.getResource(id);
    }
```



In dit geval gebeurt er geen type casting, dus kan je de implementatie ook injecteren met de default constructor, door de
klasse te annoteren met @Injectable. De bean in de config laat je in dat geval weg.

```java
@Injectable
public class JsonOutputConverter implements OutputConverter {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String stringify(Object o) {
        return gson.toJson(o);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.JSON;
    }
}
```

### Response Entities
Response Entities laten je naast je object ook een status code teruggeven. Deze kan je gewoon returnen als volgt:

```java
    @OutputMapping(value = "/get", outputConverter = JsonOutputConverter.class)
    public ResponseEntity getVendor(String name) {
        Vendor vendor = vendorService.findVendorByName(name);
        if (vendor == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(vendor, HttpStatus.OK);
    }
```

Als default wordt OK terug gegeven als de methode zonder errors werd afgerond.

### Timed
Alle methoden van geïnjecteerde klassen kan je als @Timed annoteren. Deze loggen dan hoelang ze bezig waren wanneer
ze klaar zijn.

```java
    @Timed
    public Warehouse getWarehouse(String vendorName, String resourceName) {
        // logica die getimed wordt
    }

```