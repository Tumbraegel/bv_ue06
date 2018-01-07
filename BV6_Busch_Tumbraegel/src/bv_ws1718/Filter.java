// BV Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1718;

public class Filter {
	private final static int grayLevels = 256;
	private static int[] histogram = new int[grayLevels];

	public enum PredictionType {
		COPY("copy"), A("A (horizontal)"), B("B (vertikal)"), C("C (diagonal)"), AANDBMINUSC("A+B-C"), AANDBDIVIDEDBY2(
				"(A+B/2)"), ADAPTIV("adaptiv");

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
		int predicationError, predication;
		for (int y = 0; y < src.height; y++) {
			predecessorPixel = 128;
			for (int x = 0; x < src.width; x++) {
				predicationError = (src.argb[y * src.width + (x)] & 0xFF) - 128;
				predication = predicationError + predecessorPixel;
				predication = noOverflow(predication);
				dst.argb[y * dst.width + x] = 0xff000000 | (predication << 16) | (predication << 8) | (predication);
				predecessorPixel = predication;
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
					pixelB = 0;
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
		int predicationError, predication;
		for (int y = 0; y < src.height; y++) {
			predecessorPixel = 128;
			for (int x = 0; x < src.width; x++) {
				predicationError = (src.argb[y * src.width + (x)] & 0xFF) - 128;
				predication = predicationError + predecessorPixel;
				predication = noOverflow(predication);
				dst.argb[y * dst.width + x] = 0xff000000 | (predication << 16) | (predication << 8) | (predication);
				predecessorPixel = predication;
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
				if (x < 1 && y < 1)
					pixelC = 128;
				else if (x < 1 && y >= 1)
					pixelC = 128;
				else if (y < 1 && x >= 1)
					pixelC = 128;
				else
					pixelC = src.argb[(y - 1) * src.width + (x - 1)] & 0xFF;

				pixelX = src.argb[y * src.width + x] & 0xFF;
				predictionError = pixelC - pixelX + 128;
				predictionError = noOverflow(predictionError);
				dst.argb[y * src.width + x] = 0xff000000 | (predictionError << 16) | (predictionError << 8)
						| (predictionError);
			}
		}
	}

	private int noOverflow(int predictionError) {
		if (predictionError <= 0) {
			predictionError = 0;
		} else if (predictionError >= 255) {
			predictionError = 255;
		}
		return predictionError;
	}

	public void reconstructC(RasterImage src, RasterImage dst) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
				calculateEntropy(dst);
			}
		}

	}

	public void reconstructAdaptive(RasterImage src, RasterImage dst) {
		// TODO Auto-generated method stub

	}

	static double calculateEntropy(RasterImage image) {

		double entropy = 0;
		int amount = (image.width * image.height);

		// p is the probability of seeing this particular value
		for (int i = 0; i < histogram.length; i++) {
			int count = histogram[i]++;
			double p = (double) count / amount;

			if (p > 0) {
				entropy = (entropy - p * Math.log(p) / Math.log(2));
			}
		}
		return entropy;
	}

	public void copy(RasterImage src, RasterImage dst) {
		// TODO: just copy the image
		for (int i = 0; i < dst.argb.length; i++) {
			dst.argb[i] = src.argb[i];
		}
	}

	// private int noOverflow(int value) {
	// if (value <= 0)
	// value = 0;
	// else if (value >= 255)
	// value = 255;
	// return value;
	// }
}