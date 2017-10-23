// BV Ue2 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1718;

public class GeometricTransform {

	public enum InterpolationType {
		NEAREST("Nearest Neighbour"), BILINEAR("Bilinear");

		private final String name;

		private InterpolationType(String s) {
			name = s;
		}

		public String toString() {
			return this.name;
		}
	};

	public void perspective(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion,
			InterpolationType interpolation) {
		switch (interpolation) {
		case NEAREST:
			perspectiveNearestNeighbour(src, dst, angle, perspectiveDistortion);
			break;
		case BILINEAR:
			perspectiveBilinear(src, dst, angle, perspectiveDistortion);
			break;
		default:
			break;
		}

	}

	/**
	 * @param src
	 *            source image
	 * @param dst
	 *            destination Image
	 * @param angle
	 *            rotation angle in degrees
	 * @param perspectiveDistortion
	 *            amount of the perspective distortion
	 */
	public void perspectiveNearestNeighbour(RasterImage src, RasterImage dst, double angle,
			double perspectiveDistortion) {
		// TODO: implement the geometric transformation using nearest neighbour image
		// rendering

		// NOTE: angle contains the angle in degrees, whereas Math trigonometric
		// functions need the angle in radians

	}

	/**
	 * @param src
	 *            source image
	 * @param dst
	 *            destination Image
	 * @param angle
	 *            rotation angle in degrees
	 * @param perspectiveDistortion
	 *            amount of the perspective distortion
	 */
	public void perspectiveBilinear(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
		// TODO: implement the geometric transformation using bilinear interpolation

		// NOTE: angle contains the angle in degrees, whereas Math trigonometric
		// functions need the angle in radians

	}
	
//	public void copy(RasterImage src, RasterImage dst) {
//		for (int i = 0; i < dst.argb.length; i++) {
//			int dstYNew = (int)dst.argb[i]/dst.height;
//			int dstXNew = dst.argb[i]%dst.height;
//			
//			dst.argb[i] = src.argb[i];
//		}
//	}

	public void copy(RasterImage src, RasterImage dst) {
		// TODO: just copy the image
		for (int dstY = 0; dstY < dst.height; dstY++) {
			for (int dstX = 0; dstX < dst.width; dstX++) {
				int dstYNew = dstY - dst.height / 2;
				int dstXNew = dstX - dst.width / 2;
				boolean foundCorPixel = false;

				 System.out.println(src.width*src.height);
				 System.out.println(dst.width*dst.height);
				for (int srcY = 0; srcY < src.height; srcY++) {
					for (int srcX = 0; srcX < src.width; srcX++) {
						int srcYNew = srcY + src.height / 2;
						int srcXNew = srcX + src.width / 2;
						if (srcYNew == dstYNew && srcXNew == dstXNew) {
							int srcPixel = src.argb[srcY * src.height + srcX];
							int dstPixel = dst.argb[dstY * dst.height + dstX];
							dst.argb[dstY * dst.height + dstX] = src.argb[srcY * src.height + srcX];
							foundCorPixel = true;
						}
					}
				}
				if (!foundCorPixel) {
					dst.argb[dstY * src.height + dstX] = 0xff000000 | (255 << 16) | (255 << 8) | (255);
				}

			}
		}

	}

}
