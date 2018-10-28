package stereolab;

public class CUDADistribDiff extends StereoAlgorithm {

	public CUDADistribDiff(int B, int H, int TauMax, boolean U, int S, boolean F) {
		super(B, H, TauMax, U, S, F);
	}

	public String toString() {
		String st = "Algorithmus:              Betrag der Diff. (CUDA)\n"
				+ "Fenstergöße:              " + String.valueOf(b) + " x "
				+ String.valueOf(h) + "\n" + "Tau max:                  "
				+ String.valueOf(tauMax) + "\n" + "Schwellwert/Fenster-Fkt.: "
				+ (useS ? "ja" : "nein") + "/" + (useF ? "ja" : "nein");
		if (useS)
			st = st + "\nSchwellwert:              " + String.valueOf(s);
		return (st);
	}

	public RangeProfile calc(byte[][] lb, byte[][] rb, TimerProgressBar p) {

		int zeilen = lb.length;
		int spalten = lb[0].length;

		RangeProfile profile = new RangeProfile(zeilen, spalten);

		System.out.println("Starting distributed CUDA calculation...");

		long start = System.nanoTime();
		doCalculationNative(lb, rb, b, h, tauMax, useS, useF, s, profile
				.getRawProfileData(), profile.getValidData());

		long stop = System.nanoTime();

		System.out.println("Finished CUDA calculation... (took "
				+ (stop - start) / 1000000.0 + " ms)");

		return profile;

	}

	private native static void doCalculationNative(byte[][] lb, byte[][] rb, int b, int h,
			int tauMax, boolean useS, boolean useF, int s, int[][] profile, boolean[][] valid);


	static {
		System.loadLibrary("cudadistribstereo");
	}
}
