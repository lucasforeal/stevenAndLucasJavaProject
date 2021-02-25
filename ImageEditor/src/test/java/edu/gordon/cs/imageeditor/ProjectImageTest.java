/*
 * Tests for ProjectImage class
 *
 * Copyright (c) 2021 - Russ Tuck
 */
package edu.gordon.cs.imageeditor;

import edu.gordon.cs.imageeditor.ProjectImage;
import java.awt.image.ColorModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author russtuck
 */
public class ProjectImageTest {

  public ProjectImageTest() {
  }

  @BeforeAll
  public static void setUpClass() {
  }

  @AfterAll
  public static void tearDownClass() {
  }

  @BeforeEach
  public void setUp() {
  }

  @AfterEach
  public void tearDown() {
  }


  /**
   * Utility method: Given an array of RGB pixels, zero out all but
   * the low order byte, making it a grayscale (or blue) image.
   */
  private void pixelsToGray(int[][] pixels) {
    for (int row = 0; row < pixels.length; row++) {
      for (int col = 0; col < pixels.length; col++) {
        pixels[row][col] = pixels[row][col] & 0xff;
      }
    }
/*
    for (int[] row: pixels) {
      for (int pixel: row) {

      }
    }
*/
  }


  /**
   * Test of lighten method.  Check for overflow.
   */
  @Test
  public void testLighten() {
    // Assume LIGHTEN_DARKEN_AMOUNT = 3
    int[][] before3x3 = { { 0, 1, 2 },
                          { 3, 100, 252 },
                          { 253, 254, 255 }
                        };
    int[][] after3x3  = { { 3, 4, 5 },
                          { 6, 103, 255 },
                          { 255, 255, 255 }
                        };
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    image3x3.lighten();
    int[][] result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(after3x3, result3x3);
  }

// STUDENTS: UNCOMMENT TESTS AS YOU IMPLEMENT METHODS

  /**
   * Test of darken method.  Check for underflow.
   */
  @Test
  public void testDarken() {
    // Assume LIGHTEN_DARKEN_AMOUNT = 3
    int[][] before3x3 = { { 0, 1, 2 },
                          { 3, 100, 252 },
                          { 253, 254, 255 }
                        };
    int[][] after3x3  = { { 0, 0, 0 },
                          { 0, 97, 249 },
                          { 250, 251, 252 }
                        };
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    image3x3.darken();
    int[][] result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(after3x3, result3x3);
  }

  /**
   * Test of negative method.  Gray only.
   */
  @Test
  public void testNegative() {
    int[][] before3x3 = { { 0, 1, 2 },
                          { 3, 100, 252 },
                          { 253, 254, 255 }
                        };
    int[][] after3x3  = { { 255, 254, 253 },
                          { 252, 155, 3 },
                          { 2, 1, 0 }
                        };
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    image3x3.negative();
    int[][] result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(after3x3, result3x3);
  }

  /**
   * Test of enhanceContrast method.  Check near max, min, and average.
   * Gray only.
   */
  public void testEnhanceContrast() {
    int[][] before3x3 = { { 0, 1, 2 },
                          { 94, 95, 96 },
                          { 60, 254, 255 }
                        };
    int[][] after3x3  = { { 0, 0, 1 },
                          { 93, 95, 97 },
                          { 59, 255, 255 }
                        };
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    image3x3.enhanceContrast();
    int[][] result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(after3x3, result3x3);
  }

  /**
   * Test of reduceContrast method.  Check near max, min, and average.
   * Gray only.
   */
  public void testReduceContrast() {
    int[][] before3x3 = { { 0, 1, 2 },
                          { 94, 95, 96 },
                          { 60, 254, 255 }
                        };
    int[][] after3x3  = { { 1, 2, 3 },
                          { 95, 95, 95 },
                          { 61, 253, 254 }
                        };
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    image3x3.reduceContrast();
    int[][] result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(after3x3, result3x3);
  }

  /**
   * Test of calculateHistogram method.
   */
/*
  @Test
  public void testCalculateHistogram() {
    int[][] before3x3 = { { 0, 1, 2 },
                          { 1, 95, 2 },
                          { 60, 2, 255 }
                        };
    // Convert before3x3 to color (duplicate value into 3 bytes)
    for (int r=0; r < 3; r++) {
      for (int c=0; c < 3; c++) {
        before3x3[r][c] = before3x3[r][c] * 0x010101;
      }
    }

    int[] histogram = new int[256];
    histogram[0] = 1;
    histogram[1] = 2;
    histogram[2] = 3;
    histogram[60] = 1;
    histogram[95] = 1;
    histogram[255] = 1;

    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    int[] result256 = image3x3.calculateHistogram();
    assertArrayEquals(histogram, result256);
  }
*/

  /**
   * Test of flipHorizontally method. Gray only.
   */
  @Test
  public void testFlipHorizontally() {
    int[][] before3x3 = { { 0, 50, 100 },
                          { 50, 95, 96 },
                          { 60, 254, 255 }
                        };
    int[][] after3x3  = { { 100, 50, 0 },
                          { 96, 95, 50 },
                          { 255, 254, 60 }
                        };
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    image3x3.flipHorizontally();
    int[][] result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(after3x3, result3x3);
  }

  /**
   * Test of flipVertically method. Gray only.
   */
  @Test
  public void testFlipVertically() {
    int[][] before3x3 = { { 0, 50, 100 },
                          { 50, 95, 96 },
                          { 60, 254, 255 }
                        };
    int[][] after3x3  = { { 60, 254, 255 },
                          { 50, 95, 96 },
                          { 0, 50, 100 }
                        };
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    image3x3.flipVertically();
    int[][] result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(after3x3, result3x3);
  }

  /**
   * Test of encryptDecrypt method. Gray only.
   * Calling it twice should get the original back.
   * Calling it once should change the image.
   */
/*
  @Test
  public void testEncryptDecrypt() {
    int[][] start3x3 = { { 0, 50, 100 },
                          { 50, 95, 96 },
                          { 60, 254, 255 }
                        };
                        
    int[][] copy3x3 =  { { 0, 50, 100 },
                         { 50, 95, 96 },
                         { 60, 254, 255 }
                       };
                       
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             start3x3);
    image3x3.encryptDecrypt(537);
    int[][] result3x3;
    result3x3 = image3x3.getPixels();
    for (int r=0; r < 3; r++) {
      for (int c=0; c < 3; c++) {
        assertNotEquals(copy3x3[r][c], result3x3[r][c]);
      }
    }

    image3x3.encryptDecrypt(537);
    result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(copy3x3, result3x3);
  }
*/

  /**
   * Test shiftHorizontally: right shift 1
   */
/*
  @Test
  public void testShiftHorizontally1() {
    int[][] before3x3 = { { 0, 1, 2 },
                          { 94, 95, 96 },
                          { 60, 254, 255 }
                        };
    int[][] after3x3  = { { 2, 0, 1 },
                          { 96, 94, 95 },
                          { 255, 60, 254 }
                        };
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    image3x3.shiftHorizontally(1);
    int[][] result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(after3x3, result3x3);
  }
*/

  /**
   * Test shiftHorizontally: no shift, 0
   */
/*
  @Test
  public void testShiftHorizontally0() {
    int[][] before3x3 = { { 0, 1, 2 },
                          { 94, 95, 96 },
                          { 60, 254, 255 }
                        };
    int[][] after3x3  = { { 0, 1, 2 },
                          { 94, 95, 96 },
                          { 60, 254, 255 }
                        };
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    image3x3.shiftHorizontally(0);
    int[][] result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(after3x3, result3x3);
  }
*/

  /**
   * Test shiftHorizontally: left shift 1
   */
/*
  @Test
  public void testShiftHorizontallyNeg1() {
    int[][] before3x3 = { { 0, 1, 2 },
                          { 94, 95, 96 },
                          { 60, 254, 255 }
                        };
    int[][] after3x3  = { { 1, 2, 0 },
                          { 95, 96, 94 },
                          { 254, 255, 60 }
                        };
    ProjectImage image3x3 = new ProjectImage(ColorModel.getRGBdefault(),
                                             before3x3);
    image3x3.shiftHorizontally(-1);
    int[][] result3x3 = image3x3.getPixels();
    pixelsToGray(result3x3);
    assertArrayEquals(after3x3, result3x3);
  }
*/

}
