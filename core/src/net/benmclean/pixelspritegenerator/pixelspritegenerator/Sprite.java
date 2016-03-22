package net.benmclean.pixelspritegenerator.pixelspritegenerator;

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
public class Sprite {
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

    public Sprite(int width, int height, Mask mask,
                  boolean colored, //=true,
                  double edgeBrightness, //=0.3,
                  double colorVariations, //=0.2,
                  double brightnessNoise, //=0.3,
                  double saturation, //=0.5,
                  long SEED) //=0
    {
        this.width = mask.width * (mask.mirrorX ? 2 : 1);
        this.height = mask.height * (mask.mirrorY ? 2 : 1);
        this.mask = mask;
        this.data = new int[this.width * this.height];
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
        this.initData();
        this.applyMask();
        this.generateRandomSample();

        if (this.mask.mirrorX) {
            this.mirrorX();
        }

        if (this.mask.mirrorY) {
            this.mirrorY();
        }

        this.generateEdges();
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
        return this.data[y * this.width + x];
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
        this.data[y * this.width + x] = value;
    }

    /**
     * The initData method initializes the sprite data to completely solid.
     *
     * @method initData
     * @returns {undefined}
     */
    public void initData() {
        int h = this.height;
        int w = this.width;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                this.setData(x, y, -1);
            }
        }
    }

    /**
     * The mirrorX method mirrors the template data horizontally.
     *
     * @method mirrorX
     * @returns {undefined}
     */
    public void mirrorX() {
        int h = this.height;
        int w = (int) Math.floor(this.width / (double) 2);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                this.setData(this.width - x - 1, y, this.getData(x, y));
            }
        }
    }

    /**
     * The mirrorY method mirrors the template data vertically.
     *
     * @method
     * @returns {undefined}
     */
    public void mirrorY() {
        int h = (int) Math.floor(this.height / (double) 2);
        int w = this.width;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                this.setData(x, this.height - y - 1, this.getData(x, y));
            }
        }
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
        int h = this.mask.height;
        int w = this.mask.width;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                this.setData(x, y, this.mask.data[y * w + x]);
            }
        }
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
        int h = this.height;
        int w = this.width;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int val = this.getData(x, y);
                if (val == 1) {
                    val = random.nextInt(2);
                } else if (val == 2) {
                    if (random.nextDouble() > 0.5) {
                        val = 1;
                    } else {
                        val = -1;
                    }
                }
                this.setData(x, y, val);
            }
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
        int h = this.height;
        int w = this.width;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (this.getData(x, y) > 0) {
                    if (y - 1 >= 0 && this.getData(x, y - 1) == 0) {
                        this.setData(x, y - 1, -1);
                    }
                    if (y + 1 < this.height && this.getData(x, y + 1) == 0) {
                        this.setData(x, y + 1, -1);
                    }
                    if (x - 1 >= 0 && this.getData(x - 1, y) == 0) {
                        this.setData(x - 1, y, -1);
                    }
                    if (x + 1 < this.width && this.getData(x + 1, y) == 0) {
                        this.setData(x + 1, y, -1);
                    }
                }
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
        boolean isVerticalGradient = random.nextDouble() > 0.5;
        double saturation = Math.max(Math.min(random.nextDouble() * this.saturation, 1), 0);
        double hue = random.nextDouble();
        int[] pixels = new int[height * width * 4];

        int ulen, vlen;
        if (isVerticalGradient) {
            ulen = this.height;
            vlen = this.width;
        } else {
            ulen = this.width;
            vlen = this.height;
        }

        for (int u = 0; u < ulen; u++) {
            // Create a non-uniform random number between 0 and 1 (lower numbers more likely)
            double isNewColor = Math.abs(((random.nextDouble() * 2 - 1)
                    + (random.nextDouble() * 2 - 1)
                    + (random.nextDouble() * 2 - 1)) / 3);
            // Only change the color sometimes (values above 0.8 are less likely than others)
            if (isNewColor > (1 - this.colorVariations)) {
                hue = random.nextDouble();
            }

            //MessageBox.Show(this.toString());

            for (int v = 0; v < vlen; v++) {
                int val, index;
                if (isVerticalGradient) {
                    val = this.getData(v, u);
                    index = (u * vlen + v) * 4;
                } else {
                    val = this.getData(u, v);
                    index = (v * ulen + u) * 4;
                }

                double[] rgb = new double[]{1, 1, 1};

                if (val != 0) {
                    if (this.colored) {
                        // Fade brightness away towards the edges
                        double brightness = Math.sin(((double) u / (double) ulen) * Math.PI) * (1 - this.brightnessNoise)
                                + random.nextDouble() * this.brightnessNoise;

                        // Get the RGB color value
                        rgb = this.hslToRgb(hue, saturation, brightness);

                        // If this is an edge, then darken the pixel
                        if (val == -1) {
                            rgb[0] *= this.edgeBrightness;
                            rgb[1] *= this.edgeBrightness;
                            rgb[2] *= this.edgeBrightness;
                        }

                    } else {
                        // Not colored, simply output black
                        if (val == -1) {
                            rgb = new double[]{0, 0, 0};
                        }
                    }
                }

                pixels[index + 0] = (int) (rgb[0] * 255);
                pixels[index + 1] = (int) (rgb[1] * 255);
                pixels[index + 2] = (int) (rgb[2] * 255);
                if (val != 0) {
                    pixels[index + 3] = 255;
                } else {
                    pixels[index + 3] = 0;
                }
            }
        }

        return pixels;
    }

    public String toString() {
        int h = this.height;
        int w = this.width;
        String output = "";
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double val = this.getData(x, y);
                output += val >= 0 ? " " + val : "" + val;
            }
            output += '\n';
        }
        return output;
    }
}
