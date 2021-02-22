package animal;

import java.util.Scanner;

public class Dog extends Animal {
    private String color;

    @Override
    public void initialize(String breed) {
        super.initialize(breed);
        System.out.println("What color is this dog?");
        color = new Scanner(System.in).nextLine();
    }

    @Override
    public void showInfo() {
        super.showInfo();
        System.out.println("Color: " + color);
    }

    @Override
    public void modify() {
        super.modify();
        System.out.println("Input new color:");
        System.out.println("Press enter if remaining the original color.");
        Scanner inputFromKeyboard = new Scanner(System.in);
        String newColor = inputFromKeyboard.nextLine();
        if (!newColor.equals("")) {
            color = newColor;
        }
    }
}
