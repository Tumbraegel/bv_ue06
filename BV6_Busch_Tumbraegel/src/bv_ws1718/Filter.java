// BV Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1718;

import java.util.Arrays;

public class Filter {
	private final static int grayLevels = 256;
	private static int[] histogram = new int[grayLevels];

	public enum PredictionType {
		COPY("copy"), A("A (horizontal)"), B("B (vertikal)"), C("C (diagonal)"), AANDBMINUSC("A+B-C"), AANDBDIVIDEDBY2(
				"(A+B/2)"), ADAPTIVE("adaptiv");

		private final String name;

		private PredictionType(String s) {
			name = s;
		}

		public String toString() {
			return this.name;
		}
	};

	// prediction implementations go here:
	// |C|B|
	// |A|X|

	public void methodA(RasterImage src, RasterImage dst) {
		// P = A
		int pixelA, pixelX;
		int predictionError;
		for (int y = 0; y < src.height; y++) {
			pixelA = 128;
			for (int x = 0; x < src.width; x++) {
				pixelX = src.argb[y * src.width + x] & 0xFF;
				predictionError = pixelX - pixelA + 128;
				predictionError = noOverflow(predictionError);
				dst.argb[y * src.width + x] = 0xff000000 | (predictionError << 16) | (predictionError << 8)
						| (predictionError);
				pixelA = pixelX;
			}
		}
	}

	public void reconstructA(RasterImage src, RasterImage dst) {
		int predecessorPixel;
		int predictionError, prediction;
		for (int y = 0; y < src.height; y++) {
			predecessorPixel = 128;
			for (int x = 0; x < src.width; x++) {
				predictionError = (src.argb[y * src.width + (x)] & 0xFF) - 128;
				prediction = predictionError + predecessorPixel;
				prediction = noOverflow(prediction);
				dst.argb[y * dst.width + x] = 0xff000000 | (prediction << 16) | (prediction << 8) | (prediction);
				predecessorPixel = prediction;
			}
		}
	}

	public void methodB(RasterImage src, RasterImage dst) {
		// P = B
		int pixelB, pixelX;
		int predictionError;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				if (y == 0) {
					pixelB = 128;
				} else {
					pixelB = src.argb[(y - 1) * src.width + x] & 0xFF;
				}
				pixelX = src.argb[y * src.width + x] & 0xFF;
				predictionError = pixelX - pixelB + 128;
				predictionError = noOverflow(predictionError);
				dst.argb[y * src.width + x] = 0xff000000 | (predictionError << 16) | (predictionError << 8)
						| (predictionError);
			}
		}
	}

	public void reconstructB(RasterImage src, RasterImage dst) {
		int predecessorPixel;
		int predictionError;
		int prediction;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				if (y == 0) {
					predecessorPixel = 128;
				} else {
					predecessorPixel = dst.argb[(y - 1) * dst.width + x] & 0xFF;
				}
				predictionError = (src.argb[y * src.width + (x)] & 0xFF) - 128;
				prediction = predictionError + predecessorPixel;
				prediction = noOverflow(prediction);
				dst.argb[y * dst.width + x] = 0xff000000 | (prediction << 16) | (prediction << 8) | (prediction);

			}
		}

	}

	public void methodC(RasterImage src, RasterImage dst) {
		// P = C
		int pixelC, pixelX;
		int predictionError;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				// check margins
				if ((x < 1 && (y < 1 || y >= 1)) || y < 1 && x >= 1)
					pixelC = 128;
				else
					pixelC = src.argb[(y - 1) * src.width + (x - 1)] & 0xFF;

				pixelX = src.argb[y * src.width + x] & 0xFF;
				predictionError = pixelC - pixelX + 128;
				predictionError = noOverflow(predictionError);
				dst.argb[y * dst.width + x] = 0xff000000 | (predictionError << 16) | (predictionError << 8)
						| (predictionError);
			}
		}
	}

	public void reconstructC(RasterImage src, RasterImage dst) {
		int predecessorPixel;
		int predictionError;
		int prediction;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				if (y == 0 || x == 0) {
					predecessorPixel = 128;
				} else {
					predecessorPixel = dst.argb[(y - 1) * dst.width + (x - 1)] & 0xFF;
				}
				predictionError = (src.argb[y * src.width + (x)] & 0xFF) - 128;
				prediction = predecessorPixel - predictionError;
				prediction = noOverflow(prediction);
				dst.argb[y * dst.width + x] = 0xff000000 | (prediction << 16) | (prediction << 8) | (prediction);

			}
		}

	}

	public void methodAAndBMinusC(RasterImage src, RasterImage dst) {
		// P = A + B - C
		int pixelA, pixelB, pixelC, pixelX;
		int predictionError;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				// check margins
				if (x < 1 && y < 1) {
					pixelA = 128;
					pixelB = 128;
					pixelC = 128;
				} else if (x < 1 && y >= 1) {
					pixelA = 128;
					pixelB = 128;
					pixelC = 128;
				} else if (y < 1 && x >= 1) {
					pixelA = 128;
					pixelB = 128;
					pixelC = 128;
				} else {
					pixelA = src.argb[y * src.width + (x - 1)] & 0xFF;
					pixelB = src.argb[(y - 1) * src.width + x] & 0xFF;
					pixelC = src.argb[(y - 1) * src.width + (x - 1)] & 0xFF;
				}

				pixelX = src.argb[y * src.width + x] & 0xFF;
				predictionError = pixelX - (pixelA + pixelB - pixelC) + 128;
				predictionError = noOverflow(predictionError);
				dst.argb[y * src.width + x] = 0xff000000 | (predictionError << 16) | (predictionError << 8)
						| (predictionError);
			}
		}
	}

	public void reconstructAAndBMinusC(RasterImage src, RasterImage dst) {
		int pixelA, pixelB, pixelC, prediction;
		int predictionError;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				if (y == 0 && x == 0) {
					pixelC = pixelA = pixelB = 128;
				} else if (x == 0) {
					pixelA = 128;
					pixelB = dst.argb[(y - 1) * dst.width + (x)] & 0xFF;
					pixelC = 128;
				} else if (y == 0) {
					pixelB = 128;
					pixelA = dst.argb[(y) * dst.width + (x - 1)] & 0xFF;
					pixelC = 128;
				} else {
					pixelA = dst.argb[(y) * dst.width + (x - 1)] & 0xFF;
					pixelB = dst.argb[(y - 1) * dst.width + (x)] & 0xFF;
					pixelC = dst.argb[(y - 1) * dst.width + (x - 1)] & 0xFF;
				}
				predictionError = (src.argb[y * src.width + (x)] & 0xFF) - 128;
				prediction = predictionError + (pixelA + pixelB - pixelC);
				prediction = noOverflow(prediction);
				dst.argb[y * dst.width + x] = 0xff000000 | (prediction << 16) | (prediction << 8) | (prediction);

			}
		}

	}

	public void methodAAndBDividedBy2(RasterImage src, RasterImage dst) {
		// P = (A + B) / 2
		int pixelA, pixelB, pixelX;
		int predictionError;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				// check margins
				if ((x < 1 && (y < 1 || y >= 1)) || y < 1 && x >= 1) {
					pixelA = 128;
					pixelB = 128;
				} else {
					pixelA = src.argb[y * src.width + (x - 1)] & 0xFF;
					pixelB = src.argb[(y - 1) * src.width + x] & 0xFF;
				}

				pixelX = src.argb[y * src.width + x] & 0xFF;
				predictionError = pixelX - ((pixelA + pixelB) / 2) + 128;
				predictionError = noOverflow(predictionError);
				dst.argb[y * src.width + x] = 0xff000000 | (predictionError << 16) | (predictionError << 8)
						| (predictionError);
			}
		}
	}

	public void reconstructAAndBDividedBy2(RasterImage src, RasterImage dst) {
		int pixelA, pixelB, prediction;
		int predictionError;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				if (y == 0 && x == 0) {
					pixelA = pixelB = 128;
				} else if (x == 0) {
					pixelA = 128;
					pixelB = dst.argb[(y - 1) * dst.width + (x)] & 0xFF;
				} else if (y == 0) {
					pixelB = 128;
					pixelA = dst.argb[(y) * dst.width + (x - 1)] & 0xFF;
				} else {
					pixelA = dst.argb[(y) * dst.width + (x - 1)] & 0xFF;
					pixelB = dst.argb[(y - 1) * dst.width + (x)] & 0xFF;
				}
				predictionError = (src.argb[y * src.width + (x)] & 0xFF) - 128;
				prediction = predictionError + (pixelA + pixelB)/2;
				prediction = noOverflow(prediction);
				dst.argb[y * dst.width + x] = 0xff000000 | (prediction << 16) | (prediction << 8) | (prediction);
			}
		}

	}

	public void methodAdaptive(RasterImage src, RasterImage dst) {
		// if |A-C| < |B-C|, then P = B, otherwise P = A
		int pixelA, pixelB, pixelC, pixelX;
		int predictionError;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				pixelX = src.argb[y * src.width + x] & 0xFF;
				// check margins
				if ((x < 1 && (y < 1 || y >= 1)) || y < 1 && x >= 1) {
					pixelA = 128;
					pixelB = 128;
					pixelC = 128;
				} else {
					pixelA = src.argb[y * src.width + (x - 1)] & 0xFF;
					pixelB = src.argb[(y - 1) * src.width + x] & 0xFF;
					pixelC = src.argb[(y - 1) * src.width + (x - 1)] & 0xFF;
				}

				int firstCase = Math.abs(pixelA - pixelC);
				int secondCase = Math.abs(pixelB - pixelC);

				if (firstCase < secondCase)
					predictionError = pixelB - pixelX + 128;
				else
					predictionError = pixelA - pixelX + 128;

				predictionError = noOverflow(predictionError);

				dst.argb[y * src.width + x] = 0xff000000 | (predictionError << 16) | (predictionError << 8)
						| (predictionError);
			}
		}

	}

	public void reconstructAdaptive(RasterImage src, RasterImage dst) {
		int pixelA, pixelB, pixelC, prediction;
		int predictionError;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				if (y == 0 && x == 0) {
					pixelC = pixelA = pixelB = 128;
				} else if (x == 0) {
					pixelA = 128;
					pixelB = dst.argb[(y - 1) * dst.width + (x)] & 0xFF;
					pixelC = 128;
				} else if (y == 0) {
					pixelB = 128;
					pixelA = dst.argb[(y) * dst.width + (x - 1)] & 0xFF;
					pixelC = 128;
				} else {
					pixelA = dst.argb[(y) * dst.width + (x - 1)] & 0xFF;
					pixelB = dst.argb[(y - 1) * dst.width + (x)] & 0xFF;
					pixelC = dst.argb[(y - 1) * dst.width + (x - 1)] & 0xFF;
				}
				
				predictionError = (src.argb[y * src.width + (x)] & 0xFF) - 128;
				
				int firstCase = Math.abs(pixelA - pixelC);
				int secondCase = Math.abs(pixelB - pixelC);
				
				if (firstCase < secondCase)
					prediction = pixelB - predictionError;
				else
					prediction = pixelA -predictionError;

				prediction = noOverflow(prediction);
				dst.argb[y * dst.width + x] = 0xff000000 | (prediction << 16) | (prediction << 8) | (prediction);

			}
		}

	}

	public double getEntropy(RasterImage image) {

		double entropy = 0;
		int amount = (image.width * image.height);

		Arrays.fill(histogram, 0);
		for (int y = 0; y < image.height; y++) {
			for (int x = 0; x < image.width; x++) {
				int grayLevel = (image.argb[y * image.width + x] >> 16) & 0xff;
				histogram[grayLevel]++;
			}

			// p is the probability of seeing this particular value
			for (int i = 0; i < histogram.length; i++) {
				int count = histogram[i];
				double p = (double) count / amount;

				if (p > 0) {
					entropy = (entropy - p * Math.log(p) / Math.log(2));
				}
			}

		}
		return entropy;
	}

	private int noOverflow(int predictionError) {
		if (predictionError <= 0) {
			predictionError = 0;
		} else if (predictionError >= 255) {
			predictionError = 255;
		}
		return predictionError;
	}

	public void copy(RasterImage src, RasterImage dst) {
		// TODO: just copy the image
		for (int i = 0; i < dst.argb.length; i++) {
			dst.argb[i] = src.argb[i];
		}
	}

	public float getMSE(RasterImage origImg, RasterImage filteredImg) {
		float mse = 0.0f;
		for (int y = 0; y < origImg.height; y++) {
			for (int x = 0; x < origImg.width; x++) {
				int origPixel = origImg.argb[y*origImg.width+x]& 0xff;
				int filterdPixel = filteredImg.argb[y*origImg.width+x]& 0xff;
				mse += (origPixel-filterdPixel);
			}
		}
		return mse/origImg.argb.length;
	}
}