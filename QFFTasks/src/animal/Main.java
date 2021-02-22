package animal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {
    List<Animal> animals = new ArrayList<>();

    public static void main (String[] args) throws Exception {
        Main m = new Main();
        int flag;
        // Output prompting message
        m.printPromptingMessage();
        do {
            // Input choice and response
            flag = m.chooseOperation();
        } while (flag != 0);
    }

    public void printPromptingMessage() {
        System.out.println("Input numbers to perform the operation you want.");
        System.out.println("0. Exit the programme.");
        System.out.println("1. Show animals' number and name.");
        System.out.println("2. Add a new animal.");
        System.out.println("3. Delete an existed animal.");
        System.out.println("4. Modify an existed animal.");
        System.out.println("5. View an animal's all the information.");
        System.out.println("6. Sort the animals by age and print them.");
    }

    public int chooseOperation() throws Exception {
        Scanner inputFromKeyboard = new Scanner(System.in);
        // Make sure the input is an int
        while (!inputFromKeyboard.hasNextInt()) {
            inputFromKeyboard.next();
        }
        int choice = inputFromKeyboard.nextInt();
        switch (choice) {
            case 1:
                showAnimals();
                break;

            case 2:
                addAnimal();
                break;

            case 3:
                deleteAnimal();
                break;

            case 4:
                modifyAnimal();
                break;

            case 5:
                viewAnimal();
                break;

            case 6:
                sortByAge();
                showAnimals();
                break;

            default:
                System.out.println("Please input numbers between 0 and 6.");

            case 0:
        }
        return choice;
    }

    // Output all the animals like "number : name"
    public void showAnimals() {
        if (animals.size() == 0) {
            System.out.println("It's empty now.");
            return;
        }
        int i = 1;
        for (Animal element : animals) {
            System.out.println(i + " : " + element.getName());
            ++i;
        }
    }

    // Add a new animal
    public void addAnimal() throws Exception {
        System.out.println("What breed is the animal?");
        String breed = new Scanner(System.in).nextLine();
        String animalClassName = "animal." + breed.substring(0, 1).toUpperCase() + breed.substring(1);
        Class animalClass;
        try {
            animalClass = Class.forName(animalClassName);
        } catch (ClassNotFoundException e) {
            animalClass = DefaultAnimal.class;
        }
        Animal newAnimal = (Animal)animalClass.newInstance();
        newAnimal.initialize(breed);
        animals.add(newAnimal);
        System.out.println("Add successfully.");
    }

    // Delete an existed animal
    public void deleteAnimal() {
        Animal aAnimal = selectAnAnimalByNumber();
        if (aAnimal == null) {
            return;
        }
        animals.remove(aAnimal);
        System.out.println("Remove successfully.");
    }

    // Modify an existed animal
    public void modifyAnimal() {
        Animal aAnimal = selectAnAnimalByNumber();
        if (aAnimal == null) {
            return;
        }
        aAnimal.modify();
        System.out.println("Modify successfully.");
    }

    // View all the information of an animal
    public void viewAnimal() {
        Animal aAnimal = selectAnAnimalByNumber();
        if (aAnimal == null) {
            return;
        }
        aAnimal.showInfo();
    }

    // Return an animal by the number of it
    public Animal selectAnAnimalByNumber() {
        if (animals.size() == 0) {
            System.out.println("It's empty now.");
            return null;
        }
        System.out.println("Please input the number of the animal:");
        System.out.println("You can view the numbers of animals by typing 1 in the menu.");
        int index = new Scanner(System.in).nextInt() - 1;
        return animals.get(index);
    }

    // Sort animals by age and print
    public void sortByAge() {
        if (animals.size() == 0) {
            System.out.println("It's empty now.");
            return;
        }
        animals.sort(new Comparator<Animal>() {
            @Override
            public int compare(Animal o1, Animal o2) {
                return o1.getAge() - o2.getAge();
            }
        });
        System.out.println("Successfully sorted.");
    }
}
