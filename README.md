# TeraPerms

TeraPerms – Einfaches Rechtemanagement für Bukkit

TeraPerms ist ein leichtgewichtiges und benutzerfreundliches Permissions-Plugin für Bukkit-basierte Minecraft-Server. Es bietet alle grundlegenden Funktionen zur Erstellung und Verwaltung von Gruppen, Berechtigungen (Permissions) und Prefixen – ganz ohne überladene Zusatzfunktionen.

Das Plugin verzichtet bewusst auf externe Datenbankanbindungen wie MySQL und setzt stattdessen auf eine lokale Dateispeicherung. Dadurch ist die Einrichtung besonders unkompliziert und eignet sich ideal für kleinere Serverprojekte oder Setups ohne komplexe Infrastruktur.

Ein weiteres Highlight ist die vollständige Unterstützung von Hex-Farbcodes, mit denen sich Prefixe optisch ansprechend und individuell gestalten lassen – perfekt für ein einheitliches Serverdesign. Zusätzlich unterstützt TeraPerms Placeholder, wodurch eine nahtlose Integration mit anderen Plugins und Systemen möglich ist.

---

Funktionen im Überblick:

● Verwaltung von Gruppen und Permissions über Konfigurationsdateien    
● Unterstützung benutzerdefinierter Prefixe    
● Volle Unterstützung für [Hex-Farben](https://htmlcolorcodes.com/)    
● Kein MySQL – einfache, lokale Speicherung    
● [Placeholder](https://www.spigotmc.org/resources/placeholderapi.6245/)-Unterstützung für Kompatibilität mit anderen Plugins    
● Kompatibel mit Bukkit-Servern (z. B. [Spigot](https://hub.spigotmc.org/jenkins/job/BuildTools/), [Paper](https://papermc.io/downloads/paper) oder [Purpur](https://purpurmc.org/download/purpur))

---
#### Placeholders



| Platzhalter                                      | Beschreibung                                                                                   |
| ------------------------------------------------ | ----------------------------------------------------------------------------------------------- |
| %teraperms_player_groups%                        | Gibt alle Gruppen zurück, denen der Spieler angehört.                                          |
| %teraperms_player_all_permission%                | Gibt alle Berechtigungen zurück, die dem Spieler direkt oder über Gruppen zugewiesen sind.     |
| %teraperms_player_permission%                    | Gibt alle direkt zugewiesenen Berechtigungen des Spielers zurück.                              |
| %teraperms_player_primary_group_name%            | Gibt den Namen der Primärgruppe des Spielers zurück.                                           |
| %teraperms_player_prefix%                        | Gibt das Präfix der Primärgruppe des Spielers zurück.                                          |
| %teraperms_group_list%                           | Gibt eine Liste aller existierenden Gruppen zurück.                                            |
| %teraperms_group_<Group>_permissions%            | Gibt alle Berechtigungen zurück, die direkt einer bestimmten Gruppe zugewiesen sind.           |
| %teraperms_group_<Group>_permissions_with_inherited% | Gibt alle Berechtigungen einer bestimmten Gruppe inklusive vererbter Berechtigungen zurück. |


---
## Java Beispielcode
---
## Beispielcode zum Auslesen und Setzen von Gruppeninformationen
```java

public void test(){
    String group = "admin";
    TeraGroup teraGroup;
    if(GroupManager.getGroupsListString().contains(group)){
        teraGroup = GroupManager.getGroup(group);
    }else{
        String prefix = "&4Admin";
        Integer weight = 2;
        boolean isStandard = false;
        List<String> permission = new ArrayList<>();
        List<String> inheritance = new ArrayList<>();
        teraGroup = new TeraGroup(group, prefix, weight, isStandard, permission, inheritance);
    }
    String name = teraGroup.getName();
    String prefix = teraGroup.getPrefix();
    List<String> permission = teraGroup.getPermission();
    List<String> inheritance = teraGroup.getInheritance();
    Integer weight = teraGroup.getWeight();
    boolean isStandard = teraGroup.isStandard();

    teraGroup.addPermission("test.permission");
    teraGroup.removePermission("test.permission");
    teraGroup.setPrefix("&4Admin");
    teraGroup.setWeight(2);
    teraGroup.addInheritance("vip");
    teraGroup.removeInheritance("vip");
    teraGroup.setStandard(false);

    GroupManager.saveTeraGroup(teraGroup);
    GroupManager.reloadGroup();
}
```

## Beispielcode zum Abrufen und Bearbeiten von Spielerinformationen

```java
    public void test(){
    Player player = Bukkit.getPlayer("Aaronkiller322");
    UserPermission userPermission = new UserPermission(player.getUniqueId());
    List<String> groups =  userPermission.getGroups();
    List<String> permission = userPermission.getPermission();
    List<String> all_permission = userPermission.getAllPermission();
    String primarygroup = userPermission.getPrimaryGroup();

    userPermission.addPermission("test.permission");
    userPermission.removePermission("test.permission");

    userPermission.addGroup("admin");
    userPermission.removeGroup("admin");

    userPermission.updatePermission();
}
```
