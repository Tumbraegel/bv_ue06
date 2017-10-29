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
		
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric
		// functions need the angle in radians

		// get origin to middle of picture
		for (int dstY = 0; dstY < dst.height; dstY++) {
			for (int dstX = 0; dstX < dst.width; dstX++) {
				//Koordinatensystem verschieben im Ziel
				int dstYNew = dstY - (dst.height / 2);
				int dstXNew = dstX - (dst.width / 2);

				//Transfromation von den Zielkoordinaten zu den Quellkoordinaten
				int xs = (int) (dstXNew / (Math.cos(angle) - dstXNew * perspectiveDistortion * Math.sin(angle)));
				int ys = (int) (dstYNew * (perspectiveDistortion * Math.sin(angle) * xs + 1));

				//Koordinatensystem in der Quelle zurück verschieben
				int srcY = ys + (src.height / 2);
				int srcX = xs + (src.width / 2);
				//Write values in dst pic
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
	public void perspectiveBilinear(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
		// TODO: implement the geometric transformation using bilinear interpolation

		// NOTE: angle contains the angle in degrees, whereas Math trigonometric
		// functions need the angle in radians

	}

}
