package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model;

import com.gmail.pasquarelli.brandon.destinyapi.utils.Conversions;

import java.util.ArrayList;

public class SocketFilter {

    ArrayList<String> unsignedSocketHashes;
    ArrayList<Integer> signedSocketHashes;

    public SocketFilter() { }

    public SocketFilter(ArrayList<String> unsigned) {
        setUnsignedSocketHashes(unsigned);
    }

    public void setUnsignedSocketHashes(ArrayList<String> unsigned){
        if (unsignedSocketHashes == null)
            unsignedSocketHashes = new ArrayList<>();

        if (signedSocketHashes == null)
            signedSocketHashes = new ArrayList<>();

        unsignedSocketHashes.clear();
        unsignedSocketHashes.addAll(unsigned);

        signedSocketHashes.clear();
        for (String hash : unsignedSocketHashes)
            signedSocketHashes.add(Conversions.parseUnsignedInt(hash));
    }

    public ArrayList<String> getUnsignedSocketHashes() {
        return unsignedSocketHashes;
    }

    public ArrayList<Integer> getSignedSocketHashes() {
        return signedSocketHashes;
    }
}
