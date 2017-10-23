// BV Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1718;

import java.util.Arrays;

public class Filter {

	public enum FilterType {
		COPY("Copy Image"), BOX("Box Filter"), MEDIAN("Median Filter");

		private final String name;

		private FilterType(String s) {
			name = s;
		}

		public String toString() {
			return this.name;
		}
	};

	public enum BorderProcessing {
		CONTINUE("Border: Constant Continuation"), WHITE("Border: White");

		private final String name;

		private BorderProcessing(String s) {
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

	public void box(RasterImage src, RasterImage dst, int kernelSize, BorderProcessing borderProcessing) {
		// TODO: implement a box filter with given kernel size and border processing

		// kernelRadius is casted to an int, so the result can be added/subtracted
		// to/from the hotspot for the kernel loop
		// kernelSize / 2 is for Pythagoras
		int kernelRadius = (int) Math.ceil(kernelSize / 2);
		for (int picY = 0; picY < src.height; picY++) {
			for (int picX = 0; picX < src.width; picX++) {

				// r, g and b are in grayscale all the same, so it is sufficient to use only one
				// in place of all three
				int[] kernelColor = new int[kernelSize * kernelSize];
				int index = 0;
				for (int kernelY = picY - kernelRadius; kernelY <= (picY + kernelRadius); kernelY++) {
					for (int kernelX = picX - kernelRadius; kernelX <= picX + kernelRadius; kernelX++) {

						if (kernelY < 0 || kernelY >= src.height || kernelX < 0 || kernelX >= src.width) {

							switch (borderProcessing) {
							case CONTINUE:
								boolean leftBorder = kernelY < 0;
								boolean rightBorder = kernelY > src.width;
								boolean topBorder = kernelX < 0;
								boolean bottomBorder = kernelX > src.height;

								int borderX = kernelX;
								int borderY = kernelY;

								if (leftBorder || rightBorder) {
									if (leftBorder) {
										boolean inPic = false;
										while (!inPic) {
											borderY++;
											if (borderY == 0) {
												inPic = true;
											}
										}
									} else {
										boolean inPic = false;
										while (!inPic) {
											borderY--;
											if (borderY == src.width) {
												inPic = true;
											}
										}
									}
								} else if (topBorder || bottomBorder) {
									if (topBorder) {
										boolean inPic = false;
										while (!inPic) {
											borderX++;
											if (borderX == 0) {
												inPic = true;
											}
										}
									} else {
										boolean inPic = false;
										while (!inPic) {
											borderX--;
											if (borderX == src.height) {
												inPic = true;
											}
										}
									}
								}else {
									
								}
								// in case of border continuation use the hotspot
								kernelColor[index] = src.argb[picY * src.height + picX];
								break;
							case WHITE:
								// use white for all indices outside the array
								kernelColor[index] = 0xff000000 | (255 << 16) | (255 << 8) | (255);
								break;
							default:
								break;
							}
						} else {
							kernelColor[index] = src.argb[kernelY * src.height + kernelX];
						}
						index++;
					}
				}
				int dstR = 0;
				int dstG = 0;
				int dstB = 0;
				for (int color : kernelColor) {
					int r = color >> 16 & 0xff;
					int g = color >> 8 & 0xff;
					int b = color & 0xff;
					dstR += r;
					dstG += g;
					dstB += b;
				}
				dstR = dstR / (kernelSize * kernelSize);
				dstG = dstG / (kernelSize * kernelSize);
				dstB = dstB / (kernelSize * kernelSize);
				dst.argb[picY * src.height + picX] = 0xff000000 | (dstR << 16) | (dstG << 8) | (dstB);

			}
		}
	}

	public void median(RasterImage src, RasterImage dst, int kernelSize, BorderProcessing borderProcessing) {
		// TODO: implement a median filter with given kernel size and border processing
		int kernelRadius = (int) Math.ceil(kernelSize / 2);
		for (int picY = 0; picY < src.height; picY++) {
			for (int picX = 0; picX < src.width; picX++) {

				int[] kernelR = new int[kernelSize * kernelSize];
				int[] kernelG = new int[kernelSize * kernelSize];
				int[] kernelB = new int[kernelSize * kernelSize];

				int index = 0;
				for (int kernelY = picY - kernelRadius; kernelY <= picY + kernelRadius; kernelY++) {
					for (int kernelX = picX - kernelRadius; kernelX <= picX + kernelRadius; kernelX++) {
						if (kernelY < 0 || kernelY >= src.height || kernelX < 0 || kernelX >= src.width) {

							switch (borderProcessing) {
							case CONTINUE:
								boolean leftBorder = kernelY < 0;
								boolean rightBorder = kernelY > src.width;
								boolean topBorder = kernelX < 0;
								boolean bottomBorder = kernelX > src.height;

								int borderX = kernelX;
								int borderY = kernelY;

								if (leftBorder || rightBorder) {
									if (leftBorder) {
										boolean inPic = false;
										while (!inPic) {
											borderY++;
											if (borderY == 0) {
												inPic = true;
											}
										}
									} else {
										boolean inPic = false;
										while (!inPic) {
											borderY--;
											if (borderY == src.width) {
												inPic = true;
											}
										}
									}
								} else if (topBorder || bottomBorder) {
									if (topBorder) {
										boolean inPic = false;
										while (!inPic) {
											borderX++;
											if (borderX == 0) {
												inPic = true;
											}
										}
									} else {
										boolean inPic = false;
										while (!inPic) {
											borderX--;
											if (borderX == src.height) {
												inPic = true;
											}
										}
									}
								}else {
									
								}

								int pixel = src.argb[picY * src.height + picX];
								kernelR[index] = pixel >> 16 & 0xff;
								kernelG[index] = pixel >> 8 & 0xff;
								kernelB[index] = pixel & 0xff;

								break;
							case WHITE:
								kernelR[index] = kernelB[index] = kernelG[index] = 255;
								break;
							default:
								break;
							}
						} else {
							int pixel = src.argb[kernelY * src.height + kernelX];
							kernelR[index] = pixel >> 16 & 0xff;
							kernelG[index] = pixel >> 8 & 0xff;
							kernelB[index] = pixel & 0xff;
						}
						index++;
					}
				}
				Arrays.sort(kernelR);
				Arrays.sort(kernelG);
				Arrays.sort(kernelB);
				int medianR = kernelR[(int) (kernelSize * kernelSize / 2)];
				int medianG = kernelG[(int) (kernelSize * kernelSize / 2)];
				int medianB = kernelB[(int) (kernelSize * kernelSize / 2)];
				dst.argb[picY * src.height + picX] = 0xff000000 | (medianR << 16) | (medianG << 8) | (medianB);
			}
		}
	}
}