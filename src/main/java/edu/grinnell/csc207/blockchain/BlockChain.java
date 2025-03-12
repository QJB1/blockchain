package edu.grinnell.csc207.blockchain;

/**
 * A linked list of hash-consistent blocks representing a ledger of
 * monetary transactions.
 */
public class BlockChain {
    private Node first;
    private Node last;

    // Nested Node class for the linked list structure.
    private class Node {
        Block block;
        Node next;

        Node(Block block) {
            this.block = block;
            this.next = null;
        }
    }

    /**
     * Constructs a blockchain with a single genesis block.
     * The start block stores the initial amount for Anna.
     *
     * @param initial the initial non-negative amount
     */
    public BlockChain(int initial) {
        Block start = new Block(0, initial, null);
        first = new Node(start);
        last = first;
    }

    /**
     * Returns the number of blocks in the blockchain.
     *
     * @return the size of the chain
     */
    public int getSize() {
        int count = 0;
        for (Node curr = first; curr != null; curr = curr.next) {
            count++;
        }
        return count;
    }

    /**
     * Mines a new candidate block for a given transaction amount.
     * Does not append the block to the chain.
     *
     * @param amount the transaction amount
     * @return the mined block
     */
    public Block mine(int amount) {
        int newBlockNum = getSize();
        Block candidate = new Block(newBlockNum, amount, last.block.getHash());
        return candidate;
    }

    /**
     * Appends a new block to the chain.
     * Throws IllegalArgumentException if the block is not valid with respect to the chain.
     *
     * @param blk the block to append
     */
    public void append(Block blk) {
        // Check block number continuity.
        if (blk.getNum() != getSize()) {
            throw new IllegalArgumentException("Block number is incorrect.");
        }
        // Check that the previous hash in blk matches the last block's hash.
        if (!blk.getPrevHash().equals(last.block.getHash())) {
            throw new IllegalArgumentException("Previous hash does not match.");
        }
        // Check that the transaction is valid given current balances.
        int[] balances = computeBalances();
        int amt = blk.getAmount();
        if (amt < 0) { // Anna sends money to Bob.
            if (balances[0] < Math.abs(amt)) {
                throw new IllegalArgumentException("Insufficient funds for Anna.");
            }
        } else if (amt > 0) { // Bob sends money to Anna.
            if (balances[1] < amt) {
                throw new IllegalArgumentException("Insufficient funds for Bob.");
            }
        }
        // Append block.
        Node newNode = new Node(blk);
        last.next = newNode;
        last = newNode;
    }

    /**
     * Removes the last block from the blockchain.
     * Does nothing if the chain only contains the genesis block.
     *
     * @return true if a block was removed, false otherwise
     */
    public boolean removeLast() {
        if (first == last) {
            return false;
        }
        Node curr = first;
        // Traverse to the second-to-last node.
        while (curr.next != last) {
            curr = curr.next;
        }
        curr.next = null;
        last = curr;
        return true;
    }

    /**
     * Returns the hash of the last block in the chain.
     *
     * @return the last block’s hash
     */
    public Hash getHash() {
        return last.block.getHash();
    }

    /**
     * Validates the blockchain by checking the hash chain and transaction legality.
     *
     * @return true if the blockchain is valid, false otherwise
     */
    public boolean isValidBlockChain() {
        if (first == null)
            return false;
        // Initialize balances: index 0 = Anna, index 1 = Bob.
        int anna = first.block.getAmount();
        int bob = 0;
        Node prev = first;
        Node curr = first.next;
        while (curr != null) {
            Block blk = curr.block;
            // Check block number continuity.
            if (blk.getNum() != prev.block.getNum() + 1) {
                return false;
            }
            // Check previous hash link.
            if (!blk.getPrevHash().equals(prev.block.getHash())) {
                return false;
            }
            // Check that block’s hash is valid.
            if (!blk.getHash().isValid()) {
                return false;
            }
            // Process transaction.
            int amt = blk.getAmount();
            if (amt < 0) {
                int transfer = Math.abs(amt);
                if (anna < transfer) {
                    return false;
                }
                anna -= transfer;
                bob += transfer;
            } else if (amt > 0) {
                if (bob < amt) {
                    return false;
                }
                bob -= amt;
                anna += amt;
            }
            prev = curr;
            curr = curr.next;
        }
        return true;
    }

    // Helper method to compute current balances.
    // Returns an array where index 0 is Anna’s balance and index 1 is Bob’s.
    private int[] computeBalances() {
        int anna = first.block.getAmount();
        int bob = 0;
        Node curr = first.next;
        while (curr != null) {
            int amt = curr.block.getAmount();
            if (amt < 0) {
                int transfer = Math.abs(amt);
                anna -= transfer;
                bob += transfer;
            } else if (amt > 0) {
                bob -= amt;
                anna += amt;
            }
            curr = curr.next;
        }
        return new int[]{anna, bob};
    }

    /**
     * Prints the current balances in the format "Alice: <amt>, Bob: <amt>".
     */
    public void printBalances() {
        int[] balances = computeBalances();
        System.out.println("Alice: " + balances[0] + ", Bob: " + balances[1]);
    }

    /**
     * Returns a string representation of the entire blockchain,
     * listing each block on a new line.
     *
     * @return the blockchain string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node curr = first;
        while (curr != null) {
            sb.append(curr.block.toString()).append("\n");
            curr = curr.next;
        }
        return sb.toString().trim();
    }
}




