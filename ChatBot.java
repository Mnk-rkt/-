package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

class ChatBot {

    private String userMessage;
    private String currentState;
    private ArrayList<String> commands = new ArrayList<>();
    private ArrayList<String> knownMessages = new ArrayList<>();
    private ArrayList<String> knownResponses = new ArrayList<>();
    private ArrayList<String> newKnownMessages;
    private ArrayList<String> newKnownResponses;

    private void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    void botStartup() {
        commands.add("silent");
        commands.add("getUp");
        commands.add("date");
        commands.add("time");
        commands.add("change");
        commands.add("quit");
        setCurrentState("AWAKE");
        loadFile("C:\\answers.txt");
        System.out.println("Hello! How can I help you?\n");
    }

    //Loads a file using FileReader and updates knownMessages and knownResponses
    private void loadFile(String path) {
        newKnownResponses = new ArrayList<>();
        newKnownMessages = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String currentLine;
            try {
                while ((currentLine = br.readLine()) != null) {
                    updateKnowledge(currentLine);
                }
            } catch (IOException e) {
                System.out.println("IOException while reading a file");
            }
            if (newKnownMessages.size() > 0) {
                knownMessages = newKnownMessages;
                knownResponses = newKnownResponses;
                System.out.println("*Responses pool updated successfully!*");
            } else {
                System.out.println("Could not update the responses. Specified file does not contain them.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    //Used to split the line of text into a message-response pair and save it.
    //Splitting method can be changed here
    private void updateKnowledge(String currentLine) {
        String splitter = ";";
        String[] knowledge = currentLine.split(splitter, 2);
        if (knowledge.length == 2) {
            newKnownMessages.add(knowledge[0].trim());
            newKnownResponses.add(knowledge[1].trim());
        }
    }

    void botRun() {
        while (true) {
            userMessage = getNewUserMessage();
            if (isACommand(userMessage)) {
                Execute(userMessage);
            } else {
                Response(userMessage);
            }
        }
    }

    private String getNewUserMessage() {
        return new Scanner(System.in).nextLine().trim();
    }

    private boolean isACommand(String input) {
        return commands.contains(userMessage);
    }

    private void Execute(String command) {
        if (currentState.equals("ASLEEP") && !(command.equals("getUp"))) {
            System.out.println("\n...");
        } else {
            switch (command) {
                case "getUp":
                    botGetUp();
                    break;
                case "silent":
                    botSleep();
                    break;
                case "date":
                    tellDate();
                    break;
                case "time":
                    tellTime();
                    break;
                case "change":
                    change();
                    break;
                case "quit":
                    botQuit();
            }

        }
    }

    private void botGetUp() {
        if (currentState.equals("AWAKE")) {
            System.out.println("I am listening already!");
        } else {
            setCurrentState("AWAKE");
            System.out.println("\nI was faking it, you know?");
        }
    }

    private void botSleep() {
        setCurrentState("ASLEEP");
        System.out.println("\nGoodnight!");
    }

    private void tellDate() {
        System.out.println("\nCurrent date is  " + LocalDate.now());
    }

    private void tellTime() {
        System.out.println("\nCurrent time is " + ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS));
    }

    private void change() {
        System.out.println("\nPlease specify the path:");
        loadFile(getNewUserMessage());
    }

    private void botQuit() {
        System.out.println("\nOk, bye!");
        System.exit(0);
    }

    // Response is created based on the index of recognized message.
    // This index is used to find response in "knownResponses"
    private void Response(String message) {
        if (currentState.equals("ASLEEP")) {
            System.out.println("...");
        } else if (knownMessages.contains(message)) {
            System.out.println("\n" + knownResponses.get(knownMessages.indexOf(message)));
        } else {
            System.out.println("\nI don't know what to say, sorry =(");
        }
    }
}
