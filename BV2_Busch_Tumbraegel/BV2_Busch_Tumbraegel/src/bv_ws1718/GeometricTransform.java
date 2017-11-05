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
				int srcY = (int) Math.round(dstYNew + (src.height / 2f));
				int srcX = (int) Math.round(dstXNew + (src.width / 2f));
				if (srcY >= 0 && srcY < src.height && srcX >= 0 && srcX < src.width) {
					dst.argb[dstY * dst.width + dstX] = src.argb[srcY * src.width + srcX];
				} else {
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
		double rad = angle * Math.PI / 180;

		// get origin to middle of picture
		for (int dstY = 0; dstY < dst.height; dstY++) {
			for (int dstX = 0; dstX < dst.width; dstX++) {
				// Koordinatensystem verschieben im Ziel
				double dstYNew = dstY - ((float)dst.height / 2.f);
				double dstXNew = dstX - ((float)dst.width / 2.f);

				// Transfromation von den Zielkoordinaten zu den Quellkoordinaten mit nearest
				// Neighbour
				double xs = (dstXNew / (Math.cos(rad) - dstXNew * perspectiveDistortion * Math.sin(rad)));
				double ys = (dstYNew * (perspectiveDistortion * Math.sin(rad) * xs + 1));

				// Koordinatensystem in der Quelle zurück verschieben
				int srcY = (int)Math.floor(ys + (src.height / 2));
				int srcX = (int)Math.floor(xs + (src.width / 2));
				// Write values in dst pic
				if (srcY >= 0 && srcY < src.height && srcX >= 0 && srcX < src.width) {
					dst.argb[dstY * dst.width + dstX] = src.argb[srcY * src.width + srcX];
				} else {
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

		double rad = angle * Math.PI / 180;

		// get origin to middle of picture
		for (int dstY = 0; dstY < dst.height; dstY++) {
			for (int dstX = 0; dstX < dst.width; dstX++) {
				// Koordinatensystem verschieben im Ziel
				double dstYNew = dstY - (dst.height / 2.0);
				double dstXNew = dstX - (dst.width / 2.0);

				// Transfromation von den Zielkoordinaten zu den Quellkoordinaten
				double xs = dstXNew/ (Math.cos(rad) - dstXNew * perspectiveDistortion * Math.sin(rad));
				double ys = dstYNew * (perspectiveDistortion * Math.sin(rad) * xs + 1.0);

				// Koordinaten im Original Koordinatensystem
				double srcY = ys + (src.height / 2);
				double srcX = xs + (src.width / 2);

				if (srcY >= 0 && srcY < src.height && srcX >= 0 && srcX < src.width) {

					int xsFloor = (int) Math.floor(srcX);
					int ysFloor = (int) Math.floor(srcY);
					int xsCeil = (int) Math.ceil(srcX);
					int ysCeil = (int) Math.ceil(srcY);

					// Abstand vom berechneten Wert zum Pixel in X-Richtung (h) und Y-Richtung (v)
					double h = srcX - xsFloor;
					double v = srcY - ysFloor;

					int A, B, C, D;
					A = B = C = D = 0;
					// ---------------
					// | A | B |
					// ---------------
					// | C | D |
					// ---------------

					// Mit Randbehandlung
					if (xsFloor < 0) {
						A = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						C = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						if (ysFloor < 0) {
							B = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						} else if (ysCeil >= src.height) {
							D = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						} else {
							B = src.argb[ysFloor * src.width + xsCeil];
							D = src.argb[ysCeil * src.width + xsCeil];
						}
					} else if (xsCeil >= src.width) {
						B = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						D = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						if (ysFloor < 0) {
							A = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						} else if (ysCeil >= src.height) {
							C = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						} else {
							C = src.argb[ysCeil * src.width + xsFloor];
							A = src.argb[ysFloor * src.width + xsFloor];
						}
					} else if (ysFloor < 0) {
						A = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						B = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						if (xsFloor < 0) {
							C = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						} else if (xsCeil >= src.width) {
							D = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						} else {
							C = src.argb[ysCeil * src.width + xsFloor];
							D = src.argb[ysCeil * src.width + xsCeil];
						}
					} else if (ysCeil >= src.height) {
						C = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						D = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						if (xsFloor < 0) {
							A = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						} else if (xsCeil >= src.width) {
							B = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						} else {
							A = src.argb[ysFloor * src.width + xsFloor];
							B = src.argb[ysFloor * src.width + xsCeil];
						}
					} else {
						A = src.argb[ysFloor * src.width + xsFloor];
						B = src.argb[ysFloor * src.width + xsCeil];
						C = src.argb[ysCeil * src.width + xsFloor];
						D = src.argb[ysCeil * src.width + xsCeil];
					}

					int[] Argb = getRGB(A);
					int[] Brgb = getRGB(B);
					int[] Crgb = getRGB(C);
					int[] Drgb = getRGB(D);

					int Pr = (int) ((Argb[0] * (1 - h) * (1 - v)) + (Brgb[0] * h * (1 - v)) + (Crgb[0] * (1 - h) * v)
							+ (Drgb[0] * h * v));
					int Pg = (int) ((Argb[1] * (1 - h) * (1 - v)) + (Brgb[1] * h * (1 - v)) + (Crgb[1] * (1 - h) * v)
							+ (Drgb[1] * h * v));
					int Pb = (int) ((Argb[2] * (1 - h) * (1 - v)) + (Brgb[2] * h * (1 - v)) + (Crgb[2] * (1 - h) * v)
							+ (Drgb[2] * h * v));

					// Neuen Wert berechnen
					dst.argb[dstY * dst.width + dstX] = 0xff000000 | (Pr << 16) | (Pg << 8) | (Pb);

				} else {
					int xsFloor = (int) Math.floor(srcX);
					int ysFloor = (int) Math.floor(srcY);
					int xsCeil = (int) Math.ceil(srcX);
					int ysCeil = (int) Math.ceil(srcY);

					// Abstand vom berechneten Wert zum Pixel in X-Richtung (h) und Y-Richtung (v)
					double h = srcX - xsFloor;
					double v = srcY - ysFloor;

					int A, B, C, D;
					A = B = C = D = 0;

					if (ysCeil >= 0 && ysCeil < src.height && xsFloor > 0 && xsCeil <= src.width) {
						A = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						B = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						if (xsFloor <= 0) {
							C = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						} else if (xsCeil > src.width) {
							D = 0xff000000 | (255 << 16) | (255 << 8) | (255);
						} else {
							C = src.argb[ysCeil * src.width + xsFloor];
							D = src.argb[ysCeil * src.width + xsCeil];
						}
						int[] Argb = getRGB(A);
						int[] Brgb = getRGB(B);
						int[] Crgb = getRGB(C);
						int[] Drgb = getRGB(D);

						int Pr = (int) ((Argb[0] * (1 - h) * (1 - v)) + (Brgb[0] * h * (1 - v))
								+ (Crgb[0] * (1 - h) * v) + (Drgb[0] * h * v));
						int Pg = (int) ((Argb[1] * (1 - h) * (1 - v)) + (Brgb[1] * h * (1 - v))
								+ (Crgb[1] * (1 - h) * v) + (Drgb[1] * h * v));
						int Pb = (int) ((Argb[2] * (1 - h) * (1 - v)) + (Brgb[2] * h * (1 - v))
								+ (Crgb[2] * (1 - h) * v) + (Drgb[2] * h * v));

						// Neuen Wert berechnen
						dst.argb[dstY * dst.width + dstX] = 0xff000000 | (Pr << 16) | (Pg << 8) | (Pb);
					} else {
						dst.argb[dstY * dst.width + dstX] = 0xff000000 | (255 << 16) | (255 << 8) | (255);
					}
				}

			}
		}
	}

	public int[] getRGB(int argb) {
		int[] rgb = new int[3];
		rgb[0] = (argb >> 16) & 0xff;
		rgb[1] = (argb >> 8) & 0xff;
		rgb[2] = argb & 0xff;
		// System.out.println(rgb[0] +" "+ rgb[1]+" "+rgb[2]);
		return rgb;
	}

}
