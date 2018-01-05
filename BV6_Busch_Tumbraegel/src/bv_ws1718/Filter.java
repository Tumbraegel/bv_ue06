// BV Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1718;

public class Filter {

	public enum PredicationType {
		COPY("copy"), A("A (horizontal)"), B("B (vertikal)"), C("C (diagonal)"), AANDBMINUSC("A+B-C"), AANDBDIVIDEDBY2(
				"(A+B/2)"), ADAPTIV("adaptiv");

		private final String name;

		private PredicationType(String s) {
			name = s;
		}

		public String toString() {
			return this.name;
		}
	};

	// predication implementations go here:
	// |C|B|
	// |A|X|

	public void methodA(RasterImage src, RasterImage dst) {
		// P = A
		int pixelA;
		int pixelX;
		int predicationError;
		for (int y = 0; y < src.height; y++) {
			pixelA = 0;
			for (int x = 0; x < src.width; x++) {
				pixelX = src.argb[y * src.width + (x)] >> 16;
				predicationError = pixelA - pixelX + 128;
				dst.argb[y * src.width + x] = 0xff000000 | (predicationError << 16) | (predicationError << 8)
						| (predicationError);
				;
				pixelA = pixelX;
			}
		}
	}

	public void methodB(RasterImage src, RasterImage dst) {
		// P = B
		int pixelB;
		int pixelX;
		int predicationError;
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				if (y == 0) {
					pixelB = 0;
				} else {
					pixelB = src.argb[(y - 1) * src.width + (x)] >> 16;
				}
				pixelX = src.argb[y * src.width + (x)] >> 16;
				predicationError = pixelB - pixelX + 128;
				dst.argb[y * src.width + x] = 0xff000000 | (predicationError << 16) | (predicationError << 8)| (predicationError);
			}
		}
	}

	public void copy(RasterImage src, RasterImage dst) {
		// TODO: just copy the image
		for (int i = 0; i < dst.argb.length; i++) {
			dst.argb[i] = src.argb[i];
		}
	}
}