import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, String> roomDescriptions = new HashMap<>();
    private static final Map<String, Map<String, String>> roomExits = new HashMap<>();
    private static final Map<String, String> roomItems = new HashMap<>();
    private static final Map<String, String> npcs = new HashMap<>();
    private static final Map<String, Integer> enemyHealth = new HashMap<>();
    private static final String TREASURE_ROOM = "Treasure Room";
    private static final String SECRET_ROOM = "Secret Room";

    private static String currentRoom = "Entrance";
    private static int playerHealth = 100;
    private static boolean hasTreasure = false;
    private static boolean secretEndingUnlocked = false;
    private static List<String> inventory = new ArrayList<>();
    private static boolean gameOver = false;

    public static void main(String[] args) {
        setupGame();
        System.out.println("Welcome to Dungeons Treasure!");
        System.out.println("Use these commands to navigate and play the game:");
        System.out.println("go 'direction' : To navigate to a certain direction");
        System.out.println("talk : To talk to any NPC if present");
        System.out.println("attack : To attack an enemy NPC");
        System.out.println("use : To use any inventory item");

        while (!gameOver) {
            System.out.println("\nYou are in " + currentRoom);
            System.out.println(roomDescriptions.get(currentRoom));
            displayAvailableExits();
            System.out.print("\nEnter command: ");
            String command = scanner.nextLine().toLowerCase();
            handleCommand(command);
        }
    }

    private static void setupGame() {
        // Room Descriptions
        roomDescriptions.put("Entrance", "You are at the entrance of a mysterious land.");
        roomDescriptions.put("Forest", "You see tall trees and hear rustling leaves. There is a wise old man present.");
        roomDescriptions.put("Dungeon", "It's dark and smells of damp stone. You can see an enemy. You can choose to attack or sneak out.");
        roomDescriptions.put("Treasure Room", "The room glows with the light of untold riches!");
        roomDescriptions.put("Secret Room", "A hidden room filled with ancient artifacts. You can see a riddle.");

        // Room Exits
        roomExits.put("Entrance", Map.of("north", "Forest"));
        roomExits.put("Forest", Map.of("south", "Entrance", "east", "Dungeon", "west", "Secret Room"));
        roomExits.put("Dungeon", Map.of("west", "Forest", "east", "Treasure Room"));
        roomExits.put("Treasure Room", Map.of("west", "Dungeon"));
        roomExits.put("Secret Room", Map.of("east", "Forest"));

        // Room Items
        roomItems.put("Forest", "Potion");
        roomItems.put("Dungeon", "Key");

        // NPCs
        npcs.put("Forest", "A wise old man says: 'The path to the treasure is perilous. You need a key.'");
        npcs.put("Secret Room", "A mysterious figure says: 'Solve my riddle to unlock the secret.'");

        // Enemies
        enemyHealth.put("Dungeon", 50); // Enemy in Dungeon
    }

    private static void displayAvailableExits() {
        System.out.println("Exits: " + roomExits.get(currentRoom).keySet());
    }

    private static void handleCommand(String command) {
        if (command.startsWith("go ")) {
            movePlayer(command.substring(3));
        } else if (command.equals("check inventory")) {
            checkInventory();
        } else if (command.equals("talk")) {
            interactWithNPC();
        } else if (command.equals("attack")) {
            engageCombat();
        } else if (command.equals("use potion")) {
            usePotion();
        } else if (command.equals("solve riddle")) {
            solveRiddle();
        } else {
            System.out.println("Unknown command.");
        }
    }

    private static void movePlayer(String direction) {
        Map<String, String> exits = roomExits.get(currentRoom);
        if (exits.containsKey(direction)) {
            currentRoom = exits.get(direction);
            if (roomItems.containsKey(currentRoom)) {
                System.out.println("You found a " + roomItems.get(currentRoom) + "!");
                inventory.add(roomItems.remove(currentRoom));
            }
            if (currentRoom.equals(TREASURE_ROOM) && inventory.contains("Key")) {
                hasTreasure = true;
                System.out.println("You found the treasure! You win!");
                gameOver = true;
            } else if (currentRoom.equals(TREASURE_ROOM)) {
                System.out.println("The treasure is locked. You need a key.");
                currentRoom = "Dungeon";
            }
        } else {
            System.out.println("You can't go that way.");
        }
    }

    private static void checkInventory() {
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            System.out.println("Inventory: " + inventory);
        }
    }

    private static void interactWithNPC() {
        if (npcs.containsKey(currentRoom)) {
            System.out.println(npcs.get(currentRoom));
        } else {
            System.out.println("There's no one to talk to here.");
        }
    }

    private static void engageCombat() {
        if (enemyHealth.containsKey(currentRoom)) {
            int enemyHP = enemyHealth.get(currentRoom);
            System.out.println("An enemy attacks! Enemy Health: " + enemyHP);

            while (enemyHP > 0 && playerHealth > 0) {
                System.out.print("Attack or Run? ");
                String action = scanner.nextLine().toLowerCase();

                if (action.equals("attack")) {
                    int damage = new Random().nextInt(20) + 1;
                    enemyHP -= damage;
                    System.out.println("You hit the enemy for " + damage + " damage.");
                    if (enemyHP > 0) {
                        int enemyDamage = new Random().nextInt(15) + 1;
                        playerHealth -= enemyDamage;
                        System.out.println("The enemy hits you for " + enemyDamage + " damage. Your Health: " + playerHealth);
                    }
                } else if (action.equals("run")) {
                    System.out.println("You fled the combat!");
                    return;
                }
            }

            if (enemyHP <= 0) {
                System.out.println("You defeated the enemy!");
                enemyHealth.remove(currentRoom);
            }

            if (playerHealth <= 0) {
                System.out.println("You have died. Game Over.");
                gameOver = true;
            }
        } else {
            System.out.println("There's nothing to fight here.");
        }
    }

    private static void usePotion() {
        if (inventory.contains("Potion")) {
            playerHealth += 20;
            System.out.println("You used a Potion. Health restored to: " + playerHealth);
            inventory.remove("Potion");
        } else {
            System.out.println("You don't have a Potion to use.");
        }
    }

    private static void solveRiddle() {
        if (currentRoom.equals(SECRET_ROOM)) {
            System.out.println("Riddle: What has keys but can't open locks?");
            String answer = scanner.nextLine().toLowerCase();
            if (answer.equals("piano")) {
                System.out.println("Correct! The secret ending is unlocked.");
                secretEndingUnlocked = true;
                System.out.println("Congratulations! You have discovered the secret ending. You Win!");
                gameOver = true;
            } else {
                System.out.println("Wrong answer. Try again later.");
            }
        } else {
            System.out.println("There's no riddle to solve here.");
        }
    }
}
