// BV Ue3 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1718;

public class MorphologicFilter {

	public enum FilterType {
		DILATION("Dilation"), EROSION("Erosion");

		private final String name;

		private FilterType(String s) {
			name = s;
		}

		public String toString() {
			return this.name;
		}
	};

	// filter implementations go here:

	public void copy(RasterImage src, RasterImage dst) {
		// TODO: just copy the image

		for (int i = 0; i < dst.argb.length; i++) {
			dst.argb[i] = src.argb[i];
		}
	}

	public void dilation(RasterImage src, RasterImage dst, boolean[][] kernel) {
		// TODO: dilate the image using the given kernel
		int radius = kernel.length / 2;
		for (int picY = 0; picY < src.height; picY++) {
			for (int picX = 0; picX < src.width; picX++) {
				int[] colour = src.getRGB(src.argb[picY * src.width + picX]);
				if (colour[0] == 0) {
					for (int j = 0; j < kernel.length; j++) {
						for (int k = 0; k < kernel[j].length; k++) {
							int stampY = 0;
							int stampX = 0;
							if (kernel[j][k]) {
								
								if (j < radius) {
									stampY = picY - radius + j;
									stampX = picX - radius + k;
								} else if (j > radius) {
									stampY = picY - radius + j;
									stampX =  picX - radius + k;
								} else {
									//j == radius
									stampX = picX - radius + k;
									stampY = picY;
								}
								
								if(stampX >= 0 && stampX < dst.width && stampY >= 0 && stampY < dst.height) {
									dst.argb[stampY*dst.width+stampX]=0xff000000 | (0 << 16) | (0 << 8) | (0);
								}
							}
						}
					}
				}else {
					dst.argb[picY*dst.width+picX]=0xff000000 | (255 << 16) | (255 << 8) | (255);
				}
				
			}
		}
	}

	public void erosion(RasterImage src, RasterImage dst, boolean[][] kernel) {
		// TODO: erode the image using the given kernel
		copy(src, dst);
		dst.invert();
		dilation(dst, dst, kernel);
		dst.invert();
	}

}
