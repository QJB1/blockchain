package edu.grinnell.csc207.blockchain;

import java.util.Scanner;

public class BlockChainDriver {

    /**
     * The main entry point for the block chain program.
     *
     * @param args the command-line arguments (first argument is the initial amount)
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java BlockChainDriver <initial amount>");
            return;
        }
        int initial = 0;
        try {
            initial = Integer.parseInt(args[0]);
            if (initial < 0) {
                System.err.println("Initial amount must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid initial amount.");
            return;
        }

        BlockChain blockchain = new BlockChain(initial);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            // Print current blockchain
            System.out.println(blockchain.toString());
            System.out.print("Command? ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    System.out.println("Valid commands:");
                    System.out.println("    mine: discovers the nonce for a given transaction");
                    System.out.println("    append: appends a new block onto the end of the chain");
                    System.out.println("    remove: removes the last block from the end of the chain");
                    System.out.println("    check: checks that the block chain is valid");
                    System.out.println("    report: reports the balances of Alice and Bob");
                    System.out.println("    help: prints this list of commands");
                    System.out.println("    quit: quits the program");
                    break;
                case "mine":
                    System.out.print("Amount transferred? ");
                    int mineAmt;
                    try {
                        mineAmt = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount.");
                        break;
                    }
                    Block minedBlock = blockchain.mine(mineAmt);
                    System.out.println("amount = " + mineAmt + ", nonce = " + minedBlock.getNonce());
                    break;
                case "append":
                    System.out.print("Amount transferred? ");
                    int appendAmt;
                    try {
                        appendAmt = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount.");
                        break;
                    }
                    System.out.print("Nonce? ");
                    long nonce;
                    try {
                        nonce = Long.parseLong(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid nonce.");
                        break;
                    }
                    int blockNum = blockchain.getSize();
                    Block candidate = new Block(blockNum, appendAmt, blockchain.getHash(), nonce);
                    try {
                        blockchain.append(candidate);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error appending block: " + e.getMessage());
                    }
                    break;
                case "remove":
                    if (!blockchain.removeLast()) {
                        System.out.println("Cannot remove genesis block.");
                    }
                    break;
                case "check":
                    if (blockchain.isValidBlockChain()) {
                        System.out.println("Chain is valid!");
                    } else {
                        System.out.println("Chain is invalid!");
                    }
                    break;
                case "report":
                    blockchain.printBalances();
                    break;
                case "quit":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid command. Type 'help' for a list of commands.");
                    break;
            }
        }
        scanner.close();
    }
}
