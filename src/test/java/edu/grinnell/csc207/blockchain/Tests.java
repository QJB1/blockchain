package edu.grinnell.csc207.blockchain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class Tests {

    @Test
    @DisplayName("Placeholder Test")
    public void placeholderTest() {
        assertEquals(2, 1 + 1);
    }
    
    @Test
    @DisplayName("Hash validity and toString test")
    public void hashTest() {
        // Create a hash with first three bytes zero and a nonzero fourth byte.
        byte[] validBytes = new byte[]{0, 0, 0, 1};
        Hash hash = new Hash(validBytes);
        // isValid should return true
        assertTrue(hash.isValid(), "Hash should be valid when first three bytes are 0");
        // Expected hex string: each byte is represented with two hex digits.
        String expected = "00000001";
        assertEquals(expected, hash.toString(), "Hash string should match expected hex representation");
    }
    
    @Test
    @DisplayName("Block mining produces valid hash")
    public void blockMiningTest() {
        // Create a starting block (no previous hash) with initial amount.
        Block start = new Block(0, 300, null);
        // The mining process should produce a valid hash.
        assertTrue(start.getHash().isValid(), "Mined start block hash should be valid");
        // The string representation should mention block number 0.
        String str = start.toString();
        assertTrue(str.contains("Block 0"), "Block string should contain block number 0");
    }
    
    @Test
    @DisplayName("Block re-creation with nonce yields same hash")
    public void blockRecreationTest() {
        // Mine a block to determine a valid nonce.
        Block mined = new Block(0, 300, null);
        long nonce = mined.getNonce();
        Hash hash1 = mined.getHash();
        // Recreate the block using the provided nonce.
        Block recreated = new Block(0, 300, null, nonce);
        Hash hash2 = recreated.getHash();
        // The hashes (and their string forms) should be identical.
        assertEquals(hash1.toString(), hash2.toString(), "Recreated block with same nonce should yield same hash");
    }
    
    @Test
    @DisplayName("Test appending one block increases chain size to 2 and blockchain remains valid")
    public void testAppendOneBlock() {
        // Create a blockchain with an initial amount of 300.
        BlockChain chain = new BlockChain(300);
        // Append a block where Anna sends 150 to Bob (transaction: -150).
        Block candidate = chain.mine(-150);
        Block block1 = new Block(chain.getSize(), -150, chain.getHash(), candidate.getNonce());
        chain.append(block1);
        
        // After appending one block, chain size should be 2.
        assertEquals(2, chain.getSize(), "Chain should have 2 blocks after appending one block");
        assertTrue(chain.isValidBlockChain(), "Blockchain should be valid after appending one block");
    }
    
    @Test
    @DisplayName("Test appending two blocks increases chain size to 3 and blockchain remains valid")
    public void testAppendTwoBlocks() {
        // Create a blockchain with an initial amount of 300.
        BlockChain chain = new BlockChain(300);
        
        // Append first block: Anna sends 150 to Bob (-150).
        Block candidate1 = chain.mine(-150);
        Block block1 = new Block(chain.getSize(), -150, chain.getHash(), candidate1.getNonce());
        chain.append(block1);
        
        // Append second block: Bob sends 100 to Anna (100).
        Block candidate2 = chain.mine(100);
        Block block2 = new Block(chain.getSize(), 100, chain.getHash(), candidate2.getNonce());
        chain.append(block2);
        
        // After appending two blocks, chain size should be 3.
        assertEquals(3, chain.getSize(), "Chain should have 3 blocks after appending two blocks");
        assertTrue(chain.isValidBlockChain(), "Blockchain should be valid after appending two blocks");
    }
    
    @Test
    @DisplayName("Test removeLast removes a block when chain has more than Start block")
    public void testRemoveBlockFromNonStart() {
        // Create a blockchain with an initial amount of 300.
        BlockChain chain = new BlockChain(300);
        // Append a block.
        Block candidate = chain.mine(-150);
        Block block1 = new Block(chain.getSize(), -150, chain.getHash(), candidate.getNonce());
        chain.append(block1);
        // Chain should now have 2 blocks.
        assertEquals(2, chain.getSize(), "Chain should have 2 blocks before removal");
        
        // Remove the last block; removal should succeed (return true).
        boolean removed = chain.removeLast();
        assertTrue(removed, "removeLast should succeed when chain has more than one block");
        assertEquals(1, chain.getSize(), "Chain should have 1 block (Start block only) after removal");
    }
    
    @Test
    @DisplayName("Test removeLast fails when only Start block remains")
    public void testRemoveBlockWhenOnlyStart() {
        // Create a blockchain with only the Start block.
        BlockChain chain = new BlockChain(300);
        // Attempting to remove when only the Start block exists should fail.
        boolean removed = chain.removeLast();
        assertFalse(removed, "removeLast should fail when only Start block remains");
    }
    
    @Test
    @DisplayName("Invalid transaction should throw exception")
    public void invalidTransactionTest() {
        BlockChain chain = new BlockChain(300);
        // Create a block where Anna attempts to send 350 (more than she has)
        Block candidate = chain.mine(-350);
        Block invalidBlock = new Block(chain.getSize(), -350, chain.getHash(), candidate.getNonce());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chain.append(invalidBlock);
        });
        String expectedMessage = "Insufficient funds for Anna";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Expected insufficient funds error");
    }
}
