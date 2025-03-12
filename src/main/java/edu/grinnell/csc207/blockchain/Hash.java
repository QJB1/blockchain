package edu.grinnell.csc207.blockchain;

import java.util.Arrays;

/**
 * A wrapper class over a hash value (a byte array).
 */
public class Hash {

    private byte[] hash;
    /**
     * Constructs a new Hash object that contains the given hash (as an array of bytes).
     * 
     * @param the hash data in byte form
     */
    public Hash(byte[] data){
        hash = data; // error: cannot convert from byte[] to Hash val
    } 

    /**
     * Returns the hash contained in this object.
     *
     * @return the hash contained in this object
     */
    public byte[] getData(){
        return hash;
    }

    /**
     * Returns true if this hash meets the criteria for validity, i.e., its first three indices contain zeroes.
     *
     * @return true or false depending on if it meets criteria
     */
    public boolean isValid(){
        if(hash.length < 3) return false;
        return hash[0] == 0 && hash[1] == 0 && hash[2] == 0;
    }

    /**
     * Returns the string representation of the hash as a string of hexadecimal digits, 2 digits per byte.
     *
     * @return the string representation of the hash
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", Byte.toUnsignedInt(b)));
        }
        return sb.toString();
    }
    // Str -> Byte Arr -> Str

    /**
     * returns true if this hash is structurally equal to the other object.
     * 
     * @param other the other object to compare (the hash we're comparing this hash to)
     * @return true if equal, false otherwise (structurally equal)
     */
    public boolean equals(Object other){
        if (this == other){
            return true;
        }
        if (!(other instanceof Hash)){
            return false;
        }
        Hash o = (Hash) other;
        return Arrays.equals(this.hash, o.hash);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }
}
