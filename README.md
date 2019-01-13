Deluxe Paint Animation Decoder README
=====================================

Decode and/or display animations using the Deluxe Paint Animation (.ANM) file format.

## Usage

### Command line

To create a `.gif` of the animation, specify an output file with a `.gif` extension:

`java se.vidstedt.anmdecoder.Main <animation file> <file.gif>`


Dump the frames from the animation to individual `.ppm` files in the output directory:

`java se.vidstedt.anmdecoder.Main <animation file> <output directory>`

### GUI

Display the animation in a loop:

`java se.vidstedt.anmdecoder.Gui <animation file>`

## Examples

Example animations can be found in the `examples/` directory.
