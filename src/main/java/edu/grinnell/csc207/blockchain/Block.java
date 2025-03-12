package edu.grinnell.csc207.blockchain;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A single block of a blockchain.
 */
public class Block {
    // TODO: fill me in!
    private int num;
    private int amount;
    private Hash prevHash;
    private long nonce;
    private Hash hash;

    /**
     * Constructs a new Block by mining for a valid nonce.
     *
     * @param num      the block number in the blockchain
     * @param amount   the dollar amount transferred in this block
     * @param prevHash the hash of the previous block (or null for start block)
     */
    public Block(int num, int amount, Hash prevHash){
        this.num = num;
        this.amount = amount;
        this.prevHash = prevHash;
        mineBlock();
    }

    /**
     * Constructs a new Block using a provided nonce.
     *
     * @param num      the block number
     * @param amount   the dollar amount transferred
     * @param prevHash the previous block’s hash
     * @param nonce    the nonce value to use (no mining performed)
     */
    public Block(int num, int amount, Hash prevHash, long nonce){
        this.num = num;
        this.amount = amount;
        this.prevHash = prevHash;
        this.nonce = nonce;
        this.hash = computeHash(nonce);
    }

    /**
     * Returns the number of the block in the blockchain.
     *
     * @return the block number order
     */
    int getNum(){
        return num;
    }
    /**
     * Returns the dollar amount amount transferred between the two parties in that transaction.
     *
     * @return the dollar amount data
     */
    public int getAmount(){
        return amount;
    }
    /**
     * Returns the nonce value.
     *
     * @return the nonce value
     */
    public long getNonce(){
        return nonce;
    }
    /**
     * Returns the hash of the previous block in the blockchain.
     *
     * @return the prevHash value
     */
    public Hash getPrevHash(){
        return prevHash;
    }
    /**
     * Returns the hash of this block.
     *
     * @return the hash value
     */
    public Hash getHash(){
        return hash;
    }

    /**
     * Returns the string representation of the block.
     *
     * Format: 
     * Block <num> (Amount: <amt>, Nonce: <nonce>, prevHash: <prevHash>, hash: <hash>)
     */
    public String toString(){
        return String.format("Block %d (Amount: %d, Nonce: %d, prevHash: %s, hash: %s)",
                num, amount, nonce, (prevHash == null ? "null" : prevHash.toString()), hash.toString());
    }

    // Mines for a valid nonce by iterating from 0 until a hash meets the validity criteria.
    private void mineBlock() {
        long candidate = 0;
        while (true) {
            Hash candidateHash = computeHash(candidate);
            if (candidateHash.isValid()) {
                this.nonce = candidate;
                this.hash = candidateHash;
                break;
            }
            candidate++;
        }
    }

    // Computes the block's hash using its number, amount, previous hash (if any), and a given nonce.
    private Hash computeHash(long nonceValue) {
        try {
            MessageDigest md = MessageDigest.getInstance("sha-256");
            // Add block number
            md.update(ByteBuffer.allocate(4).putInt(num).array());
            // Add amount
            md.update(ByteBuffer.allocate(4).putInt(amount).array());
            // Add previous hash bytes if exists 
            if (prevHash != null) {
                md.update(prevHash.getData());
            }
            // Add nonce
            md.update(ByteBuffer.allocate(8).putLong(nonceValue).array());
            byte[] hashBytes = md.digest();
            return new Hash(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
