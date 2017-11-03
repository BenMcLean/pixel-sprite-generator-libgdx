package net.benmclean.pixelspritegenerator.pixelspritegenerator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.Random;

/**
 * The Sprite class makes use of a Mask instance to generate a 2D sprite on a
 * HTML canvas.
 * <p>
 * var options = {
 * colored         : true,   // boolean
 * edgeBrightness  : 0.3,    // value from 0 to 1
 * colorVariations : 0.2,    // value from 0 to 1
 * brightnessNoise : 0.3,    // value from 0 to 1
 * saturation      : 0.5     // value from 0 to 1
 * }
 *
 * @param {mask}
 * @param {options}
 * @class Sprite
 * @constructor
 */
public class GenSprite {

    public int width;
    public int height;
    public Mask mask;
    public int[] data;
    public boolean colored;
    double edgeBrightness;
    double colorVariations;
    double brightnessNoise;
    double saturation;
    Random random;
    long SEED;

    public GenSprite(Mask mask,
                     boolean colored, //=true,
                     double edgeBrightness, //=0.3,
                     double colorVariations, //=0.2,
                     double brightnessNoise, //=0.3,
                     double saturation, //=0.5,
                     long SEED) //=0
    {
        width = mask.width * (mask.mirrorX ? 2 : 1);
        height = mask.height * (mask.mirrorY ? 2 : 1);
        this.mask = mask;
        this.data = new int[width * height];
        this.colored = colored;
        this.edgeBrightness = edgeBrightness;
        this.colorVariations = colorVariations;
        this.brightnessNoise = brightnessNoise;
        this.saturation = saturation;
        this.SEED = SEED;
        this.init();
    }

    /**
     * The init method calls all functions required to generate the sprite.
     *
     * @method init
     * @returns {undefined}
     */
    private void init() {
        initData();
        applyMask();
        generateRandomSample();
        if (mask.mirrorX) mirrorX();
        if (mask.mirrorY) mirrorY();
        generateEdges();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * The getData method returns the sprite template data at location (x, y)
     * <p>
     * -1 = Always border (black)
     * 0 = Empty
     * 1 = Randomly chosen Empty/Body
     * 2 = Randomly chosen Border/Body
     *
     * @param {x}
     * @param {y}
     * @method getData
     * @returns {undefined}
     */
    public int getData(int x, int y) {
        return data[y * width + x];
    }

    /**
     * The setData method sets the sprite template data at location (x, y)
     * <p>
     * -1 = Always border (black)
     * 0 = Empty
     * 1 = Randomly chosen Empty/Body
     * 2 = Randomly chosen Border/Body
     *
     * @param {x}
     * @param {y}
     * @param {value}
     * @method setData
     * @returns {undefined}
     */
    public void setData(int x, int y, int value) {
        data[y * width + x] = value;
    }

    /**
     * The initData method initializes the sprite data to completely solid.
     *
     * @method initData
     * @returns {undefined}
     */
    public void initData() {
        for (int y = 0; y < mask.height; y++)
            for (int x = 0; x < mask.width; x++)
                setData(x, y, -1);
    }

    /**
     * The mirrorX method mirrors the template data horizontally.
     *
     * @method mirrorX
     * @returns {undefined}
     */
    public void mirrorX() {
        for (int y = 0; y < mask.height; y++)
            for (int x = 0; x < mask.width; x++)
                setData(width - x - 1, y, getData(x, y));
    }

    /**
     * The mirrorY method mirrors the template data vertically.
     *
     * @method
     * @returns {undefined}
     */
    public void mirrorY() {
        for (int y = 0; y < mask.height; y++)
            for (int x = 0; x < mask.width; x++)
                setData(x, height - y - 1, getData(x, y));
    }

    /**
     * The applyMask method copies the mask data into the template data array at
     * location (0, 0).
     * <p>
     * (note: the mask may be smaller than the template data array)
     *
     * @method applyMask
     * @returns {undefined}
     */
    public void applyMask() {
        for (int y = 0; y < mask.height; y++)
            for (int x = 0; x < mask.width; x++)
                setData(x, y, mask.data[y * mask.width + x]);
    }

    /**
     * Apply a random sample to the sprite template.
     * <p>
     * If the template contains a 1 (internal body part) at location (x, y), then
     * there is a 50% chance it will be turned empty. If there is a 2, then there
     * is a 50% chance it will be turned into a body or border.
     * <p>
     * (feel free to play with this logic for interesting results)
     *
     * @method generateRandomSample
     * @returns {undefined}
     */
    public void generateRandomSample() {
        random = new Random(SEED);
        for (int y = 0; y < mask.height; y++)
            for (int x = 0; x < mask.width; x++)
                switch (getData(x, y)) {
                    case 1:
                        setData(x, y, random.nextInt(2));
                        break;
                    case 2:
                        setData(x, y, random.nextDouble() > 0.5 ? 1 : -1);
                        break;
                }
    }

    /**
     * This method applies edges to any template location that is positive in
     * value and is surrounded by empty (0) pixels.
     *
     * @method generateEdges
     * @returns {undefined}
     */
    public void generateEdges() {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                if (getData(x, y) > 0) {
                    if (y - 1 >= 0 && getData(x, y - 1) == 0)
                        setData(x, y - 1, -1);
                    if (y + 1 < height && getData(x, y + 1) == 0)
                        setData(x, y + 1, -1);
                    if (x - 1 >= 0 && getData(x - 1, y) == 0)
                        setData(x - 1, y, -1);
                    if (x + 1 < width && getData(x + 1, y) == 0)
                        setData(x + 1, y, -1);
                }
            }
    }

    /**
     * This method converts HSL color values to RGB color values.
     *
     * @param {h}
     * @param {s}
     * @param {l}
     * @param {result}
     * @method hslToRgb
     * @returns {result}
     */
    public double[] hslToRgb(double h, double s, double l) {
        double f, p, q, t;
        int i = (int) Math.floor(h * (double) 6);
        f = h * 6 - i;
        p = l * (1 - s);
        q = l * (1 - f * s);
        t = l * (1 - (1 - f) * s);
        switch (i % 6) {
            case 0:
                return new double[]{l, t, p};
            case 1:
                return new double[]{q, l, p};
            case 2:
                return new double[]{p, l, t};
            case 3:
                return new double[]{p, q, l};
            case 4:
                return new double[]{t, p, l};
            case 5:
                return new double[]{l, p, q};
            default:
                return null;
        }
    }

    /**
     * This method renders out the template data to a HTML canvas to finally
     * create the sprite.
     * <p>
     * (note: only template locations with the values of -1 (border) are rendered)
     *
     * @method renderPixelData
     * @returns {undefined}
     */
    public int[] renderPixelData() {
        random = new Random(SEED);
        int[] pixels = new int[width * height * 4];
        double saturation = Math.max(Math.min(random.nextDouble() * this.saturation, 1), 0);
        double hue = random.nextDouble();
        boolean isVerticalGradient = random.nextDouble() > 0.5;
        int ulen = isVerticalGradient ? height : width;
        int vlen = isVerticalGradient ? width : height;

        for (int u = 0; u < ulen; u++) {
            // Create a non-uniform random number between 0 and 1 (lower numbers more likely)
            double isNewColor = Math.abs(((random.nextDouble() * 2 - 1)
                    + (random.nextDouble() * 2 - 1)
                    + (random.nextDouble() * 2 - 1)) / 3);
            // Only change the color sometimes (values above 0.8 are less likely than others)
            if (isNewColor > 1 - colorVariations) hue = random.nextDouble();

            for (int v = 0; v < vlen; v++) {
                int val, index;
                if (isVerticalGradient) {
                    val = getData(v, u);
                    index = (u * vlen + v) * 4;
                } else {
                    val = getData(u, v);
                    index = (v * ulen + u) * 4;
                }

                double[] rgb = new double[]{1, 1, 1};

                if (val != 0) {
                    if (colored) {
                        // Fade brightness away towards the edges
                        double brightness = Math.sin(((double) u / (double) ulen) * Math.PI) * (1 - brightnessNoise)
                                + random.nextDouble() * brightnessNoise;

                        // Get the RGB color value
                        rgb = hslToRgb(hue, saturation, brightness);

                        // If this is an edge, then darken the pixel
                        if (val == -1) {
                            rgb[0] *= edgeBrightness;
                            rgb[1] *= edgeBrightness;
                            rgb[2] *= edgeBrightness;
                        }
                    } else if (val == -1)
                        // Not colored, simply output black
                        rgb = new double[]{0, 0, 0};
                }

                pixels[index + 0] = (int) (rgb[0] * 255);
                pixels[index + 1] = (int) (rgb[1] * 255);
                pixels[index + 2] = (int) (rgb[2] * 255);
                pixels[index + 3] = val == 0 ? 0 : 255;
            }
        }
        return pixels;
    }

    public String toString() {
        String output = "";
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double val = getData(x, y);
                output += val >= 0 ? " " + val : "" + val;
            }
            output += '\n';
        }
        return output;
    }

    /**
     * The Mask class defines a 2D template form which sprites can be generated.
     *
     * @param {data}    Integer array describing which parts of the sprite should be
     *                  empty, body, and border. The mask only defines a semi-ridgid stucture
     *                  which might not strictly be followed based on randomly generated numbers.
     *                  <p>
     *                  -1 = Always border (black)
     *                  0 = Empty
     *                  1 = Randomly chosen Empty/Body
     *                  2 = Randomly chosen Border/Body
     * @param {width}   Width of the mask data array
     * @param {height}  Height of the mask data array
     * @param {mirrorX} A boolean describing whether the mask should be mirrored on the x axis
     * @param {mirrorY} A boolean describing whether the mask should be mirrored on the y axis
     * @class Mask
     * @constructor
     */
    public static class Mask {
        public int width;
        public int height;
        public int[] data;
        public boolean mirrorX;
        public boolean mirrorY;

        public Mask(int[] data, int width, int height, boolean mirrorX, boolean mirrorY) {
            this.width = width;
            this.height = height;
            this.data = data;
            this.mirrorX = mirrorX;
            this.mirrorY = mirrorY;
        }
    }

    public Pixmap generatePixmap() {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        int[] spritePixels = renderPixelData();
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int i = (width * y + x) * 4;
                int red = spritePixels[i];
                int green = spritePixels[i + 1];
                int blue = spritePixels[i + 2];
                int alpha = spritePixels[i + 3];
                pixmap.drawPixel(x, y, Color.rgba8888(red / 255f, green / 255f, blue / 255f, alpha / 255f));
            }
        return pixmap;
    }

    public static Pixmap generatePixmap(Mask mask,
                                        boolean colored, //=true,
                                        double edgeBrightness, //=0.3,
                                        double colorVariations, //=0.2,
                                        double brightnessNoise, //=0.3,
                                        double saturation, //=0.5,
                                        long SEED) //=0
    {
        return new GenSprite(mask, colored, edgeBrightness, colorVariations, brightnessNoise, saturation, SEED).generatePixmap();
    }
}
