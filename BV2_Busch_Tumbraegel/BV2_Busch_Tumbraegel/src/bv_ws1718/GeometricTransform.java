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

	public void copy(RasterImage src, RasterImage dst) {
		for (int dstY = 0; dstY < dst.height; dstY++) {
			for (int dstX = 0; dstX < dst.width; dstX++) {
				float dstYNew = Math.round(dstY - (dst.height / 2f));
				float dstXNew = Math.round(dstX - (dst.width / 2f));
				int srcY = (int)Math.round(dstYNew + (src.height /2f));
				int srcX = (int)Math.round(dstXNew + (src.width/2f));
				if(srcY >= 0 && srcY < src.height && srcX >= 0 && srcX < src.width) {
					dst.argb[dstY * dst.width + dstX] = src.argb[srcY * src.width + srcX];
				}else {
					dst.argb[dstY * dst.width + dstX] = 0xff000000 | (255 << 16) | (255 << 8) | (255);
				}
			}
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

		// get origin to middle of picture
		for (int dstY = 0; dstY < dst.height; dstY++) {
			for (int dstX = 0; dstX < dst.width; dstX++) {
				int dstYNew = dstY - (dst.height / 2);
				int dstXNew = dstX - (dst.width / 2);

				int xs = (int) (dstXNew / (Math.cos(angle) - dstXNew * perspectiveDistortion * Math.sin(angle)));
				int ys = (int) (dstYNew * (perspectiveDistortion * Math.sin(angle) * xs + 1));

				// xsNew = xs

			}
		}

		// place origin back
		for (int srcY = 0; srcY < src.height; srcY++) {
			for (int srcX = 0; srcX < src.height; srcX++) {
				int srcYNew = srcY + (src.height / 2);
				int srcXNew = srcX + (src.width / 2);

			}
		}

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

}
