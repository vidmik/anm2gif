package se.vidstedt.anm2gif;

import java.io.*;

class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: <infile.anm> <out.gif | out directory>");
            System.exit(1);
        }
        File inFile = new File(args[0]);
        File out = new File(args[1]);
        Animation animation = new AnmReader().read(inFile);
        if (out.getName().endsWith(".gif")) {
            new AnimationDumper(animation).dumpGif(out);
        } else {
            new AnimationDumper(animation).dump(out);
        }
    }
}
