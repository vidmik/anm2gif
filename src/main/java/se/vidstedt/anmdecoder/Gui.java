package se.vidstedt.anmdecoder;

import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gui extends Application {
    private static final int SCENE_WIDTH = 800;

    static class Animator implements Runnable {
        private final Animation animation;
        private final WritableImage image;
        private final Label label;
        private final byte[] pixels;

        private volatile boolean exit = false;

        Animator(Animation animation, WritableImage image, Label label) {
            this.animation = animation;
            this.image = image;
            this.label = label;

            this.pixels = new byte[animation.getHeader().getWidth() * animation.getHeader().getHeight()];
        }

        void exitNow() {
            exit = true;
        }

        private Color getColor(Palette palette, int i) {
            int[] color = palette.getPaletteColorComponents(i);
            return Color.rgb(color[0], color[1], color[2]);
        }

        private void showRecord(int index) {
            Record record = animation.getRecord(index);
            record.decode(pixels);
            PixelWriter writer = image.getPixelWriter();
            for (int y = 0; y < animation.getHeader().getHeight(); y++) {
                for (int x = 0; x < animation.getHeader().getWidth(); x++) {
                    int paletteIndex = Byte.toUnsignedInt(pixels[y * animation.getHeader().getWidth() + x]);
                    writer.setColor(x, y, getColor(animation.getPalette(), paletteIndex));
                }
            }
            label.setText("Record: " + index);
        }

        public void run() {
            while (!exit) {
                for (int i = 0; i < animation.getRecords().length; i++) {
                    Record r = animation.getRecord(i);
                    if (r != null) {
                        int j = i;
                        Platform.runLater(() -> showRecord(j));
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (getParameters().getRaw().size() != 1) {
            System.out.println("Usage: <infile.anm>");
            System.exit(1);
        }
        Animation animation = new AnmReader().read(new File(getParameters().getRaw().get(0)));
        ImageView imageView = new ImageView();
        WritableImage writableImage = new WritableImage(animation.getHeader().getWidth(), animation.getHeader().getHeight());
        imageView.setImage(writableImage);
        Label label = new Label("Record: ");
        VBox box = new VBox();
        box.getChildren().addAll(label, imageView);
        Scene scene = new Scene(box);

        float scale = (float)animation.getHeader().getHeight() / animation.getHeader().getWidth();
        Dimension2D scaledImageDimensions = new Dimension2D(SCENE_WIDTH, SCENE_WIDTH * scale);
        Dimension2D stageDimensions = new Dimension2D(SCENE_WIDTH, scaledImageDimensions.getHeight() + label.getPrefHeight());

        primaryStage.setWidth(stageDimensions.getWidth());
        primaryStage.setHeight(stageDimensions.getHeight());
        imageView.setFitWidth(scaledImageDimensions.getWidth());
        imageView.setFitHeight(scaledImageDimensions.getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                case Q:
                    Platform.exit();
                    break;
            }
        });

        Thread t = new Thread(new Animator(animation, writableImage, label));
        t.setDaemon(true);
        t.start();
    }
}
