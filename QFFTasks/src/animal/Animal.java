package animal;

import java.util.Scanner;

public abstract class Animal {
    private static int count = 1;

    private String name;
    // Breed is final.
    private String breed;
    // True for male, false for female.
    private boolean sex;
    private int age;

    public void initialize(String breed) {
        // Initialize breed
        this.breed = breed;

        // Initialize name
        Scanner inputFromKeyboard = new Scanner(System.in);
        System.out.println("What name is this " + breed + "?");
        System.out.println("Press enter will give it a default name.");
        String input = inputFromKeyboard.nextLine();
        if (input.equals("")) {
            name = breed + " " + count;// A default name
            ++count;
        } else {
            name = input;
        }

        // Initialize sex
        System.out.println("What sex is this " + breed + "?");
        System.out.println("Input m or f.");
        System.out.println("(m stands for male, while f stands for female)");
        input = inputFromKeyboard.nextLine();
        while (!input.equals("m") && !input.equals("f")) {
            System.out.println("Please input m or f.");
            input = inputFromKeyboard.nextLine();
        }
        sex = input.equals("m");

        // Initialize age
        System.out.println("How old is this " + breed + "?");
        while (!inputFromKeyboard.hasNextInt()) {
            inputFromKeyboard.next();
        }
        age = inputFromKeyboard.nextInt();
    }

    public void showInfo() {
        System.out.println("Breed: " + breed);
        System.out.println("Name: " + name);
        System.out.println("Sex: " + (sex ? "male" : "female"));
        System.out.println("Age: " + age);
    }

    public void modify() {
        // Modify sex
        Scanner inputFromKeyboard = new Scanner(System.in);
        System.out.println("Input new sex: m or f.");
        System.out.println("(M stands for male,while f stands for female)");
        System.out.println("Press enter if remaining the original sex.");
        String newSex = inputFromKeyboard.nextLine();
        while (!newSex.equals("m") && !newSex.equals("f") && !newSex.equals("")) {
            System.out.println("Please press enter or input m / f .");
        }
        if (!newSex.equals("")) {
            sex = newSex.equals("m");
        }

        //Modify age
        System.out.println("Input new age: ");
        System.out.println("Press enter if remaining the original age.");
        String newAge = inputFromKeyboard.nextLine();
        if (!newAge.equals("")) {
            try {
                age = Integer.parseInt(newAge);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                while (!inputFromKeyboard.hasNextInt()) {
                    inputFromKeyboard.next();
                }
                age = inputFromKeyboard.nextInt();
            }
        }
    }

    public boolean getSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
