package se.vidstedt.anmdecoder;

import java.io.*;

class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: <infile.anm> <out directory>");
            System.exit(1);
        }
        File inFile = new File(args[0]);
        File outDirectory = new File(args[1]);
        Animation animation = new AnmReader().read(inFile);
        new AnimationDumper(animation).dump(outDirectory);
    }
}
