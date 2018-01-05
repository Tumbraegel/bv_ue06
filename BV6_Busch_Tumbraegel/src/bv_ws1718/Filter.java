// BV Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1718;

public class Filter {

	public enum PredicationType {
		COPY("copy"), A("A (horizontal)"), B("B (vertikal)"), C("C (diagonal)"), AANDBMINUSC("A+B-C"), AANDBDIVIDEDBY2("(A+B/2)"), ADAPTIV("adaptiv");

		private final String name;

		private PredicationType(String s) {
			name = s;
		}

		public String toString() {
			return this.name;
		}
	};

	// predication implementations go here:
	
	public void methodA(RasterImage src, RasterImage dst) {
		//P = A

		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				dst.argb[y*src.width+x] = src.argb[y*src.width+(x-1)];
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